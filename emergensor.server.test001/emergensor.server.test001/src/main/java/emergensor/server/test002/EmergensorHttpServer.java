package emergensor.server.test002;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import mirrg.lithium.cgi.HTTPResponse;
import mirrg.lithium.cgi.routing.CGIRouter;
import mirrg.lithium.cgi.routing.HttpHandlerCGIRouting;
import net.arnx.jsonic.JSON;

public class EmergensorHttpServer
{

	private Emergensor emergensor;

	public final HttpServer server;

	public EmergensorHttpServer(Emergensor emergensor, InetSocketAddress address, CGIRouter[] cgiRouters) throws IOException
	{
		this.emergensor = emergensor;
		this.server = HttpServer.create(address, 10);
		{
			HttpContext context;
			context = server.createContext("/", new HttpHandlerCGIRouting(cgiRouters));
			/*
			 context.authenticator = new BasicAuthenticator("WebInterface") {
			 @Override
			 public boolean checkCredentials(String username, String password) {
			 if (username.contains(":")) return false
			 return "$username\n$password" ==~ /[a-zA-Z0-9_]{1,16}\nho-tyo- tou4rou/
			 }
			 }
			 */
			context = server.createContext("/__api/get/portWebSocket", e -> HTTPResponse.send(e, 200, "" + emergensor.portWebSocket));
			context = server.createContext("/__api/send/alert", e -> {
				if (!e.getRequestMethod().equals("POST")) {
					HTTPResponse.send(e, 500);
					return;
				}

				AlertFromApp alert;
				try (InputStream in = e.getRequestBody()) {
					alert = JSON.decode(in, AlertFromApp.class);
				}

				emergensor.addAlert(new Alert(alert.text, alert.lat, alert.lng), alert.userId);

				HTTPResponse.send(e, 200, "{}");
				return;
			});
			context.setAuthenticator(new BasicAuthenticator("WebInterface") {
				@Override
				public boolean checkCredentials(String username, String password)
				{
					if (username.equals("a") && password.equals("gp-^45:w3v9]332c")) {
						return true;
					}
					return false;
				}
			});
		}
	}

}
