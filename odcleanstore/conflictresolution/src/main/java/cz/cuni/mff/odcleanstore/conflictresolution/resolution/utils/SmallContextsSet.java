package cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SmallContextsSet<T> {
    public static Set<Resource> fromIterator(Iterator<Statement> it) {
        if (!it.hasNext()) {
            return Collections.emptySet();
        }

        Resource first = it.next().getContext();
        while (first == null && it.hasNext()) {
            first = it.next().getContext();
        }
        while (it.hasNext()) {
            // first != null
            Resource next = it.next().getContext();
            if (next != null && !first.equals(next)) {
                return construct(first, next, it);
            }
        }
        return first == null ? Collections.<Resource>emptySet() : Collections.singleton(first);
    }

    private static Set<Resource> construct(Resource first, Resource second, Iterator<Statement> it) {
        Set<Resource> result = new HashSet<Resource>();
        result.add(first);
        result.add(second);
        while (it.hasNext()) {
            Resource next = it.next().getContext();
            if (next != null) {
                result.add(next);
            }
        }
        return result;
    }
}
