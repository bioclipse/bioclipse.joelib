///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SMARTSMatcher.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:39 $
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

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.smarts.atomexpr.QueryAtom;

import joelib2.smarts.bondexpr.QueryBond;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.NbrAtomIterator;

import java.util.List;

import org.apache.log4j.Category;


/**
 *  The JOESSMatch class performs exhaustive matching using recursion Explicit
 *  stack handling is used to find just a single match in match()
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:39 $
 * @.cite smarts
 */
public class SMARTSMatcher implements java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(SMARTSMatcher.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    protected int[] map;

    /**
     *  Description of the Field
     */
    protected Molecule molecule;

    /**
     *  Description of the Field
     */
    protected QueryPattern queryPattern;

    /**
     *  Description of the Field
     */
    protected boolean[] usedAtoms;
    private SMARTSParser smartsParser;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOESSMatch object
     *
     * @param  mol  Description of the Parameter
     * @param  pat  Description of the Parameter
     */
    public SMARTSMatcher(SMARTSParser parseSmart, Molecule mol,
        QueryPattern pat)
    {
        molecule = mol;
        queryPattern = pat;
        map = new int[pat.getAtomsSize()];
        smartsParser = parseSmart;

        //    System.out.println("pat.acount:"+pat.acount);
        if (!mol.isEmpty())
        {
            usedAtoms = new boolean[mol.getAtomsSize() + 1];

            //memset((char*)_uatoms,'\0',sizeof(bool)*(mol.NumAtoms()+1));
        }
        else
        {
            usedAtoms = null;
            logger.error("You can't match an empty molecule (" +
                mol.getTitle() + ").");

            Object obj = null;
            obj.toString();
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @param  mlist  of type int[]
     */
    public void match(List<int[]> mlist)
    {
        match(mlist, -1);
    }

    /**
     *  Description of the Method
     */
    protected void finalize()
    {
        usedAtoms = null;
    }

    /**
     * @param  mlist  of type int[]
     * @param  bidx   Description of the Parameter
     */
    private void match(List<int[]> mlist, int bidx)
    {
        if (bidx == -1)
        {
            Atom atom;
            AtomIterator ait = molecule.atomIterator();

            while (ait.hasNext())
            {
                atom = ait.nextAtom();

                //System.out.print(" a"+atom.getIdx()+"e"+_parseSmart.evalAtomExpr(_pat.atom[0].expr, atom));
                //        ParseSmart parseSmart = new ParseSmart();
                if (smartsParser.evalAtomExpr(
                            queryPattern.getAtoms()[0].getAtom(), atom))
                {
                    map[0] = atom.getIndex();
                    usedAtoms[atom.getIndex()] = true;
                    match(mlist, 0);
                    map[0] = 0;
                    usedAtoms[atom.getIndex()] = false;
                }
            }

            //      System.out.println("");
            return;
        }

        if (bidx == queryPattern.getBondsSize())
        {
            //save full match here
            int[] tmpArr = new int[map.length];
            System.arraycopy(map, 0, tmpArr, 0, map.length);

            //mlist.add(new int[]{_map[0]});
            //System.out.print("add "+_map[0]+" ");
            mlist.add(tmpArr);

            return;
        }

        if (queryPattern.getBonds()[bidx].isGrow())
        {
            //match the next bond
            int src;
            int dst;
            src = queryPattern.getBonds()[bidx].getSource();
            dst = queryPattern.getBonds()[bidx].getDestination();

            QueryAtom aexpr = queryPattern.getAtoms()[dst].getAtom();
            QueryBond bexpr = queryPattern.getBonds()[bidx].getBond();

            Atom atom;
            Atom nbr;
            atom = molecule.getAtom(map[src]);

            NbrAtomIterator nait = atom.nbrAtomIterator();

            while (nait.hasNext())
            {
                nbr = nait.nextNbrAtom();

                //        ParseSmart parseSmart = new ParseSmart();
                //System.out.println("SMARTS (atoms="+_mol.numAtoms()+"): "+_uatoms+" "+_uatoms.length+" "+nbr.getIdx()+" "+nbr);
                if (!usedAtoms[nbr.getIndex()] &&
                        smartsParser.evalAtomExpr(aexpr, nbr) &&
                        SMARTSParser.evalBondExpr(bexpr, nait.actualBond()))
                {
                    map[dst] = nbr.getIndex();
                    usedAtoms[nbr.getIndex()] = true;
                    match(mlist, bidx + 1);
                    usedAtoms[nbr.getIndex()] = false;
                    map[dst] = 0;
                }
            }
        }
        else
        {
            //just check bond here
            Bond bond = molecule.getBond(
                    map[queryPattern.getBonds()[bidx].getSource()],
                    map[queryPattern.getBonds()[bidx].getDestination()]);

            if ((bond != null) &&
                    SMARTSParser.evalBondExpr(
                        queryPattern.getBonds()[bidx].getBond(), bond))
            {
                match(mlist, bidx + 1);
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
