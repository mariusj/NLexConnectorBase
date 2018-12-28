package eu.europa.eurlex.nlex;

import eu.europa.eurlex.nlex.query.Comparator;
import eu.europa.eurlex.nlex.query.Index;
import eu.europa.eurlex.nlex.query.StringComparator;

/**
 * An interface for query builder.
 * 
 * Methods of this interface are called when parsing XML query 
 * sent by the N-Lex.
 * 
 * @author Mariusz Jakubowski
 *
 */
public interface QueryBuilder {

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
     * @param withWord a word that have to be in the index
     * @param orWord1 one of the words that can be in the index
     * @param orWord2 one of the words that can be in the index
     * @param orWord3 one of the words that can be in the index
     */
    void filterWords(Index indexName, String withWord, String orWord1, 
            String orWord2, String orWord3);
    
}
