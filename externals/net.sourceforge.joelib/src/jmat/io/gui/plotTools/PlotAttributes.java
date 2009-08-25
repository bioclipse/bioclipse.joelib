package jmat.io.gui.plotTools;

import java.awt.Color;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class PlotAttributes
{
    //~ Static fields/initializers /////////////////////////////////////////////

    public static int PIXEL = 0;
    public static int DOT = 1;
    public static int LINE = 2;
    public static int DOTLINE = 3;
    public static int BAR = 4;
    public static int DOTBAR = 5;
    public static int HIST = 6;
    public static int GRID = 7;
    public static int dotSize = 8;

    //~ Instance fields ////////////////////////////////////////////////////////

    public Color[] colorList =
        {
            Color.blue, Color.red, Color.green, Color.yellow, Color.pink,
            Color.orange
        };
    public String[] legend = {"X", "Y", "Z"};
    public int[] typeList = {DOT, DOT, DOT, DOT, DOT, DOT};
    private int numberOfElements = 6;

    //~ Constructors ///////////////////////////////////////////////////////////

    public PlotAttributes()
    {
    }

    public PlotAttributes(String[] leg)
    {
        setLegend(leg);
    }

    public PlotAttributes(Color[] col)
    {
        setColorList(col);
    }

    public PlotAttributes(int[] typ)
    {
        setTypeList(typ);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setColorList(Color[] args)
    {
        colorList = new Color[args.length];

        for (int i = 0; i < args.length; i++)
        {
            colorList[i] = args[i];
        }
    }

    public void setLegend(String[] args)
    {
        legend = new String[args.length];

        for (int i = 0; i < args.length; i++)
        {
            legend[i] = args[i];
        }
    }

    public void setTypeList(int[] args)
    {
        typeList = new int[args.length];

        for (int i = 0; i < args.length; i++)
        {
            typeList[i] = args[i];
        }
    }

    public void setTypeList(int arg)
    {
        typeList = new int[numberOfElements];

        for (int i = 0; i < numberOfElements; i++)
        {
            typeList[i] = arg;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
