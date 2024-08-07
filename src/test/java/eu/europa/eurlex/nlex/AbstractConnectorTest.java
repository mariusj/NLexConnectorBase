package eu.europa.eurlex.nlex;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.jupiter.api.Test;

import eu.europa.eurlex.nlex.query.BinaryOp;
import eu.europa.eurlex.nlex.query.ContainsType;
import eu.europa.eurlex.nlex.query.Navigation;
import eu.europa.eurlex.nlex.query.Request;
import eu.europa.eurlex.nlex.query.Request.Criteria;
import eu.europa.eurlex.nlex.query.StringValue;
import eu.europa.eurlex.nlex.query.WordsType;
import jakarta.xml.bind.JAXBElement;

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
        assertNotNull(idxName, "index should not be null");
        assertEquals("doc_type", idxName.getLocalPart());
        @SuppressWarnings("unchecked")
        JAXBElement<WordsType> jaxb = (JAXBElement<WordsType>) objects.get(1);
        WordsType words = jaxb.getValue();
        assertNotNull(words);
        assertNotNull(words.getIdxName(),"index should not be null");
        ContainsType contains = words.getContains();
        assertNotNull(contains);
        String value = contains.getValue();
        assertEquals("orden", value);
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

    @Override
    public String version() {
        return null;
    }

    @Override
    public String testQuery(String type) {
        return null;
    }

    @Override
    public String request(String query) {
        return null;
    }

    @Override
    public String aboutConnector(String type) {
        return null;
    }

    @Override
    protected int getPageSize() {
        return 0;
    }

    @Override
    protected QueryBuilder getQueryBuilder() {
        return null;
    }



    @Override
    protected String getSiteUrl() {
        return null;
    }



    @Override
    protected String getConnectorUrl() {
        return null;
    }

    @Override
    protected QueryResult getResults(QueryBuilder builder) {
        return null;
    }
    
}
