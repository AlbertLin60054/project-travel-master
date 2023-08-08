package com.tm.TravelMaster.yeh.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tm.TravelMaster.yeh.model.CommentBean;
import com.tm.TravelMaster.yeh.model.CommentRepository;

@Service
public class CommentService {

	@Autowired
	private CommentRepository cRepo;

	// 新增一則留言並返回新增完的結果
	public void insertComment(CommentBean comment) {
		cRepo.save(comment);
	}

	// 查詢所有留言
	public List<CommentBean> findAll() {
		return cRepo.findAll();
	}

	// 查詢特定文章的所有留言
	public List<CommentBean> findAllByArticleId(Integer articleId) {
		return cRepo.findAllByArticleId(articleId);
	}
	
	//透過ID刪除文章
	public void deleteById(Integer commentId) {
		cRepo.deleteById(commentId);
	}
	
	@Transactional
	public CommentBean editCommentById(Integer commentId, String newComment) {
		Optional<CommentBean> optional = cRepo.findById(commentId);
		if(optional.isPresent()) {
			CommentBean comment = optional.get();
			comment.setCommentContent(newComment);
			return comment;
		}
		return null;
	}
}
