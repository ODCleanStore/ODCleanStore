package cz.cuni.mff.odcleanstore.installer;

import java.io.OutputStream;

import javax.swing.JTextArea;

public class TextAreaOutputStream extends OutputStream {

	private JTextArea textArea;

	public TextAreaOutputStream(JTextArea textArea) {
		this.textArea = textArea;
	}

	public void write(int val) {
		textArea.append(Character.toString((char)val));
	}
	
	public synchronized void clear() {
		textArea.setText("");
	}
}