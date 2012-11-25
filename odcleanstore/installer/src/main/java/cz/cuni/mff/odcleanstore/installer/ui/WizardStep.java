package cz.cuni.mff.odcleanstore.installer.ui;

import java.awt.event.ActionEvent;

import javax.swing.JPanel;

public abstract class WizardStep {

	private WizardFrame wizardFrame;

	protected WizardStep(WizardFrame wizardFrame) {
		this.wizardFrame = wizardFrame;
	}

	public abstract JPanel getFormPanel();

	public abstract String getStepTitle();

	public abstract String getNextNavigationButtonText();

	public boolean hasSkipButton() {
		return false;
	}

	public boolean canCancel() {
		return true;
	}

	public abstract boolean onNext();

	public abstract void onFormEvent(ActionEvent arg);

	public WizardFrame getWizardFrame() {
		return wizardFrame;
	}
}
