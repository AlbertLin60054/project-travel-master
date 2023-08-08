package com.tm.TravelMaster.chih.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tm.TravelMaster.chih.dao.MemberNotFoundException;
import com.tm.TravelMaster.chih.dao.MemberService;
import com.tm.TravelMaster.chih.model.Member;
import com.tm.TravelMaster.leo.model.Playone;
import com.tm.TravelMaster.leo.service.PlayoneService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import net.bytebuddy.utility.RandomString;

@Controller
public class LoginSystemController {
	
	@Autowired
	private MemberService mService;
	
	@Autowired
	private PlayoneService pService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@GetMapping("/login.controller")
	public String processMainAction(HttpServletRequest request, HttpSession session) {
	    String referrer = request.getHeader("Referer");

	    String loginPageUrl = "http://localhost:8080/TM/login.controller";
	    if (referrer != null && !referrer.contains(loginPageUrl)) {
	        session.setAttribute("url_prior_login", referrer);
	    }

	    return "chih/loginSystem";
	}

	@PostMapping("/checklogin.controller")
	public String processAction(@RequestParam("memberacc") String memberAcc,
	    @RequestParam("memberpwd") String memberPwd, @RequestParam("captcha") String captcha,HttpSession session, Model m) {
		
		String sessionCaptcha = (String) session.getAttribute("captcha");
		boolean checkMemberAcc = mService.findByMemberAcc(memberAcc);
		if(checkMemberAcc) {
			 if (captcha.equals(sessionCaptcha)) {
				 	Member checkPwd = mService.returnByMemberAcc(new Member(memberAcc));
			    	int checkPwdTime = checkPwd.getCheckPwd();
				    boolean result = mService.checkLogin(memberAcc,memberPwd);
				    if (result && checkPwdTime!=3) {
				        Member mb = mService.returnByMemberAcc(new Member(memberAcc));
				        session.setAttribute("mbsession", mb);
				        Playone p = pService.findBySeq(mb.getMemberSeq());
				        session.setAttribute("playonesession", p);	
				        mService.updateCheckPwd(memberAcc);
				        
				        String redirectTo = (String) session.getAttribute("url_prior_login");
				        
				        if(session.getAttribute("resetsuccess")!=null){
				        	session.removeAttribute("resetsuccess");
				            return "redirect:/layout/index";
				        }
				        		        
				        if (redirectTo != null ) {
				            session.removeAttribute("url_prior_login");
				            return "redirect:" + redirectTo;
				        } else {
				            return "redirect:/layout/index";
				        }			
				    } else {				    	
				    	if(checkPwdTime==3) {
				    		String errorMessage = "此用戶已被停權，請聯繫客服0912345678";
					        m.addAttribute("errorMessage", errorMessage);
				    	}else {			    		
				    		if((checkPwdTime+1)!=3) {
				    			mService.addCheckPwd(memberAcc);
				    			String errorMessage = "密碼錯誤"+(checkPwdTime+1)+"次，錯達3次將停權";
				    			m.addAttribute("errorMessage", errorMessage);				    			
				    		}else {
				    			mService.addCheckPwd(memberAcc);
				    			String errorMessage = "密碼錯誤3次，您已被停權，請聯繫客服0912345678";
				    			checkPwd.setMemberLevel("black_user");
				    			mService.updateMember(checkPwd);
				    			m.addAttribute("errorMessage", errorMessage);
				    		}
				    	}
				    	
				        return "chih/loginSystem";
				    }
			    }else {
			    	String errorMessage = "驗證碼錯誤，請重新登入";
			        m.addAttribute("errorMessage", errorMessage);
			        return "chih/loginSystem";
			    }
		}else {
	    	String errorMessage = "查無此帳號";
	        m.addAttribute("errorMessage", errorMessage);
	        return "chih/loginSystem";
	    }
	   
	}
	
	private String generateCaptcha() {
	    // 生成隨機6位數的驗證碼
		String randomNumber=RandomString.make(6);    
	    return randomNumber;
	}
	
	@GetMapping("/checkcaptcha")
	@ResponseBody
	public String checkCaptcha(HttpSession session) {
		return (String)session.getAttribute("captcha");
	}
	
	@GetMapping("/captcha")
	public void captcha(HttpSession session, HttpServletResponse response) throws IOException {
	    int width = 120;
	    int height = 50;
	    	    
	    // 生成驗證碼
	    String captcha = generateCaptcha();

	    // 將驗證碼存入 session
	    session.setAttribute("captcha", captcha);

	    // 創建 BufferedImage 對象
	    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	    // 獲取圖片繪圖對象
	    Graphics2D graphics = image.createGraphics();

	    // 設置背景顏色
	    graphics.setColor(Color.GRAY);
	    graphics.fillRect(0, 0, width, height);

	    // 設置字體和顏色
	    graphics.setFont(new Font("Arial", Font.BOLD, 20));
	    graphics.setColor(Color.BLACK);

	    // 在圖片上繪製驗證碼
	    graphics.drawString(captcha, 23, 35);

	    // 設置響應的 Content-Type
	    response.setContentType("image/png");

	    // 將圖片寫入到響應的 OutputStream
	    OutputStream outputStream = response.getOutputStream();
	    ImageIO.write(image, "png", outputStream);
	    outputStream.close();
	}
	
