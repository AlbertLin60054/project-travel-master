//<script th:src="@{/js/yu/Spot.js}"></script>
  document.getElementById("exportBtn").addEventListener("click", function() {
    exportTableToJSON();
    exportTableToCSV();
  });

  function exportTableToJSON() {
    var spots = [];
    var rows = document.querySelectorAll("#queryResult tbody tr");
    for (var i = 0; i < rows.length; i++) {
      var spot = {
        spotNo: rows[i].cells[0].textContent,
        spotName: rows[i].cells[1].textContent,
        cityRegion: rows[i].cells[2].textContent,
        cityName: rows[i].cells[3].textContent,
        spotType: rows[i].cells[4].textContent,
        spotInfo: rows[i].cells[5].textContent,
        spotPic: rows[i].querySelector("img").getAttribute("src"),
        clickCount: rows[i].cells[7].textContent
      };
      spots.push(spot);
    }

    var jsonContent = JSON.stringify(spots, null, 2);
    downloadFile(jsonContent, "spots.json", "application/json");
  }

  function exportTableToCSV() {
    var csvContent = "data:text/csv;charset=utf-8,";
    var rows = document.querySelectorAll("#queryResult tbody tr");
    for (var i = 0; i < rows.length; i++) {
      var rowData = [];
      for (var j = 0; j < rows[i].cells.length; j++) {
        rowData.push(rows[i].cells[j].textContent);
      }
      csvContent += rowData.join(",") + "\r\n";
    }

    downloadFile(csvContent, "spots.csv", "text/csv");
  }

  function downloadFile(content, filename, contentType) {
    var link = document.createElement("a");
    var blob = new Blob([content], { type: contentType });
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    link.click();
  }

