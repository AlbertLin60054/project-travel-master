package com.tm.TravelMaster.yeh.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "comment")
public class CommentBean {
	@Id
	@Column(name = "commentId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int commentId;

	@Column(name = "commentContent")
	private String commentContent;

	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss EEEE", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "commentDate", nullable = false, updatable = false, columnDefinition = "datetime2 default getdate()")
	private Date commentDate;

	@Column(name = "commentLike")
	private int commentLike;

	@Column(name = "commentReport")
	private int commentReport;

	@JsonIgnore
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "articleId")
	private ArticleBean article;


	@JsonIgnore
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "memberNum" )
	private Member member;
	

	@PrePersist
	public void onCreate() {
		if (commentDate == null) {
			commentDate = new Date();
		}
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public Date getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}

	public int getCommentLike() {
		return commentLike;
	}

	public void setCommentLike(int commentLike) {
		this.commentLike = commentLike;
	}

	public int getCommentReport() {
		return commentReport;
	}

	public void setCommentReport(int commentReport) {
		this.commentReport = commentReport;
	}

	public ArticleBean getArticleBean() {
		return article;
	}

	public void setArticleBean(ArticleBean article) {
		this.article = article;
	}

	public ArticleBean getArticle() {
		return article;
	}

	public void setArticle(ArticleBean article) {
		this.article = article;
	}

	// 測試用
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

}
