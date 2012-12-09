package cz.cuni.mff.odcleanstore.comlib;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Factory of sockets with support of ssl.
 * 
 * @author Petr Jerman
 */
public class SocketFactory {

	private SocketFactory() {
	}

	/**
	 * Create non sll server socket.
	 * 
	 * @param hostName
	 * @param port
	 * @return
	 * @throws IOException
	 */
	public static ServerSocket createServerSocket(String hostName, int port) throws IOException {
		ServerSocket serverSocket = new ServerSocket();
		InetSocketAddress endpoint = new InetSocketAddress(hostName, port);
		serverSocket.bind(endpoint);
		return serverSocket;
	}

	/**
	 * Create ssl server socket.
	 * 
	 * @param hostName
	 * @param port
	 * @param keyManagers
	 * @return
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public static SSLServerSocket createSSLServerSocket(String hostName, int port, KeyManager[] keyManagers)
			throws IOException, KeyManagementException, NoSuchAlgorithmException {

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(keyManagers, null, null);
		SSLServerSocketFactory ssf = sc.getServerSocketFactory();
		SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket();

		InetSocketAddress endpoint = new InetSocketAddress(hostName, port);
		serverSocket.bind(endpoint);
		return serverSocket;
	}

	/**
	 * 
	 * Create non ssl client socket.
	 * 
	 * @param hostName
	 * @param port
	 * @return
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static Socket createClientSocket(String hostName, int port) throws IOException, UnknownHostException {
		return new Socket(hostName, port);
	}

	/**
	 * Create ssl client socket which trust all certificates.
	 * 
	 * @param hostName
	 * @param port
	 * @return
	 * @throws IOException
	 * @throws UnknownHostException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	public static SSLSocket createAllTrustSSLSocket(String hostName, int port)
			throws IOException, UnknownHostException, KeyManagementException, NoSuchAlgorithmException {

		satisfyTrustAllCerts();

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, trustAllCerts, new SecureRandom());
		SSLSocketFactory f = sc.getSocketFactory();

		SSLSocket socket = (SSLSocket) f.createSocket(hostName, port);
		socket.startHandshake();

		return socket;
	}

	/**
	 * Create static trustAllCerts object.
	 */
	private static synchronized void satisfyTrustAllCerts() {
		if (trustAllCerts == null) {
			trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };
		}
	}

	private static TrustManager[] trustAllCerts;
}
