package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import org.apache.log4j.Logger;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
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
import cz.cuni.mff.odcleanstore.webfrontend.core.AuthorizationHelper;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UploadButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.OntologyDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * Ontology creator edit existing ontology on this page.
 * @author Tomas Soukup
 */
@AuthorizeInstantiation({ Role.ONC })
public class EditOntologyPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(EditOntologyPage.class);
	
	private OntologyDao ontologyDao;

	/**
	 * @param ontologyId ID of ontology to be edited
	 */
	public EditOntologyPage(final Integer ontologyId) 
	{
		super(
			"Home > Ontologies > Edit", 
			"Edit an ontology"
		);
		
		// prepare DAO objects
		//
		this.ontologyDao = daoLookupFactory.getDao(OntologyDao.class);
		
		Ontology ontology = ontologyDao.load(ontologyId);
		if (!AuthorizationHelper.isAuthorizedForEntityEditing(ontology.getAuthorId())) 
		{
			throw new UnauthorizedInstantiationException(getClass());
		}
		
		// register page components
		//
		addHelpWindow(new OntologyHelpPanel("content"));
		addEditOntologyForm(ontology);
	}
	
	private void addEditOntologyForm(final Ontology ontology)
	{	
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
					logger.error(ex.getMessage(), ex);
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage(), ex);					
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
		TextArea<String> rdfData = createTextarea("definition");
		form.add(rdfData);
		
		form.setMultiPart(true);
		FileUploadField fileUpload = new FileUploadField("fileUpload", new ListModel<FileUpload>());
		form.add(fileUpload);
		form.add(new UploadButton(fileUpload, rdfData, "upload"));
		
		add(form);
	}
}