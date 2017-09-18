package emergensor.server.test002;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import mirrg.lithium.cgi.HTTPResponse;
import mirrg.lithium.cgi.routing.CGIRouter;
import mirrg.lithium.cgi.routing.HttpHandlerCGIRouting;

public class EmergensorHttpServer
{

	private Emergensor emergensor;

	public final HttpServer server;

	public EmergensorHttpServer(Emergensor emergensor, InetSocketAddress address, CGIRouter[] cgiRouters) throws IOException
	{
		this.emergensor = emergensor;
		this.server = HttpServer.create(address, 10);
		{
			HttpContext context = server.createContext("/", new HttpHandlerCGIRouting(cgiRouters));
			/*
			 context.authenticator = new BasicAuthenticator("WebInterface") {
			 @Override
			 public boolean checkCredentials(String username, String password) {
			 if (username.contains(":")) return false
			 return "$username\n$password" ==~ /[a-zA-Z0-9_]{1,16}\nho-tyo- tou4rou/
			 }
			 }
			 */
			server.createContext("/__api/get/portWebSocket", e -> HTTPResponse.send(e, 200, "" + emergensor.portWebSocket));
		}
	}

}
