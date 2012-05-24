package cz.cuni.mff.odcleanstore.webfrontend.pages;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;
import cz.cuni.mff.odcleanstore.webfrontend.core.DaoLookupFactory;
import cz.cuni.mff.odcleanstore.webfrontend.core.WicketApplication;
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
	protected WicketApplication getApp() 
	{
		return (WicketApplication) this.getApplication();
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
	protected <EnumBO extends BusinessObject> DropDownChoice<EnumBO> createEnumSelectBox(
		Dao<EnumBO> dao, String componentName)
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
		selectBox.setRequired(true);
		
		return selectBox;
	}
}
