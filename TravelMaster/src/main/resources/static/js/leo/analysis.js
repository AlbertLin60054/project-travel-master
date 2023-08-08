 (function() {
 const ctx = document.getElementById('sexChart');
   
	   const maleCount = document.getElementById('maleCount').textContent;
	   const femaleCount = document.getElementById('femaleCount').textContent;
	
		 new Chart(ctx, {
		   type: 'doughnut',
		   data: {
		     labels: ['Male', 'Female'],
		     datasets: [{
		       label: '# of Votes',
		       data: [maleCount, femaleCount],
		       borderWidth: 1
		     }]
		   },
		   options: {
		   responsive: true,
		   plugins: {
		     legend: {
		       position: 'top',
		     },
		     title: {
		       display: true,
		       text: '男女比例圖'
		     }
		   }
		 },
		 });

	   const ageCtx = document.getElementById('ageChart');

	   const ageGroup1 = document.getElementById('ageGroup1').textContent;
	   const ageGroup2 = document.getElementById('ageGroup2').textContent;
	   const ageGroup3 = document.getElementById('ageGroup3').textContent;
	   const ageGroup4 = document.getElementById('ageGroup4').textContent;
	   const ageGroup5 = document.getElementById('ageGroup5').textContent;
	   const ageGroup6 = document.getElementById('ageGroup6').textContent;

	   new Chart(ageCtx, {
		   type: 'bar',
		   data: {
		     labels: ['18-20', '21-25', '26-30', '31-40', '41-50', '51+'],
		     datasets: [{
		       label: '年齡分布',
		       data: [ageGroup1, ageGroup2, ageGroup3, ageGroup4, ageGroup5, ageGroup6],
		       borderWidth: 1
		     }]
		   },
		   options: {
		     responsive: true,
		     plugins: {
		       legend: {
		         position: 'top',
		       },
		       title: {
		         display: true,
		         text: '年齡分布圖'
		       }
		     }
		   },
	   });
	   })();
