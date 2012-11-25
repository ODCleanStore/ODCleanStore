package cz.cuni.mff.odcleanstore.installer;

import java.io.IOException;

import cz.cuni.mff.odcleanstore.installer.ui.WizardFrame;

public class App extends WizardFrame {

	private GetEngineDirectoryStep getEngineDirectoryStep;
	private CopyEngineExecutorStep copyEngineExecutorStep;
	private GetDbConnectionsStep getDbConnectionsStep;
	private GetFrontendDirectoryStep getFrontendDirectoryStep;
	private CopyFrontEndExecutorStep copyFrontEndExecutorStep;

	public static void main(String[] args) {
		try {
			App app = new App();
			app.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private App() throws IOException {
		super();
	}

	private void run() throws IOException {
		getEngineDirectoryStep = new GetEngineDirectoryStep(this);
		setNextStep(getEngineDirectoryStep);
	}

	@Override
	protected void onNext() throws IOException {
		switch (getStepNumber()) {
		case 1:
			copyEngineExecutorStep = new CopyEngineExecutorStep(this, getEngineDirectoryStep.getEngineDirectory());
			setNextStep(copyEngineExecutorStep);
			break;
		case 2:
			getDbConnectionsStep = new GetDbConnectionsStep(this);
			setNextStep(getDbConnectionsStep);
			break;
		case 3:
			setNextStep(new DBScriptExecutorStep(this, getDbConnectionsStep, true, "run clearing sql script on clean DB",
					"Run clearing sql script on clean DB by Virtuoso isql utility", "clean-clear.sql"));
			break;
		case 4:
			setNextStep(new DBScriptExecutorStep(this, getDbConnectionsStep, true, "run installation sql script on clean DB",
					"Run installation sql script on clean DB by Virtuoso isql utility", "clean.sql"));
			break;
		case 5:
			setNextStep(new DBScriptExecutorStep(this, getDbConnectionsStep, true,
					"run full text indexing sql script on clean DB - possible long runnig operation",
					"Run full text indexing sql script on clean DB by Virtuoso isql utility", "clean-fulltext-index.sql"));
			break;
		case 6:
			setNextStep(new DBScriptExecutorStep(this, getDbConnectionsStep, false, "run clearing sql script on dirty DB",
					"Run clearing sql script on dirty DB by Virtuoso isql utility", "dirty-clear.sql"));
			break;
		case 7:
			setNextStep(new DBScriptExecutorStep(this, getDbConnectionsStep, false, "run installation sql script on dirty DB",
					"Run installation sql script on dirty DB by Virtuoso isql utility", "dirty.sql"));
			break;
		case 8:
			getFrontendDirectoryStep = new GetFrontendDirectoryStep(this);
			setNextStep(getFrontendDirectoryStep);
			break;
		case 9:
			copyFrontEndExecutorStep = new CopyFrontEndExecutorStep(this, getFrontendDirectoryStep, getEngineDirectoryStep);
			setNextStep(copyFrontEndExecutorStep);
			break;
		case 10:
			setNextStep(null);
			break;
		}
	}
}
