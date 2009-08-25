package jmat.function;

import jmat.data.Matrix;

import jmat.function.expressionParser.Evaluator;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class TestDoubleFunctionExpression extends TestDoubleFunction
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private Evaluator E = new Evaluator();

    //private int argNumber;
    private String expression;
    private String[] variables;

    //~ Constructors ///////////////////////////////////////////////////////////

    public TestDoubleFunctionExpression(String exp, String[] vars)
    {
        argNumber = vars.length;
        setFunction(exp, vars);
    }

    public TestDoubleFunctionExpression(String exp, String vars)
    {
        argNumber = 1;

        String[] variable = new String[1];
        variable[0] = vars;
        setFunction(exp, variable);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean eval(double[] values)
    {
        checkArgNumber(values.length);
        setVariableValues(values);

        boolean bool = (((Double) (E.getValue())).doubleValue() > 0.5) ? (true)
                                                                       : (false);

        return bool;
    }

    public boolean eval(double value)
    {
        checkArgNumber(1);
        setVariableValues(value);

        boolean bool = (((Double) (E.getValue())).doubleValue() > 0.5) ? (true)
                                                                       : (false);

        return bool;
    }

    public boolean eval(Matrix values)
    {
        checkArgNumber(values.getRowDimension());
        setVariableValues(values);

        boolean bool = (((Double) (E.getValue())).doubleValue() > 0.5) ? (true)
                                                                       : (false);

        return bool;
    }

    private void setFunction(String exp, String[] vars)
    {
        expression = exp;
        variables = vars;
    }

    private void setVariableValues(double[] values)
    {
        for (int i = 0; i < variables.length; i++)
        {
            E.addVariable(variables[i], values[i]);
        }

        E.setExpression(expression);
    }

    private void setVariableValues(double value)
    {
        E.addVariable(variables[0], value);
        E.setExpression(expression);
    }

    private void setVariableValues(Matrix values)
    {
        for (int i = 0; i < variables.length; i++)
        {
            E.addVariable(variables[i], values.get(i, 0));
        }

        E.setExpression(expression);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
