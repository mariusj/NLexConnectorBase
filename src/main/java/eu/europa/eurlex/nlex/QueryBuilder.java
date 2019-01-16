package eu.europa.eurlex.nlex;

import eu.europa.eurlex.nlex.query.Comparator;
import eu.europa.eurlex.nlex.query.FTSearchWords;
import eu.europa.eurlex.nlex.query.Index;
import eu.europa.eurlex.nlex.query.StringComparator;

/**
 * An interface for query builder.
 * 
 * Methods of this interface are called when parsing XML query 
 * sent by the N-Lex.
 * 
 * You have to implement this interface to build a query for your legislative
 * system. Once the query is parsed it can be used to filter acts by 
 * {@link AbstractConnector#getResults(QueryBuilder)}. 
 * 
 * @author Mariusz Jakubowski
 *
 */
public interface QueryBuilder {
    
    /**
     * Sets the page of results that should be returned.
     * @param page a page number
     */
    void setPage(int page);
    
    /**
     * Returns the page number of the returned results.
     * @return
     */
    int getPage();
    
    /**
     * Sets a request id.
     * @param requestID
     */
    void setRequestID(String requestID);

    /**
     * Filters an index by date value.
     * @param indexName a name of the index to filter by
     * @param year a year
     * @param month a month (-1 means no value)
     * @param day a day (-1 means no value)
     * @param compare a comparator
     */
    void filterIndex(Index indexName, int year, int month, int day, Comparator compare);

    /**
     * Filter an index by integer value.
     * @param indexName a name of the index to filter by
     * @param value a value to filter by
     * @param compare a comparator
     */
    void filterIndex(Index indexName, int value, Comparator compare);

    /**
     * Filters an index by string value.
     * @param indexName a name of the index to filter by
     * @param value a value to filter by
     * @param compare a comparator
     */
    void filterIndex(Index indexName, String value, StringComparator compare);

    /**
     * Filter an index using full-text search.
     * @param indexName a name of the index to filter by
     * @param words a tree of operands and words
     */
    void filterWords(Index indexName, FTSearchWords words);
    
}
