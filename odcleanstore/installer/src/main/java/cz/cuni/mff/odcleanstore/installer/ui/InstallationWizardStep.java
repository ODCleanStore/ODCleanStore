package cz.cuni.mff.odcleanstore.installer.ui;

import java.awt.event.ActionEvent;

import javax.swing.JPanel;

/**
 * A Base class from GUI wizard based step used in descendant of InstallationWizardFrame class;
 * 
 * @author Petr Jerman
 */
/**
 * @author Administrator
 *
 */
public abstract class InstallationWizardStep {

	/**
	 * Parent frame of step object. 
	 */
	private InstallationWizardFrame wizardFrame;

	/**
	 * Initializes new instance of wizard step object. 
	 * @param wizardFrame
	 */
	protected InstallationWizardStep(InstallationWizardFrame wizardFrame) {
		this.wizardFrame = wizardFrame;
	}

	/**
	 * Gets panel with step form, called from wizard frame.
	 * 
	 * @return panel object with stwep form
	 */
	public abstract JPanel getFormPanel();

	/**
	 * Gets step tilte, called from wizard frame.
	 * 
	 * @return step title
	 */

	public abstract String getStepTitle();
	
	/**
	 * Called from wizard frame before moving to next installation step.
	 * 
	 * @return true value move wizard to next step 
	 */
	public abstract boolean onNext();

	/**
	 * Called from wizard frame on user interface events in step panel form.
	 * 
	 * @param arg event parameters
	 */
	public abstract void onFormEvent(ActionEvent arg);

	/**
	 *  Called from wizard frame before closing wizard.
	 */
	public void onCancel() {
	}

	/**
	 * Gets parent wizard frame. 
	 * 
	 * @return parent wizard frame
	 */
	public InstallationWizardFrame getWizardFrame() {
		return wizardFrame;
	}
}
