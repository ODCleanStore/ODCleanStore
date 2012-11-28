package cz.cuni.mff.odcleanstore.installer;

import java.io.IOException;

import cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardFrame;

/**
 * Main installer class for installing odcleanstore application.
 * 
 * @author Petr Jerman
 */
public class App extends InstallationWizardFrame {

	private GetEngineDirectoryStep getEngineDirectoryStep;
	private GetFrontendDirectoryStep getFrontendDirectoryStep;
	private GetDbConnectionsStep getDbConnectionsStep;
	private String isqlPath;

	public static void main(String[] args) {
		try {
			App app = new App();
			if (args.length > 0) {
				app.isqlPath = args[0];
			}
			app.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private App() throws IOException {
		super();
	}

	/**
	 * Sets first wizard step to wizard frame.
	 * 
	 * @throws IOException
	 */
	private void run() throws IOException {
		getEngineDirectoryStep = new GetEngineDirectoryStep(this);
		setNextStep(getEngineDirectoryStep);
	}

	/**
	 * @see cz.cuni.mff.odcleanstore.installer.ui.InstallationWizardFrame#next()
	 */
	@Override
	public void next() throws IOException {
		switch (getStepNumber()) {
		case 1:
			getFrontendDirectoryStep = new GetFrontendDirectoryStep(this);
			setNextStep(getFrontendDirectoryStep);
			break;
		case 2:
			getDbConnectionsStep = new GetDbConnectionsStep(this);
			setNextStep(getDbConnectionsStep);
			break;
		case 3:
			startInstallation();
			CopyEngineExecutorStep step3 = new CopyEngineExecutorStep(this, getEngineDirectoryStep.getEngineDirectory());
			setNextStep(step3);
			step3.onNext();
			break;
		case 4:
			DBScriptExecutorStep step4 = new DBScriptExecutorStep(this, getDbConnectionsStep, true,
					"run clearing sql script on clean DB", "clean-clear.sql", isqlPath);
			setNextStep(step4);
			step4.onNext();
			break;
		case 5:
			DBScriptExecutorStep step5 = new DBScriptExecutorStep(this, getDbConnectionsStep, true,
					"run installation sql script on clean DB", "clean.sql", isqlPath);
			setNextStep(step5);
			step5.onNext();
			break;
		case 6:
			DBScriptExecutorStep step6 = new DBScriptExecutorStep(this, getDbConnectionsStep, true,
					"run full text indexing sql script on clean DB - could be a long running operation",
					"clean-fulltext-index.sql", isqlPath);
			setNextStep(step6);
			step6.onNext();
			break;
		case 7:
			DBScriptExecutorStep step7 = new DBScriptExecutorStep(this, getDbConnectionsStep, false,
					"run clearing sql script on dirty DB", "dirty-clear.sql", isqlPath);
			setNextStep(step7);
			step7.onNext();
			break;
		case 8:
			DBScriptExecutorStep step8 = new DBScriptExecutorStep(this, getDbConnectionsStep, false,
					"run installation sql script on dirty DB", "dirty.sql", isqlPath);
			setNextStep(step8);
			step8.onNext();
			break;
		case 9:
			CopyFrontEndExecutorStep step9 = new CopyFrontEndExecutorStep(this, getFrontendDirectoryStep,
					getEngineDirectoryStep);
			setNextStep(step9);
			step9.onNext();
			break;
		case 10:
			endInstallation();
			setNextStep(null);
			break;
		}
	}
}
