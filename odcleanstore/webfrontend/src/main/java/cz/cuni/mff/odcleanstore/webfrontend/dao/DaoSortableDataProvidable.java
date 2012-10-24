package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public interface DaoSortableDataProvidable<BO extends EntityWithSurrogateKey>
{

	List<BO> loadAllBy(QueryCriteria criteria);

	BO load(Integer id);

}
