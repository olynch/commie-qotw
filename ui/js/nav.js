$(document).ready(function() {
	$.ajax({
		'url': '/html/nav.html',
		'dataType': 'text',
		'success': function(server_response) {
			$('body').prepend(server_response);
		}
	});
});