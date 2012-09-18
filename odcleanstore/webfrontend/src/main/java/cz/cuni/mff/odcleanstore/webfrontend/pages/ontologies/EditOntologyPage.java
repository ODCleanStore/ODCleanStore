package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Ontology;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UploadButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.OntologyDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class EditOntologyPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<Ontology> ontologyDao;

	public EditOntologyPage(final Long ontologyId) 
	{
		super(
			"Home > Ontologies > Edit", 
			"Edit an ontology"
		);
		
		// prepare DAO objects
		//
		this.ontologyDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OntologyDao.class);
		
		// register page components
		//
		addEditOntologyForm(ontologyId);
	}
	
	private void addEditOntologyForm(final Long groupId)
	{	
		Ontology ontology = ontologyDao.load(groupId);
		
		IModel<Ontology> formModel = new CompoundPropertyModel<Ontology>(ontology);
		
		Form<Ontology> form = new Form<Ontology>("editOntologyForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				Ontology ontology = this.getModelObject();
				
				try 
				{
					ontologyDao.update(ontology);
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
						"The ontology could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The ontology was successfuly updated.");
				setResponsePage(OntologiesListPage.class);
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		TextArea<String> rdfData = createTextarea("rdfData");
		form.add(rdfData);
		
		form.setMultiPart(true);
		FileUploadField fileUpload = new FileUploadField("fileUpload", new ListModel<FileUpload>());
		form.add(fileUpload);
		form.add(new UploadButton(fileUpload, rdfData, "upload"));
		
		add(form);
	}
}