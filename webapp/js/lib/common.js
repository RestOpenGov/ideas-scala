
var lastHeight = 0;
var lastWidth = 0;

function resize() {

	var width = $(window).width();	
	var height = $(window).height();	

	if((width == lastWidth && lastHeight == height) || $('.well.idea-container').length == 0) {
		return;
	} else {
		lastWidth = width;
		lastHeight = height;
	}

	$('.well.idea-container').each(function() { 

		$('.userinfo .box', $(this)).css('paddingTop', 0);
		
		var containerHeight = $(this).height();
		var userinfoheight  = $('.userinfo .box', $(this)).height();
		var padding = Math.ceil((containerHeight - userinfoheight) / 2);

		if(width <= 767) {
			containerHeight = 'auto';
			padding = 0;
		}

		$('.userinfo', $(this)).css('height', containerHeight);
		$('.userinfo .box', $(this)).css('marginTop', padding);
		
	});
}

$(window).bind('resize', resize);

setInterval(resize, 500);

$(function() {
	resize();
});