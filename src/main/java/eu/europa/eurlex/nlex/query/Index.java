package eu.europa.eurlex.nlex.query;

/**
 * A list of N-Lex indexes.
 * 
 * @author Mariusz Jakubowski
 *
 */
public enum Index {
    TITLE("title"),
    FULLTEXT("fulltext"),
    DOC_TYPE("doc_type"),
    DOC_NUMBER("doc_number"),
    DATE_OF_DOC("date-of-doc"),
    OJ_NAME("OJ_Name"),
    OJ_NUMBER("OJ_Number"),
    DATE_OF_PUB("date-of-pub"),
    VALIDITY_DATE("validity-date"),
    SECTION_ARTICLE("section-article"),
    ;
    
    private String value;

    private Index(String value) {
        this.value = value;
    }
    
    public String value() {
        return value;
    }
    
    public static Index fromValue(String v) {
        for (Index idx : values()) {
            if (idx.value.equals(v)) {
                return idx;
            }
        }
        throw new IllegalArgumentException(v);
    }
    
}
