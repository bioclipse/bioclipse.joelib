///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomPropertyMatrix.java,v $
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
///////////////////////////////////////////////////////////////////////////////
package joelib2.process.types;

import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;

import joelib2.feature.result.IntMatrixResult;

import joelib2.feature.types.DistanceMatrix;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;

import joelib2.process.MoleculeProcess;
import joelib2.process.MoleculeProcessException;
import joelib2.process.ProcessInfo;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 *  Calling processor classes if the filter rule fits.
 *
 * @.author     wegnerj
 */
public class AtomPropertyMatrix implements MoleculeProcess
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            AtomPropertyMatrix.class.getName());
    private final static List DEFAULT_APROP = FeatureHelper.instance()
                                                           .getAtomLabelFeatures();
    public static final String ATOM_PROPERTY_MATRIX = "Atom_property_matrix";

    /**
     *  Description of the Field
     */
    public final static String ATOM_PROPERTIES = "ATOM_PROPERTIES";
    public final static String SPHERES2CALCULATE = "SPHERES2CALCULATE";
    public final static String INCREMENTAL_SPHERES = "INCREMENTAL_SPHERES";
    private final static BasicProperty[] ACCEPTED_PROPERTIES =
        new BasicProperty[]
        {
            new BasicProperty(ATOM_PROPERTIES, "java.util.Vector",
                "Atom properties to use.", true, DEFAULT_APROP),
            new BasicProperty(INCREMENTAL_SPHERES, "java.lang.Boolean",
                "Use incremental sphere building.", true, Boolean.TRUE),
            new BasicProperty(SPHERES2CALCULATE, "java.lang.Integer",
                "Number of spheres to calculate (default=0).", true,
                new Integer(0)),
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    private ProcessInfo info;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescSelectionWriter object
     */
    public AtomPropertyMatrix()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public BasicProperty[] acceptedProperties()
    {
        return ACCEPTED_PROPERTIES;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public boolean clear()
    {
        return true;
    }

    /**
     *  Gets the processInfo attribute of the ProcessPipe object
     *
     * @return    The processInfo value
     */
    public ProcessInfo getProcessInfo()
    {
        return info;
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  properties               Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  MoleculeProcessException  Description of the Exception
     */
    public boolean process(Molecule molOriginal, Map properties)
        throws MoleculeProcessException
    {
        // check properties
        if (!PropertyHelper.checkProperties(this, properties))
        {
            logger.error(
                "Empty property definition for process or missing property entry.");

            return false;
        }

        Vector props = (Vector) PropertyHelper.getProperty(this,
                ATOM_PROPERTIES, properties);

        int spheres =
            ((Integer) PropertyHelper.getProperty(this, SPHERES2CALCULATE,
                    properties)).intValue();

        boolean incremental =
            ((Boolean) PropertyHelper.getProperty(this, INCREMENTAL_SPHERES,
                    properties)).booleanValue();

        List propertyNames;

        if (props == null)
        {
            // should never happen
            propertyNames = DEFAULT_APROP;
        }
        else
        {
            propertyNames = props;
        }

        Molecule mol = (Molecule) molOriginal.clone();
        mol.deleteHydrogens();

        // get topological distance matrix
        FeatureResult tmpResult = null;
        String distanceMatrixKey = DistanceMatrix.getName();

        try
        {
            tmpResult = FeatureHelper.instance().featureFrom(mol,
                    distanceMatrixKey);
        }
        catch (FeatureException ex)
        {
            logger.error(ex.toString());
            logger.error("Can not calculate " + distanceMatrixKey + " for " +
                this.getClass().getName() + ".");

            return false;
        }

        if (!(tmpResult instanceof IntMatrixResult))
        {
            logger.error("Needed descriptor '" + distanceMatrixKey +
                "' should be of type " + IntMatrixResult.class.getName() +
                ". " + this.getClass().getName() + " can not be calculated.");

            return false;
        }

        IntMatrixResult distResult = (IntMatrixResult) tmpResult;
        int[][] distances = distResult.value;

        // build atom property matrix
        int atoms = mol.getAtomsSize();
        double[][] matrix =
            new double[atoms][(propertyNames.size()) * (spheres + 1)];
        FeatureResult tmpPropResult;
        AtomProperties atomProperties;
        double[] aProps = new double[atoms];

        //System.out.println("atoms: "+mol.numAtoms());
        for (int ii = 0; ii < propertyNames.size(); ii++)
        {
            // get atom properties or calculate if not already available
            try
            {
                tmpPropResult = FeatureHelper.instance().featureFrom(mol,
                        (String) propertyNames.get(ii));

                //System.out.println("GET:::"+propertyNames.get(ii)+":::"+tmpPropResult);
            }
            catch (FeatureException ex)
            {
                logger.error(ex.toString());

                return false;
            }

            if (tmpPropResult instanceof AtomProperties)
            {
                atomProperties = (AtomProperties) tmpPropResult;
            }
            else
            {
                logger.error("Property '" + propertyNames.get(ii).toString() +
                    "' must be an atom type.");

                return false;
            }

            // store atom properties in an array
            for (int i = 0; i < atoms; i++)
            {
                aProps[i] = atomProperties.getDoubleValue(i + 1);
            }

            // calculate atom property matrix
            if (spheres == 0)
            {
                // just getting atom properties for every atom
                for (int i = 0; i < atoms; i++)
                {
                    matrix[i][ii] = aProps[i];
                }
            }
            else
            {
                // calculate spheres of mean atom properties
                // sphere: 0
                int iis = (ii * (spheres + 1));

                for (int i = 0; i < atoms; i++)
                {
                    matrix[i][iis] = aProps[i];
                }

                //                          System.out.print(" "+iis);
                // sphere: 1-spheres
                double v;
                int c;
                int s = -1;

                if (incremental)
                {
                    s = 1;
                }
                else
                {
                    s = spheres;
                }

                for (; s <= spheres; s++)
                {
                    iis = (ii * (spheres + 1)) + s;

                    //                                  System.out.print(" "+iis);
                    for (int i = 0; i < atoms; i++)
                    {
                        c = 0;
                        v = 0.0;

                        for (int j = 0; j < atoms; j++)
                        {
                            if (s == distances[i][j])
                            {
                                c++;
                                v += aProps[j];
                            }
                        }

                        if (c == 0)
                        {
                            //matrix[i][iis]=0.0;
                            logger.warn("Sphere (topol. dist.) " + s +
                                " does not exist for atom " + (i + 1) + " in " +
                                mol.getTitle());
                            logger.warn("Reduce " + SPHERES2CALCULATE + " to " +
                                i + ".");

                            return false;
                        }
                        else
                        {
                            matrix[i][iis] = v / ((double) c);
                        }
                    }
                }

                //                          System.out.println("");
            }
        }

        //if(properties==null)properties=new Hashtable();
        properties.put(ATOM_PROPERTY_MATRIX, matrix);

        return true;
    }

    /**
     *  Sets the processInfo attribute of the ProcessPipe object
     *
     * @param  _info  The new processInfo value
     */
    public void setProcessInfo(ProcessInfo _info)
    {
        info = _info;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
