// 購物車動態抓取資訊
const productQuantityFields = document.querySelectorAll('.quantity-field.product-quantity-field');
const playoneQuantityFields = document.querySelectorAll('.quantity-field.playone-quantity-field');
const ticketQuantityFields = document.querySelectorAll('.quantity-field.ticket-quantity-field');
const totalAmountElement = document.querySelector('#basket-total');
const totalItemsElement = document.querySelector('.total-items');

// 更新購物車總金額和總數量的函數
function updateCartTotal() {
	let totalAmount = 0;
	let totalItems = 0;

	productQuantityFields.forEach((quantityField) => {
		// 商品的數量處理
		const quantity = parseInt(quantityField.value);
		const priceElement = quantityField.parentNode.querySelector('[name="productPrice"]');
		const price = parseFloat(priceElement.value);

		const subtotal = quantity * price;

		// 更新對應的小計元素
		const subtotalElement = quantityField.parentNode.parentNode.querySelector('.subtotal');
		subtotalElement.textContent = subtotal;

		totalAmount += subtotal;
		totalItems += quantity;
	});

	playoneQuantityFields.forEach((quantityField) => {
		// 陪玩的數量處理
		const quantity = parseInt(quantityField.value);
		const price = 3000; // 假設陪玩的價格固定為 3000

		const subtotal = quantity * price;

		// 更新對應的小計元素
		const subtotalElement = quantityField.parentNode.parentNode.querySelector('.subtotal');
		subtotalElement.textContent = subtotal;

		totalAmount += subtotal;
		totalItems += quantity;
	});

	ticketQuantityFields.forEach((quantityField) => {
		// 票的數量處理
		const quantity = parseInt(quantityField.value);
		const priceElement = quantityField.parentNode.querySelector('[name="ticketPrice"]');
		const price = parseFloat(priceElement.value);

		const subtotal = quantity * price;

		// 更新對應的小計元素
		const subtotalElement = quantityField.parentNode.parentNode.querySelector('.subtotal');
		subtotalElement.textContent = subtotal;

		totalAmount += subtotal;
		totalItems += quantity;
	});

	totalAmountElement.textContent = totalAmount;
	totalItemsElement.textContent = totalItems;
}

// 初始化購物車總金額
updateCartTotal();

// 監聽數量輸入框變化事件（事件代理）
const cartItems = document.querySelector('.cart-items');
cartItems.addEventListener('input', (event) => {
	if (event.target.classList.contains('quantity-field')) {
		updateCartTotal();
	}
});



//輸入優惠碼
var promoCodeInput = document.getElementById('promo-code');

promoCodeInput.addEventListener('input', function() {
	if (promoCodeInput.value !== '') {
		promoCodeInput.placeholder = '';
	}
});

promoCodeInput.addEventListener('blur', function() {
	if (promoCodeInput.value === '') {
		promoCodeInput.placeholder = '輸入優惠碼:';
	}
});


//動態修改商品數量(判斷不能大於庫存)-傳後端
$(".quantity-field.product-quantity-field").change(function() {
	console.log($(this).val());
	let val = parseInt($(this).val());
	let productId = $(this).closest(".basket-product").find(".productId").val();
	let max = parseInt($(this).siblings(".productQuantity").val());

	if (val <= max) {
		$.ajax({
			url: "http://localhost:8080/TM/sean/updateRegistrations",
			type: "put",
			data: {
				"productId": productId,
				"productRegistrations": $(this).val()
			},
			success: function() {
				console.log("成功修改");
			},
			error: function() {
				console.log('失敗');
			}
		});
	} else {
		// 如果输入的值超过了最大值，就将其设置为最大值
		$(this).val(max);
		console.log("输入的数量超过了最大库存");
	}
});

//動態修改旅伴天數-傳後端
$(".quantity-field.playone-quantity-field").change(function() {
	console.log($(this).val());
	let val = parseInt($(this).val());
	let playoneId = $(this).closest(".basket-product").find(".playoneId").val();

	$.ajax({
		url: "http://localhost:8080/TM/sean/playOneDays",
		type: "put",
		data: {
			"playoneId": playoneId,
			"playoneDays": $(this).val()
		},
		success: function() {
			console.log("成功修改");
		},
		error: function() {
			console.log('失敗');
		}
	});
});



//購物車為空前往商品頁的按鈕
function goToProductPage() {
	window.location.href = 'http://localhost:8080/TM/sean/productPageByCount';
}

//結帳通知
function checkout() {
	// 發起結帳請求
	$.ajax({
		url: 'http://localhost:8080/TM/sean/checkOut',
		type: 'POST',
		dataType: 'json'
	})
		.done(function(response) {
			if (response.message === 'Checkout completed successfully.') {
				// 結帳成功
				Swal.fire({
					title: 'Success',
					text: '下單成功!導向結帳頁面:)',
					icon: 'success'
				}).then(() => {
					window.location.href = 'http://localhost:8080/TM/sean/getPersonalOrder';
				});
			} else if (response.message === 'Cart is empty.') {
				Swal.fire('Warning', '購物車空空的喔，去看看行程吧!', 'warning').then(() => {
					window.location.href = 'http://localhost:8080/TM/sean/productPageByCount';
				});
			} else {
				Swal.fire('Error', 'Unexpected error. Please try again.', 'error');
			}
		});
}

// 行程-防止亂輸入報名人數
$(".quantity-field.product-quantity-field").keydown(function(e) {
	e.preventDefault();
});

