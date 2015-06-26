package jeva;

import java.util.ArrayList;
import fi.iki.elonen.NanoHTTPD.*;

/**
 * A wrapper object for IHTTPSession, with the added function of the paramaters (i.e. "abc" for /test/&lt;str&gt; handling /test/abc)
 * @author ethan
 *
 */
public class Request {
	/**
	 * The IHTTPSession representing the HTTP metadata of the request, such as headers and cookies.
	 */
	private IHTTPSession session;
	/**
	 * The params of the request (i.e. "abc" for /test/&lt;str&gt; handling /test/abc)
	 */
	private ArrayList<String> params;
	/**
	 * Construct a new Request object with a given session and list of parameters.
	 * @param session The session associated with the request.
	 * @param params The parameters associated with the request.
	 */
	public Request(IHTTPSession session, ArrayList<String> params) {
		this.session = session;
		this.params = params;
	}
	/**
	 * Get the session associated with the request.
	 * @return The session associated with the request.
	 */
	public IHTTPSession getSession() {
		return session;
	}
	/**
	 * Get the nth parameter associated with the request.
	 * @param index The index of the parameter that is being read.
	 * @return The nth parameter associated with the request.
	 */
	public String getParam(int index) {
		return params.get(index);
	}
}
