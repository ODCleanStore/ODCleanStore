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

import cz.cuni.mff.odcleanstore.installer.ui.WizardFrame;
import cz.cuni.mff.odcleanstore.installer.ui.WizardStep;
import cz.cuni.mff.odcleanstore.installer.utils.TextAreaOutputStream;

public class CopyFrontEndExecutorStep extends WizardStep {
	
	private static final String ODCS_INI_FILE_NAME = "odcs.ini";
	private static final String ODCS_WEBFRONTEND_WAR_FILE_NAME = "odcs-webfrontend.war";
	private static final String WEB_INF_CLASSES_CONFIG_APPLICATION_PROPERTIES = "WEB-INF/classes/config/application.properties";
	private static final String ODCS_CONFIG_PATH_PROPERTY_NAME = "odcs.config.path";

	private static final byte[] BUFFER = new byte[4096 * 1024];

	private JPanel panel;
	private JScrollPane scp;
	private TextAreaOutputStream taos;
	private GetEngineDirectoryStep getEngineDirectoryStep;
	private GetFrontendDirectoryStep getFrontendDirectoryStep;

	protected CopyFrontEndExecutorStep(WizardFrame wizardFrame, GetFrontendDirectoryStep getFrontendDirectoryStep, GetEngineDirectoryStep getEngineDirectoryStep) {
		super(wizardFrame);
		this.getFrontendDirectoryStep = getFrontendDirectoryStep;
		this.getEngineDirectoryStep = getEngineDirectoryStep;
	}

	@Override
	public String getStepTitle() {
		return "copy frontend war to destination directory and save path to odcs.ini to front end war - existing file will be replaced";
	}

	@Override
	public String getNextNavigationButtonText() {
		return "Copy frontend war to destination directory and save path to odcs.ini to front end war";
	}
	
	@Override
	public boolean hasSkipButton() {
		return true;
	}

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

	@Override
	public boolean onNext() {
		Thread copyThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					taos.clear();
					getWizardFrame().startLongRunningOperation();
					String src = ODCS_WEBFRONTEND_WAR_FILE_NAME;
					String dst = new File(getFrontendDirectoryStep.getFrontendDirectory().getAbsolutePath(), ODCS_WEBFRONTEND_WAR_FILE_NAME).getCanonicalPath();
					String odcsini = new File(getEngineDirectoryStep.getEngineDirectory().getAbsolutePath(), ODCS_INI_FILE_NAME).getCanonicalPath();
					installFE(src, dst, odcsini,  new Runnable() {
						@Override
						public void run() {
							JScrollBar vertical = scp.getVerticalScrollBar();
							vertical.setValue(vertical.getMaximum());
						}
					});
					getWizardFrame().showInfoDialog("Copying front end war ok", "Information");
					getWizardFrame().next();
				} catch (IOException ex) {
					getWizardFrame().showWarningDialog("Copying front end war error", "Error");
				} finally {
					getWizardFrame().endLongRunningOperation();
				}
			}
		});
		copyThread.start();

		return false;
	}

	@Override
	public void onFormEvent(ActionEvent arg) {
	}
	
	private static void installFE(String srcWarFileName, String dstWarFileName, String odcsIniFileName, Runnable stepCallback) throws IOException {
		JarFile war = new JarFile(srcWarFileName);
		JarOutputStream append = new JarOutputStream(new FileOutputStream(dstWarFileName));

		try {
			war = new JarFile(srcWarFileName);
			append = new JarOutputStream(new FileOutputStream(dstWarFileName));

			Enumeration<? extends JarEntry> entries = war.entries();
			while (entries.hasMoreElements()) {
				JarEntry e = entries.nextElement();
				if (!e.isDirectory()) {
					if (e.getName().equalsIgnoreCase(WEB_INF_CLASSES_CONFIG_APPLICATION_PROPERTIES)) {
						append.putNextEntry(new JarEntry(e.getName()));
						String appString = ODCS_CONFIG_PATH_PROPERTY_NAME + " = " + odcsIniFileName;
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
			war.close();
			append.close();
		}
	}

	private static void copy(InputStream input, OutputStream output) throws IOException {
		int bytesRead;
		while ((bytesRead = input.read(BUFFER)) != -1) {
			output.write(BUFFER, 0, bytesRead);
		}
	}

	private static void copy(String input, OutputStream output) throws IOException {
		output.write(input.getBytes());
	}
}