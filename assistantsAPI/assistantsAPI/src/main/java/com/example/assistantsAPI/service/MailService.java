package com.example.assistantsAPI.service;

import com.example.assistantsAPI.controller.dto.CorrectionResponseDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;


    private MimeMessage createMessage(CorrectionResponseDto.CorrectionItem item) throws MessagingException, UnsupportedEncodingException { //메시지 생성

        MimeMessage message = emailSender.createMimeMessage();

        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("sqwa9200@naver.com"));//보내는 대상
        message.setSubject(item.getReqType() + item.getTitle() + "-" + "송성우 기자"); //제목

        String msgg ="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1> 안녕하세요 조선어시스턴트입니다. </h1>";
        msgg+= "<br>";
        msgg+= "<h3>" + item.getContent() + "</h3>";
        msgg+= "<br>";
        msgg+= "기사ID: " + item.getArcId() + "<br>" + "독자ID: " + item.getUuid();
        msgg+= "<br>";
        msgg+= "</div>";

        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("songsungwoo9200@gmail.com","IT팀 인턴"));//보내는 사람

        return message;
    }


    public void sendMessage(CorrectionResponseDto.CorrectionItem item) throws MessagingException, UnsupportedEncodingException { //메시지 전송

        MimeMessage message = createMessage(item); //메일 전송

        emailSender.send(message);
    }
}
