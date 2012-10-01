package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa;

import java.util.Map;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedDeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedRedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.CommitChangesButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.TransformerAssignmentDetailPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

@AuthorizeInstantiation({ Role.PIC })
public class QAGroupDetailPage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;

	//private static Logger logger = Logger.getLogger(EditQAGroupPage.class);
	
	private QARulesGroupDao qaRulesGroupDao;
	private QARuleDao qaRuleDao;
	
	public QAGroupDetailPage(final Integer groupId) 
	{
		this(groupId, null);
	}

	public QAGroupDetailPage(final Integer groupId, final Integer transformerInstanceId) 
	{
		super(
			"Home > Backend > QA > Groups > Edit", 
			"Edit QA rule group",
			QARulesGroupDao.class,
			groupId
		);
		
		// prepare DAO objects
		//
		qaRulesGroupDao = daoLookupFactory.getDao(QARulesGroupDao.class);
		qaRuleDao = daoLookupFactory.getDao(QARuleDao.class, isEditable());
		
		// register page components
		//
		QARulesGroup group = qaRulesGroupDao.load(groupId);
		addCommitChangesButton(group);
		addBackToPipelineLink(groupId, transformerInstanceId);
		addHelpWindow("rulesGroupHelpWindow", "openRulesGroupHelpWindow", new RulesGroupHelpPanel("content"));
		addHelpWindow("qaRuleHelpWindow", "openQARuleHelpWindow", new QARuleHelpPanel("content"));
		addEditOIRulesGroupForm(group);
		addQARulesSection(groupId);
	}
	
	private void addBackToPipelineLink(Integer groupId, Integer transformerInstanceId) 
	{
		Map<Integer, Integer> navigationMap = getODCSSession().getQaPipelineRulesNavigationMap();
		Integer linkTransformerId = null;
		if (transformerInstanceId != null) 
		{
			linkTransformerId = transformerInstanceId;
			navigationMap.put(groupId, transformerInstanceId);
		}
		else if (navigationMap.containsKey(groupId))
		{
			linkTransformerId = navigationMap.get(groupId);
		}
		
		add(new AuthorizedRedirectButton(
			TransformerAssignmentDetailPage.class,
			linkTransformerId, 
			linkTransformerId != null,
			"backToPipelineLink"
		));
	}

	private void addEditOIRulesGroupForm(final QARulesGroup group)
	{
		IModel<QARulesGroup> formModel = new CompoundPropertyModel<QARulesGroup>(group);
		
		Form<QARulesGroup> form = new LimitedEditingForm<QARulesGroup>("editQAGroupForm", formModel, isEditable())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmitImpl()
			{
				QARulesGroup group = this.getModelObject();
				
				try {
					qaRulesGroupDao.update(group);
				}
				catch (DaoException ex)
				{
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					// TODO: log the error
					
					getSession().error(
						"The group could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The group was successfuly updated.");
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextarea("description", false));
		
		add(form);
	}

	private void addQARulesSection(final Integer groupId) 
	{
		add(
			new AuthorizedRedirectButton(
				NewQARulePage.class,
				groupId, 
				isEditable(),
				"addNewRuleLink"
			)
		);
		
		addQARulesTable(groupId);
	}
	
	private void addQARulesTable(final Integer groupId)
	{
		SortableDataProvider<QARule> data = new DependentSortableDataProvider<QARule>
		(
			qaRuleDao, 
			"coefficient", 
			"groupId", 
			groupId
		);
		
		DataView<QARule> dataView = new DataView<QARule>("qaRulesTable", data)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(Item<QARule> item) 
			{
				QARule rule = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<QARule>(rule));
				
				item.add(new TruncatedLabel("filter", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new Label("coefficient"));
				item.add(new TruncatedLabel("description", MAX_LIST_COLUMN_TEXT_LENGTH));
				
				item.add(
					new AuthorizedDeleteButton<QARule>
					(
						qaRuleDao,
						rule.getId(),
						isEditable(),
						"rule",
						new DeleteConfirmationMessage("rule"),
						QAGroupDetailPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton
					(
						QARuleDetailPage.class,
						rule.getId(),
						"showEditQARulePage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<QARule>("sortByCoefficient", "coefficient", data, dataView));
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("navigator", dataView));
	}
	
	private void addCommitChangesButton(final QARulesGroup group)
	{
		add(new CommitChangesButton("commitChanges", group, qaRulesGroupDao)
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick()
			{
				super.onClick();
				setResponsePage(new QAGroupDetailPage(group.getId()));
			}
		});
	}
}
