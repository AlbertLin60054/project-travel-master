package com.tm.TravelMaster.yeh.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleReportRepository extends JpaRepository<ArticleReportBean, Integer> {
	
	//透過會員編號與文章編號查詢該文章是否已經被該會員檢舉過
	ArticleReportBean findByMemberMemberNumAndArticleArticleId(String memberNum, Integer articleId);
	
	//透過文章ID 查詢所有檢舉紀錄
	 List<ArticleReportBean> findByArticleArticleId(Integer articleId);
	

	
}
