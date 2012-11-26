package cz.cuni.mff.odcleanstore.installer.utils;

import java.io.OutputStream;

import javax.swing.JTextArea;

public class TextAreaOutputStream extends OutputStream {

	private JTextArea textArea;

	public TextAreaOutputStream(JTextArea textArea) {
		this.textArea = textArea;
	}

	@Override
	public void write(int val) {
		textArea.append(Character.toString((char)val));
	}
	
	public synchronized void clear() {
		textArea.setText("");
	}
}