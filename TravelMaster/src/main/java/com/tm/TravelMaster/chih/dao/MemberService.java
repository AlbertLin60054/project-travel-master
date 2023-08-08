package com.tm.TravelMaster.chih.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tm.TravelMaster.chih.model.Member;
import com.tm.TravelMaster.chih.model.MemberRepository;

@Service
public class MemberService {

	@Autowired
	private PasswordEncoder pwdEncoder;
	
	@Autowired
	private MemberRepository mRepo;

	public void insertMember(Member mb) {
		mb.setMemberPwd(pwdEncoder.encode(mb.getMemberAcc()));
		mRepo.save(mb);
	}
	
	 public Member findById(Integer id) {
		    Optional<Member> optional = mRepo.findById(id);
		    if (optional.isPresent()) {
		              return optional.get();
		          } 
		    return null;
		   }

	public boolean checkLogin(String memberAcc, String memberPwd) {
		Member member = mRepo.findByMemberAcc(memberAcc);
		if (member != null && member.getAuth_provider().equals("local")) {
			if(pwdEncoder.matches(memberPwd, member.getMemberPwd())) {				
				return true;
			}else {
				return false;
			}
		}
		return false;
	}

	public Member findByMemberMail(String memberMail) {
		Member member = mRepo.findByMemberMail(memberMail);	
		return member;
	}
	
	public boolean findByMemberAcc(String memberAcc) {
		Member member = mRepo.findByMemberAcc(memberAcc);
		if (member != null) {
			return true;
		}
		return false;
	}
	
	public void addCheckPwd(String memberAcc) {
		Member member = mRepo.findByMemberAcc(memberAcc);
		member.setCheckPwd(member.getCheckPwd()+1);
		mRepo.addCheckPwd(member.getCheckPwd(), memberAcc);
	}
	
	public void updateCheckPwd(String memberAcc) {
		mRepo.updateCheckPwd(memberAcc);
	}

	public Member returnByMemberAcc(Member mb) {
		Member member = mRepo.findByMemberAcc(mb.getMemberAcc());
		return member;
	}

	public Member findByMemberNum(String memberNum) {
		Member member = mRepo.findByMemberNum(memberNum);
		return member;
	}
	
	public List<Member> findAllOtherMember(Member mb){
		List<Member> memberList = mRepo.findAllOtherMember(mb.getMemberNum());
		return memberList;
	}

	public List<Member> findAllMember() {
		List<Member> memberList = mRepo.findAll();
		return memberList;
	}
	
	public List<Member> findByMemberNumOrMemberLevel(String mytext,String text) {
		List<Member> memberList = mRepo.findByMemberNumOrMemberLevel(mytext,text,text);
		return memberList;
	}

	public void saveMember(Member member) {
        mRepo.save(member);
    }
	
	public void updateMember(Member mb) {
		if(mb.getMemberPwd().length()<20) {
			mb.setMemberPwd(pwdEncoder.encode(mb.getMemberPwd()));	
		}
		mRepo.updateMember(mb.getMemberNum(),mb.getMemberName(),mb.getMemberMail(),mb.getMemberPhone(),mb.getMemberAdd(),mb.getMemberPwd(),mb.getMemberLevel(),mb.getCheckPwd());
	}
	
	public void deleteMember(Member mb) {
		mRepo.delete(mb);
	}
	
	public void updateMemberPhoto(String memberNum,byte[] memberPhoto) {
		mRepo.updateMemberPhoto(memberNum,memberPhoto);
	}
	
	public Page<Member> findByPage(Integer pageNumber, Member mb) {
	    Pageable pageable = PageRequest.of(pageNumber - 1, 10);

	    List<Member> members = mRepo.findAllOtherMember(mb.getMemberNum());
	    int start = (int) pageable.getOffset();
	    int end = Math.min((start + pageable.getPageSize()), members.size());
	    List<Member> subList = members.subList(start, end);

	    Page<Member> page = PageableExecutionUtils.getPage(subList, pageable, () -> members.size());
	    return page;
	}
	
	public Page<Member> findByPageMember(Integer pageNumber, String searchInput ,Member mb) {
	    Pageable pageable = PageRequest.of(pageNumber - 1, 10);	    
	    List<Member> members = mRepo.findAllOtherBySearchText(searchInput, mb.getMemberNum());
	    List<Member> memberSex=mRepo.findAllOtherBySearchTextSex(searchInput, mb.getMemberNum());
	    members.addAll(memberSex);
	    int start = (int) pageable.getOffset();
	    int end = Math.min((start + pageable.getPageSize()), members.size());
	    List<Member> subList = members.subList(start, end);

	    Page<Member> page = PageableExecutionUtils.getPage(subList, pageable, () -> members.size());
	    return page;
	}
	
	public List<Member> findAllOtherBySearchText(String searchInput ,Member mb){
		 List<Member> members = mRepo.findAllOtherBySearchText(searchInput, mb.getMemberNum());
		 return members;
	}
	
	public List<Member>findAllOtherBySearchTextSex(String searchInput ,Member mb){
		List<Member> memberSex=mRepo.findAllOtherBySearchTextSex(searchInput, mb.getMemberNum());
		 return memberSex;
	}
	
	public void updateResetPwdToken(String token,String email) throws MemberNotFoundException{
		 Member mb = mRepo.findByMemberMail(email);
		 
		 if(mb!=null) {
			 mb.setResetPwdToken(token);
			 mRepo.save(mb);
		 }else {
			 throw new MemberNotFoundException("這是錯誤的信箱 "+email);
		 }
	}
	
	public Member get(String resetPwdToken) {
		return mRepo.findByResetPwdToken(resetPwdToken);
	}
	
	public void updateMemberPwd(Member mb ,String newPassword) {	
		mb.setMemberPwd(pwdEncoder.encode(newPassword));
		mb.setResetPwdToken(null);		
		mRepo.updateMemberPwd(mb.getMemberNum(), mb.getMemberPwd());
	}
	
	public int findMemberSexCount(String sex) {
       List<Member> mbl=mRepo.findMemberSex(sex);
       int num=0;
       for(int i=0 ; i<mbl.size();i++) {
       	num++;
       }
       return num;
	}
	
	public int findMemberLevelCount(String level) {
	       List<Member> mbl=mRepo.findMemberLevel(level);
	       int num=0;
	       for(int i=0 ; i<mbl.size();i++) {
	       	num++;
	       }
	       return num;
		}
	 
	 
	
}
