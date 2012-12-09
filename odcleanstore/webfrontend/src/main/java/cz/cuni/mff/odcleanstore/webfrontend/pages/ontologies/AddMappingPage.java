package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Mapping;
import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Ontology;
import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.RelationType;
import cz.cuni.mff.odcleanstore.webfrontend.core.AuthorizationHelper;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DetachableAutoCompleteTextField;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.RelationTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.validators.JenaURIValidator;

/**
 * Ontology creator can add new mappings between previously choseon ontologies on this page.
 * He does so by inputting source URI, target URI and relation type into auto-complete fields.
 * @author Tomas Soukup
 */
@AuthorizeInstantiation({ Role.ONC })
public class AddMappingPage extends MappingsListPage 
{	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(AddMappingPage.class);
	
	private RelationTypeDao relationTypeDao;

	/**
	 * @param sourceOntology ontology stored in our database
	 * @param targetOntoGraphName graphName of target ontology, can be null, which represents external ontology
	 */
	public AddMappingPage(Ontology sourceOntology, String targetOntoGraphName) 
	{
		super(
			"Home > Ontologies > Mapping > Add mapping", 
			"Ontologies mapping - add mapping",
			sourceOntology.getId()
		);
		
		if (!AuthorizationHelper.isAuthorizedForEntityEditing(sourceOntology)) 
		{
			throw new UnauthorizedInstantiationException(getClass());
		}
		
		// prepare DAO objects
		//
		relationTypeDao =  daoLookupFactory.getDao(RelationTypeDao.class);
		
		// register page components
		//
		addHelpWindow(new OntologyMappingHelpPanel("content"));
		addMappingForm(sourceOntology, targetOntoGraphName);
	}

	private void addMappingForm(final Ontology sourceOntology, final String targetOntoGraphName)
	{
		Form<Mapping> form = new Form<Mapping>("addMappingForm", new CompoundPropertyModel<Mapping>(new Mapping()))
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit()
			{	
				try 
				{
					mappingDao.addMapping(sourceOntology.getId(), getModelObject());
				} catch (Exception e) 
				{	
					logger.error(e.getMessage(), e);
					getSession().error("Mapping could not be created due to an unexpected error.");
					return;
				}
				getSession().info("The mapping was successfuly created.");
				setResponsePage(new AddMappingPage(sourceOntology, targetOntoGraphName));
			}
		};
		IModel<List<String>> model = createModel(sourceOntology.getGraphName());
		TextField<String> field = new DetachableAutoCompleteTextField("sourceUri", model);
		field.add(new JenaURIValidator());
		form.add(field);
		
		AutoCompleteSettings settings = new AutoCompleteSettings();
		settings.setShowListOnEmptyInput(true);
		model = createModel();
		field = new DetachableAutoCompleteTextField("relationType", settings, model);
		field.add(new JenaURIValidator());
		form.add(field);
		
		if (targetOntoGraphName != null)
		{	
			model = createModel(targetOntoGraphName);
			field = new DetachableAutoCompleteTextField("targetUri", model);
			field.add(new JenaURIValidator());
			
		} else
		{
			field = new TextField<String>("targetUri");
			field.add(new JenaURIValidator());
		}
		form.add(field);
		
		add(form);
	}
	
	private IModel<List<String>> createModel(final String ontoGraphName)
	{
		return new LoadableDetachableModel<List<String>>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected List<String> load() {
				if (ontoGraphName == null)
				{
					return new ArrayList<String>();
				}
				return mappingDao.loadEntityURIs(ontoGraphName);
			}
		};
	}
	
	private IModel<List<String>> createModel()
	{
		return new LoadableDetachableModel<List<String>>()
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected List<String> load() {
				List<RelationType> typeList = relationTypeDao.loadAll();
				List<String> uriList = new ArrayList<String>();
				for (RelationType type: typeList)
				{
					uriList.add(type.getUri());
				}
				return uriList;
			}
		};
	}
}
