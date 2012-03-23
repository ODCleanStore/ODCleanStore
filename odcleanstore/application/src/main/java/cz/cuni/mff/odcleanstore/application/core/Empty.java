/**
 * 
 */
package cz.cuni.mff.odcleanstore.application.core;

import java.util.Iterator;

/**
 * @author jermanp
 * 
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
