/**
 * 
 */
package cz.cuni.mff.odcleanstore.application.core;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

/**
 * SecurityManager odcleanstore custom implementation.
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public class SecurityManagerImpl extends SecurityManager {

	// FIXME Not all methods overriden - not full security garanted

	private final static String SETSECURITYMANAGER = "setSecurityManager";

	/**
	 * Build security message for SecurityException.
	 * 
	 * @param name
	 *            Runtime permission name
	 * 
	 * @return Composed message
	 */
	private String buildSecurityExceptionMessage(String name) {
		return String.format("access denied (java.lang.RuntimePermission %s)", name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPermission(java.security.Permission)
	 */
	@Override
	public void checkPermission(Permission perm) {

		if (perm instanceof RuntimePermission) {
			String name = perm.getName();
			if (name.equals(SETSECURITYMANAGER)) {
				throw new SecurityException(buildSecurityExceptionMessage(SETSECURITYMANAGER));
			}
		}

		// super.checkPermission(perm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPermission(java.security.Permission, java.lang.Object)
	 */
	@Override
	public void checkPermission(Permission perm, Object context) {
		// super.checkPermission(perm, context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkAccept(java.lang.String, int)
	 */
	@Override
	public void checkAccept(String host, int port) {
		// super.checkAccept(host, port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkAccess(java.lang.Thread)
	 */
	@Override
	public void checkAccess(Thread t) {
		// super.checkAccess(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkAccess(java.lang.ThreadGroup)
	 */
	@Override
	public void checkAccess(ThreadGroup g) {
		// super.checkAccess(g);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkAwtEventQueueAccess()
	 */
	@Override
	public void checkAwtEventQueueAccess() {
		// super.checkAwtEventQueueAccess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkConnect(java.lang.String, int, java.lang.Object)
	 */
	@Override
	public void checkConnect(String host, int port, Object context) {
		// super.checkConnect(host, port, context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkConnect(java.lang.String, int)
	 */
	@Override
	public void checkConnect(String host, int port) {
		// super.checkConnect(host, port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkCreateClassLoader()
	 */
	@Override
	public void checkCreateClassLoader() {
		// super.checkCreateClassLoader();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkDelete(java.lang.String)
	 */
	@Override
	public void checkDelete(String file) {
		// super.checkDelete(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkExec(java.lang.String)
	 */
	@Override
	public void checkExec(String cmd) {
		// super.checkExec(cmd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkExit(int)
	 */
	@Override
	public void checkExit(int status) {
		// super.checkExit(status);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkLink(java.lang.String)
	 */
	@Override
	public void checkLink(String lib) {
		// super.checkLink(lib);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkListen(int)
	 */
	@Override
	public void checkListen(int port) {
		// super.checkListen(port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkMemberAccess(java.lang.Class, int)
	 */
	@Override
	public void checkMemberAccess(Class<?> clazz, int which) {
		// super.checkMemberAccess(clazz, which);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkMulticast(java.net.InetAddress, byte)
	 */
	// @SuppressWarnings("deprecation")
	@Override
	public void checkMulticast(InetAddress maddr, byte ttl) {
		// super.checkMulticast(maddr, ttl);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkMulticast(java.net.InetAddress)
	 */
	@Override
	public void checkMulticast(InetAddress maddr) {
		// super.checkMulticast(maddr);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPackageAccess(java.lang.String)
	 */
	@Override
	public void checkPackageAccess(String pkg) {
		// super.checkPackageAccess(pkg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPackageDefinition(java.lang.String)
	 */
	@Override
	public void checkPackageDefinition(String pkg) {
		// super.checkPackageDefinition(pkg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPrintJobAccess()
	 */
	@Override
	public void checkPrintJobAccess() {
		// super.checkPrintJobAccess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPropertiesAccess()
	 */
	@Override
	public void checkPropertiesAccess() {
		// super.checkPropertiesAccess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPropertyAccess(java.lang.String)
	 */
	@Override
	public void checkPropertyAccess(String key) {
		// super.checkPropertyAccess(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkRead(java.io.FileDescriptor)
	 */
	@Override
	public void checkRead(FileDescriptor fd) {
		// super.checkRead(fd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkRead(java.lang.String, java.lang.Object)
	 */
	@Override
	public void checkRead(String file, Object context) {
		// super.checkRead(file, context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkRead(java.lang.String)
	 */
	@Override
	public void checkRead(String file) {
		// super.checkRead(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkSecurityAccess(java.lang.String)
	 */
	@Override
	public void checkSecurityAccess(String target) {
		// super.checkSecurityAccess(target);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkSetFactory()
	 */
	@Override
	public void checkSetFactory() {
		// super.checkSetFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkSystemClipboardAccess()
	 */
	@Override
	public void checkSystemClipboardAccess() {
		// super.checkSystemClipboardAccess();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkTopLevelWindow(java.lang.Object)
	 */
	@Override
	public boolean checkTopLevelWindow(Object window) {
		return true;
		// return //super.checkTopLevelWindow(window);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkWrite(java.io.FileDescriptor)
	 */
	@Override
	public void checkWrite(FileDescriptor fd) {
		// super.checkWrite(fd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkWrite(java.lang.String)
	 */
	@Override
	public void checkWrite(String file) {
		// super.checkWrite(file);
	}
}