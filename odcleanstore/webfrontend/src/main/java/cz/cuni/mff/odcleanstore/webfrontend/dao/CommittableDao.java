package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying DAO class that represents the equivalent of the annotated DAO for uncommitted tables. 
 * @author Jan Michelfeit
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommittableDao
{
	Class<? extends Dao> value();
}
