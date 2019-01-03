package eu.europa.eurlex.nlex;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import eu.europa.eurlex.nlex.query.Navigation.Page;
import eu.europa.eurlex.nlex.query.NlexDate;
import eu.europa.eurlex.nlex.query.Request;
import eu.europa.eurlex.nlex.query.Result;
import eu.europa.eurlex.nlex.query.ResultList.Navigation;

/**
 * A base class for SOAP connector. 
 * @author Mariusz Jakubowski
 *
 */
public abstract class AbstractConnector {

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
            String xsd = new String(fileBytes, "UTF-8");
            return xsd;
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Parses query string as XML and returns {@link Request} object.
     * @param query a request string to parse
     * @return a request object
     */
    protected eu.europa.eurlex.nlex.query.Request unmarshallRequest(String query) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(eu.europa.eurlex.nlex.query.Request.class);
            Unmarshaller jaxbUnmarshaller = ctx.createUnmarshaller();
            StringReader r = new StringReader(query);
            eu.europa.eurlex.nlex.query.Request request = (eu.europa.eurlex.nlex.query.Request) jaxbUnmarshaller.unmarshal(r);
            return request;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Marshalls a {@link Result} object into a string.
     * @param result a result object to marshall
     * @return a string with XML result
     */
    protected String marshallResult(eu.europa.eurlex.nlex.query.Result result) {
        try {
            eu.europa.eurlex.nlex.query.ObjectFactory of = new eu.europa.eurlex.nlex.query.ObjectFactory();
            JAXBElement<Result> resultJAXB = of.createResult(result);
            JAXBContext jaxbContext = JAXBContext.newInstance(eu.europa.eurlex.nlex.query.Result.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            jaxbMarshaller.marshal(resultJAXB, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Sets fields on {@link NlexDate} element.
     * @param dateXML a date element that will be set
     * @param date a date to set
     */
    protected void fillDate(NlexDate dateXML, LocalDate date) {
        try {
            XMLGregorianCalendar y = DatatypeFactory.newInstance().newXMLGregorianCalendar();
            y.setYear(date.getYear());            
            dateXML.setYear(y);
            dateXML.setMonth(BigInteger.valueOf(date.getMonthValue()));
            dateXML.setDay(BigInteger.valueOf(date.getDayOfMonth()));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a navigation part of result.
     * @param requestId a request id, can be null
     * @param hits a number of documents in result
     * @param pageSize the number of documents on one page 
     * @param pageNum current page number
     * @return a Navigation structure
     */
    protected Navigation createNav(String requestId, int hits, int pageSize, int pageNum) {
        eu.europa.eurlex.nlex.query.ResultList.Navigation nav = new eu.europa.eurlex.nlex.query.ResultList.Navigation();
        nav.setHits(BigInteger.valueOf(hits));
        if (requestId != null) {
            nav.setRequestId(requestId);
        }
        Page page = new Page();
        page.setNumber(pageNum);
        int size = pageSize;
        page.setSize(size);
        nav.setPage(page);
        return nav;
    }
    
}
