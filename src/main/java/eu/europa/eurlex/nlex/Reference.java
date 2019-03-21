package eu.europa.eurlex.nlex;

/**
 * An information about a reference (representation of an act).
 * @author Mariusz Jakubowski
 *
 */
public interface Reference {
    
    /**
     * Returns a title of a reference.
     * @return
     */
    String getDisplay();

    /**
     * Returns a MIME format of a document (e.g. application/pdf).
     * @return
     */
    String getFormat();
    
    /**
     * Returns a link to a reference.
     * @return
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
    
}
