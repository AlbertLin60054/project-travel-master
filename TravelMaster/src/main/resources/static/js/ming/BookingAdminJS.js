
$(document).ready(function() {
	$('#queryResult').hide();
});

//var basePath = window.location.protocol + "//" + window.location.hostname + ":" + location.port + '/TM';

function show() {
	let table;
	let allTicketInfoDataSource = "services/GetAllTicketInfo";

	if ($.fn.dataTable.isDataTable('#queryResult')) {
		table = $('#queryResult').DataTable();
	} else {
		table = $('#queryResult').DataTable({
			ajax: allTicketInfoDataSource
		});
	}
	table.ajax.reload();
	$('#queryResult').show();
}

function insertRecord() {
	location.href = `highSpeedRail/insert`;

}

function updateTarget(id) {
	location.href = `highSpeedRail/update?id=${id}`;
}

function deleteTarget(deleteTarget) {
	Swal.fire({
		title: '是否刪除?',
		icon: 'warning',
		showCancelButton: true,
		cancelButtonText: '取消',
		confirmButtonText: '確定',
		preConfirm: async () => {
			console.log("id=" + deleteTarget);
			try {
				const response = await fetch(`services/DeleteTicketInfo?id=${deleteTarget}`);
				if (!response.ok) {
					throw new Error(response.statusText);
				}
				return await response.json();
			} catch (error) {
				Swal.showValidationMessage(
					`Request failed: ${error}`
				);
			}
		},
		allowOutsideClick: () => !Swal.isLoading()
	}).then((result) => {
		if (result.isConfirmed) {
			let resultObj = result.value;
			if (resultObj.result) {
				Swal.fire(
					'刪除成功',
					`紀錄刪除成功(id="${resultObj.id}")`,
					'success'
				);
				document.querySelector("#search").click();
			} else {
				Swal.fire(
					'刪除失敗',
					`紀錄刪除失敗(id="${resultObj.id}")`,
					'error'
				);
			}
		}
	});
}