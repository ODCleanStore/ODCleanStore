package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class TransformerInstanceDetailPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;
	
	private static final String QA_FULL_CLASS_NAME = 
		"cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl";
	
	private static final String OI_FULL_CLASS_NAME = 
		"cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl";
	
	private static final String DN_FULL_CLASS_NAME = 
		"cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl";

	private DaoForEntityWithSurrogateKey<TransformerInstance> transformerInstanceDao;
	private DaoForEntityWithSurrogateKey<Transformer> transformerDao;

	public TransformerInstanceDetailPage(final Integer transformerInstanceId) 
	{
		super(
			"Home > Backend > Pipelines > Transformer Instances > Detail", 
			"Show transformer instance detail"
		);
		
		// prepare DAO objects
		//
		transformerInstanceDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerInstanceDao.class);
		transformerDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerDao.class);
		
		// load common BO objects
		//
		TransformerInstance transformerInstance = transformerInstanceDao.load(transformerInstanceId);
		Transformer transformer = transformerDao.load(transformerInstance.getTransformerId());
		
		// register page components
		//
		addHelpWindow("transformerInstanceHelpWindow", "openTransformerInstanceHelpWindow", new TransformerInstanceHelpPanel("content"));
		
		addTransformerInstanceInformationSection(transformerInstance, transformer);
		addAssignedGroupsListSection(transformerInstance, transformer);
	}

	private void addTransformerInstanceInformationSection(
		final TransformerInstance transformerInstance, 
		final Transformer transformer)
	{
		IModel<TransformerInstance> model = createModelForOverview(
			transformerInstanceDao, transformerInstance.getId()
		);
		
		setDefaultModel(model);
		
		add(
			new RedirectWithParamButton
			(
				EditPipelinePage.class,
				transformerInstance.getPipelineId(),
				"showPipelineDetailPage"
			)
		);
		
		add(new Label("id"));
		add(new Label("label", transformer.getLabel()));
		add(new Label("configuration"));
		add(new Label("runOnCleanDB"));
		add(new Label("priority"));
	}
	

	private void addAssignedGroupsListSection(
		final TransformerInstance transformerInstance,
		final Transformer transformer) 
	{
		String fullClassName = transformer.getFullClassName();
		
		if (QA_FULL_CLASS_NAME.equals(fullClassName))
		{
			add(
				AssignedGroupsListPageFactory.createAssignedQAGroupsList(
					daoLookupFactory, 
					transformerInstance.getId()
				)
			);
		}
		else if (OI_FULL_CLASS_NAME.equals(fullClassName))
		{
			add(
				AssignedGroupsListPageFactory.createAssignedOIGroupsList(
					daoLookupFactory, 
					transformerInstance.getId()
				)
			);
		}
		else if (DN_FULL_CLASS_NAME.equals(fullClassName))
		{
			add(
				AssignedGroupsListPageFactory.createAssignedDNGroupsList(
					daoLookupFactory, 
					transformerInstance.getId()
				)
			);
		}
		else
		{
			add(new Label("assignedGroupsListSection", ""));
			return;
		}
	}
}
