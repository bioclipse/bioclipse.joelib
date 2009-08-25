///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: NativeValueFilter.java,v $
//  Purpose:  Interface definition for calling external programs from JOELib.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
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
package joelib2.process.filter;

import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;
import joelib2.feature.NativeValue;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.PairData;

import org.apache.log4j.Category;


/**
 * Molecule process filter for native value descriptor entries.
 *
 * <p>
 * Example:
 * <blockquote><pre>
 * // accept all values with: Kier_shape_1<=5.0
 * NativeValueFilter filter =new NativeValueFilter(
 *                                "Kier_shape_1",
 *                                NativeValueFilter.SMALLER_EQUAL,
 *                                5.0);
 *
 * // create molecule
 * Molecule mol=new Molecule();
 * String smiles="c1cc(OH)cc1";
 * if (!JOESmilesParser.smiToMol(mol, smiles, setTitle.toString()));
 *
 * // should we accept this molecule, what do you mean ?
 * System.out.println("Accept: ("+filter.toString()+")="+filter.accept(mol));
 * </pre></blockquote>
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:38 $
 */
public class NativeValueFilter implements Filter
{
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final int SMALLER_EQUAL = 0;
    public static final int SMALLER = 1;
    public static final int EQUAL = 2;
    public static final int GREATER_EQUAL = 3;
    public static final int GREATER = 4;
    public static final int NOT_EQUAL = 5;
    private static final String[] allowedRules =
        new String[]{"<=", "<", "==", ">=", ">", "!="};

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.process.filter.NativeValueFilter");

    //~ Instance fields ////////////////////////////////////////////////////////

    private String attribute;

    private FilterInfo info;
    private int relation;
    private double value;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescriptorFilter object
     */
    public NativeValueFilter()
    {
    }

    /**
     *  Constructor for the DescriptorFilter object
     *
     * @param  descNamesURL  Description of the Parameter
     */
    public NativeValueFilter(String _attribute, int _relation, double _value)
    {
        init(_attribute, _relation, _value);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean accept(Molecule mol)
    {
        if (attribute == null)
        {
            logger.warn("No data attribute defined in " +
                this.getClass().getName() + ".");

            return false;
        }

        double tmpDbl = 0.0;

        // get parsed descriptor from molecule
        PairData pairData = mol.getData(attribute, true);

        // try to calculate if descriptor entry is not available
        if (pairData == null)
        {
            FeatureResult result;

            try
            {
                result = FeatureHelper.featureFrom(mol, attribute, false);
            }
            catch (FeatureException ex)
            {
                // don't accept if descriptor can not be calculated
                return false;
            }

            if (result instanceof NativeValue)
            {
                // get native descriptor value
                tmpDbl = ((NativeValue) result).getDoubleNV();
            }
            else
            {
                logger.warn("Descriptor '" + attribute +
                    "' must be a native descriptor value.");

                return false;
            }
        }

        // use descriptor in molecule
        else
        {
            //check for native value descriptors
            if (pairData instanceof NativeValue)
            {
                // get native descriptor value
                tmpDbl = ((NativeValue) pairData).getDoubleNV();
            }
            else
            {
                logger.warn("Descriptor '" + attribute +
                    "' must be a native descriptor value.");

                return false;
            }
        }

        boolean result = false;

        switch (relation)
        {
        case SMALLER:
            result = (tmpDbl < value);

            break;

        case SMALLER_EQUAL:
            result = (tmpDbl <= value);

            break;

        case EQUAL:
            result = (tmpDbl == value);

            break;

        case GREATER_EQUAL:
            result = (tmpDbl >= value);

            break;

        case GREATER:
            result = (tmpDbl > value);

            break;

        case NOT_EQUAL:
            result = (tmpDbl != value);

            break;
        }

        return result;
    }

    public boolean fromString(String rule)
    {
        int index;
        boolean parsed = false;

        for (int i = 0; i < allowedRules.length; i++)
        {
            if ((index = rule.indexOf(allowedRules[i])) != -1)
            {
                relation = i;

                //System.out.println("relation: "+allowedRules[i]);
                attribute = rule.substring(0, index);

                String tmp = rule.substring(index + allowedRules[i].length());

                try
                {
                    value = Double.parseDouble(tmp);
                }
                catch (NumberFormatException ex)
                {
                    logger.error("Invalid number: " + tmp);
                    logger.error("in rule: " + rule);

                    return false;
                }

                parsed = true;

                break;
            }
        }

        if (!parsed)
        {
            StringBuffer sb = new StringBuffer(30);
            sb.append("Allowed rules must contain: ");

            for (int i = 0; i < allowedRules.length; i++)
            {
                sb.append("'");
                sb.append(allowedRules[i]);
                sb.append("'");

                if (i < (allowedRules.length - 2))
                {
                    sb.append(", ");
                }

                if (i < (allowedRules.length - 1))
                {
                    sb.append(" or ");
                }
            }

            logger.error(sb.toString());
        }

        return true;
    }

    public String getAttribute()
    {
        return attribute;
    }

    /**
     *  Gets the processInfo attribute of the DescriptorFilter object
     *
     * @return    The processInfo value
     */
    public FilterInfo getFilterInfo()
    {
        return info;
    }

    /**
     *  Description of the Method
     *
     * @param  _descNames  Description of the Parameter
     */
    public void init(String _attribute, int _relation, double _value)
    {
        attribute = _attribute;
        relation = _relation;
        value = _value;
    }

    public void invertRelation()
    {
        switch (relation)
        {
        case SMALLER:
            relation = GREATER_EQUAL;

            break;

        case SMALLER_EQUAL:
            relation = GREATER;

            break;

        case EQUAL:
            relation = NOT_EQUAL;

            break;

        case GREATER_EQUAL:
            relation = SMALLER;

            break;

        case GREATER:
            relation = SMALLER_EQUAL;

            break;

        case NOT_EQUAL:
            relation = EQUAL;

            break;
        }
    }

    /**
     *  Sets the filterInfo attribute of the DescriptorFilter object
     *
     * @param  _info  The new filterInfo value
     */
    public void setFilterInfo(FilterInfo _info)
    {
        info = _info;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer(30);
        sb.append(attribute);
        sb.append(allowedRules[relation]);

        /*                switch (relation)
                        {
                                case SMALLER :
                                        sb.append("<");
                                        break;
                                case SMALLER_EQUAL :
                                        sb.append("<=");
                                        break;
                                case EQUAL :
                                        sb.append("==");
                                        break;
                                case GREATER_EQUAL :
                                        sb.append(">=");
                                        break;
                                case GREATER :
                                        sb.append(">");
                                        break;
                                case NOT_EQUAL :
                                        sb.append("!=");
                                        break;
                        }
        */
        sb.append(value);

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
