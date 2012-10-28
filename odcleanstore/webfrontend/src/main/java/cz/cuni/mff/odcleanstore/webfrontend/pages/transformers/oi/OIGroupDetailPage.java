package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedDeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedRedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.CommitChangesButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.TransformerAssignmentDetailPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

/**
 * Group-of-oi-rules-overview page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class OIGroupDetailPage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(OIGroupDetailPage.class);
	
	private OIRulesGroupDao oiRulesGroupDao;
	private OIRuleDao oiRuleDao;
	
	/**
	 * 
	 * @param groupId
	 */
	public OIGroupDetailPage(final Integer groupId) 
	{
		this(groupId, null);
	}
	
	/**
	 * 
	 * @param groupId
	 * @param transformerInstanceId
	 */
	public OIGroupDetailPage(final Integer groupId, final Integer transformerInstanceId) 
	{
		super(
			"Home > Backend > Linker > Groups > Edit", 
			"Edit Linker rule group",
			OIRulesGroupDao.class,
			groupId
		);
		
		// prepare DAO objects
		//
		oiRulesGroupDao = daoLookupFactory.getDao(OIRulesGroupDao.class);
		oiRuleDao = daoLookupFactory.getDao(OIRuleDao.class, isEditable());
		
		// register page components
		//		
		OIRulesGroup group = oiRulesGroupDao.load(groupId);
		addCommitChangesButton(group);
		addBackToPipelineLink(groupId, transformerInstanceId);
		addHelpWindow("rulesGroupHelpWindow", "openRulesGroupHelpWindow", new RulesGroupHelpPanel("content"));
		addHelpWindow("oiRuleHelpWindow", "openOIRuleHelpWindow", new OIRuleHelpPanel("content"));
		addEditOIRulesGroupForm(group);
		addOIRulesSection(groupId);
	}
	
	/**
	 * 
	 * @param groupId
	 * @param transformerInstanceId
	 */
	private void addBackToPipelineLink(Integer groupId, Integer transformerInstanceId) 
	{
		Map<Integer, Integer> navigationMap = getODCSSession().getOiPipelineRulesNavigationMap();
		Integer linkTransformerId = null;
		if (transformerInstanceId != null) 
		{
			linkTransformerId = transformerInstanceId;
			navigationMap.put(groupId, transformerInstanceId);
		}
		else if (navigationMap.containsKey(groupId))
		{
			linkTransformerId = navigationMap.get(groupId);
		}
		
		add(new AuthorizedRedirectButton(
			TransformerAssignmentDetailPage.class,
			linkTransformerId, 
			linkTransformerId != null,
			"backToPipelineLink"
		));
	}
	
	/**
	 * 
	 * @param group
	 */
	private void addEditOIRulesGroupForm(final OIRulesGroup group)
	{
		IModel<OIRulesGroup> formModel = new CompoundPropertyModel<OIRulesGroup>(group);
		
		Form<OIRulesGroup> form = new LimitedEditingForm<OIRulesGroup>("editOIRulesGroupForm", formModel, isEditable())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmitImpl()
			{
				OIRulesGroup group = this.getModelObject();
				
				try {
					oiRulesGroupDao.update(group);
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
						"The group could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The group was successfuly updated.");
				//setResponsePage(OIGroupsListPage.class);
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
	private void addOIRulesSection(final Integer groupId) 
	{
		add(
			new AuthorizedRedirectButton(
				NewOIRulePage.class,
				groupId, 
				isEditable(),
				"addNewRuleLink"
			)
		);
		
		addOIRulesTable(groupId);
	}

	/**
	 * 
	 * @param groupId
	 */
	private void addOIRulesTable(final Integer groupId) 
	{
		SortableDataProvider<OIRule> data = new DependentSortableDataProvider<OIRule>
		(
			oiRuleDao, 
			"label", 
			"groupId", 
			groupId
		);
		
		DataView<OIRule> dataView = new DataView<OIRule>("oiRulesTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<OIRule> item) 
			{
				OIRule rule = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<OIRule>(rule));
				
				item.add(new Label("label"));
				item.add(new Label("linkType"));
				item.add(createNullResistentTableCellLabel("sourceRestriction", rule.getSourceRestriction()));
				item.add(createNullResistentTableCellLabel("targetRestriction", rule.getTargetRestriction()));
				item.add(new TruncatedLabel("linkageRule", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(createNullResistentTableCellLabel("filterThreshold", rule.getFilterThreshold()));
				item.add(createNullResistentTableCellLabel("filterLimit", rule.getFilterLimit()));
				
				item.add(
					new AuthorizedDeleteButton<OIRule>
					(
						oiRuleDao,
						rule.getId(),
						isEditable(),
						"rule",
						new DeleteConfirmationMessage("rule"),
						OIGroupDetailPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						OIRuleDetailPage.class,
						rule.getId(),
						"showEditOIRulePage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<OIRule>("sortByLabel", "label", data, dataView));
		add(new SortTableButton<OIRule>("sortByLinkType", "linkType", data, dataView));
		add(new SortTableButton<OIRule>("sortBySourceRestriction", "sourceRestriction", data, dataView));
		add(new SortTableButton<OIRule>("sortByTargetRestriction", "targetRestriction", data, dataView));
		add(new SortTableButton<OIRule>("sortByFilterThreshold", "filterThreshold", data, dataView));
		add(new SortTableButton<OIRule>("sortByFilterLimit", "filterLimit", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
	
	/**
	 * 
	 * @param group
	 */
	private void addCommitChangesButton(final OIRulesGroup group)
	{
		add(new CommitChangesButton("commitChanges", group, oiRulesGroupDao)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				super.onClick();
				setResponsePage(new OIGroupDetailPage(group.getId()));
			}
		});
	}
}
