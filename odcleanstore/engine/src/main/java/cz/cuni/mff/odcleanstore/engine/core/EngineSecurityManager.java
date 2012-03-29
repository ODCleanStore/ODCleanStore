/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.core;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

/**
 * SecurityManager odcleanstore custom implementation.
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public class EngineSecurityManager extends SecurityManager {

	// FIXME All deprecated methods must be overridden !!!

	private final static String SECURITYEXCEPTIONMESSAGE = "acces denied";

	private final static String SETSECURITYMANAGERPERMISSIONNAME = "setSecurityManager";
	private final static String SETCONTEXTCLASSLOADERPERMISSIONNAME = "setContextClassLoader";

	private final static String[] PRIVILEGEDCLASSNAMES = { "java.lang.ClassLoader",
			"cz.cuni.mff.odcleanstore.engine.core.CustomModuleRunner$CustomModuleClassLoader" };

	private SecurityManager _originalSecurityManager;

	public EngineSecurityManager() {

		_originalSecurityManager = System.getSecurityManager();
	}

	public void setAsSecurityManager() {

		System.setSecurityManager(this);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkPermission(java.security.Permission)
	 */
	@Override
	public void checkPermission(Permission perm) {

		if (perm instanceof RuntimePermission) {
			String name = perm.getName();

			if (name.equals(SETSECURITYMANAGERPERMISSIONNAME))
				throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

			if (name.equals(SETCONTEXTCLASSLOADERPERMISSIONNAME))
				throw new SecurityException(SECURITYEXCEPTIONMESSAGE);
		}

		if (checkPrivilegedClasses())
			return;

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkPermission(perm);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkPermission(java.security.Permission, java.lang.Object)
	 */
	@Override
	public void checkPermission(Permission perm, Object context) {

		if (checkPrivilegedClasses())
			return;

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkPermission(perm, context);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkAccept(java.lang.String, int)
	 */
	@Override
	public void checkAccept(String host, int port) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkAccept(host, port);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkAccess(java.lang.Thread)
	 */
	@Override
	public void checkAccess(Thread t) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkAccess(t);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkAccess(java.lang.ThreadGroup)
	 */
	@Override
	public void checkAccess(ThreadGroup g) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkAccess(g);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkAwtEventQueueAccess()
	 */
	@Override
	public void checkAwtEventQueueAccess() {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkAwtEventQueueAccess();
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkConnect(java.lang.String, int, java.lang.Object)
	 */
	@Override
	public void checkConnect(String host, int port, Object context) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkConnect(host, port, context);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkConnect(java.lang.String, int)
	 */
	@Override
	public void checkConnect(String host, int port) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkConnect(host, port);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkCreateClassLoader()
	 */
	@Override
	public void checkCreateClassLoader() {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkCreateClassLoader();
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkDelete(java.lang.String)
	 */
	@Override
	public void checkDelete(String file) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkDelete(file);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkExec(java.lang.String)
	 */
	@Override
	public void checkExec(String cmd) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkExec(cmd);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkExit(int)
	 */
	@Override
	public void checkExit(int status) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkExit(status);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkLink(java.lang.String)
	 */
	@Override
	public void checkLink(String lib) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkLink(lib);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkListen(int)
	 */
	@Override
	public void checkListen(int port) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkListen(port);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkMemberAccess(java.lang.Class, int)
	 */
	@Override
	public void checkMemberAccess(Class<?> clazz, int which) {

		if (checkPrivilegedClasses())
			return;

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkMemberAccess(clazz, which);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkMulticast(java.net.InetAddress, byte)
	 */

	@Deprecated
	@Override
	public void checkMulticast(InetAddress maddr, byte ttl) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkMulticast(maddr, ttl);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkMulticast(java.net.InetAddress)
	 */
	@Override
	public void checkMulticast(InetAddress maddr) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkMulticast(maddr);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkPackageAccess(java.lang.String)
	 */
	@Override
	public void checkPackageAccess(String pkg) {

		if (checkPrivilegedClasses())
			return;

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkPackageAccess(pkg);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkPackageDefinition(java.lang.String)
	 */
	@Override
	public void checkPackageDefinition(String pkg) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkPackageDefinition(pkg);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkPrintJobAccess()
	 */
	@Override
	public void checkPrintJobAccess() {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkPrintJobAccess();
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkPropertiesAccess()
	 */
	@Override
	public void checkPropertiesAccess() {

		if (checkPrivilegedClasses())
			return;

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkPropertiesAccess();
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkPropertyAccess(java.lang.String)
	 */
	@Override
	public void checkPropertyAccess(String key) {

		if (checkPrivilegedClasses())
			return;

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkPropertyAccess(key);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkRead(java.io.FileDescriptor)
	 */
	@Override
	public void checkRead(FileDescriptor fd) {

		if (checkPrivilegedClasses())
			return;

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkRead(fd);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkRead(java.lang.String, java.lang.Object)
	 */
	@Override
	public void checkRead(String file, Object context) {

		if (checkPrivilegedClasses())
			return;

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkRead(file, context);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkRead(java.lang.String)
	 */
	@Override
	public void checkRead(String file) {

		if (checkPrivilegedClasses())
			return;

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkRead(file);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkSecurityAccess(java.lang.String)
	 */
	@Override
	public void checkSecurityAccess(String target) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkSecurityAccess(target);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkSetFactory()
	 */
	@Override
	public void checkSetFactory() {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkSetFactory();
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkSystemClipboardAccess()
	 */
	@Override
	public void checkSystemClipboardAccess() {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkSystemClipboardAccess();
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkTopLevelWindow(java.lang.Object)
	 */
	@Override
	public boolean checkTopLevelWindow(Object window) {

		if (CustomModuleRunner.isInCustomModuleThread())
			return false;

		if (_originalSecurityManager != null)
			return _originalSecurityManager.checkTopLevelWindow(window);

		return true;
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkWrite(java.io.FileDescriptor)
	 */
	@Override
	public void checkWrite(FileDescriptor fd) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkWrite(fd);
	}

	/**
	 * 
	 * @see java.lang.SecurityManager#checkWrite(java.lang.String)
	 */
	@Override
	public void checkWrite(String file) {

		if (CustomModuleRunner.isInCustomModuleThread())
			throw new SecurityException(SECURITYEXCEPTIONMESSAGE);

		if (_originalSecurityManager != null)
			_originalSecurityManager.checkWrite(file);
	}

	private boolean checkPrivilegedClasses() {

		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {

			String className = ste.getClassName();

			for (String privilegedClassName : PRIVILEGEDCLASSNAMES) {
				if (privilegedClassName.equals(className))
					return true;
			}
		}

		return false;
	}
}