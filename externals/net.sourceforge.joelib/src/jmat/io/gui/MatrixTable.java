package jmat.io.gui;

import jmat.data.Matrix;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class MatrixTable extends JPanel
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private Dimension defaultSize = new Dimension(400, 400);
    private DoubleModel model;
    private boolean modificationEnabled = false;
    private JTable table;

    //~ Constructors ///////////////////////////////////////////////////////////

    public MatrixTable(Matrix m)
    {
        setModel(m);
        setAppearence();
        toWindow();
    }

    public MatrixTable(double[][] d)
    {
        setModel(new Matrix(d));
        setAppearence();
        toWindow();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Matrix getMarix()
    {
        return model.getMatrix();
    }

    public void setModificationDisabled()
    {
        modificationEnabled = false;
    }

    public void setModificationEnabled()
    {
        modificationEnabled = true;
    }

    public void update(Matrix m)
    {
        model.setMatrix(m);
    }

    private void setAppearence()
    {
        setPreferredSize(defaultSize);
        setSize(defaultSize);
    }

    private void setModel(Matrix m)
    {
        model = new DoubleModel(m, modificationEnabled);
    }

    private void toWindow()
    {
        table = new JTable(model);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setCellSelectionEnabled(true);

        //table.setRowSelectionAllowed(false);
        //table.setColumnSelectionAllowed(false);
        JPanel panel = new JPanel();
        panel.add(table);

        JScrollPane scrollPane = new JScrollPane(panel);

        scrollPane.setPreferredSize(getSize());
        scrollPane.setSize(getSize());

        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    private class DoubleModel extends AbstractTableModel
    {
        private Matrix M;
        private boolean modificationEnabled;

        public DoubleModel(Matrix m, boolean mE)
        {
            this.setMatrix(m);
            modificationEnabled = mE;
        }

        public int getColumnCount()
        {
            return M.getColumnDimension();
        }

        public Matrix getMatrix()
        {
            return M;
        }

        public int getRowCount()
        {
            return M.getRowDimension();
        }

        public Object getValueAt(int i, int j)
        {
            return new Double(M.get(i, j));
        }

        public boolean isCellEditable(int i, int j)
        {
            return modificationEnabled;
        }

        public void setMatrix(Matrix m)
        {
            M = m;
        }

        public void setValueAt(int i, int j, double v)
        {
            M.set(i, j, v);
        }

        public void setValueAt(Object o, int i, int j)
        {
            M.set(i, j, (Double.parseDouble((String) o)));
            fireTableCellUpdated(i, j);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
