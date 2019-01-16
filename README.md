# NLexConnectorBase
A base library for building N-Lex Connector (http://eur-lex.europa.eu/n-lex/).

N-Lex is a common access to different legislative websites. The N-Lex website is available at <http://eur-lex.europa.eu/n-lex/>.

When the user accesses a common website, N-Lex, fills a common search form, then his query is transmitted to the selected legislative connector and N-Lex shows the result in its own web-page.

A connector is an interface from the N-Lex portal to the national legislation. Connectors are simple web-services (SOAP). There are currently 4 methods that must be implemented:
- `String VERSION()`
- `String test_query()`
- `String request (String query)` // query and result are XML documents
- `String about_connector (String type)`

This project contains a base for building such connectors using Java Web Services. The `AbstractConnector` class is a base for that connector. Beside extending this class you have to:  
- implement the `QueryBuilder` interface to build a query for your legislative system and 
- implement the `QueryResult` interface that returns results of user query
 

