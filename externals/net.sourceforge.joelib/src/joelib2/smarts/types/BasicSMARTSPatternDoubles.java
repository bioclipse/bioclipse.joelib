///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicSMARTSPatternDoubles.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:40 $
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
package joelib2.smarts.types;

import joelib2.smarts.SMARTSPatternMatcher;


/**
 *  Atom representation.
 *
 * @.author     wegnerj
 *     30. Januar 2002
 */
public class BasicSMARTSPatternDoubles extends BasicSMARTSValue
    implements java.io.Serializable, SMARTSValue, SMARTSPatternDoubles
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    public double[] doubles;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * @param  _fV  {@link java.util.Vector} of <tt>double[1]</tt>
     * @param  smartsPattern  Description of the Parameter
     */
    public BasicSMARTSPatternDoubles(SMARTSPatternMatcher smartsPattern,
        double[] seedCharge)
    {
        this.smartsValue = smartsPattern;
        this.doubles = seedCharge;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the doubles.
     */
    public double[] getDoubles()
    {
        return doubles;
    }

    /**
     * @param doubles The doubles to set.
     */
    public void setDoubles(double[] doubles)
    {
        this.doubles = doubles;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
