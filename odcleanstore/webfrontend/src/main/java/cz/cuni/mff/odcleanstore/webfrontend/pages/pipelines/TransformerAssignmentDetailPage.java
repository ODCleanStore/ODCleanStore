package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC, Role.ADM_PIC })
public class TransformerAssignmentDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private static final String QA_FULL_CLASS_NAME = 
		"cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl";
	
	private static final String OI_FULL_CLASS_NAME = 
		"cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl";
	
	private static final String DN_FULL_CLASS_NAME = 
		"cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl";

	private static Logger logger = Logger.getLogger(TransformerAssignmentDetailPage.class);
	
	private TransformerDao transformerDao;
	private TransformerInstanceDao transformerInstanceDao;
	
	public TransformerAssignmentDetailPage(final Integer transformerInstanceId) 
	{
		super
		(
			"Home > Backend > Pipelines > Transformer Instances > Edit", 
			"Edit a transformer instance"
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
	
	private void addTransformerInstanceInformationSection(
		final TransformerInstance transformerInstance, 
		final Transformer transformer)
	{
		add(new Label("id", transformerInstance.getId().toString()));
		add(new Label("transformer", transformer.getLabel()));
	}
	
	private void addEditAssignmentForm(final TransformerInstance transformerInstance)
	{
		IModel<TransformerInstance> formModel = new CompoundPropertyModel<TransformerInstance>(transformerInstance);
		Form<TransformerInstance> form = new Form<TransformerInstance>("editAssignmentForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				TransformerInstance assignment = this.getModelObject();
				
				try {
					transformerInstanceDao.update(assignment);
				}
				catch (DaoException ex)
				{
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage());
					
					getSession().error(
						"The assignment could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The assignment was successfuly updated.");
			}
		};

		form.add(createTextarea("configuration", false));
		form.add(createCheckbox("runOnCleanDB"));
		addPriorityTextfield(form);
		
		add(form);
	}

	private void addPriorityTextfield(Form<TransformerInstance> form)
	{
		TextField<String> textfield = new TextField<String>("priority");
		
		textfield.setRequired(true);
		textfield.add(new RangeValidator<Integer>(Integer.MIN_VALUE, Integer.MAX_VALUE));
		form.add(textfield);
	}
	
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
