package ex01.pyrmont;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class Response {

	private Request request;
	private OutputStream output;
	private String header = "HTTP/1.1 200 OK\r\nDate: Fri, 31 Dec 1999 23:59:59 GMT\r\nServer: Genius/0.0.1\r\nContent-Type: text/html charset=utf-8\r\nContent-Length: 136\r\n\r\n";
	private String errorMessage = "HTTP/1.1 404 File Not Found\r\n" + "Content-Type: text/html\r\n" + "Content-Length: 23\r\n" + "\r\n" + "<h1>File Not Found</h1>";

	public Response(OutputStream output) {
		this.output = output;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public void sendStaticResource() throws IOException {
		File file = new File(HttpServer.WEB_ROOT, request.getUri());
		if (file.exists()) {
			try (FileInputStream fis = new FileInputStream(file);
				var bufferedReader = new BufferedReader(new InputStreamReader(fis))) {
				output.write(header.getBytes());
				var html = new StringBuilder();
				var temp = "";
				while ((temp = bufferedReader.readLine()) != null) {
					html.append(temp);
				}
				output.write(html.toString().getBytes());
			} catch (Exception e) {
				log.error(e.getLocalizedMessage());
			}
		} else {
			output.write(errorMessage.getBytes());
		}
	}
}
