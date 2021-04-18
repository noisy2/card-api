# card-api

## 개발 프레임워크 및 라이브러리
> Spring Boot / Java 

> H2 Embedded Database

> Spring REST Docs

> Spring HATEOAS

> Lombok

## 테이블 설계
![ERD](https://user-images.githubusercontent.com/16730107/115133247-b4b4d380-a041-11eb-8e28-fd593ea70a65.png)

    취소처리를 위해 순환참조로 결재(Payment) 테이블을 설계

    실제 구현을 하진 못했지만 부분 취소처리까지 감안하여 One to Many관계로 생성
    
    카드사 전송 테이블(CreditCardTran)은 통신을 대체하는 것으로 Payment과 연관 없다고 판단하여 별도 테이블로 생성
 

## 문제해결 전략
+ 각 Input의 Validation이 필요함.
  + 모든 입력값이 숫자로 이루어져 있다는 것에 착안하여 기본적인 입력값 검증은 정규식을 통해 진행.
  
+ 입출력값과 실제 저장되는 객체가 상이함.
  + 데이터의 입출력만 담당하는 Dto(PaymentReqDto, PaymentResDto)와 모델객체(Payment) 분리하여 개발.
  
+ 카드정보의 암복호화 되어야 함.
  + 테이블 저장시에만 암호화 되면 되므로 AttributeConverter를 이용하여 자동으로 암복호화 되도록 처리.
  
+ 거래 관리번호는 20자리의 id로 생성되어야 함.
  + 20자리 id생성을 위해 millisecond를 포함한 시분초 + 3자리 랜덤한 수를 조합하여 구성.
  
+ 카드사로 전송하는 string 데이터를 공통헤더부문과 데이터부문을 합쳐 하나의 string(450자리)으로 만들어서 DB에 저장
  + 카드사로 전송하는 대부분의 데이터가 Payment객체에 존재하므로 toString을 Override하여 저장시 해당 함수를 이용하도록 함.


## 빌드 및 실행 방법 
    mvn package spring-boot:run 

## API Guide
> 어플리케이션 실행 후 http://localhost:8080/docs/index.html
