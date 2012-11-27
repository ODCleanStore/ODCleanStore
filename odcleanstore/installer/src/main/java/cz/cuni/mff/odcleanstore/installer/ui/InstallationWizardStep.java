package cz.cuni.mff.odcleanstore.installer.ui;

import java.awt.event.ActionEvent;

import javax.swing.JPanel;

public abstract class InstallationWizardStep {

	private InstallationWizardFrame wizardFrame;

	protected InstallationWizardStep(InstallationWizardFrame wizardFrame) {
		this.wizardFrame = wizardFrame;
	}

	public abstract JPanel getFormPanel();
	public abstract String getStepTitle();
	
	public abstract boolean onNext();

	public abstract void onFormEvent(ActionEvent arg);

	public void onCancel() {
	}

	public InstallationWizardFrame getWizardFrame() {
		return wizardFrame;
	}
}
