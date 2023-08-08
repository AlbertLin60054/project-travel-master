document.querySelectorAll('a[data-playone-id]').forEach(function(link) {
	  link.addEventListener('click', function(event) {
	    var playoneId = event.currentTarget.getAttribute('data-playone-id');
	    console.log('Current playoneId: ', playoneId); 
	    var visitedPlayones = JSON.parse(sessionStorage.getItem('visitedPlayones')) || [];
	    if (!visitedPlayones.includes(playoneId)) {
	      visitedPlayones.unshift(playoneId);
	      if (visitedPlayones.length > 4) {
	        visitedPlayones.pop();
	      }
	      sessionStorage.setItem('visitedPlayones', JSON.stringify(visitedPlayones));
	    }
	    console.log('Visited playones: ', visitedPlayones); 
	  });
	});