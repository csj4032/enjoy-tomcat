package ex03.pyrmont.connector.http;


import java.io.OutputStream;
import java.io.PrintWriter;

public class HttpResponse {

	private static final int BUFFER_SIZE = 1024;
	HttpRequest request;
	OutputStream output;
	PrintWriter writer;
	protected byte[] buffer = new byte[BUFFER_SIZE];
	protected int bufferCount = 0;

	public HttpResponse(OutputStream output) {
		this.output = output;
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}
}
