///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicRotorRulesHolder.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.5 $
//            $Date: 2005/03/03 07:13:36 $
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

import joelib2.feature.types.atomlabel.AtomHybridisation;
import joelib2.feature.types.atomlabel.AtomIsHydrogen;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.ConformerAtom;
import joelib2.molecule.ConformerMolecule;
import joelib2.molecule.MoleculeHelper;

import joelib2.rotor.BasicRotorIncrement;
import joelib2.rotor.BasicRotorRule;

import joelib2.smarts.SMARTSPatternMatcher;

import joelib2.util.HelperMethods;

import joelib2.util.iterator.NbrAtomIterator;

import joelib2.util.types.BasicIntInt;

import wsi.ra.tool.BasicPropertyHolder;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Atom representation.
 *
 * @.author     wegnerj
 * @.wikipedia Molecule
 * @.license GPL
 * @.cvsversion    $Revision: 1.5 $, $Date: 2005/03/03 07:13:36 $
 */
public class BasicRotorRulesHolder extends AbstractDataHolder
    implements RotorRulesHolder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final double JOE_DEFAULT_DELTA = 10.0f;

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicRotorRulesHolder.class.getName());
    private static BasicRotorRulesHolder instance;
    private static final String DEFAULT_RESOURCE = "joelib2/data/torlib.txt";
    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.5 $";
    private static final String RELEASE_DATE = "$Date: 2005/03/03 07:13:36 $";
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            AtomHybridisation.class, AtomIsHydrogen.class,
            SMARTSPatternMatcher.class
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    private Vector _sp2sp2 = new Vector();
    private Vector _sp3sp2 = new Vector();
    private Vector _sp3sp3 = new Vector();

    /**
    * <tt>Vector</tt> of <tt>JOERotorRule</tt>
    */
    private Vector _vr;

    /**
    * <tt>Vector</tt> of <tt>Double</tt>
    */
    private double[] sp2sp2;

    /**
    * <tt>Vector</tt> of <tt>Double</tt>
    */
    private double[] sp3sp2;

    /**
    * <tt>Vector</tt> of <tt>Double</tt>
    */
    private double[] sp3sp3;

    //~ Constructors ///////////////////////////////////////////////////////////

    public BasicRotorRulesHolder()
    {
        initialized = false;

        Properties prop = BasicPropertyHolder.instance().getProperties();
        resourceFile = prop.getProperty(this.getClass().getName() +
                ".resourceFile", DEFAULT_RESOURCE);

        _vr = new Vector();
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
     * @return    Description of the Return Value
     */
    public static synchronized BasicRotorRulesHolder instance()
    {
        if (instance == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting " +
                    BasicRotorRulesHolder.class.getName() + " instance.");
            }

            instance = new BasicRotorRulesHolder();
        }

        return instance;
    }

    /**
     *
     * @param vals <tt>Vector</tt> of <tt>float[1]</tt>
     */
    public BasicRotorIncrement getRotorIncrements(ConformerMolecule mol,
        Bond bond, int[] ref)
    {
        // IntInt
        BasicRotorIncrement increment = new BasicRotorIncrement();
        Vector vpr = new Vector();
        vpr.add(new BasicIntInt(0, bond.getBeginIndex()));
        vpr.add(new BasicIntInt(0, bond.getEndIndex()));

        increment.delta = JOE_DEFAULT_DELTA;

        int j;
        SMARTSPatternMatcher sp;

        // of type int[]
        List map;
        BasicRotorRule rotRule;

        for (int i = 0; i != _vr.size(); i++)
        {
            rotRule = ((BasicRotorRule) _vr.get(i));
            sp = rotRule.getSmartsPattern();
            rotRule.getReferenceAtoms(ref);
            ((BasicIntInt) vpr.get(0)).intValue1 = ref[1];
            ((BasicIntInt) vpr.get(1)).intValue1 = ref[2];

            if (!sp.restrictedMatch(mol, vpr, true))
            {
                int tmpInt = ((BasicIntInt) vpr.get(0)).intValue1;
                ((BasicIntInt) vpr.get(0)).intValue1 = ((BasicIntInt) vpr.get(
                            1)).intValue1;
                ((BasicIntInt) vpr.get(1)).intValue1 = tmpInt;

                if (!sp.restrictedMatch(mol, vpr, true))
                {
                    continue;
                }
            }

            map = sp.getMatches();

            for (j = 0; j < 4; j++)
            {
                ref[j] = ((int[]) map.get(0))[ref[j]];
            }

            increment.values = rotRule.getTorsionValues();
            increment.delta = rotRule.getDelta();

            ConformerAtom a1 = null;
            ConformerAtom a2 = null;
            ConformerAtom a3 = null;
            ConformerAtom a4 = null;
            ConformerAtom r = null;
            a1 = (ConformerAtom) mol.getAtom(ref[0]);
            a4 = (ConformerAtom) mol.getAtom(ref[3]);

            if (AtomIsHydrogen.isHydrogen(a1) && AtomIsHydrogen.isHydrogen(a4))
            {
                continue; //don't allow hydrogens at both ends
            }

            if (AtomIsHydrogen.isHydrogen(a1) || AtomIsHydrogen.isHydrogen(a4))
            //need a heavy atom reference - can use hydrogen
            {
                boolean swapped = false;
                a2 = (ConformerAtom) mol.getAtom(ref[1]);
                a3 = (ConformerAtom) mol.getAtom(ref[2]);

                if (AtomIsHydrogen.isHydrogen(a4))
                {
                    ConformerAtom tmpAtom = a1;
                    a1 = a4;
                    a4 = tmpAtom;

                    tmpAtom = a2;
                    a2 = a3;
                    a3 = tmpAtom;
                    swapped = true;
                }

                NbrAtomIterator nait = a2.nbrAtomIterator();
                ConformerAtom nbrAtom;

                while (nait.hasNext())
                {
                    r = nbrAtom = (ConformerAtom) nait.nextNbrAtom();

                    //bond = nait.actualBond();
                    if (!AtomIsHydrogen.isHydrogen(nbrAtom) && (r != a3))
                    {
                        break;
                    }
                }

                //      unable to find reference heavy atom
                if (r == null)
                {
                    logger.error("Unable to find reference atom r = " +
                        r.getIndex());

                    continue;
                }

                double t1 = MoleculeHelper.getTorsion(mol, a1, a2, a3, a4);
                double t2 = MoleculeHelper.getTorsion(mol, r, a2, a3, a4);
                double diff = t2 - t1;

                if (diff > 180.0)
                {
                    diff -= 360.0f;
                }

                if (diff < -180.0)
                {
                    diff += 360.0f;
                }

                diff *= HelperMethods.DEG_TO_RAD;

                for (int m = 0; m < increment.values.length; m++)
                {
                    increment.values[m] += diff;

                    if (increment.values[m] < Math.PI)
                    {
                        increment.values[m] += (2.0f * Math.PI);
                    }

                    if (increment.values[m] > Math.PI)
                    {
                        increment.values[m] -= (2.0f * Math.PI);
                    }
                }

                if (swapped)
                {
                    ref[3] = r.getIndex();
                }
                else
                {
                    ref[0] = r.getIndex();
                }

                if (logger.isDebugEnabled())
                {
                    MoleculeHelper.setTorsion(mol, r, a2, a3, a4,
                        increment.values[0]);
                    logger.debug("test = " +
                        ((increment.values[0] - diff) *
                            HelperMethods.RAD_TO_DEG) + ' ' +
                        MoleculeHelper.getTorsion(mol, a1, a2, a3, a4) + ' ' +
                        MoleculeHelper.getTorsion(mol, r, a2, a3, a4));
                }
            }

            if (logger.isDebugEnabled())
            {
                logger.debug(ref[0] + " " + ref[1] + " " + ref[2] + " " +
                    ref[3] + " " + rotRule.getSmartsString());
            }

            return increment;
        }

        //didn't match any rules - assign based on hybridization
        Atom a1 = null;
        Atom a2 = null;
        Atom a3 = null;
        Atom a4 = null;
        a2 = bond.getBegin();
        a3 = bond.getEnd();

        NbrAtomIterator nait = a2.nbrAtomIterator();

        while (nait.hasNext())
        {
            a1 = nait.nextNbrAtom();

            if (!AtomIsHydrogen.isHydrogen(a1) && (a1 != a3))
            {
                break;
            }
        }

        nait = a3.nbrAtomIterator();

        while (nait.hasNext())
        {
            a4 = nait.nextNbrAtom();

            if (!AtomIsHydrogen.isHydrogen(a4) && (a4 != a2))
            {
                break;
            }
        }

        ref[0] = a1.getIndex();
        ref[1] = a2.getIndex();
        ref[2] = a3.getIndex();
        ref[3] = a4.getIndex();

        if ((AtomHybridisation.getIntValue(a2) == 3) &&
                (AtomHybridisation.getIntValue(a3) == 3)) //sp3-sp3
        {
            increment.values = sp3sp3;

            if (logger.isDebugEnabled())
            {
                logger.debug(ref[0] + " " + ref[1] + " " + ref[2] + " " +
                    ref[3] + "sp3-sp3");
            }
        }
        else if ((AtomHybridisation.getIntValue(a2) == 2) &&
                (AtomHybridisation.getIntValue(a3) == 2)) //sp2-sp2
        {
            increment.values = sp2sp2;

            if (logger.isDebugEnabled())
            {
                logger.debug(ref[0] + " " + ref[1] + " " + ref[2] + " " +
                    ref[3] + "sp2-sp2");
            }
        }
        else //must be sp2-sp3
        {
            increment.values = sp3sp2;

            if (logger.isDebugEnabled())
            {
                logger.debug(ref[0] + " " + ref[1] + " " + ref[2] + " " +
                    ref[3] + "sp2-sp3");
            }
        }

        return increment;
    }

    protected synchronized void init()
    {
        _sp3sp3 = new Vector();
        _sp3sp2 = new Vector();
        _sp2sp2 = new Vector();

        super.init();

        sp3sp3 = new double[_sp3sp3.size()];

        for (int i = 0; i < sp3sp3.length; i++)
        {
            sp3sp3[i] = ((Double) _sp3sp3.get(i)).doubleValue();
        }

        sp3sp2 = new double[_sp3sp2.size()];

        for (int i = 0; i < sp3sp2.length; i++)
        {
            sp3sp2[i] = ((Double) _sp3sp2.get(i)).doubleValue();
        }

        sp2sp2 = new double[_sp2sp2.size()];

        for (int i = 0; i < sp2sp2.length; i++)
        {
            sp2sp2[i] = ((Double) _sp2sp2.get(i)).doubleValue();
        }
    }

    protected void parseLine(String buffer)
    {
        int i;
        int[] ref = new int[4];
        double delta;
        Vector _vals = new Vector();
        double[] vals = null;
        List<String> vs = new Vector<String>();

        if (!buffer.trim().equals("") && (buffer.charAt(0) != '#'))
        {
            HelperMethods.tokenize(vs, buffer);

            if (((String) vs.get(0)).equals("SP3-SP3"))
            {
                _sp3sp3.clear();

                for (int j = 1; j < vs.size(); j++)
                {
                    _sp3sp3.add(new Double((String) vs.get(j)));
                }

                return;
            }

            if (((String) vs.get(0)).equals("SP3-SP2"))
            {
                _sp3sp3.clear();

                for (int j = 1; j < vs.size(); j++)
                {
                    _sp3sp2.add(new Double((String) vs.get(j)));
                }

                return;
            }

            if (((String) vs.get(0)).equals("SP2-SP2"))
            {
                _sp3sp3.clear();

                for (int j = 1; j < vs.size(); j++)
                {
                    _sp2sp2.add(new Double((String) vs.get(j)));
                }

                return;
            }

            if (vs.size() > 5)
            {
                //strcpy(buffer, vs[0].c_str());
                //reference atoms
                for (i = 0; i < 4; i++)
                {
                    ref[i] = Integer.parseInt((String) vs.get(i + 1)) - 1;
                }

                //possible torsions
                _vals.clear();
                delta = JOE_DEFAULT_DELTA;

                for (i = 5; i < vs.size(); i++)
                {
                    if ((i == (vs.size() - 2)) &&
                            ((String) vs.get(i)).equals("Delta"))
                    {
                        delta = Double.parseDouble((String) vs.get(i + 1));
                        i += 2;
                    }
                    else
                    {
                        _vals.add(new Double(
                                HelperMethods.DEG_TO_RAD *
                                Double.parseDouble((String) vs.get(i))));
                    }
                }

                if (_vals.size() == 0)
                {
                    logger.error(
                        "The following rule has no associated torsions: " +
                        vs.get(0));
                }

                vals = new double[_vals.size()];

                for (int m = 0; m < vals.length; m++)
                {
                    vals[m] = ((Double) _vals.get(m)).doubleValue();
                }

                BasicRotorRule rr = new BasicRotorRule((String) vs.get(0), ref,
                        vals, delta);

                if (rr.isValid())
                {
                    _vr.add(rr);
                }
                else
                {
                    rr = null;
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
