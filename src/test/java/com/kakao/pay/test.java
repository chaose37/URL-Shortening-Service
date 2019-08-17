package com.kakao.pay;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class test {
	private static final Logger Logger = LoggerFactory.getLogger(test.class);
	private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String URL = "jdbc:oracle:thin:@192.168.0.184:1521:orcl";
	private static final String USER = "kakaoPayUser";
	private static final String PW = "kakaoPayUser";
	
	//문자열을 url에 사용하기 적합한 base62 형태의 문자열로 고치기 위해 base62 문자열을 선언
	private static final String BASE62STR = "0123456789"
									      + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
									      + "abcdefghijklmnopqrstuvwxyz";
	private static final int    BASE      = BASE62STR.length();
	
	private static final StringBuilder out = new StringBuilder();

	@Test //JUnit이 테스트하는 코드
	public void testConnection() throws Exception {
	        Class.forName(DRIVER);
	        try(Connection conn= DriverManager.getConnection(URL, USER, PW)) 
	        {
	                Logger.info("오라클에 연결되었습니다.");
	        }catch(Exception e) {
	                e.printStackTrace();
	        }
	}
	
	@Test // BASE62 ENCODE TEST
	public void base62EncodeTest() 
	{
		Logger.info("base62 Encode Test");
		char[] BASE62 = BASE62STR.toCharArray();
		for(long i = 1000000000; i< 1000000025; i++)
		{
			long value = i;
			out.setLength(0);
			while(value > 0) 
			{
				out.append(BASE62[(int)(value%BASE)]);
				value = value/BASE;
			}
			Logger.info("원본 숫자 : " + i);
			Logger.info("결과       : " + out.toString());
		}
	}
}

