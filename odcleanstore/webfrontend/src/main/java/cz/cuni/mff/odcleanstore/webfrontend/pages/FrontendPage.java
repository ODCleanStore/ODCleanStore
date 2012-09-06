package cz.cuni.mff.odcleanstore.webfrontend.pages;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.core.DaoLookupFactory;
import cz.cuni.mff.odcleanstore.webfrontend.core.ODCSWebFrontendApplication;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.HelpWindow;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts.UserAccountHelpPanel;

/**
 * An abstract base class for all WebFrontend page components, except for
 * some meta-page-components (like the LogOutPage component).
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public abstract class FrontendPage extends WebPage 
{
	public static final int MAX_LIST_COLUMN_TEXT_LENGTH = 100;
	
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
	protected <EnumBO extends EntityWithSurrogateKey> DropDownChoice<EnumBO> createEnumSelectbox(
		DaoForEntityWithSurrogateKey<EnumBO> dao, String componentName, boolean required)
	{
		// create the model
		IModel<List<EnumBO>> choices = createModelForListView(dao);
		
		// prepare the select-box renderer
		ChoiceRenderer<EnumBO> renderer = new ChoiceRenderer<EnumBO>("label", "id");
		
		// create the select-box component
		DropDownChoice<EnumBO> selectBox = new DropDownChoice<EnumBO>
		(
			componentName,
			choices,
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
	protected <EnumBO extends EntityWithSurrogateKey> DropDownChoice<EnumBO> createEnumSelectbox(
			DaoForEntityWithSurrogateKey<EnumBO> dao, String componentName)
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
	protected CheckBox createCheckbox(String componentName)
	{
		return new CheckBox(componentName);
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
	
	protected Label createNullResistentTableCellLabel(String compName, Object value)
	{
		if (value != null)
			return new Label(compName);
		
		Label label = new Label(compName, "&nbsp;");
		label.setEscapeModelStrings(false);
		
		return label;
	}
	
	/*
		=======================================================================
		OTHER HELPERS
		=======================================================================
	*/
	
	protected void addHelpWindow(Panel content)
	{
		addHelpWindow("helpWindow", "openHelpWindow", content);
	}
	
	protected void addHelpWindow(String compName, String linkCompName, Panel content)
	{
		final ModalWindow helpWindow = new HelpWindow(
			compName,
			content
		);
		
		add(helpWindow);
		
		add(new AjaxLink(linkCompName)
		{
			private static final long serialVersionUID = 1L;

			public void onClick(AjaxRequestTarget target) 
            {
            	helpWindow.show(target);
            }
        });
	}
	
	protected <BO extends EntityWithSurrogateKey> IModel<List<BO>> createModelForListView(
		final DaoForEntityWithSurrogateKey<BO> dao)
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
	
	protected <BO extends EntityWithSurrogateKey> IModel<BO> createModelForOverview(
		final DaoForEntityWithSurrogateKey<BO> dao, final Long boId)
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
