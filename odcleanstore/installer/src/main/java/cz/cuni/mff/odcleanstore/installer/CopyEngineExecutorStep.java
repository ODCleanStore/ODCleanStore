package cz.cuni.mff.odcleanstore.installer;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardFrame;
import cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep;
import cz.cuni.mff.odcleanstore.installer.utils.FileUtils;
import cz.cuni.mff.odcleanstore.installer.utils.TextAreaOutputStream;

/**
 * A installer step for copying files from Engine source subdirectorty to destination directory.
 * 
 * @author Petr Jerman
 */
public class CopyEngineExecutorStep extends InstallationWizardStep {

	private static final String ENGINE_SRC_PATH = "Engine";
	private JPanel panel;
	private JScrollPane scp;
	private TextAreaOutputStream taos;
	private File dstDirectory;

	/**
	 * Create CopyEngineExecutorStep instance.
	 * 
	 * @param wizardFrame parent wizard frame
	 * @param dstDirectory destination directory of engine
	 */
	protected CopyEngineExecutorStep(InstallationWizardFrame wizardFrame, File dstDirectory) {
		super(wizardFrame);
		this.dstDirectory = dstDirectory;
	}

	/**
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep#getStepTitle()
	 */
	@Override
	public String getStepTitle() {
		return "copy engine files - all existing files will be replaced";
	}

	/**
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep#getFormPanel()
	 */
	@Override
	public JPanel getFormPanel() {
		panel = new JPanel();
		panel.setLayout(new FlowLayout());

		JTextArea ta = new JTextArea(28, 68);
		ta.setEditable(false);
		taos = new TextAreaOutputStream(ta);
		taos.clear();
		PrintStream ps = new PrintStream(taos);
		System.setOut(ps);
		System.setErr(ps);
		scp = new JScrollPane(ta);
		panel.add(scp);
		return panel;
	}

	/**
	 * Copying engine files with own thread.
	 * 
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep#onNext()
	 */
	@Override
	public boolean onNext() {
		Thread copyThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					taos.clear();
					FileUtils.copyFolder(new File(ENGINE_SRC_PATH), new File(dstDirectory.getAbsolutePath()), new Runnable() {
						@Override
						public void run() {
							JScrollBar vertical = scp.getVerticalScrollBar();
							vertical.setValue(vertical.getMaximum());
						}
					});
					getWizardFrame().next();
				} catch (IOException ex) {
					ex.printStackTrace();
					getWizardFrame().cancelInstallation("Copying engine files error");

				}
			}
		});
		copyThread.start();
		return false;
	}

	/**
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep#onFormEvent(java.awt.event.ActionEvent)
	 */
	@Override
	public void onFormEvent(ActionEvent arg) {
	}
}
