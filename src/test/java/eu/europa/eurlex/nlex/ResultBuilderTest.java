package eu.europa.eurlex.nlex;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigInteger;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import eu.europa.eurlex.nlex.query.NlexDate;

public class ResultBuilderTest extends ResultBuilder {

    public ResultBuilderTest() {
        super(null, null);
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
        createDocuments(null, 10, 5, 1);
        String xml = marshall();
        assertNotNull(xml);
        assertThat(xml, containsString("result"));
        assertThat(xml, containsString("result-list"));
        assertThat(xml, containsString("navigation"));
        assertThat(xml, containsString("documents"));
    }
    
}
