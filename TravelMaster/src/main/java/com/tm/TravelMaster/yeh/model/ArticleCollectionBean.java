package com.tm.TravelMaster.yeh.model;

import com.tm.TravelMaster.chih.model.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="articleCollection")
public class ArticleCollectionBean {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer articleCollectionId;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "memberNum", nullable=false)
	private Member member;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="articleId", nullable = false)
	private ArticleBean article;

	public Integer getArticleCollectionId() {
		return articleCollectionId;
	}

	public void setArticleCollectionId(Integer articleCollectionId) {
		this.articleCollectionId = articleCollectionId;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public ArticleBean getArticle() {
		return article;
	}

	public void setArticle(ArticleBean article) {
		this.article = article;
	}

	@Override
	public String toString() {
		return "ArticleCollectionBean [articleCollectionId=" + articleCollectionId + ", member=" + member + ", article="
				+ article + "]";
	}
	
	
	
	
	
	
	
	
}
