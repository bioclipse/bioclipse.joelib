///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ExampleSetHelper.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
//            $Date: 2005/02/17 16:48:28 $
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
package joelib2.algo.datamining.yale;

import joelib2.feature.NativeValue;

import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeVector;

import joelib2.molecule.types.BasicPairData;

import joelib2.process.types.DescriptorBinning;

//import joelib2.util.JOEHelper;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.WekaException;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Category;


/**
 *  Atom representation.
 *
 * @.author    wegnerj
 */
public class ExampleSetHelper
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "jcompchem.joelib2.algo.yale.ExampleSetHelper");

    //~ Constructors ///////////////////////////////////////////////////////////

    public ExampleSetHelper()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Instances createMolInstances(MoleculeVector molecules,
        String[] attributes, int[] attributeTypes) throws WekaException
    {
        // load descriptor binning
        DescriptorBinning binning = DescriptorBinning.getDescBinning(molecules);

        int length = molecules.getSize();

        if (attributes.length != attributeTypes.length)
        {
            throw new WekaException(
                "Different number of attributes and attribute types.");

            //return null;
        }

        Enumeration enumeration = binning.getDescriptors();
        FastVector attributesV = new FastVector(binning.numberOfDescriptors());
        Molecule mol;
        BasicPairData pairData;

        for (int i = 0; i < attributes.length; i++)
        {
            if (attributeTypes[i] == Attribute.NUMERIC)
            {
                // numeric
                attributesV.addElement(new Attribute(
                        (String) enumeration.nextElement(),
                        attributesV.size()));
            }
            else if (attributeTypes[i] == Attribute.NOMINAL)
            {
                // nominal
                // create a list with all nominal values
                Hashtable hashed = new Hashtable();

                for (int j = 0; j < length; j++)
                {
                    mol = molecules.getMol(j);

                    // get unparsed data
                    pairData = (BasicPairData)mol.getData(attributes[i], false);

                    if (pairData != null)
                    {
                        if (pairData.getKeyValue() instanceof String)
                        {
                            hashed.put(pairData.getKeyValue(), "");
                        }
                        else
                        {
                            hashed.put(pairData.toString(), "");
                        }
                    }
                }

                // store list of nominal values in the Weka data structure
                FastVector attributeValues = new FastVector(hashed.size());
                String tmp;

                for (Enumeration e = hashed.keys(); e.hasMoreElements();)
                {
                    tmp = (String) e.nextElement();
                    attributeValues.addElement(tmp);

                    //System.out.println("NOMINAL " + tmp);
                }

                attributesV.addElement(new Attribute(attributes[i],
                        attributeValues, attributesV.size()));
            }
        }

        int size = attributesV.size();
        Attribute attribute;

        // create molecule instances
        Instances instances = new Instances("MoleculeInstances", attributesV,
                attributesV.size());

        // iterate over all instances (to generate them)
        double[] instance;

        for (int i = 0; i < length; i++)
        {
            mol = molecules.getMol(i);
            instance = new double[size];

            for (int j = 0; j < size; j++)
            {
                attribute = (Attribute) attributesV.elementAt(j);

                // get parsed data
                pairData = (BasicPairData)mol.getData(attribute.name(), true);

                // add nominal or numeric or missing value
                if (pairData == null)
                {
                    instance[attribute.index()] = Instance.missingValue();
                }
                else
                {
                    if (attribute.isNominal())
                    {
                        // nominal
                        String tmpS = pairData.toString().trim();

                        if (tmpS.indexOf("\n") != -1)
                        {
                            throw new WekaException("Descriptor " +
                                attribute.name() +
                                " contains multiple lines and is not a valid nominal value.");
                        }
                        else
                        {
                            instance[attribute.index()] = attribute
                                .indexOfValue(pairData.toString());

                            if (instance[attribute.index()] == -1)
                            {
                                // invalid nominal value
                                logger.error("Invalid nominal value.");

                                return null;
                            }
                        }
                    }
                    else
                    {
                        // numeric
                        if (pairData instanceof NativeValue)
                        {
                            double tmpD = ((NativeValue) pairData)
                                .getDoubleNV();

                            if (Double.isNaN(tmpD))
                            {
                                instance[attribute.index()] = Instance
                                    .missingValue();
                            }
                            else
                            {
                                instance[attribute.index()] = tmpD;
                            }
                        }
                        else
                        {
                            throw new WekaException("Descriptor " +
                                attribute.name() + " is not a native value.");
                        }
                    }
                }

                attribute.index();
            }

            // add created molecule instance to molecule instances
            instances.add(new Instance(1, instance));
        }

        return instances;
    }

    public static Instances matrix2instances(double[][] matrix,
        String[] descriptors, int[] attributeTypes)
    {
        FastVector attributesV = new FastVector(descriptors.length);
        int molecules = matrix[0].length;

        for (int i = 0; i < descriptors.length; i++)
        {
            if (attributeTypes[i] == Attribute.NUMERIC)
            {
                // numeric
                attributesV.addElement(new Attribute(descriptors[i],
                        attributesV.size()));
            }
            else if (attributeTypes[i] == Attribute.NOMINAL)
            {
                // nominal
                // create a list with all nominal values
                Hashtable hashed = new Hashtable();

                for (int j = 0; j < molecules; j++)
                {
                    hashed.put(new Double(matrix[i][j]), "");
                }

                // store list of nominal values in the Weka data structure
                FastVector attributeValues = new FastVector(hashed.size());
                Double tmp;

                for (Enumeration e = hashed.keys(); e.hasMoreElements();)
                {
                    tmp = (Double) e.nextElement();
                    attributeValues.addElement(tmp.toString());

                    //System.out.println("NOMINAL " + tmp);
                }

                attributesV.addElement(new Attribute(descriptors[i],
                        attributeValues, attributesV.size()));
            }
        }

        int descriptorSize = attributesV.size();
        Attribute attribute = null;

        // create molecule instances
        Instances instances = new Instances("MatrixInstances", attributesV,
                attributesV.size());

        // iterate over all instances (to generate them)
        double[] instance;

        for (int i = 0; i < molecules; i++)
        {
            instance = new double[descriptorSize];

            for (int j = 0; j < descriptorSize; j++)
            {
                attribute = (Attribute) attributesV.elementAt(j);

                if (Double.isNaN(matrix[j][i]))
                {
                    instance[attribute.index()] = Instance.missingValue();
                }
                else
                {
                    if (attributeTypes[j] == Attribute.NUMERIC)
                    {
                        // numeric
                        instance[attribute.index()] = matrix[j][i];
                    }
                    else if (attributeTypes[j] == Attribute.NOMINAL)
                    {
                        // nominal
                        instance[attribute.index()] = attribute.indexOfValue(
                                Double.toString(matrix[j][i]));

                        if (instance[attribute.index()] == -1)
                        {
                            // invalid nominal value
                            logger.error("Invalid nominal value.");

                            return null;
                        }
                    }
                }

                attribute.index();
            }

            // add created molecule instance to molecule instances
            Instance inst = new Instance(1, instance);
            instances.add(inst);

            //System.out.println("instance (attr.:"+inst.numAttributes()+", vals:"+inst.numValues()+"): "+inst);
        }

        //System.out.println(instances.toString());
        return instances;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
