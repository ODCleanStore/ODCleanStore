package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * Common interface for DAOs for sortable data.
 * @author Jan Michelfeit
 *
 * @param <BO>
 */
public interface DaoSortableDataProvidable<BO extends EntityWithSurrogateKey>
{

	List<BO> loadAllBy(QueryCriteria criteria);

	BO load(Integer id);

}
