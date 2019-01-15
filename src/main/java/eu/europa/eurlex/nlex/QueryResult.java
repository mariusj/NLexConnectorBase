package eu.europa.eurlex.nlex;

import java.util.Iterator;

/**
 * An iterator with found documents.
 * @author Mariusz Jakubowski
 *
 */
public interface QueryResult extends Iterator<Document> {

    /**
     * Returns a total number of found documents.
     * @return
     */
    int getTotalCount();
}
