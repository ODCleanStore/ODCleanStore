package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.RelationType;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.OntologyMappingDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class AddMappingPage extends FrontendPage {
	
	private static final long serialVersionUID = 1L;
	private DaoForEntityWithSurrogateKey<RelationType> mappingDao;
	
	private String sourceUri;
	private String targetUri;
	private RelationType relationType;

	public AddMappingPage(String sourceOntoGraphName, String targetOntoGraphName) {
		super(
			"Home > Ontologies > Mapping > Add mapping", 
			"Ontologies mapping - add mapping"
		);
		
		// prepare DAO objects
		//
		this.mappingDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OntologyMappingDao.class);
		
		// register page components
		//
		addMappingForm();
	}

	private void addMappingForm()
	{
		Form<AddMappingPage> form = new Form<AddMappingPage>(
			"addMappingForm", new CompoundPropertyModel<AddMappingPage>(this))
		{
			private static final long serialVersionUID = 1L;
			
			
		};
		form.add(createAutoCompleteTextField(mappingDao, "relationType"));
		
		add(form);
	}
}
