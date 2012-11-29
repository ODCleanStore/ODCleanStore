package cz.cuni.mff.odcleanstore.installer;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardFrame;
import cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep;
import cz.cuni.mff.odcleanstore.installer.utils.TextAreaOutputStream;

/**
 * A installer step for execution database script via external isql utility.
 * 
 * @author Petr Jerman
 */
public class DBScriptExecutorStep extends InstallationWizardStep {

	private static final String DEFAULT_ISQL_COMMAND = "isql";
	private static final String INPUTSTREAM_ISQL_CHARSET = "UTF-8";

	private JPanel panel;
	private TextAreaOutputStream taos;
	private JScrollPane scp;
	private GetDbConnectionsStep getDbConnectionsStep;
	private boolean cleanDB;
	private String title;
	private File scriptFile;
	private String isqlPath;

	private Process isqlProcess;

	/**
	 * Create instance of database script executor object
	 * 
	 * @param wizardFrame parent wizard frame
	 * @param getDbConnectionsStep step object with db connection parameters
	 * @param cleanDB run in clean/dirty database flag
	 * @param title title of wizard step
	 * @param scriptFileName name of sql script file
	 * @param isqlPath path to Virtuoso isql utility
	 */
	protected DBScriptExecutorStep(InstallationWizardFrame wizardFrame, GetDbConnectionsStep getDbConnectionsStep,
			boolean cleanDB, String title, String scriptFileName, String isqlPath) {
		super(wizardFrame);
		this.getDbConnectionsStep = getDbConnectionsStep;
		this.cleanDB = cleanDB;
		this.title = title;
		this.scriptFile = new File(App.INSTALL_SQL_SCRIPTS_PATH, scriptFileName);
		this.isqlPath = isqlPath != null ? isqlPath : DEFAULT_ISQL_COMMAND;
	}

	/**
	 * 
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep#getStepTitle()
	 */
	@Override
	public String getStepTitle() {
		return title;
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
		PrintStream ps = new PrintStream(taos);
		System.setOut(ps);
		System.setErr(ps);
		scp = new JScrollPane(ta);
		panel.add(scp);
		return panel;
	}

	/**
	 * 
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep#onNext()
	 */
	@Override
	public boolean onNext() {
		Thread scriptThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					taos.clear();
					Runnable callBack = new Runnable() {
						@Override
						public void run() {
							JScrollBar vertical = scp.getVerticalScrollBar();
							vertical.setValue(vertical.getMaximum());
						}
					};

					if (cleanDB) {
						runIsql(getDbConnectionsStep.getCleanDBHostName(), getDbConnectionsStep.getCleanDBPort(),
								getDbConnectionsStep.getCleanDBUser(), getDbConnectionsStep.getCleanDBPassword(),
								scriptFile, callBack);
					} else {
						runIsql(getDbConnectionsStep.getDirtyDBHostName(), getDbConnectionsStep.getDirtyDBPort(),
								getDbConnectionsStep.getDirtyDBUser(), getDbConnectionsStep.getDirtyDBPassword(),
								scriptFile, callBack);
					}
					getWizardFrame().next();
				} catch (IOException ex) {
					getWizardFrame().cancelInstallation("Executing DB script error");
				}
			}
		});
		scriptThread.start();
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
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardStep#onCancel()
	 */
	@Override
	public void onCancel() {
		if (isqlProcess != null) {
			isqlProcess.destroy();
			isqlProcess = null;

		}
	}

	/**
	 * Run sql script in isql utility.
	 * 
	 * @param host DB host name
	 * @param port DB port
	 * @param user DB user
	 * @param password DB password
	 * @param scriptName name of sql script
	 * @param stepCallback callback called after writing line of text to stdout
	 * @throws IOException
	 */
	private void runIsql(String host, String port, String user, String password, File scriptFile, Runnable stepCallback)
			throws IOException {
		
		if (App.FAKE_DB_CONNECTION) {
			return;
		}
		
		ProcessBuilder pb = new ProcessBuilder(isqlPath, host + ":" + port, user, password, scriptFile.getPath());

		pb.redirectErrorStream(true);
		isqlProcess = pb.start();

		BufferedReader is;
		String line;

		is = new BufferedReader(new InputStreamReader(isqlProcess.getInputStream(), INPUTSTREAM_ISQL_CHARSET));

		while ((line = is.readLine()) != null) {
			System.out.println(line);
			System.out.flush();
			stepCallback.run();
		}

		try {
			if (isqlProcess != null) {
				isqlProcess.waitFor();
			}
		} catch (InterruptedException e) {
			return;
		}

		if (isqlProcess != null) {
			System.err.println("Isql done with exit status " + isqlProcess.exitValue());
			if (isqlProcess.exitValue() != 0) {
				throw new IOException("Isql done with exit status " + isqlProcess.exitValue());
			}
		}
		return;
	}
}
