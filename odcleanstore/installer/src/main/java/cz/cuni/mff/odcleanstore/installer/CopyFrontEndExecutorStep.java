package cz.cuni.mff.odcleanstore.installer;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardFrame;
import cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep;
import cz.cuni.mff.odcleanstore.installer.utils.TextAreaOutputStream;

/**
 * A installer step for repacking odcleanstore front end war file to destination directory with writing destination path to
 * engine odcs.ini to war file.
 * 
 * @author Petr Jerman
 */
public class CopyFrontEndExecutorStep extends InstallationWizardStep {

	private static final String WEB_INF_CLASSES_CONFIG_APPLICATION_PROPERTIES = "WEB-INF/classes/config/application.properties";
	private static final String ODCS_CONFIG_PATH_PROPERTY_NAME = "odcs.config.path";

	private static final byte[] BUFFER = new byte[4096 * 1024];

	private JPanel panel;
	private JScrollPane scp;
	private TextAreaOutputStream taos;
	private GetEngineDirectoryStep getEngineDirectoryStep;
	private GetFrontendDirectoryStep getFrontendDirectoryStep;

	/**
	 * Create CopyFrontEndExecutorStep instance.
	 * 
	 * @param wizardFrame parent wizard frame
	 * @param getFrontendDirectoryStep front end directory step
	 * @param getEngineDirectoryStep engine directory step
	 */
	protected CopyFrontEndExecutorStep(InstallationWizardFrame wizardFrame, GetFrontendDirectoryStep getFrontendDirectoryStep,
			GetEngineDirectoryStep getEngineDirectoryStep) {
		super(wizardFrame);
		this.getFrontendDirectoryStep = getFrontendDirectoryStep;
		this.getEngineDirectoryStep = getEngineDirectoryStep;
	}

	/**
	 * 
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep#getStepTitle()
	 */
	@Override
	public String getStepTitle() {
		return "copy frontend war to destination directory and save path to odcs.ini to front end war - existing file will be replaced";
	}

	/**
	 * 
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
		scp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scp);
		return panel;
	}

	/**
	 * Repacks front end war file from source to destination with writing configuration operations in own thread.
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
					File srcFile = new File(App.FRONTEND_DIR_PATH, App.FRONTEND_ARCHIVE_FILENAME);
					File dstFile = new File(getFrontendDirectoryStep.getFrontendDirectory().getAbsolutePath(),
							App.FRONTEND_ARCHIVE_FILENAME).getCanonicalFile();
					File odcsini = new File(getEngineDirectoryStep.getEngineDirectory().getAbsolutePath(), App.ODCS_INI_FILENAME);
					installFE(srcFile, dstFile, odcsini, new Runnable() {
						@Override
						public void run() {
							JScrollBar vertical = scp.getVerticalScrollBar();
							vertical.setValue(vertical.getMaximum());
						}
					});
					getWizardFrame().next();
				} catch (IOException ex) {
					getWizardFrame().cancelInstallation("Copying front end war error");
				}
			}
		});
		copyThread.start();
		return false;
	}

	/**
	 * 
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep#onFormEvent(java.awt.event.ActionEvent)
	 */
	@Override
	public void onFormEvent(ActionEvent arg) {
	}

	/**
	 * Repacks front end war file from source to destination with writing configuration operations.
	 * 
	 * @param srcWarFileName source war file name
	 * @param dstWarFileName destination war file name
	 * @param odcsIniFileName odcs.ini file name in engine directory
	 * @param stepCallback callback called after each copy step
	 * @throws IOException
	 */
	private static void installFE(File srcWarFile, File dstWarFile, File odcsIniFile, Runnable stepCallback)
			throws IOException {
		JarFile war = null;
		JarOutputStream append = null;

		try {
			war = new JarFile(srcWarFile);
			append = new JarOutputStream(new FileOutputStream(dstWarFile));

			Enumeration<? extends JarEntry> entries = war.entries();
			while (entries.hasMoreElements()) {
				JarEntry e = entries.nextElement();
				if (!e.isDirectory()) {
					if (e.getName().equalsIgnoreCase(WEB_INF_CLASSES_CONFIG_APPLICATION_PROPERTIES)) {
						append.putNextEntry(new JarEntry(e.getName()));
						String escapedOdcsIniPath = odcsIniFile.getAbsolutePath().replace("\\", "\\\\");
						String appString = ODCS_CONFIG_PATH_PROPERTY_NAME + " = " + escapedOdcsIniPath;
						copy(appString, append);
					} else {
						append.putNextEntry(e);
						copy(war.getInputStream(e), append);
					}
					System.out.format("copy %s\n", e.getName());
					stepCallback.run();
				}
				append.closeEntry();
			}
		} finally {
			if (war != null) {
				war.close();
			}
			if (append != null) {
				append.close();
			}
		}
	}

	/**
	 * Copying input stream to output stream via buffer.
	 * 
	 * @param input input stream
	 * @param output output stream
	 * @throws IOException
	 */
	private static void copy(InputStream input, OutputStream output) throws IOException {
		int bytesRead;
		while ((bytesRead = input.read(BUFFER)) != -1) {
			output.write(BUFFER, 0, bytesRead);
		}
	}

	/**
	 * Copying input string to output stream.
	 * 
	 * @param input input string
	 * @param output output stream
	 * @throws IOException
	 */
	private static void copy(String input, OutputStream output) throws IOException {
		output.write(input.getBytes());
	}
}