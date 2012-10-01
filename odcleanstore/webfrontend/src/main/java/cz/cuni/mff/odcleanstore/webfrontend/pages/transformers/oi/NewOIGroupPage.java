package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import java.util.Map;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

@AuthorizeInstantiation({ Role.PIC })
public class NewOIGroupPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private OIRulesGroupDao oiRulesGroupDao;
	
	/**
	 * Reference to id of transformer instance from which we navigated to this page.
	 * Null if we didn't navigate to this page from transformer instance detail page. 
	 */
	private Integer transformerInstanceId;
	
	public NewOIGroupPage() 
	{
		this(null);
	}
	
	public NewOIGroupPage(final Integer transformerInstanceId) 
	{
		super(
			"Home > Backend > OI > Groups > New", 
			"Add a new rule group"
		);
		
		this.transformerInstanceId = transformerInstanceId;

		// prepare DAO objects
		//
		this.oiRulesGroupDao = daoLookupFactory.getDao(OIRulesGroupDao.class);
		
		// register page components
		//
		addHelpWindow(new RulesGroupHelpPanel("content"));
		addNewOIRulesGroupForm();
	}
	
	private void addNewOIRulesGroupForm()
	{
		IModel<OIRulesGroup> formModel = new CompoundPropertyModel<OIRulesGroup>(new OIRulesGroup());
		
		Form<OIRulesGroup> form = new Form<OIRulesGroup>("newOIRulesGroupForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				OIRulesGroup group = this.getModelObject();
				group.setAuthorId(getODCSSession().getUser().getId());
				
				int insertId;
				try {
					insertId = oiRulesGroupDao.saveAndGetKey(group);
				}
				catch (DaoException ex)
				{
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					// TODO: log the error
					
					getSession().error(
						"The group could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The group was successfuly registered.");
				updateBackToPipelineNavigation(insertId);
				setResponsePage(new OIGroupDetailPage(insertId));
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		
		add(form);
	}
	
	private void updateBackToPipelineNavigation(Integer groupId) 
	{
		Map<Integer, Integer> navigationMap = getODCSSession().getOiPipelineRulesNavigationMap();
		if (transformerInstanceId != null) 
		{
			navigationMap.put(groupId, transformerInstanceId);
		}
	}
}
