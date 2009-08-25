///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicRotorRule.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.1 $
//            $Date: 2005/01/16 18:08:00 $
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

import joelib2.smarts.BasicSMARTSPatternMatcher;
import joelib2.smarts.SMARTSPatternMatcher;


/**
 * Atom representation.
 */
public class BasicRotorRule implements RotorRule
{
    //~ Instance fields ////////////////////////////////////////////////////////

    double delta;
    int[] referenceAtoms = new int[4];
    SMARTSPatternMatcher smartsPattern;

    /**
     * Vector of Double
     */
    double[] torsionValues;

    //~ Constructors ///////////////////////////////////////////////////////////

    public BasicRotorRule(String buffer, int[] ref, double[] vals, double delta)
    {
        smartsPattern = new BasicSMARTSPatternMatcher();
        smartsPattern.init(buffer);
        System.arraycopy(ref, 0, referenceAtoms, 0, ref.length);
        torsionValues = vals;
        this.delta = delta;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public double getDelta()
    {
        return (delta);
    }

    public void getReferenceAtoms(int[] ref)
    {
        System.arraycopy(referenceAtoms, 0, ref, 0, referenceAtoms.length);
    }

    public SMARTSPatternMatcher getSmartsPattern()
    {
        return (smartsPattern);
    }

    public String getSmartsString()
    {
        return smartsPattern.getSmarts();
    }

    public double[] getTorsionValues()
    {
        return (torsionValues);
    }

    public boolean isValid()
    {
        return (smartsPattern.isValid());
    }

    public void setDelta(float delta)
    {
        this.delta = delta;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
