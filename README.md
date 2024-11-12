
<h1 align="center">
  <br>
  <img src="https://github.com/user-attachments/assets/2b35db18-e259-48fc-8a40-239f664dab8b" alt="WC logo" width="200">
  <p>
  Weather Claus
  <p>
</h1>

<h4 align="center">날씨가 어려운 웨더즈들을 위한 사이트, 웨더 클로스입니다</h4>

<!-- 이미지 추가 예정 (gif 등) -->

<br/>

## 프로젝트 소개

https://weather-claus.netlify.app



<img width="500" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/87f321ae-e3e4-4686-8be3-98a7abbb29af">
<img width="500" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/7bdcec7e-5846-4faf-a582-eec05f4ba6b1">
<img width="500" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/f5d59c8e-b3cc-4b05-9f13-2134ed01950e">

## 개발 기간과 팀 웨더클로스
2024/10/4 ~ 2024/11 (진행중)

## 기술 스택
	• 프로그래밍 언어 및 프레임워크
      Java 17, Spring 3.3.4

	• 데이터베이스 
      mysql 8.0.32,redis

	• 도커 및 컨테이너화
      Docker, Docker Compose

    • 인프라 및 클라우드 관련 서비스
      aws ec2, route53, ELB , ECR,IAM, s3

    • DevOps 및 CI/CD 관련 도구
       github actions, postman, swagger


## 주요 기능

