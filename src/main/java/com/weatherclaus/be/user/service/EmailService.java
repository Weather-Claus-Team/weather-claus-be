package com.weatherclaus.be.user.service;

import com.weatherclaus.be.user.dto.EmailCode;
import com.weatherclaus.be.user.exception.CodeMismatchException;
import com.weatherclaus.be.user.exception.EmailAlreadyExistsException;
import com.weatherclaus.be.user.repository.UserRepsotiroy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {


    // IntelliJ에서 JavaMailSender 빈을 찾지 못하는 경고가 있지만,
    // 애플리케이션은 정상 작동하므로 무시해도 됨.
    // 의존성은 spring-boot-starter-mail을 통해 자동 설정됨.

    private final JavaMailSender mailSender;

    private final StringRedisTemplate redisTemplate;

    private final UserRepsotiroy userRepsotiroy;



    // 사용자 정보를 인자로 받는 메소드로 확장
    public void sendContactEmail(String toEmail) {

        if(userRepsotiroy.existsByEmail(toEmail)) {
            throw new EmailAlreadyExistsException("email already exists");
        }



        String[] to = {toEmail}; // 이메일을 받을 주소들
        String subject = "Weather Claus ! 회원가입 인증 메일입니다 ! "; // 메일 제목
        String text = buildEmailContent(toEmail); // 이메일 본문 조합

        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("~~@naver.com"); // 발신자 이메일 주소
        message.setTo(toEmail); // 수신자 이메일 주소
        message.setSubject(subject); // 메일 제목
        message.setText(text); // 메일 내용
        mailSender.send(message);
    }

    // 이메일 본문을 조합하는 메소드
    private String buildEmailContent(String toEmail) {
        String code = verify(toEmail);
        return String.format(toEmail+"님, 인증번호 입니다\n"+ "===========\n" + code +"\n==========\n"
                +" 인증번호 확인란에 숫자를 입력해주세요 ");
    }




    public String verify(String email) {
        // 6자리 임의 숫자 생성
        String verificationCode = String.format("%06d", new Random().nextInt(1000000));

        // Redis에 이메일을 키로, 인증 코드를 값으로 저장 (TTL 10분 설정)
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(email, verificationCode, Duration.ofMinutes(10)); // 10분 동안 유효

        // 클라이언트에게 성공 메시지 반환
        return verificationCode;
    }


    /**
     * 인증로직
     */
    @Transactional
    public void verifyCode(EmailCode emailCode)   {

        // Redis에서 이메일에 해당하는 인증번호 조회
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        String storedCode = valueOps.get(emailCode.getEmail());

        // Redis에 저장된 인증번호가 없는 경우
        if (storedCode == null) {
            throw new CodeMismatchException("code mismatch");
        }

        if (storedCode.equals(emailCode.getCode())) {
            redisTemplate.delete(emailCode.getEmail());
        } else {
            throw new CodeMismatchException("code mismatch");
        }
    }
}
