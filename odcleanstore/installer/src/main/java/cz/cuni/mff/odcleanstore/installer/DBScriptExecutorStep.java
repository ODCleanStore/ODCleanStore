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

public class DBScriptExecutorStep extends InstallationWizardStep {

	private JPanel panel;
	private TextAreaOutputStream taos;
	private JScrollPane scp;
	private GetDbConnectionsStep getDbConnectionsStep;
	private boolean cleanDB;
	private String title;
	private String scriptFileName;
	private String isqlPath;

	private Process isqlProcess;

	protected DBScriptExecutorStep(InstallationWizardFrame wizardFrame, GetDbConnectionsStep getDbConnectionsStep, boolean cleanDB,
			String title, String scriptFileName, String isqlPath) {
		super(wizardFrame);
		this.getDbConnectionsStep = getDbConnectionsStep;
		this.cleanDB = cleanDB;
		this.title = title;
		this.scriptFileName = scriptFileName;
		this.isqlPath = isqlPath != null ? isqlPath : "isql";
	}

	@Override
	public String getStepTitle() {
		return title;
	}

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
								scriptFileName, callBack);
					} else {
						runIsql(getDbConnectionsStep.getDirtyDBHostName(), getDbConnectionsStep.getDirtyDBPort(),
								getDbConnectionsStep.getDirtyDBUser(), getDbConnectionsStep.getDirtyDBPassword(),
								scriptFileName, callBack);
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

	@Override
	public void onFormEvent(ActionEvent arg) {
	}

	@Override
	public void onCancel() {
		if (isqlProcess != null) {
			isqlProcess.destroy();
			isqlProcess = null;

		}
	}

	private void runIsql(String host, String port, String user, String password, String scriptName, Runnable stepCallback)
			throws IOException {
		ProcessBuilder pb = new ProcessBuilder(isqlPath, host + ":" + port, user, password, scriptName);
		
		pb.redirectErrorStream(true);
		isqlProcess = pb.start();

		BufferedReader is;
		String line;

		is = new BufferedReader(new InputStreamReader(isqlProcess.getInputStream(), "UTF-8"));

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
