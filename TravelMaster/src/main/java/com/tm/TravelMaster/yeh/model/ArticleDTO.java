package com.tm.TravelMaster.yeh.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ArticleDTO {

	private Integer articleId;
	
	private String articleName;
	
	private String articleSubtitle;
	
	private String articleContent;
	
	private String articleType;
	
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss EEEE", timezone = "GMT+8")
	private Date articleDate;
	
	private Integer articleLikeCount;
	
	private Integer articleViewCount;
	
	private Integer articleCommentNum;
	
	private byte[] articlePic;
	
	private Integer totalPage;
	
	private String memberName;
	
	private String articleStatus;

	public Integer getArticleId() {
		return articleId;
	}

	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}

	public String getArticleName() {
		return articleName;
	}

	public void setArticleName(String articleName) {
		this.articleName = articleName;
	}

	public String getArticleSubtitle() {
		return articleSubtitle;
	}

	public void setArticleSubtitle(String articleSubtitle) {
		this.articleSubtitle = articleSubtitle;
	}

	public String getArticleContent() {
		return articleContent;
	}

	public void setArticleContent(String articleContent) {
		this.articleContent = articleContent;
	}

	public String getArticleType() {
		return articleType;
	}

	public void setArticleType(String articleType) {
		this.articleType = articleType;
	}

	public Date getArticleDate() {
		return articleDate;
	}

	public void setArticleDate(Date articleDate) {
		this.articleDate = articleDate;
	}

	public Integer getArticleLikeCount() {
		return articleLikeCount;
	}

	public void setArticleLikeCount(Integer articleLikeCount) {
		this.articleLikeCount = articleLikeCount;
	}

	public Integer getArticleViewCount() {
		return articleViewCount;
	}

	public void setArticleViewCount(Integer articleViewCount) {
		this.articleViewCount = articleViewCount;
	}
	
	public Integer getArticleCommentNum() {
		return articleCommentNum;
	}

	public void setArticleCommentNum(Integer articleCommentNum) {
		this.articleCommentNum = articleCommentNum;
	}

	public byte[] getArticlePic() {
		return articlePic;
	}

	public void setArticlePic(byte[] articlePic) {
		this.articlePic = articlePic;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getArticleStatus() {
		return articleStatus;
	}

	public void setArticleStatus(String articleStatus) {
		this.articleStatus = articleStatus;
	}
	
	
	
	
	
	
	
}
