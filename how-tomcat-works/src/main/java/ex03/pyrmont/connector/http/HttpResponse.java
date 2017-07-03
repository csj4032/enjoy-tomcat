package ex03.pyrmont.connector.http;


import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

public class HttpResponse implements HttpServletResponse {

	private static final int BUFFER_SIZE = 1024;
	HttpRequest request;
	OutputStream output;
	PrintWriter writer;
	protected byte[] buffer = new byte[BUFFER_SIZE];
	protected int bufferCount = 0;

	protected boolean committed = false;
	protected int contentCount = 0;
	protected int contentLength = -1;
	protected String contentType = null;
	protected String encoding = null;
	protected ArrayList cookies = new ArrayList();
	protected HashMap headers = new HashMap();
	protected final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
	protected String message = getStatusMessage(HttpServletResponse.SC_OK);
	protected int status = HttpServletResponse.SC_OK;

	public HttpResponse(OutputStream output) {
		this.output = output;
	}

	public void finishResponse() {
		if (writer != null) {
			writer.flush();
			writer.close();
		}
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	@Override
	public void addCookie(Cookie cookie) {

	}

	@Override
	public boolean containsHeader(String name) {
		return false;
	}

	@Override
	public String encodeURL(String url) {
		return null;
	}

	@Override
	public String encodeRedirectURL(String url) {
		return null;
	}

	@Override
	public String encodeUrl(String url) {
		return null;
	}

	@Override
	public String encodeRedirectUrl(String url) {
		return null;
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {

	}

	@Override
	public void sendError(int sc) throws IOException {

	}

	@Override
	public void sendRedirect(String location) throws IOException {

	}

	@Override
	public void setDateHeader(String name, long date) {

	}

	@Override
	public void addDateHeader(String name, long date) {

	}

	public void setHeader(String name, String value) {
		if (isCommitted()) {
			return;
		}

		ArrayList values = new ArrayList();
		values.add(value);

		synchronized (headers) {
			headers.put(name, values);
		}

		String match = name.toLowerCase();
		if (match.equals("content-length")) {
			int contentLength = -1;

			try {
				contentLength = Integer.parseInt(value);
			} catch (NumberFormatException e) {

			}
		} else if (match.equals("content-type")) {
			setContentType(value);
		}
	}

	public void sendStaticResource() throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		FileInputStream fis = null;
		try {
			File file = new File(Constants.WEB_ROOT, request.getRequestURI());
			fis = new FileInputStream(file);
			int ch = fis.read(bytes, 0, BUFFER_SIZE);
			while (ch != -1) {
				output.write(bytes, 0, ch);
				ch = fis.read(bytes, 0, BUFFER_SIZE);
			}
		} catch (FileNotFoundException e) {
			String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
					"Content-Type: text/html\r\n" +
					"Content-Length: 23\r\n" +
					"\r\n" +
					"<h1>File Not Found</h1>";
			output.write(errorMessage.getBytes());
		} finally {
			if (fis != null)
				fis.close();
		}
	}

	@Override
	public void addHeader(String name, String value) {

	}

	@Override
	public void setIntHeader(String name, int value) {
		if (isCommitted())
			return;
		setHeader(name, "" + value);
	}

	@Override
	public void addIntHeader(String name, int value) {

	}

	@Override
	public void setStatus(int sc) {

	}

	@Override
	public void setStatus(int sc, String sm) {

	}

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public String getHeader(String name) {
		return null;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		return null;
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	protected String getProtocol() {
		return request.getProtocol();
	}

	protected String getStatusMessage(int status) {
		switch (status) {
			case SC_OK:
				return ("OK");
			default:
				return ("HTTP Response Status " + status);
		}
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return null;
	}

	@Override
	public void setCharacterEncoding(String charset) {

	}

	@Override
	public void setContentLength(int len) {

	}

	@Override
	public void setContentLengthLong(long length) {

	}

	@Override
	public void setContentType(String type) {

	}

	@Override
	public void setBufferSize(int size) {

	}

	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public void flushBuffer() throws IOException {

	}

	@Override
	public void resetBuffer() {

	}

	@Override
	public boolean isCommitted() {
		return (committed);
	}

	@Override
	public void reset() {

	}

	@Override
	public void setLocale(Locale loc) {

	}

	@Override
	public Locale getLocale() {
		return null;
	}
}
