package cz.cuni.mff.odcleanstore.installer;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cz.cuni.mff.odcleanstore.installer.ui.WizardFrame;
import cz.cuni.mff.odcleanstore.installer.ui.WizardStep;
import cz.cuni.mff.odcleanstore.installer.utils.TextAreaOutputStream;

public class DBScriptExecutorStep extends WizardStep {

	private JPanel panel;
	private TextAreaOutputStream taos;
	private JScrollPane scp;
	private GetDbConnectionsStep getDbConnectionsStep;
	private boolean cleanDB;
	private String title;
	private String nextNavigationButtonText;
	private String scriptFileName;

	private Process isqlProcess;

	protected DBScriptExecutorStep(WizardFrame wizardFrame, GetDbConnectionsStep getDbConnectionsStep, boolean cleanDB,
			String title, String nextNavigationButtonText, String scriptFileName) {
		super(wizardFrame);
		this.getDbConnectionsStep = getDbConnectionsStep;
		this.cleanDB = cleanDB;
		this.title = title;
		this.nextNavigationButtonText = nextNavigationButtonText;
		this.scriptFileName = scriptFileName;
	}

	@Override
	public String getStepTitle() {
		return title;
	}

	@Override
	public String getNextNavigationButtonText() {
		return nextNavigationButtonText;
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
	public boolean hasSkipButton() {
		return true;
	}

	@Override
	public boolean onNext() {
		Thread scriptThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					taos.clear();
					getWizardFrame().startLongRunningOperation();
					runIsql();
				} catch (IOException ex) {
					getWizardFrame().showWarningDialog("Executing script error", "Error");
				} finally {
					getWizardFrame().endLongRunningOperation();
				}
			}
		});
		scriptThread.start();

		return false;
	}

	private void runIsql() throws IOException {
		Runnable callBack = new Runnable() {
			@Override
			public void run() {
				JScrollBar vertical = scp.getVerticalScrollBar();
				vertical.setValue(vertical.getMaximum());
			}
		};

		if (cleanDB) {
			runIsql(getDbConnectionsStep.getCleanDBHostName(), getDbConnectionsStep.getCleanDBPort(),
					getDbConnectionsStep.getCleanDBUser(), getDbConnectionsStep.getCleanDBPassword(), scriptFileName, callBack);
		} else {
			runIsql(getDbConnectionsStep.getDirtyDBHostName(), getDbConnectionsStep.getDirtyDBPort(),
					getDbConnectionsStep.getDirtyDBUser(), getDbConnectionsStep.getDirtyDBPassword(), scriptFileName, callBack);
		}
	}

	@Override
	public void onFormEvent(ActionEvent arg) {
	}

	@Override
	public boolean canCancel() {
		if (isqlProcess != null) {
			if (getWizardFrame().showConfirmDialog("Do you want interrupt script?", "Script interrupting")) {
				isqlProcess.destroy();
				isqlProcess = null;
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	public void runIsql(String host, String port, String user, String password, String scriptName, Runnable stepCallback)
			throws IOException {
		ProcessBuilder pb = new ProcessBuilder("isql", host + ":" + port, user, password, scriptName);
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
			System.err.println(e);
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
