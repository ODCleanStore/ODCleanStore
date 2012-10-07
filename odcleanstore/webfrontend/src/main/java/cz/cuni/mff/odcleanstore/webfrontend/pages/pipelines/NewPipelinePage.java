package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.PipelineDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class NewPipelinePage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewPipelinePage.class);

	private PipelineDao pipelineDao;
	
	public NewPipelinePage() 
	{
		super
		(
			"Home > Backend > Pipelines > New", 
			"Create a new pipeline"
		);
		

		// prepare DAO objects
		//
		pipelineDao = daoLookupFactory.getDao(PipelineDao.class);
		
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
				pipeline.setAuthorId(getODCSSession().getUser().getId());
				int insertId;
				try 
				{
					insertId = pipelineDao.saveAndGetKey(pipeline);
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
						"The pipeline could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The pipeline was successfuly registered.");
				setResponsePage(new PipelineDetailPage(insertId));
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
