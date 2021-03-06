package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.GenericSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

/**
 * List-all-registered-transformers page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.ADM })
public class TransformersListPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private TransformerDao transformerDao;
	
	/**
	 * 
	 */
	public TransformersListPage() 
	{
		super
		(
			"Home > Backend > Transformers > List", 
			"List all transformers"
		);
		
		// prepare DAO objects
		//
		transformerDao = daoLookupFactory.getDao(TransformerDao.class);
		
		// register page components
		//
		addHelpWindow(new TransformerHelpPanel("content"));
		addTransformersTable();
	}

	/**
	 * 
	 */
	private void addTransformersTable()
	{
		SortableDataProvider<Transformer> data = new GenericSortableDataProvider<Transformer>(transformerDao, "label");
		
		DataView<Transformer> dataView = new DataView<Transformer>("transformersTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Transformer> item) 
			{
				Transformer transformer = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<Transformer>(transformer));
	
				item.add(new Label("label"));
				item.add(new TruncatedLabel("description", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new TruncatedLabel("jarPath", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new TruncatedLabel("workDirPath", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new TruncatedLabel("simpleClassName", MAX_LIST_COLUMN_TEXT_LENGTH));
				
				item.add(
					new DeleteButton<Transformer>
					(
						transformerDao,
						transformer.getId(),
						"transformer",
						new DeleteConfirmationMessage("transformer", "pipeline assignment"),
						TransformersListPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton(
						TransformerDetailPage.class, 
						transformer.getId(), 
						"showEditTransformerPage"
					)
				);
			}
		};

		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<Transformer>("sortByLabel", "label", data, dataView));
		add(new SortTableButton<Transformer>("sortByJARPath", "jarPath", data, dataView));
		add(new SortTableButton<Transformer>("sortByWorkDirPath", "workDirPath", data, dataView));
		add(new SortTableButton<Transformer>("sortByFullClassName", "fullClassName", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
}
