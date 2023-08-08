function showForm() {
		  var modal = document.getElementById("myModal");
		  modal.style.display = "block";
		}

function closeModal() {
  var modal = document.getElementById("myModal");
  var saveBtnDisplay=document.getElementById("saveBtn");
  if (saveBtnDisplay.style.display === "block") {
	  var confirmation = confirm("放棄更改？");
	   if(confirmation){
		   modal.style.display = "none";
		   window.location.href = 'tobackstage.controller';
		}else{
			showForm();
		}
    
  }else{		  
	  modal.style.display = "none";
  }
}

$(function () {
                    $(".updateBtn").click(function () {
                    	$("#membername, #membermail, #memberphone, #memberadd, #memberpwd").prop("disabled", false);
                    	$(this).css("display", "none");
                        $(".saveBtn").css("display", "block")
                      
                        
                       		 
                    });
                    
                    $(".saveBtn").click(function() { 
						var numValue = $('#membernum').val();
                    	var nameValue = $('#membername').val();
                    	var emailValue = $('#membermail').val();
                    	var phoneValue = $('#memberphone').val();
                    	var addressValue = $('#memberadd').val();
                    	var passwordValue = $('#memberpwd').val();	
                    	         	  
                    	var confirmation = confirm("確認更改？"); 	  
                    	  if(confirmation){
							  $("#membernum").prop("disabled", false);
                        	  $("#membernum").prop("readonly", true);
                        	  
							  var numValue = $('#membernum').val();
	                    	  var nameValue = $('#membername').val();
	                    	  var emailValue = $('#membermail').val();
	                    	  var phoneValue = $('#memberphone').val();
	                    	  var addressValue = $('#memberadd').val();
	                    	  var passwordValue = $('#memberpwd').val();
							  
	                    	  $('#check').val("Y")  
	                    	  $('#num1').val(numValue);
	                    	  $('#name1').val(nameValue);
	                    	  $('#email1').val(emailValue);
	                    	  $('#phone1').val(phoneValue);
	                    	  $('#address1').val(addressValue);
	                    	  $('#password1').val(passwordValue);
	                    	  alert("更新成功!!!");
                    	  }else{
                    		  $('#num1').val(numValue);
                    		  $('#check').val("N");
                    		  alert("更新失敗!!!");
                    	  }
                    	});
                    	
                    	$('#memberphoto').change(function() {
						  var file = this.files[0];
						  $("#membernum").prop("disabled", false);
						  var numValue = $('#membernum').val();
						  $("#membernum").prop("disabled", true);
						  var formData = new FormData();
						  formData.append('memberNum', numValue);
						  formData.append('file', file);
						
						  $.ajax({
						    url: 'http://localhost:8080/TM/updatepersonalphoto.controller',
						    type: 'POST',
						    data: formData,
						    processData: false,
						    contentType: false,
						    success: function(response) {
						      console.log('照片上傳成功');
						       var timestamp = new Date().getTime();
						       var newhtml="http://localhost:8080/TM/getpersonphoto.controller?memberNum="+numValue+ "&timestamp=" + timestamp;
						       $("#current-photo").attr("src", newhtml);
						       $("#memberphoto").val("");
						    },
						    error: function(error) {
						      console.log('照片上傳失敗');
						    }
						  });
						});

	})
