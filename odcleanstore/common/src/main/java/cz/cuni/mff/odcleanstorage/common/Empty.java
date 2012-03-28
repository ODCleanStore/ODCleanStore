/**
 * 
 */
package cz.cuni.mff.odcleanstorage.common;

import java.util.Iterator;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public class Empty {

	public static final Iterable<String> ITERABLE_STRING = new Iterable<String>() {

		public Iterator<String> iterator() {
			return ITERATOR_STRING;
		}
	};

	private static final Iterator<String> ITERATOR_STRING = new Iterator<String>() {

		public boolean hasNext() {
			return false;
		}

		public String next() {
			return null;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	};
}