package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.PipelineDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class NewPipelinePage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<Pipeline> pipelineDao;
	
	public NewPipelinePage() 
	{
		super
		(
			"Home > Backend > Pipelines > New", 
			"Create a new pipeline"
		);
		

		// prepare DAO objects
		//
		pipelineDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(PipelineDao.class);
		
		// register page components
		//
		addHelpWindow(new PipelineHelpPanel("content"));
		addNewPipelineForm();
	}
	
	private void addNewPipelineForm()
	{
		IModel<Pipeline> formModel = new CompoundPropertyModel<Pipeline>(new Pipeline());
		
		Form<Pipeline> form = new Form<Pipeline>("newPipelineForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				Pipeline pipeline = this.getModelObject();
				
				try {
					pipelineDao.save(pipeline);
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
						"The pipeline could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The pipeline was successfuly registered.");
				setResponsePage(PipelinesListPage.class);
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
