package jmat.io.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class FrameView extends JFrame
{
    //~ Constructors ///////////////////////////////////////////////////////////

    public FrameView(JPanel panel)
    {
        setContentPane(panel);
        pack();
        show();
    }

    public FrameView(JPanel[] panels)
    {
        JPanel panel = new JPanel();

        for (int i = 0; i < panels.length; i++)
        {
            panel.add(panels[i]);
        }

        setContentPane(panel);
        pack();
        show();
    }

    public FrameView(String title, JPanel panel)
    {
        super(title);
        setContentPane(panel);
        pack();
        show();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
