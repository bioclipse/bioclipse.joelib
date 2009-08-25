///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: GlobalTopologicalChargeIndex.java,v $
//  Purpose:  Calculates a descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.11 $
//            $Date: 2005/02/17 16:48:31 $
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
package joelib2.feature.types;

import jmat.data.Matrix;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.BasicFeatureDescription;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.Feature;
import joelib2.feature.FeatureDescription;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;
import joelib2.feature.ResultFactory;

import joelib2.feature.result.APropDoubleResult;
import joelib2.feature.result.IntMatrixResult;

import joelib2.feature.types.atomlabel.AtomValence;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import joelib2.util.iterator.NbrAtomIterator;

import java.util.Map;

import org.apache.log4j.Category;


/**
 *  Calculates the Topological Charge Index.
 *
 * @.author     Gregor Wernet
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2005/02/17 16:48:31 $
 */
public class GlobalTopologicalChargeIndex implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.11 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(
            GlobalTopologicalChargeIndex.class.getName());
    public final static String ATOM_PROPERTY = "ATOM_PROPERTY";
    private final static BasicProperty[] ACCEPTED_PROPERTIES =
        new BasicProperty[]
        {
            new BasicProperty(ATOM_PROPERTY, "java.lang.String",
                "Atom property to use.", true, AtomValence.getName()),
        };
    private static final Class[] DEPENDENCIES =
        new Class[]{AtomValence.class, DistanceMatrix.class};

    //~ Instance fields ////////////////////////////////////////////////////////

    private BasicFeatureInfo descInfo;
    private String propertyName;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the GlobalTopologicalChargeIndex object
     */
    public GlobalTopologicalChargeIndex()
    {
        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.APropDoubleResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return GlobalTopologicalChargeIndex.class.getName();
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
        return ACCEPTED_PROPERTIES;
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
     * @param  initData                 Description of the Parameter
     * @param  descResult               Description of the Parameter
     * @param  molOriginal              Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  FeatureException  Description of the Exception
     */
    public FeatureResult calculate(Molecule molOriginal,
        FeatureResult descResult, Map properties) throws FeatureException
    {
        APropDoubleResult result = null;

        // check if the result type is correct
        if (!(descResult instanceof APropDoubleResult))
        {
            logger.error(descInfo.getName() + " result should be of type " +
                APropDoubleResult.class.getName() + " but it's of type " +
                descResult.getClass().toString());

            return null;
        }

        // initialize result type, if not already initialized
        else
        {
            result = (APropDoubleResult) descResult;
        }

        // check if the init type is correct
        if (!initialize(properties))
        {
            return null;
        }

        if (molOriginal.isEmpty())
        {
            result.value = Double.NaN;
            logger.warn("Empty molecule '" + molOriginal.getTitle() + "'. " +
                getName() + " was set to " + result.value);
            result.atomProperty = propertyName;

            return result;
        }

        Molecule mol = (Molecule) molOriginal.clone(true,
                new String[]{propertyName});
        mol.deleteHydrogens();

        // get atom properties or calculate if not already available
        FeatureResult tmpPropResult;
        tmpPropResult = FeatureHelper.instance().featureFrom(mol, propertyName);

        AtomProperties atomProperties;

        if (tmpPropResult instanceof AtomProperties)
        {
            atomProperties = (AtomProperties) tmpPropResult;
        }
        else
        {
            logger.error("Property '" + propertyName +
                "' must be an atom type to calculate the " + getName() + ".");

            return null;
        }

        FeatureResult tmpResult = null;

        try
        {
            tmpResult = FeatureHelper.instance().featureFrom(mol,
                    DistanceMatrix.getName());
        }
        catch (FeatureException ex)
        {
            logger.error(ex.toString());
            logger.error("Can not calculate distance matrix for " + getName() +
                ".");

            return null;
        }

        if (!(tmpResult instanceof IntMatrixResult))
        {
            logger.error("Needed descriptor '" + DistanceMatrix.getName() +
                "' should be of type " + IntMatrixResult.class.getName() +
                ". " + getName() + " can not be calculated.");

            return null;
        }

        IntMatrixResult distResult = (IntMatrixResult) tmpResult;
        int[][] distances = distResult.value;

        Matrix adjacent = new Matrix(mol.getAtomsSize(), mol.getAtomsSize());

        Atom atom1;
        Atom atom2;

        for (int i = 1; i <= mol.getAtomsSize(); i++)
        {
            atom1 = mol.getAtom(i);

            NbrAtomIterator nbit = atom1.nbrAtomIterator();

            while (nbit.hasNext())
            {
                atom2 = nbit.nextNbrAtom();
                adjacent.set(atom1.getIndex() - 1, atom2.getIndex() - 1, 1.0);
                adjacent.set(atom2.getIndex() - 1, atom1.getIndex() - 1, 1.0);
            }
        }

        double[][] d_dist = new double[distances.length][distances[0].length];
        double[][] d_rs_dist =
            new double[distances.length][distances[0].length];

        for (int i = 0; i < distances.length; i++)
        {
            for (int j = 0; j < distances[i].length; j++)
            {
                d_dist[i][j] = (double) distances[i][j];

                double x = (double) distances[i][j];

                if (x != 0.0)
                {
                    d_rs_dist[i][j] = 1 / (x * x);
                }
                else
                {
                    d_rs_dist[i][j] = 0.0;
                }
            }
        }

        Matrix distance = new Matrix(d_dist);
        Matrix rs_distance = new Matrix(d_rs_dist);

        //System.out.println("Die rs_distance-Matrix:");
        //System.out.print(rs_distance.toString());
        Matrix galvez = adjacent.times(rs_distance);

        //System.out.println("Die Galvez-Matrix:");
        //System.out.print(galvez.toString());
        //Atom atom3 = new Atom();
        Matrix charge_term = new Matrix(mol.getAtomsSize(), mol.getAtomsSize());

        for (int i = 0; i < mol.getAtomsSize(); i++)
        {
            for (int j = 0; j < mol.getAtomsSize(); j++)
            {
                charge_term.set(i, j, (galvez.get(i, j) - galvez.get(j, i)));

                if (i == j)
                {
                    //atom3 = mol.getAtom(i + 1);
                    charge_term.set(i, j, atomProperties.getDoubleValue(i + 1));

                    //charge_term.set(i,j,(double) atom3.getValence());
                }
            }
        }

        //System.out.println("Die charge_term-Matrix:");
        //System.out.print(charge_term.toString());
        double[] tci_gk = new double[mol.getAtomsSize()];

        for (int k = 0; k < mol.getAtomsSize(); k++)
        {
            for (int i = 0; i < mol.getAtomsSize(); i++)
            {
                for (int j = 0; j < mol.getAtomsSize(); j++)
                {
                    double abs_ij = charge_term.get(i, j);

                    if (abs_ij < 0.0)
                    {
                        abs_ij *= -1.0;
                    }

                    double delta = 0.0;

                    if (k == (int) distance.get(i, j))
                    {
                        delta = 1.0;
                    }

                    tci_gk[k] += (0.5 * (abs_ij * delta));

                    //          System.out.println("In G"+k+"(i="+i+",j="+j+"): "+ (0.5*(abs_ij * delta)));
                }
            }

            //System.out.println("G----->G"+k+": "+tci_gk[k]);
        }

        double[] tci_jk = new double[mol.getAtomsSize()];

        for (int i = 0; i < tci_jk.length; i++)
        {
            tci_jk[i] = tci_gk[i] / (mol.getAtomsSize() - 1);
        }

        double tci = 0.0;

        for (int i = 0; i < tci_jk.length; i++)
        {
            tci += tci_jk[i];
        }

        // save result
        result.value = tci;
        result.atomProperty = propertyName;

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
     *  Sets the descriptionFile attribute of the Descriptor object
     *
     *  Gets the description attribute of the Descriptor object
     *
     * @return            The description value
     * @return            The description value
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
        if (!PropertyHelper.checkProperties(this, properties))
        {
            logger.error(
                "Empty property definition or missing property entry.");

            return false;
        }

        String property = (String) PropertyHelper.getProperty(this,
                ATOM_PROPERTY, properties);

        if (property == null)
        {
            // should never happen
            propertyName = AtomValence.getName();
        }
        else
        {
            propertyName = property;
        }

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
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
