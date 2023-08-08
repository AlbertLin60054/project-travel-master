function createBtn(content) {
	let td = document.createElement("td");
	let input_btn = document.createElement("input");
	td.appendChild(input_btn);
	input_btn.id = content;
	input_btn.name = "seat";
	input_btn.type = "checkbox";
	input_btn.classList.add("btn-check");
	input_btn.autocomplete = "off";
	input_btn.onchange = show;


	let input_btn_lbl = document.createElement("label");
	td.appendChild(input_btn_lbl);
	input_btn_lbl.classList.add("btn");
	input_btn_lbl.classList.add("btn-secondary");

	input_btn.onclick = () => {
		if (input_btn.checked) {
			input_btn_lbl.classList.remove("btn-secondary");
			input_btn_lbl.classList.add("btn-dark");

		} else {
			input_btn_lbl.classList.remove("btn-dark");
			input_btn_lbl.classList.add("btn-secondary");
		}
	};

	input_btn_lbl.setAttribute("for", content);
	input_btn_lbl.innerHTML = content;
	return td;
}

function initSeats() {
	const rowNum = 10;
	let seatChar = ['A', 'B', 'C', 'D', 'E'];
	let seats_container = document.getElementById("seats_container");
	let tr = document.createElement("tr");
	let cnt = 0;
	for (let i = 0; i < seatChar.length + 1; i++) {
		if (i == 3) {
			let sideWay = document.createElement("td");
			sideWay.innerHTML = "走道";
			sideWay.classList.add("align-middle");
			sideWay.rowSpan = rowNum;
			sideWay.style = "text-align:center; width:55px";
			tr.appendChild(sideWay);
			continue;
		}
		tr.appendChild(createBtn("01" + seatChar[cnt]));
		cnt++;
	}
	seats_container.appendChild(tr);
	for (let i = 2; i <= rowNum; i++) {
		let trTmp = document.createElement("tr");
		for (let j = 0; j < seatChar.length; j++) {
			trTmp.appendChild(createBtn(i.toString().padStart(2, '0')
				+ seatChar[j]));
		}
		seats_container.appendChild(trTmp);
	}
}
function show() {
	let selectedSeats = document.getElementsByName("seat");
	let p = document.getElementById("selectedSeats");
	p.innerHTML = "&nbsp;";
	let p_innerHTML = "";
	let selectedCnt = 0;
	for (let i = 0; i < selectedSeats.length; i++) {
		if (selectedSeats[i].checked == true) {
			selectedCnt++;
			p_innerHTML = p_innerHTML + " [ " + selectedSeats[i].id
				+ " ] ";

		}
	}
	if (selectedCnt > 0) {
		p.innerHTML = p_innerHTML; // &nbsp;
		document.querySelector("#formInputVal_selectedSeats").value = p_innerHTML;
	}
	let priceTotal = document.querySelector("#singlePrice").value * selectedCnt;
	document.querySelector("#priceTotal").innerHTML = priceTotal + "&nbsp;元";
	document.querySelector("#formInputVal_priceTotal").value = priceTotal;
}

function cancel() {
	Swal.fire({
		title: '是否取消訂票?',
		icon: 'warning',
		showCancelButton: true,
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
		confirmButtonText: '確定',
		cancelButtonText: '取消'
	}).then((result) => {
		if (result.isConfirmed) {
			Swal.fire(
				'您已取消訂票，即將返回查詢頁面',
			)
			window.setTimeout((() => location.href = `select`), 2000);
		}
	})
}

$(document).ready(function() {
	$('#submitForm').on('click', function(e) {
		e.preventDefault();
		var form = $('#bookingGoForm');
		Swal.fire({
			title: '是否加入購物車?',
			icon: 'warning',
			showCancelButton: true,
			confirmButtonColor: '#3085d6',
			cancelButtonColor: '#d33',
			confirmButtonText: '確定',
			cancelButtonText: '繼續訂票'
		}).then((result) => {
			if (result.isConfirmed) {
				Swal.fire(
					'( ｣｡╹o╹｡)｣您的訂票已加入購物車，即將導向購物車頁面',
				);
				setTimeout((() => {
					form.submit();
				}), 4000);

			} else {
				return false;
			}
		});
	});
});
