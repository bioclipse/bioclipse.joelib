///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: QueryPattern.java,v $
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

/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.9 $, $Date: 2005/02/17 16:48:39 $
 */
public interface QueryPattern
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public final static int ATOMPOOL = 1;

    /**
     *  Description of the Field
     */
    public final static int BONDPOOL = 1;

    //~ Methods ////////////////////////////////////////////////////////////////

    QueryAtomSpecification getAtom(int index);

    /**
     * @return Returns the queryAtoms.
     */
    QueryAtomSpecification[] getAtoms();

    /**
     * @return Returns the atomsAllocated.
     */
    int getAtomsAllocated();

    /**
     * @return Returns the queryAtomsSize.
     */
    int getAtomsSize();

    QueryBondSpecification getBond(int index);

    /**
     * @return Returns the queryBonds.
     */
    QueryBondSpecification[] getBonds();

    /**
     * @return Returns the bondsAllocated.
     */
    int getBondsAllocated();

    /**
     * @return Returns the queryBondsSize.
     */
    int getBondsSize();

    /**
     * @return Returns the parts.
     */
    int getParts();

    /**
     * @return Returns the ischiral.
     */
    boolean isChiral();

    void setAtom(int index, QueryAtomSpecification queryAtom);

    /**
     * @param atomsAllocated The atomsAllocated to set.
     */
    void setAtomsAllocated(int atomsAllocated);

    /**
     * @param queryAtomsSize The queryAtomsSize to set.
     */
    void setAtomsSize(int queryAtomsSize);

    void setBond(int index, QueryBondSpecification queryBond);

    /**
     * @param bondsAllocated The bondsAllocated to set.
     */
    void setBondsAllocated(int bondsAllocated);

    /**
     * @param queryBondsSize The queryBondsSize to set.
     */
    void setBondsSize(int queryBondsSize);

    /**
     * @param ischiral The ischiral to set.
     */
    void setChiral(boolean chiral);

    /**
     * @param parts The parts to set.
     */
    void setParts(int parts);

    /**
     * @param queryAtoms The queryAtoms to set.
     */
    void setQueryAtoms(QueryAtomSpecification[] queryAtoms);

    /**
     * @param queryBonds The queryBonds to set.
     */
    void setQueryBonds(QueryBondSpecification[] queryBonds);

    String toString();

    String toString(String startLineWith);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
