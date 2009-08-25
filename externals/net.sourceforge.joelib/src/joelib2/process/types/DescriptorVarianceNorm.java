///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DescriptorVarianceNorm.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:38 $
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
package joelib2.process.types;

import joelib2.feature.NativeValue;

import joelib2.io.BasicIOType;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.PairData;

import joelib2.process.BasicProcess;
import joelib2.process.MoleculeProcessException;

import joelib2.util.BasicProperty;

import joelib2.util.iterator.PairDataIterator;

import wsi.ra.tool.ArrayStatistic;
import wsi.ra.tool.BasicPropertyHolder;
import wsi.ra.tool.BasicResourceLoader;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;


/**
 * Scales the values in one descriptor so that they have similar magnitudes.
 *
 * TeX: $x_i^n$ = \frac{x_i-\overline{x}}{\sigma _i}
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:38 $
 */
public class DescriptorVarianceNorm extends BasicProcess
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            DescriptorVarianceNorm.class.getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private List desc2ignore;

    //  private final static  JOEProperty[]  ACCEPTED_PROPERTIES    = new JOEProperty[]{
    //      new JOEProperty("NUMBER_OF_BINS", "java.lang.Integer", "Number of bins to create.", true),
    //      };
    private DescriptorStatistic statistic;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescSelectionWriter object
     */
    public DescriptorVarianceNorm()
    {
        statistic = new DescriptorStatistic();
        clear();

        // load descriptors which should be ignored
        String value;

        if ((value = BasicPropertyHolder.instance().getProperty(this,
                            "descriptors2ignore")) != null)
        {
            List tmpVec = BasicResourceLoader.readLines(value);

            if (tmpVec == null)
            {
                logger.error("File with descriptor names to ignore not found.");
            }

            desc2ignore = tmpVec;
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean clear()
    {
        if (statistic == null)
        {
            return false;
        }

        return true;
    }

    public List descriptors2ignore()
    {
        return desc2ignore;
    }

    /**
     *  Description of the Method
     *
     * @param  _statistic  Description of the Parameter
     */
    public void init(DescriptorStatistic _statistic)
    {
        statistic = _statistic;
    }

    /**
     *  Description of the Method
     *
     * @param  inType         Description of the Parameter
     * @param  inStream       Description of the Parameter
     * @param  _numberOfBins  Description of the Parameter
     * @exception  Exception  Description of the Exception
     */
    public void init(BasicIOType inType, String inFile) throws Exception
    {
        logger.info(
            "Creating statistical data for descriptor variance normalisation.");
        statistic = DescriptorStatistic.getDescStatistic(inType, inFile);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public BasicProperty[] neededProperties()
    {
        //    return ACCEPTED_PROPERTIES;
        return null;
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  properties               Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  MoleculeProcessException  Description of the Exception
     */
    public boolean process(Molecule mol, Map properties)
        throws MoleculeProcessException
    {
        if (statistic == null)
        {
            return false;
        }

        try
        {
            super.process(mol, properties);
        }
        catch (MoleculeProcessException e)
        {
            throw new MoleculeProcessException("Properties for " +
                this.getClass().getName() + " not correct.");
        }

        PairData pairData;
        PairDataIterator gdit = mol.genericDataIterator();
        ArrayStatistic arrayStat;
        String descriptor;
        double value = 0.0;
        double newValue;
        boolean ignoreDesc = false;

        while (gdit.hasNext())
        {
            pairData = gdit.nextPairData();
            descriptor = pairData.getKey();

            // ignore descriptors in list
            if (desc2ignore != null)
            {
                ignoreDesc = false;

                for (int i = 0; i < desc2ignore.size(); i++)
                {
                    if (descriptor.equals((String) desc2ignore.get(i)))
                    {
                        ignoreDesc = true;

                        break;
                    }
                }

                if (ignoreDesc)
                {
                    continue;
                }
            }

            // parse data, if possible
            pairData = mol.getData(descriptor, true);

            // check descriptor statistic entry
            arrayStat = statistic.getDescriptorStatistic(descriptor);

            if (arrayStat == null)
            {
                logger.error("Statistic for " + descriptor + " don't exist");

                return false;
            }

            if (pairData instanceof NativeValue)
            {
                value = ((NativeValue) pairData).getDoubleNV();
                newValue = arrayStat.varianceNormalization(value);

                // to avoid precision loss all normalized values will now be internally
                // stored as double values.
                ((NativeValue) pairData).setDoubleNV(newValue);
            }
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String toString()
    {
        if (statistic == null)
        {
            return null;
        }

        //    StringBuffer    sb            = new StringBuffer(100);
        //    return sb.toString();
        return null;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
