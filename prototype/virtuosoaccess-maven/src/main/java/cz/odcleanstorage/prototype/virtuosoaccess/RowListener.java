package cz.odcleanstorage.prototype.virtuosoaccess;

/**
 * Interface for processing rows returned SQL statements. 
 * 
 * @author Petr Jerman (petr.jerman@centrum.cz)
 *
 */
public interface RowListener {
	/**
	 * The operation processes row from ResultSet returned from Sql statement
	 * 
	 */
	public void processRow(VirtuosoConnection con, String[] row);
}
