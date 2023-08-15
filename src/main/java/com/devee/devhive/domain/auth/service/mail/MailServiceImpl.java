package com.devee.devhive.domain.auth.service.mail;

import static com.devee.devhive.global.exception.ErrorCode.FAILED_SENDING_VERIFY_CODE;

import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.util.RedisUtil;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

  private final JavaMailSender emailSender;
  private final RedisUtil redisUtil;

  private static String createKey() {
    StringBuilder key = new StringBuilder();
    Random rnd = new Random();

    // 인증코드 8자리
    for (int i = 0; i < 8; i++) {
      int index = rnd.nextInt(3);

      switch (index) {
        case 0 -> key.append((char) (rnd.nextInt(26) + 97));
        case 1 -> key.append((char) (rnd.nextInt(26) + 65));
        case 2 -> key.append((rnd.nextInt(10)));
      }
    }
    return key.toString();
  }

  @Override
  public void sendAuthEmail(String to) throws Exception {
    String authCode = createKey();
    MimeMessage message = createMessage(to, authCode);
    redisUtil.setDataExpire(to, authCode, 60 * 5L);
    try {
      emailSender.send(message);
    } catch (MailException es) {
      es.printStackTrace();
      throw new CustomException(FAILED_SENDING_VERIFY_CODE);
    }
  }

  private MimeMessage createMessage(String to, String authCode) throws Exception {
    MimeMessage message = emailSender.createMimeMessage();

    message.addRecipients(RecipientType.TO, to);
    message.setSubject("이메일 인증 코드");
    String msgg = "회원가입 인증 코드는 <strong>" + authCode + "</strong> 입니다.";
    message.setText(msgg, "utf-8", "html");
    message.setFrom(new InternetAddress("devHive", "devee"));

    return message;
  }
}
