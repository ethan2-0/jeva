package jeva.example;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fi.iki.elonen.NanoHTTPD.Response;
import jeva.Jeva;
import jeva.Request;
import jeva.Serves;

public class Example {
	/**
	 * The entry point for the example server.
	 * @param args Unused.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Jeva jeva = new Jeva(new Example());
		jeva.run(8000); // runThreaded could be used to run it in a seperate thread; you'd probably need a sleep-loop, though.
	}
	
	/**
	 * Serve the root.
	 * @param request The request.
	 * @return The content.
	 */
	@Serves(path="/")
	public Response serveRoot(Request request) {
		StringBuilder response = new StringBuilder();
		
		String host = request.getSession().getHeaders().get("host"); // Used in some of the constructions later.
		
		response.append("This an example page from Jeva.\n"
				+ "Try requesting:\n"
				+ "* http://" + host + "/static/static.html\n"
				+ "* http://" + host + "/echo/[insert some text]\n"
				+ "* http://" + host + "/echoInt/[insert an integer]\n"
				+ "\n"
				+ "This is actually created dynamically, generated after\n"
				+ "each request; this request was processed at the timestamp\n"
				+ new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		
		return new Response(response.toString()).setMimeType("text/plain");
	}
	/**
	 * Serve /echo/&lt;str&gt;.
	 * @param request The request.
	 * @return The content.
	 */
	@Serves(path="/echo/<str>")
	public String serveEcho(Request request) {
		return request.getParam(0);
	}
	/**
	 * Serve /echoInt/&lt;int&gt;.
	 * @param request The request.
	 * @return The content.
	 */
	@Serves(path="/echoInt/<int>")
	public String serveEchoInt(Request request) {
		return request.getParam(0); // If you wanted to do something useful with the param, you'd have to parseInt it.
	}
}
