package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.RuleAssignment;
import cz.cuni.mff.odcleanstore.webfrontend.core.AuthorizationHelper;
import cz.cuni.mff.odcleanstore.webfrontend.core.DaoLookupFactory;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AssignedGroupRedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedDeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.HelpWindow;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

/**
 * List-all-groups-assigned-to-the-given-transformer-instance panel component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class AssignedGroupsList extends Panel
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<? extends RulesGroupEntity> groupsDao;
	private DaoForEntityWithSurrogateKey<RuleAssignment> assignedGroupsDao;
	private boolean isEditable = true;
	
	/**
	 * 
	 * @param id
	 * @param transformerInstanceId
	 * @param daoLookupFactory
	 * @param groupsDao
	 * @param assignedGroupsDao
	 * @param groupDetailPageClass
	 * @param newGroupPageClass
	 */
	public AssignedGroupsList(
		final String id, 
		final Integer transformerInstanceId,
		final DaoLookupFactory daoLookupFactory,
		final DaoForEntityWithSurrogateKey<? extends RulesGroupEntity> groupsDao,
		final DaoForEntityWithSurrogateKey<RuleAssignment> assignedGroupsDao,
		final Class<? extends FrontendPage> groupDetailPageClass,
		final Class<? extends FrontendPage> newGroupPageClass)
	{
		super(id);
		
		this.groupsDao = groupsDao;
		this.assignedGroupsDao = assignedGroupsDao;
		
		int authorId = daoLookupFactory.getDao(TransformerInstanceDao.class).getAuthorId(transformerInstanceId);
		isEditable = AuthorizationHelper.isAuthorizedForEntityEditing(authorId);
		
		addHelpWindow();
		addNewAssignmentLink(transformerInstanceId);
		addNewGroupLink(newGroupPageClass, transformerInstanceId);
		addAssignmentTable(transformerInstanceId, groupDetailPageClass);
	}
	
	/**
	 * 
	 */
	protected void addHelpWindow()
	{
		final ModalWindow helpWindow = new HelpWindow(
			"rulesGroupHelpWindow",
			new RulesGroupHelpPanel("content")
		);
		
		add(helpWindow);
		
		add(new AjaxLink<String>("openRulesGroupHelpWindow")
		{
			private static final long serialVersionUID = 1L;

			public void onClick(AjaxRequestTarget target) 
            {
            	helpWindow.show(target);
            }
        });
	}
	
	/**
	 * 
	 * @param transformerInstanceId
	 */
	private void addNewAssignmentLink(final Integer transformerInstanceId)
	{
		add(
			new Link<String>("showNewGroupAssignmentPage")
			{
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isVisible() 
				{
					return isEditable;
				};
				
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
	
	/**
	 * 
	 * @param newGroupPageClass
	 * @param transformerInstanceId
	 */
	private void addNewGroupLink(final Class<? extends FrontendPage> newGroupPageClass, Integer transformerInstanceId)
	{
		add(new RedirectWithParamButton(newGroupPageClass, transformerInstanceId, "showNewGroupPage"));
	}
	
	/**
	 * 
	 * @param transformerInstanceId
	 * @param groupDetailPageClass
	 */
	private void addAssignmentTable(
		final Integer transformerInstanceId, 
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
				
				item.add(new AuthorizedDeleteButton<RuleAssignment>(
					assignedGroupsDao,
					ruleAssignment.getId(),
					isEditable,
					"assignment",
					new DeleteConfirmationMessage("group assignment"),
					new TransformerAssignmentDetailPage(transformerInstanceId)
				));

				item.add(
					new AssignedGroupRedirectButton
					(
						groupDetailPageClass,
						ruleAssignment.getGroupId(),
						transformerInstanceId,
						"showGroupDetailPage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(FrontendPage.ITEMS_PER_PAGE);
		
		add(new SortTableButton<RuleAssignment>("sortByLabel", "label", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
}
