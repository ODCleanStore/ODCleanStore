package cz.cuni.mff.odcleanstore.installer.utils;

import java.io.OutputStream;

import javax.swing.JTextArea;

/**
 * Class for displaying stream with GUI applications.
 * 
 * @author Petr Jerman
 */
public class TextAreaOutputStream extends OutputStream {

	private JTextArea textArea;

	/**
	 * Initializes new instance for displaying stream with GUI applications. 
	 * 
	 * @param textArea used textarea gui element 
	 */
	public TextAreaOutputStream(JTextArea textArea) {
		this.textArea = textArea;
	}

	/**
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int val) {
		textArea.append(Character.toString((char) val));
	}

	/**
	 * Clear text area.
	 */
	public synchronized void clear() {
		textArea.setText("");
	}
}