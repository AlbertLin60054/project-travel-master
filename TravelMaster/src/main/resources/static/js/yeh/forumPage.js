
/*----------------------------------點擊PostArea生成該文章留言及是否已經按讚按來控制按鈕狀態Ajax---------------------------------------- */
const mainArea = document.querySelector('.mainArea');

mainArea.addEventListener('click', (e) => {

	//透過點擊當下元素來尋找最接近的含特定class元素
	if (e.target.closest('.postArea')) {
		let postArea = e.target.closest('.postArea');
		let articleId = postArea.getAttribute('data-post-id');
		let targetModal = document.querySelector('#modalId' + articleId);
		let likeArea = targetModal.querySelector('.likeArea');
		let memberNum = document.querySelector('.memberNum').value;
		findComment(articleId, targetModal);//透過觸發modal找尋該文章所有留言
		likeBtnCssControl(articleId, memberNum, likeArea);//透過會員編號、文章編號控制愛心按鈕狀態
		recordArticleViewCount(articleId);
	}

	if (e.target.closest('.insert-comment-btn')) {
		let commentBtn = e.target.closest('.insert-comment-btn')
		e.preventDefault();
		let targetModal = commentBtn.closest('.modal-content');
		let articleId = targetModal.querySelector('input[name="articleId"]').value;
		let comment = targetModal.querySelector('textarea').value;
		insertComment(comment, articleId, targetModal);
		targetModal.querySelector('textarea').value = '';
	}

	if (e.target.classList.contains('delete-comment')) {
		let deleteCommentBtn = e.target.closest('.delete-comment');
		e.preventDefault();
		let commentId = deleteCommentBtn.getAttribute('data-comment-id');
		let targetModal = deleteCommentBtn.closest('.modal-content');
		let articleId = targetModal.querySelector('input[name="articleId"]').value;
		deleteComment(commentId, articleId, targetModal);
		console.log(commentId);
		console.log(targetModal);
		console.log(articleId);
	}
	if (e.target.classList.contains('edit-comment')) {
		let editCommentBtn = e.target.closest('.edit-comment');
		e.preventDefault()
		let commentId = editCommentBtn.getAttribute('data-comment-id');
		editComment(commentId);
	}

	if (e.target.classList.contains('like-btn')) {
		let likeBtn = e.target.closest('.like-btn');
		let targetLikeArea = e.target.closest('.likeArea');
		let targetModal = likeBtn.closest('.modal-content');
		let memberNum = document.querySelector('.memberNum').value;
		let articleId = targetModal.querySelector('input[name="articleId"]').value;
		let targetPost = document.querySelector('#infoBar' + articleId);
		let targetPostLikeCountArea = targetPost.querySelector('.post-like');
		likeRequest(memberNum, articleId, targetLikeArea, targetPostLikeCountArea)
	}

	if (e.target.classList.contains('report-btn')) {
		e.preventDefault();
		let articleId = e.target.getAttribute('data-article-id');
		Swal.fire({
			title: '檢舉這一則文章的原因是?',
			input: 'radio',
			inputOptions: {
				'挑釁、歧視及謾罵他人': '挑釁、歧視及謾罵他人',
				'重複張貼及洗版': '重複張貼及洗版',
				'色情、裸露等內容': '色情、裸露等內容',
				'廣告及商業宣傳之內容': '廣告及商業宣傳之內容',
				'文章內容空泛無意義': '文章內容空泛無意義'
			},
			inputValidator: (value) => {
				if (!value) {
					return '請選擇一個選項';
				}
			}
		}).then((result) => {
			if (result.isConfirmed) {
				reportRequest(result.value, articleId)
			}
		});
	}

	if (e.target.classList.contains('collect-btn')) {
		e.preventDefault();
		let articleId = e.target.getAttribute('data-article-id');
		doCollect(articleId);
	}


})

