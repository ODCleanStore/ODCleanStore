package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class NewAssignmentPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private Dao<Transformer> transformerDao;
	private TransformerInstanceDao transformerInstanceDao;
	
	private Transformer transformer;
	private String workDirPath;
	private String configuration;
	private Integer priority;
	
	public NewAssignmentPage(final Long pipelineId) 
	{
		super
		(
			"Home > Pipelines > Assigned transformers > Create", 
			"Add a new transformer assignment"
		);
		

		// prepare DAO objects
		//
		transformerDao = daoLookupFactory.getDao(TransformerDao.class);
		transformerInstanceDao = daoLookupFactory.getTransformerInstanceDao();
		
		// register page components
		//
		addGoBackLink(pipelineId);
		addNewAssignmentForm(pipelineId);
	}
	
	private void addGoBackLink(final Long pipelineId)
	{
		add(new Link("managePipelineTransformers")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() 
			{
				setResponsePage(
					new ManagePipelineTransformersPage(pipelineId)
				);
			}
		});
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
					transformer.getLabel(),
					workDirPath,
					configuration,
					priority
				);

				try {
					transformerInstanceDao.save(assignment);
				}
				catch (Exception ex)
				{
					// TODO: log the error
					
					getSession().error(
						"The assignment could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The assignment was successfuly registered.");
				setResponsePage(new ManagePipelineTransformersPage(pipelineId));
			}
		};

		form.add(createEnumSelectbox(transformerDao, "transformer"));
		form.add(createTextfield("workDirPath"));
		form.add(createTextarea("configuration"));
		addPriorityTextfield(form);
		
		add(form);
	}

	private void addPriorityTextfield(Form form)
	{
		TextField<String> textfield = new TextField<String>("priority");
		
		textfield.setRequired(true);
		textfield.add(new RangeValidator<Integer>(-10000, 10000));
		form.add(textfield);
	}
}
