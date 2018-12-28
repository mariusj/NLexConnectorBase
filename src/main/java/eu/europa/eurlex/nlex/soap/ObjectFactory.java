
package eu.europa.eurlex.nlex.soap;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the pl.gov.sejm.isap.nlex package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: pl.gov.sejm.isap.nlex
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link VERSIONResponse }
     * 
     */
    public VERSIONResponse createVERSIONResponse() {
        return new VERSIONResponse();
    }

    /**
     * Create an instance of {@link Request }
     * 
     */
    public Request createRequest() {
        return new Request();
    }

    /**
     * Create an instance of {@link TestQueryResponse }
     * 
     */
    public TestQueryResponse createTestQueryResponse() {
        return new TestQueryResponse();
    }

    /**
     * Create an instance of {@link AboutConnectorResponse }
     * 
     */
    public AboutConnectorResponse createAboutConnectorResponse() {
        return new AboutConnectorResponse();
    }

    /**
     * Create an instance of {@link TestQuery }
     * 
     */
    public TestQuery createTestQuery() {
        return new TestQuery();
    }

    /**
     * Create an instance of {@link VERSION }
     * 
     */
    public VERSION createVERSION() {
        return new VERSION();
    }

    /**
     * Create an instance of {@link RequestResponse }
     * 
     */
    public RequestResponse createRequestResponse() {
        return new RequestResponse();
    }

    /**
     * Create an instance of {@link AboutConnector }
     * 
     */
    public AboutConnector createAboutConnector() {
        return new AboutConnector();
    }

}
