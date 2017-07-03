package ex03.pyrmont.connector.http;

import java.io.IOException;
import java.io.InputStream;

public class SocketInputStream extends InputStream {

	protected byte buf[];
	protected InputStream is;

	public SocketInputStream(InputStream is, int bufferSize) {
		this.is = is;
		buf = new byte[bufferSize];
	}

	@Override
	public int read() throws IOException {
		return 0;
	}
}
