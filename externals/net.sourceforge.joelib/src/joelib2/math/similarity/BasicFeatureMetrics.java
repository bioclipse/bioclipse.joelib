///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicFeatureMetrics.java,v $
//  Purpose:  Holds all native value descriptors as double matrix for all known molecules.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:35 $
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
package joelib2.math.similarity;

import joelib2.feature.BitVectorValue;
import joelib2.feature.DistanceMetricValue;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;
import joelib2.feature.NativeValue;

import joelib2.io.BasicIOType;
import joelib2.io.BasicReader;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;

import joelib2.util.BasicBitVector;
import joelib2.util.BitVector;

import java.io.FileInputStream;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 *  Example for converting molecules.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:35 $
 */
public class BasicFeatureMetrics implements FeatureMetrics
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicFeatureMetrics.class.getName());
    private final static int NOT_DEFINED = -1;
    private final static int BINARY_COMPARISON = 0;
    private final static int EUKLIDIAN_COMPARISON = 1;
    private final static int DIST_METRIC_COMPARISON = 2;

    //~ Instance fields ////////////////////////////////////////////////////////

    private int comparisonType = NOT_DEFINED;

    private List<BitVector> descBinaryValue;
    private String[] descriptors;
    private List<double[]> descValues;
    private List<DistanceMetricValue> distMetricValue;
    private List<Molecule> targetMols;
    private double[] tmpValues;

    //~ Constructors ///////////////////////////////////////////////////////////

    public BasicFeatureMetrics()
    {
    }

    /**
     *  Constructor for the ComparisonHelper object
     *
     * @param  targetMolecule  Description of the Parameter
     */
    public BasicFeatureMetrics(Molecule targetMolecule)
    {
        int initialSize = 1;
        targetMols = new Vector<Molecule>(initialSize);
        descValues = new Vector<double[]>(initialSize);
        descBinaryValue = new Vector<BitVector>(initialSize);

        targetMols.add(targetMolecule);
    }

    public BasicFeatureMetrics(BasicIOType inType, String _targetFile)
    {
        this(inType, _targetFile, 10);
    }

    /**
     *  Constructor for the ComparisonHelper object
     *
     * @param  _targetFile  Description of the Parameter
     * @param  inType       Description of the Parameter
     */
    public BasicFeatureMetrics(BasicIOType inType, String _targetFile,
        int initialSize)
    {
        //targetFile = _targetFile;
        BasicReader reader = null;

        try
        {
            reader = new BasicReader(new FileInputStream(_targetFile), inType);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            //            throw ex;
        }

        targetMols = new Vector<Molecule>(initialSize);
        descValues = new Vector<double[]>(initialSize);
        descBinaryValue = new Vector<BitVector>(initialSize);

        Molecule targetMol = null;

        for (;;)
        {
            targetMol = new BasicConformerMolecule(inType, inType);

            //load only the first molecule !
            try
            {
                if (!reader.readNext(targetMol))
                {
                    //        targetMols = null;
                    break;
                }

                targetMols.add(targetMol);
            }
            catch (Exception ex)
            {
                targetMol = null;
                ex.printStackTrace();

                break;

                //            throw ex;
            }
        }

        reader.close();
        reader = null;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    public double[] compare(Molecule mol)
    {
        return compare(mol, null, null);
    }

    public double compare(Molecule source, Molecule target)
    {
        // get the actual descriptor values
        BitVector bitsetSource = null;
        double[] descSource = new double[tmpValues.length];
        DistanceMetricValue dMetricValueSource = null;
        BitVector bitsetTarget = null;
        double[] descTarget = new double[tmpValues.length];
        DistanceMetricValue dMetricValueTarget = null;

        switch (comparisonType)
        {
        case BINARY_COMPARISON:
            bitsetSource = getBitset(source);

            if (bitsetSource == null)
            {
                return Double.NaN;
            }

            bitsetTarget = getBitset(target);

            if (bitsetTarget == null)
            {
                return Double.NaN;
            }

            break;

        case EUKLIDIAN_COMPARISON:
            descSource = getDescriptors(source, descSource);

            if (tmpValues == null)
            {
                return Double.NaN;
            }

            descTarget = getDescriptors(target, descTarget);

            if (tmpValues == null)
            {
                return Double.NaN;
            }

            break;

        case DIST_METRIC_COMPARISON:
            dMetricValueSource = getDistMetricValue(source);

            if (dMetricValueSource == null)
            {
                return Double.NaN;
            }

            dMetricValueTarget = getDistMetricValue(target);

            if (dMetricValueTarget == null)
            {
                return Double.NaN;
            }

            break;

        default:
            logger.error(
                "Descriptor value(s) does not contain valid comparison values (or are not initialized).");

            return Double.NaN;
        }

        // calculate distance values for all target patterns
        switch (comparisonType)
        {
        case BINARY_COMPARISON:
            return distance(bitsetSource, bitsetSource);

        case EUKLIDIAN_COMPARISON:
            return distance(descSource, descSource);

        case DIST_METRIC_COMPARISON:
            return distance(dMetricValueSource, dMetricValueTarget);
        }

        return Double.NaN;
    }

    /**
     *  Description of the Method. <code>Double.NaN</code> is returned if no
     *  comparison value could be calulated
     *
     * @param  mol              Description of the Parameter
     * @param  _doubleDescName  Stores the distance value at the double value
     *      descriptor with this name.
     * @return                  double distance value
     */
    public double[] compare(Molecule mol, String _distResultName,
        double[] distances)
    {
        // get the actual descriptor values
        int size = targetMols.size();

        if (distances == null)
        {
            distances = new double[size];
        }

        BitVector bitset = null;
        DistanceMetricValue dMetricValue = null;

        switch (comparisonType)
        {
        case BINARY_COMPARISON:
            bitset = getBitset(mol);

            if (bitset == null)
            {
                return null;
            }

            break;

        case EUKLIDIAN_COMPARISON:
            tmpValues = getDescriptors(mol, tmpValues);

            if (tmpValues == null)
            {
                return null;
            }

            break;

        case DIST_METRIC_COMPARISON:
            dMetricValue = getDistMetricValue(mol);

            if (dMetricValue == null)
            {
                return null;
            }

            break;

        default:
            logger.error(
                "Descriptor value(s) does not contain valid comparison values (or are not initialized).");

            return null;
        }

        // calculate distance values for all target patterns
        for (int ii = 0; ii < size; ii++)
        {
            switch (comparisonType)
            {
            case BINARY_COMPARISON:
                distances[ii] = distance(bitset, ii);

                break;

            case EUKLIDIAN_COMPARISON:
                distances[ii] = distance(tmpValues, ii);

                break;

            case DIST_METRIC_COMPARISON:
                distances[ii] = distance(dMetricValue, ii);

                break;
            }

            // add distance result to molecule
            // store as String value !!!!
            // if you like another representation you must define
            // a descriptor result for this case !!!
            if (_distResultName != null)
            {
                BasicPairData dp = new BasicPairData();
                dp.setKey(_distResultName + "_" + ii);
                dp.setKeyValue(Double.toString(distances[ii]));
                mol.addData(dp);
            }
        }

        return distances;
    }

    public final List getTargetMols()
    {
        return targetMols;
    }

    /**
     *  Sets the comparisonDescriptor attribute of the ComparisonHelper object
     *
     * @param  _descriptor  The new comparisonDescriptor value
     * @return              Description of the Return Value
     */
    public boolean setComparisonDescriptor(String _descriptor)
    {
        if (_descriptor == null)
        {
            logger.error("No descriptors for comparison defined.");

            return false;
        }

        if ((targetMols == null) || (targetMols.size() == 0))
        {
            logger.error("No target molecule available.");

            return false;
        }

        descriptors = new String[1];
        descriptors[0] = _descriptor;
        tmpValues = new double[1];

        FeatureResult result = null;

        int size = targetMols.size();
        Molecule targetMol;
        double[] tmpVal;
        BitVector tmpBit;
        DistanceMetricValue tmpDistMetricValue;

        for (int i = 0; i < size; i++)
        {
            targetMol = (Molecule) targetMols.get(i);

            //      System.out.println("targetMol("+i+"):"+targetMol);
            try
            {
                result = FeatureHelper.featureFrom(targetMol, _descriptor,
                        true);
            }
            catch (FeatureException ex)
            {
                ex.printStackTrace();

                return false;
            }

            if (result == null)
            {
                logger.error("Descriptor '" + _descriptor +
                    "' not found in target molecule " + targetMol.getTitle() +
                    ".");

                return false;
            }

            if (result instanceof NativeValue)
            {
                tmpVal = new double[1];
                tmpVal[0] = ((NativeValue) result).getDoubleNV();
                descValues.add(tmpVal);
                comparisonType = EUKLIDIAN_COMPARISON;
            }
            else if (result instanceof BitVectorValue)
            {
                tmpBit = ((BitVectorValue) result).getBinaryValue();

                //      System.out.println("add BIT:"+tmpBit.toString());
                descBinaryValue.add(tmpBit);
                comparisonType = BINARY_COMPARISON;
            }
            else if (result instanceof DistanceMetricValue)
            {
                tmpDistMetricValue = (DistanceMetricValue) result;
                distMetricValue.add(tmpDistMetricValue);
                comparisonType = DIST_METRIC_COMPARISON;
            }
            else
            {
                logger.error("Descriptor '" + _descriptor +
                    "' must be a double, a 'bit set' value or allow a distance metric.");

                return false;
            }
        }

        return true;
    }

    /**
     *  Sets the comparisonDescriptors attribute of the ComparisonHelper object
     *
     * @param  _descriptors  The new comparisonDescriptors value
     * @return               Description of the Return Value
     */
    public boolean setComparisonDescriptors(String[] _descriptors)
    {
        if (_descriptors == null)
        {
            logger.error("No descriptors for comparison defined.");

            return false;
        }

        if (_descriptors.length == 0)
        {
            logger.error("Descriptors for comparison seems to be empty.");

            return false;
        }

        if ((targetMols == null) || (targetMols.size() == 0))
        {
            logger.error("No target molecule available.");

            return false;
        }

        if (_descriptors.length == 1)
        {
            return setComparisonDescriptor(_descriptors[0]);
        }

        descriptors = _descriptors;

        int size = _descriptors.length;
        tmpValues = new double[size];

        //        descriptors= new String[size];
        int sizeMols = targetMols.size();
        Molecule targetMol;
        double[] tmpVal;
        double value;

        for (int ii = 0; ii < sizeMols; ii++)
        {
            targetMol = (Molecule) targetMols.get(ii);
            tmpVal = new double[size];

            for (int i = 0; i < size; i++)
            {
                //          descriptors[i]=_descriptors[i];
                try
                {
                    value = getDoubleDesc(targetMol, descriptors[i]);
                }
                catch (FeatureException ex)
                {
                    logger.error(ex.toString());
                    logger.error("Can't load double value of descriptor '" +
                        descriptors[i] + "' in target molecule.");

                    return false;
                }

                tmpVal[i] = value;
            }

            descValues.add(tmpVal);
        }

        comparisonType = EUKLIDIAN_COMPARISON;

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    private final double distance(final BitVector bitset, int index)
    {
        if (bitset == null)
        {
            return Double.NaN;
        }

        BasicBitVector target = (BasicBitVector) descBinaryValue.get(index);

        return bitset.tanimoto(target);
    }

    private final double distance(final BitVector source,
        final BitVector target)
    {
        if ((source == null) || (target == null))
        {
            return Double.NaN;
        }

        return source.tanimoto(target);
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    private final double distance(final DistanceMetricValue dMetricValue,
        int index)
    {
        if (dMetricValue == null)
        {
            return Double.NaN;
        }

        DistanceMetricValue targetValue = (DistanceMetricValue) distMetricValue
            .get(index);

        return dMetricValue.getDistance(targetValue);
    }

    private final double distance(final DistanceMetricValue source,
        final DistanceMetricValue target)
    {
        if ((source == null) || (target == null))
        {
            return Double.NaN;
        }

        return source.getDistance(target);
    }

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    private final double distance(final double[] ds, int index)
    {
        double[] tmpVal = (double[]) descValues.get(index);

        return distance(ds, tmpVal);
    }

    private final double distance(final double[] source, final double[] target)
    {
        if ((source == null) || (target == null))
        {
            return Double.NaN;
        }

        int s = source.length;
        double val = 0.0;
        double sum = 0.0;

        for (int i = 0; i < s; i++)
        {
            val = target[i] - source[i];
            sum += (val * val);
        }

        return Math.sqrt(sum);
    }

    private final BitVector getBitset(final Molecule mol)
    {
        FeatureResult result = null;

        try
        {
            result = FeatureHelper.featureFrom(mol, descriptors[0], true);
        }
        catch (FeatureException ex)
        {
            ex.printStackTrace();

            return null;
        }

        if (result == null)
        {
            logger.error("Descriptor '" + descriptors[0] +
                "' not found in comparison molecule " + mol.getTitle() + ".");

            return null;
        }

        BitVector bitset = null;

        if (result instanceof BitVectorValue)
        {
            bitset = ((BitVectorValue) result).getBinaryValue();
        }
        else
        {
            logger.error("Descriptor '" + descriptors[0] +
                "' must be a 'bit set' value.");

            return null;
        }

        return bitset;
    }

    private final double[] getDescriptors(final Molecule mol, double[] vals)
    {
        FeatureResult result = null;
        int size = vals.length;

        for (int i = 0; i < size; i++)
        {
            try
            {
                result = FeatureHelper.featureFrom(mol, descriptors[i], true);
            }
            catch (FeatureException ex)
            {
                logger.error(ex.toString());
                vals[i] = Double.NaN;
            }

            if (result == null)
            {
                logger.error("Descriptor '" + descriptors[i] +
                    "' not found in comparison molecule " + mol.getTitle() +
                    ".");
                vals[i] = Double.NaN;
            }

            vals[i] = ((NativeValue) result).getDoubleNV();

            if (!(result instanceof NativeValue))
            {
                logger.error("Descriptor '" + descriptors[i] +
                    "' must be a native value.");
                vals[i] = Double.NaN;
            }
        }

        return vals;
    }

    private final DistanceMetricValue getDistMetricValue(final Molecule mol)
    {
        FeatureResult result = null;

        try
        {
            result = FeatureHelper.featureFrom(mol, descriptors[0], true);
        }
        catch (FeatureException ex)
        {
            ex.printStackTrace();

            return null;
        }

        if (result == null)
        {
            logger.error("Descriptor '" + descriptors[0] +
                "' not found in comparison molecule " + mol.getTitle() + ".");

            return null;
        }

        DistanceMetricValue dMetricValue = null;

        if (result instanceof DistanceMetricValue)
        {
            dMetricValue = ((DistanceMetricValue) result);
        }
        else
        {
            logger.error("Descriptor '" + descriptors[0] +
                "' must be a 'distance metric value' value.");

            return null;
        }

        return dMetricValue;
    }

    /**
     *  Gets the doubleDesc attribute of the ComparisonHelper object
     *
     * @param  mol                      Description of the Parameter
     * @param  _descName                Description of the Parameter
     * @return                          The doubleDesc value
     * @exception  FeatureException  Description of the Exception
     */
    private final double getDoubleDesc(final Molecule mol, String _descName)
        throws FeatureException
    {
        double value = Double.NaN;
        FeatureResult result = null;

        //        try
        //        {
        result = FeatureHelper.featureFrom(mol, _descName, true);

        //        }
        //        catch (DescriptorException ex)
        //        {
        //            throw ex;
        //        }
        if (result instanceof NativeValue)
        {
            value = ((NativeValue) result).getDoubleNV();
        }

        return value;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
