package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import cz.cuni.mff.odcleanstore.webfrontend.behaviours.ConfirmationBoxRenderer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.RuleAssignment;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.HelpWindow;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

@AuthorizeInstantiation({ "POC" })
public class AssignedGroupsList extends Panel
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<RulesGroupEntity> groupsDao;
	private DaoForEntityWithSurrogateKey<RuleAssignment> assignedGroupsDao;
	
	public AssignedGroupsList(
		final String id, 
		final Long transformerInstanceId,
		final DaoForEntityWithSurrogateKey<RulesGroupEntity> groupsDao,
		final DaoForEntityWithSurrogateKey<RuleAssignment> assignedGroupsDao,
		final Class<? extends FrontendPage> groupDetailPageClass,
		final Class<? extends FrontendPage> newGroupPageClass)
	{
		super(id);
		
		this.groupsDao = groupsDao;
		this.assignedGroupsDao = assignedGroupsDao;
		
		addHelpWindow();
		addNewAssignmentLink(transformerInstanceId);
		addNewGroupLink(newGroupPageClass);
		addAssignmentTable(transformerInstanceId, groupDetailPageClass);
	}
	
	protected void addHelpWindow()
	{
		final ModalWindow helpWindow = new HelpWindow(
			"rulesGroupHelpWindow",
			new RulesGroupHelpPanel("content")
		);
		
		add(helpWindow);
		
		add(new AjaxLink("openRulesGroupHelpWindow")
		{
			private static final long serialVersionUID = 1L;

			public void onClick(AjaxRequestTarget target) 
            {
            	helpWindow.show(target);
            }
        });
	}
	
	private void addNewAssignmentLink(final Long transformerInstanceId)
	{
		add(
			new Link("showNewGroupAssignmentPage")
			{
				@Override
				public void onClick() 
				{
					NewGroupAssignmentPage page = new NewGroupAssignmentPage(
						groupsDao, assignedGroupsDao, transformerInstanceId
					);
					
					setResponsePage(page);
				}
			}
		);
	}
	
	private void addNewGroupLink(final Class<? extends FrontendPage> newGroupPageClass)
	{
		add(new RedirectButton(newGroupPageClass, "showNewGroupPage"));
	}
	
	private void addAssignmentTable(
		final Long transformerInstanceId, 
		final Class<? extends FrontendPage> groupDetailPageClass) 
	{
		SortableDataProvider<RuleAssignment> data = new DependentSortableDataProvider<RuleAssignment>
		(
			assignedGroupsDao,
			"label",
			"transformerInstanceId",
			transformerInstanceId
		);
		
		DataView<RuleAssignment> dataView = new DataView<RuleAssignment>("assignmentTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<RuleAssignment> item) 
			{
				RuleAssignment ruleAssignment = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<RuleAssignment>(ruleAssignment));
				
				item.add(new Label("groupLabel"));	
				item.add(new TruncatedLabel("groupDescription", FrontendPage.MAX_LIST_COLUMN_TEXT_LENGTH));
				
				item.add(
					createDeleteButton(
						transformerInstanceId, 
						ruleAssignment.getId()
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						groupDetailPageClass,
						ruleAssignment.getGroupId(),
						"showGroupDetailPage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(FrontendPage.ITEMS_PER_PAGE);
		
		add(new SortTableButton<RuleAssignment>("sortByLabel", "label", data, dataView));
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
	
	private Link createDeleteButton(final Long transformerInstanceId, final Long groupAssignmentId)
	{
		Link button = new Link("deleteAssignment")
		{

			@Override
			public void onClick() 
			{
				try {
					assignedGroupsDao.deleteRaw(groupAssignmentId);
				}
				catch (Exception ex)
				{
					getSession().error(
						"The group assignment could not be deleted due to an unexpected error."
					);
					
					return;
				}
		    	
				getSession().info("The group assignment was successfuly deleted.");
				setResponsePage(new TransformerInstanceDetailPage(transformerInstanceId));	
			}
			
		};
		
		button.add(new ConfirmationBoxRenderer("Are you sure you want to delete the group assignment?"));
		
		return button;
	}
}
