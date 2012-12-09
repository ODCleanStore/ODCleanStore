package cz.cuni.mff.odcleanstore.installer;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardFrame;
import cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep;
import cz.cuni.mff.odcleanstore.installer.utils.AwtUtils;
import cz.cuni.mff.odcleanstore.installer.utils.FileUtils;
import cz.cuni.mff.odcleanstore.installer.utils.FileUtils.DirectoryException;

/**
 * A step for getting destination engine directory from user.
 * 
 * @author Petr Jerman
 */
public class GetEngineDirectoryStep extends InstallationWizardStep {

	private JPanel panel;
	private JLabel jlbDirectory;
	private JTextField jtfDirectory;
	private JButton jbDirectory;

	/**
	 * Create instance for getting destination engine directory from user. 
	 * 
	 * @param wizardFrame parent frame wizard
	 */
	protected GetEngineDirectoryStep(InstallationWizardFrame wizardFrame) {
		super(wizardFrame);
	}

	/**
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep#getStepTitle()
	 */
	@Override
	public String getStepTitle() {
		return "setting the engine directory";
	}

	/**
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep#getFormPanel()
	 */
	@Override
	public JPanel getFormPanel()  {

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));

		jlbDirectory = new JLabel("Engine directory:");
		panel.add(jlbDirectory);
		jtfDirectory = new JTextField(53);
		panel.add(jtfDirectory);
		jbDirectory = AwtUtils.createImageButton("/folder.png");
		jbDirectory.setToolTipText("Choose engine directory");
		panel.add(jbDirectory);
		jbDirectory.addActionListener(getWizardFrame().getActionListener());

		return panel;
	}

	/**
	 * Validate engine destination directory from user.
	 * 
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep#onNext()
	 */
	@Override
	public boolean onNext() {
		String dirName = jtfDirectory.getText();
		if (dirName.isEmpty()) {
			getWizardFrame().showWarningDialog("Engine directory is empty - enter it", "Error");
			return false;
		}

		File file = new File(dirName);
		if (!file.exists()) {
			String message = String.format("Create directory %s?", file.getAbsolutePath());
			if (getWizardFrame().showConfirmDialog(message, "Creating not existing engine directory")) {
				try {
					FileUtils.satisfyDirectory(dirName);
					if (!file.exists()) {
						String messageError = String.format("Error creating %s directory.", file.getAbsolutePath());
						getWizardFrame().showWarningDialog(messageError, "Creating not existing engine directory");
						return false;
					}
				} catch (DirectoryException e) {
					String messageError = String.format("Error creating %s directory.", file.getAbsolutePath());
					getWizardFrame().showWarningDialog(messageError, "Creating not existing engine directory");
					return false;
				}
			} else {
				return false;
			}
		}
		if (file.list().length > 0) {
			if (!getWizardFrame().showConfirmDialog("Directory is not empty,\n do you want to continue?", "warning")) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep#onFormEvent(java.awt.event.ActionEvent)
	 */
	@Override
	public void onFormEvent(ActionEvent arg) {
		if (arg.getSource() == jbDirectory) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fc.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
				try {
					String path = fc.getSelectedFile().getCanonicalPath();
					jtfDirectory.setText(path);
				} catch (IOException e) {
					jtfDirectory.setText("");
				}
			}
		}
	}

	/**
	 * @return engine directory
	 */
	public File getEngineDirectory() {
		return new File(jtfDirectory.getText());
	}
}
