///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomPropertyDescriptors.java,v $
//  Purpose:  Descriptor helper class.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.12 $
//            $Date: 2005/03/03 07:13:49 $
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

import joelib2.feature.Feature;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureFactory;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;

import joelib2.feature.result.APropDoubleArrResult;

import joelib2.feature.types.Autocorrelation;
import joelib2.feature.types.BurdenModifiedEigenvalues;
import joelib2.feature.types.GlobalTopologicalChargeIndex;
import joelib2.feature.types.RadialDistributionFunction;

import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeHelper;

import joelib2.molecule.types.BasicPairData;

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Category;


/**
 *  Example for loading molecules and get atom properties.
 *
 * @.author     wegnerj
 */
public class AtomPropertyDescriptors
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            AtomPropertyDescriptors.class.getName());
    private static final List atomProperties = FeatureHelper.instance()
                                                            .getAtomLabelFeatures(
            true);
    public static final double[] RDF_SMOOTHINGFACTORS =
        new double[]{5.0, 25.0, 100.0, 200.0};
    public static final String[] DEFAULT_DESC_NAMES =
        {
            BurdenModifiedEigenvalues.getName(), Autocorrelation.getName(),
            GlobalTopologicalChargeIndex.getName(),
            RadialDistributionFunction.getName()
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    private String[] descNames;
    private Feature[] descriptor;
    private double[] rdfSmoothings;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
    *  A unit test for JUnit
    *
    * @param  molURL   Description of the Parameter
    * @param  inType   Description of the Parameter
    * @param  outType  Description of the Parameter
    */
    public void calculate(Molecule mol) throws FeatureException
    {
        // strip salt !!!
        MoleculeHelper.stripSalts(mol);

        // delete hydrogens
        //mol.deleteHydrogens();
        //AtomProperties ap;
        //for (int j = 0; j < atomProperties.size(); j++)
        //{
        //      ap=(AtomProperties)DescriptorHelper.descFromMol(mol,(String)atomProperties.get(j),false);
        //              for (int index = 1;
        //              index <= mol.numAtoms();
        //              index++)
        //              {
        //                      System.out.println("AP: "+mol.getTitle()+" "+atomProperties.get(j)+" "+ap.getDoubleValue(index));
        //              }
        //}
        FeatureResult result;
        Hashtable[] stringInits = new Hashtable[atomProperties.size()];

        // initialize atom properties
        for (int i = 0; i < atomProperties.size(); i++)
        {
            stringInits[i] = new Hashtable();
            stringInits[i].put("ATOM_PROPERTY", atomProperties.get(i));
        }

        // start calculations
        int rdfSmooth = 0;
        boolean isRDF = false;
        String descBaseName;

        // calculate descriptors and
        // store them as native descriptor values
        for (int i = 0; i < descriptor.length; i++)
        {
            //System.out.print(descNames[i]);
            if (descriptor[i].getDescInfo().getName().equals("RDF"))
            {
                rdfSmooth = rdfSmoothings.length;
                isRDF = true;
            }
            else
            {
                rdfSmooth = 1;
                isRDF = false;
            }

            for (int s = 0; s < rdfSmooth; s++)
            {
                for (int k = 0; k < stringInits.length; k++)
                {
                    if (isRDF)
                    {
                        stringInits[k].put("SMOOTHING_FACTOR",
                            new Double(rdfSmoothings[s]));
                    }

                    // calculate atom properties
                    descriptor[i].clear();
                    result = descriptor[i].calculate(mol, stringInits[k]);

                    // has something weird happen
                    if (result == null)
                    {
                        logger.error("Descriptor " + descNames[i] + ":" +
                            atomProperties.get(k) + " was not calculated for " +
                            mol.getTitle());

                        continue;
                    }

                    // add descriptor data to molecule
                    BasicPairData dp = new BasicPairData();

                    //          dp.setAttribute( descriptor[i].getDescInfo().getName()+"_"+k );
                    if (descriptor[i].getDescInfo().getName().equals(
                                RadialDistributionFunction.getName()))
                    {
                        dp.setKey(descriptor[i].getDescInfo().getName() + "_B" +
                            rdfSmoothings[s] + ":" + atomProperties.get(k));
                    }
                    else
                    {
                        dp.setKey(descriptor[i].getDescInfo().getName() + ":" +
                            atomProperties.get(k));
                    }

                    dp.setKeyValue(result);
                    mol.addData(dp, true);

                    if (result instanceof APropDoubleArrResult)
                    {
                        if (isRDF)
                        {
                            descBaseName =
                                descriptor[i].getDescInfo().getName() + "_B" +
                                rdfSmoothings[s];
                        }
                        else
                        {
                            descBaseName = descriptor[i].getDescInfo()
                                                        .getName();
                        }

                        ((APropDoubleArrResult) result).writeSingleResults(mol,
                            descBaseName, true, false, null, null, true);
                    }
                }
            }
        }
    }

    /**
     * Set descriptors which accept the 'ATOM_PROPERTY' initializer property.
     */
    public void setDescriptors2Calculate(String[] _descNames)
        throws FeatureException
    {
        descNames = new String[_descNames.length];

        for (int i = 0; i < _descNames.length; i++)
        {
            descNames[i] = _descNames[i];
        }

        descriptor = new Feature[descNames.length];

        //get descriptor base
        for (int i = 0; i < descriptor.length; i++)
        {
            descriptor[i] = FeatureFactory.getFeature(descNames[i]);

            if (descriptor[i] == null)
            {
                throw new FeatureException("Descriptor " + descNames[i] +
                    " can't be loaded.");
            }
        }
    }

    public void setRDFSmoothings(double[] smoothings)
    {
        rdfSmoothings = new double[smoothings.length];

        for (int i = 0; i < smoothings.length; i++)
        {
            rdfSmoothings[i] = smoothings[i];
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
