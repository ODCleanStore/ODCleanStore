package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ "POC" })
public class TransformerDetailPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<Transformer> transformerDao;

	public TransformerDetailPage(final Long transformerId) 
	{
		super(
			"Home > Pipelines > Pipeline > Detail", 
			"Show pipeline detail"
		);
		
		// prepare DAO objects
		//
		transformerDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerDao.class);
		
		// register page components
		//
		addHelpWindow(new TransformerHelpPanel("content"));
		addTransformerInformationSection(transformerId);
	}

	private void addTransformerInformationSection(final Long transformerId)
	{
		setDefaultModel(createModelForOverview(transformerDao, transformerId));
		
		add(new Label("label"));
		add(new Label("workDirPath"));
		add(new Label("description"));
		add(new Label("jarPath"));
		add(new Label("fullClassName"));
	}
}
