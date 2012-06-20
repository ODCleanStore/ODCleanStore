package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.link.Link;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class DeleteButton<BO extends BusinessObject> extends Link
{
	private static final long serialVersionUID = 1L;

	private Dao<BO> dao; 
	private Long boId;
	private String objName;
	private FrontendPage redirectPage;
	
	public DeleteButton(Dao<BO> dao, Long boId, String objName,
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
		dao.deleteRaw(boId);
    	
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
