///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicGroupContributions.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:28 $
//            $Author: wegner $
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
package joelib2.algo.contribution;

import joelib2.smarts.SMARTSPatternMatcher;

import java.util.ArrayList;


/**
 * Group contribution informations.
 *
 * @.author     wegnerj
 * @.author  Stephen Jelfs
 * @.wikipedia QSAR
 * @.wikipedia Data mining
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:28 $
 */
public class BasicGroupContributions implements GroupContributions
{
    //~ Instance fields ////////////////////////////////////////////////////////

    // atom contributions
    public ArrayList<Double> atomContributions = new ArrayList<Double>();

    // atom SMARTS patterns
    public ArrayList<SMARTSPatternMatcher> atomSmarts =
        new ArrayList<SMARTSPatternMatcher>();
    public ArrayList<Double> hydrogenContributions = new ArrayList<Double>();
    public ArrayList<SMARTSPatternMatcher> hydrogenSmarts =
        new ArrayList<SMARTSPatternMatcher>();
    String model;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the StringString object
     *
     * @param  _s1  Description of the Parameter
     * @param  _s2  Description of the Parameter
     */
    public BasicGroupContributions(String model)
    {
        this.model = model;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the atomContributions.
     */
    public ArrayList<Double> getAtomContributions()
    {
        return atomContributions;
    }

    /**
     * @return Returns the atomSmarts.
     */
    public ArrayList<SMARTSPatternMatcher> getAtomSmarts()
    {
        return atomSmarts;
    }

    /**
     * @return Returns the hydrogenContributions.
     */
    public ArrayList<Double> getHydrogenContributions()
    {
        return hydrogenContributions;
    }

    /**
     * @return Returns the hydrogenSmarts.
     */
    public ArrayList<SMARTSPatternMatcher> getHydrogenSmarts()
    {
        return hydrogenSmarts;
    }

    /**
     * @return Returns the model.
     */
    public String getModel()
    {
        return model;
    }

    /**
     * @param atomContributions The atomContributions to set.
     */
    public void setAtomContributions(ArrayList<Double> atomContributions)
    {
        this.atomContributions = atomContributions;
    }

    /**
     * @param atomSmarts The atomSmarts to set.
     */
    public void setAtomSmarts(ArrayList<SMARTSPatternMatcher> atomSmarts)
    {
        this.atomSmarts = atomSmarts;
    }

    /**
     * @param hydrogenContributions The hydrogenContributions to set.
     */
    public void setHydrogenContributions(
        ArrayList<Double> hydrogenContributions)
    {
        this.hydrogenContributions = hydrogenContributions;
    }

    /**
     * @param hydrogenSmarts The hydrogenSmarts to set.
     */
    public void setHydrogenSmarts(
        ArrayList<SMARTSPatternMatcher> hydrogenSmarts)
    {
        this.hydrogenSmarts = hydrogenSmarts;
    }

    /**
     * @param model The model to set.
     */
    public void setModel(String model)
    {
        this.model = model;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
