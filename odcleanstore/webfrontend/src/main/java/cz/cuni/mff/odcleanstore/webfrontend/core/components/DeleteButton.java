package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.markup.html.link.Link;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class DeleteButton<BO extends EntityWithSurrogateKey> extends Link
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<BO> dao; 
	private Long boId;
	private String objName;
	private FrontendPage redirectPage;
	
	public DeleteButton(DaoForEntityWithSurrogateKey<BO> dao, Long boId, String objName,
		DeleteConfirmationMessage message, FrontendPage redirectPage) 
	{
		super(createCompName(objName));
		
		this.dao = dao;
		this.boId = boId;
		this.objName = objName;
		this.redirectPage = redirectPage;
		
		this.add(new ConfirmationBoxRenderer(message.toString()));
	}

	@Override
	public void onClick() 
	{
		try {
			dao.deleteRaw(boId);
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
	
	private static String createCompName(String objName)
	{
		assert objName != null && !objName.isEmpty();
		return
			"delete" +
			Character.toUpperCase(objName.charAt(0)) +
			objName.substring(1);
	}
}
