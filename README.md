# URL-Shortening-Service
 
 카카오페이 경력직 사내정보개발자 사전과제

## 프로젝트 소개

사용자로부터 URL을 입력받아 8자리 이내의 KEY로 이루어진 단축 URL을 제공하고 단축 URL 호출 시 입력받았던 원본 URL로 리다이덱트 시켜주는 웹 어플리케이션 개발

## 사용기술

  ### front-end
     * jQuery 3.4.1
     * BootStrap 3.3.2
     
  ### back-end
     * maven
     * Java 1.8
     * Spring-boot 2.0.4.RELEASE
     * log4j 1.2.15
     * junit 4.7
     * ojdbc6 11.2.0.3
     
  ### database
     * Oracle 12c
     
  ### server
     * tomcat 8.5
     
 

## 요구사항 정의
 * webapp으로 개발 URL 입력폼 제공 및 결과 출력 
 * Shortening Key는 8 Character 이내로 생성.  
 * 동일한 URL에 대한 요청은 동일한 Shortening Key 로 응답. 
 * Shortening된 URL을 요청받으면 원래 URL로 리다이렉트. 
 * Shortening Key 생성 알고리즘은 직접 구현. (라이브러리 사용 불가) 
 * Unit Test 코드 작성
 * Database 사용은 필수 아님 (선택) 
 
## 해결방안
 * webapp 으로 개발하기 위해 STS를 설치하여 Spring 환경의 웹 어플리케이션 개발 환경을 구축
 
 * 간단한 입력 폼 및 출력폼의 구성을 위해 BootStrap과 이벤트 컨트롤을 용이하게 하기 위해 jQuery 사용
 
 * Shortening Key 생성 
 
   * 우선 단축 URL을 만들기 위해 각 Url마다 고유한 정수값을 부여하여 [0-9a-zA-Z]  
     총 62글자로 이루어진 BASE62 형태의 62진수로 변환하여 키를 생성
     
   * 키 생성을 위한 정수값을 얻기 위한 방법은 크게 두가지
     1. DB insert 시 발생하는 고유한 시퀀스값을 활용하는 방법
     1. url의 문자를 char[] 형태로 변환 후 char를 정수형으로 형변환하여 그에대한 합계값을 구하는 방법
        * 합계값을 이용한 방법의 경우 다른 URL이 동일한 합계값을 가지거나 매우 긴 URL의 경우 너무 큰 숫자가 나올 수 있어 사용하지 않기로 결정.
   
   * 위의 두가지 방안 중 DB insert 방법을 채택함 
     1. DB insert 시 매우 긴 URL의 저장을 위해 CLOB 자료형을 사용하여 저장 사용
        * 매우 긴 URL 주소를 가상으로 만들어 테스트 했을 때 (ORA-01460 : 요구된 변환은 실행될 수 없습니다) 에러발생  
        
           *  저장할 때 CLOB을 제외한 자료를 먼저 INSERT 한 뒤 자바에서 CLOB 자료형을 별도로 처리하여 저장 
              (조회시에도 조건 절에 필요할 때도  마찬가지로 사용)
     
   * 동일한 URL에 대한 요청은 동일한 Shortening Key 로 응답. 
     * Shortening Key 생성 전 DB에서 해당 URL정보가 존재하는지 조회 후 존재한다면 이미 생성된 Shortening Key 를 반환.
     * http:// 와 프로토콜 정보가 없는 경우는 뒤 url이 동일하다면 동일한 url로 판단 https://의 경우 별개의 url로 판단함.
     
   * Shortening된 URL을 요청받으면 원래 URL로 리다이렉트
     * http://localhost:8080/{Shortening Key} 형대로 GET 방식 접근 시 원본 URL로 리다이렉트 해줌
     * 현재 URL의 유효성은 체크하고 있지 않음 
   
     
   * BASE62 + 시퀀스 값 사용시 1부터 ~ 62^8(218,340,105,584,896) 의 개수의 URL의 대응이 가능해지므로 
     사실상 8글자를 초과할 일은 없다고 가정함.
   
## DB 세팅
  1.  SYS 계정 접속 하여 유저생성 및 권한 부여
    
```
      CREATE USER kakaoPayUser identified by kakaoPayUser
      GRANT CONNECT, RESOURCE, DBA TO kakaoPayUser
```

