/*

--시스템에서실행
CREATE USER kakaoPayUser identified by kakaoPayUser
GRANT CONNECT, RESOURCE, DBA TO kakaoPayUser
*/

--이 아래부터는 생성된 계정에서 실행
--테이블 생성
CREATE TABLE T_URL01 (
    URL_ID                NUMBER             CONSTRAINT PK_URL01 PRIMARY KEY,
    ORIGIN_URL            CLOB               NOT NULL ,
    SHORT_URL             VARCHAR2(100)      NOT NULL UNIQUE,
    CALL_CNT              NUMBER             DEFAULT 0,
    INS_TIME              TIMESTAMP          DEFAULT SYSTIMESTAMP,
    LAST_TIME             TIMESTAMP          DEFAULT SYSTIMESTAMP
)
;


--시퀀스생성
CREATE SEQUENCE SEQ_URL01 START WITH 100000 INCREMENT BY 1 NOMAXVALUE MINVALUE 0 ;
