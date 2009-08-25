///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicTransformationRulesHolder.java,v $
//  Purpose:  Transformation of chemical groups.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.data;

import joelib2.feature.types.atomlabel.AtomPartialCharge;

import joelib2.molecule.Atom;
import joelib2.molecule.AtomHelper;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.smarts.BasicSMARTSPatternMatcher;
import joelib2.smarts.SMARTSPatternMatcher;

import joelib2.util.types.BasicIntInt;
import joelib2.util.types.BasicIntIntInt;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Transformation of chemical groups.
 *
 * @.author     wegnerj
 * @.wikipedia Molecule
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:29 $
 * @see joelib2.data.BasicProtonationModel
 */
public class BasicTransformationRulesHolder implements TransformationRulesHolder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicTransformationRulesHolder.class.getName());
    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.10 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:29 $";
    private static final Class[] DEPENDENCIES =
        new Class[]{BasicSMARTSPatternMatcher.class};

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * Stores atoms which atom type should be changed.
     */
    private List<BasicIntInt> atom2Change;

    /**
     * Stores atoms which should be deleted.
     */
    private List<int[]> atom2Delete;
    private SMARTSPatternMatcher beginPattern;

    /**
     * Stores bonds which bond type should be changed.
     */
    private List<BasicIntIntInt> bond2Change;

    /**
     * Stores atoms which charge should be deleted.
     */
    private List<BasicIntInt> charge2Change;
    private SMARTSPatternMatcher endPattern;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOEChemTransformation object
     */
    public BasicTransformationRulesHolder()
    {
        atom2Delete = new Vector<int[]>();
        atom2Change = new Vector<BasicIntInt>();
        charge2Change = new Vector<BasicIntInt>();
        bond2Change = new Vector<BasicIntIntInt>();

        beginPattern = new BasicSMARTSPatternMatcher();
        endPattern = new BasicSMARTSPatternMatcher();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getReleaseDate()
    {
        return VENDOR;
    }

    public static String getReleaseVersion()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_VERSION);
    }

    public static String getVendor()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_DATE);
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean apply(Molecule mol)
    {
        if (!beginPattern.match(mol))
        {
            return (false);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Apply SMARTS transformation for pattern '" +
                beginPattern.getSmarts() + "' >> " + endPattern.getSmarts() +
                " on " + mol.getTitle());
        }

        List mlist = beginPattern.getMatchesUnique();

        changeCharges(mol, mlist);
        changeBonds(mol, mlist);
        changeElement(mol, mlist);
        deleteAtoms(mol, mlist);

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  bgn  Description of the Parameter
     * @param  end  Description of the Parameter
     * @return      Description of the Return Value
     */
    protected boolean init(String bgn, String end)
    {
        //    System.out.println("beginPattern "+beginPattern);
        if (!beginPattern.init(bgn))
        {
            return (false);
        }

        if (end.length() != 0)
        {
            if (!endPattern.init(end))
            {
                return (false);
            }
        }

        //find atoms to be deleted
        initAtoms2Del();

        //find elements to be changed
        initElements();

        //find charges to modify
        initCharges();

        //find bonds to be modified
        initBonds();

        //make sure there is some kind of transform to do here
        if ((atom2Delete.size() == 0) && (charge2Change.size() == 0) &&
                (bond2Change.size() == 0))
        {
            return false;
        }

        return true;
    }

    /**
    * @param mol
    * @param mlist
    */
    private void changeBonds(Molecule mol, List mlist)
    {
        BasicIntIntInt iii;
        int[] itmp;

        if (bond2Change.size() != 0)
        {
            //modify bond orders
            if (logger.isDebugEnabled())
            {
                logger.debug("modify " + bond2Change.size() + " bonds");
            }

            Bond bond;

            for (int i = 0; i < mlist.size(); i++)
            {
                //v = (Vector)mlist.get(i);
                itmp = (int[]) mlist.get(i);

                for (int j = 0; j < bond2Change.size(); j++)
                {
                    iii = (BasicIntIntInt) bond2Change.get(j);

                    //itmp  = (int[])v.get(iii.ii.i1);
                    //itmp2 = (int[])v.get(iii.i);
                    bond = mol.getBond(itmp[iii.intPair.intValue1],
                            itmp[iii.intValue]);

                    if (bond == null)
                    {
                        logger.error("Unable to find bond.");

                        continue;
                    }

                    bond.setBondOrder(iii.intValue);
                }
            }
        }
    }

    /**
     *
     */
    private void changeCharges(Molecule mol, List mlist)
    {
        int[] itmp;
        BasicIntInt ii;

        if (charge2Change.size() != 0)
        {
            //modify charges
            if (logger.isDebugEnabled())
            {
                logger.debug("modify " + charge2Change.size() + " charges");
            }

            for (int i = 0; i < mlist.size(); i++)
            {
                //v = (Vector)mlist.get(i);
                itmp = (int[]) mlist.get(i);

                for (int j = 0; j < charge2Change.size(); j++)
                {
                    ii = (BasicIntInt) charge2Change.get(j);

                    if (ii.intValue1 < itmp.length)
                    {
                        Atom atom = mol.getAtom(itmp[ii.intValue1]);
                        atom.setFormalCharge(ii.intValue2);

                        if (AtomHelper.correctFormalCharge(atom))
                        {
                            String rule = beginPattern.getSmarts() + "' >> " +
                                endPattern.getSmarts();
                            logger.warn(mol.getTitle() +
                                ": Check transformation rule '" + rule + "'");
                        }

                        if (logger.isDebugEnabled())
                        {
                            logger.debug(mol.getTitle() +
                                ": set formal charge for atom " +
                                mol.getAtom(itmp[ii.intValue1]).getIndex() +
                                " " + mol.getAtom(itmp[ii.intValue1]) + " to " +
                                ii.intValue2);
                        }
                    }
                }
            }

            mol.deleteData(AtomPartialCharge.getName());
        }
    }

    /**
     * @param mol
     * @param mlist
     */
    private void changeElement(Molecule mol, List mlist)
    {
        int[] itmp;
        BasicIntInt ii;

        //      if(logger.isDebugEnabled()) logger.debug("change/delete atom");
        if (atom2Change.size() != 0)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("modify " + atom2Change.size() + " atoms");
            }

            for (int i = 0; i < mlist.size(); i++)
            {
                //v = (Vector)mlist.get(i);
                itmp = (int[]) mlist.get(i);

                for (int k = 0; k < atom2Change.size(); k++)
                {
                    ii = (BasicIntInt) atom2Change.get(k);

                    //itmp  = (int[])v.get(ii.i1);
                    mol.getAtom(itmp[ii.intValue1]).setAtomicNumber(
                        ii.intValue2);
                }
            }
        }
    }

    /**
         * @param mol
         * @param mlist
         */
    private void deleteAtoms(Molecule mol, List mlist)
    {
        int[] itmp;
        int[] itmp2;

        if ((atom2Delete.size() != 0))
        {
            //make sure same atom isn't delete twice
            Vector<boolean[]> vda = new Vector<boolean[]>();
            Vector<Atom> vdel = new Vector<Atom>();
            boolean[] btmp;

            vda.ensureCapacity(mol.getAtomsSize() + 1);

            for (int i = 0; i <= mol.getAtomsSize(); i++)
            {
                vda.add(new boolean[]{false});
            }

            for (int i = 0; i < mlist.size(); i++)
            {
                itmp = (int[]) mlist.get(i);

                if (logger.isDebugEnabled())
                {
                    logger.debug("delete " + atom2Delete.size() + " atoms");
                }

                for (int j = 0; j < atom2Delete.size(); j++)
                {
                    itmp2 = atom2Delete.get(j);

                    //itmp2 = (int[]) v.get(itmp2[0]);
                    btmp = vda.get(itmp[itmp2[0]]);

                    if (!btmp[0])
                    {
                        btmp[0] = true;
                        vdel.add(mol.getAtom(itmp[itmp2[0]]));
                    }
                }
            }

            //                  if (logger.isDebugEnabled())
            //                  {
            //                          logger.debug("delete " + atom2Delete.size() + " atoms: begin modifiy");
            //                  }
            mol.beginModify();

            for (int k = 0; k < vdel.size(); k++)
            {
                mol.deleteAtom(vdel.get(k));
            }

            mol.endModify(true, false);
        }
    }

    /**
     *
     */
    private void initAtoms2Del()
    {
        int i;
        int j;
        boolean found;

        for (i = 0; i < beginPattern.getQueryAtomsSize(); i++)
        {
            if (beginPattern.getVectorBinding(i) != 0)
            {
                found = false;

                for (j = 0; j < endPattern.getQueryAtomsSize(); j++)
                {
                    if (endPattern.getVectorBinding(j) != 0)
                    {
                        found = true;

                        break;
                    }
                }

                if (!found)
                {
                    atom2Delete.add(new int[]{i});
                }
            }
        }
    }

    /**
         *
         */
    private void initBonds()
    {
        int i;
        int j;

        BasicIntIntInt bbb = new BasicIntIntInt();
        int bvb1;
        int bvb2;
        BasicIntIntInt eee = new BasicIntIntInt();
        int evb1;
        int evb2;

        for (i = 0; i < beginPattern.getQueryBondsSize(); i++)
        {
            beginPattern.getQueryBond(bbb, i);
            bvb1 = beginPattern.getVectorBinding(bbb.intPair.intValue1);
            bvb2 = beginPattern.getVectorBinding(bbb.intPair.intValue2);

            if ((bvb1 == 0) || (bvb2 == 0))
            {
                continue;
            }

            for (j = 0; j < endPattern.getQueryBondsSize(); j++)
            {
                endPattern.getQueryBond(eee, j);
                evb1 = endPattern.getVectorBinding(eee.intPair.intValue1);
                evb2 = endPattern.getVectorBinding(eee.intPair.intValue2);

                if (((bvb1 == evb1) && (bvb2 == evb2)) ||
                        ((bvb1 == evb2) && (bvb2 == evb1)))
                {
                    if (bbb.intValue == eee.intValue)
                    {
                        break;
                    }

                    //nothing to modify if bond orders identical
                    bond2Change.add(new BasicIntIntInt(bbb.intPair,
                            eee.intValue));

                    //          System.out.println("bond2change:"+bgn+" "+ end);
                    break;
                }
            }
        }
    }

    /**
     *
     */
    private void initCharges()
    {
        int i;
        int j;
        int vb;
        int chrg;

        for (i = 0; i < beginPattern.getQueryAtomsSize(); i++)
        {
            if ((vb = beginPattern.getVectorBinding(i)) != 0)
            {
                chrg = beginPattern.getQueryAtomCharge(i);

                for (j = 0; j < endPattern.getQueryAtomsSize(); j++)
                {
                    if ((vb == endPattern.getVectorBinding(j)))
                    {
                        if (chrg != endPattern.getQueryAtomCharge(j))
                        {
                            charge2Change.add(new BasicIntInt(i,
                                    endPattern.getQueryAtomCharge(j)));
                        }
                    }
                }
            }
        }
    }

    /**
     *
     */
    private void initElements()
    {
        int i;
        int j;
        int vb;
        int ele;

        for (i = 0; i < beginPattern.getQueryAtomsSize(); i++)
        {
            if ((vb = beginPattern.getVectorBinding(i)) != 0)
            {
                ele = beginPattern.getQueryAtomIndex(i);

                for (j = 0; j < endPattern.getQueryAtomsSize(); j++)
                {
                    if ((vb == endPattern.getVectorBinding(j)))
                    {
                        if (ele != endPattern.getQueryAtomIndex(j))
                        {
                            atom2Change.add(new BasicIntInt(i,
                                    endPattern.getQueryAtomIndex(j)));

                            break;
                        }
                    }
                }
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
