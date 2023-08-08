package com.tm.TravelMaster.yeh.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<ArticleBean, Integer> {

	@Query("SELECT articles FROM ArticleBean articles WHERE articles.member.memberNum = :memberNum AND articles.articleStatus = '公開'")
	Page<ArticleBean> findPublicArticlesByMemberNum(@Param("memberNum") String memberNum, Pageable pgb);

	// 查詢所有公開發表的文章
	@Query("SELECT articles FROM ArticleBean articles WHERE articles.articleStatus = '公開'")
	Page<ArticleBean> findPublicArticles(Pageable pgb);

	// 查詢特定文章類別所有文章且該文章是公開的
	@Query("SELECT article FROM ArticleBean article WHERE article.articleType = :articleType AND article.articleStatus = '公開'")
	Page<ArticleBean> findPublicArticlesByArticleType(@Param("articleType") String articleType, Pageable pgb);

	
	//關鍵字搜尋
	@Query("SELECT a FROM ArticleBean a WHERE a.articleStatus = '公開' "
			+ "AND (a.articleName LIKE %:keyword% OR a.articleSubtitle LIKE %:keyword% OR a.articleContent LIKE %:keyword%)")
	Page<ArticleBean> searchArticlesByKeyword(String keyword, Pageable pageable);

}
