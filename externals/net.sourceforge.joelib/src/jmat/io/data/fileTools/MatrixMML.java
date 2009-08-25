package jmat.io.data.fileTools;

import jmat.data.Matrix;

import java.util.List;

import org.jdom.Element;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class MatrixMML
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private Element E;
    private Matrix M;

    //~ Constructors ///////////////////////////////////////////////////////////

    public MatrixMML(Matrix m)
    {
        M = m;
        E = printMatrix(M);
    }

    public MatrixMML(Element e)
    {
        E = e;
        M = readMatrix(E);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Element printMatrix(Matrix m)
    {
        Element e = new Element("matrix");

        for (int i = 0; i < m.getRowDimension(); i++)
        {
            Element ei = new Element("matrixrow");

            for (int j = 0; j < m.getColumnDimension(); j++)
            {
                Element ej = new Element("cn");
                ej.addContent(new String("" + m.get(i, j)));
                ei.addContent(ej);
            }

            e.addContent(ei);
        }

        return e;
    }

    public static Matrix readMatrix(Element e)
    {
        List allRows = e.getChildren();
        List firstRow = ((Element) allRows.get(0)).getChildren();
        Matrix m = new Matrix(allRows.size(), firstRow.size());

        for (int i = 0; i < m.getRowDimension(); i++)
        {
            List currentRow = ((Element) allRows.get(i)).getChildren();

            for (int j = 0; j < m.getColumnDimension(); j++)
            {
                Element current = ((Element) currentRow.get(j));
                m.set(i, j, Double.parseDouble(current.getText()));
            }
        }

        return m;
    }

    public Element getElement()
    {
        return E;
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
