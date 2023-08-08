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
	}
	// 
});


function showPrice() {
	let departureST = document.querySelector("#DepartureST");
	let destinationST = document.querySelector("#DestinationST");
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



