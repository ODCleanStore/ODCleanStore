package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.markup.html.link.Link;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * A generic delete button.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 * @param <BO> The type of the primary BO to be deleted
 */
public abstract class AbstractDeleteButton <BO extends EntityWithSurrogateKey> extends Link<BO>
{
	private static final long serialVersionUID = 1L;

	/** the DAO to do the deletion */
	protected DaoForEntityWithSurrogateKey<BO> dao;
	
	/** the id of the BO to be deleted */
	protected Integer boId;
	
	/** the root of the component name */
	protected String objName;
	
	/** the page to which to redirect after the deletion */
	protected FrontendPage redirectPage;
	
	/**
	 * 
	 * @param dao
	 * @param boId
	 * @param compName
	 * @param objName
	 * @param message
	 * @param redirectPage
	 */
	public AbstractDeleteButton(DaoForEntityWithSurrogateKey<BO> dao, Integer boId, String compName,
		String objName, DeleteConfirmationMessage message, FrontendPage redirectPage) 
	{
		super(compName);
		
		this.dao = dao;
		this.boId = boId;
		this.objName = objName;
		this.redirectPage = redirectPage;
		
		this.add(new ConfirmationBoxRenderer(message.toString()));
	}
	
	/**
	 * 
	 * @param dao
	 * @param boId
	 * @param objName
	 * @param message
	 * @param redirectPage
	 */
	public AbstractDeleteButton(DaoForEntityWithSurrogateKey<BO> dao, Integer boId, String objName,
		DeleteConfirmationMessage message, FrontendPage redirectPage) 
	{
		this(dao, boId, createCompName(objName), objName, message, redirectPage);
	}
	
	@Override
	public void onClick() 
	{
		try {
			delete();
		}
		catch (Exception ex)
		{
			getSession().error(
				"The " + objName + " could not be deleted due to an unexpected error."
			);
			
			return;
		}
    	
		getSession().info("The " + objName + " was successfuly deleted.");
		setResponsePage(redirectPage);
	}
	
	/**
	 * Constructs the name of the component from the given name root.
	 * 
	 * @param objName
	 * @return
	 */
	protected static String createCompName(String objName)
	{
		assert objName != null && !objName.isEmpty();
		
		return
			"delete" +
			Character.toUpperCase(objName.charAt(0)) +
			objName.substring(1);
	}
	
	/**
	 * Does the actual deletion operation.
	 * 
	 * @throws Exception
	 */
	abstract protected void delete() throws Exception;
}