[//]: # (이거는... 기능설명 프론트쪽 사진 있어야 될 거 같음 , )
```
회원가입("/api/users/**") -> 아이디체크,이메일인증번호발송,확인, 아이디찾기
```

```
마이페이지("/api/profile/**") -> 내정보보기(이미지,닉네임), 회원정보 수정,패스워드 확인,변경,회원탈퇴
```
```
날씨 보여주기("/api/weather/forecast") -> 해당 지역의 날씨를 보여줌
```
```
웹소켓 채팅("/ws","api/chatList") -> 1개의 공용채팅방으로 회원은 채팅을 할 수 있으며, 비회원은 채팅내역을 볼 수 있으나 채팅은 할 수 없음
```




aws 백엔드 서버 구조 
```
Route53(domain 별칭) -> ELB(SSL,TLS-https,80port,443port) -> EC2(80 port,EIP)
```


[//]: # (aws image)


ec2 내부 docker를 통한 빌드, docker-compose를 통한 개발환경, 배포환경 분리

<img width="700" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/b86f4977-36cf-42b9-a2ad-ccd751eb4860">




CI/CD깃허브 액션 

<img width="500" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/bf8bcb20-c458-4a4d-9f48-2fb91ca3ae3a">




Dockerfile
```Dockerfile

FROM openjdk:17-jdk

COPY build/libs/*SNAPSHOT.jar /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]


```





시큐리티 구조 모식도  ( jwt,access,refresh, redis 사용 ) 

<img width="700" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/0f609f68-175a-4101-8c76-86a8ff6f8932">


jpa 연관관계
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;
```
querydsl ( 채팅 목록 )
````java
    @Override
public Slice<ChatMessage> findAllChatMessages(Pageable pageable) {

    List<ChatMessage> content = queryFactory.select(chatMessage)
                                    .from(chatMessage)
                                    .join(chatMessage.user, user).fetchJoin()
                                    .offset(pageable.getOffset())
                                    .limit(pageable.getPageSize() + 1)
                                    .orderBy(chatMessage.sentDate.desc())
                                    .fetch();

    boolean hasNext = content.size() > pageable.getPageSize();
    if (hasNext) {
            content.remove(content.size() - 1); 
    }

    return new SliceImpl<>(content, pageable, hasNext);
}
````
restful api
```java
@RequestMapping("/api/profile")
@GetMapping("/myPage")
@PatchMapping("/myPage")
@PostMapping("/password")
@DeleteMapping()
```
redis -> 캐시 사용 @Cacheable
```java
//refresh 토큰 발급,재발급 경우 레디스에 저장 
private void storeRefreshTokenInRedis(String username, String refreshToken) {
    redisTemplate.opsForValue().set(username, refreshToken, jwtUtil.getRefreshTokenExpiredMs(), TimeUnit.MILLISECONDS);
}


// Redis에 이메일을 키로, 인증 코드를 값으로 저장 (TTL 10분 설정)
ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(email, verificationCode, Duration.ofMinutes(10)); // 10분 동안 유효

// 지오코딩 사용시 도시이름 위경도 캐시로 저장
@Cacheable(value = "cityCache", key = "#city")
public LatLonDTO getLatLon(String city) throws JsonProcessingException {
```
custom validator 사용
```
@Override
  public void validate(Object target, Errors errors) {

      UpdatePasswordRequest request = (UpdatePasswordRequest) target;

      // 두 개의 비밀번호가 일치하는지 검증
      if (!request.getPassword().equals(request.getPassword2())) {
            errors.rejectValue("password", "PasswordMismatch", "Passwords do not match");
      }
  }
```
도메인별 예외 및 controllerAdivce 처리


<img width="252" alt="스크린샷 2024-11-04 오전 8 52 52" src="https://github.com/user-attachments/assets/e645d775-1292-4bfb-a00f-3efe114da7fc">
<img width="243" alt="스크린샷 2024-11-04 오전 8 53 32" src="https://github.com/user-attachments/assets/2017ae9f-2634-4fe1-961e-26ac87173fe1">
<img width="247" alt="스크린샷 2024-11-04 오전 9 19 51" src="https://github.com/user-attachments/assets/75fe9010-9f66-41d8-895c-62aa45cdfc0d">



응답 획일화
```json
//성공
{
    "status": "success",
    "message": "Email sent successfully",
    "data": null,
    "errorDetails": null,
    "code": 200
}
```

```json
//실패
{
    "status": "fail",
    "message": "Invalid request",
    "data": null,
    "errorDetails": {
        "message": "Bad Request",
        "details": "잘못된 요청입니다."
    },
    "code": 400
}
```

resoureces환경 분리 - 기본yml, dev, local, prod, secret

<img width="338" alt="스크린샷 2024-11-04 오전 9 03 24" src="https://github.com/user-attachments/assets/f03ef4cf-c793-427e-b3f1-166235334638">


- g-mail: 이메일 서비스를 사용하여 인증 및 알림을 보냅니다.
- s3: AWS S3를 사용하여 파일을 저장하고 관리합니다.
- weathermap api 사용: 외부 API를 사용하여 날씨 정보를 가져옵니다.
- 캐시, 지오코딩: RedisTemplate과 ObjectMapper를 사용하여 JSON 데이터를 객체로 변환하고 캐시합니다.
- web-socket: 웹소켓을 사용하여 실시간 통신을 구현합니다. 페이징(무한스크롤 슬라이싱)과 QueryDSL을 사용하여 데이터를 효율적으로 조회합니다.
- 리캡챠: Google reCAPTCHA를 사용하여 봇을 방지합니다.


[//]: # ( 엔티티다이어그램 )

[//]: # ( 엔티티 이미지 ? )




## 후기
구성원들 모두 프론트와 백엔드의 협업 프로젝트는 처음이지만 서로 많이 질의하며 협업에 대해 알게된것같다.

원래 백엔드가 2명이었지만 1분이 나가셔서 혼자 하게되었는데. 혼자 정해진 기간내에 프로젝트를 완성해야되다 보니까 많이 걱정되고 불안했지만 
무사히 끝낼수 있어서 기쁘다.

같이 프로젝트를 진행하면서 프론트와 백의 통신방법, restApi설계 , 소통과 협업의 방식,
커뮤니케이션 능력, 팀워크,동기부여 등 토론,협의 방법에 대해 많이 배운것같다. 

프론트의 작업진행 방식에 대해서도 알게되어 좋았다

또한 백엔드 개발자로서 혼자 개인 프로젝트를 할 때 보지 못했던 것들(CORS,DTO 반환 등 )
에 대해 고민해볼 시간을 갖게 되었고 부족한 부분에 대해서 다시 공부해볼 수 있었다.  

혼자 진행하다보니 나의 페이스에 맞게 필요한 기술들에 대해 공부 할 수 있었지만 다른 백엔드 개발자와 피드백? 코드리뷰같은걸 할 수 없어서 아쉬웠다. 



