///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: GasteigerMarsiliState.java,v $
//  Purpose:  Stores actual Gasteiger charge state.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
//            $Date: 2005/02/17 16:48:37 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation version 2 of the License.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.molecule.charge;

/**
 * Stores actual Gasteiger charge state.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:37 $
 */
public class GasteigerMarsiliState implements java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Default denominator for hydrogen atoms.
     */
    public static double MX_GASTEIGER_DENOM = 20.02;

    /**
     *  Damping factor.
     */
    public static double MX_GASTEIGER_DAMP = 0.5;

    /**
     * Number of iterations.
     */
    public static int MX_GASTEIGER_ITERS = 6;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public double a;

    /**
     *  Description of the Field
     */
    public double b;

    /**
     *  Description of the Field
     */
    public double c;

    /**
     *  Description of the Field
     */
    public double chi;

    /**
     *  Description of the Field
     */
    public double denom;

    /**
     *  Description of the Field
     */
    public double q;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the GasteigerState object
     */
    public GasteigerMarsiliState()
    {
        a = 0.0;
        b = 0.0;
        c = 0.0;
        denom = 0.0;
        chi = 0.0;
        q = 0.0;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Sets the values attribute of the GasteigerState object
     *
     * @param  _a  The new values value
     * @param  _b  The new values value
     * @param  _c  The new values value
     * @param  _q  The new values value
     */
    public void setValues(double _a, double _b, double _c, double _q)
    {
        a = _a;
        b = _b;
        c = _c;
        denom = a + b + c;
        q = _q;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer(100);
        buffer.append('<');
        buffer.append("a=");
        buffer.append(a);
        buffer.append(',');
        buffer.append("b=");
        buffer.append(b);
        buffer.append(',');
        buffer.append("c=");
        buffer.append(c);
        buffer.append(',');
        buffer.append("denom=");
        buffer.append(denom);
        buffer.append(',');
        buffer.append("q=");
        buffer.append(q);
        buffer.append(',');
        buffer.append('>');

        return buffer.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
