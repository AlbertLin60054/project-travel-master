var priceInfos = new Map();
var basePath = window.location.protocol + "//" + window.location.hostname + ":" + location.port + '/TM';
let tmpS;

$(document).ready(function() {
	if (priceInfos.size == 0) {
		let priceInfosObj = JSON.parse(document.querySelector("#priceInfos").value);
		priceInfosObj.forEach((element) => {
			tmpS = new Set();
			tmpS.add(element.pk.departureST); // departureST
			tmpS.add(element.pk.destinationST); // destinationST
			priceInfos.set(tmpS, element.price);
		});
	} // (1,2) or (2,1) -> 40  

});

function showPrice() {
	let departureST = document.querySelector("#departureST");
	let destinationST = document.querySelector("#destinationST");
	let price = document.querySelector("#price");

	if (parseInt(departureST.value) == parseInt(destinationST.value)) {
		price.value = 0;
		return;
	}

	priceInfos.forEach((value, key) => {
		if (key.has(departureST.value) && key.has(destinationST.value)) {
			price.value = value;
			return;
		}
	});
}

var currPageNum = 1;
function search(pageNum = 1) {
	currPageNum = pageNum;
	let departureST = document.querySelector("#departureST");
	let destinationST = document.querySelector("#destinationST");
	let departuredate = document.querySelector("#departuredate");
	let departureTime = document.querySelector("#departureTime");
	if (parseInt(departureST.value) == parseInt(destinationST.value)) {
		Swal.fire(
			'( ｣｡╹o╹｡)｣',
			'您的 「起程站」 與 「到達站」 相同 <(ΦωΦ)>',
			'warning'
		)
		return;
	}
	if (departuredate.value == "") {
		Swal.fire(
			'( ｣｡╹o╹｡)｣',
			'請記得選擇出發日期 !',
			'warning'
		)
		return;
	}
	if (departureTime.value == "") {
		Swal.fire(
			'( ｣｡╹o╹｡)｣',
			'請記得選擇出發時刻 !',
			'warning'
		)
		return;
	}

	let queryResult = document.querySelector("#queryResult");
	let from_st = document.querySelector("#from_st");
	let to_st = document.querySelector("#to_st");
	let dep_date = document.querySelector("#dep_date");
	from_st.innerHTML = departureST.options[departureST.selectedIndex].text;
	to_st.innerHTML = destinationST.options[destinationST.selectedIndex].text;
	dep_date.innerHTML = departuredate.value;

	fetch(`services/GetTranInfo?
			departureST=${departureST.value}&
			destinationST=${destinationST.value}&
			departureTime=${departureTime.value}&
			p=${currPageNum}`, {
		method: "GET",
		headers: {
			'Content-Type': 'application/json'
		}
	}).then(response => {
		if (response.ok) {
			return response.json();
		} else {
			throw new Error("Error: " + response.status);
		}
	}).then(data => {
		//console.log(data);
		let totalPageNumber = Math.ceil(data.total / data.pageable.size);  //Math.ceil 無條件進位
		//console.log(totalPageNumber);
		placeQueryContent(data.content);
		createPageButton(totalPageNumber);
		queryResult.style.display = "";
	}).catch(error => {
		console.error(error);
	});
}

function createPageButton(totalPageNumber) {
	let paginationField = document.querySelector("#pagination");
	paginationField.innerHTML = "";
	/* bootstrap 5 pagination
	<ul class="pagination">
    <li class="page-item"><a class="page-link" href="#">Previous</a></li>*/
	let pagination_ul = document.createElement("ul");
	pagination_ul.className = "pagination";

	//上一頁
	let pre = document.createElement("li");
	pre.className = "page-item";
	let pre_a = document.createElement("a");
	pre_a.className = "page-link";
	pre_a.innerHTML = "上一頁";
	pre_a.href = "#";
	pre.appendChild(pre_a);
	if (currPageNum == 1) {
		pre.classList.add("disabled");
	}
	// pre
	pre.addEventListener("click", () => {
		if (currPageNum == 1) {
			return;
		}
		search(currPageNum - 1);
	});
	pagination_ul.appendChild(pre);

	//頁數
	for (let i = 1; i < (totalPageNumber + 1); i++) {
		let pagination_li = document.createElement("li");
		pagination_li.classList.add("page-item");
		if (i == currPageNum) {   //若為當前頁面
			pagination_li.classList.add("active");
			let pagination_span = document.createElement("span");
			pagination_span.className = "page-link";
			pagination_span.innerHTML = i;
			pagination_li.appendChild(pagination_span);
		} else {
			let pagination_a = document.createElement("a");
			pagination_a.className = "page-link";
			pagination_a.innerHTML = i;
			pagination_a.href = "#";
			pagination_a.addEventListener("click", () => {
				search(i);
			});
			pagination_li.appendChild(pagination_a);
		}
		pagination_ul.appendChild(pagination_li);
	}

	//下一頁
	let next = document.createElement("li");
	next.className = "page-item";
	let next_a = document.createElement("a");
	next_a.className = "page-link";
	next_a.href = "#";
	next_a.innerHTML = "下一頁";
	next.appendChild(next_a);
	// next
	if (currPageNum == totalPageNumber) {
		next.classList.add("disabled");
	}
	next.addEventListener("click", () => {
		if (currPageNum == totalPageNumber) {
			return;
		}
		search(currPageNum + 1);
	});
	pagination_ul.appendChild(next);

	paginationField.appendChild(pagination_ul);


}

