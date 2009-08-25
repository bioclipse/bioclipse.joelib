package jmat.io.data;

import jmat.data.Matrix;

import jmat.io.data.fileTools.MatrixMML;
import jmat.io.data.fileTools.XMLFile;

import java.io.File;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class MatrixMMLFile
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private File file;
    private Matrix M;

    //~ Constructors ///////////////////////////////////////////////////////////

    public MatrixMMLFile(File f)
    {
        file = f;

        if (file.exists())
        {
            M = MatrixMML.readMatrix(XMLFile.fromFile(file));
        }
        else
        {
            M = new Matrix(0, 0);
            throw new IllegalArgumentException("File does not exist.");
        }
    }

    public MatrixMMLFile(String fn)
    {
        file = new File(fn);

        if (file.exists())
        {
            M = MatrixMML.readMatrix(XMLFile.fromFile(file));
        }
        else
        {
            M = new Matrix(0, 0);
            throw new IllegalArgumentException("File does not exist.");
        }
    }

    public MatrixMMLFile(File f, Matrix m)
    {
        M = m;
        file = f;
        XMLFile.toFile(file, MatrixMML.printMatrix(M));
    }

    public MatrixMMLFile(String fn, Matrix m)
    {
        M = m;
        file = new File(fn);
        XMLFile.toFile(file, MatrixMML.printMatrix(M));
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
