package eu.europa.eurlex.nlex;

import eu.europa.eurlex.nlex.query.Result;

public interface ConnectorService {

    /**
     * Returns the version of the connector.
     * @return a version of the connector
     */
    String version();
    
    /**
     * Returns a string containing an N-Lex query. This query must never change, 
     * as it will be used by N-Lex as a non-regression test of your connector. 
     * @param type the format of desired results (should always be <code>result-list.xsd</code>) 
     * @return a sample N-Lex query
     */
    String testQuery(String type);
    
    /**
     * N-Lex calls this method when the user posts a query in the N-Lex form. 
     * It is also called when, seeing one page of a long result list, the user 
     * calls for next or previous page.
     * <p>The steps for implementing this method are:
     * <ul>
     *  <li>create Result object ({@link #createResult(String, String)}) 
     *  <li>parse a query (you may use {@link #parseQuery(String, QueryBuilder)} method)
     *  <li>get result list (execute a query)
     *  <li>create documents list ({@link #createDocuments(Result, int, int, int)})
     *  <li>for each document add it to a list ({@link #createDoc(Document)})
     *  <li>marshall the result to XML string ({@link #marshallResult(Result)})
     * </ul>
     * </p>
     * @param query the query in N-Lex query language
     * @return XML in the N-Lex result language
     */
    String request(String query);
    
    /**
     * This method is called by N-Lex when the portal is started up, in order to 
     * configure itself. It returns the schemas describing the connector: 
     * which parameters it can get in a query, and what kind of information it 
     * can give in the answer.
     * @param type should always be <code>query</code>
     * @return a schema for queries
     */
    String aboutConnector(String type);

}
