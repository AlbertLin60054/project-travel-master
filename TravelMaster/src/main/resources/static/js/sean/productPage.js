//alert提醒(事件代理)
$(document).ready(function() {
	$(document).on('submit', '.addToCartForm', function(event) {
		event.preventDefault();

		var form = $(this);

		$.ajax({
			url: 'http://localhost:8080/TM/sean/addToCart',
			type: 'POST',
			data: form.serialize(),
			success: function(response) {
				if (response === '已成功加入商品') {
					Swal.fire({
						title: 'Success',
						text: response,
						icon: 'success',
						confirmButtonText: 'OK'
					});
				} else if (response === '商品已存在於購物車中') {
					Swal.fire({
						title: 'Warning',
						text: response,
						icon: 'warning',
						confirmButtonText: 'OK'
					});
				}
			},
			error: function(xhr, status, error) {
				if (xhr.responseText === '請先登入') {
					Swal.fire({
						title: 'Error',
						text: xhr.responseText,
						icon: 'error',
						confirmButtonText: 'OK'
					}).then(() => {
						window.location.href = '/TM/login.controller';
					});
				} else {
					Swal.fire({
						title: 'Error',
						text: xhr.responseText,
						icon: 'error',
						confirmButtonText: 'OK'
					});
				}
			}
		});
	});
});

//上下架狀態切換
function confirmToggle(event) {
	event.preventDefault();

	Swal.fire({
		title: '是否要切換商品狀態？',
		icon: 'question',
		showCancelButton: true,
		confirmButtonText: '確認',
		cancelButtonText: '取消',
	}).then((result) => {
		if (result.isConfirmed) {
			const form = event.target.closest('form');
			form.submit();
		}
	});
}

//查詢後關閉滾動式查詢
$('.queryBtn').click(function() {
	$('#loadMoreContainer').hide();
});

