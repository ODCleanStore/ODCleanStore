package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

/**
 * Add-new-group-of-QA-rules page components.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class NewQAGroupPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewQAGroupPage.class);

	private QARulesGroupDao qaRulesGroupDao;
	
	/**
	 * Reference to id of transformer instance from which we navigated to this page.
	 * Null if we didn't navigate to this page from transformer instance detail page. 
	 */
	private Integer transformerInstanceId;
	
	/**
	 * 
	 */
	public NewQAGroupPage() 
	{
		this(null);
	}
	
	/**
	 * 
	 * @param transformerInstanceId
	 */
	public NewQAGroupPage(final Integer transformerInstanceId) 
	{
		super(
			"Home > Backend > Quality Assessment > Groups > New", 
			"Add a new rule group"
		);
		
		this.transformerInstanceId = transformerInstanceId;

		// prepare DAO objects
		//
		this.qaRulesGroupDao = daoLookupFactory.getDao(QARulesGroupDao.class);
		
		// register page components
		//
		addHelpWindow(new RulesGroupHelpPanel("content"));
		addNewOIRulesGroupForm();
	}
	
	/**
	 * 
	 */
	private void addNewOIRulesGroupForm()
	{
		IModel<QARulesGroup> formModel = new CompoundPropertyModel<QARulesGroup>(new QARulesGroup());
		
		Form<QARulesGroup> form = new Form<QARulesGroup>("newQAGroupForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				QARulesGroup group = this.getModelObject();
				group.setAuthorId(getODCSSession().getUser().getId());
				
				int insertId;
				try {
					insertId = qaRulesGroupDao.saveAndGetKey(group);
				}
				catch (DaoException ex)
				{
					logger.error(ex.getMessage(), ex);
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage(), ex);
					getSession().error(
						"The group could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The group was successfuly registered.");
				updateBackToPipelineNavigation(insertId);
				setResponsePage(new QAGroupDetailPage(insertId));
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		
		add(form);
	}
	
	/**
	 * 
	 * @param groupId
	 */
	private void updateBackToPipelineNavigation(Integer groupId) 
	{
		Map<Integer, Integer> navigationMap = getODCSSession().getQaPipelineRulesNavigationMap();
		if (transformerInstanceId != null) 
		{
			navigationMap.put(groupId, transformerInstanceId);
		}
	}
}
