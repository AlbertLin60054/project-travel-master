package com.tm.TravelMaster.yeh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class MailService {

	private JavaMailSender mailSender;

	@Autowired
	public MailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendReportRejectMail(String recipient, String articleName, String articleReportReason) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
			messageHelper.setFrom("travelmastereeit65@gmail.com");
			messageHelper.setTo(recipient);
			messageHelper.setSubject("文章:\r\"" + articleName + "\r\"檢舉駁回");
			messageHelper.setText(rejectMailMaker(articleName, articleReportReason));
		};
		try {
			mailSender.send(messagePreparator);
		} catch (MailException e) {
		}
	}

	private String rejectMailMaker(String articleName, String articleReportReason) {
		String mailContent = 
				 "親愛的收件人\r\n"
		    	+ "\r\n"
		        + " Travel Master來寫信是要通知您，之前您對此篇文章:\r\""+ articleName + "\"\r 提到有"+articleReportReason+"的狀況，經過仔細確認並無違反論壇規範，因此我們決定撤銷此檢舉請求。\r\n"
		        + "如果您於此決策有任何疑問或需要進一步訊息，請隨時與我們聯繫。\r\n"
		        + "再次感謝您的合作和理解 !\r\n"
		        + "\r\n"
		        + "\r\n"
		        + "\r\n"
		        + "Travel Master";
				
		return mailContent;
	}
	
	public void sendReportBlockMail(String recipient, String articleName, String articleReportReason) {
		MimeMessagePreparator messagePreparator = mimeMessage -> {
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
			messageHelper.setFrom("travelmastereeit65@gmail.com");
			messageHelper.setTo(recipient);
			messageHelper.setSubject("主旨: 文章:\r\"" + articleName + "\r\"文章已被封鎖");
			messageHelper.setText(rejectMailMaker(articleName, articleReportReason));
		};
		try {
			mailSender.send(messagePreparator);
		} catch (MailException e) {
		}
	}
	
	
	private String blockMailMaker(String articleName, String articleReportReason) {
		String mailContent = 
				 "親愛的收件人\r\n"
		    	+ "\r\n"
		        + " Travel Master來寫信是要通知您，關於您此篇文章:\r\""+ articleName + "\"\r 提出的檢舉請求已經經過仔細確認並無違反論壇規範，因此我們決定撤銷此檢舉請求。\r\n"
		        + "如果您於此決策有任何疑問或需要進一步訊息，請隨時與我們聯繫。\r\n"
		        + "再次感謝您的合作和理解 !\r\n"
		        + "\r\n"
		        + "\r\n"
		        + "\r\n"
		        + "Travel Master";
				
		return mailContent;
	}
	
	

}