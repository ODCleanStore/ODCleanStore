package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * Base class for DAO objects that can retrieve author ID by entity ID.
 * @author Jan Michelfeit
 */
public abstract class DaoForAuthorableEntity<T extends EntityWithSurrogateKey> extends DaoForEntityWithSurrogateKey<T>
{
	private static final long serialVersionUID = 1L;

	/** 
	 * Return entity author ID or zero if the author is NULL.
	 * @param entityId ID of entity of type T
	 * @return user id or zero 
	 */
	public abstract int getAuthorId(Integer entityId);
}
