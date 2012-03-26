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

	// FIXME Replace Strings in PRIVILEGEDCLASSNAMES !!!

	private final static String[] PRIVILEGEDCLASSNAMES = { "java.lang.ClassLoader",
			"cz.cuni.mff.odcleanstore.engine.core.CustomModuleRunner$CustomModuleClassLoader",
			"cz.cuni.mff.odcleanstore.engine.core.CustomModuleRunner$ThreadRunner" };

	private SecurityManager _originalSecurityManager;

	public EngineSecurityManager() {

		// TODO add exception handling

		_originalSecurityManager = System.getSecurityManager();
		System.setSecurityManager(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPermission(java.security.Permission)
	 */
	@Override
	public void checkPermission(Permission perm) {
		checkCustomModuleAccessForPrivilegedClasses(true);

		if (perm instanceof RuntimePermission) {
			String name = perm.getName();
			if (name.equals(SETSECURITYMANAGERPERMISSIONNAME)) {
				throw new SecurityException(SECURITYEXCEPTIONMESSAGE);
			}
		}

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkPermission(perm);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPermission(java.security.Permission, java.lang.Object)
	 */
	@Override
	public void checkPermission(Permission perm, Object context) {
		checkCustomModuleAccessForPrivilegedClasses(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkPermission(perm, context);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkAccept(java.lang.String, int)
	 */
	@Override
	public void checkAccept(String host, int port) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkAccept(host, port);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkAccess(java.lang.Thread)
	 */
	@Override
	public void checkAccess(Thread t) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkAccess(t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkAccess(java.lang.ThreadGroup)
	 */
	@Override
	public void checkAccess(ThreadGroup g) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkAccess(g);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkAwtEventQueueAccess()
	 */
	@Override
	public void checkAwtEventQueueAccess() {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkAwtEventQueueAccess();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkConnect(java.lang.String, int, java.lang.Object)
	 */
	@Override
	public void checkConnect(String host, int port, Object context) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkConnect(host, port, context);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkConnect(java.lang.String, int)
	 */
	@Override
	public void checkConnect(String host, int port) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkConnect(host, port);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkCreateClassLoader()
	 */
	@Override
	public void checkCreateClassLoader() {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkCreateClassLoader();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkDelete(java.lang.String)
	 */
	@Override
	public void checkDelete(String file) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkDelete(file);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkExec(java.lang.String)
	 */
	@Override
	public void checkExec(String cmd) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkExec(cmd);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkExit(int)
	 */
	@Override
	public void checkExit(int status) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkExit(status);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkLink(java.lang.String)
	 */
	@Override
	public void checkLink(String lib) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkLink(lib);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkListen(int)
	 */
	@Override
	public void checkListen(int port) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkListen(port);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkMemberAccess(java.lang.Class, int)
	 */
	@Override
	public void checkMemberAccess(Class<?> clazz, int which) {
		checkCustomModuleAccessForPrivilegedClasses(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkMemberAccess(clazz, which);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkMulticast(java.net.InetAddress, byte)
	 */

	@Deprecated
	@Override
	public void checkMulticast(InetAddress maddr, byte ttl) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkMulticast(maddr, ttl);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkMulticast(java.net.InetAddress)
	 */
	@Override
	public void checkMulticast(InetAddress maddr) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkMulticast(maddr);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPackageAccess(java.lang.String)
	 */
	@Override
	public void checkPackageAccess(String pkg) {
		checkCustomModuleAccessForPrivilegedClasses(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkPackageAccess(pkg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPackageDefinition(java.lang.String)
	 */
	@Override
	public void checkPackageDefinition(String pkg) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkPackageDefinition(pkg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPrintJobAccess()
	 */
	@Override
	public void checkPrintJobAccess() {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkPrintJobAccess();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPropertiesAccess()
	 */
	@Override
	public void checkPropertiesAccess() {
		checkCustomModuleAccessForPrivilegedClasses(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkPropertiesAccess();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkPropertyAccess(java.lang.String)
	 */
	@Override
	public void checkPropertyAccess(String key) {
		checkCustomModuleAccessForPrivilegedClasses(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkPropertyAccess(key);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkRead(java.io.FileDescriptor)
	 */
	@Override
	public void checkRead(FileDescriptor fd) {
		checkCustomModuleAccessForPrivilegedClasses(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkRead(fd);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkRead(java.lang.String, java.lang.Object)
	 */
	@Override
	public void checkRead(String file, Object context) {
		checkCustomModuleAccessForPrivilegedClasses(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkRead(file, context);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkRead(java.lang.String)
	 */
	@Override
	public void checkRead(String file) {
		checkCustomModuleAccessForPrivilegedClasses(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkRead(file);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkSecurityAccess(java.lang.String)
	 */
	@Override
	public void checkSecurityAccess(String target) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkSecurityAccess(target);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkSetFactory()
	 */
	@Override
	public void checkSetFactory() {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkSetFactory();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkSystemClipboardAccess()
	 */
	@Override
	public void checkSystemClipboardAccess() {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkSystemClipboardAccess();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkTopLevelWindow(java.lang.Object)
	 */
	@Override
	public boolean checkTopLevelWindow(Object window) {
		if (!checkCustomModuleAccess(false))
			return false;

		if (_originalSecurityManager != null) {
			return _originalSecurityManager.checkTopLevelWindow(window);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkWrite(java.io.FileDescriptor)
	 */
	@Override
	public void checkWrite(FileDescriptor fd) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkWrite(fd);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.SecurityManager#checkWrite(java.lang.String)
	 */
	@Override
	public void checkWrite(String file) {
		checkCustomModuleAccess(true);

		if (_originalSecurityManager != null) {
			_originalSecurityManager.checkWrite(file);
		}
	}

	private boolean checkCustomModuleAccess(boolean throwSecurityException) {
		if (CustomModuleRunner.isInCustomModuleThread()) {
			if (throwSecurityException) {
				throw new SecurityException(SECURITYEXCEPTIONMESSAGE);
			} else {
				return false;
			}
		}

		return true;
	}

	private boolean checkCustomModuleAccessForPrivilegedClasses(boolean throwSecurityException) {
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			String className = ste.getClassName();
			for (String privilegedClassName : PRIVILEGEDCLASSNAMES) {
				if (privilegedClassName.equals(className)) {
					return true;
				}
			}
		}

		return checkCustomModuleAccess(throwSecurityException);
	}
}