///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicQueryAtomSpecification.java,v $
//  Purpose:  Atom specification of a SMARTS bond expression.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:38 $
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
package joelib2.smarts;

import joelib2.smarts.atomexpr.BasicQueryAtom;
import joelib2.smarts.atomexpr.QueryAtom;


/**
 * Atom specification of a SMARTS bond expression.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:38 $
 */
public class BasicQueryAtomSpecification implements java.io.Serializable,
    QueryAtomSpecification
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * Representation of the SMARTS atom expression.
     */
    public QueryAtom atom;

    /**
     * Chiral flag.
     */
    public int chiral;

    /**
     * Part number of this atom expression in the SMARTS pattern.
     */
    public int part;

    /**
     * Vector binding.
     * Used for molecule modifications in <tt>JOEChemTransformation</tt>.
     */
    public int vectorBinding;

    /**
     * Visited flag.
     */
    public int visit;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the AtomSpec object
     */
    public BasicQueryAtomSpecification()
    {
        atom = new BasicQueryAtom();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the atom.
     */
    public QueryAtom getAtom()
    {
        return atom;
    }

    /**
     * @return Returns the chiral.
     */
    public int getChiral()
    {
        return chiral;
    }

    /**
     * @return Returns the part.
     */
    public int getPart()
    {
        return part;
    }

    /**
     * @return Returns the vectorBinding.
     */
    public int getVectorBinding()
    {
        return vectorBinding;
    }

    /**
     * @return Returns the visit.
     */
    public int getVisit()
    {
        return visit;
    }

    /**
     * @param atom The atom to set.
     */
    public void setAtom(QueryAtom atom)
    {
        this.atom = atom;
    }

    /**
     * @param chiral The chiral to set.
     */
    public void setChiral(int chiral)
    {
        this.chiral = chiral;
    }

    /**
     * @param part The part to set.
     */
    public void setPart(int part)
    {
        this.part = part;
    }

    /**
     * @param vectorBinding The vectorBinding to set.
     */
    public void setVectorBinding(int vectorBinding)
    {
        this.vectorBinding = vectorBinding;
    }

    /**
     * @param visit The visit to set.
     */
    public void setVisit(int visit)
    {
        this.visit = visit;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
