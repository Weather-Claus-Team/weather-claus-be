
<h1 align="center">
  <br>
  <img src="https://github.com/user-attachments/assets/d971e139-2f93-463b-8eba-e7fc7c5a71da" alt="WC logo" width="200">
  <p>
  Weather Claus
  <p>
</h1>

<h4 align="center">날씨가 어려운 이들을 위한 사이트, 웨더 클로스입니다</h4>


<br/>

► 해당 readme는 웨더 클로스 프로젝트 ${\textsf{\color{LightSalmon}백엔드(BE)}}$ readme 입니다 (팀 readme는 아래 참고 부탁드립니다)
<br>
## 프로젝트 주소
웨더클로스 웹 : [weather-claus-web](https://weather-claus.netlify.app)


웨더클로스 팀  : [weather-claus-team](https://github.com/Weather-Claus-Team)
<br>
<br>


# 프로젝트 이미지



<img width="500" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/c3e1dc03-6ae7-4375-8628-0d68ac6a4b24">
<img width="500" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/ce623d57-798d-4e45-b8f3-e69c9cbddb05">
<img width="500" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/1e9d29c2-88f0-4364-9169-9e51241bb261">

[//]: # (회원가입 쪽도 수정되면 추가하기.. )

# 개발 기간 
2024/10/4 ~ 2024/11/13 (6주)

# 팀 웨더클로스 BE

| <img src="https://github.com/user-attachments/assets/984d3041-b787-4da3-b07e-f2132411193e" width="150"> |
|:--------------------------------------------------------:|
|        [HyungGeun](https://github.com/HyungGeun94)         |
|                            BE                            |

# 기술 스택
	• 프로그래밍 언어 및 프레임워크
      Java 17, Spring 3.3.4

	• 데이터베이스 
      mysql 8.0.32, redis

	• 도커 및 컨테이너화
      Docker, Docker Compose

    • 인프라 및 클라우드 관련 서비스
      aws ec2, route53, ELB, ECR, IAM, S3

    • DevOps 및 CI/CD 관련 도구
       github actions, postman, swagger

    • 협업 및 개발 도구 
       github, notion, discord, intelliJ

# 주요 기능

[//]: # (이거는... 기능설명 프론트쪽 사진 있어야 될 거 같음 )
```
회원가입("/api/users/**") -> 아이디체크,이메일인증번호발송,확인, 아이디찾기
```

```
마이페이지("/api/profile/**") -> 내정보보기(이미지,닉네임), 회원정보 수정,패스워드 확인,변경,회원탈퇴
```
```
날씨 보여주기("/api/weather/forecast") -> 해당 지역의 날씨를 보여줍니다.
```
```
웹소켓 채팅("/ws","api/chatList") -> 
1개의 공용채팅방으로 회원은 채팅을 할 수 있으며, 
비회원은 채팅에 연결되어 있지만 실시간 채팅은 할 수 없습니다.
```




## AWS 백엔드 서버 구조 
```
Route53(domain 별칭) -> ELB(SSL,TLS-https,80port,443port) -> EC2(80 port,EIP)
```


<img width="500" height="300" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/fd70cfa2-2682-48f3-96d6-c2e3089c90c9">



```
ec2 내부 docker-compose 구조
```
<img width="700" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/b86f4977-36cf-42b9-a2ad-ccd751eb4860">



```
CI/CD -> 깃허브 액션 
```
<img width="500" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/bf8bcb20-c458-4a4d-9f48-2fb91ca3ae3a">







```
시큐리티 구조 모식도  ( jwt,access,refresh, redis 사용 ) 
```

<img width="500" height="300" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/f9fc0924-bfa5-4018-a193-c4bc3f724db7">

<img width="500" height="300" alt="스크린샷 2024-10-11 오전 8 00 09" src="https://github.com/user-attachments/assets/0f609f68-175a-4101-8c76-86a8ff6f8932">


# 주요 기능 일부 상세 코드

## querydsl ( 채팅 목록 )
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

## redis -> 캐시 사용 @Cacheable
```java
// refresh 토큰 발급,재발급 경우 레디스에 저장 
private void storeRefreshTokenInRedis(String username, String refreshToken) {
    redisTemplate.opsForValue().set(username, refreshToken, jwtUtil.getRefreshTokenExpiredMs(), TimeUnit.MILLISECONDS);
}


// Redis에 이메일을 키로, 인증 코드를 값으로 저장 (TTL 10분 설정)
ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(email, verificationCode, Duration.ofMinutes(10)); // 10분 동안 유효

// 지오코딩 사용시 도시이름 위경도 캐시로 저장
@Cacheable(value = "cityCache", key = "#city")
public LatLonDTO getLatLon(String city) throws JsonProcessingException {}
    
// 날씨정보 캐싱
@Cacheable(value = "weatherCache", key = "#cacheKey")
    public WeatherResponse getWeather(String cacheKey, double lat, double lon) throws JsonProcessingException {}
```

## jpa 연관관계 
```java
// 채팅과 유저는 다대일 관계 (단방향)
public class ChatMessage {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

}
```

## restful api
```java
@RequestMapping("/api/profile")

@GetMapping("/myPage")
@PatchMapping("/myPage")
@PostMapping("/password")
@DeleteMapping()
```

## custom validator 및 validation 사용
```java
@Override
public void validate(Object target, Errors errors) {
    
    UpdatePasswordRequest request = (UpdatePasswordRequest) target;

      // 두 개의 비밀번호가 일치하는지 검증
      if (!request.getPassword().equals(request.getPassword2())) {
            errors.rejectValue("password", "PasswordMismatch", "Passwords do not match");
      }
  }
```
## 도메인별 예외 및 controllerAdivce 처리


<img width="252" alt="스크린샷 2024-11-04 오전 8 52 52" src="https://github.com/user-attachments/assets/e645d775-1292-4bfb-a00f-3efe114da7fc">
<img width="243" alt="스크린샷 2024-11-04 오전 8 53 32" src="https://github.com/user-attachments/assets/2017ae9f-2634-4fe1-961e-26ac87173fe1">
<img width="247" alt="스크린샷 2024-11-04 오전 9 19 51" src="https://github.com/user-attachments/assets/75fe9010-9f66-41d8-895c-62aa45cdfc0d">



## 응답 획일화
```json
//성공
{
    "status": "success",
    "message": "Email sent successfully",
    "data": null, ( data가 있을시 객체 전달 )
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

## 개발과 배포 환경 분리 

- application.yml -> local, dev, prod, secret


<img width="338" alt="스크린샷 2024-11-04 오전 9 03 24" src="https://github.com/user-attachments/assets/f03ef4cf-c793-427e-b3f1-166235334638">

- docker-compose.yml ->  dev, prod


<img width="338" alt="스크린샷 2024-11-13 오전 9 06 54" src="https://github.com/user-attachments/assets/f3eb4159-c951-4662-9ad8-76f11f85f66d">


<br>
<br>
<br>
<br>

## 의존성 및 기타

- Jwt ->  로그인 시 생성,발급하고 사용자의 인증,인가 작업에 사용합니다.
- Spring-security -> 인증과 권한을 관리합니다.
<br>
<br>


- QueryDSL -> JPA를 사용할 때 동적 쿼리를 작성하고 실행합니다.
- web-socket ->  TextWebSocketHandler 구현한 웹소켓을 사용하여 실시간 통신을 구현합니다. <br>
  QueryDSL을 사용하여 페이징(무한스크롤 슬라이싱) 데이터를 효율적으로 조회합니다.
- Redis -> 캐시를 사용하여 데이터를 저장하고 조회합니다.
<br>
<br>


- S3 -> 사용자가 올리는 이미지를 업로드하고 불러옵니다. 
- g-mail ->  이메일 서비스를 사용하여 인증 및 알림을 보냅니다.
<br>
<br>


- openWeatherMap API -> 외부 api를 사용하여 날씨 정보를 가져옵니다.
- cache : RedisTemplate과 ObjectMapper를 사용하여 JSON 데이터를 객체로 변환하고 캐시합니다.
<br>
<br>







## 추가로 구현하고 싶은 부분

- 소셜로그인 : 카카오,네이버,구글,애플 로그인 등 편리하게 회원가입합니다.

- 웹소켓 업그레이드 : 채팅방을 개별로 생성하고, 채팅방을 나가거나 생성할 수 있습니다.

- 리캡챠 : Google reCAPTCHA를 사용하여 봇을 방지합니다. ( 완료 )





# 후기 
(형근)

구성원들이 협업 프로젝트에 익숙하지 않지만 서로 질의응답하며 프론트와 백에 대해 더 알게된것같다.

<br>
프론트쪽으로는 통신방법 ,restapi설계 ,소통과 협업 ,문제해결 능력, 팀워크,동기부여 , 프론트의 작업 진행방식에 대해 알게 되었고,

<br>
<br>

백엔드 개발자로서는 혼자 개인 프로젝트를 할 때 보지 못했던 것들(CORS,DTO 반환,예외처리 등)
에 대해 고민해볼 시간을 갖게 되었고 부족한 부분에 대해서 다시 공부해볼 수 있었다.  

<br>
<br>

원래 백엔드가 2명이었지만 한명이 나가서 혼자 진행 하게되었는데, 
<br>

나의 페이스에 맞게 필요한 기술들에 대해 공부 할 수 있었지만
<br>

다른 백엔드 개발자와 더 깊은 얘기를 해볼수 있는 상호작용, 코드리뷰를 할 수 없어서 아쉬웠다.

<br>
정해진 기간내에 프로젝트를 완성해야 하고 결과물도 내야하는 부분에서 많이 걱정되고 불안했지만 무사히 팀원들과 프로젝트를 끝낼수 있어서 기쁘다 !


