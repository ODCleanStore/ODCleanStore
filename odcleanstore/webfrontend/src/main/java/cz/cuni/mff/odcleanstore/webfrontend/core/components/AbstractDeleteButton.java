package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.markup.html.link.Link;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public abstract class AbstractDeleteButton <BO extends EntityWithSurrogateKey> extends Link<BO>
{
	private static final long serialVersionUID = 1L;

	protected DaoForEntityWithSurrogateKey<BO> dao; 
	protected Integer boId;
	protected String objName;
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
	
	protected static String createCompName(String objName)
	{
		assert objName != null && !objName.isEmpty();
		return
			"delete" +
			Character.toUpperCase(objName.charAt(0)) +
			objName.substring(1);
	}
	
	abstract protected void delete() throws Exception;
}
