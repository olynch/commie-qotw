$(document).ready(function() {
	$.ajax({
		'url': '/html/nav.html',
		'dataType': 'text',
		'success': function(server_response) {
			$('body').prepend(server_response);
		}
	});
});

function addZero(number) {
	if (number < 10) return '0' + String(number);
	else return String(number);
}
function formatDate(date) {
	return ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'][date.getDay()] + ' ' + ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'][date.getMonth()] + ' ' + String(date.getDate()) + ', ' + String(date.getFullYear()) + ' at ' + String(date.getHours()) + ':' + addZero(date.getMinutes());
}