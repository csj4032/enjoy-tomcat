package ex03.pyrmont.connector.http;

import org.apache.naming.StringManager;

import java.io.OutputStream;
import java.net.Socket;

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

			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}