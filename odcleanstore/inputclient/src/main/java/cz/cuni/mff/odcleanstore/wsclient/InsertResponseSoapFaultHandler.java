package cz.cuni.mff.odcleanstore.wsclient;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class for reading InsertException from SOAP fault message.
 * 
 * @author Petr Jerman
 *
 */
class InsertResponseSoapFaultHandler extends DefaultHandler {
	
	private int level;

	private enum State {ISONPATH, ISONID, ISONMESSAGE, ISONMOREINFO, ISONOTHER}
	private class Node {
		String uri;
		String localName;
		State  state;
		
		Node(String uri, String localName) {
			this.uri = uri;
			this.localName = localName;
			this.state = State.ISONOTHER;
		}
	}
	private Stack<Node> path;
	
	private StringBuilder id, message, moreInfo;
	
	/**
	 * Create InsertResponseSoapFaultHandler object for reading InsertException from SOAP fault message.
	 */
	InsertResponseSoapFaultHandler() {
		level = 0;
		path = new Stack<Node>();
		id = message = moreInfo = null;
	}
	
	/**
	 * @return recognized id member of InsertException
	 */
	public String getId() {
		return id != null ? id.toString() : null;
	}

	/**
	 * @return recognized message member of InsertException
	 */
	public String getMessage() {
		return message != null ? message.toString() : null;
	}

	/**
	 * @return recognized moreInfo member of InsertException
	 */
	public String getMoreInfo() {
		return moreInfo != null ? moreInfo.toString() : null;
	}
	
	/**
	 * Analyze notification of each start element in SOAP message.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch(level) {
			case 0:
				startElement(uri, localName, "http://schemas.xmlsoap.org/soap/envelope/", "Envelope");
				break;
			case 1:
				startElement(uri, localName, "http://schemas.xmlsoap.org/soap/envelope/", "Body");
				break;
			case 2:
				startElement(uri, localName, "http://schemas.xmlsoap.org/soap/envelope/", "Fault");
				break;
			case 3:
				startElement(uri, localName, "", "detail");
				break;
			case 4:
				startElement(uri, localName, "http://inputws.engine.odcleanstore.mff.cuni.cz/", "InsertException");
				break;
			case 5:
				if(path.peek().state == State.ISONPATH) {
					startElement(uri, localName);
					if(uri.equals("") && localName.equals("id")) {
						if (id != null) {
							throw new SAXException();
						}
						id = new StringBuilder();
						path.peek().state = State.ISONID;
					} else if(uri.equals("") && localName.equals("message")) {
						if (message != null) {
							throw new SAXException();
						}
						message = new StringBuilder();
						path.peek().state = State.ISONMESSAGE;
					} else if(uri.equals("") && localName.equals("moreInfo")) {
						if (moreInfo != null) {
							throw new SAXException();
						}
						moreInfo = new StringBuilder();
						path.peek().state = State.ISONMOREINFO;
					}
				} else {
					startElement(uri, localName);
				}
				break;
			default:
				startElement(uri, localName);
		}
	}
	
	/**
	 * Add node to analyzer stack and set node state. 
	 * 
	 * @param uri element prefix 
	 * @param localName  element local name
	 * @param uriInPath element uri which is on InsertException path
	 * @param localNameInPath element local name which is on InsertException path
	 * @throws SAXException
	 */
	private void startElement(String uri, String localName, String uriInPath, String localNameInPath) throws SAXException {
		Node node = new Node(uri, localName);
		
		if((path.isEmpty() || path.peek().state == State.ISONPATH) && uri.equals(uriInPath) && localName.equals(localNameInPath)) {
			node.state = State.ISONPATH;
		}
		path.push(node);
		level++;
	}
	
	/**
	 * Add node to analyzer stack with ISONOTHER state. 
     *     
	 * @param uri element prefix
	 * @param localName element local name
	 * @throws SAXException
	 */
	private void startElement(String uri, String localName) throws SAXException {
		Node node = new Node(uri, localName);
		path.push(node);
		level++;
	}

	/** 
	 * Extract id, message and moreinfo of InsertException from SOAP.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		State state = path.peek().state;
		if (state == State.ISONID) {
			id.append(ch, start, length);
		}
		else if  (state == State.ISONMESSAGE) {
			message.append(ch, start, length);
		}
		else if  (state == State.ISONMOREINFO) {
			moreInfo.append(ch, start, length);
		}
	}

	/**
	 * Remove node from analyzer stack and check if element is properly ended.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		Node node = path.pop();
		level--;
		if (!uri.equals(node.uri) || !localName.equals(node.localName)) {
			throw new SAXException();
		}
	}
}
