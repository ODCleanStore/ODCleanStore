package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl;
import cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl;
import cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

/**
 * Transformer-instance-to-pipeline-assignment-overview page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class TransformerAssignmentDetailPage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;
	
	private class TransformerInstanceWrapper extends TransformerInstance 
	{
		private static final long serialVersionUID = 1L;
		
		public TransformerInstance transformerPlaceBefore = null;
		
		public TransformerInstanceWrapper(TransformerInstance wrappedInstance, AssignedInstancesModel assignedInstanceModel)
		{
			super(
				wrappedInstance.getId(),
				wrappedInstance.getTransformerId(),
				wrappedInstance.getPipelineId(),
				wrappedInstance.getLabel(),
				wrappedInstance.getConfiguration(),
				wrappedInstance.getRunOnCleanDB(),
				wrappedInstance.getPriority()
			);
			for (TransformerInstance ti : assignedInstanceModel.getObject())
			{
				if (getPriority().equals(ti.getPriority() - 1)) 
				{
					transformerPlaceBefore = ti;
					break;
				}
			}
		}
	}
	
	/** the full class-name of the QA transformer */
	private static final String QA_FULL_CLASS_NAME = QualityAssessorImpl.class.getCanonicalName();
	
	/** the full class-name of the OI transformer */
	private static final String OI_FULL_CLASS_NAME = LinkerImpl.class.getCanonicalName();
	
	/** the full class-name of the DN transformer */
	private static final String DN_FULL_CLASS_NAME = DataNormalizerImpl.class.getCanonicalName();

	private static Logger logger = Logger.getLogger(TransformerAssignmentDetailPage.class);
	
	private TransformerDao transformerDao;
	private TransformerInstanceDao transformerInstanceDao;
	
	/**
	 * 
	 * @param transformerInstanceId
	 */
	public TransformerAssignmentDetailPage(final Integer transformerInstanceId) 
	{
		super
		(
			"Home > Backend > Pipelines > Transformer Instances > Edit", 
			"Edit a transformer instance",
			TransformerInstanceDao.class,
			transformerInstanceId
		);
		

		// prepare DAO objects
		//
		transformerDao = daoLookupFactory.getDao(TransformerDao.class);
		transformerInstanceDao = daoLookupFactory.getDao(TransformerInstanceDao.class);
		
		// register page components
		//
		addHelpWindow(new TransformerInstanceHelpPanel("content"));
		
		// load common BO objects
		TransformerInstance transformerInstance = transformerInstanceDao.load(transformerInstanceId);
		Transformer transformer = transformerDao.load(transformerInstance.getTransformerId());
		
		add(
			new RedirectWithParamButton(
				PipelineDetailPage.class,
				transformerInstance.getPipelineId(), 
				"managePipelineTransformers"
			)
		);
		
		addTransformerInstanceInformationSection(transformerInstance, transformer);
		addEditAssignmentForm(transformerInstance);
		addAssignedGroupsListSection(transformerInstance, transformer);
	}
	
	/**
	 * 
	 * @param transformerInstance
	 * @param transformer
	 */
	private void addTransformerInstanceInformationSection(
		final TransformerInstance transformerInstance, 
		final Transformer transformer)
	{
		add(new Label("id", transformerInstance.getId().toString()));
		add(new Label("transformer", transformer.getLabel()));
	}
	
	/**
	 * 
	 * @param transformerInstance
	 */
	private void addEditAssignmentForm(final TransformerInstance transformerInstance)
	{
		AssignedInstancesModel assignedInstancesModel = 
			new AssignedInstancesModel(transformerInstance.getPipelineId(), transformerInstanceDao, transformerInstance.getId());
		final TransformerInstanceWrapper instanceWrapper = new TransformerInstanceWrapper(transformerInstance, assignedInstancesModel);
			
		IModel<TransformerInstance> formModel = new CompoundPropertyModel<TransformerInstance>(instanceWrapper);
		Form<TransformerInstance> form = new LimitedEditingForm<TransformerInstance>("editAssignmentForm", formModel, isEditable())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmitImpl()
			{
				TransformerInstance assignment = this.getModelObject();
				int priority = transformerInstance.getPriority();
				if (instanceWrapper.transformerPlaceBefore == null)
				{
					priority = transformerInstanceDao.getInstancesCount(assignment.getPipelineId());
				}
				else if (instanceWrapper.transformerPlaceBefore.getPriority() > priority)
				{
					priority = instanceWrapper.transformerPlaceBefore.getPriority() - 1;
				}
				else
				{
					priority = instanceWrapper.transformerPlaceBefore.getPriority();
				}
				assignment.setPriority(priority);
				
				try {
					transformerInstanceDao.update(assignment);
				}
				catch (DaoException ex)
				{	
					logger.error(ex.getMessage(), ex);
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage());
					getSession().error("The assignment could not be updated due to an unexpected error.");
					return;
				}
				
				getSession().info("The assignment was successfuly updated.");
			}
		};

		form.add(createTextarea("configuration", false));
		form.add(createCheckbox("runOnCleanDB"));
		form.add(createEnumSelectbox(assignedInstancesModel, "transformerPlaceBefore", false));
		
		add(form);
	}

	/**
	 * 
	 * @param transformerInstance
	 * @param transformer
	 */
	private void addAssignedGroupsListSection(
		final TransformerInstance transformerInstance,
		final Transformer transformer) 
	{
		String fullClassName = transformer.getFullClassName();
		
		if (QA_FULL_CLASS_NAME.equals(fullClassName))
		{
			add(
				AssignedGroupsListPageFactory.createAssignedQAGroupsList(
					daoLookupFactory, 
					transformerInstance.getId()
				)
			);
		}
		else if (OI_FULL_CLASS_NAME.equals(fullClassName))
		{
			add(
				AssignedGroupsListPageFactory.createAssignedOIGroupsList(
					daoLookupFactory, 
					transformerInstance.getId()
				)
			);
		}
		else if (DN_FULL_CLASS_NAME.equals(fullClassName))
		{
			add(
				AssignedGroupsListPageFactory.createAssignedDNGroupsList(
					daoLookupFactory, 
					transformerInstance.getId()
				)
			);
		}
		else
		{
			add(new Label("assignedGroupsListSection", ""));
			return;
		}
	}
}
