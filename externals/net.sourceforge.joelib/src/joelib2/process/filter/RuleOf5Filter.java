///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: RuleOf5Filter.java,v $
//  Purpose:  Filter to avoid molecules with a 'poor absorption or permeability'.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
//            $Date: 2005/02/24 16:58:58 $
//            $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
///////////////////////////////////////////////////////////////////////////////
package joelib2.process.filter;

import joelib2.feature.Feature;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureFactory;
import joelib2.feature.FeatureResult;

import joelib2.feature.result.DoubleResult;
import joelib2.feature.result.IntResult;

import joelib2.feature.types.MolecularWeight;

import joelib2.molecule.Molecule;

import org.apache.log4j.Category;


/**
 * Filter to avoid molecules with a 'poor absorption or permeability'.
 * This is only a 'soft' filter and can be used to get an idea about lead/drug-likeness.
 *
 * @.author     wegnerj
 * @.wikipedia Lipinski's Rule of Five
 * @.wikipedia ADME
 * @.wikipedia Drug
 * @.wikipedia QSAR
 * @.wikipedia Data mining
 * @.license    GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/24 16:58:58 $
 * @.cite lldf01
 * @.cite odtl01
 * @see joelib2.feature.types.HBD1
 * @see joelib2.feature.types.HBD2
 * @see joelib2.feature.types.HBA1
 * @see joelib2.feature.types.HBD2
 * @see joelib2.feature.types.AtomInDonor
 * @see joelib2.feature.types.AtomInAcceptor
 * @see joelib2.feature.types.AtomInDonAcc
 */
public class RuleOf5Filter implements Filter
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.process.filter.RuleOf5Filter");

    //~ Instance fields ////////////////////////////////////////////////////////

    private FilterInfo info;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescriptorFilter object
     */
    public RuleOf5Filter()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Returns only <tt>true</tt> if less than two of the four rules fullfills the rules.
     * Otherwise a 'poor absorption or permeability' is possible and <tt>false</tt> will be returned.
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     * @.cite lldf01
     */
    public boolean accept(Molecule mol)
    {
        //    if (false)
        //    {
        //      logger.warn("" + this.getClass().getName() + ".");
        //      return false;
        //    }
        // gets lipinski parameters
        double mw = MolecularWeight.getMolecularWeight(mol);

        //double logP = getLogP(mol, "XlogP");
        double logP = getLogP(mol, "LogP");
        int hba = getNumberHBA(mol, "HBA1");
        int hbd = getNumberHBD(mol, "HBD1");

        // check lipinski parameters
        boolean fitsRuleOf5 = false;
        int numberOfBreakedRules = 0;

        if (mw >= 500)
        {
            numberOfBreakedRules++;
        }

        if (logP >= 5)
        {
            numberOfBreakedRules++;
        }

        if (hba >= 10)
        {
            numberOfBreakedRules++;
        }

        if (hbd >= 5)
        {
            numberOfBreakedRules++;
        }

        // decide lipinski criteria
        if (numberOfBreakedRules < 2)
        {
            // all fine, no molecule with a
            // 'poor absorption or permeability'-problem
            fitsRuleOf5 = true;
        }

        return fitsRuleOf5;
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
     *  Sets the filterInfo attribute of the DescriptorFilter object
     *
     * @param  _info  The new filterInfo value
     */
    public void setFilterInfo(FilterInfo _info)
    {
        info = _info;
    }

    private double getLogP(Molecule mol, String name)
    {
        // call external logP calculation program

        /*      External ext =null;
              boolean success=false;
              Hashtable props = new Hashtable();
              try
              {
                ext= ExternalFactory.instance().getExternal(extName);
                success=ext.process(mol, props);
              }
              catch (ExternalException ex)
              {
                ex.printStackTrace();
                return Double.NaN;
              }
              catch (JOEProcessException ex)
              {
                ex.printStackTrace();
                return Double.NaN;
              }

              Double value=(Double)props.get("XLOGP");
              return value.doubleValue();
        */
        Feature logP = null;
        FeatureResult logPResult = null;

        try
        {
            logP = FeatureFactory.getFeature(name);

            if (logP == null)
            {
                logger.error("Descriptor " + logP + " can't be loaded.");

                return -1;
            }

            logP.clear();
            logPResult = logP.calculate(mol);

            // has something weird happen
            if (logPResult == null)
            {
                return -1;
            }
        }
        catch (FeatureException ex)
        {
            logger.error(ex.toString());

            return -1;
        }

        return ((DoubleResult) logPResult).value;
    }

    private int getNumberHBA(Molecule mol, String hbaName)
    {
        Feature hba = null;
        FeatureResult hbaResult = null;

        try
        {
            hba = FeatureFactory.getFeature(hbaName);

            if (hba == null)
            {
                logger.error("Descriptor " + hba + " can't be loaded.");

                return -1;
            }

            hba.clear();
            hbaResult = hba.calculate(mol);

            // has something weird happen
            if (hbaResult == null)
            {
                return -1;
            }
        }
        catch (FeatureException ex)
        {
            logger.error(ex.toString());

            return -1;
        }

        return ((IntResult) hbaResult).getInt();
    }

    private int getNumberHBD(Molecule mol, String hbdName)
    {
        Feature hbd = null;
        FeatureResult hbdResult = null;

        try
        {
            hbd = FeatureFactory.getFeature(hbdName);

            if (hbd == null)
            {
                logger.error("Descriptor " + hbd + " can't be loaded.");

                return -1;
            }

            hbd.clear();
            hbdResult = hbd.calculate(mol);

            // has something weird happen
            if (hbdResult == null)
            {
                return -1;
            }
        }
        catch (FeatureException ex)
        {
            logger.error(ex.toString());

            return -1;
        }

        return ((IntResult) hbdResult).getInt();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
