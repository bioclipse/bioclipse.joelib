///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SquareRootLookup.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
//            $Date: 2005/02/17 16:48:35 $
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
package joelib2.math;

/**
 * Sqrt table.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:35 $
 */
public class SquareRootLookup implements java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    private double _incr;
    private double _max;

    private double[] _tbl;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOESqrtTbl object
     */
    public SquareRootLookup()
    {
    }

    /**
     *  Constructor for the JOESqrtTbl object
     *
     * @param  max   Description of the Parameter
     * @param  incr  Description of the Parameter
     */
    public SquareRootLookup(double max, double incr)
    {
        init(max, incr);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  max   Description of the Parameter
     * @param  incr  Description of the Parameter
     */
    public void init(double max, double incr)
    {
        int i;
        double r;
        _max = max * max;
        _incr = incr;

        //array size needs to be large enough to account for fp error
        _tbl = new double[(int) ((_max / _incr) + 10)];

        for (r = (_incr / 2.0f), i = 0; r <= _max; r += _incr, i++)
        {
            _tbl[i] = Math.sqrt(r);
        }

        _incr = 1 / _incr;
    }

    /**
     *  Description of the Method
     *
     * @param  d2  Description of the Parameter
     * @return     Description of the Return Value
     */
    public double sqrt(double d2)
    {
        return ((d2 < _max) ? _tbl[(int) (d2 * _incr)] : Math.sqrt(d2));
    }

    /**
     *  Description of the Method
     */
    protected void finalize() throws Throwable
    {
        _tbl = null;
        super.finalize();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
