package cz.cuni.mff.odcleanstore.installer;

import java.io.IOException;
import javax.swing.*;
import java.io.*;

public class App {

	public static void main(String[] args) throws IOException {
		
		JFrame frame = new JFrame();
		frame.setSize(800, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTextArea ta = new JTextArea();
		TextAreaOutputStream taos = new TextAreaOutputStream(ta);
		PrintStream ps = new PrintStream(taos);
		System.setOut(ps);
		System.setErr(ps);

		frame.add(new JScrollPane(ta));
		frame.setVisible(true);
		
		InstallerUtils.runIsql();
	}

}
