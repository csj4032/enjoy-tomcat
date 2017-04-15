import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;

public class TomcatServer {

	public static void main(String[] args) throws LifecycleException, ServletException {
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(8080);
		tomcat.start();
		tomcat.getServer().await();

		Connector connector = tomcat.getConnector();
		connector.setURIEncoding("UTF-8");
	}
}