//找特定文章的所有留言Ajax
const findAllCommentByArticleUrl = 'http://localhost:8080/TM/forum/findComment';
function findComment(targetArticleId, targetModal) {
	axios({
		url: findAllCommentByArticleUrl,
		method: 'get',
		params: {
			articleId: targetArticleId
		}
	})
		.then(response => {
			commentHtmlMaker(response.data, targetModal);
		})
		.catch(err => {
		})
}
//留言區塊字串拼接
function commentHtmlMaker(CommentArray, target) {
	let output = target.querySelector('.commentArea');
	let loginUser = document.querySelector('.memberNum');// 取得目前登入會員的編號 

	if (CommentArray.length !== 0) {
		let htmlString = '<hr><div>共' + CommentArray.length + ' 則留言</div><hr>';
		for (let i = 0; i < CommentArray.length; i++) {
			htmlString += '<div class="container-fluid d-flex flex-row mb-2 p-0">';
			htmlString += '<div class="co1">#第' + (i + 1) + '樓・</div>';
			htmlString += '<div class="co1">' + CommentArray[i].memberName + '・</div>';
			htmlString += '<div class="co1 me-auto">回覆時間:' + CommentArray[i].commentDate + '</div>';
			htmlString += '<div class="btn-group">';
			htmlString += '<button type="button" class="btn btn-light dropdown-toggle" data-bs-toggle="dropdown"></button>';
			htmlString += '<ul class="dropdown-menu">';
			htmlString += '<li><button class="dropdown-item" data-bs-toggle="modal" data-bs-target="#report" type="button">檢舉留言</button></li>';
			if (loginUser.value === CommentArray[i].memberNum) {
				htmlString += '<li><button class="dropdown-item delete-comment" data-comment-id="' + CommentArray[i].commentId + '" type="button">刪除留言</button></li>';
				htmlString += '<li><button class="dropdown-item edit-comment" data-comment-id="' + CommentArray[i].commentId + '" type="button">修改留言</button></li></ul></div></div>';
			} else {
				htmlString += '</ul></div></div>';
			}
			htmlString += '<div><p data-contentId="' + CommentArray[i].commentId + '">' + CommentArray[i].commentContent + '</p></div>';
			//htmlString += '<div class="col"><i class="fa-regular fa-heart"></i><span>like count</span></div><hr>';
			htmlString +='<hr>';
		}
		output.innerHTML = htmlString;
	}
	else {
		let htmlString = '<hr><div>尚未有任何留言</div><hr>';
		output.innerHTML = htmlString;
	}
}
//透過登入會員編號&點擊的文章modal來觸發確認是否已經按過這則文章讚?
const likeBtnControlUrl = 'http://localhost:8080/TM/forum/likeBtnControl';
function likeBtnCssControl(targetArticleId, targetMemberNum, targetLikeArea) {
	axios({
		url: likeBtnControlUrl,
		method: 'get',
		params: {
			articleId: targetArticleId,
			memberNum: targetMemberNum
		}
	})
		.then(response => {
			if (response.data.liked === true) {
				targetLikeArea.innerHTML = `<i class="like-btn fa-solid fa-heart fa-lg" style="color:red; cursor: pointer;"></i> &nbsp <span class="likeCountArea">${response.data.articleLikeCount}</span>`;
			}
			else {
				targetLikeArea.innerHTML = `<i class="like-btn fa-regular fa-heart fa-lg" style="cursor: pointer;"></i>&nbsp <span class="likeCountArea">${response.data.articleLikeCount}</span>`;
			}
		})
		.catch(err => {

		})

}

//觀看次數紀錄Ajax
function recordArticleViewCount(targetArticleId) {
	axios({
		url: 'http://localhost:8080/TM/forum/' + targetArticleId + '/recordViewCount',
		method: 'post'
	})
		.then(response => {
			console.log(response);
			let viewCount = document.querySelector(`#infoBar${targetArticleId} .viewCount`)
			viewCount.innerHTML = response.data

		})
		.catch(err => {

		})
}

