package jeva.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import jeva.Jeva;
import jeva.Request;
import jeva.Serves;

public class Tests {
	private Jeva jeva;
	private HashMap<String, Boolean> testPasses;
	/**
	 * Construct a new Tests object. This is the object that both the client and server run out of.
	 */
	public Tests() {
		jeva = new Jeva(this);
		testPasses = new HashMap<>();
	}
	/**
	 * The entry point for the tests.
	 * @param args Unused.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new Tests().run();
	}
	
	/**
	 * Record the result of a test. Part of the mini unit testing framework.
	 * @param name The name of the test.
	 * @param passes Did the test pass?
	 */
	private void test(String name, Boolean passes) {
		testPasses.put(name, passes);
	}
	
	/**
	 * Request a URL from the server hosted by the unit tests. Note that a trailing newline is added.
	 * @param relativeUrl The URL relative to the root of the server. For example, the root would be "/". Must be preceded by a /.
	 * @param ignoreException Don't print stack trace on exceptions; still return null.
	 * @return The interned result returned by the unit testing server.
	 */
	private String request(String relativeUrl, boolean ignoreExceptions) {
		String resolvedUrl = "http://localhost:8000" + relativeUrl;
		InputStream stream = null;
		try {
			URL url = new URL(resolvedUrl);
			
			URLConnection conn = url.openConnection();
			stream = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder ret = new StringBuilder();
			String line;
			while((line = reader.readLine()) != null) {
				ret.append(line + "\n");
			}
			//We don't even need to read from stream; the HTTP request has been sent.
			stream.close();
			return ret.substring(0, ret.length() - 1).intern();
		} catch(IOException e) {
			if(!ignoreExceptions) {
				System.out.println("uh oh");
				e.printStackTrace();
			}
		} finally {
			if(stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					System.out.println("uh oh");
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	/**
	 * Alias for request(relativeUrl, false).
	 * @param relativeUrl This is an alias
	 * @return This is an alias
	 */
	private String request(String relativeUrl) {
		return request(relativeUrl, false);
	}
	
	/**
	 * Run the tests.
	 * @throws IOException
	 */
	public void run() throws IOException {
		jeva.runThreaded(8000);
		
		// The reason I can do request(...) == "string literal" is because request calls intern on the string.
		
		// The explanations for the tests are in the request handlers.
		test("Bare-bones request a page", request("/barebones") == "barebones");
		test("Request a static page", request("/static/static.txt") == "static");
		test("Request a paramaterised page", request("/dataTest/thisIsAString") == "thisIsAString");
		test("Request a paramaterised page [int edition]", request("/dataTestInt/65536") == "65536");
		test("Paramaterised int validation", request("/dataTestInt/thisIsAString", true) == null); // Null means HTTP error
		test("Walk above static directory", request("/static/../../test.txt", true) == null); // Null means HTTP error
		
		
		// This section sets up two StringBuilders. They will both be printed out consecutively.
		// The loop appends the name of the test to either `passes` or `fails`, depending on the result of the test.
		StringBuilder passes = new StringBuilder(); // The StringBuilder
		StringBuilder fails = new StringBuilder();
		for(String testName : testPasses.keySet()) {
			StringBuilder appropriate = testPasses.get(testName) ? passes : fails; // The StringBuilder to actually append to.
			appropriate.append("* " + testName + "\n");
		}
		System.out.println("R E S U L T S"
				+ "\n=============");
		System.out.println("==Passes==\n" + passes);
		System.out.println("==Fails==\n" + fails);
	}
	
	/**
	 * Serve the root of the unit test server. This is never actually used by the unit tests; it's for debugging.
	 * @return
	 */
	@Serves(path="/")
	public String serveRoot() {
		return "Things work";
	}
	
	/**
	 * This is for the "Bare-bones request a page" test.
	 * @return The content.
	 */
	@Serves(path="/barebones")
	public String serveBarebones() {
		return "barebones";
	}
	/**
	 * This is for the "Request a paramaterised page" test.
	 * @param request The request object.
	 * @return The content.
	 */
	@Serves(path="/dataTest/<str>")
	public String serveDataTest(Request request) {
		return request.getParam(0);
	}
	/**
	 * This is for the "Request a paramaterised page [int edition]" test.
	 * @param request The request object.
	 * @return The content.
	 */
	@Serves(path="/dataTestInt/<int>")
	public String serveDataTestInt(Request request) {
		return request.getParam(0);
	}
}