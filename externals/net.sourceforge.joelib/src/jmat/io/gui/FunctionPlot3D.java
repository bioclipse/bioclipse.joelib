package jmat.io.gui;

import jmat.function.DoubleFunction;

import jmat.io.gui.plotTools.PlotAttributes;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class FunctionPlot3D extends Plot3D
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static int nbPointsX = 20;
    private static int nbPointsY = 20;

    //~ Instance fields ////////////////////////////////////////////////////////

    private DoubleFunction[] F;
    private double Xmax;
    private double Xmin;
    private double Ymax;
    private double Ymin;

    //~ Constructors ///////////////////////////////////////////////////////////

    public FunctionPlot3D(DoubleFunction f, double xmin, double xmax,
        double ymin, double ymax)
    {
        setAppearence();
        setPlotAttributes();
        Xmin = xmin;
        Xmax = xmax;
        Ymin = ymin;
        Ymax = ymax;
        update(f);
    }

    public FunctionPlot3D(DoubleFunction[] f, double xmin, double xmax,
        double ymin, double ymax)
    {
        setAppearence();
        setPlotAttributes();
        Xmin = xmin;
        Xmax = xmax;
        Ymin = ymin;
        Ymax = ymax;
        update(f);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void add(DoubleFunction f)
    {
        checkArgNumber(f);

        DoubleFunction[] F_tmp = new DoubleFunction[F.length + 1];

        for (int i = 0; i < F.length; i++)
        {
            F_tmp[i] = F[i];
        }

        F_tmp[F.length] = f;
        F = F_tmp;

        setXYZ();
        update();
    }

    public void setMinMax(double xmin, double xmax, double ymin, double ymax)
    {
        Xmin = xmin;
        Xmax = xmax;
        Ymin = ymin;
        Ymax = ymax;

        setXYZ();
        update();
    }

    public void update(DoubleFunction f)
    {
        checkArgNumber(f);
        F = new DoubleFunction[1];
        F[0] = f;

        setXYZ();
        update();
    }

    public void update(DoubleFunction[] f)
    {
        checkArgNumber(f);
        F = new DoubleFunction[f.length];

        for (int i = 0; i < f.length; i++)
        {
            F[i] = f[i];
        }

        setXYZ();
        update();
    }

    protected void setPlotAttributes()
    {
        PA = new PlotAttributes();
        PA.setTypeList(PIXEL);

        String[] leg = {"X", "Y", "Z"};
        PA.setLegend(leg);
    }

    /** Check if argNumber == 2.
    @param f   DoubleFunction.
     */
    private void checkArgNumber(DoubleFunction f)
    {
        f.checkArgNumber(2);
    }

    /** Check if argNumber == 2.
    @param F   DoubleFunction array.
     */
    private void checkArgNumber(DoubleFunction[] F)
    {
        for (int i = 0; i < F.length; i++)
        {
            F[i].checkArgNumber(2);
        }
    }

    private void setXYZ()
    {
        X = new double[F.length][nbPointsX * nbPointsY];
        Y = new double[F.length][nbPointsX * nbPointsY];
        Z = new double[F.length][nbPointsX * nbPointsY];
        widthX = new double[F.length][];
        widthY = new double[F.length][];
        widthZ = new double[F.length][];

        for (int i = 0; i < F.length; i++)
        {
            for (int j = 0; j < nbPointsX; j++)
            {
                for (int k = 0; k < nbPointsY; k++)
                {
                    double[] xy =
                        {
                            Xmin + (((Xmax - Xmin) * j) / (nbPointsX - 1)),
                            Ymin + (((Ymax - Ymin) * k) / (nbPointsY - 1))
                        };
                    X[i][j + (k * nbPointsX)] = xy[0];
                    Y[i][j + (k * nbPointsX)] = xy[1];
                    Z[i][j + (k * nbPointsX)] = F[i].eval(xy);
                }
            }

            widthX[i] = new double[nbPointsX * nbPointsY];
            widthY[i] = new double[nbPointsX * nbPointsY];
            widthZ[i] = new double[nbPointsX * nbPointsY];
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
