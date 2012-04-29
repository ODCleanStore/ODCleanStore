/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.common;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author jermanp
 * 
 */
public class Utils {

	public static Collection<String> selectColumn(Collection<String[]> source, int columnNum) {
		LinkedList<String> retVal = new LinkedList<String>();

		if (source == null | columnNum < 0) {
			return retVal;
		}

		for (String[] row : source) {
			if (columnNum >= row.length) {
				return new LinkedList<String>();
			}
			retVal.add(row[columnNum]);
		}

		return retVal;
	}
}
