package eu.europa.eurlex.nlex.query;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * An enum for <code>string-comparator</code> type.
 * 
 * <p>A <code>string-comparator</code> type is not matched as enum in JAXB.
 * It extends a <code>comparator</code> type with new values.</p>
 * 
 * @author Mariusz Jakubowski
 *
 */
@XmlType(name = "string-comparator")
@XmlEnum
public enum StringComparator {

    @XmlEnumValue("eq")
    EQ("eq"),
    @XmlEnumValue("ne")
    NE("ne"),
    @XmlEnumValue("lt")
    LT("lt"),
    @XmlEnumValue("le")
    LE("le"),
    @XmlEnumValue("gt")
    GT("gt"),
    @XmlEnumValue("ge")
    GE("ge"),

    @XmlEnumValue("match")
    MATCH("match"),
    @XmlEnumValue("like")
    LIKE("like"),
    @XmlEnumValue("truncated")
    TRUNCATED("truncated"),
    @XmlEnumValue("contains")
    CONTAINS("contains"),
    @XmlEnumValue("sound")
    SOUND("sound"),
    ;
    private final String value;

    StringComparator(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StringComparator fromValue(String v) {
        if (v == null) {
            return EQ;
        }
        for (StringComparator c: StringComparator.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
