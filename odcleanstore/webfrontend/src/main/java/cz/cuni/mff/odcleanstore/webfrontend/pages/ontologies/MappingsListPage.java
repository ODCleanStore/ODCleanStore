package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Mapping;
import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Ontology;
import cz.cuni.mff.odcleanstore.webfrontend.core.AuthorizationHelper;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.OntologyDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.OntologyMappingDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * On this page ontology creator can see a list of existing mappings for one ontology.
 * He can also delete them.
 * @author Tomas Soukup
 */
@AuthorizeInstantiation({ Role.ONC })
public class MappingsListPage extends FrontendPage 
{	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(MappingsListPage.class);
	
	protected OntologyMappingDao mappingDao;

	public MappingsListPage(Integer sourceOntologyId) 
	{
		this(
			"Home > Ontologies > Mapping > Existing mappings", 
			"Ontologies mapping - existing mappings",
			sourceOntologyId
		);
	}
	
	public MappingsListPage(String pageCrumbs, String pageTitle, Integer sourceOntologyId)
	{
		super(pageCrumbs, pageTitle);
		
		// prepare DAO objects
		//
		mappingDao = daoLookupFactory.getDao(OntologyMappingDao.class);
		OntologyDao ontologyDao = daoLookupFactory.getDao(OntologyDao.class);
		Ontology sourceOntology = ontologyDao.load(sourceOntologyId);
		
		// register page components
		//		
		addMappingsTable(sourceOntology);
	}
	
	private void addMappingsTable(final Ontology ontology)
	{
		MappingDataProvider data = new MappingDataProvider(mappingDao, ontology.getId());
		DataView<Mapping> dataView = new DataView<Mapping>("mappingsTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Mapping> item)
			{
				Mapping mapping = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<Mapping>(mapping));
				
				item.add(new Label("sourceUri"));
				item.add(new Label("relationType"));
				item.add(new Label("targetUri"));
				item.add(createDeleteButton(
						ontology.getId(), mapping, AuthorizationHelper.isAuthorizedForEntityEditing(ontology)));
			}
			
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
	
	private Component createDeleteButton(final Integer ontologyId, final Mapping mapping, final boolean isAuthorized)
	{
		return new Link<Mapping>("delete")
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() 
			{
				if (isAuthorized)
				{
					try {
						mappingDao.delete(ontologyId, mapping);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						getSession().error("Mapping could not be deleted due to an unexpected error.");
					}
					getSession().info("The mapping was successfuly deleted.");
				}
			}
			
			@Override
			public boolean isVisible()
			{
				return isAuthorized;
			}
		};
	}
}
