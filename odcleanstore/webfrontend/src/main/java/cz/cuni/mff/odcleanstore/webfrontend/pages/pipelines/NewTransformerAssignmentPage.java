package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.PipelineDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

/**
 * Assign-new-transformer-instance-to-a-pipeline page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class NewTransformerAssignmentPage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(NewTransformerAssignmentPage.class);
	
	private TransformerDao transformerDao;
	private TransformerInstanceDao transformerInstanceDao;
	
	private Transformer transformer;
	private String workDirPath;
	private String configuration;
	private Boolean runOnCleanDB = true;
	private TransformerInstance transformerPlaceBefore;
	
	/**
	 * 
	 * @param pipelineId
	 */
	public NewTransformerAssignmentPage(final Integer pipelineId) 
	{
		super
		(
			"Home > Backend > Pipelines > Transformer Instances > New", 
			"Add a new transformer instance",
			PipelineDao.class,
			pipelineId
		);
		
		checkUnathorizedInstantiation();

		// prepare DAO objects
		//
		transformerDao = daoLookupFactory.getDao(TransformerDao.class);
		transformerInstanceDao = daoLookupFactory.getDao(TransformerInstanceDao.class);
		
		// register page components
		//
		addHelpWindow(new TransformerInstanceHelpPanel("content"));
		
		add(
			new RedirectWithParamButton(
				PipelineDetailPage.class,
				pipelineId, 
				"managePipelineTransformers"
			)
		);
		
		addNewAssignmentForm(pipelineId);
	}
	
	/**
	 * 
	 * @param pipelineId
	 */
	private void addNewAssignmentForm(final Integer pipelineId)
	{
		Form<NewTransformerAssignmentPage> form = 
			new Form<NewTransformerAssignmentPage>("newAssignmentForm", new CompoundPropertyModel<NewTransformerAssignmentPage>(this))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				int priority = (transformerPlaceBefore == null)
					? transformerInstanceDao.getInstancesCount(pipelineId) + 1
					: transformerPlaceBefore.getPriority();
				
				TransformerInstance assignment = new TransformerInstance
				(
					transformer.getId(),
					pipelineId,
					workDirPath,
					configuration,
					runOnCleanDB,
					priority
				);

				int insertId;
				try {
					insertId = transformerInstanceDao.saveAndGetKey(assignment);
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
						"The assignment could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The assignment was successfuly registered.");
				setResponsePage(new TransformerAssignmentDetailPage(insertId));
			}
		};

		form.add(createEnumSelectbox(transformerDao, "transformer"));
		form.add(createTextarea("configuration", false));
		form.add(createCheckbox("runOnCleanDB"));
		form.add(createEnumSelectbox(new AssignedInstancesModel(pipelineId, transformerInstanceDao, null), "transformerPlaceBefore", false));
		
		add(form);
	}
}
