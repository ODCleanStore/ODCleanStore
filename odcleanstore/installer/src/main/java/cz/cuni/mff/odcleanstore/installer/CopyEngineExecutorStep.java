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

import cz.cuni.mff.odcleanstore.installer.ui.WizardFrame;
import cz.cuni.mff.odcleanstore.installer.ui.WizardStep;
import cz.cuni.mff.odcleanstore.installer.utils.FileUtils;
import cz.cuni.mff.odcleanstore.installer.utils.TextAreaOutputStream;

public class CopyEngineExecutorStep extends WizardStep {

	private static final String ENGINE_SRC_PATH = "Engine";
	private JPanel panel;
	private JScrollPane scp;
	private TextAreaOutputStream taos;
	private File dstDirectory;

	protected CopyEngineExecutorStep(WizardFrame wizardFrame, File dstDirectory) {
		super(wizardFrame);
		this.dstDirectory = dstDirectory;
	}

	@Override
	public String getStepTitle() {
		return "copy engine files to engine directory - all existing files will be replaced";
	}

	@Override
	public String getNextNavigationButtonText() {
		return "Copy engine files to engine directory";
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
					FileUtils.copyFolder(new File(ENGINE_SRC_PATH), new File(dstDirectory.getAbsolutePath()), new Runnable() {
						@Override
						public void run() {
							JScrollBar vertical = scp.getVerticalScrollBar();
							vertical.setValue(vertical.getMaximum());
						}
					});
					getWizardFrame().showInfoDialog("Copying engine files ok", "Information");
					getWizardFrame().next();
				} catch (IOException ex) {
					getWizardFrame().showWarningDialog("Copying engine files error", "Error");
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
}
