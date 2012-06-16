package cz.cuni.mff.odcleanstore.webfrontend.pages;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;
import cz.cuni.mff.odcleanstore.webfrontend.core.DaoLookupFactory;
import cz.cuni.mff.odcleanstore.webfrontend.core.ODCSWebFrontendApplication;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

/**
 * An abstract base class for all WebFrontend page components, except for
 * some meta-page-components (like the LogOutPage component).
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public abstract class FrontendPage extends WebPage 
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(FrontendPage.class);
	
	protected DaoLookupFactory daoLookupFactory;
	
	/**
	 * 
	 * @param pageCrumbs the page-crumbs in the form of a simple string
	 * to be rendered using a label component
	 * @param pageTitle
	 */
	public FrontendPage(String pageCrumbs, String pageTitle)
	{
		// obtain the DAO-lookup-factory
		//
		daoLookupFactory = getApp().getDaoLookupFactory();
		
		// add common page components
		//
		add(new Label("pageCrumbs", pageCrumbs));
		add(new Label("pageTitle", pageTitle));
		add(new UserPanel("userPanel", LogOutPage.class));
		add(new FeedbackPanel("feedback"));
	}
	
	/**
	 * 
	 * @return
	 */
	protected ODCSWebFrontendApplication getApp() 
	{
		return (ODCSWebFrontendApplication) this.getApplication();
	}
	
	/* 	
	 	========================================================================
		FORM HELPERS
	 	========================================================================
	*/
	
	/**
	 * Creates a select-box form component for an SQL-table based enumeration.
	 * 
	 * @param dao
	 * @param componentName
	 * @return
	 */
	protected <EnumBO extends BusinessObject> DropDownChoice<EnumBO> createEnumSelectbox(
		Dao<EnumBO> dao, String componentName, boolean required)
	{
		// load all enum items
		List<EnumBO> allItems = dao.loadAll();
		
		// prepare the select-box renderer
		ChoiceRenderer<EnumBO> renderer = new ChoiceRenderer<EnumBO>("label", "id");
		
		// create the select-box component
		DropDownChoice<EnumBO> selectBox = new DropDownChoice<EnumBO>
		(
			componentName,
			allItems,
			renderer
		);
		
		// mark the select-box as a required form field
		selectBox.setRequired(required);
		
		return selectBox;
	}
	
	/**
	 * 
	 * @param dao
	 * @param componentName
	 * @return
	 */
	protected <EnumBO extends BusinessObject> DropDownChoice<EnumBO> createEnumSelectbox(
			Dao<EnumBO> dao, String componentName)
	{
		return createEnumSelectbox(dao, componentName, true);
	}
	
	/**
	 * 
	 * @param componentName
	 * @param required
	 * @return
	 */
	protected TextField<String> createTextfield(String componentName, boolean required)
	{
		TextField<String> textfield = new TextField<String>(componentName);
		textfield.setRequired(required);
		return textfield;
	}
	
	/**
	 * 
	 * @param componentName
	 * @return
	 */
	protected TextField<String> createTextfield(String componentName)
	{
		return createTextfield(componentName, true);
	}
	
	/**
	 * 
	 * @param componentName
	 * @param required
	 * @return
	 */
	protected TextArea<String> createTextarea(String componentName, boolean required)
	{
		TextArea<String> textarea = new TextArea<String>(componentName);
		textarea.setRequired(required);
		return textarea;
	}
	
	/**
	 * 
	 * @param componentName
	 * @return
	 */
	protected TextArea<String> createTextarea(String componentName)
	{
		return createTextarea(componentName, true);
	}
	
	/**
	 * 
	 * @param page
	 * @param compName
	 * @return
	 */
	protected Link createGoToPageButton(final Class<? extends FrontendPage> redirectPage, 
		final Long param, final String compName)
	{
		return new Link(compName)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() 
			{
				FrontendPage page;
				
				try 
				{
					// using reflection here (instead of passing the page instance as a method
					// argument) is necessary in order to postpone creating the page instance
					// to when onClick is called
					Constructor<? extends FrontendPage> constructor = 
						redirectPage.getConstructor(new Class[]{Long.class});
					
					page = (FrontendPage) constructor.newInstance(param);
				} 
				catch (Exception ex) 
				{
					throw new AssertionError(
						"Could not instantiate page class: " + redirectPage
					);
				}
				
				setResponsePage(page);
			}
		};
	}
	
	/**
	 * 
	 * @param dao
	 * @param boId
	 * @param compName
	 * @param objName
	 * @param redirectPage
	 * @return
	 */
	protected <LinkBO extends BusinessObject> Link createDeleteRawButton(
		final Dao<LinkBO> dao, final Long boId, final String compName, 
		final String objName, final Class<? extends FrontendPage> redirectPage)
	{
		Link button = new Link(compName)
	    {
			private static final long serialVersionUID = 1L;
	
			@Override
	        public void onClick()
	        {
	        	dao.deleteRaw(boId);
	        	
				getSession().info("The " + objName + " was successfuly deleted.");
				setResponsePage(redirectPage);
	        }
	    };
	    
	    button.add(
	    	new ConfirmationBoxRenderer(
	    		"Are you sure you want to delete the " + objName + "?"
	    	)
	    );
	    
	    return button;
	}
	
	/**
	 * 
	 * @param dao
	 * @param boId
	 * @param compName
	 * @param objName
	 * @param redirectPage
	 * @return
	 */
	protected <LinkBO extends BusinessObject> Link createDeleteRawButton(
		final Dao<LinkBO> dao, final Long boId, final String compName, 
		final String objName, final FrontendPage redirectPage)
	{
		Link button = new Link(compName)
	    {
			private static final long serialVersionUID = 1L;
	
			@Override
	        public void onClick()
	        {
	        	dao.deleteRaw(boId);
	        	
				getSession().info("The " + objName + " was successfuly deleted.");
				
				// TODO: toto nefunguje, jak by melo (tj. zobrazit aktualni stav pravidel)
				setResponsePage(redirectPage);
	        }
	    };
	    
	    button.add(
	    	new ConfirmationBoxRenderer(
	    		"Are you sure you want to delete the " + objName + "?"
	    	)
	    );
	    
	    return button;
	}
	
	/**
	 * 
	 * @param dao
	 * @param bo
	 * @param compName
	 * @param primaryObjName
	 * @param secondaryObjName
	 * @param redirectPage
	 * @return
	 */
	protected <LinkBO extends BusinessObject> Link createDeleteButton(
		final Dao<LinkBO> dao, final LinkBO bo, final String compName, 
		final String primaryObjName, final String secondaryObjName, 
		final Class<? extends FrontendPage> redirectPage)
	{
		Link button = new Link(compName)
	    {
			private static final long serialVersionUID = 1L;
	
			@Override
	        public void onClick()
	        {
	        	dao.delete(bo);
	        	
				getSession().info("The " + primaryObjName + " was successfuly deleted.");
				setResponsePage(redirectPage);
	        }
	    };
	    
	    button.add(
	    	new ConfirmationBoxRenderer(
	    		"Are you sure you want to delete the " + primaryObjName + 
	    		" and all associated " + secondaryObjName + "s?"
	    	)
	    );
	    
	    return button;
	}
	
	/*
		=======================================================================
		OTHER HELPERS
		=======================================================================
	*/
	
	protected <BO extends BusinessObject> IModel<List<BO>> createModelForListView(
		final Dao<BO> dao)
	{
		return new LoadableDetachableModel<List<BO>>() 
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected List<BO> load() 
			{
				return dao.loadAll();
			}
		};
	}
	
	protected <BO extends BusinessObject> IModel<BO> createModelForOverview(
		final Dao<BO> dao, final Long boId)
	{
		IModel model = new LoadableDetachableModel<BO>() 
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected BO load() 
			{
				return dao.load(boId);
			}
		};
		
		return new CompoundPropertyModel<BO>(model);
	}
}