//執行新增留言Ajax
const insertCommentUrl = 'http://localhost:8080/TM/forum/insertComment';
function insertComment(comment, articleId, targetModal) {
	axios({
		url: insertCommentUrl,
		method: 'post',
		params: {
			commentContent: comment,
			articleId: articleId
		}
	})
		.then(response => {
			Swal.mixin({
				toast: true,
				position: 'bottom',
				showConfirmButton: false,
				timer: 2000
			})
				.fire({
					icon: 'success',
					title: '新增留言成功 !'
				})
			console.log(response.data);
			commentHtmlMaker(response.data, targetModal);
			let commentNum = document.querySelector(`#infoBar${articleId} .commentNum`)
			commentNum.innerHTML = response.data.length

		})
		.catch(err => {
			Swal.mixin({
				toast: true,
				position: 'bottom',
				showConfirmButton: false,
				timer: 2000
			})
				.fire({
					icon: 'warning',
					title: '登入後即可留言 !'
				})
			setTimeout(() => {
				window.location.href = "http://localhost:8080/TM/login.controller";
			}, 1500);

		});
}
//執行留言修改及刪除ajax
const deleteCommentUrl = 'http://localhost:8080/TM/forum/deleteComment';
//刪除留言
function deleteComment(targetCommentId, targetArticleId, targetModal) {
	axios({
		url: deleteCommentUrl,
		method: 'post',
		params: {
			commentId: targetCommentId,
			articleId: targetArticleId
		}
	})
		.then(response => {
			Swal.mixin({
				toast: true,
				position: 'bottom',
				showConfirmButton: false,
				timer: 2000
			})
				.fire({
					icon: 'success',
					title: '刪除留言成功!'
				})

			commentHtmlMaker(response.data, targetModal);
			let commentNum = document.querySelector(`#infoBar${targetArticleId} .commentNum`)
			commentNum.innerHTML = response.data.length

		})
		.catch(err => {
		});


}
//編輯留言
function editComment(msgID) {
	let content = document.querySelector(`[data-contentid='${msgID}']`)
	let contentBody = content.parentNode;

	if (contentBody.childNodes.length <= 2) {
		const saveAndUpdateButton = document.createElement('button');
		saveAndUpdateButton.className = 'sav-update_btn btn btn-outline-primary btn-sm';
		saveAndUpdateButton.innerHTML = '送出';
		insertAfter(content, saveAndUpdateButton)
		content.setAttribute("contenteditable", "true");
		content.focus();

		saveAndUpdateButton.addEventListener('click', (e) => {
			const commentJSONObject = {
				"commentId": msgID,
				"commentContent": content.textContent
			}
			content.setAttribute("contenteditable", "false");
			saveAndUpdateButton.remove()

			doEditComment(commentJSONObject)
		}), { once: true }
	}
}

function insertAfter(referenceNode, newNode) {
	referenceNode.parentNode.insertBefore(newNode, referenceNode.nextSibling)
}

function doEditComment(json) {
	axios.put('http://localhost:8080/TM/forum/editComment', json)
		.then(res => {
			Swal.mixin({
				toast: true,
				position: 'bottom',
				showConfirmButton: false,
				timer: 1500
			})
				.fire({
					icon: 'success',
					title: '修改留言成功 !'
				});
		})
		.catch(err => {
		})
}

//按讚ajax
const likeControlUrl = 'http://localhost:8080/TM/forum/articleLike';
function likeRequest(targetMemberNum, targetArticleId, targetLikeArea, targetPostLikeCountArea) {
	axios({
		url: likeControlUrl,
		method: 'post',
		params: {
			memberNum: targetMemberNum,
			articleId: targetArticleId
		}
	})
		.then(response => {
			if (response.data.liked === true) {
				targetLikeArea.innerHTML = `<i class="like-btn fa-solid fa-heart fa-lg" style="color:red; cursor: pointer;"></i> &nbsp <span class="likeCountArea">${response.data.articleLikeCount}</span>`;
				targetPostLikeCountArea.innerHTML = response.data.articleLikeCount;
			} else {
				targetLikeArea.innerHTML = `<i class="like-btn fa-regular fa-heart fa-lg" style="cursor: pointer;"></i>&nbsp <span class="likeCountArea">${response.data.articleLikeCount}</span>`;
				targetPostLikeCountArea.innerHTML = response.data.articleLikeCount;
			}
		})
		.catch(err => {
			console.log(err)
			Swal.mixin({
				toast: true,
				position: 'bottom',
				showConfirmButton: false,
				timer: 2000
			})
				.fire({
					icon: 'warning',
					title: '登入後即可與其他用戶互動!'
				})
		})
}

//檢舉文章ajax
const reportArticleUrl = 'http://localhost:8080/TM/forum/report';
function reportRequest(reason, articleId) {
	let formData = new FormData();
	formData.append('articleId', articleId);
	formData.append('reportReason', reason);
	axios({
		url: reportArticleUrl,
		method: 'post',
		data: formData,
		headers: {
			'Content-Type': 'multipart/form-data'
		}

	})
		.then(response => {
			switch (response.status) {
				case 200:
					Swal.fire({
						icon: 'success',
						title: '已將請求送出，待審核!',
						showConfirmButton: false,
						timer: 1500
					});
					break;
			}
		})
		.catch(err => {
			switch (err.response.status) {
				case 401:
					Swal.fire({
						icon: 'warning',
						title: '登入後即可進行操作唷!',
						showConfirmButton: false,
						timer: 1500
					});
					break;
				case 409:
					Swal.fire({
						icon: 'warning',
						title: '請求已經提交過，待審核!',
						showConfirmButton: false,
						timer: 1500
					});
					break;
			}

		})
}

