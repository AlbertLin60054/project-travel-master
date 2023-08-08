
/*----------------------------------點擊文章生成該文章留言及是否已經按讚按來控制按鈕狀態Ajax---------------------------------------- */

const findAllCommentByArticleUrl = 'http://localhost:8080/TM/forum/findComment'; //查找特定文章留言
const likeBtnControlUrl = 'http://localhost:8080/TM/forum/likeBtnControl';//確認用戶是否已經按過讚
const postArea = document.querySelectorAll('.postArea');


for (let i = 0; i < postArea.length; i++) {
	postArea[i].addEventListener('click', (e) => {
		let articleId = e.currentTarget.getAttribute('data-post-id');
		let targetModal = document.querySelector('#modalId' + articleId);
		let likeArea = targetModal.querySelector('.likeArea');
		let memberNum = document.querySelector('.memberNum').value;
		findComment(articleId, targetModal);
		likeBtnCssControl(articleId, memberNum, likeArea);
		//recordArticleViewCount(articleId);

	});
}

//找特定文章的所有留言
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

//觀看次數紀錄
// function recordArticleViewCount(targetArticleId) {
// 	axios({
// 		url: 'http://localhost:8080/TM/forum/' + targetArticleId + '/recordViewCount',
// 		method: 'post'
// 	})
// 		.then(response => {
// 			console.log(response);
// 			let viewCount = document.querySelector(`#infoBar${targetArticleId} .viewCount`)
// 			viewCount.innerHTML = response.data

// 		})
// 		.catch(err => {

// 		})
// }



//透過登入會員編號&點擊的文章modal來觸發確認是否已經按過這則文章讚?
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

/*-----------------------------------新增留言Ajax--------------------------------------- */

const insertCommentUrl = 'http://localhost:8080/TM/forum/insertComment';
const commentBtn = document.querySelectorAll('.insert-comment-btn');

for (let i = 0; i < commentBtn.length; i++) {
	commentBtn[i].addEventListener('click', (e) => {
		e.preventDefault();
		const modal = commentBtn[i].closest('.modal-content');
		const articleId = modal.querySelector('input[name="articleId"]').value;
		const comment = modal.querySelector('textarea.form-control').value;
		let output = modal.querySelector('.commentArea');
		insertComment(comment, articleId, modal);
		modal.querySelector('.form-control').value = '';

	})
}

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

/*--------------------------------------執行刪除 & 修改ajax--------------------------------------------- */
const deleteCommentUrl = 'http://localhost:8080/TM/forum/deleteComment';
let commentArea = document.querySelectorAll('.commentArea');  // 透過父元素觸發委託事件

for (let i = 0; i < commentArea.length; i++) {
	commentArea[i].addEventListener('click', (e) => {
		switch (true) {
			case e.target.classList.contains('delete-comment'): {
				e.preventDefault();
				let commentId = e.target.getAttribute('data-comment-id');
				let modal = commentArea[i].closest('.modal-content');
				let articleId = modal.querySelector('input[name="articleId"]').value;
				deleteComment(commentId, articleId, modal)
				break;
			}
			case e.target.classList.contains('edit-comment'): {
				e.preventDefault();
				let commentId = e.target.getAttribute('data-comment-id');
				editComment(commentId);
				break;
			}

		}
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
				timer: 2000
			})
				.fire({
					icon: 'success',
					title: '修改留言成功 !'
				})
		})
		.catch(err => {
		})
}

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

/*------------------------------------------按讚ajax----------------------------------------- */

const likeControlUrl = 'http://localhost:8080/TM/forum/articleLike';

let likeArea = document.querySelectorAll('.likeArea');
for (let i = 0; i < likeArea.length; i++) {
	likeArea[i].addEventListener('click', (e) => {
		if (e.target.classList.contains('like-btn')) {
			let modal = likeArea[i].closest('.modal-content');
			let memberNum = document.querySelector('.memberNum').value;
			let articleId = modal.querySelector('input[name="articleId"]').value;
			likeRequest(memberNum, articleId, i)
		}
	});
}

//對某文章執行添加讚或取消的請求
function likeRequest(targetMemberNum, targetArticleId, index) {
	axios({
		url: likeControlUrl,
		method: 'post',
		params: {
			memberNum: targetMemberNum,
			articleId: targetArticleId
		}
	})
		.then(response => {
			let likeArea = document.querySelectorAll('.likeArea')
			console.log(likeArea);
			let postLike = document.querySelectorAll('.post-like');
			if (response.data.liked === true) {

				likeArea[index].innerHTML = `<i class="like-btn fa-solid fa-heart fa-lg" style="color:red; cursor: pointer;"></i> &nbsp <span class="likeCountArea">${response.data.articleLikeCount}</span>`;

				postLike[index].innerHTML = response.data.articleLikeCount;
			} else {

				likeArea[index].innerHTML = `<i class="like-btn fa-regular fa-heart fa-lg" style="cursor: pointer;"></i>&nbsp <span class="likeCountArea">${response.data.articleLikeCount}</span>`;
				postLike[index].innerHTML = response.data.articleLikeCount;
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


/*------------------------------------------檢舉相關操作---------------------------------------------- */

const reportArticleUrl = 'http://localhost:8080/TM/forum/report';

let reportBtn = document.querySelectorAll('.report-btn');
for (let i = 0; i < reportBtn.length; i++) {
	reportBtn[i].addEventListener('click', (e) => {
		e.preventDefault();
		let articleId = e.currentTarget.getAttribute('data-article-id')

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

	});
}

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
/*-----------------------------------------留言區塊拼接函數--------------------------------------------- */

//留言區塊字串拼接
function commentHtmlMaker(CommentArray, target) {
	let output = target.querySelector('.commentArea');
	let loginUser = document.querySelector('.memberNum');/* 取得目前登入會員的編號 */

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
			htmlString+='<hr>'
		}
		output.innerHTML = htmlString;
	}
	else {
		let htmlString = '<hr><div>尚未有任何留言</div><hr>';
		output.innerHTML = htmlString;
	}
}



