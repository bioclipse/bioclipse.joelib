package jmat.io.gui.plotTools;

import java.awt.Color;
import java.awt.Graphics2D;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class DataPlot2D
{
    //~ Static fields/initializers /////////////////////////////////////////////

    public static int PIXEL = PlotAttributes.PIXEL;
    public static int DOT = PlotAttributes.DOT;
    public static int LINE = PlotAttributes.LINE;
    public static int DOTLINE = PlotAttributes.DOTLINE;
    public static int BAR = PlotAttributes.BAR;
    public static int DOTBAR = PlotAttributes.DOTBAR;
    public static int HIST = PlotAttributes.HIST;

    //~ Instance fields ////////////////////////////////////////////////////////

    private Axe2D axe;
    private Color color;
    private Coordinates2D[] points;

    //private double[] widthY;
    private int type;
    private double[] widthX;

    //~ Constructors ///////////////////////////////////////////////////////////

    public DataPlot2D(double[] x, double[] y, double[] wX, double[] wY,
        Axe2D ax, int typ, Color col)
    {
        axe = ax;
        points = new Coordinates2D[x.length];

        for (int i = 0; i < x.length; i++)
        {
            points[i] = new Coordinates2D(x[i], y[i], axe);
        }

        color = col;
        type = typ;
        widthX = wX;

        //widthY = wY;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void draw(Graphics2D comp2D)
    {
        switch (type)
        {
        case 0:
            comp2D.setColor(color);

            for (int i = 0; i < (points.length - 1); i++)
            {
                comp2D.drawLine(points[i].getSc()[0], points[i].getSc()[1],
                    points[i].getSc()[0], points[i].getSc()[1]);
            }

            break;

        case 1:
            drawDots(comp2D);

            break;

        case 2:
            comp2D.setColor(color);

            for (int i = 0; i < (points.length - 1); i++)
            {
                comp2D.drawLine(points[i].getSc()[0], points[i].getSc()[1],
                    points[i + 1].getSc()[0], points[i + 1].getSc()[1]);
            }

            break;

        case 3:
            drawDots(comp2D);
            comp2D.setColor(color);

            for (int i = 0; i < (points.length - 1); i++)
            {
                comp2D.drawLine(points[i].getSc()[0], points[i].getSc()[1],
                    points[i + 1].getSc()[0], points[i + 1].getSc()[1]);
            }

            break;

        case 4:
            comp2D.setColor(color);

            for (int i = 0; i < points.length; i++)
            {
                Coordinates2D pointbase = points[i].copy();
                double[] coord = pointbase.getPl();
                coord[1] = 0;
                pointbase.setPl(coord);

                comp2D.drawLine(pointbase.getSc()[0], pointbase.getSc()[1],
                    points[i].getSc()[0], points[i].getSc()[1]);
            }

            break;

        case 5:
            drawDots(comp2D);
            comp2D.setColor(color);

            for (int i = 0; i < points.length; i++)
            {
                Coordinates2D pointbase = points[i].copy();
                double[] coord = pointbase.getPl();
                coord[1] = 0;
                pointbase.setPl(coord);

                comp2D.drawLine(pointbase.getSc()[0], pointbase.getSc()[1],
                    points[i].getSc()[0], points[i].getSc()[1]);
            }

            break;

        case 6:
            comp2D.setColor(color);

            for (int i = 0; i < points.length; i++)
            {
                Coordinates2D pointbase = points[i].copy();
                double[] coord = pointbase.getPl();
                coord[1] = 0;
                pointbase.setPl(coord);

                Coordinates2D pt1 = pointbase.addVector(-widthX[i] / 2, 0);
                Coordinates2D pt2 = pointbase.addVector(widthX[i] / 2, 0);
                Coordinates2D pt3 = points[i].addVector(-widthX[i] / 2, 0);
                Coordinates2D pt4 = points[i].addVector(widthX[i] / 2, 0);

                comp2D.drawLine(pt1.getSc()[0], pt1.getSc()[1], pt2.getSc()[0],
                    pt2.getSc()[1]);
                comp2D.drawLine(pt2.getSc()[0], pt2.getSc()[1], pt4.getSc()[0],
                    pt4.getSc()[1]);
                comp2D.drawLine(pt4.getSc()[0], pt4.getSc()[1], pt3.getSc()[0],
                    pt3.getSc()[1]);
                comp2D.drawLine(pt3.getSc()[0], pt3.getSc()[1], pt1.getSc()[0],
                    pt1.getSc()[1]);
            }

            break;
        }
    }

    public Coordinates2D[] getCoord()
    {
        return points;
    }

    private void drawDots(Graphics2D comp2D)
    {
        int d = PlotAttributes.dotSize;

        for (int i = 0; i < points.length; i++)
        {
            comp2D.setColor(color);
            comp2D.fillOval(points[i].getSc()[0] - (int) (d / 2),
                points[i].getSc()[1] - (int) (d / 2), d, d);
            comp2D.drawOval(points[i].getSc()[0] - (int) (d / 2),
                points[i].getSc()[1] - (int) (d / 2), d, d);
            comp2D.setColor(Color.white);
            comp2D.fillOval(points[i].getSc()[0] - (int) (d / 4),
                points[i].getSc()[1] - (int) (d / 4), (int) (d / 4),
                (int) (d / 4));
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
