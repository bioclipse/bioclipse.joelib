package jmat.data.matrixDecompositions;

/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class Mathfun
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /** sqrt(a^2 + b^2) without under/overflow. **/
    public static double hypot(double a, double b)
    {
        double r;

        if (Math.abs(a) > Math.abs(b))
        {
            r = b / a;
            r = Math.abs(a) * Math.sqrt(1 + (r * r));
        }
        else if (b != 0)
        {
            r = a / b;
            r = Math.abs(b) * Math.sqrt(1 + (r * r));
        }
        else
        {
            r = 0.0;
        }

        return r;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
