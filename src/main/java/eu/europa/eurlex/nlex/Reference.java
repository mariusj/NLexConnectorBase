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
     * Returns a MIME format of a document (eg. application/pdf).
     * @return
     */
    String getFormat();
    
    /**
     * Returns a link to a reference.
     * @return
     */
    String getHRef();
}
