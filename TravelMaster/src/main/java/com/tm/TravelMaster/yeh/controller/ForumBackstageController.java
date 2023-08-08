package com.tm.TravelMaster.yeh.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tm.TravelMaster.chih.model.Member;
import com.tm.TravelMaster.yeh.model.ArticleBean;
import com.tm.TravelMaster.yeh.model.ArticleDTO;
import com.tm.TravelMaster.yeh.model.ArticleReportBean;
import com.tm.TravelMaster.yeh.service.ArticleReportService;
import com.tm.TravelMaster.yeh.service.ArticleService;
import com.tm.TravelMaster.yeh.service.MailService;

@Controller
public class ForumBackstageController {

	@Autowired
	private ArticleService aService;
	@Autowired
	private ArticleReportService arService;
	@Autowired
	private MailService mService;

	@GetMapping("/forum/backstage")
	public String intoForumBackstage(Model model) {
		List<ArticleBean> articles = aService.findAllArticle();
		model.addAttribute("articles", articles);
		return "yeh/forumBackstage";
	}

	// 進入檢舉列表
	@GetMapping("/forum/reportBackstage")
	public String intoForumReportBackstage(@RequestParam("articleId") Integer articleId, Model model) {
		List<ArticleReportBean> reports = arService.findByArticleArticleId(articleId);
		model.addAttribute("reports", reports);
		return "yeh/forumReportBackStage";
	}

	// 檢舉文章封鎖處理
	@PostMapping("/forum/banArticle")
	public ResponseEntity<?> banArticle(@RequestParam("articleId") Integer articleId) {
		ArticleBean article = aService.findById(articleId);
		if (article != null) {
			if (!article.getArticleStatus().equals("封鎖")) {
				article.setArticleStatus("封鎖");
				List<ArticleReportBean> reports = article.getReport();
				for (ArticleReportBean report : reports) {
					report.setArticleReportStatus("審核完畢");
				}
				article.setReport(reports);
				aService.edit(article);
				return ResponseEntity.ok().body("文章封鎖");
			} else {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("文章已被封鎖");
			}
		} else {

			return ResponseEntity.notFound().build();
		}
	}

	// 駁回檢舉
	@PostMapping("/forum/rejectReport")
	public ResponseEntity<?> rejectArticleReport(@RequestParam("articleReportId") Integer articleReportId) {
		ArticleReportBean report = arService.findByArticleReportId(articleReportId);
		if (report == null) {
			return ResponseEntity.notFound().build();
		} else {
			if (report.getArticleReportStatus().equals("審核完畢")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("該檢舉已審核");
			} else {
				Member member = report.getMember();
				report.setArticleReportStatus("審核完畢");
				arService.edit(report);
				mService.sendReportRejectMail(member.getMemberMail(), report.getArticle().getArticleName(),
						report.getReportReason());
				return ResponseEntity.ok().build();
			}
		}

	}

	// 變更文章狀態
	@PutMapping("/forum/editStatus")
	public ResponseEntity<?> editArticleStatus(@RequestBody ArticleDTO dto) {
		Integer articleId = dto.getArticleId();
		String articleStatus = dto.getArticleStatus();
		ArticleBean article = aService.findById(articleId);
		article.setArticleStatus(articleStatus);
		aService.edit(article);
		return ResponseEntity.ok().build();

	}

	@GetMapping("/forum/forumChart")
	public String intoForumChartBackStage(Model model) {
		List<ArticleBean> articles = aService.findAllArticle();
		// 取得文章類別 各類別數量
		Map<String, Integer> articleTypeCountMap = new HashMap<>();
		for (ArticleBean article : articles) {
			String articleType = article.getArticleType();
			Integer count = articleTypeCountMap.getOrDefault(articleType, 0);
			articleTypeCountMap.put(articleType, count + 1);
		}
		// 取得國內貼文按讚前5
		Page<ArticleBean> page = aService.findPublicArticlesByArticleTypeOrderByLikeCountDesc("國內", 1);
		List<ArticleBean> articlesLikeInternal = page.getContent();
		List<ArticleDTO> dtos = new ArrayList<>();
		for (ArticleBean article : articlesLikeInternal) {
			ArticleDTO dto = new ArticleDTO();
			dto.setArticleLikeCount(article.getArticleLikeCount());
			dto.setArticleName(article.getArticleName());
			dtos.add(dto);
		}
		// 取得國內貼文點閱前5
		Page<ArticleBean> viewPage = aService.findPublicArticlesByArticleTypeOrderByViewCountDesc("國內", 1);
		List<ArticleBean> articleViewInternal = viewPage.getContent();
		List<ArticleDTO> dtos2 =new ArrayList<>();
		for (ArticleBean article : articleViewInternal) {
			ArticleDTO dto = new ArticleDTO();
			dto.setArticleViewCount(article.getArticleViewCount());
			dto.setArticleName(article.getArticleName());
			dtos2.add(dto);
		}
		

		model.addAttribute("articleTypeCount", articleTypeCountMap);
		model.addAttribute("dtos",dtos);
		model.addAttribute("dtos2",dtos2);

		return "yeh/forumChartBackStage";
	}

	// 回傳個文章讚數統計
	@ResponseBody
	@GetMapping("/forum/Top5LikeByArticleType")
	public List<ArticleDTO> findArticleLikeTop5ByArticleType(@RequestParam("articleType") String articleType) {
		Page<ArticleBean> page = aService.findPublicArticlesByArticleTypeOrderByLikeCountDesc(articleType, 1);
		List<ArticleBean> articles = page.getContent();
		List<ArticleDTO> dtos = new ArrayList<>();
		for (ArticleBean article : articles) {
			ArticleDTO dto = new ArticleDTO();
			dto.setArticleLikeCount(article.getArticleLikeCount());
			dto.setArticleName(article.getArticleName());
			dtos.add(dto);
		}
		return dtos;
	}
	
	@ResponseBody
	@GetMapping("/forum/Top5ViewByArticleType")
	public List<ArticleDTO> findArticleViewTop5ByArticleType(@RequestParam("articleType") String articleType){
		Page<ArticleBean> page = aService.findPublicArticlesByArticleTypeOrderByViewCountDesc(articleType,1);
		List<ArticleBean> articles = page.getContent();
		List<ArticleDTO> dtos = new ArrayList<>();
		for (ArticleBean article : articles) {
			ArticleDTO dto = new ArticleDTO();
			dto.setArticleViewCount(article.getArticleViewCount());
			dto.setArticleName(article.getArticleName());
			dtos.add(dto);
		}
		return dtos;
		
		
	}
	

}
