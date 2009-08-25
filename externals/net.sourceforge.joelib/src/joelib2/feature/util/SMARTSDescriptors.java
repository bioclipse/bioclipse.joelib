///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SMARTSDescriptors.java,v $
//  Purpose:  Descriptor helper class.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:32 $
//            $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
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
package joelib2.feature.util;

import joelib2.feature.FeatureException;

import joelib2.feature.result.BitResult;
import joelib2.feature.result.BooleanResult;
import joelib2.feature.result.IntResult;

import joelib2.feature.types.SSKey3DS;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;

import joelib2.smarts.BasicSMARTSPatternMatcher;
import joelib2.smarts.SMARTSPatternMatcher;

import java.util.List;


/**
 * Example for loading molecules and get atom properties.
 *
 * @.author     wegnerj
 */
public class SMARTSDescriptors
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private SMARTSPatternMatcher[] smarts;
    private String[] smartsDescriptions;
    private String[] smartsStrings;

    //private MACCS maccsFingerprint = new MACCS();
    private SSKey3DS ssFingerprint = new SSKey3DS();

    //~ Constructors ///////////////////////////////////////////////////////////

    public SMARTSDescriptors()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    *  A unit test for JUnit
    *
    * @param  molURL   Description of the Parameter
    * @param  inType   Description of the Parameter
    * @param  outType  Description of the Parameter
    */
    public void calculate(Molecule mol, boolean countSMARTS)
        throws FeatureException
    {
        StringBuffer descName;
        List matchList;

        // calculate descriptors and
        // store them as native descriptor values
        for (int i = 0; i < smarts.length; i++)
        {
            if (smartsStrings[i] != null)
            {
                descName = new StringBuffer(100);

                if ((smartsDescriptions != null) &&
                        (smartsDescriptions[i] != null))
                {
                    descName.append(smartsDescriptions[i]);

                    //System.out.println("print "+smartsDescriptions[i]);
                }
                else
                {
                    descName.append("SMARTS_");

                    if (countSMARTS)
                    {
                        descName.append("c");
                    }
                    else
                    {
                        descName.append("b");
                    }

                    descName.append('_');
                    descName.append(smartsStrings[i]);
                }

                //System.out.println("match: "+smartsStrings[i]+"="+smarts[i].match(mol));
                // match SMARTS pattern
                if (!smarts[i].match(mol))
                {
                    matchList = null;
                }
                else
                {
                    matchList = smarts[i].getMatchesUnique();
                }

                // add descriptor data to molecule
                BasicPairData dp = new BasicPairData();
                dp.setKey(descName.toString());

                //System.out.println("matchList.size(): "+matchList.size());
                //System.out.println("matchList.size(): "+matchList.size());
                if (countSMARTS)
                {
                    IntResult result = new IntResult();

                    if (matchList != null)
                    {
                        //System.out.println("matchList.size(): "+matchList.size());
                        result.setInt(matchList.size());
                    }
                    else
                    {
                        result.setInt(0);
                    }

                    dp.setKeyValue(result);
                }
                else
                {
                    BooleanResult result = new BooleanResult();

                    if ((matchList != null) && (matchList.size() != 0))
                    {
                        result.value = true;
                    }
                    else
                    {
                        result.value = false;
                    }

                    dp.setKeyValue(result);
                }

                //System.out.println("add : "+smartsStrings[i]+" = "+dp);
                mol.addData(dp, true);
            }
        }
    }

    /**
    * MACCS keys.
    *
    */

    //    public void calculateMACCS(Molecule mol) throws DescriptorException
    //    {
    //        //MACCS_fingerprint
    //        PairData dp = null;
    //        BitResult fingerprint = (BitResult) maccsFingerprint.calculate(mol);
    //
    //        for (int i = 1; i <= fingerprint.maxBitSize; i++)
    //        {
    //            BooleanResult result = new BooleanResult();
    //            result.value = fingerprint.value.get(i);
    //            dp = new PairData();
    //            dp.setAttribute("SMARTS_MACCS_" + i);
    //            dp.setValue(result);
    //            mol.addData(dp, true);
    //        }
    //    }

    /**
    * Pharmacophore fingerprint.
    *
    * @.author     wegnerj
    * @.cite gxsb00
    */
    public void calculateSSKeys(Molecule mol) throws FeatureException
    {
        //Pharmacophore_fingerprint_1
        BasicPairData dp = null;
        BitResult fingerprint = (BitResult) ssFingerprint.calculate(mol);

        if (fingerprint != null)
        {
            for (int i = 1; i <= fingerprint.maxBitSize; i++)
            {
                BooleanResult result = new BooleanResult();
                result.value = fingerprint.value.get(i);
                dp = new BasicPairData();
                dp.setKey("SMARTS_SSKey_" + i);
                dp.setKeyValue(result);
                mol.addData(dp, true);
            }
        }
    }

    public void setSMARTS2Calculate(String[] _smartsStrings)
        throws FeatureException
    {
        setSMARTS2Calculate(_smartsStrings, null);
    }

    public void setSMARTS2Calculate(String[] smartsStrings,
        String[] descriptions) throws FeatureException
    {
        this.smartsStrings = new String[smartsStrings.length];
        smarts = new SMARTSPatternMatcher[smartsStrings.length];

        if (descriptions != null)
        {
            smartsDescriptions = new String[descriptions.length];
        }

        SMARTSPatternMatcher single = null;

        for (int i = 0; i < smartsStrings.length; i++)
        {
            this.smartsStrings[i] = smartsStrings[i];
            single = new BasicSMARTSPatternMatcher();
            smarts[i] = single;

            if (descriptions != null)
            {
                smartsDescriptions[i] = descriptions[i];
            }
            else
            {
                smartsDescriptions = null;
            }

            if (this.smartsStrings[i] != null)
            {
                if (!smarts[i].init(this.smartsStrings[i]))
                {
                    throw new FeatureException("Invalid SMARTS pattern: " +
                        this.smartsStrings[i]);
                }
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
