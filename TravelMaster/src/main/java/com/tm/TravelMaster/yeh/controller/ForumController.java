package com.tm.TravelMaster.yeh.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.tm.TravelMaster.yeh.service.ArticleCollectionService;
import com.tm.TravelMaster.yeh.service.ArticleLikeService;
import com.tm.TravelMaster.yeh.service.ArticleReportService;
import com.tm.TravelMaster.yeh.service.ArticleService;
import com.tm.TravelMaster.yeh.service.CommentService;
import com.tm.TravelMaster.yeh.model.ArticleBean;
import com.tm.TravelMaster.yeh.model.ArticleCollectionBean;
import com.tm.TravelMaster.yeh.model.ArticleDTO;
import com.tm.TravelMaster.yeh.model.ArticleLikeBean;
import com.tm.TravelMaster.yeh.model.ArticleLikeDTO;
import com.tm.TravelMaster.yeh.model.ArticleReportBean;
import com.tm.TravelMaster.yeh.model.CommentBean;
import com.tm.TravelMaster.yeh.model.CommentDTO;
import com.tm.TravelMaster.chih.dao.MemberService;
import com.tm.TravelMaster.chih.model.Member;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForumController {

	@Autowired
	private ArticleService aService;
	@Autowired
	private CommentService cService;
	@Autowired
	private ArticleLikeService alService;
	@Autowired
	private ArticleReportService arService;
	@Autowired
	private MemberService mService;
	@Autowired
	private ArticleCollectionService acService;

	/*----------------------------------------------頁面跳轉相關--------------------------------------------------*/

	// 進入論壇首頁
	@GetMapping("/forum/intoForumPage")
	public String intoForumPage(Model model, HttpSession session,
			@RequestParam(name = "page", defaultValue = "1") Integer pageNum) {
		Member member = (Member) session.getAttribute("mbsession");
		Page<ArticleBean> page = aService.findPublicArticle(pageNum);
		Page<ArticleBean> likeFirstPage = aService.findPublicArticlesOrderByLikeCountDesc(1);// 只取第一頁按讚數前五名
		Page<ArticleBean> viewFirstPage = aService.findPublicArticlesOrderByViewCountDesc(1);// 只取第一頁按讚數前五名
		model.addAttribute("page", page);
		model.addAttribute("likeFirstPage", likeFirstPage);// 按讚數前五
		model.addAttribute("viewFirstPage", viewFirstPage);// 按讚數前五
		model.addAttribute("articleType", "all");// 首頁包含所有種類，故設定all;
		if (member != null) {
			model.addAttribute("member", member);
		}
		return "yeh/forumPage";
	}

	// 模糊搜尋
	@GetMapping("/forum/search")
	public String articleSearch(Model model, HttpSession session, @RequestParam("keyword") String keyword,
			@RequestParam(name = "page", defaultValue = "1") Integer pageNum) {
		Member member = (Member) session.getAttribute("mbsession");
		Page<ArticleBean> page = aService.searchArticleByKeyword(keyword, pageNum);
		Page<ArticleBean> likeFirstPage = aService.findPublicArticlesOrderByLikeCountDesc(1);
		Page<ArticleBean> viewFirstPage = aService.findPublicArticlesOrderByViewCountDesc(1);
		model.addAttribute("page", page);
		model.addAttribute("likeFirstPage", likeFirstPage);
		model.addAttribute("viewFirstPage", viewFirstPage);
		model.addAttribute("articleType", "結果");
		if (member != null) {
			model.addAttribute("member", member);
		}

		return "yeh/forumPage";
	}

	// 文章分類跳轉
	@GetMapping("/forum/articleType")
	public String findPublicArticleByArticleType(HttpSession session, @RequestParam("articleType") String articleType,
			Model model, @RequestParam(name = "page", defaultValue = "1") Integer pageNum) {
		Member member = (Member) session.getAttribute("mbsession");
		Page<ArticleBean> page = aService.findPublicArticlesByArticleType(articleType, pageNum);
		Page<ArticleBean> likeFirstPage = aService.findPublicArticlesByArticleTypeOrderByLikeCountDesc(articleType, 1);
		Page<ArticleBean> viewFirstPage = aService.findPublicArticlesByArticleTypeOrderByViewCountDesc(articleType, 1);
		model.addAttribute("page", page);
		model.addAttribute("likeFirstPage", likeFirstPage);// 按讚數前五
		model.addAttribute("viewFirstPage", viewFirstPage);// 觀看數前五
		model.addAttribute("articleType", articleType);// 返回查詢了哪一種類的文章
		if (member != null) {
			model.addAttribute("member", member);
		}
		return "yeh/forumPage";
	}

	// 加載更多文章 當前的文章類別加載更多文章
	@ResponseBody
	@GetMapping("/forum/findNextPage")
	public List<ArticleDTO> findNextPage(@RequestParam("page") Integer pageNum,
			@RequestParam("articleType") String articleType) {
		if (articleType.equals("all")) {
			Page<ArticleBean> page = aService.findPublicArticle(pageNum);
			Integer totalPages = page.getTotalPages();
			List<ArticleBean> articles = page.getContent();
			List<ArticleDTO> dtos = new ArrayList<>();
			for (ArticleBean article : articles) {
				ArticleDTO dto = new ArticleDTO();
				dto.setArticleId(article.getArticleId());
				dto.setArticlePic(article.getArticlePic());
				dto.setArticleDate(article.getArticleDate());
				dto.setArticleName(article.getArticleName());
				dto.setArticleType(article.getArticleType());
				dto.setArticleContent(article.getArticleContent());
				dto.setArticleSubtitle(article.getArticleSubtitle());
				dto.setArticleViewCount(article.getArticleViewCount());
				dto.setArticleLikeCount(article.getArticleLikeCount());
				dto.setMemberName(article.getMember().getMemberName());
				dto.setTotalPage(totalPages);
				dto.setArticleCommentNum(article.getComments().size());
				dtos.add(dto);
			}

			return dtos;
		} else {
			Page<ArticleBean> page = aService.findPublicArticlesByArticleType(articleType, pageNum);
			Integer totalPages = page.getTotalPages();
			List<ArticleBean> articles = page.getContent();
			List<ArticleDTO> dtos = new ArrayList<>();
			for (ArticleBean article : articles) {
				ArticleDTO dto = new ArticleDTO();
				dto.setArticleId(article.getArticleId());
				dto.setArticlePic(article.getArticlePic());
				dto.setArticleDate(article.getArticleDate());
				dto.setArticleName(article.getArticleName());
				dto.setArticleType(article.getArticleType());
				dto.setArticleContent(article.getArticleContent());
				dto.setArticleSubtitle(article.getArticleSubtitle());
				dto.setArticleViewCount(article.getArticleViewCount());
				dto.setArticleLikeCount(article.getArticleLikeCount());
				dto.setMemberName(article.getMember().getMemberName());
				dto.setTotalPage(totalPages);
				dto.setArticleCommentNum(article.getComments().size());
				dtos.add(dto);
			}
			return dtos;
		}

	}

	// 進入會員個人文章頁面
	@GetMapping("/forum/memberCenter/articleMange")
	public String intoForumMemberCenter(HttpSession session,
			@RequestParam(name = "page", defaultValue = "1") Integer pageNum, Model model) {
		Member member = (Member) session.getAttribute("mbsession");
		if (member != null) {
			Page<ArticleBean> page = aService.findPublicArticlesByMemberNum(member.getMemberNum(), pageNum);
			model.addAttribute("page", page);
			model.addAttribute("member", member);

			return "yeh/forumMemberCenter";
		} else {
			return "redirect:/login.controller";
		}
	}

	// 進入會員文章收藏頁面
	@GetMapping("/forum/memberCenter/collection")
	public String intoForumMemberCenterMyCollection(HttpSession session,
			@RequestParam(name = "page", defaultValue = "1") Integer pageNum, Model model) {
		Member member = (Member) session.getAttribute("mbsession");
		if (member != null) {
			Page<ArticleCollectionBean> page = acService.findAllByMemberMemberNum(member.getMemberNum(), pageNum);
			model.addAttribute("page", page);
			model.addAttribute("member", member);
			return "yeh/forumMemberCenterCollection";
		} else {
			return "redirect:/login.controller";
		}

	}

	// 進入新增文章頁面
	@GetMapping("/forum/insertArticle")
	public String intoInsertArticle(HttpSession session) {
		Member member = (Member) session.getAttribute("mbsession");
		if (member != null) {
			return "yeh/insertArticle";
		} else {
			return "redirect:/login.controller";
		}
	}

	// 進入後台頁面
	@GetMapping("/forum/forumBackstage")
	public String intoForumBackstage(Model model) {
		List<ArticleBean> articles = aService.findAllArticle();
		model.addAttribute("articles", articles);
		return "yeh/forumBackstage";
	}

	// 搜尋特定照片
	@GetMapping("/forum/getImage/{articleId}")
	public ResponseEntity<byte[]> getImage(@PathVariable Integer articleId) {
		ArticleBean article = aService.findById(articleId);
		byte[] articlePic = article.getArticlePic();
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<byte[]>(articlePic, header, HttpStatus.OK);
	}

	/*----------------------------------------------文章相關操作--------------------------------------------------*/

	// 執行新增文章
	@PostMapping("/forum/insertArticle")
	public String doInsertArticle(HttpSession session, @RequestParam("articleName") String articleName,
			@RequestParam("articleType") String articleType, @RequestParam("articleContent") String articleContent,
			@RequestParam("articlePic") MultipartFile articlePic,
			@RequestParam("articleSubtitle") String articleSubtitle, Model model) {
		Member member = (Member) session.getAttribute("mbsession");
		try {
			ArticleBean article = new ArticleBean();
			article.setArticleName(articleName);
			article.setArticleSubtitle(articleSubtitle);
			article.setArticleContent(articleContent);
			article.setArticleType(articleType);
			article.setArticlePic(articlePic.getBytes());
			Hibernate.initialize(member);
			article.setMember(member);
			aService.insert(article);
			return "redirect:/forum/memberCenter/articleMange";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/forum/forumBackstage";
	}

	// 取得欲修改文章
	@GetMapping("/forum/intoEdit")
	public String getEditArticle(@RequestParam("articleId") Integer articleId, Model model) {
		ArticleBean result = aService.findById(articleId);
		model.addAttribute("article", result);
		return "yeh/editArticle";
	}

	// 執行文章修改
	@PostMapping("/forum/doEdit")
	public String doEditArticle(HttpSession session, @RequestParam("articleId") Integer articleId,
			@RequestParam("articleName") String articleName, @RequestParam("articleSubtitle") String articleSubtitle,
			@RequestParam("articleType") String articleType, @RequestParam("articleContent") String articleContent,
			@RequestParam("articlePic") MultipartFile articlePic) {
		Member member = (Member) session.getAttribute("mbsession");
		try {
			ArticleBean article = new ArticleBean();
			article.setArticleId(articleId);
			article.setArticleName(articleName);
			article.setArticleSubtitle(articleSubtitle);
			article.setArticleContent(articleContent);
			article.setArticleType(articleType);
			article.setArticlePic(articlePic.getBytes());
			article.setMember(member);
			aService.edit(article);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/forum/memberCenter/articleMange";
	}

	// 執行刪除文章
	@DeleteMapping("/forum/deleteArticle")
	public ResponseEntity<?> doDeleteArticle(@RequestParam("articleId") Integer articleId) {
		ArticleBean article = aService.findById(articleId);
		if (article != null) {
			article.setArticleStatus("刪除");
			aService.edit(article);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();// 找不到要刪除文章
	}

	// 紀錄文章觀看次數
	@PostMapping("/forum/{articleId}/recordViewCount")
	public ResponseEntity<?> recordArticleViewCount(@PathVariable Integer articleId) {
		ArticleBean article = aService.findById(articleId);
		article.setArticleViewCount(article.getArticleViewCount() + 1);
		aService.edit(article);
		return ResponseEntity.ok(article.getArticleViewCount());

	}

	/*----------------------------------------------留言相關操作--------------------------------------------------*/

	// 取得特定文章的留言
	@ResponseBody
	@GetMapping("/forum/findComment")
	public List<CommentDTO> findCommentByArticleId(@RequestParam("articleId") Integer articleId) {
		List<CommentBean> comments = cService.findAllByArticleId(articleId);
		List<CommentDTO> dtos = new ArrayList<>();
		for (CommentBean comment : comments) {
			CommentDTO dto = new CommentDTO();
			dto.setCommentId(comment.getCommentId());
			dto.setCommentDate(comment.getCommentDate());
			dto.setCommentContent(comment.getCommentContent());
			dto.setMemberNum(comment.getMember().getMemberNum());
			dto.setMemberName(comment.getMember().getMemberName());
			dtos.add(dto);
		}
		return dtos;
	}

	// 新增留言
	@ResponseBody
	@PostMapping("/forum/insertComment")
	public ResponseEntity<?> insertComment(HttpSession session, @RequestParam("commentContent") String commentContent,
			@RequestParam("articleId") Integer articleId) {
		Member member = (Member) session.getAttribute("mbsession");
		if (member != null) {
			ArticleBean article = aService.findById(articleId);
			CommentBean NewComment = new CommentBean();
			NewComment.setCommentContent(commentContent);
			NewComment.setArticle(article);
			NewComment.setMember(member);
			cService.insertComment(NewComment);
			List<CommentBean> comments = cService.findAllByArticleId(articleId);
			List<CommentDTO> dtos = new ArrayList<>();
			for (CommentBean comment : comments) {
				CommentDTO dto = new CommentDTO();
				dto.setCommentId(comment.getCommentId());
				dto.setCommentDate(comment.getCommentDate());
				dto.setCommentContent(comment.getCommentContent());
				dto.setMemberNum(comment.getMember().getMemberNum());
				dto.setMemberName(comment.getMember().getMemberName());
				dtos.add(dto);
			}
			return ResponseEntity.ok(dtos);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入!!");
		}
	}

	// 刪除留言
	@ResponseBody
	@PostMapping("/forum/deleteComment")
	public ResponseEntity<List<CommentDTO>> deleteComment(@RequestParam("commentId") Integer commentId,
			@RequestParam("articleId") Integer articleId) {

		cService.deleteById(commentId);
		List<CommentBean> comments = cService.findAllByArticleId(articleId);
		List<CommentDTO> dtos = new ArrayList<>();
		for (CommentBean comment : comments) {
			CommentDTO dto = new CommentDTO();
			dto.setCommentId(comment.getCommentId());
			dto.setCommentDate(comment.getCommentDate());
			dto.setCommentContent(comment.getCommentContent());
			dto.setMemberNum(comment.getMember().getMemberNum());
			dto.setMemberName(comment.getMember().getMemberName());
			dtos.add(dto);
		}
		return ResponseEntity.ok(dtos);

	}

	// 留言修改
	@ResponseBody
	@PutMapping("/forum/editComment")
	public String editMessage(@RequestBody CommentDTO commentDTO) {
		CommentBean newComment = cService.editCommentById(commentDTO.getCommentId(), commentDTO.getCommentContent());
		return newComment.getCommentContent();
	}

	/*----------------------------------------------按讚相關操作--------------------------------------------------*/

	@ResponseBody
	@PostMapping("/forum/articleLike")
	public ArticleLikeDTO likeControl(@RequestParam("memberNum") String memberNum,
			@RequestParam("articleId") Integer articleId) {
		Optional<ArticleLikeBean> optional = alService.findByMemberNumAndArticleId(memberNum, articleId);
		ArticleBean article = aService.findById(articleId);
		Member member = mService.findByMemberNum(memberNum);
		if (optional.isEmpty()) {
			ArticleLikeDTO dto = new ArticleLikeDTO();
			ArticleLikeBean like = new ArticleLikeBean();
			article.setArticleLikeCount(article.getArticleLikeCount() + 1);
			aService.edit(article);
			like.setArticle(article);
			like.setMember(member);
			alService.addLike(like);
			dto.setLiked(true);
			dto.setArticleLikeCount(article.getArticleLikeCount());
			return dto;
		} else {
			ArticleLikeDTO dto = new ArticleLikeDTO();
			ArticleLikeBean like = optional.get();
			article.setArticleLikeCount(article.getArticleLikeCount() - 1);
			aService.edit(article);
			alService.removeLike(like);
			dto.setLiked(false);
			dto.setArticleLikeCount(article.getArticleLikeCount());
			return dto;
		}

	}

	// 判斷某會員是否已經按過某文章讚，對按鈕做控制

	@GetMapping("/forum/likeBtnControl")
	public ResponseEntity<ArticleLikeDTO> likeBtnControl(@RequestParam("memberNum") String memberNum,
			@RequestParam("articleId") Integer articleId) {
		List<ArticleLikeBean> list = alService.findBymemberIdAndArticleId(memberNum, articleId);
		ArticleBean article = aService.findById(articleId);
		ArticleLikeDTO dto = new ArticleLikeDTO();
		if (list != null && list.size() > 0) {
			dto.setArticleLikeCount(article.getArticleLikeCount());
			dto.setLiked(true);
			return ResponseEntity.ok(dto);
		} else {
			dto.setArticleLikeCount(article.getArticleLikeCount());
			dto.setLiked(false);
			return ResponseEntity.ok(dto);
		}
	}

	/*----------------------------------------------檢舉相關操作--------------------------------------------------*/

	@PostMapping("/forum/report")
	public ResponseEntity<?> sendReport(HttpSession session, @RequestParam("reportReason") String reportReason,
			@RequestParam("articleId") Integer articleId) {
		Member member = (Member) session.getAttribute("mbsession");

		if (member == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入!");
		} else {
			ArticleReportBean report = arService.findByMemberMemberNumAndArticleArticleId(member.getMemberNum(),
					articleId);
			if (report == null || report.getArticleReportStatus().equals("審核完畢")) {
				ArticleBean article = aService.findById(articleId);
				ArticleReportBean newReport = new ArticleReportBean();
				newReport.setReportReason(reportReason);
				newReport.setArticle(article);
				newReport.setMember(member);
				arService.insertReport(newReport);
				return ResponseEntity.ok().build();
			} else {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("該用戶已舉過!");
			}
		}

	}

	/*------------------------------------------------收藏相關操作---------------------------------------------------*/

	@PostMapping("/forum/collect")
	public ResponseEntity<?> doCollect(HttpSession session, @RequestParam("articleId") Integer articleId) {
		Member member = (Member) session.getAttribute("mbsession");

		System.out.println("文章ID" + articleId);
		if (member == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
		} else {
			ArticleCollectionBean collection = acService.findByMemberMemberNumAndArticleArticleId(member.getMemberNum(),
					articleId);
			if (collection == null) {
				ArticleBean article = aService.findById(articleId);
				ArticleCollectionBean newCollection = new ArticleCollectionBean();
				newCollection.setArticle(article);
				newCollection.setMember(member);
				acService.saveCollection(newCollection);
				return ResponseEntity.ok("收藏成功");
			} else {
				acService.removeCollection(collection);
				return ResponseEntity.ok("取消收藏");
			}

		}

	}

}
