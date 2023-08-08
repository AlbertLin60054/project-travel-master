package com.tm.TravelMaster.yeh.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleLikeRepository extends JpaRepository<ArticleLikeBean, Integer> {
	
	//比對會員是否已針對特定文章按讚
	List<ArticleLikeBean> findByMemberMemberNumAndArticleArticleId(String memberNum, Integer articleId);
	
	// 根據文章編號及會員編號查詢按讚紀錄 new
	@Query("SELECT al FROM ArticleLikeBean al WHERE al.member.memberNum = :memberNum AND al.article.articleId = :articleId")
	Optional<ArticleLikeBean> findByMemberNumAndArticleId(@Param("memberNum") String memberNum, @Param("articleId") Integer articleId);
	

}
