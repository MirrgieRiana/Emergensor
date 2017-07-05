package emergensor.server.test001;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import mirrg.lithium.properties.HPropertiesParser;
import mirrg.lithium.properties.Properties;
import mirrg.lithium.struct.Tuple;

public class Server
{

	public static ArrayList<Tuple<Integer, String>> lines = new ArrayList<>();

	public static void main(String[] args) throws Exception
	{
		Properties properties = HPropertiesParser.parse(new File("emergensor.properties"), e -> {
			throw new RuntimeException(e);
		});

		HttpServer httpServer = HttpServer.create();
		httpServer.bind(new InetSocketAddress(
			properties.getString("host").get(),
			properties.getInteger("port").get()),
			properties.getInteger("backlog").get());

		httpServer.createContext("/send", e -> {

			if (e.getRequestMethod().equals("POST")) {
				String line = null;
				try (BufferedReader in = new BufferedReader(new InputStreamReader(e.getRequestBody()))) {
					line = in.readLine();
				}
				if (line != null) {
					lines.add(new Tuple<>(lines.size(), line));
				}

				send(e, "{}");
				return;
			}

			send(e, 500, "500");

		}).setAuthenticator(new BasicAuthenticator("API") {
			@Override
			public boolean checkCredentials(String arg0, String arg1)
			{
				if (arg0.equals(properties.getString("username").get())) {
					if (arg1.equals(properties.getString("password").get())) {
						return true;
					}
				}
				return false;
			}
		});

		httpServer.createContext("/api/lines", e -> send(e, String.format(
			"<meta charset='utf-8'>"
				+ "<style>td, th { border: 1px black solid; }</style>"
				+ "<table style='border-collapse: collapse;width: 100%%;'>"
				+ "<tr><th>No</th><th>内容</th></tr>"
				+ "%s"
				+ "</table>",
			lines.stream()
				.sorted((a, b) -> b.x - a.x)
				.limit(100)
				.map(t -> "<tr><td>" + t.x + "</td><td>" + t.y + "</td></tr>")
				.collect(Collectors.joining()))));

		httpServer.createContext("/api/lines/modCount", e -> send(e, "" + lines.size()));

		httpServer.createContext("/", e -> {
			String path = e.getRequestURI().getPath();
			if (path.endsWith("/")) {
				File file = new File("http_home/" + path + "index.html");
				if (file.isFile()) {
					e.getResponseHeaders().add("Content-Type", "text/html");
					sendFile(e, file.toURI().toURL());
					return;
				}
			} else {
				File file = new File("http_home/" + path);
				if (file.isFile()) {
					sendFile(e, file.toURI().toURL());
					return;
				}
			}
			send(e, 404, "404");
		});

		httpServer.start();
	}

	protected static void redirect(HttpExchange httpExchange, String string) throws IOException
	{
		httpExchange.getResponseHeaders().add("Location", string);
		httpExchange.sendResponseHeaders(301, 0);
		httpExchange.getResponseBody().close();
	}

	protected static void send(HttpExchange httpExchange, String text) throws IOException
	{
		send(httpExchange, 200, "text/html", text, "utf-8");
	}

	protected static void send(HttpExchange httpExchange, int code, String text) throws IOException
	{
		send(httpExchange, code, "text/html", text, "utf-8");
	}

	protected static void send(HttpExchange httpExchange, int code, String contentType, String text, String charset) throws IOException
	{
		httpExchange.getResponseHeaders().add("Content-Type", contentType + "; charset= " + charset);
		byte[] bytes = text.getBytes(charset);
		httpExchange.sendResponseHeaders(code, bytes.length);
		httpExchange.getResponseBody().write(bytes);
		httpExchange.getResponseBody().close();
	}

	protected static void sendFile(HttpExchange httpExchange, URL url) throws IOException
	{
		try {
			InputStream in = url.openStream();

			ArrayList<Tuple<byte[], Integer>> buffers = new ArrayList<>();
			while (true) {
				byte[] buffer = new byte[4000];
				int len = in.read(buffer);
				if (len == -1) break;
				buffers.add(new Tuple<>(buffer, len));
			}
			in.close();

			httpExchange.sendResponseHeaders(200, buffers.stream()
				.mapToInt(t -> t.y)
				.sum());
			for (Tuple<byte[], Integer> buffer : buffers) {
				httpExchange.getResponseBody().write(buffer.x, 0, buffer.y);
			}
			httpExchange.getResponseBody().close();
		} catch (IOException e2) {
			send(httpExchange, 404, "404");
		}
	}

}
