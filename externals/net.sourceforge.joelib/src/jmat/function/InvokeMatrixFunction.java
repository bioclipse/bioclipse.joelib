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
public class InvokeMatrixFunction
{
    //~ Instance fields ////////////////////////////////////////////////////////

    File functionFile;
    File resultFile;

    //~ Constructors ///////////////////////////////////////////////////////////

    public InvokeMatrixFunction(String fn, String rf)
    {
        functionFile = new File(fn);
        resultFile = new File(rf);
    }

    public InvokeMatrixFunction(File fn, File rf)
    {
        functionFile = fn;
        resultFile = rf;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Matrix eval()
    {
        try
        {
            Process p = Runtime.getRuntime().exec(functionFile.getName());
            p.waitFor();

            MatrixFile mf = new MatrixFile(resultFile);
            Matrix X = mf.getMatrix();

            return X;
        }
        catch (Exception e)
        {
            System.out.println("Error : File " + resultFile + " unreadable : " +
                e);

            return null;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
