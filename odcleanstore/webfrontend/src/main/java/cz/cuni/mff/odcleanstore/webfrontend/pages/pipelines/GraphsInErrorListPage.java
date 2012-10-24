package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.model.EnumGraphState;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.GraphInError;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.StateLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.StringifiedEnumLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.GraphInErrorDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC, Role.ADM })
public class GraphsInErrorListPage extends FrontendPage {
	private static final long serialVersionUID = 1L;
	
	private static Logger LOG = Logger.getLogger(GraphsInErrorListPage.class);
	
	private final GraphInErrorDao graphInErrorDao;

	// Disable showing engine UUID while there is only one engine instance allowed
	// May change in the future
	private static final boolean showEngineUUID = false;
	
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

		addHelpWindow(new GraphsInErrorHelpPanel("content"));
		addGraphsTable(params);
	}
	
	private void addGraphsTable(final Object... params) {
		DependentSortableDataProvider<GraphInError> data;
		
		final QueryCriteria criteria = new QueryCriteria();
		final QueryCriteria criteriaAll = new QueryCriteria();
		
		for (int i = 0; i < params.length; i += 2) {
			criteria.addWhereClause((String)params[i], params[i + 1]);
			criteriaAll.addWhereClause((String)params[i], params[i + 1]);
		}
		
		add(new Link<GraphInError>("finishAll") {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onClick() {
				try {
					graphInErrorDao.markAllFinished(criteriaAll);
					getSession().info(
							"All related graphs accepted successfully."
						);
				} catch (Exception e) {
					LOG.error(String.format("Could not mark all wrong graphs finished: %s", e.getMessage()));
					getSession().error(
							"Could not accept all related graphs."
					);
				}
			}
			
		});
		
		add(new Link<GraphInError>("rerunAll") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				try {
					graphInErrorDao.markAllQueued(criteriaAll);
					getSession().info(
						"All related graphs queued for rerun successfully."
					);
				} catch (Exception e) {
					LOG.error(String.format("Could not mark all wrong graphs queued: %s", e.getMessage()));
					getSession().error(
						"Could not queue all related graphs for rerun."
					);
				}
			}
			
		});

		add(new DeleteButton<GraphInError>(graphInErrorDao, null, "deleteAll", "graphs", new DeleteConfirmationMessage("graphs"), GraphsInErrorListPage.this) {

			private static final long serialVersionUID = 1L;

			@Override
			public void delete() {
				try {
					graphInErrorDao.markAllQueuedForDelete(criteriaAll);
				} catch (Exception e) {
					LOG.error(String.format("Could not mark all wrong graphs queued for delete: %s", e.getMessage()));
					throw new RuntimeException(e);
				}
			}
		});
		
		criteria.addWhereClause("iState.label", EnumGraphState.WRONG.name());
		
		data = new DependentSortableDataProvider<GraphInError>(graphInErrorDao, "uuid",
			criteria);
		
		DataView<GraphInError> dataView = new DataView<GraphInError>("graphInError", data) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<GraphInError> item) {
				final GraphInError graphInError = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<GraphInError>(graphInError));
				
				item.add(new Label("engineUUID") {

					private static final long serialVersionUID = 1L;
					
					@Override
					public boolean isVisible() {
						return showEngineUUID;
					}
				});
				item.add(new Label("pipelineLabel"));
				item.add(new Label("UUID", ODCSInternal.dataGraphUriPrefix + graphInError.UUID));
				item.add(new StateLabel("stateLabel"));
				item.add(new StringifiedEnumLabel("errorTypeLabel"));
				item.add(new TruncatedLabel("errorMessage", MAX_LIST_COLUMN_TEXT_LENGTH));
				
				item.add(new Link<GraphInError>("finishGraph") {

					private static final long serialVersionUID = 1L;

					@Override
					public void onClick() {
						try {
							graphInErrorDao.markFinished(graphInError);
							getSession().info(
									"The graph was queued for rerun successfully."
								);
						} catch (Exception e) {
							LOG.error(String.format("Could not mark graph %s finished: %s", graphInError.UUID, e.getMessage()));
							getSession().error(
								"Could not accept the graph."
							);
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
							getSession().info(
								"The graph was queued for rerun successfully."
							);
						} catch (Exception e) {
							LOG.error(String.format("Could not mark graph %s queued: %s", graphInError.UUID, e.getMessage()));
							getSession().error(
								"Could not queue the graph for rerun."
							);
						}
					}
				});

				item.add(new DeleteButton<GraphInError>(graphInErrorDao, null, "deleteGraph", "graph", new DeleteConfirmationMessage("graph"), GraphsInErrorListPage.this) {

					private static final long serialVersionUID = 1L;

					@Override
					public void delete() {
						try {
							graphInErrorDao.markQueuedForDelete(graphInError);
						} catch (Exception e) {
							LOG.error(String.format("Could not mark graph %s queued for delete: %s", graphInError.UUID, e.getMessage()));
							throw new RuntimeException(e);
						}
					}
				});
				
				item.add(new RedirectWithParamButton(GraphInErrorDetailPage.class, "detail", graphInError.getId(), GraphsInErrorListPage.this) {

					private static final long serialVersionUID = 1L;

					@Override
					public boolean isVisible() {
						return graphInError.errorTypeId != null;
					}
				});
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<GraphInError>("sortByEngine", "engineUuid", data, dataView) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isVisible() {
				return showEngineUUID;
			}
		});
		add(new SortTableButton<GraphInError>("sortByPipeline", "pipelineLabel", data, dataView));
		add(new SortTableButton<GraphInError>("sortByGraph", "uuid", data, dataView));
		add(new SortTableButton<GraphInError>("sortByState", "stateLabel", data, dataView));
		add(new SortTableButton<GraphInError>("sortByError", "errorTypeLabel", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
}