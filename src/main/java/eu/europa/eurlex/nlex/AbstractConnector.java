package eu.europa.eurlex.nlex;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import eu.europa.eurlex.nlex.query.Request;

/**
 * A base class for the SOAP connector. 
 * @author Mariusz Jakubowski
 *
 */
public abstract class AbstractConnector implements ConnectorService {
    

    /**
     * A namespace definition for nlex prefix.
     */
    protected static final String NLEX_NS = "xmlns:nlex=\"http://n-lex.europa.eu/xml/schemas/query/legislation/\"";

    /**
     * Reads a file from jar and returns its content as string.
     * @param fileName a name of the file
     * @return a content of the file
     */
    protected String readFile(String fileName) {
        ClassLoader loader = getClass().getClassLoader();
        try {
            URI uri = loader.getResource(fileName).toURI();
            Path path = Paths.get(uri);       
            byte[] fileBytes = Files.readAllBytes(path);
            String xsd = new String(fileBytes, StandardCharsets.UTF_8);
            return xsd;
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Checks if request XML contains namespace declaration for 
     * <code>nlex</code> prefix.
     * If it doesn't it inserts it. The namespace declaration is required
     * for proper unmarshalling.
     * @param query a request with XML string
     * @return a XML string containing <code>nlex</code> prefix declaration 
     */
    protected String insertNamespace(String query) {
        if (!query.contains(NLEX_NS)) {
            int idx = query.indexOf("<request");
            if (idx != -1) {
                query = query.substring(0, idx + 8) 
                        + " " + NLEX_NS + " " 
                        + query.substring(idx + 8);
            }
        }
        return query;
    }

    /**
     * Parses query string as XML and returns {@link Request} object.
     * @param query a request string to parse
     * @return a request object
     */
    protected Request unmarshallRequest(String query) {
        try {
            query = insertNamespace(query);
            JAXBContext ctx = JAXBContext.newInstance(Request.class);
            Unmarshaller jaxbUnmarshaller = ctx.createUnmarshaller();
            StringReader r = new StringReader(query);
            Request request = (Request) jaxbUnmarshaller.unmarshal(r);
            return request;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parses the query and builds a query object via {@link QueryBuilder}.
     * @param query a query to parse
     * @param builder a builder used to create query object
     * @return true if query was parsed correctly, false otherwise
     */
    protected boolean parseQuery(String query, QueryBuilder builder) {
        try {
            Request request = unmarshallRequest(query);
            QueryParser parser = new QueryParser(builder, request);
            parser.parse();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String request(String query) {
        ResultBuilder resultBuilder = new ResultBuilder(getSiteUrl(), 
                getConnectorUrl());
        QueryBuilder builder = getQueryBuilder();
        if (parseQuery(query, builder)) {
            QueryResult results = getResults(builder);
            if (results != null) {
                resultBuilder.createDocuments(null, results.getTotalCount(), 
                        getPageSize(), builder.getPage());
                resultBuilder.addDocuments(results);
            } else {
                resultBuilder.addError(ErrorCode.DB_UNAVAILABLE);
            }
        } else {
            resultBuilder.addError(ErrorCode.INCORRECT_CRITERIA);
        }
        String resultStr = resultBuilder.marshall();        
        return resultStr;
    }

    /**
     * Returns number of documents per page.
     * @return a number of documents per page
     */
    protected abstract int getPageSize();

    /**
     * Returns a new query builder.
     * @return a query builder
     */
    protected abstract QueryBuilder getQueryBuilder();

    /**
     * Returns the URL of the site with legal acts. 
     * @return an URL
     */
    protected abstract String getSiteUrl();

    /**
     * Returns the URL of the connector.
     * @return an URL
     */
    protected abstract String getConnectorUrl();

    /**
     * Executes query and returns results.
     * @param builder a query to execute
     * @return an iterator with found documents
     */
    protected abstract QueryResult getResults(QueryBuilder builder);

}
