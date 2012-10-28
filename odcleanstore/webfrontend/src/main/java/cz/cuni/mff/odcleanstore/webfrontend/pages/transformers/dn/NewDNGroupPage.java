package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

@AuthorizeInstantiation({ Role.PIC })
public class NewDNGroupPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewDNGroupPage.class);

	private DNRulesGroupDao dnRulesGroupDao;
	
	/**
	 * Reference to id of transformer instance from which we navigated to this page.
	 * Null if we didn't navigate to this page from transformer instance detail page. 
	 */
	private Integer transformerInstanceId;
	
	public NewDNGroupPage() 
	{
		this(null);
	}
	
	public NewDNGroupPage(final Integer transformerInstanceId) 
	{
		super(
			"Home > Backend > Data Normalization > Groups > New", 
			"Add a new Data Normalization rule group"
		);
		
		this.transformerInstanceId = transformerInstanceId;

		// prepare DAO objects
		//
		this.dnRulesGroupDao = daoLookupFactory.getDao(DNRulesGroupDao.class);
		
		// register page components
		//
		addHelpWindow(new RulesGroupHelpPanel("content"));
		addNewDNRulesGroupForm();
	}
	
	private void addNewDNRulesGroupForm()
	{
		IModel<DNRulesGroup> formModel = new CompoundPropertyModel<DNRulesGroup>(new DNRulesGroup());
		
		Form<DNRulesGroup> form = new Form<DNRulesGroup>("newDNGroupForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				DNRulesGroup group = this.getModelObject();
				group.setAuthorId(getODCSSession().getUser().getId());
				
				int insertId;
				try {
					insertId = dnRulesGroupDao.saveAndGetKey(group);
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
				setResponsePage(new DNGroupDetailPage(insertId));
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		
		add(form);
	}
	
	private void updateBackToPipelineNavigation(Integer groupId) 
	{
		Map<Integer, Integer> navigationMap = getODCSSession().getDnPipelineRulesNavigationMap();
		if (transformerInstanceId != null) 
		{
			navigationMap.put(groupId, transformerInstanceId);
		}
	}
}
