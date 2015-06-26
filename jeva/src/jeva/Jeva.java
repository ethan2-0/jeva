package jeva;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import fi.iki.elonen.NanoHTTPD;

public class Jeva {
	private JevaServer server;
	private Object responder;
	public Jeva(Object responder) {
		this.responder = responder;
	}
	/**
	 * Assigns the location of the responder as the static path.
	 */
	private void assignResponderLocationAsStaticPath() {
		URL url = responder.getClass().getResource("");
		try {
			Path path = Paths.get(url.toURI()).resolve("static");
			server.setStaticPath(path);
		} catch (URISyntaxException e) {
			System.out.println("uh oh");
			e.printStackTrace();
		}
	}
	/**
	 * Run the server on the current thread. Will return when stopped.
	 * @param port The port to run the server on.
	 * @throws IOException
	 */
	public void run(int port) throws IOException {
		server = new JevaServer(port, responder);
		assignResponderLocationAsStaticPath();
		server.start();
	}
	/**
	 * Run the server in a seperate daemon thread. The daemon thread will exit when the server stops.
	 * @param port The port to run the server on.
	 * @throws IOException
	 */
	public void runThreaded(int port) throws IOException {
		server = new JevaServer(port, responder);
		assignResponderLocationAsStaticPath();
		server.startThreaded();
	}
	/**
	 * Get the underlying server object.
	 * @return The server. Unless overridden by a subclass, is always instanceof JevaServer.
	 */
	public NanoHTTPD getServer() {
		return server;
	}
	/**
	 * Stop the server.
	 */
	public void stop() {
		server.stop();
	}
}