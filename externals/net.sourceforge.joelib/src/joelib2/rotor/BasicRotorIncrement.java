///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicRotorIncrement.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
//            $Date: 2005/01/26 12:07:23 $
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
package joelib2.rotor;

/**
 * Atom representation.
 */
public class BasicRotorIncrement implements RotorIncrement
{
    //~ Instance fields ////////////////////////////////////////////////////////

    public double delta;

    public double[] values;

    //~ Constructors ///////////////////////////////////////////////////////////

    public BasicRotorIncrement()
    {
    }

    public BasicRotorIncrement(double[] values, double delta)
    {
        this.values = values;
        this.delta = delta;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the delta.
     */
    public double getDelta()
    {
        return delta;
    }

    /**
     * @return Returns the values.
     */
    public double[] getValues()
    {
        return values;
    }

    /**
     * @param delta The delta to set.
     */
    public void setDelta(double delta)
    {
        this.delta = delta;
    }

    /**
     * @param values The values to set.
     */
    public void setValues(double[] values)
    {
        this.values = values;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
