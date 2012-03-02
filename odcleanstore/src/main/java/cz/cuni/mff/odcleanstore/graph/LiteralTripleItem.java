package cz.cuni.mff.odcleanstore.graph;

/**
 * 
 */
public final class LiteralTripleItem extends TripleItem {
    private String value;
    private String type;

    public LiteralTripleItem(String value) {
        this.value = value;
        this.type = "";
    }

    public LiteralTripleItem(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public String geType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LiteralTripleItem other = (LiteralTripleItem) obj;
        return getValue().equals(other.getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public String toString() {
        return "\"" + getValue() + "\"";
    }
}
