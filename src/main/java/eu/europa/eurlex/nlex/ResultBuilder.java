package eu.europa.eurlex.nlex;

import java.io.StringWriter;
import java.math.BigInteger;
import java.time.LocalDate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
import eu.europa.eurlex.nlex.query.Result;
import eu.europa.eurlex.nlex.query.ResultList;
import eu.europa.eurlex.nlex.query.ResultList.Documents;
import eu.europa.eurlex.nlex.query.ResultList.Navigation;
import eu.europa.eurlex.nlex.query.Title;

/**
 * A wrapper for connector response. This class contains methods that simplify
 * building a connector response in the XML format. 
 * @author Mariusz Jakubowski
 *
 */
public class ResultBuilder {
    
    private eu.europa.eurlex.nlex.query.Result result;

    /**
     * Creates a Result object.
     * @param siteUrl the URL of the site with legal acts
     * @param connectorUrl the URL of the connector
     * @return the Result object
     */
    public ResultBuilder(String siteUrl, String connectorUrl) {
        result = new eu.europa.eurlex.nlex.query.Result();
        result.setSite(siteUrl);
        result.setConnector(connectorUrl);
    }
    
    public eu.europa.eurlex.nlex.query.Result getResult() {
        return result;
    }

    /**
     * Adds an error to the result object.
     * @param errorCode an error code
     */
    public void addError(ErrorCode errorCode) {
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
     * @param errorMessage an error message
     */
    public void addError(String errorMessage) {
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
     * @param requestId a request id, can be null
     * @param hits a total number of documents that match the criteria
     * @param pageSize a number of items per page
     * @param pageNum the current page number (can be read from {@link QueryBuilder#getPage()})
     * @return a container for documents
     */
    public Documents createDocuments(String requestId, int hits, int pageSize, int pageNum) {
        result.setStatus("OK");
        ResultList results = new ResultList();
        result.setResultList(results);
        Navigation nav = createNav(null, hits, pageSize, pageNum);
        results.setNavigation(nav);
        Documents docs = new Documents();
        results.setDocuments(docs);
        return docs;
    }

    /**
     * Creates a navigation part of a result.
     * @param requestId a request id, can be null
     * @param hits a total number of documents found
     * @param pageSize the number of documents on one page 
     * @param pageNum the current page number
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
     * Adds documents to a document container. This method iterates over results 
     * and for each document it converts the {@link Document} interface 
     * to the {@link DocumentSpecification} object 
     * and adds this object to the {@link Documents} list.
     * @param foundDocs an iterator with result list
     */
    public void addDocuments(QueryResult foundDocs) {
        Documents docs = result.getResultList().getDocuments();
        while (foundDocs.hasNext()) {
            Document doc = foundDocs.next();
            DocumentSpecification docSpec = createDoc(doc);
            docs.getDocument().add(docSpec);
        }
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
     * Marshalls a {@link Result} object into a string.
     * @return a string with XML result
     */
    public String marshall() {
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
    
}
