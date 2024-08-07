package eu.europa.eurlex.nlex;

import java.math.BigInteger;
import java.util.List;

import jakarta.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europa.eurlex.nlex.query.BinaryOp;
import eu.europa.eurlex.nlex.query.BinaryOpWords;
import eu.europa.eurlex.nlex.query.Comparator;
import eu.europa.eurlex.nlex.query.ContainsType;
import eu.europa.eurlex.nlex.query.DateType;
import eu.europa.eurlex.nlex.query.FTSearchWords;
import eu.europa.eurlex.nlex.query.FTSearchWords.Operator;
import eu.europa.eurlex.nlex.query.Index;
import eu.europa.eurlex.nlex.query.IntValue;
import eu.europa.eurlex.nlex.query.Navigation;
import eu.europa.eurlex.nlex.query.Navigation.Page;
import eu.europa.eurlex.nlex.query.Request;
import eu.europa.eurlex.nlex.query.Request.Criteria;
import eu.europa.eurlex.nlex.query.StringComparator;
import eu.europa.eurlex.nlex.query.StringValue;
import eu.europa.eurlex.nlex.query.WordsType;

/**
 * Parses the request.
 * While parsing methods from {@link QueryParser} are invoked allowing you
 * to create a query for your legislative system. 
 * @author Mariusz Jakubowski
 *
 */
public class QueryParser {

    private static final Logger log = LogManager.getLogger(QueryParser.class);
    
    public static final String UTF16_BOM = "\uFEFF";
    public static final char[] UTF8_BOM_C = { 0xEF, 0xBB, 0xBF };
    public static final String UTF8_BOM = new String(UTF8_BOM_C);

    private QueryBuilder builder;
    private Request request;

    public QueryParser(QueryBuilder builder, Request request) {
        this.builder = builder;
        this.request = request;
    }
    
    /**
     * Starts the parse process.
     */
    public void parse() {
        parseNav();
        parseCriteria();
    }
    
    /**
     * Parses the navigation part of the query.
     * @param request
     */
    protected void parseNav() {
        Navigation nav = request.getNavigation();
        if (nav != null) {
            String requestId = nav.getRequestId();
            builder.setRequestID(requestId);
            Page page = nav.getPage();
            if (page != null) {
                int pageNum = page.getNumber();
                builder.setPage(pageNum);
            }
        }
    }
    
    /**
     * Parse the criteria part of the query.
     */
    protected void parseCriteria() {
        Criteria criteria = request.getCriteria();
        BinaryOp and = criteria.getAnd();
        List<Object> ands = and.getWordsOrDateOrStringValue();
        for (Object object : ands) {
            log.info(object.toString());
            if (object instanceof JAXBElement) {
                object = ((JAXBElement<?>) object).getValue();
                log.info("-> {}", object);                
            }
            if (object instanceof StringValue) {
                parseStringIndex((StringValue) object);
            } else if (object instanceof IntValue) {
                parseIntIndex((IntValue) object);
            } else if (object instanceof DateType) {
                parseDateIndex((DateType) object);
            } else if (object instanceof WordsType) {
                parseFTIndex((WordsType) object);
            }
        }
    }
    
    /**
     * Parse the full-text query.
     * @param words a ft-query to parse
     */
    protected void parseFTIndex(WordsType words) {
        try {
            QName idxName = words.getIdxName();
            if (idxName != null) {
                ContainsType contains = words.getContains();
                if (contains != null) {
                    // simple filter by one value
                    builder.filterIndex(Index.fromValue(idxName.getLocalPart()), 
                            removeUTF8BOM(contains.getValue()), 
                            StringComparator.CONTAINS);
                } else {
                    parseFTIndexComplex(words, idxName);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses the complex form of full-text query.
     * @param words a tree with query
     * @param idxName a name of the index
     */
    protected void parseFTIndexComplex(WordsType words, QName idxName) {
        // filter with and/or
        FTSearchWords root;
        BinaryOpWords rootX;
        if (words.getAnd() != null) {
            root = new FTSearchWords(Operator.AND);
            rootX = words.getAnd();
        } else if (words.getOr() != null) {
            root = new FTSearchWords(Operator.OR);
            rootX = words.getOr();
        } else {
            return;
        }
        List<JAXBElement<?>> booleanWords = rootX.getBooleanWords();
        parseBWords(root, booleanWords);
        builder.filterWords(Index.fromValue(idxName.getLocalPart()), root);
    }

    /**
     * Parses part of a tree of full-text query.
     * @param root a root of the parsed tree
     * @param booleanWords a list of elements to parse
     */
    protected void parseBWords(FTSearchWords root, List<JAXBElement<?>> booleanWords) {
        for (JAXBElement<?>  b : booleanWords) {
            Object bv = b.getValue();
            if (bv instanceof ContainsType) {
                String value = removeUTF8BOM(((ContainsType) bv).getValue());
                FTSearchWords s = new FTSearchWords(value);
                root.addChild(s);
            } else if (bv instanceof BinaryOpWords) {
                BinaryOpWords opw = (BinaryOpWords) bv;
                List<JAXBElement<?>> children = opw.getBooleanWords();
                FTSearchWords newRoot = new FTSearchWords(
                        Operator.fromName(b.getName().getLocalPart()));
                root.addChild(newRoot);
                parseBWords(newRoot, children);
            }
        }
    }

    /**
     * Parses filtering by date value.
     * @param dt a date index to parse
     */
    protected void parseDateIndex(DateType dt) {
        QName idxName = dt.getIdxName();
        if (idxName != null) {
            XMLGregorianCalendar year = dt.getYear();
            BigInteger month = dt.getMonth();
            BigInteger day = dt.getDay();
            int y = -1;
            int m = -1;
            int d = -1;
            if (year != null) {
                y = year.getYear();
            }
            if (month != null) {
                m = month.intValue();
            }
            if (day != null) {
                d = day.intValue();
            }
            Comparator compare = dt.getCompare();
            if (compare == null) {
                compare = Comparator.EQ;
            }
            builder.filterIndex(Index.fromValue(idxName.getLocalPart()), 
                    y, m, d, compare);
        }
    }

    /**
     * Parses filtering by integer value.
     * @param iv an int index to parse
     */
    protected void parseIntIndex(IntValue iv) {
        QName idxName = iv.getIdxName();
        Comparator compare = iv.getCompare();
        if (compare == null) {
            compare = Comparator.EQ;
        }
        if (idxName != null) {
            builder.filterIndex(Index.fromValue(idxName.getLocalPart()), 
                    iv.getValue(), compare);
        }
    }

    /**
     * Parses filtering by string value.
     * @param sv a string index to parse
     */
    protected void parseStringIndex(StringValue sv) {
        QName idxName = sv.getIdxName();
        if (idxName != null) {
            StringComparator compare = StringComparator.fromValue(sv.getCompare());
            builder.filterIndex(Index.fromValue(idxName.getLocalPart()), 
                    removeUTF8BOM(sv.getValue()), compare);
        }
    }
    
    /**
     * Removes the Byte Order Mark from a string.
     * @param s
     * @return
     */
    public static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF16_BOM)) {
            return s.substring(1);
        } else if (s.startsWith(UTF8_BOM)) {
            return s.substring(3);
        }        
        return s;
    }    
    
}
