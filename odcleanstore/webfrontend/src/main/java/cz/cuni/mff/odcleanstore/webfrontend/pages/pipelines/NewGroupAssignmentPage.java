package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.RuleAssignment;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

/**
 * Assign-new-group-to-transformer-instance page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class NewGroupAssignmentPage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewGroupAssignmentPage.class);
	
	private DaoForEntityWithSurrogateKey<? extends RulesGroupEntity> groupsDao;
	private DaoForEntityWithSurrogateKey<RuleAssignment> assignedGroupsDao;
	
	private RulesGroupEntity rulesGroup;
	
	/**
	 * 
	 * @param groupsDao
	 * @param assignedGroupsDao
	 * @param transformerInstanceId
	 */
	public NewGroupAssignmentPage(
		final DaoForEntityWithSurrogateKey<? extends RulesGroupEntity> groupsDao,
		final DaoForEntityWithSurrogateKey<RuleAssignment> assignedGroupsDao,
		final Integer transformerInstanceId) 
	{
		super
		(
			"Home > Backend > Pipelines > Transformer Instances > Assigned Groups > New", 
			"Assign a new group",
			TransformerInstanceDao.class,
			transformerInstanceId
		);
		
		checkUnathorizedInstantiation();
		
		this.groupsDao = groupsDao;
		this.assignedGroupsDao = assignedGroupsDao;
		
		// register page components
		//
		addHelpWindow(new RulesGroupHelpPanel("content"));
		addGoBackButton(transformerInstanceId);
		addNewAssignmentForm(transformerInstanceId);
	}
	
	/**
	 * 
	 * @param transformerInstanceId
	 */
	private void addGoBackButton(final Integer transformerInstanceId)
	{
		add(
			new RedirectWithParamButton(
				TransformerAssignmentDetailPage.class, 
				transformerInstanceId, 
				"showTransformerInstanceDetailPage"
			)
		);
	}
	
	/**
	 * 
	 * @param transformerInstanceId
	 */
	private void addNewAssignmentForm(final Integer transformerInstanceId)
	{
		Form<NewGroupAssignmentPage> form = new Form<NewGroupAssignmentPage>("newAssignmentForm", new CompoundPropertyModel<NewGroupAssignmentPage>(this))
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
					logger.error(ex.getMessage(), ex);
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage(), ex);
					getSession().error(
						"The group could not be assigned due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The group was successfuly assigned.");
				setResponsePage(new TransformerAssignmentDetailPage(transformerInstanceId));
			}
		};

		form.add(createEnumSelectbox(groupsDao, "rulesGroup"));
		
		add(form);
	}
}
