package cz.cuni.mff.odcleanstore.installer.utils;

import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Helpers for working with awt and swing framework.
 * 
 * @author Petr Jerman
 */
public class AwtUtils {
	
    /** Disable constructor for utility class. */
    private AwtUtils() {
    }
    
	/**
	 * Create grid bag constraints object.
	 * 
	 * @param row row of grid bag
	 * @param column column of grid bag
	 * @return Grid bag constraints object
	 */
	public static GridBagConstraints createGbc(int row, int column) {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridy = row;
		gridBagConstraints.gridx = column;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		return gridBagConstraints;
	}

	/**
	 * Create grid bag constraints object.
	 * 
	 * @param row row of grid bag
	 * @param column column of grid bag
	 * @param rowSpan number of occupying rows
	 * @param colSpan number of occupying columns
	 * @return Grid bag constraints object
	 */
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

	/**
	 * Create image button from embedded resource.
	 * 
	 * @param resourceName name of embedded resource
	 * @return
	 */
	public static JButton createImageButton(String resourceName) {
		JButton jbButton = new JButton();
		try {
			Image img = AwtUtils.loadResourceToImage(resourceName);
			jbButton.setIcon(new ImageIcon(img));
		} catch (IOException e) {
		}

		return jbButton;
	}

	/**
	 * Create image from embedde resoprce.
	 * 
	 * @param resourceName name of embedded resource
	 * @return image object
	 * @throws IOException
	 */
	public static Image loadResourceToImage(String resourceName) throws IOException {
		InputStream is = AwtUtils.class.getResourceAsStream(resourceName);
		return ImageIO.read(is);
	}
}
