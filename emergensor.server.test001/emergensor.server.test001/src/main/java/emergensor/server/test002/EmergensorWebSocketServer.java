package emergensor.server.test002;

import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import net.arnx.jsonic.JSON;

public class EmergensorWebSocketServer extends WebSocketServer
{

	private Emergensor emergensor;

	public EmergensorWebSocketServer(Emergensor emergensor, InetSocketAddress address)
	{
		super(address);
		this.emergensor = emergensor;
	}

	@Override
	public void onStart()
	{

	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake)
	{
		for (Marker marker : emergensor.getMarkers()) {
			sendMarker(conn, marker);
		}
	}

	public static void sendMarker(WebSocket conn, Marker marker)
	{
		conn.send("Marker " + JSON.encode(marker));
	}

	public void onAddMarker(EmergensorEvent.AddMarker event)
	{
		connections().forEach(c -> sendMarker(c, event.marker));
	}

	@Override
	public void onMessage(WebSocket conn, String message)
	{
		if (message.startsWith("Alert ")) {
			emergensor.addAlert(JSON.decode(message.substring(6), Alert.class), conn);

			/*
			new Thread(() -> {
				for (int i = 0; i < 10; i++) {
					String message2 = String.format("lat: %f lon: %f",
						lat + (Math.random() * 0.002 - 0.001),
						lon + (Math.random() * 0.002 - 0.001));
					connections().forEach(s -> s.send(message2));
					try {
						Thread.sleep((int) (Math.random() * 200));
					} catch (InterruptedException e) {
						break;
					}
				}
			}).start();
			*/
		}
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote)
	{

	}

	@Override
	public void onError(WebSocket conn, Exception ex)
	{
		ex.printStackTrace();
	}

}
