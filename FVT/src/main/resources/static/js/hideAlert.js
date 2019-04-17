window.setTimeout(function() {
	$(".alert-info").fadeTo(900, 0).slideUp(600, function() {
		$(this).remove();
	});
}, 8000);

window.setTimeout(function() {
	$(".alert-warning").fadeTo(900, 0).slideUp(600, function() {
		$(this).remove();
	});
}, 8000);

window.setTimeout(function() {
	$(".alert-danger").fadeTo(1200, 0).slideUp(900, function() {
		$(this).remove();
	});
}, 10000);