package cz.cuni.mff.odcleanstore.webfrontend.pages;

import java.io.File;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
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

import cz.cuni.mff.odcleanstore.configuration.BackendConfig;
import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.core.DaoLookupFactory;
import cz.cuni.mff.odcleanstore.webfrontend.core.ODCSWebFrontendApplication;
import cz.cuni.mff.odcleanstore.webfrontend.core.ODCSWebFrontendSession;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.HelpWindow;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.MenuGroupComponent;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RegexField;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.ReplacementField;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.pages.engine.EngineStatePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies.OntologiesListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.outputws.AggregationSettingsPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.PipelinesListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes.PrefixesListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.TransformersListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.OIGroupsListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts.AccountsListPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.IRIValidator;

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
	public static final int ITEMS_PER_PAGE = 25;
	
	private static final long serialVersionUID = 1L;

	//private static Logger logger = Logger.getLogger(FrontendPage.class);
	
	protected final DaoLookupFactory daoLookupFactory;
	
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
		
		final Application app = Application.get();
		app.getHeaderContributorListenerCollection().add(new IHeaderContributor() {
			private static final long serialVersionUID = 1L;

			public void renderHead(IHeaderResponse response) {
				if (app.getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT) {
						response.renderCSSReference("css/theme_development.css");
				}
			}
		});
		
		// set up menu
		add(new MenuGroupComponent("pipelinesMenuGroup", PipelinesListPage.class));
		add(new MenuGroupComponent("engineStateMenuGroup", EngineStatePage.class));
		add(new MenuGroupComponent("rulesMenuGroup", OIGroupsListPage.class));
		add(new MenuGroupComponent("outputWSMenuGroup", AggregationSettingsPage.class));
		add(new MenuGroupComponent("ontologyMenuGroup", OntologiesListPage.class));
		add(new MenuGroupComponent("userAccountsMenuGroup", AccountsListPage.class));
		add(new MenuGroupComponent("transformersMenuGroup", TransformersListPage.class));
		add(new MenuGroupComponent("prefixesMenuGroup", PrefixesListPage.class));
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
	 * @param choices
	 * @param componentName
	 * @return
	 */
	protected <EnumBO extends EntityWithSurrogateKey> DropDownChoice<EnumBO> createEnumSelectbox(
		IModel<List<EnumBO>> choices, String componentName, final boolean required)
	{
		// prepare the select-box renderer
		ChoiceRenderer<EnumBO> renderer = new ChoiceRenderer<EnumBO>("label", "id");
		
		// create the select-box component
		DropDownChoice<EnumBO> selectBox = new DropDownChoice<EnumBO>(componentName, choices, renderer)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected CharSequence getDefaultChoice(String selectedValue)
			{
				return !isNullValid() && !getChoices().isEmpty() ? "" : super.getDefaultChoice(selectedValue);
			}
		};
		
		selectBox.setNullValid(!required);

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
		return createEnumSelectbox(createModelForListView(dao), componentName, true);
	}
	
	/**
	 * 
	 * @param dao
	 * @param componentName
	 * @return
	 */
	protected <EnumBO extends EntityWithSurrogateKey> DropDownChoice<EnumBO> createEnumSelectbox(
		DaoForEntityWithSurrogateKey<EnumBO> dao, String componentName, boolean required)
	{
		return createEnumSelectbox(createModelForListView(dao), componentName, required);
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
	protected TextField<String> createIRITextfield(String componentName, boolean required)
	{
		TextField<String> textfield = new TextField<String>(componentName);
		textfield.setRequired(required);
		textfield.add(new IRIValidator());
		return textfield;
	}

	/**
	 * 
	 * @param componentName
	 * @return
	 */
	protected TextField<String> createIRITextfield(String componentName)
	{
		return createIRITextfield(componentName, true);
	}
	
	/**
	 * 
	 * @param componentName
	 * @param required
	 * @return
	 */
	protected RegexField createRegexTextfield(String componentName, boolean required)
	{
		RegexField regexField = new RegexField(componentName, ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials());
		regexField.setRequired(required);
		return regexField;
	}

	/**
	 * 
	 * @param componentName
	 * @return
	 */
	protected RegexField createRegexTextfield(String componentName)
	{
		return createRegexTextfield(componentName, true);
	}
	
	/**
	 * 
	 * @param componentName
	 * @param required
	 * @return
	 */
	protected ReplacementField createReplacementTextfield(String componentName, RegexField pattern, boolean required)
	{
		ReplacementField replacementField = new ReplacementField(componentName, pattern);
		replacementField.setRequired(required);
		return replacementField;
	}

	/**
	 * 
	 * @param componentName
	 * @return
	 */
	protected ReplacementField createReplacementTextfield(String componentName, RegexField pattern)
	{
		return createReplacementTextfield(componentName, pattern, true);
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
		
		add(new AjaxLink<String>(linkCompName)
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
		final DaoForEntityWithSurrogateKey<BO> dao, final Integer boId)
	{
		IModel<BO> model = new LoadableDetachableModel<BO>() 
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
	
	protected ODCSWebFrontendSession getODCSSession()
	{
		return (ODCSWebFrontendSession) getSession();
	}
	
	protected TransformationContext createContext()
	{
		final BackendConfig config = ConfigLoader.getConfig().getBackendGroup();
		return new TransformationContext()
		{

			public JDBCConnectionCredentials getDirtyDatabaseCredentials() {
				return config.getDirtyDBJDBCConnectionCredentials();
			}

			public JDBCConnectionCredentials getCleanDatabaseCredentials() {
				return config.getCleanDBJDBCConnectionCredentials();
			}

			public String getTransformerConfiguration() {
				return "";
			}

			public File getTransformerDirectory() {
				String path = ConfigLoader.getConfig().getWebFrontendGroup().getDebugDirectoryPath();
				File dir = new File(path);
				dir.mkdir();
				return dir;
			}

			public EnumTransformationType getTransformationType() {
				return EnumTransformationType.NEW;
			}			
		};
	}
}
