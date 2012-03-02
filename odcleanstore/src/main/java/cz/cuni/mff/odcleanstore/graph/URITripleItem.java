package cz.cuni.mff.odcleanstore.graph;

/**
 * 
 */
public final class URITripleItem extends TripleItem {
    private String uri;

    public URITripleItem(String uri) {
        this.uri = uri;
    }

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final URITripleItem other = (URITripleItem) obj;
        return getURI().equals(other.getURI());
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public String toString() {
        return "<" + getURI() + ">";
    }
}
