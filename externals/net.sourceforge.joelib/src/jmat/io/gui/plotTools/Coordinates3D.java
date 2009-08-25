package jmat.io.gui.plotTools;

/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class Coordinates3D
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private Axe3D axe;
    private double[] Pl = new double[3];
    private int[] Sc = new int[2];

    //~ Constructors ///////////////////////////////////////////////////////////

    public Coordinates3D(double[] xyz, Axe3D ax)
    {
        Pl = xyz;
        axe = ax;
        evalSc();
    }

    public Coordinates3D(double x, double y, double z, Axe3D ax)
    {
        Pl[0] = x;
        Pl[1] = y;
        Pl[2] = z;
        axe = ax;
        evalSc();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Coordinates3D addVector(double[] xyz)
    {
        return new Coordinates3D(Pl[0] + xyz[0], Pl[1] + xyz[1], Pl[2] + xyz[2],
                axe);
    }

    public Coordinates3D addVector(double x, double y, double z)
    {
        return new Coordinates3D(Pl[0] + x, Pl[1] + y, Pl[2] + z, axe);
    }

    public Coordinates3D copy()
    {
        return new Coordinates3D(Pl[0], Pl[1], Pl[2], axe);
    }

    public Axe3D getAxe()
    {
        return axe;
    }

    public double[] getPl()
    {
        return Pl;
    }

    public int[] getSc()
    {
        return Sc;
    }

    /*  public void setSc(int[] xyz) {
        Sc = xyz;
        evalPl();
      }*/
    /*  private void evalPl() {
        Pl = axe.Sc2Pl(Sc);
      }*/
    public void setAxe3D(Axe3D ax)
    {
        axe = ax;
        evalSc();
    }

    public void setPl(double[] xyz)
    {
        Pl = xyz;
        evalSc();
    }

    public void setPl(double x, double y, double z)
    {
        Pl[0] = x;
        Pl[1] = y;
        Pl[2] = z;
        evalSc();
    }

    public double[] vector(Coordinates3D B)
    {
        double[] V = new double[3];
        V[0] = this.Pl[0] - B.Pl[0];
        V[1] = this.Pl[1] - B.Pl[1];
        V[2] = this.Pl[2] - B.Pl[2];

        return V;
    }

    private void evalSc()
    {
        Sc = axe.Pl2Sc(Pl);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
