package cz.cuni.mff.odcleanstore.webfrontend.core.components;

/**
 * Represents the message to be displayed inside a confirmation
 * box applied to a delete button.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DeleteConfirmationMessage
{
	/** the message to be displayed */
	String message;
	
	/**
	 * 
	 * @param primaryObjName the name of the entity to be deleted
	 */
	public DeleteConfirmationMessage(String primaryObjName)
	{
		this.message = 
			"Are you sure you want to delete the " + primaryObjName + "?";
	}
	
	/**
	 * 
	 * @param primaryObjName the name of the primary entity to be deleted
	 * @param secondaryObjName the name of the secondary (dependent) entities 
	 * to be deleted
	 */
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
