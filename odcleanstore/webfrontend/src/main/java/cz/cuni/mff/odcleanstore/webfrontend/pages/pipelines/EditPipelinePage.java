package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.PipelineDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class EditPipelinePage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<Pipeline> pipelineDao;
	
	public EditPipelinePage(final Long pipelineId) 
	{
		super
		(
			"Home > Backend > Pipelines > Edit", 
			"Edit a pipeline"
		);
		

		// prepare DAO objects
		//
		pipelineDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(PipelineDao.class);
		
		// register page components
		//
		addNewPipelineForm(pipelineId);
	}
	
	private void addNewPipelineForm(final Long pipelineId)
	{
		Pipeline pipeline = pipelineDao.load(pipelineId);
		IModel<Pipeline> formModel = new CompoundPropertyModel<Pipeline>(pipeline);
		
		Form<Pipeline> form = new Form<Pipeline>("editPipelineForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				Pipeline pipeline = this.getModelObject();
				
				try {
					pipelineDao.update(pipeline);
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
						"The pipeline could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The pipeline was successfuly updated.");
				setResponsePage(PipelinesListPage.class);
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
