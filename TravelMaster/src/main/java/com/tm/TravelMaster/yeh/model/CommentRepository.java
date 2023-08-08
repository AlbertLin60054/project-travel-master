package com.tm.TravelMaster.yeh.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<CommentBean, Integer>{

	@Query("SELECT comments FROM CommentBean comments WHERE comments.article.articleId = :articleId")
	List<CommentBean> findAllByArticleId(@Param("articleId") int articleId);
}
