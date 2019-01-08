package eu.europa.eurlex.nlex;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.junit.Test;

import eu.europa.eurlex.nlex.query.BinaryOp;
import eu.europa.eurlex.nlex.query.ContainsType;
import eu.europa.eurlex.nlex.query.Navigation;
import eu.europa.eurlex.nlex.query.NlexDate;
import eu.europa.eurlex.nlex.query.Request;
import eu.europa.eurlex.nlex.query.Request.Criteria;
import eu.europa.eurlex.nlex.query.Result;
import eu.europa.eurlex.nlex.query.ResultList;
import eu.europa.eurlex.nlex.query.ResultList.Documents;
import eu.europa.eurlex.nlex.query.StringValue;
import eu.europa.eurlex.nlex.query.WordsType;

public class AbstractConnectorTest extends AbstractConnector {

    @Test
    public void testUnmarshallRequest() {
        String query = "<request xmlns:nlex=\"http://n-lex.europa.eu/xml/schemas/query/legislation/\"><navigation></navigation><criteria><and><string.value idx-name=\"nlex:doc_type\">LAW</string.value><words idx-name=\"nlex:title\" lang=\"es-ES\"><contains>orden</contains></words></and></criteria></request>";
        Request request = unmarshallRequest(query);
        assertNotNull(request);
        Navigation navigation = request.getNavigation();
        assertNotNull(navigation);
        Criteria criteria = request.getCriteria();
        assertNotNull(criteria);
        BinaryOp and = criteria.getAnd();
        assertNotNull(and);
        List<Object> objects = and.getWordsOrDateOrStringValue();
        assertEquals(2, objects.size());
        StringValue str = (StringValue) objects.get(0);
        assertEquals("LAW", str.getValue());
        QName idxName = str.getIdxName();
        assertNotNull("index should not be null", idxName);
        assertEquals("doc_type", idxName.getLocalPart());
        @SuppressWarnings("unchecked")
        JAXBElement<WordsType> jaxb = (JAXBElement<WordsType>) objects.get(1);
        WordsType words = jaxb.getValue();
        assertNotNull(words);
        assertNotNull("index should not be null", words.getIdxName());
        ContainsType contains = words.getContains();
        assertNotNull(contains);
        String value = contains.getValue();
        assertEquals("orden", value);
    }
    
    @Test
    public void testFillDate() {
        LocalDate date = LocalDate.of(2019, 1, 8);
        NlexDate dateXML = new NlexDate();
        fillDate(dateXML, date);
        assertEquals(2019, dateXML.getYear().getYear());
        assertEquals(BigInteger.valueOf(1), dateXML.getMonth());
        assertEquals(BigInteger.valueOf(8), dateXML.getDay());
    }
    
    @Test
    public void testMarshallResult() {
        Result result = new Result();
        ResultList rl = new ResultList();
        eu.europa.eurlex.nlex.query.ResultList.Navigation nav = createNav(null, 10, 5, 1);
        rl.setNavigation(nav);
        Documents docs = new Documents();
        rl.setDocuments(docs );
        result.setResultList(rl);
        String xml = marshallResult(result);
        assertNotNull(xml);
        assertThat(xml, containsString("result"));
        assertThat(xml, containsString("result-list"));
        assertThat(xml, containsString("navigation"));
        assertThat(xml, containsString("documents"));
    }
    
    @Test
    public void testInsertNamespace() {
        String q1 = insertNamespace("<request></request>");
        assertThat(q1, containsString(NLEX_NS));
        String q2 = insertNamespace("<request xmlns:ns=\"http://www.w3.org/1999/xlink\"></result>");
        assertThat(q2, containsString(NLEX_NS));
    }
    
    @Test
    public void testReadFile() {
        String file = readFile("xsd/types.xsd");
        assertNotNull(file);
    }
    
}
