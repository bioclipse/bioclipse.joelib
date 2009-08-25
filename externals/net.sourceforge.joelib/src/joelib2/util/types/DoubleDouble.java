///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DoubleDouble.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg K. Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2004/12/17 14:19:20 $
//            $Author: wegnerj $
//
//  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
///////////////////////////////////////////////////////////////////////////////
package joelib2.util.types;

/**
 * Two double values.
 *
 * @author     wegnerj
 */
public class DoubleDouble implements java.io.Serializable
{
    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public double d1;

    /**
     *  Description of the Field
     */
    public double d2;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntInt object
     */
    public DoubleDouble()
    {
    }

    /**
     *  Constructor for the IntInt object
     *
     * @param  _i1  Description of the Parameter
     * @param  _i2  Description of the Parameter
     */
    public DoubleDouble(double _d1, double _d2)
    {
        d1 = _d1;
        d2 = _d2;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object otherObj)
    {
        if (otherObj instanceof DoubleDouble)
        {
            DoubleDouble dd = (DoubleDouble) otherObj;

            if ((dd.d1 == this.d1) && (dd.d2 == this.d2))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer(10);
        sb.append('<');
        sb.append(d1);
        sb.append(',');
        sb.append(d2);
        sb.append('>');

        return sb.toString();
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
