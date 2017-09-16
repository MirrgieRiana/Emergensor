function EmergensorView() {
	this.map = null;

	this.location = null; // {lat: , lon: }

	this.socket = null;

	this.alertImage = null;
	this.infoWindow = null;

	this.$map = null;
	this.$updates = null;
	this.$connStatus = null;
}

EmergensorView.prototype.connect = function() {
	var view = this;

	this.socket = new WebSocket('ws://localhost:3031/view');
	this.socket.onerror = function(error) {
		view.setStatus('Connection to server error', 'error');
		console.log('ERROR:' + error);
		view.socket = null;
	};
	this.socket.onopen = function(event) {
		view.setStatus('Connection to server successful!', 'open');
		view.socket.send("Mobile 1");
	};
	this.socket.onclose = function(event) {
		view.setStatus('Connection to server closed', 'closed');
		view.socket = null;
	};
	this.socket.onmessage = function(event) {
		view.addUpdate('Received Alert - ' + event.data);
		if (event.data.substr(0, 3) == "lat") {
			var res = event.data.split(" ");
			view.addMarker(parseFloat(res[1]), parseFloat(res[3]));
		}
	};
};
EmergensorView.prototype.disconnect = function() {
	this.socket.close();
};
EmergensorView.prototype.isConnected = function() {
	return this.socket != null;
};
EmergensorView.prototype.submit = function(alertMessage) {
	var msg = "Alert " + alertMessage + " lat: " + this.location.lat
			+ " long: " + this.location.lon;
	if (this.isConnected()) {
		this.socket.send(msg);
		this.addUpdate('Sent Alert - ' + msg);
		return true;
	}
	return false;
};
EmergensorView.prototype.addUpdate = function(update) {
	var $div = $("<div>");
	$div.text(update);
	this.$updates.append($div);
};
EmergensorView.prototype.setStatus = function(status, clazz) {
	this.$connStatus.removeClass();
	if (clazz)
		this.$connStatus.addClass(clazz);
	this.$connStatus.text(status);
};
EmergensorView.prototype.addMarker = function(lat, lon) {
	var alertMarker = new google.maps.Marker({
		position : new google.maps.LatLng(lat, lon),
		map : this.map,
		icon : this.alertImage
	});
};
EmergensorView.prototype.initMap = function() {
	var view = this;

	this.map = new google.maps.Map(this.$map[0], {
		center : {
			lat : -34.397,
			lng : 150.644
		},
		zoom : 17
	});
	this.infoWindow = new google.maps.InfoWindow;
	this.alertImage = new google.maps.MarkerImage(
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

			view.location = {
				lat : position.coords.latitude,
				lon : position.coords.longitude
			};

			/*******************************************************************
			 * var marker = new google.maps.Marker({ position: pos, map: map });
			 ******************************************************************/

			view.infoWindow.setPosition(pos);
			view.infoWindow.setContent('Location found.');
			view.infoWindow.open(view.map);
			view.map.setCenter(pos);
		}, function() {
			handleLocationError(true);
		});
	} else {
		// Browser doesn't support Geolocation
		handleLocationError(false);
	}
};
EmergensorView.prototype.handleLocationError = function(browserHasGeolocation) {
	this.infoWindow.setPosition(this.map.getCenter());
	this.infoWindow
			.setContent(browserHasGeolocation ? 'Error: The Geolocation service failed.'
					: 'Error: Your browser doesn\'t support geolocation.');
	this.infoWindow.open(this.map);
};
