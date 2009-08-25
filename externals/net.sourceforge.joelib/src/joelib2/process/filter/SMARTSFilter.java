///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SMARTSFilter.java,v $
//  Purpose:  Interface definition for calling external programs from JOELib.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
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

import joelib2.molecule.Molecule;

import joelib2.smarts.BasicSMARTSPatternMatcher;
import joelib2.smarts.SMARTSPatternMatcher;

import java.util.List;

import org.apache.log4j.Category;


/**
 * Molecule process filter for SMARTS patterns.
 *
 * <p>
 * Example:
 * <blockquote><pre>
 * // accept all molecules, where the molecule contains ONE NC=O group connected to an aromatic atom (aNC=O)
 * SMARTSFilter filter =new SMARTSFilter(
 *                                "aNC=O",
 *                                NativeValueFilter.EQUAL,
 *                                1);
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
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:38 $
 * @.cite smarts
 * @see joelib2.smarts.SMARTSPatternMatcher
 */
public class SMARTSFilter implements Filter
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
            "joelib2.process.filter.SMARTSFilter");

    //~ Instance fields ////////////////////////////////////////////////////////

    private FilterInfo info;
    private int relation;
    private SMARTSPatternMatcher smarts;
    private boolean unique;
    private int value;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescriptorFilter object
     */
    public SMARTSFilter()
    {
    }

    /**
     *  Constructor for the DescriptorFilter object
     *
     * @param  descNamesURL  Description of the Parameter
     */
    public SMARTSFilter(String _smarts, int _relation, int _value,
        boolean _unique)
    {
        init(_smarts, _relation, _value, _unique);
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
        if (smarts == null)
        {
            logger.warn("No SMARTS pattern defined in " +
                this.getClass().getName() + ".");

            return false;
        }

        smarts.match(mol);

        List matchList;

        if (unique)
        {
            matchList = smarts.getMatchesUnique();
        }
        else
        {
            matchList = smarts.getMatches();
        }

        int matches = matchList.size();

        boolean result = false;

        switch (relation)
        {
        case SMALLER:
            result = (matches < value);

            break;

        case SMALLER_EQUAL:
            result = (matches <= value);

            break;

        case EQUAL:
            result = (matches == value);

            break;

        case GREATER_EQUAL:
            result = (matches >= value);

            break;

        case GREATER:
            result = (matches > value);

            break;

        case NOT_EQUAL:
            result = (matches != value);

            break;
        }

        return result;
    }

    public boolean fromString(String rule)
    {
        return fromString(rule, true);
    }

    public boolean fromString(String rule, boolean _unique)
    {
        unique = _unique;

        int index;
        boolean parsed = false;

        for (int i = 0; i < allowedRules.length; i++)
        {
            if ((index = rule.lastIndexOf(allowedRules[i])) != -1)
            {
                relation = i;

                //System.out.println("relation: "+allowedRules[i]);
                smarts = new BasicSMARTSPatternMatcher();

                if (!smarts.init(rule.substring(0, index)))
                {
                    logger.error("Invalid SMARTS pattern: " +
                        rule.substring(0, index));

                    return false;
                }

                String tmp = rule.substring(index + allowedRules[i].length());

                try
                {
                    value = Integer.parseInt(tmp);
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
    public boolean init(String smarts, int relation, int value, boolean unique)
    {
        this.smarts = new BasicSMARTSPatternMatcher();

        if (!this.smarts.init(smarts))
        {
            logger.error("Invalid SMARTS pattern: " + smarts);

            return false;
        }

        this.relation = relation;
        this.value = value;
        this.unique = unique;

        return true;
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
        sb.append(smarts.getSmarts());
        sb.append(allowedRules[relation]);
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
