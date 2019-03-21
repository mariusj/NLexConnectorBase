package eu.europa.eurlex.nlex;

import java.time.LocalDate;
import java.util.List;

/**
 * An interface used to build a document info in response to search.
 * @author Mariusz Jakubowski
 *
 */
public interface Document {
    
    /**
     * Returns a document number.
     * @return
     */
    String getNumber();
    
    /**
     * Returns a document type.
     * @return
     */
    String getType();
    
    /**
     * Returns a title of the document.
     * @return
     */
    String getTitle();
    
    /**
     * Returns the language of the title.
     * @return
     */
    String getTitleLang();

    /**
     * Returns a document date.
     * @return
     */
    LocalDate getDocDate();
    
    /**
     * Returns the publisher name (name of the official journal).
     * @return
     */
    String getOJName();
    
    /**
     * Returns a number of the journal where the document was published.
     * @return
     */
    String getOJNumber();
    
    /**
     * Returns a date of publication.
     * @return
     */
    LocalDate getDateOfPub();
    
    /**
     * Returns a list of links to a document representations. 
     * @return
     */
    List<Reference> getReferences();
}
