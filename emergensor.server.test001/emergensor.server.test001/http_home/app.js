function EmergensorView() {
	this.map = null;

	this.location = null; // {lat: , lon: }

	this.socket = null;

	this.alertImage = null;
	this.infoWindow = null;
	this.polylines = {};
	this.markerEntries = [];
	this.userIdToPolyline = {};
	this.dangerZones = [];

	this.debug = false;

	this.$map = null;
	this.$updates = null;
	this.$connStatus = null;
}

EmergensorView.prototype.connect = function() {
	var view = this;

	$.ajax({
		type : "GET",
		url : "/__api/get/portWebSocket",
		success : function(message) {
			view.socket = new WebSocket("ws://" + location.hostname + ":"
					+ message + "/view");
			view.socket.onerror = function(error) {
				view.setStatus('Connection to server error', 'error');
				console.log('ERROR:' + error);
				view.socket = null;
			};
			view.socket.onopen = function(event) {
				view.setStatus('Connection to server successful!', 'open');
				view.socket.send("Mobile 1");
			};
			view.socket.onclose = function(event) {
				view.setStatus('Connection to server closed', 'closed');
				view.socket = null;
			};
			view.socket.onmessage = function(event) {
				view.addUpdate('Received Alert - ' + event.data);
				if (event.data.startsWith("Marker ")) {
					view.addMarker(JSON.parse(event.data.substr(7)));
				}
			};
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			view.setStatus('Connection to server error', 'error');
			console.log('ERROR:' + textStatus + "/" + errorThrown);
		}
	});
};
EmergensorView.prototype.disconnect = function() {
	this.socket.close();
};
EmergensorView.prototype.isConnected = function() {
	return this.socket != null;
};
EmergensorView.prototype.submit = function(alertMessage) {
	this.sendAlert({
		text : alertMessage,
		lat : this.location.lat,
		lon : this.location.lon
	});
};
EmergensorView.prototype.sendAlert = function(data) {
	var msg = "Alert " + JSON.stringify(data);

	// moment(moment().valueOf()).valueOf()
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
EmergensorView.prototype.addMarker = function(markerEntry) {
	var view = this;

	var duration = markerEntry.time + 60 * 1000 - moment().valueOf();
	if (duration > 0) {

		markerEntry.latLng = new google.maps.LatLng(markerEntry.lat,
				markerEntry.lon);

		view.markerEntries.push(markerEntry);

		// make marker
		markerEntry.marker = new google.maps.Marker({
			position : markerEntry.latLng,
			icon : view.alertImage
		});
		markerEntry.marker.setMap(view.map);

		// make polyline
		if (view.userIdToPolyline[markerEntry.userId] == undefined) {
			view.userIdToPolyline[markerEntry.userId] = new google.maps.Polyline(
					{
						strokeColor : '#000000',
						strokeOpacity : 1.0,
						strokeWeight : 3
					});
			view.userIdToPolyline[markerEntry.userId].setMap(view.map);
		}
		view.userIdToPolyline[markerEntry.userId].getPath().push(
				markerEntry.latLng);

		// make dangerZone
		if (!view.isInDangerZone(markerEntry.latLng)) {
			var markerEntries = view.markerEntries.filter(
					function(m) {
						return google.maps.geometry.spherical
								.computeDistanceBetween(m.latLng,
										markerEntry.latLng) < 200;
					}).filter(function(m) {
				return !view.isInDangerZone(m.latLng);
			});
			if (markerEntries.length >= 10) {
				var lat = 0;
				var lng = 0;
				markerEntries.forEach(function(m) {
					lat += m.latLng.lat();
					lng += m.latLng.lng();
				});
				lat /= markerEntries.length;
				lng /= markerEntries.length;
				var latLng = new google.maps.LatLng(lat, lng);
				var radius = 0;
				markerEntries.forEach(function(m) {
					radius = Math.max(radius, google.maps.geometry.spherical
							.computeDistanceBetween(m.latLng, latLng));
				});

				var dangerZone = new google.maps.Circle({
					strokeColor : '#FF0000',
					strokeOpacity : 0.8,
					strokeWeight : 2,
					fillColor : '#FF0000',
					fillOpacity : 0.35,
					center : latLng,
					radius : radius
				});

				dangerZone.setMap(view.map);
				view.dangerZones.push(dangerZone);
				setTimeout(function() {
					for (var i = view.dangerZones.length - 1; i >= 0; i--) {
						if (view.dangerZones[i] == dangerZone) {
							view.dangerZones.splice(i, 1);
						}
					}
					dangerZone.setMap(null);
					dangerZone = null;
				}, 60 * 1000);
			}
		}

		// register remover
		setTimeout(function() {
			for (var i = view.markerEntries.length - 1; i >= 0; i--) {
				if (view.markerEntries[i] == markerEntry) {
					view.markerEntries.splice(i, 1);
				}
			}
			view.removeMarker(markerEntry);
		}, duration);

	}
};
EmergensorView.prototype.isInDangerZone = function(latLng) {
	for (var i = 0; i < this.dangerZones.length; i++) {
		if (google.maps.geometry.spherical.computeDistanceBetween(
				this.dangerZones[i].center, latLng) < this.dangerZones[i].radius) {
			return true;
		}
	}
	return false;
};
EmergensorView.prototype.removeMarker = function(markerEntry) {
	markerEntry.marker.setMap(null);

	// remove polyline
	var path = this.userIdToPolyline[markerEntry.userId].getPath();
	for (var i = path.getLength() - 1; i >= 0; i--) {
		if (path.getAt(i) == markerEntry.latLng) {
			path.removeAt(i);
		}
	}
	if (path.getLength() == 0) {
		this.userIdToPolyline[markerEntry.userId].setMap(null);
		this.userIdToPolyline[markerEntry.userId] = undefined;
	}

};
EmergensorView.prototype.initMap = function() {
	var view = this;

	this.map = new google.maps.Map(this.$map[0], {
		center : {
			lat : 35.70923054243309,
			lng : 139.76082175970078
		},
		zoom : 17
	});
	this.infoWindow = new google.maps.InfoWindow;
	this.alertImage = new google.maps.MarkerImage(
			"http://chart.apis.google.com/chart?chst=d_map_xpin_letter&chld=pin_star|A|FF0000|000000|",
			new google.maps.Size(21, 34), new google.maps.Point(0, 0),
			new google.maps.Point(10, 34));
	this.map.addListener("click", function(e) {
		if (view.debug) {
			e.stop();
			view.sendAlert({
				text : "Debug",
				lat : e.latLng.lat(),
				lon : e.latLng.lng()
			});
		}
	});

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
