package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC, Role.ADM_PIC })
public class NewTransformerAssignmentPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(NewTransformerAssignmentPage.class);
	
	private DaoForEntityWithSurrogateKey<Transformer> transformerDao;
	private DaoForEntityWithSurrogateKey<TransformerInstance> transformerInstanceDao;
	
	private Transformer transformer;
	private String workDirPath;
	private String configuration;
	private Boolean runOnCleanDB;
	private Integer priority;
	
	public NewTransformerAssignmentPage(final Integer pipelineId) 
	{
		super
		(
			"Home > Backend > Pipelines > Transformer Instances > New", 
			"Add a new transformer instance"
		);
		

		// prepare DAO objects
		//
		transformerDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerDao.class);
		transformerInstanceDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerInstanceDao.class);
		
		// register page components
		//
		addHelpWindow(new TransformerInstanceHelpPanel("content"));
		
		add(
			new RedirectWithParamButton(
				EditPipelinePage.class,
				pipelineId, 
				"managePipelineTransformers"
			)
		);
		
		addNewAssignmentForm(pipelineId);
	}
	
	private void addNewAssignmentForm(final Integer pipelineId)
	{
		Form<NewTransformerAssignmentPage> form = 
			new Form<NewTransformerAssignmentPage>("newAssignmentForm", new CompoundPropertyModel<NewTransformerAssignmentPage>(this))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
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
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage());
					
					getSession().error(
						"The assignment could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The assignment was successfuly registered.");
				setResponsePage(new EditTransformerAssignmentPage(insertId));
			}
		};

		form.add(createEnumSelectbox(transformerDao, "transformer"));
		form.add(createTextarea("configuration", false));
		form.add(createCheckbox("runOnCleanDB"));
		addPriorityTextfield(form);
		
		add(form);
	}

	private void addPriorityTextfield(Form<NewTransformerAssignmentPage> form)
	{
		TextField<String> textfield = new TextField<String>("priority");
		
		textfield.setRequired(true);
		textfield.add(new RangeValidator<Integer>(Integer.MIN_VALUE, Integer.MAX_VALUE));
		form.add(textfield);
	}
}
