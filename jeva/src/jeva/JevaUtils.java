package jeva;

import java.nio.file.Path;
import java.nio.file.Paths;

import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.IStatus;

public final class JevaUtils {
	public static Response getErrorResponse(IStatus status) {
		return getErrorResponse(status, ""); //Give better explanation
	}
	/**
	 * Construct an error response given a response code and an explanation. Utility method.
	 * @param status The IStatus of the status code.
	 * @param explanation The explanation of the error.
	 * @return A response containing information about the status and the explanation.
	 */
	public static Response getErrorResponse(IStatus status, String explanation) {
		return new Response(status.getDescription().toUpperCase() + "\n" + explanation)
			.setStatus(status)
			.setMimeType("text/plain");
	}
	/**
	 * Alias for Paths.get("").toAbsolutePath().
	 * @return Paths.get("").toAbsolutePath().
	 */
	public static Path getRootPath() {
		return Paths.get("").toAbsolutePath();
	}
}
