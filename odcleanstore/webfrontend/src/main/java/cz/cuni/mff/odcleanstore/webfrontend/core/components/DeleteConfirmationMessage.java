package cz.cuni.mff.odcleanstore.webfrontend.core.components;

public class DeleteConfirmationMessage
{
	String message;
	
	public DeleteConfirmationMessage(String primaryObjName)
	{
		this.message = 
			"Are you sure you want to delete the " + primaryObjName + "?";
	}
	
	public DeleteConfirmationMessage(String primaryObjName, String secondaryObjName)
	{
		this.message =
			"Are you sure you want to delete the " + primaryObjName + 
	    	" and all associated " + secondaryObjName + "s?";
	}
	
	@Override
	public String toString()
	{
		return message;
	}
}
