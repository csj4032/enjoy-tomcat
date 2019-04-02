package ex04.pyrmont.core;

import org.apache.catalina.*;

import javax.naming.directory.DirContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

public class SimpleContainer implements Container {

	public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "how-tomcat-works" + File.separator + "ex04" + File.separator + "webroot";

	@Override
	public void addChild(Container container) {

	}

	@Override
	public void addContainerListener(ContainerListener containerListener) {

	}

	@Override
	public void addMapper(Mapper mapper) {

	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {

	}

	@Override
	public Container findChild(String s) {
		return null;
	}

	@Override
	public Container[] findChildren() {
		return new Container[0];
	}

	@Override
	public Mapper findMapper(String s) {
		return null;
	}

	@Override
	public Mapper[] findMappers() {
		return new Mapper[0];
	}

	@Override
	public Cluster getCluster() {
		return null;
	}

	@Override
	public String getInfo() {
		return null;
	}

	@Override
	public Loader getLoader() {
		return null;
	}

	@Override
	public Logger getLogger() {
		return null;
	}

	@Override
	public Manager getManager() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Container getParent() {
		return null;
	}

	@Override
	public ClassLoader getParentClassLoader() {
		return null;
	}

	@Override
	public Realm getRealm() {
		return null;
	}

	@Override
	public DirContext getResources() {
		return null;
	}

	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		String servletName = ((HttpServletRequest) request).getRequestURI();
		servletName = servletName.substring(servletName.lastIndexOf("/") + 1);
		URLClassLoader loader = null;
		try {
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;
			File classPath = new File(WEB_ROOT);
			String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
			urls[0] = new URL(null, repository, streamHandler);
			loader = new URLClassLoader(urls);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		Class myClass = null;
		try {
			myClass = loader.loadClass(servletName);
		} catch (ClassNotFoundException e) {
			System.out.println(e.toString());
		}

		Servlet servlet;

		try {
			servlet = (Servlet) myClass.newInstance();
			servlet.service((HttpServletRequest) request, (HttpServletResponse) response);
		} catch (Exception e) {
			System.out.println(e.toString());
		} catch (Throwable e) {
			System.out.println(e.toString());
		}
	}

	@Override
	public Container map(Request request, boolean b) {
		return null;
	}

	@Override
	public void removeChild(Container container) {

	}

	@Override
	public void removeContainerListener(ContainerListener containerListener) {

	}

	@Override
	public void removeMapper(Mapper mapper) {

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {

	}

	@Override
	public void setCluster(Cluster cluster) {

	}

	@Override
	public void setLoader(Loader loader) {

	}

	@Override
	public void setLogger(Logger logger) {

	}

	@Override
	public void setManager(Manager manager) {

	}

	@Override
	public void setName(String s) {

	}

	@Override
	public void setParent(Container container) {

	}

	@Override
	public void setParentClassLoader(ClassLoader classLoader) {

	}

	@Override
	public void setRealm(Realm realm) {

	}

	@Override
	public void setResources(DirContext dirContext) {

	}
}
