package com.project.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	@Autowired
	public JavaMailSender javaMailSender;
	public boolean sendMail(String receiver , String subject , String content) {
		
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper =new MimeMessageHelper(message);
			
			helper.setFrom("ankitrajmth2004@gmail.com");
			helper.setTo(receiver);
			helper.setSubject(subject);
			helper.setText(content);
			
			javaMailSender.send(message);
			return true;
			
		}
		catch(Exception e ) {
			return false;
		}
	}
	
}
