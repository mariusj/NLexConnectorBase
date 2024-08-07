package eu.europa.eurlex.nlex;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import eu.europa.eurlex.nlex.query.Comparator;
import eu.europa.eurlex.nlex.query.FTSearchWords;
import eu.europa.eurlex.nlex.query.FTSearchWords.Operator;
import eu.europa.eurlex.nlex.query.Index;
import eu.europa.eurlex.nlex.query.Request;
import eu.europa.eurlex.nlex.query.StringComparator;

public class QueryParserTest {

    /**
     * You can construct an SQL query in similar way, but please don't use
     * string concatenation for parameters. 
     *
     */
    public class TestQueryBuilder implements QueryBuilder {
        
        String query = "";

        @Override
        public void setPage(int page) {
        }

        @Override
        public void setRequestID(String requestID) {
        }

        @Override
        public void filterIndex(Index indexName, int year, int month, int day, Comparator compare) {
            query += " AND " + indexName.value() + " " + compare.name() + " '" + year + "-" + month + "-" + day + "'";
        }

        @Override
        public void filterIndex(Index indexName, int value, Comparator compare) {
            query += " AND " + indexName.value() + " " + compare.name() + " " + value;
        }

        @Override
        public void filterIndex(Index indexName, String value, StringComparator compare) {
            query += " AND " + indexName.value() + " " + compare.name() + " '" + value + "'";
        }
        
        

        @Override
        public void filterWords(Index indexName, FTSearchWords words) {
            query += " AND (";
            query += filterWordsPart(indexName, words.getOperator(), words.getChildren());
            query += ")";
        }
        
        private String filterWordsPart(Index indexName, Operator operator, List<FTSearchWords> children) {
            String q = "";
            for (FTSearchWords child : children) {
                if (q.length() > 0) {
                    q += " " + operator.name() + " ";
                }
                q += "(";
                if (child.getOperator() == Operator.CONTAINS) {
                    q += indexName.value() + " " + child.getOperator() + " '" + child.getValue() + "'";
                } else {
                    q += filterWordsPart(indexName, child.getOperator(), child.getChildren());
                }
                q += ")";
            }
            return q;
        }
        
        public String getQuery() {
            return query.length() > 0 ? query.substring(5) : query;
        }

        @Override
        public int getPage() {
            return 0;
        }

    }

    @Test
    public void testSimple() {
        AbstractConnector conn = new AbstractConnectorTest();
        String query = "<request><navigation></navigation><criteria><and><string.value idx-name=\"nlex:doc_type\">LAW</string.value><words idx-name=\"nlex:title\" lang=\"es-ES\"><contains>orden</contains></words></and></criteria></request>";
        Request request = conn.unmarshallRequest(query);
        TestQueryBuilder builder = new TestQueryBuilder();
        QueryParser parser = new QueryParser(builder, request);
        parser.parse();
        assertEquals("doc_type EQ 'LAW' AND title CONTAINS 'orden'", builder.getQuery());
    }
    
    @Test
    public void testFT() {
        String query = "<request><navigation></navigation><criteria>    <and>\n" + 
                "      <words idx-name=\"nlex:fulltext\" lang=\"en-UK\">\n" + 
                "        <and>\n" + 
                "          <or>\n" + 
                "            <contains>Sample expression</contains>\n" + 
                "            <contains>Another expression</contains>\n" + 
                "            <contains>word</contains>\n" + 
                "          </or>\n" + 
                "          <contains>something else</contains>\n" + 
                "        </and>\n" + 
                "      </words>\n" +
                "     </and>\n" +  
                "</criteria></request>";
        AbstractConnector conn = new AbstractConnectorTest();
        Request request = conn.unmarshallRequest(query);
        TestQueryBuilder builder = new TestQueryBuilder();
        QueryParser parser = new QueryParser(builder, request);
        parser.parse();
        String ftq = "(((fulltext CONTAINS 'Sample expression') OR (fulltext CONTAINS 'Another expression') OR (fulltext CONTAINS 'word')) AND (fulltext CONTAINS 'something else'))";
        assertEquals(ftq, builder.getQuery());
    }
}
