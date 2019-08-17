package com.kakao.pay;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	//로그찍기위한 로거객체
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	//DB정보
	private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String URL    = "jdbc:oracle:thin:@192.168.0.184:1521:orcl";
	private static final String USER   = "kakaoPayUser";
	private static final String PW     = "kakaoPayUser";
	
	//문자열을 url에 사용하기 적합한 base62 형태의 문자열로 고치기 위해 base62 문자열을 선언
	private static final String BASE62STR = "0123456789"
									      + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
									      + "abcdefghijklmnopqrstuvwxyz";
	private static final int    BASE      = BASE62STR.length();
	
	private static final StringBuilder out = new StringBuilder();
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Locale locale, Model model)
	{
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);
		
		model.addAttribute("serverTime", formattedDate );
		
		return "index";
	}
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public String saveUrl (@RequestParam("url")String data) throws Exception 
	{
		logger.info("==================================================================");
		logger.info("input url : " + data);
		logger.info("==================================================================");
		
		String result = null;
		if(!data.startsWith("http://") && !data.startsWith("https://"))
		{
			data = "http://" + data;
		}
		Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement pstmt = null;
			if(conn == null)
			{
				throw new Exception("DB 연결 오류");
			}
			
			
			if(checkUrl(conn, data))
			{
				result = getShortUrl(conn,data);
			}
			else
			{
				long indexNo = getNextIndex(conn);
				
				logger.info("==================================================================");
				logger.info("indexNo : " + indexNo);
				logger.info("==================================================================");
				//URL을 신규등록
				
				String shortUrl = encode(indexNo);
				
				insertUrl(conn,indexNo,data,shortUrl);
				result = getShortUrl(conn,data);
			}
			
			conn.close();
		}
		catch(SQLException se)
		{
			throw new Exception("SQL Error : " + se.getMessage());
		}
		finally 
		{
			if(conn != null) { conn.close();}
		}
		
		return (result == null) ? data : result;
	}
	
	//단축url이 들어왔을때의 처리
	@RequestMapping(value = "/{cd}", method = RequestMethod.GET)
	public void redirectUrl(@PathVariable("cd") String cd, HttpServletResponse resp) throws Exception
	{
		
		logger.info("CD ::: " + cd);
		
		String url = getOriginUrl(getConnection(),cd);
		
		logger.info("url ::: " + url);
		
		if(url.isEmpty())
		{
			url = "/";
		}
		
		resp.sendRedirect(url);
	}
	
	//url_id 를 단축 url로 가공 base62 이용
	public static String encode(long value) 
	{
		char[] BASE62 = BASE62STR.toCharArray();
		out.setLength(0);
//		System.out.println(Arrays.toString(BASE62));
		while(value > 0) 
		{
			out.append(BASE62[(int)(value%BASE)]);
			value = value/BASE;
		}
        String returnVal =  out.toString();
        logger.info( returnVal);
        return returnVal;
    }
	
	//DB Connection 받기
	private Connection getConnection()
    {
        Connection conn = null;
        try {
            Class.forName(DRIVER);        
            conn = DriverManager.getConnection(URL, USER, PW);
            logger.info("DB Connection 성공");
            
        } catch (ClassNotFoundException cne) {
        	logger.info("DB Driver load fail :"+cne.toString());
        } catch (SQLException se) {
        	logger.info("DB Connection Fail : "+se.toString());
        } catch (Exception e) {
        	logger.info("DB Connection error : " + e.getMessage());
        }
        return conn;     
    }
	
	//url을 새로 입력하기 위해 새로운 시퀀스 넘버를 세팅
	private long getNextIndex(Connection conn) throws Exception
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Long indexNo = null;
		String query = "SELECT SEQ_URL01.NEXTVAL AS INDEX_NO FROM DUAL";
		try
		{
			pstmt = conn.prepareStatement(query);
			rs    = pstmt.executeQuery();
			if(rs.next())
			{
				indexNo = rs.getLong("INDEX_NO");
			}
		}
		catch(SQLException se) 
		{
			logger.error("SQL Error : " + se.getMessage());
		}
		finally{
            // DB 연결을 종료한다.
            try{
                if ( rs != null ){rs.close();}   
                if ( pstmt != null ){pstmt.close();}   
            }catch(Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }
		if(indexNo == null)
		{
			throw new Exception("Index No 생성 실패");
		}
		return indexNo;
	}
	
	//URL이 이미 등록되었는지 체크
	private boolean checkUrl(Connection conn, String url) throws Exception
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String YN = null;
		String query = "SELECT CASE WHEN EXISTS \n" + 
				"         (\n" + 
				"            SELECT 1 FROM T_URL01 T1 WHERE DBMS_LOB.COMPARE(T1.ORIGIN_URL,?) = 0\n" + 
				"          ) THEN 'Y' \n" + 
				"            ELSE 'N' \n" + 
				"        END AS YN\n" + 
				"  FROM DUAL";
		try
		{
			pstmt = conn.prepareStatement(query);
			Clob clob = conn.createClob();
			clob.setString(1, (String) url);
			pstmt.setClob(1, clob);
			rs    = pstmt.executeQuery();
			if(rs.next())
			{
				YN = rs.getString("YN");
			}
		}
		catch(SQLException se) 
		{
			logger.error("SQL Error 1: " + se.getMessage());
		}
		finally{
			// DB 연결을 종료한다.
			try{
				if ( rs != null ){rs.close();}   
				if ( pstmt != null ){pstmt.close();}   
			}catch(Exception e){
				throw new RuntimeException(e.getMessage());
			}
		}
		if(YN == null) return false;
		return "Y".equals(YN);
	}
	
	//url 정보 입력
	private void insertUrl(Connection conn, long indexNo, String oriUrl, String shortUrl) throws Exception
	{
		conn.setAutoCommit(false);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = "INSERT INTO T_URL01 VALUES(?, empty_clob(), ?, 0 , SYSTIMESTAMP, SYSTIMESTAMP)";
		try
		{
			pstmt = conn.prepareStatement(query);
	        pstmt.setLong(1, indexNo);
	        pstmt.setString(2, shortUrl);
	        pstmt.executeUpdate();
	        pstmt.close();
	        
	        pstmt = conn.prepareStatement("SELECT ORIGIN_URL FROM T_URL01 WHERE URL_ID = "+indexNo+" FOR UPDATE");
	        rs    = pstmt.executeQuery();
	        if (rs.next()) {

	            Writer writer = null;
	            Reader reader = null;
	            try {
	                Clob clob = rs.getClob(1);
	                writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
	                reader = new CharArrayReader(oriUrl.toCharArray());
	            
	                char[] buffer = new char[oriUrl.length()];
	                int read = 0;
	                while ( (read = reader.read(buffer,0,oriUrl.length())) != -1){
	                    writer.write(buffer, 0, read);
	                }

	            } catch (Exception e) {
	                logger.error("Error at Insert",e);
	                throw e;
	            } finally {
	                if (reader != null) try { reader.close(); } catch (Exception exception) {}
	                if (writer != null) try { writer.close(); } catch (Exception exception) {}
	            }
	        }
	        
	        conn.commit();
		}
		catch(SQLException se) 
		{
			logger.error("SQL Error 2 : " + se.getMessage());
		}
		finally{
            // DB 연결을 종료한다.
            try{
                if ( pstmt != null ){pstmt.close();}   
            }catch(Exception e){
                throw new RuntimeException(e.getMessage());
            }
            conn.setAutoCommit(true);
        }
	}
	
	//단축된 url 주소를 가져옴
	private String getShortUrl(Connection conn, String oriUrl) 
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String SHORT_URL = null;
		String query = "SELECT T1.SHORT_URL FROM T_URL01 T1 WHERE DBMS_LOB.COMPARE(T1.ORIGIN_URL,?) = 0";
		try
		{
			pstmt = conn.prepareStatement(query);
			Clob clob = conn.createClob();
			clob.setString(1, (String) oriUrl);
			pstmt.setClob(1, clob);
			rs    = pstmt.executeQuery();
			if(rs.next())
			{
				SHORT_URL = rs.getString("SHORT_URL");
			}
		}
		catch(SQLException se) 
		{
			logger.error("SQL Error 3 : " + se.getMessage());
		}
		finally{
			// DB 연결을 종료한다.
			try{
				if ( rs != null ){rs.close();}   
				if ( pstmt != null ){pstmt.close();}   
			}catch(Exception e){
				throw new RuntimeException(e.getMessage());
			}
		}
		if(SHORT_URL == null) return "FAIL";
		return SHORT_URL;
	}
	
	//실제 url 주소를 가져옴
	private String getOriginUrl(Connection conn, String shortUrl)
	{
		String url = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String query = "SELECT T1.ORIGIN_URL FROM T_URL01 T1 WHERE T1.SHORT_URL = ?";
		try
		{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, shortUrl);
			rs    = pstmt.executeQuery();
			if(rs.next()) 
			{
		          StringBuffer output = new StringBuffer(); 
		          Reader input = rs.getCharacterStream("ORIGIN_URL"); 
		          char[] buffer = new char[102400]; 
		          int byteRead = 0; 
		          while((byteRead=input.read(buffer,0,102400))!=-1)
		          { 
		               output.append(buffer,0,byteRead); 
		          } 
		          
		          url = output.toString();
			}
			conn.commit();
		}
		catch(SQLException se) 
		{
			logger.error("SQL Error 4 : " + se.getMessage());
		} catch (IOException ie) {
			logger.error("CLOB READ ERROR : " + ie.getMessage());
		}
		finally{
			// DB 연결을 종료한다.
			try{
				if ( rs != null ){rs.close();}   
				if ( pstmt != null ){pstmt.close();}   
				conn.setAutoCommit(true);
			}catch(Exception e){
				throw new RuntimeException(e.getMessage());
			}
		}
		return url;
	}
	
	
}
