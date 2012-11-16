
function resize() {

	var width = $(window).width();	

	$('.well.idea-container').each(function() { 

		$('.userinfo .box', $(this)).css('paddingTop', 0);
		
		var containerHeight = $(this).height();
		var userinfoheight  = $('.userinfo .box', $(this)).height();
		var padding = Math.ceil((containerHeight - userinfoheight) / 2);

		if(width <= 767) {
			containerHeight = 'auto';
		}

		if(width < 400 && padding > 100) {
			padding = 0;
		}

		$('.userinfo', $(this)).css('height', containerHeight);
		$('.userinfo .box', $(this)).css('marginTop', padding);
		
	});
}

$(window).bind('resize', resize);

setInterval(resize, 500)

$(function() {
	resize();
});