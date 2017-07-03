package ex03.pyrmont.connector.http;

import java.net.Socket;

public class HttpProcessor {

	private HttpConnector connector;

	public HttpProcessor(HttpConnector connector) {
		this.connector = connector;
	}

	public void process(Socket socket) {
		SocketInputStream input = null;
	}
}