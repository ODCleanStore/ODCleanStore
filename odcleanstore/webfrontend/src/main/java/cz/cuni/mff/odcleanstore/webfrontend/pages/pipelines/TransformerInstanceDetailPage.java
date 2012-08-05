package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class TransformerInstanceDetailPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;
	
	private static final String QA_FULL_CLASS_NAME = 
		"cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl";
	
	private static final String OI_FULL_CLASS_NAME = 
		"cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl";
	
	private static final String DN_FULL_CLASS_NAME = 
		"cz.cuni.mff.odcleanstore.datanormalization.DataNormalizer";

	private DaoForEntityWithSurrogateKey<TransformerInstance> transformerInstanceDao;
	private DaoForEntityWithSurrogateKey<Transformer> transformerDao;

	public TransformerInstanceDetailPage(final Long transformerInstanceId) 
	{
		super(
			"Home > Pipelines > Pipeline > Transformer Instances > Transformer Instance > Detail", 
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
			new RedirectButton
			(
				PipelineDetailPage.class,
				transformerInstance.getPipelineId(),
				"showPipelineDetailPage"
			)
		);
		
		add(new Label("label", transformer.getLabel()));
		add(new Label("workDirPath"));
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