//文章收藏
const ArticleCollectUrl = 'http://localhost:8080/TM/forum/collect';
function doCollect(articleId) {
	let formData = new FormData();
	formData.append('articleId', articleId);
	axios.post(ArticleCollectUrl, formData)
		.then(response => {
			if (response.data === '收藏成功') {
				Swal.mixin({
					toast: true,
					position: 'bottom',
					showConfirmButton: false,
					timer: 1500
				})
					.fire({
						icon: 'success',
						title: '收藏成功!'
					});
			}
			else {
				Swal.mixin({
					toast: true,
					position: 'bottom',
					showConfirmButton: false,
					timer: 1500
				})
					.fire({
						icon: 'info',
						title: '取消收藏!'
					});
			}
		})
		.catch(err => {
			if (err.response.status === 401) {
				Swal.mixin({
					toast: true,
					position: 'bottom',
					showConfirmButton: false,
					timer: 1500
				})
					.fire({
						icon: 'error',
						title: '登入後，即可收藏文章!'
					});
			}
		})
}



/*-------------------------------------------滾動查詢----------------------------------------------- */
const findArticleUrl = 'http://localhost:8080/TM/forum/findNextPage';
let loadedArticles = []; //用來紀錄每一次載入的資料
let isLoading = false; //確認是否正在載入數據，預設是false





window.addEventListener("scroll", function () {
	let scrollPosition = window.scrollY; // 當前滾動位置
	let windowHeight = window.innerHeight; // 窗口內部高度
	let documentHeight = document.body.scrollHeight; // 檔案總高度
	if (scrollPosition + windowHeight + 1 >= documentHeight && !isLoading) {
		let currentType = document.querySelector('.currentArticleType').value;
		findArticleRequest(currentType);
	}
});

function findArticleRequest(targetArticleType) {
	let currentPage = parseInt(document.querySelector('.currentPage').value);
	let nextPage = currentPage + 1;
	isLoading = true; // 設置為true，表示正在讀取數據


	axios({
		url: findArticleUrl,
		params: {
			page: nextPage,
			articleType: targetArticleType,
		}
	})
		.then(response => {
			console.log(response.data);
			loadedArticles = loadedArticles.concat(response.data);//將加載到的數據，加入全域的陣列中
			articleHtmlMaker(loadedArticles)

			document.querySelector('.currentPage').value = nextPage;
			console.log(document.querySelector('.currentPage'));

			isLoading = false; // 加載完成，設置為false







		})
		.catch(err => {
			console.log(err);
			isLoading = false; // 發生異常設置成false，後續才能重新載入
		});
}










