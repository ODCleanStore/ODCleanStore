package cz.cuni.mff.odcleanstore.installer;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cz.cuni.mff.odcleanstore.installer.ui.WizardFrame;
import cz.cuni.mff.odcleanstore.installer.ui.WizardStep;
import cz.cuni.mff.odcleanstore.installer.utils.AwtUtils;


public class GetDbConnectionsStep extends WizardStep {

	private JPanel panel;

	private JLabel jlbCleanHostName;
	private JTextField jtfCleanHostName;
	private JLabel jlbCleanPort;
	private JTextField jtfCleanPort;
	private JLabel jlbCleanUser;
	private JTextField jtfCleanUser;
	private JLabel jlbCleanPassword;
	private JTextField jtfCleanPassword;
	private JLabel jlbDirtyHostName;
	private JTextField jtfDirtyHostName;
	private JLabel jlbDirtyPort;
	private JTextField jtfDirtyPort;
	private JLabel jlbDirtyUser;
	private JTextField jtfDirtyUser;
	private JLabel jlbDirtyPassword;
	private JTextField jtfDirtyPassword;

	protected GetDbConnectionsStep(WizardFrame wizardFrame) {
		super(wizardFrame);
	}

	@Override
	public String getStepTitle() {
		return "setting Virtuoso instances connection parameters for administrator scripts";
	}

	@Override
	public String getNextNavigationButtonText() {
		return "Validate Virtuoso instances connection parameters for administrator scripts";
	}

	@Override
	public JPanel getFormPanel() {

		panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		jlbCleanHostName = new JLabel("Clean DB HostName:");
		jtfCleanHostName = new JTextField(40);
		jlbCleanPort = new JLabel("Clean DB port:");
		jtfCleanPort = new JTextField(40);
		jlbCleanUser = new JLabel("Clean DB user:");
		jtfCleanUser = new JTextField(40);
		jlbCleanPassword = new JLabel("Clean DB password:");
		jtfCleanPassword = new JTextField(40);

		jlbDirtyHostName = new JLabel("Dirty DB HostName:");
		jtfDirtyHostName = new JTextField(40);
		jlbDirtyPort = new JLabel("Dirty DB port:");
		jtfDirtyPort = new JTextField(40);
		jlbDirtyUser = new JLabel("Dirty DB user:");
		jtfDirtyUser = new JTextField(40);
		jlbDirtyPassword = new JLabel("Dirty DB password:");
		jtfDirtyPassword = new JTextField(40);

		panel.add(jlbCleanHostName, AwtUtils.createGbc(0, 0));
		panel.add(jtfCleanHostName, AwtUtils.createGbc(0, 1));
		panel.add(jlbCleanPort, AwtUtils.createGbc(1, 0));
		panel.add(jtfCleanPort, AwtUtils.createGbc(1, 1));
		panel.add(jlbCleanUser, AwtUtils.createGbc(2, 0));
		panel.add(jtfCleanUser, AwtUtils.createGbc(2, 1));
		panel.add(jlbCleanPassword, AwtUtils.createGbc(3, 0));
		panel.add(jtfCleanPassword, AwtUtils.createGbc(3, 1));

		panel.add(Box.createVerticalStrut(40), AwtUtils.createGbc(4, 0));

		panel.add(jlbDirtyHostName, AwtUtils.createGbc(5, 0));
		panel.add(jtfDirtyHostName, AwtUtils.createGbc(5, 1));
		panel.add(jlbDirtyPort, AwtUtils.createGbc(6, 0));
		panel.add(jtfDirtyPort, AwtUtils.createGbc(6, 1));
		panel.add(jlbDirtyUser, AwtUtils.createGbc(7, 0));
		panel.add(jtfDirtyUser, AwtUtils.createGbc(7, 1));
		panel.add(jlbDirtyPassword, AwtUtils.createGbc(8, 0));
		panel.add(jtfDirtyPassword, AwtUtils.createGbc(8, 1));
		
		return panel;
	}

	@Override
	public boolean onNext() {
		try {
            Class.forName("virtuoso.jdbc3.Driver");
        } catch (ClassNotFoundException e) {
        	getWizardFrame().showWarningDialog("Couldn't load Virtuoso jdbc driver", "Error");
       		return false;
        }

		boolean retVal = true;		
        try {
			DriverManager.getConnection(
			        getCleanDBConnectionString(),
			        getCleanDBUser(),
			        getCleanDBPassword());
		} catch (SQLException e) {
			getWizardFrame().showWarningDialog("Connect to clean DB failed", "Error");
			retVal = false;
		}
        
        try {
			DriverManager.getConnection(
			        getDirtyDBConnectionString(),
			        getDirtyDBUser(),
			        getDirtyDBPassword());
		} catch (SQLException e) {
			getWizardFrame().showWarningDialog("Connect to dirty DB failed", "Error");
			retVal = false;
		}
        
		return retVal;
	}

	@Override
	public void onFormEvent(ActionEvent arg) {
	}
	
	public String getCleanDBHostName() {
		return jtfCleanHostName.getText();
	}
	
	public String getCleanDBPort() {
		return jtfCleanPort.getText();
	}
	
	public String getCleanDBConnectionString() {
		return "jdbc:virtuoso://" + getCleanDBHostName() + ":" + getCleanDBPort() + "/CHARSET=UTF-8";
	}
	
	public String getCleanDBUser() {
		return jtfCleanUser.getText();
	}

	public String getCleanDBPassword() {
		return jtfCleanPassword.getText();
	}

	public String getDirtyDBHostName() {
		return jtfDirtyHostName.getText();
	}

	public String getDirtyDBPort() {
		return jtfDirtyPort.getText();
	}
	
	public String getDirtyDBConnectionString() {
		return "jdbc:virtuoso://" + getDirtyDBHostName() + ":" + getDirtyDBPort() + "/CHARSET=UTF-8";
	}

	public String getDirtyDBUser() {
		return jtfDirtyUser.getText();
	}

	public String getDirtyDBPassword() {
		return jtfDirtyPassword.getText();
	}
}
