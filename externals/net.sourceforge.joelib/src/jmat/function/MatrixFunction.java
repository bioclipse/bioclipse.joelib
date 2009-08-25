package jmat.function;

import jmat.data.Matrix;


/**
 * <p>Titre : JAva MAtrix TOols</p>
 * <p>Description : </p>
 * @.author Yann RICHET
 * @version 1.0
 */
public abstract class MatrixFunction
{
    //~ Instance fields ////////////////////////////////////////////////////////

    protected int argNumber;

    //~ Methods ////////////////////////////////////////////////////////////////

    public abstract Matrix eval(Matrix[] values);

    public void checkArgNumber(int n)
    {
        if (argNumber != n)
        {
            throw new IllegalArgumentException(
                "Number of arguments must equals " + n);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
