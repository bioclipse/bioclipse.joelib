///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SSKey3DS.java,v $
//  Purpose:  Pharmacophore fingerprint.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.14 $
//            $Date: 2006/02/22 02:18:22 $
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
package joelib2.feature.types;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.BasicFeatureDescription;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.Feature;
import joelib2.feature.FeatureDescription;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureFactory;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;
import joelib2.feature.ResultFactory;

import joelib2.feature.result.BitResult;
import joelib2.feature.result.DoubleResult;
import joelib2.feature.result.IntResult;

import joelib2.feature.types.atomlabel.AtomIsHalogen;
import joelib2.feature.types.atomlabel.AtomIsHeteroatom;
import joelib2.feature.types.count.AromaticBonds;
import joelib2.feature.types.count.HBA1;
import joelib2.feature.types.count.HBA2;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.ring.Ring;
import joelib2.ring.RingFinderSSSR;

import joelib2.smarts.BasicSMARTSPatternMatcher;
import joelib2.smarts.SMARTSPatternMatcher;

import joelib2.util.BasicBitVector;
import joelib2.util.BasicProperty;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;


/**
 * Pharmacophore fingerprint.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.14 $, $Date: 2006/02/22 02:18:22 $
 * @.cite gxsb00
 */
public class SSKey3DS implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.14 $";
    private static final String RELEASE_DATE = "$Date: 2006/02/22 02:18:22 $";
    private static Category logger = Category.getInstance(SSKey3DS.class
            .getName());
    public static final int FP_SIZE = 54;
    private static Map<String, SMARTSPatternMatcher> smartsPatterns = null;
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            AromaticBonds.class, AtomIsHeteroatom.class, RingFinderSSSR.class,
            FractionRotatableBonds.class, HBA1.class, HBA2.class,
            BasicSMARTSPatternMatcher.class
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    private String aromaticBondsName = AromaticBonds.getName();

    private BasicFeatureInfo descInfo;
    private String fracRotBondsName = FractionRotatableBonds.getName();
    private String hbaDescriptorName = HBA1.getName();
    private String hbdDescriptorName = HBA2.getName();

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape2 object
     */
    public SSKey3DS()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.BitResult");

        if (smartsPatterns == null)
        {
            smartsPatterns = getFingerprintPatterns();
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return SSKey3DS.class.getName();
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

    public BasicProperty[] acceptedProperties()
    {
        return null;
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  FeatureException  Description of the Exception
     */
    public FeatureResult calculate(Molecule mol) throws FeatureException
    {
        FeatureResult result = ResultFactory.instance().getFeatureResult(
                descInfo.getName());

        return calculate(mol, result, null);
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  initData                 Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  FeatureException  Description of the Exception
     */
    public FeatureResult calculate(Molecule mol, Map properties)
        throws FeatureException
    {
        FeatureResult result = ResultFactory.instance().getFeatureResult(
                descInfo.getName());

        return calculate(mol, result, properties);
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  descResult               Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  FeatureException  Description of the Exception
     */
    public FeatureResult calculate(Molecule mol, FeatureResult descResult)
        throws FeatureException
    {
        return calculate(mol, descResult, null);
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  initData                 Description of the Parameter
     * @param  descResult               Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  FeatureException  Description of the Exception
     */
    public FeatureResult calculate(Molecule mol, FeatureResult descResult,
        Map properties) throws FeatureException
    {
        if (descResult == null)
        {
            return null;
        }

        if (!(descResult instanceof BitResult))
        {
            logger.error(descInfo.getName() + " result should be of type " +
                BitResult.class.getName() + " but it's of type " +
                descResult.getClass().toString());
        }

        if (mol.isEmpty())
        {
            BasicBitVector fp = new BasicBitVector(FP_SIZE);
            BitResult result = (BitResult) descResult;
            result.value = fp;
            result.maxBitSize = FP_SIZE;
            logger.warn("Empty molecule '" + mol.getTitle() +
                "'. No bits were set.");

            return result;
        }

        Feature hbd = null;
        Feature hba = null;
        Feature fracRotBonds = null;
        Feature aromaticBonds = null;

        try
        {
            hbd = FeatureFactory.getFeature(hbdDescriptorName);
            hba = FeatureFactory.getFeature(hbaDescriptorName);
            fracRotBonds = FeatureFactory.getFeature(fracRotBondsName);
            aromaticBonds = FeatureFactory.getFeature(aromaticBondsName);
        }
        catch (FeatureException ex)
        {
            ex.printStackTrace();

            return null;
        }

        IntResult hbdResult = null;
        IntResult hbaResult = null;
        IntResult aroResult = null;
        DoubleResult frbResult = null;

        try
        {
            //calculate hydrogen bond donors
            hbdResult = (IntResult) hbd.calculate(mol);

            if (hbdResult == null)
            {
                logger.error("Hydrogen bond donors " + hbdDescriptorName +
                    " can't be calculated.");

                return null;
            }

            //calculate hydrogen bond acceptors
            hbaResult = (IntResult) hba.calculate(mol);

            if (hbaResult == null)
            {
                logger.error("Hydrogen bond acceptors " + hbaDescriptorName +
                    " can't be calculated.");

                return null;
            }

            //calculate fraction of rotatable bonds
            frbResult = (DoubleResult) fracRotBonds.calculate(mol);

            if (frbResult == null)
            {
                logger.error("Fraction of rotatable bonds " + fracRotBondsName +
                    " can't be calculated.");

                return null;
            }

            //calculate number of aromatic bonds
            aroResult = (IntResult) aromaticBonds.calculate(mol);

            if (aroResult == null)
            {
                logger.error("Number of aromatic bonds " + aromaticBondsName +
                    " can't be calculated.");

                return null;
            }
        }
        catch (FeatureException ex)
        {
            ex.printStackTrace();
        }

        BasicBitVector fp = new BasicBitVector(54);

        /////////////////////////////
        // FRB
        /////////////////////////////
        if (frbResult.value > 0.0)
        {
            //1:  Fraction of rotatable bonds: 0.0 < x <= 0.1 (0)
            fp.set(0);
        }

        if (frbResult.value > 0.1)
        {
            //2:  Fraction of rotatable bonds: 0.1 < x <= 0.2
            fp.set(1);
        }

        if (frbResult.value > 0.2)
        {
            //3:  Fraction of rotatable bonds: 0.2 < x <= 0.3
            fp.set(2);
        }

        if (frbResult.value > 0.3)
        {
            //4:  Fraction of rotatable bonds: 0.3 < x <= 0.4
            fp.set(3);
        }

        if (frbResult.value > 0.4)
        {
            //5:  Fraction of rotatable bonds:       x > 0.4
            fp.set(4);
        }

        ////////////////////////////
        // ARB
        ////////////////////////////
        if (aroResult.getInt() >= 2)
        {
            //6:  Aromatic bonds in molecule:  2 - 7  (0-1)
            fp.set(5);
        }

        if (aroResult.getInt() >= 8)
        {
            //7:  Aromatic bonds in molecule:  8 -15
            fp.set(6);
        }

        if (aroResult.getInt() >= 16)
        {
            //8:  Aromatic bonds in molecule:  16-19
            fp.set(7);
        }

        if (aroResult.getInt() >= 20)
        {
            //9:  Aromatic bonds in molecule:  20-25
            fp.set(8);
        }

        if (aroResult.getInt() >= 26)
        {
            //10: Aromatic bonds in molecule:  26-31
            fp.set(9);
        }

        if (aroResult.getInt() >= 32)
        {
            //11: Aromatic bonds in molecule:  32-37
            fp.set(10);
        }

        if (aroResult.getInt() >= 38)
        {
            //12: Aromatic bonds in molecule:  >= 38
            fp.set(11);
        }

        //////////////////////////////
        // SSKeys
        //////////////////////////////
        if (hasHeteroCycle(mol))
        {
            //13: heterocycle
            fp.set(12);
        }

        if (hasSMARTSPattern(mol, "13"))
        {
            //14: aromatic OH
            fp.set(13);
        }

        if (hasSMARTSPattern(mol, "14"))
        {
            //15: aliphatic OH
            fp.set(14);
        }

        if (hasSMARTSPattern(mol, "15"))
        {
            //16: aliphatic secondary amine
            fp.set(15);
        }

        if (hasSMARTSPattern(mol, "16"))
        {
            //17: aliphatic tertiary amine
            fp.set(16);
        }

        if (hasSMARTSPattern(mol, "17"))
        {
            //18: phenyl ring
            fp.set(17);
        }

        if (hasNRing(mol))
        {
            //19: Nitrogen-containing ring
            fp.set(18);
        }

        if (hasSMARTSPattern(mol, "19"))
        {
            //20: -SO2
            fp.set(19);
        }

        if (hasSMARTSPattern(mol, "20"))
        {
            //21: -SO
            fp.set(20);
        }

        if (hasSMARTSPattern(mol, "21"))
        {
            //22: ester
            fp.set(21);
        }

        if (hasSMARTSPattern(mol, "22"))
        {
            //23: amide
            fp.set(22);
        }

        if (hasNonAromatic5Ring(mol))
        {
            //24: 5-membered non-aromatic ring
            fp.set(23);
        }

        if (hasAromatic5Ring(mol))
        {
            //25: 5-membered aromatic ring
            fp.set(24);
        }

        if (hasRingGreater9(mol))
        {
            //26: 9-membered or larger (fused) ring
            fp.set(25);
        }

        if (hasSMARTSPattern(mol, "26"))
        {
            //27: fused ring system
            fp.set(26);
        }

        if (hasSMARTSPattern(mol, "27"))
        {
            //28: fused aromatic ring system
            fp.set(27);
        }

        if (hasSMARTSPattern(mol, "28"))
        {
            //29: -OSO
            fp.set(28);
        }

        if (hasHalogen(mol))
        {
            //30: halogen atom
            fp.set(29);
        }

        if (hasSMARTSPattern(mol, "30"))
        {
            //31: Nitrogen attached to alpha-carbon of aromatic system
            fp.set(30);
        }

        if (hasSMARTSPattern(mol, "31"))
        {
            //32: -NO2
            fp.set(31);
        }

        if (hasSMARTSPattern(mol, "32"))
        {
            //33: rings separated by 2-3 non-ring atoms
            fp.set(32);
        }

        if (hasSMARTSPattern(mol, "33"))
        {
            //34: rings separated by 4-5 non-ring atoms
            fp.set(33);
        }

        if (hasSMARTSPattern(mol, "34"))
        {
            //35: NN
            fp.set(34);
        }

        if (hasSMARTSPattern(mol, "35"))
        {
            //36: C attached to 3 carbons and a hetero atom
            fp.set(35);
        }

        if (hasSMARTSPattern(mol, "36"))
        {
            //37: oxygen separated by 2 atoms
            fp.set(36);
        }

        if (hasSMARTSPattern(mol, "37"))
        {
            //38: methyl attached to hetero atom
            fp.set(37);
        }

        if (hasDoubleBond(mol))
        {
            //39: double bond
            fp.set(38);
        }

        if (hasSMARTSPattern(mol, "39"))
        {
            //40: Non-H atom linked to 3 heteroatoms
            fp.set(39);
        }

        if (hasSMARTSPattern(mol, "40"))
        {
            //41: Quaternary atom
            fp.set(40);
        }

        if (hasSMARTSPattern(mol, "41"))
        {
            //42: 2 methylenes separated by 2 atoms
            fp.set(41);
        }

        if (hasSMARTSPattern(mol, "42"))
        {
            //43: non-ring oxygen attached to aromatic system
            fp.set(42);
        }

        if (hasSMARTSPattern(mol, "43"))
        {
            //44: 2 non-C,H atoms separated by 2 atoms
            fp.set(43);
        }

        ///////////////////////////////
        // HBA
        ///////////////////////////////
        if (hbaResult.getInt() >= 1)
        {
            //45: HBA=1 (0)
            fp.set(44);
        }

        if (hbaResult.getInt() >= 2)
        {
            //46: HBA=2
            fp.set(45);
        }

        if (hbaResult.getInt() >= 3)
        {
            //47: HBA=3
            fp.set(46);
        }

        if (hbaResult.getInt() >= 4)
        {
            //48: HBA=4
            fp.set(47);
        }

        if (hbaResult.getInt() >= 5)
        {
            //49: HBA=5
            fp.set(48);
        }

        if (hbaResult.getInt() >= 6)
        {
            //50: HBA=6
            fp.set(49);
        }

        if (hbaResult.getInt() >= 7)
        {
            //51: HBA=7
            fp.set(50);
        }

        if (hbaResult.getInt() >= 8)
        {
            //52: HBA=8
            fp.set(51);
        }

        if (hbaResult.getInt() >= 9)
        {
            //53: HBA=9
            fp.set(52);
        }

        if (hbaResult.getInt() >= 10)
        {
            //54: HBA>=10
            fp.set(53);
        }

        BitResult result = (BitResult) descResult;
        result.value = fp;
        result.maxBitSize = 54;

        return result;
    }

    /**
     *  Description of the Method
     */
    public void clear()
    {
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public BasicFeatureInfo getDescInfo()
    {
        return descInfo;
    }

    /**
     *  Gets the description attribute of the Descriptor object
     *
     * @return    The description value
     */
    public FeatureDescription getDescription()
    {
        return new BasicFeatureDescription(descInfo.getDescriptionFile());
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }

    /**
     *  Description of the Method
     *
     * @param  initData  Description of the Parameter
     * @return           Description of the Return Value
     */
    public boolean initialize(Map properties)
    {
        return true;
    }

    /**
    * Test the implementation of this descriptor.
    *
    * @return <tt>true</tt> if the implementation is correct
    */
    public boolean testDescriptor()
    {
        return true;
    }

    private static Map<String, SMARTSPatternMatcher> getFingerprintPatterns()
    {
        Map<String, SMARTSPatternMatcher> smarts = new Hashtable<String, SMARTSPatternMatcher>();

        initSingleSMARTS(smarts, "13", "[OX2;H1;$(O-a)]");
        initSingleSMARTS(smarts, "14", "[OX2;H1;$(O-C)]");
        initSingleSMARTS(smarts, "15", "[NX3;H1;$(N(~C)~C)]");
        initSingleSMARTS(smarts, "16", "[NQ3;H0;$(N(~C)(~C)~C)]");
        initSingleSMARTS(smarts, "17", "[Cc1ccccc1]");
        initSingleSMARTS(smarts, "19", "[#16Q4;$(S(=O)(=O))]-*");
        initSingleSMARTS(smarts, "20", "[#16Q2;$(S~O);!$(S(~O)~O)]-*");
        initSingleSMARTS(smarts, "21", "C([OQ2])=O");
        initSingleSMARTS(smarts, "22", "C([NX3])=O");
        initSingleSMARTS(smarts, "26", "[!a;R2]");
        initSingleSMARTS(smarts, "27", "[a;R2]");
        initSingleSMARTS(smarts, "28", "[#16Q2;$(S(~O)~O)]-*");
        initSingleSMARTS(smarts, "30", "cC~N");
        initSingleSMARTS(smarts, "31", "[#7Q3;$(N(~O)~O)]-*");
        initSingleSMARTS(smarts, "32",
            "[$([*;R][*;!R][*;!R][*;R]),$([*;R][*;!R][*;!R][*;!R][*;R])]");
        initSingleSMARTS(smarts, "33",
            "[$([*;R][*;!R][*;!R][*;!R][*;!R][*;R]),$([*;R][*;!R][*;!R][*;!R][*;!R][*;!R][*;R])]");
        initSingleSMARTS(smarts, "34", "[NN]");
        initSingleSMARTS(smarts, "35", "C([#6])([#6])[#6][*;!C;!H]");
        initSingleSMARTS(smarts, "36", "[#8]**[#8]");
        initSingleSMARTS(smarts, "37", "[C;H3][*;!C;!H]");
        initSingleSMARTS(smarts, "39",
            "[*X3;!H]([*;!$([#6]);!H])([*;!$([#6]);!H])[*;!$([#6]);!H]");
        initSingleSMARTS(smarts, "40", "[*X4]");
        initSingleSMARTS(smarts, "41", "*=C~*~*~C=*");
        initSingleSMARTS(smarts, "42", "[O;R0]~a");
        initSingleSMARTS(smarts, "43", "[!#6;!H]~*~*~[!#6;!H]");

        return smarts;
    }

    private static void initSingleSMARTS(
        Map<String, SMARTSPatternMatcher> table, String identifier,
        String smartPattern)
    {
        SMARTSPatternMatcher smarts = new BasicSMARTSPatternMatcher();

        if (!smarts.init(smartPattern))
        {
            logger.error("Invalid SMARTS pattern (id:" + identifier + ") '" +
                smartPattern + "' defined in " + SSKey3DS.class.getName());

            return;
        }

        table.put(identifier, smarts);
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    private boolean hasAromatic5Ring(Molecule mol)
    {
        List sssRings = mol.getSSSR();
        Ring ring;

        for (int i = 0; i < sssRings.size(); i++)
        {
            ring = (Ring) sssRings.get(i);

            if (ring.size() == 5)
            {
                if (ring.isAromatic())
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    private boolean hasDoubleBond(Molecule mol)
    {
        BondIterator bit = mol.bondIterator();
        Bond bond;

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (bond.getBondOrder() == 2)
            {
                return true;
            }
        }

        return false;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    private boolean hasHalogen(Molecule mol)
    {
        Atom atom;
        AtomIterator ait = mol.atomIterator();
        boolean hasHalogen = false;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (AtomIsHalogen.isHalogen(atom))
            {
                hasHalogen = true;

                break;
            }
        }

        return hasHalogen;
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    private boolean hasHeteroCycle(Molecule mol)
    {
        List sssRings = mol.getSSSR();
        Ring ring;
        boolean hasHetero = false;

        //System.out.println("rings: "+sssRings.size());
        for (int i = 0; i < sssRings.size(); i++)
        {
            ring = (Ring) sssRings.get(i);
            //System.out.println(ring);
            
            if (ring.isHetero())
            {
                hasHetero = true;

                break;
            }
        }

        return hasHetero;
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    private boolean hasNonAromatic5Ring(Molecule mol)
    {
        List sssRings = mol.getSSSR();
        Ring ring;

        for (int i = 0; i < sssRings.size(); i++)
        {
            ring = (Ring) sssRings.get(i);

            if (ring.size() == 5)
            {
                if (!ring.isAromatic())
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    private boolean hasNRing(Molecule mol)
    {
        List sssRings = mol.getSSSR();
        Ring ring;
        int[] atomIDs;

        for (int i = 0; i < sssRings.size(); i++)
        {
            ring = (Ring) sssRings.get(i);
            atomIDs = ring.getAtomIndices();

            for (int j = 0; j < atomIDs.length; j++)
            {
                if ((mol.getAtom(atomIDs[j])).getAtomicNumber() == 7)
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    private boolean hasRingGreater9(Molecule mol)
    {
        List sssRings = mol.getSSSR();
        Ring ring;

        for (int i = 0; i < sssRings.size(); i++)
        {
            ring = (Ring) sssRings.get(i);

            if (ring.size() > 9)
            {
                return true;
            }
        }

        return false;
    }

    /**
     *  Description of the Method
     *
     * @param  mol           Description of the Parameter
     * @param  smartPattern  Description of the Parameter
     * @return               Description of the Return Value
     */
    private boolean hasSMARTSPattern(Molecule mol, String id)
    {
        SMARTSPatternMatcher smarts = (SMARTSPatternMatcher) smartsPatterns.get(
                id);

        if (smarts == null)
        {
            logger.error("ID '" + id + "' is missing in " +
                SSKey3DS.class.getName());

            return false;
        }

        smarts.match(mol);

        if (smarts.getMatchesSize() > 0)
        {
            return true;
        }

        return false;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