	@PostMapping("/googlechecklogin.controller")
	public String processAction2(@RequestParam("membermail") String membermail,HttpSession session, Model m) {    
	        Member mb = mService.findByMemberMail(membermail);
	        session.setAttribute("mbsession", mb);
	        Playone p = pService.findBySeq(mb.getMemberSeq());
	        session.setAttribute("playonesession", p);
	        String redirectTo = (String) session.getAttribute("url_prior_login");
	        if (redirectTo != null) {
	            session.removeAttribute("url_prior_login");
	            return "redirect:" + redirectTo;
	        } else {
	            return "redirect:/layout/index";
	        }

	    
	}
	
	
//	@GetMapping("/logout.controller")
//	public String StringprocessAction(HttpSession session, HttpServletRequest request) {
//	    String referrer = request.getHeader("Referer");
//	    session.invalidate();
//	    if (!referrer.equals("")) {
//	        return "redirect:" + referrer;
//	    } else {
//	        return "chih/loginSystem";
//	    }
//	}
	
	@GetMapping("/logout.controller")
	public String StringprocessAction(HttpSession session) {
	    session.invalidate();	   
	       return "chih/loginSystem";
	}

	
	@GetMapping("/forgotpassword.controller")
	public String showForgotPasswordForm(HttpServletRequest request) {
		request.setAttribute("forgot", "yes");
		request.setAttribute("sendemail", "yes");
		return "chih/loginSystem";
	}
	
	@PostMapping("/forgotpassword.controller")
	public String processForgotPasswordForm(HttpServletRequest request , Model model) {
		String email = request.getParameter("email");
		Member mb = mService.findByMemberMail(email);
		if(mb!=null) {
			String token=RandomString.make(45);
			try {
				mService.updateResetPwdToken(token, email);
				
				String resetPasswordLink=Utility.getSiteURL(request)+"/reset.controller?token="+token;
				
				sendEmail(email,resetPasswordLink);
				
				request.setAttribute("message","已發送至您的信箱，請至信箱驗證");
				request.setAttribute("sendsuccess","ok");
				request.setAttribute("currectemail",email);
				
			} catch (MemberNotFoundException e) {
				model.addAttribute("error",e.getMessage());
			} catch (UnsupportedEncodingException | MessagingException e) {
				model.addAttribute("error","Error while sending email.");
			} 
			request.setAttribute("forgot", "yes");
			request.setAttribute("sendemail", "no");
			
			return "chih/loginSystem";
		}else {
			request.setAttribute("message","查無此信箱，請重新輸入");
			request.setAttribute("forgot", "yes");
			request.setAttribute("sendemail", "yes");
			return "chih/loginSystem";
		}
			
		
	}

	private void sendEmail(String email, String resetPasswordLink) throws UnsupportedEncodingException, MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom("travelmasterEEIT65@gmail.com","Travel Master");
		helper.setTo(email);
		
		String subject = "這是TravelMaster更改密碼的連結";
		String content = "<p>你好,</p>"
				+"<p>請改你的密碼，不要再忘記了</p>"
				+"<p><b><a href=\""+resetPasswordLink+"\">Change my password</a></b></p>"
				+"<p>如果您還記得帳號密碼請忽略此封信件</p>";
		
		helper.setSubject(subject);
		helper.setText(content,true);
		
		mailSender.send(message);
	}
	

	@GetMapping("/reset.controller")
	public String showResetPasswordForm(@Param(value="token") String token,HttpServletRequest request,Model model) {
		Member mb = mService.get(token);
		if(mb == null) {
			model.addAttribute("message","信箱驗證錯誤,請重新發送信箱");
			request.setAttribute("forgot", "yes");
			return "chih/loginSystem";	
		}
		model.addAttribute("token",token);
		request.setAttribute("reset", "ok");
		return "chih/loginSystem";
	}
	
	@PostMapping("/resetpassword.controller")
	public String processResetPassword(HttpServletRequest request,HttpSession session) {
		String token = request.getParameter("token");
		String password = request.getParameter("memberpwd");
		String password2 = request.getParameter("checkmemberpwd");
		System.out.println("---------------------"+token);
		if(!password.equals(password2)) {			
			request.setAttribute("reset", "ok");
			request.setAttribute("message", "密碼不一樣，請重新輸入");
		}else {
			Member mb = mService.get(token);
			mService.updateMemberPwd(mb, password);
			request.setAttribute("errorMessage","重設密碼成功，請重新登入!!!");
			session.setAttribute("resetsuccess", "resetsuccess");
		}
		
		return "chih/loginSystem";
	}
	
	@GetMapping("/fastlogin")
	public String fastLogin(@RequestParam(value = "n") String n,HttpSession session, Model m) {	
		Member mb;
		if(n.equals("0")) {
			mb = mService.returnByMemberAcc(new Member("qwer123456"));
		}else {
			 mb = mService.returnByMemberAcc(new Member("qwer1234"));
		}		        
		        session.setAttribute("mbsession", mb);
		        Playone p = pService.findBySeq(mb.getMemberSeq());
		        session.setAttribute("playonesession", p);	
		        
		        String redirectTo = (String) session.getAttribute("url_prior_login");
		        
		        if(session.getAttribute("resetsuccess")!=null){
		        	session.removeAttribute("resetsuccess");
		            return "redirect:/layout/index";
		        }
		        		        
		        if (redirectTo != null ) {
		            session.removeAttribute("url_prior_login");
		            return "redirect:" + redirectTo;
		        } else {
		            return "redirect:/layout/index";
		        }
	
	}
}
