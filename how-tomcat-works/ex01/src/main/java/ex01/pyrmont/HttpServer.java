package ex01.pyrmont;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class HttpServer {

	public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "how-tomcat-works" + File.separator + "webroot" + File.separator;
	public static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
	private boolean shutDown = false;

	public static void main(String[] args) {
		log.info("WEB ROOT : {}", WEB_ROOT);
		HttpServer server = new HttpServer();
		server.await();
	}

	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;

		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		while (!shutDown) {
			try {
				Socket socket = serverSocket.accept();
				InputStream inputStream = socket.getInputStream();
				OutputStream outputStream = socket.getOutputStream();

				Request request = new Request(inputStream);
				request.parse();

				Response response = new Response(outputStream);
				response.setRequest(request);
				response.sendStaticResource();

				socket.close();

				shutDown = request.getUri().equals(SHUTDOWN_COMMAND);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
}
