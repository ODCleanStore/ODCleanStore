package cz.cuni.mff.odcleanstore.webfrontend.pages.ontologies;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import cz.cuni.mff.odcleanstore.webfrontend.bo.onto.Mapping;
import cz.cuni.mff.odcleanstore.webfrontend.dao.onto.OntologyMappingDao;

/**
 * Data provider, which provides stored mappings for given ontology id.
 * 
 * @author Tomas Soukup
 */
public class MappingDataProvider implements IDataProvider<Mapping> 
{
	private static final long serialVersionUID = 1L;
	
	private OntologyMappingDao dao;
	private Integer ontologyId;
	private List<Mapping> data;
	
	public MappingDataProvider(OntologyMappingDao dao, Integer ontologyId)
	{
		this.dao = dao;
		this.ontologyId = ontologyId;
	}
	
	private List<Mapping> getData()
	{
		if (data == null)
		{
			data = dao.loadAll(ontologyId);
		}
		
		return data;
	}
	
	public void detach() 
	{
		data = null;
	}

	public Iterator<Mapping> iterator(int first, int count) 
	{
		return getData().subList(first, first + count).iterator();
	}

	public int size() 
	{
		return getData().size();
	}

	public IModel<Mapping> model(Mapping object) 
	{
		return new Model<Mapping>(object);
	}
}
