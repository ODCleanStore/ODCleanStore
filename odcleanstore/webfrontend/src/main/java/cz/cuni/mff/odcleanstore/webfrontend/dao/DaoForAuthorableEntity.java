package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.bo.AuthoredEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * Base class for DAO objects that can retrieve author ID by entity ID.
 * Note that this doesn't imply that the entity implements {@link AuthoredEntity}, which is reserved
 * only for entities that store the author ID directly.
 * @author Jan Michelfeit
 */
public abstract class DaoForAuthorableEntity<T extends EntityWithSurrogateKey> extends DaoForEntityWithSurrogateKey<T>
{
	private static final long serialVersionUID = 1L;

	public abstract int getAuthorId(Integer entityId);
}
