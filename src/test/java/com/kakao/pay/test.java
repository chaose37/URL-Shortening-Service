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
	
	//���ڿ��� url�� ����ϱ� ������ base62 ������ ���ڿ��� ��ġ�� ���� base62 ���ڿ��� ����
	private static final String BASE62STR = "0123456789"
									      + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
									      + "abcdefghijklmnopqrstuvwxyz";
	private static final int    BASE      = BASE62STR.length();
	
	private static final StringBuilder out = new StringBuilder();

	@Test //JUnit�� �׽�Ʈ�ϴ� �ڵ�
	public void testConnection() throws Exception {
	        Class.forName(DRIVER);
	        try(Connection conn= DriverManager.getConnection(URL, USER, PW)) 
	        {
	                Logger.info("����Ŭ�� ����Ǿ����ϴ�.");
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
			Logger.info("���� ���� : " + i);
			Logger.info("���       : " + out.toString());
		}
	}
}

