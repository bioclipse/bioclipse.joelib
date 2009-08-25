///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: GroupContributions.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 16, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.8 $
//          $Date: 2005/02/17 16:48:28 $
//          $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.algo.contribution;

import joelib2.smarts.SMARTSPatternMatcher;

import java.util.ArrayList;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.wikipedia QSAR
 * @.wikipedia Data mining
 * @.license    GPL
 * @.cvsversion $Revision: 1.8 $, $Date: 2005/02/17 16:48:28 $
 */
public interface GroupContributions
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the atomContributions.
     */
    ArrayList<Double> getAtomContributions();

    /**
     * @return Returns the atomSmarts.
     */
    ArrayList<SMARTSPatternMatcher> getAtomSmarts();

    /**
     * @return Returns the hydrogenContributions.
     */
    ArrayList<Double> getHydrogenContributions();

    /**
     * @return Returns the hydrogenSmarts.
     */
    ArrayList<SMARTSPatternMatcher> getHydrogenSmarts();

    /**
     * @return Returns the model.
     */
    String getModel();

    /**
     * @param atomContributions The atomContributions to set.
     */
    void setAtomContributions(ArrayList<Double> atomContributions);

    /**
     * @param atomSmarts The atomSmarts to set.
     */
    void setAtomSmarts(ArrayList<SMARTSPatternMatcher> atomSmarts);

    /**
     * @param hydrogenContributions The hydrogenContributions to set.
     */
    void setHydrogenContributions(ArrayList<Double> hydrogenContributions);

    /**
     * @param hydrogenSmarts The hydrogenSmarts to set.
     */
    void setHydrogenSmarts(ArrayList<SMARTSPatternMatcher> hydrogenSmarts);

    /**
     * @param model The model to set.
     */
    void setModel(String model);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
