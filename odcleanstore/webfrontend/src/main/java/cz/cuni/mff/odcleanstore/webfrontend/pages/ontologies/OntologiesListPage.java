package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Ontology;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthoredEntityDeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthoredEntityLimitedRedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.GenericSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.OntologyDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.ONC })
public class OntologiesListPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;
	
	private OntologyDao ontologyDao;

	public OntologiesListPage() 
	{
		super(
			"Home > Ontologies > List", 
			"List all ontologies"
		);
		
		// prepare DAO objects
		//
		this.ontologyDao = daoLookupFactory.getDao(OntologyDao.class);
		
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
					new AuthoredEntityDeleteButton<Ontology>(
						ontologyDao,
						ontology,
						Role.ONC,
						"ontology",
						new DeleteConfirmationMessage("ontology"),
						OntologiesListPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton(
						OntologyDetailPage.class,
						ontology.getId(), 
						"ontologyDetail"
					)
				);
				
				item.add(
					new AuthoredEntityLimitedRedirectButton(
						ChooseOntologiesPage.class,
						ontology,
						Role.ONC,
						"ontologyMapping"
					)
				);
				
				item.add(
					new AuthoredEntityLimitedRedirectButton(
						EditOntologyPage.class,
						ontology,
						Role.ONC,
						"showEditOntologyPage"
					)
				);
			}
		};

		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<Ontology>("sortByLabel", "label", data, dataView));
		add(new SortTableButton<Ontology>("sortByGraphName", "graphName", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
}
