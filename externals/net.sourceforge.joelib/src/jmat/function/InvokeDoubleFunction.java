package jmat.function;

import jmat.data.Matrix;

import jmat.io.data.MatrixFile;

import java.io.File;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class InvokeDoubleFunction
{
    //~ Instance fields ////////////////////////////////////////////////////////

    File functionFile;
    File resultFile;

    //~ Constructors ///////////////////////////////////////////////////////////

    public InvokeDoubleFunction(String fn, String rf)
    {
        functionFile = new File(fn);
        resultFile = new File(rf);
    }

    public InvokeDoubleFunction(File fn, File rf)
    {
        functionFile = fn;
        resultFile = rf;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public double eval()
    {
        try
        {
            Process p = Runtime.getRuntime().exec(functionFile.getName());
            p.waitFor();

            MatrixFile mf = new MatrixFile(resultFile);
            Matrix X = mf.getMatrix();

            return new Double(X.get(0, 0)).doubleValue();
        }
        catch (Exception e)
        {
            System.out.println("Error : File " + resultFile + " unreadable : " +
                e);

            return Double.NaN;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
