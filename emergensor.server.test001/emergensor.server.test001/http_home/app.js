function View() {

}

View.location = null; // {lat: , lon: }
View.connect = function() {

};

var view = new View();

//

var lat
var lon

var alertOn = false
var alertLat
var alertLon
var alertImage

var connClosed = false
var map, infoWindow

// window.onload = function initMap () {
function initMap() {
	map = new google.maps.Map($('#map')[0], {
		center : {
			lat : -34.397,
			lng : 150.644
		},
		zoom : 17
	});
	infoWindow = new google.maps.InfoWindow;

	alertImage = new google.maps.MarkerImage(
			"http://chart.apis.google.com/chart?chst=d_map_xpin_letter&chld=pin_star|A|FF0000|000000|",
			new google.maps.Size(21, 34), new google.maps.Point(0, 0),
			new google.maps.Point(10, 34));

	// Try HTML5 geolocation.
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			var pos = {
				lat : position.coords.latitude,
				lng : position.coords.longitude
			};

			lat = position.coords.latitude;
			lon = position.coords.longitude;

			/*******************************************************************
			 * var marker = new google.maps.Marker({ position: pos, map: map });
			 ******************************************************************/

			infoWindow.setPosition(pos);
			infoWindow.setContent('Location found.');
			infoWindow.open(map);
			map.setCenter(pos);
		}, function() {
			handleLocationError(true, infoWindow, map.getCenter());
		});
	} else {
		// Browser doesn't support Geolocation
		handleLocationError(false, infoWindow, map.getCenter());
	}
}

function handleLocationError(browserHasGeolocation, infoWindow, pos) {
	infoWindow.setPosition(pos);
	infoWindow
			.setContent(browserHasGeolocation ? 'Error: The Geolocation service failed.'
					: 'Error: Your browser doesn\'t support geolocation.');
	infoWindow.open(map);
}

var connect;
var disconnect;

function addUpdate(update) {
	var $div = $("<div>");
	$div.text(update);
	$("#updates").append($div);
}

function setStatus(status, clazz) {
	$('#connStatus').removeClass();
	if (clazz)
		$('#connStatus').addClass(clazz);
	$('#connStatus').text(status);
}

window.onload = function() {
	// Get Form Variables for client
	var form = document.getElementById('alertForm');
	var alertMessage = document.getElementById('optionalText');
	var disconnButton = document.getElementById('disconnButton');

	var socket

	// Create websocket connection
	connect = function(e) {
		socket = new WebSocket('ws://localhost:3031/echo');
		var initmsg = "Mobile 1";
		socket.onerror = function(error) {
			console.log('ERROR:' + err);
		};

		socket.onopen = function(event) {
			setStatus('Connection to server successful!', 'open');
			socket.send(initmsg);
		};

		socket.onmessage = function(event) {
			var msg = event.data;
			addUpdate('Received Alert - ' + msg);
			if (msg.substr(0, 3) == "lat") {
				alertOn = true;
				if (alertOn) {
					var alertMarker = new google.maps.Marker({
						position : new google.maps.LatLng(alertLat, alertLon),
						map : map,
						icon : alertImage
					});
				}
				var res = msg.split(" ");
				alertLat = parseFloat(res[1]);
				alertLon = parseFloat(res[3]);
				console.log(alertLat)
				console.log(alertOn)
			}
		};
		return false;
	}

	form.onsubmit = function(e) {
		e.preventDefault();
		var msg = "Alert " + alertMessage.value + " lat: " + lat + " long: "
				+ lon;
		if (!connClosed) {
			socket.send(msg);
			addUpdate('Sent Alert - ' + msg);
			alertMessage.value = '';
		}
		return false;
	};

	disconnect = function(e) {
		socket.close();
		connClosed = true
		socket.onclose = function(event) {
			setStatus('Connection to server closed', 'closed');
		};
		return false;
	};

	connect();
};