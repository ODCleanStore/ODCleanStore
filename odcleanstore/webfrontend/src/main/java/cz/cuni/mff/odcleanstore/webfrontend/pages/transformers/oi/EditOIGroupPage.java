package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

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
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.EditTransformerAssignmentPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

@AuthorizeInstantiation({ Role.PIC })
public class EditOIGroupPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	//private static Logger logger = Logger.getLogger(OIGroupDetailPage.class);
	
	private DaoForEntityWithSurrogateKey<OIRulesGroup> oiRulesGroupDao;
	private DaoForEntityWithSurrogateKey<OIRule> oiRuleDao;
	
	public EditOIGroupPage(final Integer groupId) 
	{
		this(groupId, null);
	}
	
	public EditOIGroupPage(final Integer groupId, final Integer transformerInstanceId) 
	{
		super(
			"Home > Backend > OI > Groups > Edit", 
			"Edit OI rule group"
		);
		
		// prepare DAO objects
		//
		oiRulesGroupDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRulesGroupDao.class);
		oiRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRuleDao.class);
		
		// register page components
		//		
		addBackToPipelineLink(transformerInstanceId);
		addHelpWindow("rulesGroupHelpWindow", "openRulesGroupHelpWindow", new RulesGroupHelpPanel("content"));
		addHelpWindow("oiRuleHelpWindow", "openOIRuleHelpWindow", new OIRuleHelpPanel("content"));
		addEditOIRulesGroupForm(groupId);
		addOIRulesSection(groupId);
	}
	
	private void addBackToPipelineLink(Integer transformerInstanceId) 
	{
		RedirectWithParamButton link = new RedirectWithParamButton(
			EditTransformerAssignmentPage.class,
			transformerInstanceId, 
			"backToPipelineLink"
		);
		link.setVisible(transformerInstanceId != null);
		add(link);
	}
	
	private void addEditOIRulesGroupForm(final Integer groupId)
	{
		OIRulesGroup group = oiRulesGroupDao.load(groupId);
		IModel<OIRulesGroup> formModel = new CompoundPropertyModel<OIRulesGroup>(group);
		
		Form<OIRulesGroup> form = new Form<OIRulesGroup>("editOIRulesGroupForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				OIRulesGroup group = this.getModelObject();
				
				try {
					oiRulesGroupDao.update(group);
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
	
	private void addOIRulesSection(final Integer groupId) 
	{
		add(
			new RedirectWithParamButton(
				NewOIRulePage.class,
				groupId, 
				"addNewRuleLink"
			)
		);
		
		addOIRulesTable(groupId);
	}

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
					new DeleteRawButton<OIRule>
					(
						oiRuleDao,
						rule.getId(),
						"rule",
						new DeleteConfirmationMessage("rule"),
						EditOIGroupPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						OIRuleDetailPage.class,
						rule.getId(),
						"showOIRuleDetail"
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						EditOIRulePage.class,
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
	
}
