package emergensor.server.test002;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.ArrayList;

import mirrg.lithium.cgi.routing.CGIRouter;
import mirrg.lithium.event.EventManager;

public class Emergensor
{

	public final String hostname;
	public final int portHttp;
	public final int portWebSocket;

	private EventManager<EmergensorEvent> event = new EventManager<>();
	private int markerIdNext = 0;
	private ArrayList<Marker> markers = new ArrayList<>();
	public final IdentifierTable identifierTable = new IdentifierTable();

	public Emergensor(String hostname, int portHttp, int portWebSocket)
	{
		this.hostname = hostname;
		this.portHttp = portHttp;
		this.portWebSocket = portWebSocket;
	}

	public EventManager<EmergensorEvent> event()
	{
		return event;
	}

	public EmergensorHttpServer createHttpServer(CGIRouter[] cgiRouters) throws IOException
	{
		return new EmergensorHttpServer(this, new InetSocketAddress(hostname, portHttp), cgiRouters);
	}

	public EmergensorWebSocketServer createWebSocketServer()
	{
		EmergensorWebSocketServer webSocketServer = new EmergensorWebSocketServer(this, new InetSocketAddress(hostname, portWebSocket));
		event().register(EmergensorEvent.AddMarker.class, webSocketServer::onAddMarker);
		return webSocketServer;
	}

	public synchronized void addAlert(Alert alert, Object identifier)
	{
		Marker marker = new Marker(
			markerIdNext,
			identifierTable.get(identifier),
			Instant.now().toEpochMilli(),
			alert.text,
			alert.lat,
			alert.lon);
		markerIdNext++;

		markers.add(marker);
		event().post(new EmergensorEvent.AddMarker(marker));
	}

	public synchronized Marker[] getMarkers()
	{
		Instant now = Instant.now();
		markers.removeIf(m -> m.time < now.toEpochMilli() - 60 * 1000);
		return markers.toArray(new Marker[0]);
	}

}
