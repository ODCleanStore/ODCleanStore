package cz.cuni.mff.odcleanstore.webfrontend.bo;

/**
 * Interface for business objects that track their author.
 * @author Jan Michelfeit
 */
public interface AuthoredEntity extends IdentifiedEntity
{
	/**
	 * Returns ID of the creator.
	 * @return user id
	 */
	Integer getAuthorId();
}
