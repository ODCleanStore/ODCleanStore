package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Ontology;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.GenericSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.OntologyDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.outputws.AggregationPropertyHelpPanel;

public class OntologiesListPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<Ontology> ontologyDao;

	public OntologiesListPage() 
	{
		super(
			"Home > Ontologies > List", 
			"List all ontologies"
		);
		
		// prepare DAO objects
		//
		this.ontologyDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OntologyDao.class);
		
		// register page components
		//
		addHelpWindow(new OntologyHelpPanel("content"));
		addOntologiesTable();
	}
	
	private void addOntologiesTable()
	{
		SortableDataProvider<Ontology> data = new GenericSortableDataProvider<Ontology>(ontologyDao, "label");
		
		DataView<Ontology> dataView = new DataView<Ontology>("OntologiesTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Ontology> item) 
			{
				Ontology ontology = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<Ontology>(ontology));

				item.add(new Label("label"));
				item.add(new TruncatedLabel("description", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new Label("graphName"));
				
				item.add(
					new DeleteButton<Ontology>
					(
						ontologyDao,
						ontology,
						"ontology",
						new DeleteConfirmationMessage("ontology"),
						OntologiesListPage.this
					)
				);
				
				item.add(
					new RedirectButton(
						OntologyDetailPage.class,
						ontology.getId(), 
						"ontologyDetail"
					)
				);
				
				item.add(
					new RedirectButton(
						EditOntologyPage.class,
						ontology.getId(),
						"showEditOntologyPage"
					)
				);
			}
		};

		dataView.setItemsPerPage(10);
		
		add(new SortTableButton<Ontology>("sortByLabel", "label", data, dataView));
		add(new SortTableButton<Ontology>("sortByGraphName", "graphName", data, dataView));
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
}
