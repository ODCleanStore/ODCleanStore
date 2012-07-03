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

	private DaoForEntityWithSurrogateKey<TransformerInstance> transformerInstanceDao;
	private DaoForEntityWithSurrogateKey<Transformer> transformerDao;

	public TransformerInstanceDetailPage(final Long transformerInstanceId) 
	{
		super(
			"Home > Pipelines > Pipeline > Detail", 
			"Show pipeline detail"
		);
		
		// prepare DAO objects
		//
		transformerInstanceDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerInstanceDao.class);
		transformerDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerDao.class);
		
		// register page components
		//
		addTransformerInstanceInformationSection(transformerInstanceId);
	}

	private void addTransformerInstanceInformationSection(final Long transformerInstanceId)
	{
		IModel<TransformerInstance> model = createModelForOverview(
			transformerInstanceDao, transformerInstanceId
		);

		Transformer transformer = transformerDao.load(model.getObject().getTransformerId());

		setDefaultModel(model);
		
		add(
			new RedirectButton
			(
				PipelineDetailPage.class,
				model.getObject().getPipelineId(),
				"showPipelineDetailPage"
			)
		);
		
		add(new Label("label", transformer.getLabel()));
		add(new Label("workDirPath"));
		add(new Label("configuration"));
		add(new Label("priority"));
	}
}
