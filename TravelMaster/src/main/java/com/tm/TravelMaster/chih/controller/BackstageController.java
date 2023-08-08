package com.tm.TravelMaster.chih.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tm.TravelMaster.chih.dao.MemberService;
import com.tm.TravelMaster.chih.model.Member;
import com.tm.TravelMaster.chih.model.MemberDTO;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@SessionAttributes("mbsession")
public class BackstageController {

	@Autowired
	private MemberService mService;

	@GetMapping("/tobackstage.controller")
	public String redirectToBackstage() {
		return "layout/backstage";
	}

	@GetMapping("/tobackstagepersonal.controller")
	public String redirectToBackstagePersonal(Model m) {
		Member mb = (Member) m.getAttribute("mbsession");
		if (mb != null) {
			Member member = mService.findByMemberNum(mb.getMemberNum());
			m.addAttribute("member", member);
			return "chih/personalBackstage";
		} else {
			return "chih/loginSystem";
		}

	}


	@GetMapping("/tobackstageallmember.controller")
	public String pageMember(@RequestParam(name = "p", defaultValue = "1") Integer pageNumber,HttpServletRequest request, Model m) throws JsonProcessingException {
		Member mb = (Member) m.getAttribute("mbsession");
		if (mb != null) {
			Page<Member> page = mService.findByPage(pageNumber, mb);
			m.addAttribute("memberList", page);

			m.addAttribute("maleCount", mService.findMemberSexCount("male"));
	        m.addAttribute("femaleCount", mService.findMemberSexCount("female"));
	        m.addAttribute("superCount", mService.findMemberLevelCount("super_user"));
	        m.addAttribute("normalCount", mService.findMemberLevelCount("normal_user"));
	        request.setAttribute("findInputMember", "no");
	        //用DTO新建json檔案並輸出
	        List<Member> allMember = mService.findAllOtherMember(mb);
	        List<MemberDTO> allMemberDTO = new ArrayList<>();
	        for (Member member : allMember) {
	        	MemberDTO memberDTO = new MemberDTO();
	        	memberDTO.setMemberSeq(member.getMemberSeq());
	        	memberDTO.setMemberNum(member.getMemberNum());
	        	memberDTO.setMemberName(member.getMemberName());
	        	memberDTO.setMemberSex(member.getMemberSex());
	        	memberDTO.setMemberMail(member.getMemberMail());
	        	memberDTO.setMemberPhone(member.getMemberPhone());
	        	memberDTO.setMemberAdd(member.getMemberAdd());
	        	memberDTO.setMemberId(member.getMemberId());
	        	memberDTO.setMemberAcc(member.getMemberAcc());
	        	memberDTO.setMemberLevel(member.getMemberLevel());
	        	memberDTO.setAuth_provider(member.getAuth_provider());
	        	allMemberDTO.add(memberDTO);
	        }
	        ObjectMapper objectMapper = new ObjectMapper();
	        String json = objectMapper.writeValueAsString(allMemberDTO);
	        m.addAttribute("allMemberJson", json);
	        
			return "chih/backstageAllMember";
		} else {
			return "chih/loginSystem";
		}
	}
	
	@GetMapping("/tosearchallmember.controller")
	public String pageMemberSearch(@RequestParam(name = "p", defaultValue = "1") Integer pageNumber,@RequestParam("searchInput") String searchInput ,HttpServletRequest request,Model m) throws JsonProcessingException {
		if(searchInput.isEmpty()) {
			 return "redirect:/tobackstageallmember.controller";
		}else {
			Member mb = (Member) m.getAttribute("mbsession");
			if (mb != null) {

				Page<Member> page = mService.findByPageMember(pageNumber, searchInput, mb);
				m.addAttribute("memberList", page);

				m.addAttribute("maleCount", mService.findMemberSexCount("male"));
		        m.addAttribute("femaleCount", mService.findMemberSexCount("female"));
		        m.addAttribute("superCount", mService.findMemberLevelCount("super_user"));
		        m.addAttribute("normalCount", mService.findMemberLevelCount("normal_user"));
		        request.setAttribute("findInputMember", "yes");
		        request.setAttribute("findsearchInput", searchInput);
		        
		      //用DTO新建json檔案並輸出
		        List<Member> allMember = mService.findAllOtherBySearchText(searchInput, mb);
		        List<Member> allMemberSex = mService.findAllOtherBySearchTextSex(searchInput, mb);
		        allMember.addAll(allMemberSex);
		        List<MemberDTO> allMemberDTO = new ArrayList<>();
		        for (Member member : allMember) {
		        	MemberDTO memberDTO = new MemberDTO();
		        	memberDTO.setMemberSeq(member.getMemberSeq());
		        	memberDTO.setMemberNum(member.getMemberNum());
		        	memberDTO.setMemberName(member.getMemberName());
		        	memberDTO.setMemberSex(member.getMemberSex());
		        	memberDTO.setMemberMail(member.getMemberMail());
		        	memberDTO.setMemberPhone(member.getMemberPhone());
		        	memberDTO.setMemberAdd(member.getMemberAdd());
		        	memberDTO.setMemberId(member.getMemberId());
		        	memberDTO.setMemberAcc(member.getMemberAcc());
		        	memberDTO.setMemberLevel(member.getMemberLevel());
		        	memberDTO.setAuth_provider(member.getAuth_provider());
		        	allMemberDTO.add(memberDTO);
		        }
		        ObjectMapper objectMapper = new ObjectMapper();
		        String json = objectMapper.writeValueAsString(allMemberDTO);
		        m.addAttribute("allMemberJson", json);
		        
				return "chih/backstageAllMember";
			} else {
				return "chih/loginSystem";
			}
		}
		
	}
	

}
