package com.tm.TravelMaster.yeh.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tm.TravelMaster.yeh.model.ArticleBean;
import com.tm.TravelMaster.yeh.model.ArticleRepository;

@Service
public class ArticleService {

	@Autowired
	private ArticleRepository arRepo;

	// 新增一篇文章
	public void insert(ArticleBean article) {
		arRepo.save(article);
	}

	// 查詢所有文章
	public List<ArticleBean> findAllArticle() {
		return arRepo.findAll();
	}

	// 查詢所有公開文章，按照5篇文章為一個分頁，並按照文章ID做降序排列
	public Page<ArticleBean> findPublicArticle(Integer pageNum) {
		Pageable pgb = PageRequest.of(pageNum - 1, 5, Sort.Direction.DESC, "articleId");
		return arRepo.findPublicArticles(pgb);
	}

	// 查詢所有公開文章，按照5篇文章為一個分頁，並按照文章按讚數做降序排列
	public Page<ArticleBean> findPublicArticlesOrderByLikeCountDesc(Integer pageNum) {
		PageRequest pgb = PageRequest.of(pageNum - 1, 5, Sort.Direction.DESC, "articleLikeCount");
		return arRepo.findPublicArticles(pgb);
	}

	// 查詢所有公開文章，按照5篇文章為一個分頁，並按照文章觀看數做降序排列
	public Page<ArticleBean> findPublicArticlesOrderByViewCountDesc(Integer pageNum) {
		PageRequest pgb = PageRequest.of(pageNum - 1, 5, Sort.Direction.DESC, "articleViewCount");
		return arRepo.findPublicArticles(pgb);
	}

	// 透過文章編號查詢特定文章
	public ArticleBean findById(Integer articleId) {
		Optional<ArticleBean> optional = arRepo.findById(articleId);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	// 透過文章編號刪除文章
	public void deleteById(Integer articleId) {
		Optional<ArticleBean> optional = arRepo.findById(articleId);
		if (optional.isPresent()) {
			arRepo.deleteById(articleId);
		}
	}

	// 更新文章
	public void edit(ArticleBean article) {
		arRepo.save(article);
	}

	// 透過會員編號查詢該會員文章且為公開的並按照降序排列
//	public List<ArticleBean> findPublicArticlesByMemberNumOrderByArticleIdDesc(String memberNum) {
//		return arRepo.findPublicArticlesByMemberNumOrderByArticleIdDesc(memberNum);
//	}

	public Page<ArticleBean> findPublicArticlesByMemberNum(String memberNum, Integer pageNum){
		Pageable pgb=PageRequest.of(pageNum-1, 3, Sort.Direction.DESC, "articleId");
		return arRepo.findPublicArticlesByMemberNum(memberNum, pgb);
	}

	// 查詢所有特定類別文章
	public Page<ArticleBean> findPublicArticlesByArticleType(String articleType, Integer pageNum) {
		Pageable pgb = PageRequest.of(pageNum - 1, 5, Sort.Direction.DESC, "articleId");
		return arRepo.findPublicArticlesByArticleType(articleType, pgb);
	}

	// 查詢特定類別公開文章，按照5篇文章為一個分頁，並按照文章按讚數做降序排列
	public Page<ArticleBean> findPublicArticlesByArticleTypeOrderByLikeCountDesc(String articleType, Integer pageNum) {
		PageRequest pgb = PageRequest.of(pageNum - 1, 5, Sort.Direction.DESC, "articleLikeCount");
		return arRepo.findPublicArticlesByArticleType(articleType, pgb);
	}

	// 查詢特定類別公開文章，按照5篇文章為一個分頁，並按照文章按觀看數做降序排列
	public Page<ArticleBean> findPublicArticlesByArticleTypeOrderByViewCountDesc(String articleType, Integer pageNum) {
		PageRequest pgb = PageRequest.of(pageNum - 1, 5, Sort.Direction.DESC, "articleViewCount");
		return arRepo.findPublicArticlesByArticleType(articleType, pgb);
	}
	
	
	public Page<ArticleBean> searchArticleByKeyword(String keyword,Integer pageNum){
		PageRequest pgb = PageRequest.of(pageNum-1, 5);
		return arRepo.searchArticlesByKeyword(keyword, pgb);
		
		
	}
	
	

}
