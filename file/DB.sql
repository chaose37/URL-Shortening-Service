/*

--�ý��ۿ�������
CREATE USER kakaoPayUser identified by kakaoPayUser
GRANT CONNECT, RESOURCE, DBA TO kakaoPayUser
*/

--�� �Ʒ����ʹ� ������ �������� ����
--���̺� ����
CREATE TABLE T_URL01 (
    URL_ID                NUMBER             CONSTRAINT PK_URL01 PRIMARY KEY,
    ORIGIN_URL            CLOB               NOT NULL ,
    SHORT_URL             VARCHAR2(100)      NOT NULL UNIQUE,
    CALL_CNT              NUMBER             DEFAULT 0,
    INS_TIME              TIMESTAMP          DEFAULT SYSTIMESTAMP,
    LAST_TIME             TIMESTAMP          DEFAULT SYSTIMESTAMP
)
;


--����������
CREATE SEQUENCE SEQ_URL01 START WITH 100000 INCREMENT BY 1 NOMAXVALUE MINVALUE 0 ;
