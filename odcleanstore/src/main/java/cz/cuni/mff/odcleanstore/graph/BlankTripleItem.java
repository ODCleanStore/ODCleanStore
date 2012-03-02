package cz.cuni.mff.odcleanstore.graph;

/**
 * 
 */
public final class BlankTripleItem extends TripleItem {
    // TODO: something else than a String?
    private String id;

    public BlankTripleItem(String id) {
        this.id = id;
    }

    public String getAnonId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BlankTripleItem other = (BlankTripleItem) obj;
        return getAnonId().equals(other.getAnonId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        if (id == null) {
            return "[bnode" + id + "]";
        } else {
            return "[bnode]";
        }
    }
}
