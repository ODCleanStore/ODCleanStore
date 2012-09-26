package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.RelationType;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DetachableAutoCompleteTextField;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.OntologyMappingDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.EnumValidator;
import cz.cuni.mff.odcleanstore.webfrontend.validators.JenaURIValidator;

@AuthorizeInstantiation({ Role.ONC })
public class AddMappingPage extends FrontendPage {
	
	private static final long serialVersionUID = 1L;
	private OntologyMappingDao mappingDao;
	
	private String sourceUri;
	private String targetUri;
	private String relationType;

	public AddMappingPage(String sourceOntoGraphName, String targetOntoGraphName) {
		super(
			"Home > Ontologies > Mapping > Add mapping", 
			"Ontologies mapping - add mapping"
		);
		
		// prepare DAO objects
		//
		mappingDao = (OntologyMappingDao)daoLookupFactory.getUnsafeDao(OntologyMappingDao.class);
		
		// register page components
		//
		addMappingForm(sourceOntoGraphName, targetOntoGraphName);
	}

	private void addMappingForm(final String sourceOntoGraphName, final String targetOntoGraphName)
	{
		Form<AddMappingPage> form = new Form<AddMappingPage>(
			"addMappingForm", new CompoundPropertyModel<AddMappingPage>(this))
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit()
			{
				try 
				{
					mappingDao.addMapping(sourceOntoGraphName, sourceUri, relationType, targetUri);
				} catch (Exception e) 
				{	
					// TODO: log the error
					getSession().error("Mapping could not be created due to an unexpected error.");
					return;
				}
				getSession().info("The mapping was successfuly created.");
				setResponsePage(new AddMappingPage(sourceOntoGraphName, targetOntoGraphName));
			}
		};
		IModel<List<String>> model = createModel(sourceOntoGraphName);
		TextField<String> field = new DetachableAutoCompleteTextField("sourceUri", model);
		field.add(new EnumValidator(model));
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
			field.add(new EnumValidator(model));
			
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
				List<RelationType> typeList = mappingDao.loadAll();
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