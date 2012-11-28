package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Ontology;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UploadButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.OntologyDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * On this page Ontology creator can create and store a new ontology.
 * Its definition can be uploaded from a file.
 * @author Tomas Soukup
 */
@AuthorizeInstantiation({ Role.ONC })
public class NewOntologyPage extends FrontendPage 
{	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewOntologyPage.class);
	
	private OntologyDao ontologyDao;
	
	public NewOntologyPage() 
	{
		super(
			"Home > Ontologies > New", 
			"Add a new ontology"
		);
		
		// prepare DAO objects
		//
		this.ontologyDao = daoLookupFactory.getDao(OntologyDao.class);
		
		// register page components
		//
		addHelpWindow(new OntologyHelpPanel("content"));
		addNewOntologyForm();
	}
	
	private void addNewOntologyForm()
	{
		IModel<Ontology> formModel = new CompoundPropertyModel<Ontology>(new Ontology());
		
		Form<Ontology> form = new Form<Ontology>("newOntologyForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				Ontology ontology = this.getModelObject();
				ontology.setAuthorId(getODCSSession().getUser().getId());
				
				try {
					ontologyDao.save(ontology);
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
						"The ontology could not be created due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The ontology was successfuly stored.");
				setResponsePage(OntologiesListPage.class);
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		TextArea<String> definition = createTextarea("definition");
		form.add(definition);
		
		form.setMultiPart(true);
		FileUploadField fileUpload = new FileUploadField("fileUpload", new ListModel<FileUpload>(new ArrayList<FileUpload>()));
		form.add(fileUpload);
		form.add(new UploadButton(fileUpload, definition, "upload"));
		
		add(form);
	}
}