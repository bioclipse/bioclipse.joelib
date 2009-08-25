package jmat.io.data;

import jmat.data.Matrix;

import jmat.io.data.fileTools.CharFile;
import jmat.io.data.fileTools.MatrixString;

import java.io.File;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class MatrixFile
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private File file;
    private Matrix M;

    //~ Constructors ///////////////////////////////////////////////////////////

    public MatrixFile(File f)
    {
        file = f;

        if (file.exists())
        {
            M = MatrixString.readMatrix(CharFile.fromFile(file));
        }
        else
        {
            M = new Matrix(0, 0);
            throw new IllegalArgumentException("File does not exist.");
        }
    }

    public MatrixFile(String fn)
    {
        file = new File(fn);

        if (file.exists())
        {
            M = MatrixString.readMatrix(CharFile.fromFile(file));
        }
        else
        {
            M = new Matrix(0, 0);
            throw new IllegalArgumentException("File does not exist.");
        }
    }

    public MatrixFile(File f, Matrix m)
    {
        M = m;
        file = f;
        CharFile.toFile(file, MatrixString.printMatrix(M));
    }

    public MatrixFile(String fn, Matrix m)
    {
        M = m;
        file = new File(fn);
        CharFile.toFile(file, MatrixString.printMatrix(M));
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public File getFile()
    {
        return file;
    }

    public String getFileName()
    {
        return file.getName();
    }

    public Matrix getMatrix()
    {
        return M;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
