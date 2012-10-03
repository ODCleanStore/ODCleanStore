package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.GraphInError;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.GraphInErrorDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.InputGraphStateDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class GraphsInErrorListPage extends FrontendPage {
	private static final long serialVersionUID = 1L;
	
	private static Logger LOG = Logger.getLogger(GraphsInErrorListPage.class);
	
	private final GraphInErrorDao graphInErrorDao;
	private final InputGraphStateDao inputGraphStateDao;
	
	public GraphsInErrorListPage() {
		this(new Object[]{});
	}
	
	public GraphsInErrorListPage(String column, Integer id) {
		this((Object)column, (Object)id);
	}

	public GraphsInErrorListPage(String column0, Integer id0, String column1, Integer id1) {
		this((Object)column0, (Object)id0, (Object)column1, (Object)id1);
	}
	
	private GraphsInErrorListPage(Object... params) {
		super
		(
			"Home > Backend > Pipelines > Graphs in Error", 
			"Graphs in Error"
		);

		graphInErrorDao = daoLookupFactory.getDao(GraphInErrorDao.class);
		inputGraphStateDao = daoLookupFactory.getDao(InputGraphStateDao.class);

		addGraphsTable(params);
	}
	
	private void addGraphsTable(Object... params) {
		DependentSortableDataProvider<GraphInError> data;
		
		QueryCriteria wrongStateCriteria = new QueryCriteria();
		
		wrongStateCriteria.addWhereClause("label", "WRONG");
		
		Object[] criteria = new Object[params.length + 2];

		criteria[0] = "stateId";
		criteria[1] = inputGraphStateDao.loadAllBy(wrongStateCriteria).get(0).id;

		for (int i = 2; i < criteria.length; ++i) {
			criteria[i] = params[i - 2];
		}
		
		data = new DependentSortableDataProvider<GraphInError>(graphInErrorDao, "uuid",
			criteria);
		
		DataView<GraphInError> dataView = new DataView<GraphInError>("graphInError", data) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<GraphInError> item) {
				final GraphInError graphInError = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<GraphInError>(graphInError));
				
				item.add(new Label("engineUUID"));
				item.add(new Label("pipelineLabel"));
				item.add(new Label("UUID"));
				item.add(new Label("stateLabel"));
				item.add(new Label("errorTypeLabel"));
				item.add(new TruncatedLabel("errorMessage", MAX_LIST_COLUMN_TEXT_LENGTH));
				
				item.add(new Link<GraphInError>("finishGraph") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						try {
							graphInErrorDao.markFinished(graphInError);
						} catch (Exception e) {
							LOG.error(String.format("Could not mark graph %s finished: %s", graphInError.UUID, e.getMessage()));
						}
					}
					
					@Override
					public boolean isVisible() {
						return graphInError.isInCleanDB;
					}
				});
				
				item.add(new Link<GraphInError>("rerunGraph") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						try {
							graphInErrorDao.markQueued(graphInError);
						} catch (Exception e) {
							LOG.error(String.format("Could not mark graph %s queued: %s", graphInError.UUID, e.getMessage()));
						}
					}
				});

				item.add(new Link<GraphInError>("deleteGraph") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						try {
							graphInErrorDao.markQueuedForDelete(graphInError);
						} catch (Exception e) {
							LOG.error(String.format("Could not mark graph %s queued for delete: %s", graphInError.UUID, e.getMessage()));
						}
					}
				});
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<GraphInError>("sortByEngine", "engineUuid", data, dataView));
		add(new SortTableButton<GraphInError>("sortByPipeline", "pipelineLabel", data, dataView));
		add(new SortTableButton<GraphInError>("sortByGraph", "uuid", data, dataView));
		add(new SortTableButton<GraphInError>("sortByState", "stateLabel", data, dataView));
		add(new SortTableButton<GraphInError>("sortByError", "errorTypeLabel", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
}