package cz.cuni.mff.odcleanstore.installer.utils;

import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class AwtUtils {

	public static GridBagConstraints createGbc(int row, int column) {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = row;
		gridBagConstraints.gridx = column;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		return gridBagConstraints;
	}

	public static GridBagConstraints createGbc(int row, int column, int rowSpan, int colSpan) {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = row;
		gridBagConstraints.gridx = column;
		gridBagConstraints.gridwidth = rowSpan;
		gridBagConstraints.gridheight = colSpan;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		return gridBagConstraints;
	}

	public static JButton createImageButton(String resourceName) {
		JButton jbButton = new JButton();
		try {
			Image img = AwtUtils.loadResourceToImage(resourceName);
			jbButton.setIcon(new ImageIcon(img));
		} catch (IOException e) {
		}

		return jbButton;
	}

	public static Image loadResourceToImage(String resourceName) throws IOException {
		InputStream is = AwtUtils.class.getResourceAsStream(resourceName);
		return ImageIO.read(is);
	}
}
