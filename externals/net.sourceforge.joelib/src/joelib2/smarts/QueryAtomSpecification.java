///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: QueryAtomSpecification.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 16, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.9 $
//          $Date: 2005/02/17 16:48:39 $
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
package joelib2.smarts;

import joelib2.smarts.atomexpr.QueryAtom;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.9 $, $Date: 2005/02/17 16:48:39 $
 */
public interface QueryAtomSpecification
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the atom.
     */
    QueryAtom getAtom();

    /**
     * @return Returns the chiral.
     */
    int getChiral();

    /**
     * @return Returns the part.
     */
    int getPart();

    /**
     * @return Returns the vectorBinding.
     */
    int getVectorBinding();

    /**
     * @return Returns the visit.
     */
    int getVisit();

    /**
     * @param atom The atom to set.
     */
    void setAtom(QueryAtom atom);

    /**
     * @param chiral The chiral to set.
     */
    void setChiral(int chiral);

    /**
     * @param part The part to set.
     */
    void setPart(int part);

    /**
     * @param vectorBinding The vectorBinding to set.
     */
    void setVectorBinding(int vectorBinding);

    /**
     * @param visit The visit to set.
     */
    void setVisit(int visit);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
