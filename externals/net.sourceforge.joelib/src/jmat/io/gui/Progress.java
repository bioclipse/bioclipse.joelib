package jmat.io.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class Progress extends JFrame
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private int max;
    private int min;

    private JPanel pane;
    private JProgressBar progress;
    private int val;

    //~ Constructors ///////////////////////////////////////////////////////////

    public Progress(int m, int M)
    {
        min = m;
        max = M;

        val = min;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pane = new JPanel();

        progress = new JProgressBar(min, max);
        progress.setValue(val);
        progress.setString(null);

        pane.add(progress);
        this.setContentPane(pane);
        this.pack();
        this.setVisible(true);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setValue(int n)
    {
        val = n;
        progress.setValue(val);

        if (val >= max)
        {
            setVisible(false);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
