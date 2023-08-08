//結帳
function payMoney(button) {
	let orderId = $(button).data('order-id');
	console.log("訂單編號: " + orderId);

	// 馬上關閉按鈕
	$(button).prop('disabled', true);

	$.ajax({
		url: 'http://localhost:8080/TM/sean/payMoney',
		type: 'POST',
		contentType: 'application/json',
		data: JSON.stringify({
			'orderId': orderId
		}),
		success: function(response) {
			let paymentUrl = response.paymentUrl;
			console.log(paymentUrl);

			// 重導付款網址
			window.location.href = paymentUrl;
		},
		error: function(error) {
			console.error(error);

			// 關閉按鈕
			$(button).prop('disabled', false);
		}
	});
}

// 申請取消按鈕:
function cancelOrder(button) {
	var orderId = button.getAttribute('data-order-id');
	console.log(orderId);
	Swal.fire({
		title: '你確定要取消這筆訂單嗎?',
		icon: 'warning',
		showCancelButton: true,
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
		confirmButtonText: '確定',
		cancelButtonText: '取消'
	}).then((result) => {
		if (result.isConfirmed) {
			$.ajax({
				url: 'http://localhost:8080/TM/sean/deleteOrder/' + orderId,
				type: 'DELETE',
				success: function(response) {
					if (response === '付款成功') {
						Swal.fire({
							title: '取消成功!',
							text: '你的訂單已被取消，款項將於3-7個工作天退款至原匯款帳戶。',
							icon: 'success',
							confirmButtonText: 'OK'
						}).then(() => {
							window.location.href = 'http://localhost:8080/TM/sean/getPersonalOrder';
						});
					} else {
						Swal.fire({
							title: '取消成功!',
							text: '你的訂單已被取消。',
							icon: 'success',
							confirmButtonText: 'OK'
						}).then(() => {
							window.location.href = 'http://localhost:8080/TM/sean/getPersonalOrder';
						});
					}
				},
				error: function(xhr, status, error) {
					Swal.fire(
						'取消失敗!',
						'取消訂單時發生錯誤: ' + error,
						'error'
					);
				}
			});
		}
	});
}
