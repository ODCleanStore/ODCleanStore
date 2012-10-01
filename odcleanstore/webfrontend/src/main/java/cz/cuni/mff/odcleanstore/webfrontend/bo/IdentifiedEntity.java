package cz.cuni.mff.odcleanstore.webfrontend.bo;

/**
 * Interface for business objects that have a surrogate key (interface equivalent to {@link EntityWithSurrogateKey}
 * @author Jan Michelfeit
 */
public interface IdentifiedEntity
{
	/**
	 * Returns ID of the entity.
	 * @return entity id
	 */
	Integer getId();
}
