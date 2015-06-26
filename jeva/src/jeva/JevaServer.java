package jeva;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import static jeva.JevaUtils.*;

public class JevaServer extends NanoHTTPD {
	private Object responder;
	private HashMap<String, java.lang.reflect.Method> responders;
	private Path staticPath;
	/**
	 * Create a new JevaServer that will listen on port and use
	 * the object responder as its source of handlers.
	 * Does not listen until the run method is called. 
	 * @param port The port to listen on.
	 * @param responder The responder to use as a source of handlers.
	 */
	protected JevaServer(int port, Object responder) {
		super(port);
		this.responder = responder;
	}
	/**
	 * Set the static path; that is, the path searched to respond to
	 * /static/[somthing] URLs.
	 * @param path The path on the filesystem.
	 */
	protected void setStaticPath(Path path) {
		this.staticPath = path;
	}
	/**
	 * Update the cache of methods that respond to requests in responder.
	 * This should only really be called once.
	 */
	public void updateRespondersCache() {
		responders = ClassAnnotationScanner.getResponders(responder);
	}
	/**
	 * Is template appliccable to real? Template is the "path" paramater
	 * to Serves. For example, for real="/var" and template="/var", returns
	 * true; for real="/abc" and template="/var", returns false;
	 * for real="/var/&lt;str&gt;" and template="/var/abc", return true;
	 * for real="/var/&lt;int&gt;" and template="/var/abc", returns false.
	 * Sorry I can't describe it better.
	 * @param real The actual URL requested.
	 * @param template The template; paramater to Serves.
	 * @return Does real match template?
	 */
	public boolean matches(String real, String template) {
		// I'm not even going to try to comment this function.
		String[] realParts = real.split("/");
		String[] templateParts = template.split("/");
		if(realParts.length != templateParts.length) {
			return false;
		}
		for(int i = 0; i < realParts.length; i++) {
			if(templateParts[i].equals("<int>")) {
				try {
					Integer.parseInt(realParts[i]);
				} catch(NumberFormatException e) {
					return false;
				}
			} else if(templateParts[i].equals("<str>")) {
				//Automatic match
			} else {
				if(!templateParts[i].equals(realParts[i])) {
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * Given a template (for a url; the path in the jeva.Serves annotation),
	 * get a list of the actual matches. For example, for real="/test/abc/123"
	 * and template="/test/&lt;str&gt;/&lt;int&gt;", would return the ArrayList
	 * of {"abc", "123"}.
	 * @param real The actual string provided.
	 * @param template
	 * @return
	 */
	public ArrayList<String> getMatches(String real, String template) {
		//Again, not going to try to comment this function.
		ArrayList<String> ret = new ArrayList<>();
		String[] realParts = real.split("/");
		String[] templateParts = template.split("/");
		if(!matches(real, template)) {
			throw new RuntimeException("Invalid arguments: real and template don't even match");
		}
		for(int i = 0; i < realParts.length; i++) {
			if(templateParts[i].equals("<int>") || templateParts[i].equals("<str>")) {
				ret.add(realParts[i]);
			}
		}
		return ret;
	}
	/**
	 * An override to the NanoHTTPD method of serve. This is where all of the HTTP requests are processsed from.
	 */
	@Override
	public Response serve(IHTTPSession session) {
		// Serve static paths
		if(session.getUri().startsWith("/static")) {
			// Convert the URL requested into the corresponding path on the filesystem
			Path staticPath = this.staticPath == null ? getRootPath().resolve("static") : this.staticPath; // Provide a default staticPath
			Path uriPath = staticPath.resolve(session.getUri().substring(8)).toAbsolutePath(); // substring(8) to remove "/static" (len = 8)
			if(uriPath.toString().length() < staticPath.toString().length()) {
				return getErrorResponse(Status.BAD_REQUEST, "Nice try");
			}
			// Read it into memory
			StringBuilder response = new StringBuilder(); //TODO: Large files (or do I even want to?)
			try {
				BufferedReader br = new BufferedReader(new FileReader(uriPath.toAbsolutePath().toString()));
				String line;
				while((line = br.readLine()) != null) {
					response.append(line);
				}
				br.close();
			} catch(FileNotFoundException e) {
				return getErrorResponse(Status.NOT_FOUND);
			} catch (IOException e) {
				return getErrorResponse(Status.INTERNAL_ERROR);
			}
			// Return it
			// TODO: MIME type
			return new Response(response.toString());
		}
		try {
			if(responders == null) {
				System.out.println("Updating responders cache");
				updateRespondersCache();
			}
			Response response = null;
			// Find the appropriate responder
			for(String s : responders.keySet()) {
				if(matches(session.getUri(), s)) {
					// We've found it; invoke it.
					// o is the object returned by the responder.
					Object o;
					if(responders.get(s).getParameterCount() == 0) {
						o = responders.get(s).invoke(responder);
					} else {
						o = responders.get(s).invoke(responder, new Request(session, getMatches(session.getUri(), s)));
					}
					// If the responder returned a string, create a response wrapping the string.
					if(o instanceof String) {
						response = new NanoHTTPD.Response((String) o);
					} else if(o instanceof Response) {
						response = (Response) o;
					}
				}
			}
			if(response == null) {
				return getErrorResponse(Status.NOT_FOUND, "No responder found for '" + session.getUri() + "'");
			}
			return response;
		} catch(Exception e) {
			NanoHTTPD.Response resp = new NanoHTTPD.Response("Internal server error: " + e.toString());
			resp.setMimeType("text/plain");
			resp.setStatus(Status.INTERNAL_ERROR);
			return resp;
		}
	}
}