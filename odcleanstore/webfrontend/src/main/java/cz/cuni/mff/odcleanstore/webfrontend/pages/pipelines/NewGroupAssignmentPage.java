package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.RuleAssignment;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class NewGroupAssignmentPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<RulesGroupEntity> groupsDao;
	private DaoForEntityWithSurrogateKey<RuleAssignment> assignedGroupsDao;
	
	private RulesGroupEntity rulesGroup;
	
	public NewGroupAssignmentPage(
		final DaoForEntityWithSurrogateKey<RulesGroupEntity> groupsDao,
		final DaoForEntityWithSurrogateKey<RuleAssignment> assignedGroupsDao,
		final Long transformerInstanceId) 
	{
		super
		(
			"Home > Pipelines > Pipeline > Transformer instances > Instance > Assigned Groups > Create", 
			"Assign a new group"
		);
		
		this.groupsDao = groupsDao;
		this.assignedGroupsDao = assignedGroupsDao;
		
		// register page components
		//
		addGoBackButton(transformerInstanceId);
		addNewAssignmentForm(transformerInstanceId);
	}
	
	private void addGoBackButton(final Long transformerInstanceId)
	{
		add(
			new RedirectButton(
				TransformerInstanceDetailPage.class, 
				transformerInstanceId, 
				"showTransformerInstanceDetailPage"
			)
		);
	}
	
	private void addNewAssignmentForm(final Long transformerInstanceId)
	{
		Form form = new Form("newAssignmentForm", new CompoundPropertyModel(this))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				RuleAssignment assignment = new RuleAssignment(
					transformerInstanceId,
					rulesGroup.getId()
				);
				
				try {
					assignedGroupsDao.save(assignment);
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
						"The group could not be assigned due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The group was successfuly assigned.");
				setResponsePage(new TransformerInstanceDetailPage(transformerInstanceId));
			}
		};

		form.add(createEnumSelectbox(groupsDao, "rulesGroup"));
		
		add(form);
	}
}