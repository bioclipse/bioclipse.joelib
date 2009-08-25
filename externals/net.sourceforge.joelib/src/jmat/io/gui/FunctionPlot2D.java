package jmat.io.gui;

import jmat.function.DoubleFunction;

import jmat.io.gui.plotTools.PlotAttributes;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class FunctionPlot2D extends Plot2D
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static int nbPointsX = 100;

    //~ Instance fields ////////////////////////////////////////////////////////

    private DoubleFunction[] F;
    private double Xmax;
    private double Xmin;

    //~ Constructors ///////////////////////////////////////////////////////////

    public FunctionPlot2D(DoubleFunction f, double xmin, double xmax)
    {
        setAppearence();
        setPlotAttributes();
        Xmin = xmin;
        Xmax = xmax;
        update(f);
    }

    public FunctionPlot2D(DoubleFunction[] f, double xmin, double xmax)
    {
        setAppearence();
        setPlotAttributes();
        Xmin = xmin;
        Xmax = xmax;

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

        setXY();
        update();
    }

    public void setMinMax(double xmin, double xmax)
    {
        Xmin = xmin;
        Xmax = xmax;

        setXY();
        update();
    }

    public void update(DoubleFunction f)
    {
        checkArgNumber(f);
        F = new DoubleFunction[1];
        F[0] = f;

        setXY();
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

        setXY();
        update();
    }

    protected void setPlotAttributes()
    {
        PA = new PlotAttributes();
        PA.setTypeList(LINE);

        String[] leg = {"X", "Y"};
        PA.setLegend(leg);
    }

    /** Check if argNumber == 1.
    @param f   DoubleFunction.
     */
    private void checkArgNumber(DoubleFunction f)
    {
        f.checkArgNumber(1);
    }

    /** Check if argNumber == 1.
    @param F   DoubleFunction array.
     */
    private void checkArgNumber(DoubleFunction[] F)
    {
        for (int i = 0; i < F.length; i++)
        {
            F[i].checkArgNumber(1);
        }
    }

    private void setXY()
    {
        X = new double[F.length][nbPointsX];
        Y = new double[F.length][nbPointsX];
        widthX = new double[F.length][];
        widthY = new double[F.length][];

        for (int i = 0; i < F.length; i++)
        {
            for (int j = 0; j < nbPointsX; j++)
            {
                double[] x = {Xmin + (((Xmax - Xmin) * j) / (nbPointsX - 1))};
                X[i][j] = x[0];
                Y[i][j] = F[i].eval(x);
            }

            widthX[i] = new double[nbPointsX];
            widthY[i] = new double[nbPointsX];
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
