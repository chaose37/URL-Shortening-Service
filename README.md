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
 
 
