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

import eu.europa.eurlex.nlex.query.Content;
import eu.europa.eurlex.nlex.query.DocNumber;
import eu.europa.eurlex.nlex.query.DocType;
import eu.europa.eurlex.nlex.query.DocumentSpecification;
import eu.europa.eurlex.nlex.query.Error;
import eu.europa.eurlex.nlex.query.Errors;
import eu.europa.eurlex.nlex.query.ExternUrl;
import eu.europa.eurlex.nlex.query.Metadata;
import eu.europa.eurlex.nlex.query.Metadata.DateOfDoc;
import eu.europa.eurlex.nlex.query.Metadata.PubSource;
import eu.europa.eurlex.nlex.query.Navigation.Page;
import eu.europa.eurlex.nlex.query.NlexDate;
import eu.europa.eurlex.nlex.query.OJName;
import eu.europa.eurlex.nlex.query.OJNumber;
import eu.europa.eurlex.nlex.query.ParagraphRole;
import eu.europa.eurlex.nlex.query.References;
import eu.europa.eurlex.nlex.query.Request;
import eu.europa.eurlex.nlex.query.Result;
import eu.europa.eurlex.nlex.query.ResultList;
import eu.europa.eurlex.nlex.query.ResultList.Documents;
import eu.europa.eurlex.nlex.query.ResultList.Navigation;
import eu.europa.eurlex.nlex.query.Title;

/**
 * A base class for SOAP connector. 
 * @author Mariusz Jakubowski
 *
 */
public abstract class AbstractConnector {

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
            String xsd = new String(fileBytes, "UTF-8");
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
    protected eu.europa.eurlex.nlex.query.Request unmarshallRequest(String query) {
        try {
            query = insertNamespace(query);
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

    /**
     * Creates a XML representation of a document 
     * that will be returned in result list.
     * @param doc
     * @return
     */
    protected DocumentSpecification createDoc(Document doc) {
        DocumentSpecification docSpec = new DocumentSpecification();
        
        Metadata meta = new Metadata();       
        docSpec.setMetadata(meta);
        
        DocNumber num = new DocNumber();
        num.setRole("nlex:doc_number");
        num.setContent(doc.getNumber());
        meta.getDocNumber().add(num);
        
        if (doc.getType() != null) {
            DocType type = new DocType();
            type.setValue(doc.getType());
            meta.getDocType().add(type);
        }
        
        if (doc.getDocDate() != null) { 
            DateOfDoc dateDoc = new DateOfDoc();
            fillDate(dateDoc, doc.getDocDate());
            meta.setDateOfDoc(dateDoc);
        }
    
        PubSource pub = new PubSource();
        if (doc.getOJName() != null) {
            OJName oj = new OJName();
            oj.setValue(doc.getOJName());
            pub.setOJName(oj);
        }
        if (doc.getDateOfPub() != null) {
            OJNumber ojn = new OJNumber();
            ojn.setContent(doc.getOJNumber());
            pub.setOJNumber(ojn);
        }
        if (doc.getDateOfPub() != null) {
            NlexDate datePub = new NlexDate(); 
            fillDate(datePub, doc.getDateOfPub());
            pub.setDateOfPub(datePub);
            meta.getPubSource().add(pub);
        }
        
        References refs = new References();
        docSpec.setReferences(refs);
        
        for (Reference ref : doc.getReferences()) {
            ExternUrl url = new ExternUrl();
            url.setDisplay(ref.getDisplay());
            url.setFormat(ref.getFormat());
            url.setHref(ref.getHRef());
            refs.getExternUrl().add(url);
        }
        
        Content content = new Content();
        docSpec.setContent(content);
        content.setLang(doc.getTitleLang());
        Title title = new Title();
        title.getRoles().add(ParagraphRole.TITLE);
        title.getContent().add(doc.getTitle());
        content.getContent().add(title);
        return docSpec;
    }

    /**
     * Creates a Result object.
     * @param siteUrl the URL of the site with legal acts
     * @param connectorUrl the URL of the connector
     * @return the Result object
     */
    protected eu.europa.eurlex.nlex.query.Result createResult(String siteUrl, String connectorUrl) {
        eu.europa.eurlex.nlex.query.Result result = new eu.europa.eurlex.nlex.query.Result();
        result.setSite(siteUrl);
        result.setConnector(connectorUrl);
        return result;
    }

    /**
     * Adds an error to the result object.
     * @param result a Result object
     * @param errorCode an error code
     */
    protected void addError(eu.europa.eurlex.nlex.query.Result result, ErrorCode errorCode) {
        result.setStatus("error");
        if (result.getErrors() == null) {
            result.setErrors(new Errors());
        }
        Errors errors = result.getErrors();
        Error error = new Error();
        error.setCause(BigInteger.valueOf(errorCode.ordinal()));
        errors.getError().add(error);
    }
    
    /**
     * Adds a new error message to the Result object.
     * @param result a Result object
     * @param errorMessage an error message
     */
    protected void addError(eu.europa.eurlex.nlex.query.Result result, String errorMessage) {
        result.setStatus("error");
        if (result.getErrors() == null) {
            result.setErrors(new Errors());
        }
        Errors errors = result.getErrors();
        Error error = new Error();
        error.setContent(errorMessage);
        errors.getError().add(error);
    }

    /**
     * Creates a container for documents in Result object.
     * @param result a Result object
     * @param docCount a total number of documents that match the criteria
     * @param pageSize a number of items per page
     * @param builder a builder with query
     * @return a container for documents
     */
    protected Documents createDocuments(eu.europa.eurlex.nlex.query.Result result, int docCount, int pageSize, QueryBuilder builder) {
        result.setStatus("OK");
        ResultList results = new ResultList();
        result.setResultList(results);
        Navigation nav = createNav(null, docCount, pageSize, builder.getPage());
        results.setNavigation(nav);
        Documents docs = new Documents();
        results.setDocuments(docs);
        return docs;
    }

    /**
     * Parses the query and builds a query object via {@link QueryBuilder}.
     * @param query a query to parse
     * @param builder a builder used to create query object
     * @return true if query was parsed correctly, false otherwise
     */
    protected boolean parseQuery(String query, QueryBuilder builder) {
        try {
            eu.europa.eurlex.nlex.query.Request request = unmarshallRequest(query);
            QueryParser parser = new QueryParser(builder, request);
            parser.parse();
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
