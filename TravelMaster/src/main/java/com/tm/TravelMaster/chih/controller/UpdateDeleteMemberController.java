package com.tm.TravelMaster.chih.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.tm.TravelMaster.chih.dao.MemberService;
import com.tm.TravelMaster.chih.model.Member;



@Controller
@SessionAttributes("mbsession")
public class UpdateDeleteMemberController {
	
	@Autowired
	private MemberService mService;
	
	@PutMapping("/updateallmember.controller")
	public String updateAllMember(@RequestParam("check") String check,@RequestParam("membernum") String membernum,@RequestParam("membername") String mambername,
			@RequestParam("membermail") String membermail,@RequestParam("memberphone") String memberphone,@RequestParam("memberadd") String memberadd,@RequestParam("memberpwd") String memberpwd,@RequestParam("memberlevel") String memberlevel,Model m) {
		if(check.equals("Y")) {			
			Member mb = mService.findByMemberNum(membernum);
			mb.setMemberName(mambername);
			mb.setMemberMail(membermail);
			mb.setMemberPhone(memberphone);
			mb.setMemberAdd(memberadd);
			mb.setMemberPwd(memberpwd);
			if(memberlevel.equals("normal_user") || memberlevel.equals("super_user")) {
				mb.setCheckPwd(0);
			}
			if(memberlevel.equals("black_user")) {
				mb.setCheckPwd(3);
			}
			mb.setMemberLevel(memberlevel);
			mService.updateMember(mb);
		}
		return "redirect:/tobackstageallmember.controller";
	}
	
	@PostMapping("/updateallmember.controller")
	public String updateAllMember1(@RequestParam("check") String check,@RequestParam("membernum") String membernum,@RequestParam("membername") String mambername,
			@RequestParam("membermail") String membermail,@RequestParam("memberphone") String memberphone,@RequestParam("memberadd") String memberadd,@RequestParam("memberpwd") String memberpwd,@RequestParam("memberlevel") String memberlevel,Model m) {
		if(check.equals("Y")) {			
			Member mb = mService.findByMemberNum(membernum);
			mb.setMemberName(mambername);
			mb.setMemberMail(membermail);
			mb.setMemberPhone(memberphone);
			mb.setMemberAdd(memberadd);
			mb.setMemberPwd(memberpwd);
			mb.setMemberLevel(memberlevel);
			mService.updateMember(mb);
		}
		return "redirect:/tobackstageallmember.controller";
	}
	
	@DeleteMapping("/deletemember.controller")
	public String deleteAllMember(@RequestParam("check") String check,@RequestParam("numdelete") String membernum,Model m) {
		if(check.equals("Y")) {	
			Member mb = mService.findByMemberNum(membernum);
			mService.deleteMember(mb);
		}
		return "redirect:/tobackstageallmember.controller";
	}
	
//	@PutMapping("/updatemember.controller")
//	public String updateMember(@RequestParam("check") String check,@RequestParam("membernum") String membernum,@RequestParam("membername") String mambername,
//			@RequestParam("membermail") String membermail,@RequestParam("memberphone") String memberphone,@RequestParam("memberadd") String memberadd,@RequestParam("memberpwd") String memberpwd,Model m) {
//		if(check.equals("Y")) {	
//			Member mb = mService.findByMemberNum(membernum);
//			mb.setMemberName(mambername);
//			mb.setMemberMail(membermail);
//			mb.setMemberPhone(memberphone);
//			mb.setMemberAdd(memberadd);
//			mb.setMemberPwd(memberpwd);
//			mService.updateMember(mb);
//		}
//		Member member = mService.findByMemberNum(membernum);
//		m.addAttribute("member", member);
//		return "chih/personalBackstage";
//	}
//	
	@PutMapping("/updatepersonalmember.controller")
	public String updatePersonalMember(@RequestParam("check") String check,@RequestParam("membernum") String membernum,@RequestParam("membername") String mambername,
			@RequestParam("membermail") String membermail,@RequestParam("memberphone") String memberphone,@RequestParam("memberadd") String memberadd,@RequestParam("memberpwd") String memberpwd,@RequestParam("tolocation") String tolocation,Model m) {
		if(check.equals("Y")) {	
			Member mb = mService.findByMemberNum(membernum);
			mb.setMemberName(mambername);
			mb.setMemberMail(membermail);
			mb.setMemberPhone(memberphone);
			mb.setMemberAdd(memberadd);
			mb.setMemberPwd(memberpwd);
			mService.updateMember(mb);
		}
		Member mb = mService.findByMemberNum(membernum);
		m.addAttribute("mbsession", mb);
		System.out.println("------------------------------------------------"+tolocation);
		if(tolocation.equals("backstage")) {
			return "layout/backstage";
		}else{
			return "redirect:/layout/memberCenter";
		}
		
	}
	
	@GetMapping("/getpersonphoto.controller")
	public ResponseEntity<byte[]> getHouseImage(@RequestParam("memberNum") String memberNum) {
	    Member member = mService.findByMemberNum(memberNum);
	    if (member != null) {
	        byte[] memberPhoto = member.getMemberPhoto();
	        return ResponseEntity.ok()
	                .contentType(MediaType.IMAGE_JPEG)
	                .body(memberPhoto);
	    }
	    return null;
	}
	
	@ResponseBody
	@PostMapping("/updatepersonalphoto.controller")
	public ResponseEntity<byte[]> updateMemberPhoto(@RequestParam("memberNum") String memberNum, @RequestParam("file") MultipartFile file)
	        throws IOException {
	    byte[] photoByte = file.getBytes();
	    mService.updateMemberPhoto(memberNum, photoByte);
	    
	    Member member = mService.findByMemberNum(memberNum);
	    if (member != null) {
	        byte[] memberPhoto = member.getMemberPhoto();
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.IMAGE_JPEG);
	        
	        return new ResponseEntity<>(memberPhoto, headers, HttpStatus.OK);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}
	
	


	
}
