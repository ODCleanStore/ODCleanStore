package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

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
	
	public NewTransformerAssignmentPage(final Long pipelineId) 
	{
		super
		(
			"Home > Pipelines > Pipeline > Transformer instances > Create", 
			"Add a new transformer instance"
		);
		

		// prepare DAO objects
		//
		transformerDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerDao.class);
		transformerInstanceDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerInstanceDao.class);
		
		// register page components
		//
		add(
			new RedirectButton(
				PipelineDetailPage.class,
				pipelineId, 
				"managePipelineTransformers"
			)
		);
		
		addNewAssignmentForm(pipelineId);
	}
	
	private void addNewAssignmentForm(final Long pipelineId)
	{
		Form form = new Form("newAssignmentForm", new CompoundPropertyModel(this))
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

				try {
					transformerInstanceDao.save(assignment);
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
				setResponsePage(new PipelineDetailPage(pipelineId));
			}
		};

		form.add(createEnumSelectbox(transformerDao, "transformer"));
		form.add(createTextfield("workDirPath"));
		form.add(createTextarea("configuration"));
		form.add(createCheckbox("runOnCleanDB"));
		addPriorityTextfield(form);
		
		add(form);
	}

	private void addPriorityTextfield(Form form)
	{
		TextField<String> textfield = new TextField<String>("priority");
		
		textfield.setRequired(true);
		textfield.add(new RangeValidator<Integer>(Integer.MIN_VALUE, Integer.MAX_VALUE));
		form.add(textfield);
	}
}
