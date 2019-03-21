package eu.europa.eurlex.nlex;

/**
 * An information about a reference (representation of an act).
 * @author Mariusz Jakubowski
 *
 */
public interface Reference {
    
    /**
     * Returns a displayed type of a reference (e.g. PDF or HTML).
     * @return
     */
    String getDisplay();

    /**
     * Returns a MIME format of a document (e.g. application/pdf).
     * @return
     */
    String getFormat();
    
    /**
     * Returns a link to a source document (PDF/HTML).
     * @return a link to an act 
     */
    String getHRef();
    
    /**
     * Returns if this link is hidden.
     * @return true if link is hidden
     */
    default boolean isHidden() {
        return false;
    }
    
    /**
     * Returns optional id of the link. The id may be used in a link 
     * from a title.
     * @return the id of the link.
     */
    default String getId() {
        return null;
    }
    
    /**
     * Indicates if this link should be used as link in title. 
     * @return true if link should be used in title
     */
    default boolean useInTitle() {
        return false;
    }
    
}
