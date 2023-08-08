package com.tm.TravelMaster.yeh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tm.TravelMaster.yeh.model.ArticleCollectionBean;
import com.tm.TravelMaster.yeh.model.ArticleCollectionRepository;

@Service
public class ArticleCollectionService {

	@Autowired
	private ArticleCollectionRepository acRepo;
	
	//儲存收藏
	public void saveCollection(ArticleCollectionBean collection) {
		acRepo.save(collection);
	}
	
	//刪除收藏
	public void removeCollection(ArticleCollectionBean collection) {
		acRepo.delete(collection);
	}
	
	//查詢會員收藏文章的紀錄
	public ArticleCollectionBean findByMemberMemberNumAndArticleArticleId(String memberNum, Integer articleId) {
		return acRepo.findByMemberMemberNumAndArticleArticleId(memberNum, articleId);
	}
	
	//透過會員編號查詢該會員所有收藏文章
	 public Page<ArticleCollectionBean> findAllByMemberMemberNum(String memberNum, Integer pageNum){
		 PageRequest pgb = PageRequest.of(pageNum - 1, 3, Sort.Direction.DESC, "articleCollectionId");
		 return acRepo.findAllByMemberMemberNum(memberNum, pgb);
	 }
	
}
