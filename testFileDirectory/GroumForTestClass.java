
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroumForTestClass {

    private List<AbstractStatement> statementList;
    private List<AbstractExpression> expressionList;

    public List<TryStatementObject> getTryStatements() {
        List<TryStatementObject> tryStatements = new ArrayList<TryStatementObject>();
        int size = this.statementList.size();
        if(this.getType().equals(StatementType.TRY))
            tryStatements.add((TryStatementObject)this);
        for(AbstractStatement statement : statementList) {
            if(statement instanceof CompositeStatementObject) {
                CompositeStatementObject composite = (CompositeStatementObject)statement;
                tryStatements.addAll(composite.getTryStatements());
            }
        }
        return tryStatements;
    }

    /*public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getType().toString());
        if (getType() == StatementType.ENHANCED_FOR) {
            sb.append("(");
            for(int i=0; i<expressionList.size()-1; i++) {
                sb.append(expressionList.get(i).getLocalVariableDeclarations().get(0)).append(" : ");
            }
            sb.append(expressionList.get(expressionList.size()-1).toString());
            sb.append(")");
        }
        else if(expressionList.size() > 0) {
            sb.append("(");
            for(int i=0; i<expressionList.size()-1; i++) {
                sb.append(expressionList.get(i).toString()).append("; ");
            }
            sb.append(expressionList.get(expressionList.size()-1).toString());
            sb.append(")");
        }
        sb.append("\n");
        return sb.toString();
    }*/
}