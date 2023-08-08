package com.tm.TravelMaster.yeh.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tm.TravelMaster.yeh.model.ArticleReportBean;
import com.tm.TravelMaster.yeh.model.ArticleReportRepository;

@Service
public class ArticleReportService {

	@Autowired
	private ArticleReportRepository arRepo;

	// 新增檢舉
	public void insertReport(ArticleReportBean report) {
		arRepo.save(report);
	}

	public ArticleReportBean findByArticleReportId(Integer articleReportId) {
		Optional<ArticleReportBean> optional = arRepo.findById(articleReportId);
		if (optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	// 確認某會員是否已經有檢舉過某篇文章記錄
	public ArticleReportBean findByMemberMemberNumAndArticleArticleId(String memberNum, Integer articleId) {
		return arRepo.findByMemberMemberNumAndArticleArticleId(memberNum, articleId);
	}

	// 查詢某文章所有檢舉紀錄
	public List<ArticleReportBean> findByArticleArticleId(Integer articleId) {
		return arRepo.findByArticleArticleId(articleId);
	}
	
	public void edit(ArticleReportBean report) {
		arRepo.save(report);
	}

}
