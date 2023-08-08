package com.tm.TravelMaster.yeh.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tm.TravelMaster.chih.model.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name="articleReport")
public class ArticleReportBean {
	
	@Id
	@Column(name="articleReportId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer articleReportId;
	
	@Column(name = "reportReason")
	private String reportReason;
	
	@Column(name="articleReportStatus")
	private String articleReportStatus;

	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="articleId")
	private ArticleBean article;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="memberNum")
	private Member member;
	
	@PrePersist
	public void onCreate() {
		if (this.articleReportStatus == null) {
			this.articleReportStatus = "審核中";
		}
	}
	

	public Integer getArticleReportId() {
		return articleReportId;
	}

	public void setArticleReportId(Integer articleReportId) {
		this.articleReportId = articleReportId;
	}

	public String getReportReason() {
		return reportReason;
	}

	public void setReportReason(String reportReason) {
		this.reportReason = reportReason;
	}

	public ArticleBean getArticle() {
		return article;
	}

	public void setArticle(ArticleBean article) {
		this.article = article;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public String getArticleReportStatus() {
		return articleReportStatus;
	}

	public void setArticleReportStatus(String articleReportStatus) {
		this.articleReportStatus = articleReportStatus;
	}
	
	


	
	
}
