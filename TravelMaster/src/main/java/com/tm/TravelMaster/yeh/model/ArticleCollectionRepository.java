package com.tm.TravelMaster.yeh.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCollectionRepository extends JpaRepository<ArticleCollectionBean,Integer > {

	//透過會員編號與文章編號查詢是否已經收藏過
	ArticleCollectionBean findByMemberMemberNumAndArticleArticleId(String memberNum ,Integer articleId);
	
	//透過會員編號查詢該會員所有收藏
	 Page<ArticleCollectionBean> findAllByMemberMemberNum(String memberNum, Pageable pageable);
	
}
