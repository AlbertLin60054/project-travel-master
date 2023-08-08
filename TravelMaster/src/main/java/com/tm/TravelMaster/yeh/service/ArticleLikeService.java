package com.tm.TravelMaster.yeh.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tm.TravelMaster.yeh.model.ArticleLikeBean;
import com.tm.TravelMaster.yeh.model.ArticleLikeRepository;

@Service
public class ArticleLikeService {
	
	@Autowired
	private ArticleLikeRepository alRepo;
	
	//新增讚
	public void addLike(ArticleLikeBean like) {
		alRepo.save(like);
	}
	
	//移除讚
	public void removeLike(ArticleLikeBean like) {
		System.out.println(" before   Executing removeLike method---------------------------------");
		alRepo.deleteById(like.getArticleLikeId());
		
		System.out.println("Executing removeLike method---------------------------------------------");
	}
	
    
    public List<ArticleLikeBean>findBymemberIdAndArticleId(String memberNum,Integer articleId){
    	return alRepo.findByMemberMemberNumAndArticleArticleId(memberNum,articleId);
    }
    
    // 根據會員編號與文章編號查詢
    public Optional<ArticleLikeBean> findByMemberNumAndArticleId(String memberNum, Integer articleId) {
        return alRepo.findByMemberNumAndArticleId(memberNum, articleId);
    }
	
}
