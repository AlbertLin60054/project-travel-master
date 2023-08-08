// 當文檔完全加載後執行JavaScript代碼
$(document).ready(function () {
  // 監聽陪玩暱稱輸入框的失焦事件
  $(".checknick").blur(function () {
    var playoneNick = $(this).val();
    if (playoneNick.trim() === "") {
      // 檢查暱稱是否為空
      // 如果暱稱為空，顯示錯誤提示並禁用提交按鈕
      $("#nickNameStatus").html(
        '<span class="text-danger" style="color:red;">請輸入暱稱</span>'
      );
      $('input[type="submit"]').attr("disabled", true);
      return; // 直接返回，不發送AJAX請求
    }

    // 使用AJAX向指定的URL發送POST請求
    $.ajax({
      url: "/TM/CNController",
      method: "POST",
      data: {
        playoneNick: playoneNick,
      },
      success: function (data) {
        if (data != "0") {
          // 如果暱稱已存在，顯示錯誤提示並禁用提交按鈕
          $("#nickNameStatus").html(
            '<span class="text-danger" style="color:red;">暱稱已存在</span>'
          );
          $('input[type="submit"]').attr("disabled", true);
        } else {
          // 如果暱稱可用，顯示成功提示並啟用提交按鈕
          $("#nickNameStatus").html(
            '<span class="text-success" style="color:green;">暱稱可用</span>'
          );
          $('input[type="submit"]').attr("disabled", false);
        }
      },
    });
  });
  
  var deletedImageIndices = [];

  //新的函数，使用 Promise 和 FileReader 读取文件
  function readFileAsDataURL(file) {
    return new Promise((resolve, reject) => {
      let reader = new FileReader();

      reader.onload = (e) => {
        resolve(e.target.result);
      };

      reader.onerror = (e) => {
        reject(e.target.error);
      };

      reader.readAsDataURL(file);
    });
  }
  
  
  //宣告一個新的陣列selectedFiles，用於存儲上傳的文件對象。
  var selectedFiles = [];

  $("#playonePhoto").change(async function (e) {
    var files = e.target.files; //從事件對象e中取得上傳的文件列表。
    for (var i = 0; i < files.length; i++) {
      //使用for迴圈遍歷所有上傳的文件。
      var file = files[i];
      var imageType = /image.*/; //檢查文件的類型是否為圖像類型。
      if (!file.type.match(imageType)) continue; //如果不是，則跳過該文件並繼續處理下一個文件。


		//使用新的函数读取文件，这个函数会返回一个 Promise
      let fileContent = await readFileAsDataURL(file);
      //检查文件是否已经在 deletedImageIndices 数组中
      let index = deletedImageIndices.indexOf(fileContent);
      //如果在，那么将它从 deletedImageIndices 数组中删除
      if (index > -1) {
        deletedImageIndices.splice(index, 1);
        document.getElementById("deletedImages").value = deletedImageIndices.join(",");
        
      }
      
      //建立一個新的img元素並設定其類別為preview-img，並將文件對象設定為其文件屬性。
      var img = document.createElement("img");
      img.className = "preview-img";
      img.file = file;

      //建立一個刪除按鈕，並將文件、圖像和索引作為參數傳遞給createDeleteButton函數。
      var deleteBtn = createDeleteButton(file, img, i);
      //建立一個新的div元素作為圖像容器，並添加圖像和刪除按鈕元素。
      var imgContainer = document.createElement("div");
      imgContainer.className = "img-container";
      imgContainer.appendChild(img);
      imgContainer.appendChild(deleteBtn);
      //將圖像容器添加到預覽區域，並將文件對象添加到selectedFiles陣列。
      document.getElementById("preview").appendChild(imgContainer);
      selectedFiles.push({ file: file, deleted: false });

      //建立一個新的FileReader對象來讀取文件的內容，並設定當讀取操作完成後要執行的函數。這個函數將文件的內容設定為圖像元素的來源。
      var reader2 = new FileReader();
      reader2.onload = (function (aImg) {
        return function (e) {
          aImg.src = e.target.result;
        };
      })(img);
      reader2.readAsDataURL(file);
    }
  });
  //在每次刪除圖片時，將被刪除的圖片存入deletedImageIndices陣列中，這個陣列將在提交表單前使用。
  var deletedImageIndices = [];

  //函数用于创建一个删除按钮。该函数接受三个参数：file（文件对象）、img（图片对象）和 index（索引值）。
  function createDeleteButton(file, img, index) {
    var deleteBtn = document.createElement("span");
    deleteBtn.className = "delete-btn";
    deleteBtn.innerText = "X";

    deleteBtn.addEventListener("click", async function (e) {
      var imgContainers = document.getElementById("preview").children;
      var imgContainer = e.target.parentNode;
      var index = Array.from(imgContainers).indexOf(imgContainer);

      if (index > -1) {
        // 使用新的函数读取文件，这个函数会返回一个 Promise
        let fileContent = await readFileAsDataURL(selectedFiles[index].file);

        deletedImageIndices.push(fileContent);
        document.getElementById("deletedImages").value = deletedImageIndices.join(",");
        console.log(deletedImageIndices);

        selectedFiles.splice(index, 1);
        e.target.parentNode.remove();
      }
    });

    return deleteBtn;
  }
  $("#subbb").submit(function (e) {
    if (selectedFiles.length === 0) {
      // selectedFiles 是你在上一個JavaScript代碼中用於存儲上傳的照片的變量
      e.preventDefault();
      alert("請至少上傳一張照片");
    }
  });
});
