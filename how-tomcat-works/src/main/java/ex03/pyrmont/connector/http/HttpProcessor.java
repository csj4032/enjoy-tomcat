package ex03.pyrmont.connector.http;

import ex03.pyrmont.ServletProcessor;
import ex03.pyrmont.StaticResourceProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.RequestUtil;
import org.apache.naming.StringManager;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

@Slf4j
public class HttpProcessor {

	private HttpConnector connector;
	private HttpRequest request;
	private HttpRequestLine requestLine = new HttpRequestLine();
	private HttpResponse response;

	protected String method = null;
	protected String queryString = null;

	protected StringManager sm = StringManager.getManager("");

	public HttpProcessor(HttpConnector connector) {
		this.connector = connector;
	}

	public void process(Socket socket) {
		SocketInputStream input = null;
		OutputStream output = null;

		try {

			input = new SocketInputStream(socket.getInputStream(), 2048);
			output = socket.getOutputStream();

			request = new HttpRequest(input);

			response = new HttpResponse(output);
			response.setRequest(request);

			response.setHeader("Server", "Pyrmont Servlet Container");

			parseRequest(input, output);
			parseHeaders(input);

			if (request.getRequestURI().startsWith("/servlet/")) {
				ServletProcessor processor = new ServletProcessor();
				processor.process(request, response);
			} else {
				StaticResourceProcessor processor = new StaticResourceProcessor();
				processor.process(request, response);
			}

			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseHeaders(SocketInputStream input) throws IOException, ServletException {
		while (true) {
			HttpHeader header = new HttpHeader();

			// Read the next header
			input.readHeader(header);
			if (header.nameEnd == 0) {
				if (header.valueEnd == 0) {
					return;
				} else {
					throw new ServletException
							(sm.getString("httpProcessor.parseHeaders.colon"));
				}
			}

			String name = new String(header.name, 0, header.nameEnd);
			String value = new String(header.value, 0, header.valueEnd);
			request.addHeader(name, value);

			if (name.equals("cookie")) {
				Cookie cookies[] = RequestUtil.parseCookieHeader(value);
				for (int i = 0; i < cookies.length; i++) {
					if (cookies[i].getName().equals("jsessionid")) {
						// Override anything requested in the URL
						if (!request.isRequestedSessionIdFromCookie()) {
							// Accept only the first session id cookie
							request.setRequestedSessionId(cookies[i].getValue());
							request.setRequestedSessionCookie(true);
							request.setRequestedSessionURL(false);
						}
					}
					request.addCookie(cookies[i]);
				}
			} else if (name.equals("content-length")) {
				int n = -1;
				try {
					n = Integer.parseInt(value);
				} catch (Exception e) {
					throw new ServletException(sm.getString("httpProcessor.parseHeaders.contentLength"));
				}
				request.setContentLength(n);
			} else if (name.equals("content-type")) {
				request.setContentType(value);
			}
		}
	}

	private void parseRequest(SocketInputStream input, OutputStream output) throws IOException, ServletException {
		input.readRequestLine(requestLine);
		String method = new String(requestLine.method, 0, requestLine.methodEnd);
		String uri = null;
		String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);

		if (method.length() < 1) {
			throw new ServletException("Missing HTTP request method");
		} else if (requestLine.uriEnd < 1) {
			throw new ServletException("Missing HTTP request URI");
		}

		int question = requestLine.indexOf("?");
		if (question >= 0) {
			request.setQueryString(new String(requestLine.uri, question + 1,
					requestLine.uriEnd - question - 1));
			uri = new String(requestLine.uri, 0, question);
		} else {
			request.setQueryString(null);
			uri = new String(requestLine.uri, 0, requestLine.uriEnd);
		}

		if (!uri.startsWith("/")) {
			int pos = uri.indexOf("://");
			if (pos != -1) {
				pos = uri.indexOf('/', pos + 3);
				if (pos == -1) {
					uri = "";
				} else {
					uri = uri.substring(pos);
				}
			}
		}

		String match = ";jsessionid=";
		int semicolon = uri.indexOf(match);
		if (semicolon >= 0) {
			String rest = uri.substring(semicolon + match.length());
			int semicolon2 = rest.indexOf(';');
			if (semicolon2 >= 0) {
				request.setRequestedSessionId(rest.substring(0, semicolon2));
				rest = rest.substring(semicolon2);
			} else {
				request.setRequestedSessionId(rest);
				rest = "";
			}
			request.setRequestedSessionURL(true);
			uri = uri.substring(0, semicolon) + rest;
		} else {
			request.setRequestedSessionId(null);
			request.setRequestedSessionURL(false);
		}

		String normalizedUri = normalize(uri);

		((HttpRequest) request).setMethod(method);
		request.setProtocol(protocol);
		if (normalizedUri != null) {
			((HttpRequest) request).setRequestURI(normalizedUri);
		} else {
			((HttpRequest) request).setRequestURI(uri);
		}

		if (normalizedUri == null) {
			throw new ServletException("Invalid URI: " + uri + "'");
		}
	}

	protected String normalize(String path) {
		if (path == null)
			return null;

		String normalized = path;

		if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
			normalized = "/~" + normalized.substring(4);

		if ((normalized.indexOf("%25") >= 0)
				|| (normalized.indexOf("%2F") >= 0)
				|| (normalized.indexOf("%2E") >= 0)
				|| (normalized.indexOf("%5C") >= 0)
				|| (normalized.indexOf("%2f") >= 0)
				|| (normalized.indexOf("%2e") >= 0)
				|| (normalized.indexOf("%5c") >= 0)) {
			return null;
		}

		if (normalized.equals("/."))
			return "/";

		if (normalized.indexOf('\\') >= 0)
			normalized = normalized.replace('\\', '/');
		if (!normalized.startsWith("/"))
			normalized = "/" + normalized;

		while (true) {
			int index = normalized.indexOf("//");
			if (index < 0)
				break;
			normalized = normalized.substring(0, index) + normalized.substring(index + 1);
		}

		while (true) {
			int index = normalized.indexOf("/./");
			if (index < 0)
				break;
			normalized = normalized.substring(0, index) + normalized.substring(index + 2);
		}

		while (true) {
			int index = normalized.indexOf("/../");
			if (index < 0)
				break;
			if (index == 0)
				return (null);
			int index2 = normalized.lastIndexOf('/', index - 1);
			normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
		}

		if (normalized.indexOf("/...") >= 0)
			return (null);

		return (normalized);
	}
}