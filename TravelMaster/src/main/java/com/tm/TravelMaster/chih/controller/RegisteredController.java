package com.tm.TravelMaster.chih.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tm.TravelMaster.chih.dao.MemberService;
import com.tm.TravelMaster.chih.model.Member;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class RegisteredController {

	@Autowired
	private MemberService mService;

	@GetMapping("/registered.controller")
	public String showRegistrationForm() {
		return "chih/registered";
	}
	
	@PostMapping("/registered.controller")
	public String registerMember(@RequestParam("membername") String memberName, @RequestParam("membersex") String memberSex,
			@RequestParam("membermail") String memberMail, @RequestParam("memberphone") String memberPhone,
			@RequestParam("memberadd") String memberAdd, @RequestParam("memberid") String memberId,
			@RequestParam("memberacc") String memberAcc, @RequestParam("memberpwd") String memberPwd, Model m) {
		
			Member mb = new Member();
			mb.setMemberName(memberName);
			mb.setMemberSex(memberSex);
			mb.setMemberMail(memberMail);
			mb.setMemberPhone(memberPhone);
			mb.setMemberAdd(memberAdd);;
			mb.setMemberId(memberId.toUpperCase());
			mb.setMemberAcc(memberAcc);
			mb.setMemberPwd(memberPwd);
			byte[] defaultPicture = readPictureFromFile();
			mb.setMemberPhoto(defaultPicture);
			mb.setAuth_provider("local");
			mService.insertMember(mb);
			m.addAttribute("registerSuccess", "註冊成功，請重新登入!");
			return "chih/loginSystem";
		
	}
	
	public byte[] readPictureFromFile() {
		String filePath = "src/main/resources/static/images/chih/user.png";
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            byte[] pictureBytes = new byte[(int) file.length()];
            fis.read(pictureBytes);
            return pictureBytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            // 关闭 FileInputStream
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	@ResponseBody
	@GetMapping("/checkgoogleregistered.controller")
	public String googleCheckRegistrationForm(
            @RequestParam("email") String email) {
	// 處理從前端傳來的資料
	Member mb = mService.findByMemberMail(email);
		if (mb!=null) {
	        return "exists";
	    } else {
	        return "not_exists";
	    }
	}
	
	@PostMapping("/googleregistered.controller")
	public String googleShowRegistrationForm(@RequestParam("name") String memberName, @RequestParam("gender") String memberSex,
			@RequestParam("email") String memberMail,HttpServletRequest request) {
		
		request.setAttribute("memberName", memberName);
	    request.setAttribute("memberSex", memberSex);
	    request.setAttribute("memberMail", memberMail);	
		return "chih/googleregistered";
	}
	
	@PostMapping("/savegoogleregistered.controller")
	public String googleRegisterMember(@RequestParam("membername") String memberName, @RequestParam("membersex") String memberSex,
			@RequestParam("membermail") String memberMail, @RequestParam("memberphone") String memberPhone,
			@RequestParam("memberadd") String memberAdd, @RequestParam("memberid") String memberId,Model m) {
		
			Member mb = new Member();
			mb.setMemberName(memberName);
			mb.setMemberSex(memberSex);
			mb.setMemberMail(memberMail);
			mb.setMemberPhone(memberPhone);
			mb.setMemberAdd(memberAdd);;
			mb.setMemberId(memberId.toUpperCase());
			mb.setMemberAcc(memberMail);
			byte[] defaultPicture = readPictureFromFile();
			mb.setMemberPhoto(defaultPicture);
			mb.setAuth_provider("google");
			mService.insertMember(mb);
			m.addAttribute("registerSuccess", "註冊成功，請重新登入!");
			return "chih/loginSystem";
		
	}

	
	
	@GetMapping("/CheckMemberAccAjax.controller")
    @ResponseBody
    public String checkMemberAcc(@RequestParam("memberacc") String memberacc) {
        boolean result = mService.findByMemberAcc(memberacc);
        if (result) {
            return "exists";
        } else {
            return "not_exists";
        }
    }

}
