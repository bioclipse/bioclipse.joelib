package jmat.io.gui.plotTools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class NotedPoint3D
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private Axe3D axe;
    private Grid3D grid;
    private boolean isVisible;
    private double[] Pl = new double[3];
    private double[] Pl_0 = new double[3];
    private double[] Pl_X0 = new double[3];
    private double[] Pl_Y0 = new double[3];
    private double[] Pl_Z0 = new double[3];
    private int[] Sc = new int[2];
    private int[] Sc_X0 = new int[2];
    private int[] Sc_Y0 = new int[2];
    private int[] Sc_Z0 = new int[2];

    //~ Constructors ///////////////////////////////////////////////////////////

    public NotedPoint3D()
    {
        setVisible(false);
    }

    public NotedPoint3D(double x, double y, double z, Axe3D ax, Grid3D g)
    {
        axe = ax;
        grid = g;

        Pl[0] = x;
        Pl[1] = y;
        Pl[2] = z;

        Pl_0 = axe.getPl0();

        Pl_X0[0] = Pl_0[0];
        Pl_X0[1] = y;
        Pl_X0[2] = z;

        Pl_Y0[0] = x;
        Pl_Y0[1] = Pl_0[1];
        Pl_Y0[2] = z;

        Pl_Z0[0] = x;
        Pl_Z0[1] = y;
        Pl_Z0[2] = Pl_0[2];

        Pl2ScConvert();

        setVisible(true);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void draw(Graphics2D comp2D)
    {
        if (isVisible)
        {
            Pl2ScConvert();
            comp2D.setColor(Color.black);
            comp2D.setFont(new Font("Arial", Font.PLAIN, 12));
            comp2D.drawLine(Sc[0], Sc[1], Sc_X0[0], Sc_X0[1]);
            comp2D.drawLine(Sc[0], Sc[1], Sc_Y0[0], Sc_Y0[1]);
            comp2D.drawLine(Sc[0], Sc[1], Sc_Z0[0], Sc_Z0[1]);

            comp2D.drawString(new String(
                    "(" + grid.troncatedStringX(Pl[0], 2) + "," +
                    grid.troncatedStringY(Pl[1], 2) + "," +
                    grid.troncatedStringZ(Pl[2], 2) + ")"), Sc[0], Sc[1]);

            //comp2D.drawString(new String(""+Pl[0]),Sc_Y0[0],Sc_Y0[1]);
            //comp2D.drawString(new String(""+Pl[1]),Sc_X0[0],Sc_X0[1]);
            //comp2D.drawString(new String(""+Pl[2]),Sc_Z0[0],Sc_Z0[1]);
        }
    }

    public void setVisible(boolean b)
    {
        isVisible = b;
    }

    private void Pl2ScConvert()
    {
        Sc = axe.Pl2Sc(Pl);
        Sc_X0 = axe.Pl2Sc(Pl_X0);
        Sc_Y0 = axe.Pl2Sc(Pl_Y0);
        Sc_Z0 = axe.Pl2Sc(Pl_Z0);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