//滾動式查詢
$(document).ready(function() {
	var page = 1;
	var loading = false;
	var hasMoreProducts = true;

	$(window).scroll(function() {
		if ($('#loadMoreContainer').is(':visible') && $(window).scrollTop() + $(window).height() >= $(document).height() - 100) {
			if (!loading && hasMoreProducts) {
				page++;
				loadMoreData(page);
			}
		}
	});

	function loadMoreData(page) {
		loading = true;
		$.ajax({
			url: "http://localhost:8080/TM/sean/productMore",
			type: "GET",
			data: { page: page },
			success: function(response, textStatus, xhr) {
				if (response.products.length > 0) {
					var products = response.products;
					var memberLevel = response.memberLevel;
					console.log("當前會員等級: " + memberLevel);
					var html = '';

					for (var i = 0; i < products.length; i++) {
						var product = products[i];
						var productStartDate = formatDate(product.productStartDate);
						var productEndDate = formatDate(product.productEndDate);

						if (memberLevel === null || memberLevel === "normal_user") {
							html += '<div class="row gx-4 gx-lg-5 align-items-center">' +
								'<div class="col-md-6">' +
								'<img class="card-img-top mb-5 mb-md-0 product-image" src="/TM/sean/getImage/' + product.productId + '" alt="未上傳照片.." />' +
								'</div>' +
								'<div class="col-md-6">' +
								'<h1 class="display-5 fw-bolder">' + product.productName + '</h1>' +
								'<div class="fs-5">' +
								'行程編號: ' + product.productId +
								'</div>' +
								'<div class="fs-5">' +
								'<span class="text-decoration">行程價格: $' + product.productPrice + '</span>' +
								'</div>' +
								'<div class="fs-5">' +
								'<span class="text-decoration">行程名額: ' + product.productQuantity + '</span>' +
								'</div>' +
								'<div class="fs-5">' +
								'<span class="text-decoration">當前報名人數: ' + product.productRegistrations + '</span>' +
								'</div>' +
								'<div class="fs-5">' +
								'<span class="text-decoration">行程類別: ' + product.productType + '</span>' +
								'</div>' +
								'<div class="fs-5">' +
								'<span class="text-decoration">行程日期: ' + productStartDate + ' 至 ' + productEndDate + '</span>' +
								'</div>' +
								'<div class="fs-5">' +
								'<span class="text-decoration">行程簡介: ' + product.productDescription + '</span>' +
								'</div>' +
								'<br>' +
								'<div class="d-flex">' +
								'<form action="http://localhost:8080/TM/sean/addToCart" method="post" class="addToCartForm">' +
								'<input type="hidden" name="productId" value="' + product.productId + '" />' +
								'<input type="hidden" name="productType" value="' + product.productType + '" />' +
								'<input type="hidden" name="productName" value="' + product.productName + '" />' +
								'<input type="hidden" name="productPrice" value="' + product.productPrice + '" />' +
								'<input type="hidden" name="productRegistrations" value="1" />' +
								'<input type="hidden" name="productQuantity" value="' + product.productQuantity + '" />' +
								'<input type="hidden" name="productDescription" value="' + product.productDescription + '" />' +
								'<input type="hidden" name="productStartDate" value="' + productStartDate + '" />' +
								'<input type="hidden" name="productEndDate" value="' + productEndDate + '" />' +
								'<input type="hidden" name="productStatus" value="' + product.productStatus + '" />' +
								'<button type="submit" class="btn btn-outline-dark flex-shrink-0 add-to-cart-btn">' +
								'<i class="bi-cart-fill me-1"></i>加入購物車' +
								'</button>' +
								'</form>&nbsp;' +
								'<div id="message"></div>' +
								'</div>' +
								'<br>' +
								'</div>' +
								'<div style="height: 50px;"></div>' +
								'<hr>' +
								'<div style="height: 20px;"></div>' +
								'</div>';
						} else {
							html += '<div class="row gx-4 gx-lg-5 align-items-center">' +
								'<div class="col-md-6">' +
								'<img class="card-img-top mb-5 mb-md-0 product-image" src="/TM/sean/getImage/' + product.productId + '" alt="未上傳照片.."/>' +
								'</div>' +
								'<div class="col-md-6">' +
								'<div class="status" id="statusText-' + product.productId + '">' + product.productStatus + '中</div>' +
								'<form id="toggleForm" action="/TM/sean/toggleStatus/' + product.productId + '" method="post">' +
								'<button class="toggle-button" type="submit" onclick="confirmToggle(event)">' +
								'<i id="toggleIcon-' + product.productId + '" class="' + (product.productStatus == '上架' ? 'fa-solid fa-toggle-on' : 'fa-solid fa-toggle-off') + '"></i>' +
								'</button>' +
								'</form>' +
								'<h1 class="display-5 fw-bolder">' + product.productName + '</h1>' +
								'<div class="fs-5">' +
								'行程編號: <span>' + product.productId + '</span>' +
								'</div>' +
								'<div class="fs-5">' +
								'<span class="text-decoration">行程價格 : $' + product.productPrice + '</span>' +
								'</div>' +
								'<div class="fs-5">' +
								'<span class="text-decoration">行程名額 : ' + product.productQuantity + '</span>' +
								'</div>' +
								'<div class="fs-5">' +
								'<span class="text-decoration">當前報名人數 : ' + product.productRegistrations + '</span>' +
								'</div>' +
								'<div class="fs-5">' +
								'<span class="text-decoration">行程類別 : ' + product.productType + '</span>' +
								'</div>' +
								'<div class="fs-5">' +
								'<span class="text-decoration">行程狀態 : ' + product.productStatus + '</span>' +
								'</div>' +
								'<div class="fs-5">' +
								'<span class="text-decoration">行程日期 : ' + productStartDate + ' 至 ' + productEndDate + '</span>' +
								'</div>' +
								'<div class="fs-5">' +
								'<span class="text-decoration">行程簡介 : ' + product.productDescription + '</span>' +
								'</div>' +
								'<br>' +
								'<div class="d-flex">' +
								'<form action="/sean/addToCart" method="post" class="addToCartForm">' +
								'<input type="hidden" name="productId" value="' + product.productId + '" />' +
								'<input type="hidden" name="productType" value="' + product.productType + '" />' +
								'<input type="hidden" name="productName" value="' + product.productName + '" />' +
								'<input type="hidden" name="productPrice" value="' + product.productPrice + '" />' +
								'<input type="hidden" name="productRegistrations" value="1" />' +
								'<input type="hidden" name="productQuantity" value="' + product.productQuantity + '" />' +
								'<input type="hidden" name="productDescription" value="' + product.productDescription + '" />' +
								'<input type="hidden" name="productStartDate" value="' + productStartDate + '" />' +
								'<input type="hidden" name="productEndDate" value="' + productEndDate + '" />' +
								'<input type="hidden" name="productStatus" value="' + product.productStatus + '" />' +
								'<button type="submit" class="btn btn-outline-dark flex-shrink-0 add-to-cart-btn">' +
								'<i class="bi-cart-fill me-1"></i>加入購物車' +
								'</button>' +
								'</form>&nbsp;' +
								'<div id="message"></div>' +
								'<button class="btn btn-outline-dark flex-shrink-0 edit-btn" onclick="editProduct(' + product.productId + ')">' +
								'<i class="bi-pencil-fill me-1"></i>編輯' +
								'</button>' +
								'</div>' +
								'<br>' +
								'<div class="edit-form" id="editForm-' + product.productId + '">' +
								'<form method="post" action="/TM/sean/editProduct" enctype="multipart/form-data">' +
								'<input type="hidden" name="productId" value="' + product.productId + '">' +
								'<div class="mb-3">' +
								'<label for="productName" class="form-label"> <i class="fa-solid fa-file-signature"></i> 行程名稱:</label>' +
								'<input type="text" class="form-control" id="productName" name="productName" value="' + product.productName + '">' +
								'</div>' +
								'<div class="mb-3">' +
								'<label for="productPrice" class="form-label"> <i class="fa-solid fa-dollar-sign"></i> 行程售價:</label>' +
								'<input type="number" class="form-control" id="productPrice" name="productPrice" value="' + product.productPrice + '">' +
								'</div>' +
								'<div class="mb-3">' +
								'<label for="productQuantity" class="form-label"> <i class="fa-solid fa-person-walking"></i> 行程名額:</label>' +
								'<input type="number" class="form-control" id="productQuantity" name="productQuantity" value="' + product.productQuantity + '">' +
								'</div>' +
								'<div class="mb-3">' +
								'<label for="productType" class="form-label"><i class="fa-regular fa-font-awesome"></i> 行程類別:</label>' +
								'<select class="form-select" id="productType" value="' + product.productType + '" name="productType" required>' +
								'<option value="國內"' + (product.productType === '國內' ? ' selected' : '') + '>國內</option>' +
								'<option value="國外"' + (product.productType === '國外' ? ' selected' : '') + '>國外</option>' +
								'</select>' +
								'</div>' +
								'<div class="mb-3">' +
								'<label for="productStartDate" class="form-label"> <i class="fa-regular fa-clock"></i> 啟程日期:</label>' +
								'<input type="date" class="form-control" id="productStartDate" name="productStartDate" value="' + productStartDate + '">' +
								'</div>' +
								'<div class="mb-3">' +
								'<label for="productEndDate" class="form-label"> <i class="fa-solid fa-clock"></i> 返程日期:</label>' +
								'<input type="date" class="form-control" id="productEndDate" name="productEndDate" value="' + productEndDate + '">' +
								'</div>' +
								'<div class="mb-3">' +
								'<label for="productDescription" class="form-label"> <i class="fa-solid fa-pen"></i> 行程簡介:</label>' +
								'<textarea class="form-control" id="productDescription" name="productDescription" rows="3">' + product.productDescription + '</textarea>' +
								'</div>' +
								'<div class="mb-3">' +
								'<label for="productImage" class="form-label"> <i class="fa-solid fa-file-image"></i> 更換行程圖片:</label>' +
								'<input class="form-control" type="file" id="productImage" name="productImage">' +
								'</div>' +
								'<button type="submit" class="btn btn-primary">Save</button>&nbsp;' +
								'<button type="button" class="btn btn-secondary" onclick="cancelEdit(' + product.productId + ')">Cancel</button>' +
								'</form>' +
								'</div>' +
								'</div>' +
								'<div style="height: 50px;"></div>' +
								'<hr>' +
								'<div style="height: 20px;"></div>';
						}
					}

					var container = $("#productContainer");
					var existingProducts = container.children();
					container.append(html);
					initializeDatePickers(); // 重新初始化日期選擇器

					var newProducts = container.children().not(existingProducts);
					newProducts.css('opacity', 0); // 將新商品元素的透明度設為 0

					setTimeout(function() {
						newProducts.addClass('fade-in').css('opacity', 1); // 為新商品元素添加類名，並設定透明度為 1，以套用淡入特效
					}, 10);

					hasMoreProducts = response.hasMoreProducts;

					//總商品數量
					var totalProducts = parseInt(xhr.getResponseHeader('totalProducts'));
					//已載入商品數量
					var loadedProducts = response.loadedProducts;

					if (hasMoreProducts) {
						console.log("totalProducts數量: " + totalProducts);
						console.log("loadedProducts數量: " + loadedProducts);
						console.log("hasMoreProducts狀態: " + hasMoreProducts);
					} else {
						$("#loadMoreContainer").hide();
						console.log("totalProducts數量: " + totalProducts);
						console.log("loadedProducts數量: " + loadedProducts);
						console.log("hasMoreProducts狀態: " + hasMoreProducts);
					}
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				console.log("Error: " + textStatus);
			},
			complete: function() {
				loading = false;
			}
		});
	}
});

//編輯
function editProduct(productId) {
	document.getElementById("editForm-" + productId).classList.add("show");
}

//取消編輯
function cancelEdit(productId) {
	document.getElementById("editForm-" + productId).classList.remove("show");
}

// 商品頁-新增資料日期判定
function initializeDatePickers() {
	var startDatePicker = flatpickr("#productStartDate", {
		enableTime: false,
		dateFormat: "Y-m-d",
		minDate: "today",
		required: true,
		onClose: function(selectedDates, dateStr, instance) {
			if (!dateStr) {
				instance.redraw(); // 重新绘制日期选择器，触发验证
			}
		},
		onChange: function(selectedDates, dateStr, instance) {
			var minDate = new Date(selectedDates[0].getTime());
			if (endDatePicker) {
				endDatePicker.set("minDate", minDate);
			}
		}
	});

	var endDatePicker;

	flatpickr("#productEndDate", {
		enableTime: false,
		dateFormat: "Y-m-d",
		minDate: "today",
		required: true,
		onReady: function(selectedDates, dateStr, instance) {
			endDatePicker = instance;
		}
	});
}

document.addEventListener("DOMContentLoaded", function() {
	initializeDatePickers();
});

//商品頁面顯示-日期格式化
function formatDate(dateString) {
	var date = new Date(dateString);
	var year = date.getFullYear();
	var month = ("0" + (date.getMonth() + 1)).slice(-2); // add leading zero
	var day = ("0" + date.getDate()).slice(-2); // add leading zero
	return year + "-" + month + "-" + day;
}

//模糊查詢完畢-刪除滾動式查詢
$('#searchForm').on('submit', function(event) {
	event.preventDefault();

	var form = $(this);
	var action = form.attr('action');
	var data = form.serialize();

	data += '&removeDiv=true';

	// 將 'removeDiv=true' 加入網址
	window.location.href = action + '?' + data;
});

$(document).ready(function() {
	var urlParams = new URLSearchParams(window.location.search);
	if (urlParams.has('removeDiv')) {
		$('#loadMoreContainer').remove();
	}
});

//新增商品-一鍵輸入
document.querySelector('.btn.btn-primary.OneTap').addEventListener('click', function() {
	event.preventDefault(); // Prevent the form from being submitted
	document.querySelector('.productName').value = "-這是預設行程名稱-";
	document.querySelector('.productPrice').value = 100;
	document.querySelector('.productQuantity').value = 10;
	document.querySelector('.productType').value = "國外";
	document.querySelector('.flatpickr[name="productStartDate"]').value = "2023-07-21";
	document.querySelector('.flatpickr[name="productEndDate"]').value = "2023-07-28";
	document.querySelector('.productDescription').value = "-這是預設的行程簡介-";
	document.querySelector('.productStatus').value = "上架";
});


