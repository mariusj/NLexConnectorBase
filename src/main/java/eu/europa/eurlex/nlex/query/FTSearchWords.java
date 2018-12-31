package eu.europa.eurlex.nlex.query;

import java.util.ArrayList;
import java.util.List;

/**
 * A tree structure with full-text search query.
 * @author Mariusz Jakubowski
 *
 */
public class FTSearchWords {
    
    /**
     * An operator in full-text search query.
     *
     */
    public static enum Operator {
        AND,
        OR, 
        NOT,
        CONTAINS;

        public static Operator fromName(String localPart) {
            switch (localPart) {
            case "or": return OR;
            case "and": return AND;
            case "not": return NOT;
            }
            return null;
        }
    }
    
    private List<FTSearchWords> children;
    
    private Operator operator;

    private String value;

    /**
     * Constructs a query node with logical operator and children. 
     * @param op an operator (<code>AND</code> or <code>OR</code>)
     */
    public FTSearchWords(Operator op) {
        this.operator = op;
        this.children =  new ArrayList<>();
    }
    
    /**
     * Constructs a query node for contains operator.
     * @param value a value to search for
     */
    public FTSearchWords(String value) {
        this.operator = Operator.CONTAINS;
        this.value = value;
    }

    /**
     * Returns a type of node (operator).
     * @return
     */
    public Operator getOperator() {
        return operator;
    }
    
    /**
     * Returns a list of children (for nodes of type 
     * <code>AND</code> or <code>OR</code>).
     * @return
     */
    public List<FTSearchWords> getChildren() {
        return children;
    }
    
    /**
     * Returns a value to search (for <code>CONTAINS</code> node).
     * @return
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Adds a new children of this node.
     * @param child a child to add
     */
    public void addChild(FTSearchWords child) {
        children.add(child);        
    }

    
    @Override
    public String toString() {
        if (operator == Operator.CONTAINS) {
            return "'" + value + "'";
        }
        if (children == null) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        b.append('(');
        boolean op = false;
        for (FTSearchWords c : children) {
            if (op) {
                b.append(' ');
                b.append(operator.name());
                b.append(' ');
            } else {
                op = true;
            }
            b.append(c);
        }
        b.append(')');
        return b.toString();
    }

}