function articleHtmlMaker(articleArray) {
	let postOutput = document.querySelector('.appendArticle');
	let modalOutput = document.querySelector('.appendModal')

	if (articleArray.length !== 0) {
		let postHtmlString = '';
		let modalHtmlString = '';

		for (let i = 0; i < articleArray.length; i++) {
			postHtmlString += '<div class="list-group shadow" style="cursor: pointer;">';
			postHtmlString += '<div class="list-group-item list-group-item-action position-relative p-2">';
			postHtmlString += '<div class="container-fluid d-flex flex-row p-0">';
			postHtmlString += '<i class="fa-solid fa-user"></i> &nbsp';
			postHtmlString += '<div class="co1">' + articleArray[i].memberName + '</div>';
			postHtmlString += '&nbsp &nbsp <i class="fa-solid fa-calendar-days"></i>&nbsp';
			postHtmlString += '<div class="col me-auto">' + articleArray[i].articleDate + '</div>';
			postHtmlString += '<div class="btn-group">';
			postHtmlString += '<button type="button" class="btn btn-light dropdown-toggle" data-bs-toggle="dropdown"></button>';
			postHtmlString += '<ul class="dropdown-menu"> <li> <button class="collect-btn dropdown-item" type="button" data-article-id="' + articleArray[i].articleId + '">收藏文章</button> </li>';
			postHtmlString += '<li> <button class="report-btn dropdown-item" type="button" data-article-id="' + articleArray[i].articleId + '">檢舉文章</button></li></ul></div></div>';
			postHtmlString += '<div class="postArea d-flex" data-bs-toggle="modal" data-bs-target="#modalId' + articleArray[i].articleId + '"data-post-id="' + articleArray[i].articleId + '">';
			postHtmlString += '<div class="me-auto mb-auto"> <h5 >' + articleArray[i].articleName + '</h5>';
			postHtmlString += '<p>' + articleArray[i].articleSubtitle + '</p>';
			postHtmlString += '</div>';
			postHtmlString += '<img class="rounded m-1 end-0" style="width: 100px; height: 100px;" alt="..." src="http://localhost:8080/TM/forum/getImage/' + articleArray[i].articleId + '" />';
			postHtmlString += '</div>';
			postHtmlString += '<div id="infoBar' + articleArray[i].articleId + '" class="d-flex flex-row">';
			postHtmlString += '<i class="fa-solid fa-heart"></i>&nbsp';
			postHtmlString += '<div class="post-like">' + articleArray[i].articleLikeCount + '</div>';
			postHtmlString += '&nbsp&nbsp&nbsp&nbsp<i class="fa-solid fa-comment"></i>&nbsp';
			postHtmlString += '<div class="commentNum">' + articleArray[i].articleCommentNum + '</div>';
			postHtmlString += '&nbsp&nbsp&nbsp<i class="fa-solid fa-eye"></i>&nbsp';
			postHtmlString += '<div class="viewCount">' + articleArray[i].articleViewCount + '</div>';
			postHtmlString += '</div>';
			postHtmlString += '</div>';
			postHtmlString += '</div>';
			// 彈跳視窗部分

			modalHtmlString += '<div class="modal fade" id="modalId' + articleArray[i].articleId + '" tabindex="-1">';
			modalHtmlString += '<div class="modal-dialog modal-xl modal-dialog-centered modal-dialog-scrollable">';
			modalHtmlString += '<div class="modal-content">';
			modalHtmlString += '<div class="modal-header">';
			modalHtmlString += '<i class="fa-solid fa-user"></i>&nbsp<span class="modal-title fs-5" >' + articleArray[i].memberName + '</span>';
			modalHtmlString += '<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>';
			modalHtmlString += '</div>';
			modalHtmlString += '<div class="modal-body">';
			modalHtmlString += '<div>';
			modalHtmlString += '<h3>' + articleArray[i].articleName + '</h3>';
			modalHtmlString += '</div>';
			modalHtmlString += '<span><a class="articleTypeLink" href="#">' + articleArray[i].articleType + '</a>・</span>';
			modalHtmlString += '<span>' + '發布時間:' + articleArray[i].articleDate + '</span>';
			modalHtmlString += '<div class="row">';
			modalHtmlString += '</div>';
			modalHtmlString += '<p>' + articleArray[i].articleContent + '</p>';
			modalHtmlString += '<div class="likeArea">';
			modalHtmlString += '&nbsp;<span class="likeCountArea">' + articleArray[i].articleLikeCount + '</span>';
			modalHtmlString += '</div>';
			modalHtmlString += '<div class="commentArea"></div>';
			modalHtmlString += '</div>';
			modalHtmlString += '<div class="modal-footer p-0">';
			modalHtmlString += '<div class="container">';
			modalHtmlString += '<div class="row g-1">';
			modalHtmlString += '<div class="col">';
			modalHtmlString += '<input type="hidden" name="articleId" value="' + articleArray[i].articleId + '">';
			modalHtmlString += '<textarea class="form-control" rows="1" placeholder="留言...."></textarea>';
			modalHtmlString += '</div>';
			modalHtmlString += '<div class="col-auto">';
			modalHtmlString += '<button class="insert-comment-btn btn btn-outline-primary">送出</button>';
			modalHtmlString += '</div>';
			modalHtmlString += '</div>';
			modalHtmlString += '</div>';
			modalHtmlString += '</div>';
			modalHtmlString += '</div>';
			modalHtmlString += '</div>';
			modalHtmlString += '</div>';

		}
		postOutput.innerHTML = postHtmlString;
		modalOutput.innerHTML = modalHtmlString;
	}

}