function placeQueryContent(tranInfos) {
	let queryContent = document.querySelector("#queryContent");
	queryContent.innerHTML = '';
	//console.log(tranInfos);
	if (tranInfos.length > 0) {
		tranInfos.forEach(function(tranInfo) {
			let infoRow = document.createElement("tr");
			let tranNo = document.createElement("td");
			tranNo.innerHTML = tranInfo.tranNo;
			tranNo.classList.add("fs-5");
			let dep_time = document.createElement("td");
			dep_time.innerHTML = tranInfo.departureTime;
			dep_time.classList.add("fs-5");
			let arr_time = document.createElement("td");
			arr_time.innerHTML = tranInfo.arrivalTime;
			arr_time.classList.add("fs-5");
			let diff_time = document.createElement("td");
			let arrTime = arr_time.innerHTML.split(":");
			arrTime = parseInt(arrTime[0]) * 60 + parseInt(arrTime[1]);
			//console.log(arrTime);
			let depTime = dep_time.innerHTML.split(":");
			depTime = parseInt(depTime[0]) * 60 + parseInt(depTime[1]);
			let timeDiff = Math.abs(depTime - arrTime);
			diff_time.innerHTML = parseInt(timeDiff / 60) + "&nbsp;小時&nbsp;" + timeDiff % 60 + "&nbsp;分";
			diff_time.classList.add("fs-5");
			//diff_time.innerHTML = arr_time.value - dep_time.value
			//diff_time.innerHTML = Math.abs(arrTime.toDateString() - depTime.toDateString());

			let booking = document.createElement("td");
			let booking_btn = document.createElement("button");
			booking_btn.type = "button";
			booking_btn.innerHTML = "訂票";

			booking.appendChild(booking_btn);
			booking_btn.classList.add("btn");
			booking_btn.classList.add("btn-secondary");
			
			if (isMemberLoggedIn()) {
				booking_btn.onclick = function() { choose(tranInfo) };
			} else {
				booking_btn.disabled = true;
				showSweetAlert();
			}

			infoRow.appendChild(tranNo);
			infoRow.appendChild(dep_time);
			infoRow.appendChild(arr_time);
			infoRow.appendChild(diff_time);
			infoRow.appendChild(booking);

			queryContent.appendChild(infoRow);
		});
	} else {
		queryContent.innerHTML = '<td colspan="5" class="align-center justify-content-center text-center"><i>無結果</i></td>';
	}
}

function showSweetAlert() {
    Swal.fire({
        title: '( ｣｡╹o╹｡)｣',
        text: '登入會員後才可以進行訂票唷 !',
        icon: 'warning',
        confirmButtonText: '確定'
    });
}

function isMemberLoggedIn(){
	// 在這裡判斷會員是否已登入，回傳結果（true代表已登入，false代表未登入） [[${session.mbsession.memberName}]]
	// 未登入應顯示showSweetAlert()
}

function choose(tranInfo) {
	console.log(tranInfo);
	let parmLst = "id,tranNo,arrivalTime,departureST,departureTime,destinationST".split(",");
	let submitForm = document.createElement("form")
	submitForm.method = "POST";
	submitForm.action = `choose`;
	parmLst.forEach((parm) => {
		var tmpInput = document.createElement("input");
		tmpInput.setAttribute("name", parm);
		tmpInput.setAttribute("value", tranInfo[parm]);
		submitForm.appendChild(tmpInput);
	});
	var tmpInput = document.createElement("input");
	tmpInput.setAttribute("name", "departureDate");
	tmpInput.setAttribute("value", document.querySelector("#departuredate").value);
	submitForm.appendChild(tmpInput);
	
	
	document.body.appendChild(submitForm);
	submitForm.submit();
}

