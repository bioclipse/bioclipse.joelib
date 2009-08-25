///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: IsomerismHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
//            $Date: 2007/03/03 00:03:49 $
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
package joelib2.molecule;

import joelib2.feature.types.atomlabel.AtomHybridisation;
import joelib2.feature.types.atomlabel.AtomIsHydrogen;
import joelib2.feature.types.bondlabel.BondInRing;

import joelib2.math.BasicVector3D;

import joelib2.util.iterator.NbrAtomIterator;

import org.apache.log4j.Category;


/**
 * Helper class to detect E/Z isomerism.
 *
 * @.author     wegnerj
 * @.wikipedia Stereoisomerism
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2007/03/03 00:03:49 $
 */
public class IsomerismHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(IsomerismHelper.class
            .getName());
    public static final int EZ_ISOMERISM_UNDEFINED = 0;
    public static final int CISTRANS_ISOMERISM_UNDEFINED = 0;

    public static final int Z_ISOMERISM = 1;
    public static final int CIS_ISOMERISM = 1;

    public static final int E_ISOMERISM = 2;
    public static final int TRANS_ISOMERISM = 2;

    //~ Constructors ///////////////////////////////////////////////////////////

    public IsomerismHelper()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Checks bonds for cis/trans isomerism using 2D/3D informations.
     * Single bond flags will be not set if a cis/trans double bond is detected.
     *
     * @param bond
     * @param setSingleBondFlags set the single bond flags {@link BondHelper#IS_TORUP}/{@link BondHelper#IS_TORDOWN}, if <tt>true</tt> and a cis/trans bond is detected.
     * @return int Z_ISOMERISM for cis, E_ISOMERISM for trans and EZ_ISOMERISM_UNDEFINED for undefined isomerism
     * @see #EZ_ISOMERISM_UNDEFINED
     * @see #Z_ISOMERISM
     * @see #E_ISOMERISM
     * @see #isCisTransBond(Bond)
     */
    public static int getCisTransFrom2D3D(Bond bond)
    {
        return getCisTransFrom2D3D(bond);
    }

    /**
     * Checks bonds for cis/trans isomerism using 2D/3D informations.
     *
     * @param bond
     * @param setSingleBondFlags set the single bond flags {@link BondHelper#IS_TORUP}/{@link BondHelper#IS_TORDOWN}, if <tt>true</tt> and a cis/trans bond is detected.
     * @return int Z_ISOMERISM for cis, E_ISOMERISM for trans and EZ_ISOMERISM_UNDEFINED for undefined isomerism
     * @see #EZ_ISOMERISM_UNDEFINED
     * @see #Z_ISOMERISM
     * @see #E_ISOMERISM
     * @see #isCisTransBond(Bond)
     */
    public static int getCisTransFrom2D3D(Bond bond, boolean setSingleBondFlags)
    {
        int isomerism = EZ_ISOMERISM_UNDEFINED;

        if (bond.isDouble() && !BondInRing.isInRing(bond))
        {
            Atom begin = bond.getBegin();
            Atom end = bond.getEnd();
            //System.out.println(bond.getParent().getTitle());

            //skip allenes
            if ((AtomHybridisation.getIntValue(begin) != 1) &&
                    (AtomHybridisation.getIntValue(end) != 1))
            {
                Bond beginBond = getTorsionBondOf2D3D(bond, begin);
                Bond endBond = getTorsionBondOf2D3D(bond, end);

                // one side of the double bond is not really cis/trans !
                if ((beginBond != null) && (endBond != null))
                {
                	// calculate torsion angle
                    Atom afterBeginAtom = beginBond.getNeighbor(begin);
                    Atom afterEndAtom = endBond.getNeighbor(end);
                    double torsionAngle=Math.abs(
                                BasicVector3D.calcTorsionAngle(
                                    afterBeginAtom.getCoords3D(),
                                    begin.getCoords3D(), end.getCoords3D(),
                                    afterEndAtom.getCoords3D()));
                    //System.out.println(beginBond.getParent().getTitle()+" "+torsionAngle);
                    if(torsionAngle!=0.0){
	                    if (torsionAngle > 10.0)
	                    {
	                        if (setSingleBondFlags)
	                        {
	                            beginBond.setUp();
	                            endBond.setUp();
	                        }
	
	                        isomerism = E_ISOMERISM;
	                    }
	                    else
	                    {
	                        if (setSingleBondFlags)
	                        {
	                            beginBond.setUp();
	                            endBond.setDown();
	                        }
	
	                        isomerism = Z_ISOMERISM;
	                    }
                    }
                }
            }
        }

        return isomerism;
    }

    /**
     * Checks bonds for cis/trans isomerism using the SMILES flags up/down bond connected to a double bond.<br>
     * Cases:<br>
     * E/trans -- bondUP/doubleBond/bondUP<br>
     * E/trans -- bondDOWN/doubleBond/bondDOWN<br>
     * Z/cis   -- bondUP/doubleBond/bondDOWN<br>
     * Z/cis   -- bondDOWN/doubleBond/bondUP<br>
     * <br>
     * This method does not check multiple definitions.
     * If no up/down informations are available, the {@link #getCisTransFrom2D3D(Bond)} method is used
     * also to get cis/trans informations.
     * Single bond flags will be not set if a cis/trans double bond is detected.
     *
     * @param bond
     * @return int Z_ISOMERISM for cis, E_ISOMERISM for trans and EZ_ISOMERISM_UNDEFINED for undefined isomerism
     * @see #EZ_ISOMERISM_UNDEFINED
     * @see #Z_ISOMERISM
     * @see #E_ISOMERISM
     * @see #getCisTransFrom2D3D(Bond)
     */
    public static int isCisTransBond(Bond bond)
    {
        return isCisTransBond(bond, false);
    }

    /**
     * Checks bonds for cis/trans isomerism using the SMILES flags up/down bond connected to a double bond.<br>
     * Cases:<br>
     * E/trans -- bondUP/doubleBond/bondUP<br>
     * E/trans -- bondDOWN/doubleBond/bondDOWN<br>
     * Z/cis   -- bondUP/doubleBond/bondDOWN<br>
     * Z/cis   -- bondDOWN/doubleBond/bondUP<br>
     * <br>
     * This method does not check multiple definitions.
     * If no up/down informations are available, the {@link #getCisTransFrom2D3D(Bond)} method is used
     * also to get cis/trans informations.
     *
     *
     * @param bond
     * @param setSingleBondFlags set the single bond flags {@link BondHelper#IS_TORUP}/{@link BondHelper#IS_TORDOWN}, if <tt>true</tt> and a cis/trans bond is detected.
     * @return int Z_ISOMERISM for cis, E_ISOMERISM for trans and EZ_ISOMERISM_UNDEFINED for undefined isomerism
     * @see #EZ_ISOMERISM_UNDEFINED
     * @see #Z_ISOMERISM
     * @see #E_ISOMERISM
     * @see #getCisTransFrom2D3D(Bond)
     */
    public static int isCisTransBond(Bond bond, boolean setSingleBondFlags)
    {
        int isomerism = EZ_ISOMERISM_UNDEFINED;

        if (bond.isDouble())
        {
            Atom begin = bond.getBegin();
            Atom end = bond.getEnd();
            Bond beginBond = getTorsionBondOf(bond, begin, true);
            Bond endBond = getTorsionBondOf(bond, end, true);
            
            //System.out.println(bond.getParent().getTitle());

            if ((beginBond == null) || (endBond == null))
            {
                //try to resolve cis/trans isomerism from 2D/3D structure
                isomerism = getCisTransFrom2D3D(bond, setSingleBondFlags);
            }
            else
            {
                if ((beginBond.isUp() && endBond.isUp()) ||
                        (beginBond.isDown() && endBond.isDown()))
                {
                    isomerism = E_ISOMERISM;
                }

                if ((beginBond.isDown() && endBond.isUp()) ||
                        (beginBond.isUp() && endBond.isDown()))
                {
                    isomerism = Z_ISOMERISM;
                }
            }

            if (logger.isDebugEnabled())
            {
                if (isomerism == EZ_ISOMERISM_UNDEFINED)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Isomerism undefined for bond " +
                            bond.getIndex() + " (" + bond.getBeginIndex() +
                            "," + bond.getEndIndex() + ")");
                    }
                }
                else if (isomerism == E_ISOMERISM)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("E/trans isomerism found for bond " +
                            bond.getIndex() + " (" + bond.getBeginIndex() +
                            "," + bond.getEndIndex() + ")");
                    }
                }
                else if (isomerism == Z_ISOMERISM)
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Z/cis isomerism found for bond " +
                            bond.getIndex() + " (" + bond.getBeginIndex() +
                            "," + bond.getEndIndex() + ")");
                    }
                }
            }
        }

        return isomerism;
    }

    /**
     * Sets up/down informations for cis/trans isomerism of a double bond.<br>
     * Cases:<br>
     * E/trans -- bondUP/doubleBond/bondUP<br>
     * Z/cis   -- bondDOWN/doubleBond/bondUP<br>
     * <br>
     * The method gets two heavy atoms with the highest atomic number and sets the corresponding
     * up/down bond flags for cis/trans isomeres.
     *
     *
     * @param bond the double bond
     * @return ezType new cis/trans type for this double bond
     * @see #EZ_ISOMERISM_UNDEFINED
     * @see #Z_ISOMERISM
     * @see #E_ISOMERISM
     * @see #isCisTransBond(Bond)
     * @see #getCisTransFrom2D3D(Bond)
     */
    public static void setCisTransBond(Bond bond, int ezType)
    {
        if (bond.isDouble())
        {
            Atom begin = bond.getBegin();
            Atom end = bond.getEnd();
            Bond beginBond = getTorsionBondOf(bond, begin, false);
            Bond endBond = getTorsionBondOf(bond, end, false);

            if ((beginBond != null) && (endBond != null))
            {
                if (ezType == Z_ISOMERISM)
                {
                    beginBond.setDown();
                    endBond.setUp();
                }
                else if (ezType == E_ISOMERISM)
                {
                    beginBond.setUp();
                    endBond.setUp();
                }
            }
        }
    }

    /**
     * @param bond
     * @param begin
     * @return
     */
    private static Bond getTorsionBondOf(Bond bond, Atom atom, boolean useFlags)
    {
        atom.nbrAtomIterator();

        NbrAtomIterator nait = atom.nbrAtomIterator();
        Bond tmpBond;
        Bond torsionBond = null;
        Atom nbrAtom;

        while (nait.hasNext())
        {
            nbrAtom = nait.nextNbrAtom();
            tmpBond = nait.actualBond();

            if (useFlags)
            {
                if (((tmpBond.getFlags() & BondHelper.IS_TORUP) != 0) ||
                        ((tmpBond.getFlags() & BondHelper.IS_TORDOWN) != 0))
                {
                    torsionBond = tmpBond;

                    break;
                }
            }
            else
            {
                if (nbrAtom != atom)
                {
                    if ((torsionBond == null) ||
                            (nbrAtom.getAtomicNumber() >
                                atom.getAtomicNumber()))
                    {
                        torsionBond = tmpBond;
                    }
                }
            }
        }

        return torsionBond;
    }

    /**
     * @param bond
     * @param atom
     * @return
     */
    private static Bond getTorsionBondOf2D3D(Bond bond, Atom atom)
    {
        NbrAtomIterator nait = atom.nbrAtomIterator();
        Bond tmpBond;
        Bond torsionBond = null;
        Atom nbrAtom;
        Atom torsionAtom = null;
        boolean uniqueHeavyAtom = false;

        while (nait.hasNext())
        {
            nbrAtom = nait.nextNbrAtom();
            tmpBond = nait.actualBond();

            if (nbrAtom.getIndex() != bond.getNeighborIndex(atom))
            {
                if (torsionBond == null)
                {
                    // skip hydrogens
                    if (!AtomIsHydrogen.isHydrogen(nbrAtom))
                    {
                        torsionBond = tmpBond;
                        torsionAtom = nbrAtom;
                        uniqueHeavyAtom = true;
                    }
                }
                else
                {
                    // skip hydrogens
                    if (!AtomIsHydrogen.isHydrogen(nbrAtom) &&
                            (torsionAtom != null))
                    {
                        if (nbrAtom.getAtomicNumber() >
                                torsionAtom.getAtomicNumber())
                        {
                            torsionBond = tmpBond;
                            torsionAtom = nbrAtom;
                            uniqueHeavyAtom = true;
                        }
                        else if (nbrAtom.getAtomicNumber() ==
                                torsionAtom.getAtomicNumber())
                        {
                            uniqueHeavyAtom = false;
                        }
                    }
                }
            }
        }

        //do not use multiple heavy atoms
        if (uniqueHeavyAtom == false)
        {
            torsionBond = null;
        }

        return torsionBond;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
