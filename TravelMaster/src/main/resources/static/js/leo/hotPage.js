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
$(document).ready(function() {
		  $('.responsive').slick({
		  	
			  dots: true,
			  infinite: false,
			  speed: 300,
			  slidesToShow: 4,
			  slidesToScroll: 4,
			  responsive: [
			    {
			      breakpoint: 1024,
			      settings: {
			        slidesToShow: 3,
			        slidesToScroll: 3,
			        infinite: true,
			        dots: true
			      }
			    },
			    {
			      breakpoint: 600,
			      settings: {
			        slidesToShow: 2,
			        slidesToScroll: 2
			      }
			    },
			    {
			      breakpoint: 480,
			      settings: {
			        slidesToShow: 1,
			        slidesToScroll: 1
			      }
			    }
			  ]
			});
				
		});
		