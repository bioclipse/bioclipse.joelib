///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: GhemicalInterface.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:41 $
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
package joelib2.util.ghemical;

import joelib2.feature.types.bondlabel.BondInAromaticSystem;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import java.io.File;

import org.apache.log4j.Category;


/**
 * This interface class to Ghemical defines some native methods for
 * accessing parts of the Ghemical functionality.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:41 $
 */
public class GhemicalInterface
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /** Obtain a suitable logger. */
    private static Category logger = Category.getInstance(
            "joelib2.util.ghemical.GhemicalInterface");
    private static GhemicalInterface ghemical = null;
    private static String ghemicalLibraryPath = "lib";
    private static String ghemicalLibraryName = "ghemical";
    private static boolean addOSNameFlag = true;

    //~ Constructors ///////////////////////////////////////////////////////////

    /** Don't let anyone instantiate this class */
    private GhemicalInterface()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Determines if the OS name (like 'windows' or 'linux') will be appended
     * automatically to the library path. Default value is 'true'.
     */
    public static void addOSNameToLibraryPath(boolean value)
    {
        addOSNameFlag = value;
    }

    //  public native double doMonteCarloSearch(int n_init_steps, int n_simul_steps, int optsteps);
    //  public native boolean doGeomOpt(boolean checkSteps, int maxSteps, boolean checkGrad, double minGrad, boolean checkDeltaE, double minDeltaE);

    /*-------------------------------------------------------------------------*
     * private static methods
     *-------------------------------------------------------------------------*/

    /** @todo maybe move this method to a more common class */
    public static String getOperationSystemName()
    {
        String osName = System.getProperty("os.name");

        // determine name of operation system and convert it into lower caps without blanks
        if (osName.indexOf("Windows") != -1)
        {
            osName = "windows";
        }
        else if (osName.indexOf("Linux") != -1)
        {
            osName = "linux";
        }

        return osName;
    }

    /**
     * Tries to create an instance of our interface class.
     */
    public static synchronized GhemicalInterface instance()
    {
        // check if we already have an instance of the OElib interface class
        if (ghemical != null)
        {
            return ghemical;
        }

        String libPath = null;

        try
        {
            // build the full path to the OElib interface dynamic library
            libPath = ghemicalLibraryPath +
                System.getProperty("file.separator", "/");

            if (addOSNameFlag)
            {
                libPath += (getOperationSystemName() +
                        System.getProperty("file.separator", "/"));
            }

            libPath += System.mapLibraryName(ghemicalLibraryName);

            // make sure that we have the absolute path
            libPath = new File(libPath).getAbsolutePath();
        }
        catch (Exception e)
        {
            logger.error(
                "Could not build the full path to the GhemicalInterface interface dynamic library.");
            logger.error(e.getMessage());
        }

        try
        {
            // try to load the OElib java natice interface dynamic library
            System.load(libPath);
        }
        catch (Error e)
        {
            logger.error(
                "Could not find the GhemicalInterface java native interface dynamic library in '" +
                libPath + "'.");
            logger.error(e.getMessage());
        }

        try
        {
            // try to create instance of the oelib interface
            ghemical = new GhemicalInterface();
        }
        catch (Exception e)
        {
            logger.error(
                "Could not create an instance of the Ghemical interface class.");
            logger.error(e.getMessage());
        }

        return ghemical;
    }

    /**
     * Sets the name of the OElib interface dynamic library. Note that the name can
     * not be changed anymore after the first call to the 'instance()' method.
     * The default library name is 'OE_jni', which should in almost all cases be ok.
     */
    public static void setLibraryName(String libraryName)
    {
        // set new library name if the OElib interface class instance was not already created
        if (ghemical == null)
        {
            ghemicalLibraryName = libraryName;
        }
        else
        {
            logger.error(
                "Library was already initialized. Library name can't be changed anymore.");
        }
    }

    /**
     * Sets the path to the OElib interface dynamic library. Note that the path can
     * not be changed anymore after the first call to the 'instance()' method.
     *  The default library path is 'lib'.
     */
    public static void setLibraryPath(String libraryPath)
    {
        // set new library path if the OElib interface class instance was not already created
        if (ghemical == null)
        {
            ghemicalLibraryPath = libraryPath;
        }
        else
        {
            logger.error(
                "Library was already initialized. Library path can't be changed anymore.");
        }
    }

    public native boolean addAtom(int atomID, float charge, double x, double y,
        double z);

    /**
     * Add ghemical bond.
     *
     * @param startBond
     * @param endBond
     * @param bondType 0=conjugated, 1=single, 2=double, 3=triple, 4=quadrupole
     * @return boolean
     */
    public native boolean addBond(int startBond, int endBond, int bondType);

    // DOES NOT WORK !?!?!?!?!?
    // JNI internal bug ????
    //  public native boolean createGhemicalModel(
    //          int atomId[],
    //          float charges[],
    //          int startBonds[],
    //          int endBonds[],
    //          int bondTypes[],
    //          double coords[]);
    public native boolean createGeometryOptimizer();

    public native boolean createGeometryOptimizer(int defaultSteps, float delta);

    public native boolean createGhemicalModel();

    public boolean createGhemicalModel(Molecule mol, boolean replaceZeroValues)
    {
        if (!createGhemicalModel())
        {
            return false;
        }

        int atoms = mol.getAtomsSize();
        Atom atom;
        boolean areXposZero = true;
        boolean areYposZero = true;
        boolean areZposZero = true;

        for (int i = 1; i <= atoms; i++)
        {
            atom = mol.getAtom(i);

            if (atom.get3Dx() != 0.0)
            {
                areXposZero = false;
            }

            if (atom.get3Dy() != 0.0)
            {
                areYposZero = false;
            }

            if (atom.get3Dz() != 0.0)
            {
                areZposZero = false;
            }
        }

        double x;
        double y;
        double z;

        for (int i = 1; i <= atoms; i++)
        {
            atom = mol.getAtom(i);
            x = atom.get3Dx();
            y = atom.get3Dy();
            z = atom.get3Dz();

            if (replaceZeroValues &&
                    (areXposZero || areYposZero || areZposZero))
            {
                if (areXposZero)
                {
                    x = (Math.random() * 2) - 1.0;
                }

                if (areYposZero)
                {
                    y = (Math.random() * 2) - 1.0;
                }

                if (areZposZero)
                {
                    z = (Math.random() * 2) - 1.0;
                }
            }

            if (!addAtom(atom.getAtomicNumber(), atom.getFormalCharge(), x, y,
                        z))
            {
                return false;
            }
        }

        int bonds = mol.getBondsSize();
        Bond bond;
        int type;

        for (int i = 0; i < bonds; i++)
        {
            bond = mol.getBond(i);

            // mmmh, is there a problem when representing aromatic bond as
            // conjugated ones
            // it seems to be better with single-double-single-double-...
            if (BondInAromaticSystem.isAromatic(bond))
            {
                type = 0;
            }
            else if (bond.isDouble())
            {
                type = 2;
            }
            else if (bond.isTriple())
            {
                type = 3;
            }
            else
            {
                type = 1;
            }

            //                  System.out.println(
            //                          "start:" + (bond.getBeginAtomIdx() - 1) + " end:" + (bond.getEndAtomIdx() - 1) + " type:" + type);
            if (!addBond(bond.getBeginIndex() - 1, bond.getEndIndex() - 1,
                        type))
            {
                return false;
            }
        }

        return true;
    }

    public boolean doGeometryOptimization(boolean print)
    {
        double tresholdDeltaE = 1.0e-14;
        double tresholdStep = 6.0e-11;
        int numSteps = 100;

        return doGeometryOptimization(numSteps, tresholdDeltaE, tresholdStep,
                print);
    }

    public boolean doGeometryOptimization(int numSteps, double tresholdDeltaE,
        double tresholdStep, boolean print)
    {
        double lastEnergy = Double.MAX_VALUE;
        double deltaE;
        int numberOfZeros = 0;

        for (int n1 = 0; n1 < numSteps; n1++)
        {
            if (!ghemical.takeCGStep())
            {
                return false;
            }

            boolean terminate = false;
            deltaE = lastEnergy - ghemical.getEnergy();

            if (deltaE == 0.0)
            {
                numberOfZeros++;

                if (numberOfZeros > 10)
                {
                    break;
                }
            }
            else
            {
                numberOfZeros = 0;
            }

            if ((n1 != 0) && ((deltaE) != 0.0) &&
                    (Math.abs(deltaE) < tresholdDeltaE))
            {
                terminate = true;
            }

            if ((ghemical.getStepLength() != 0.0) &&
                    (ghemical.getStepLength() < tresholdStep))
            {
                terminate = true;
            }

            if (terminate)
            {
                break;
            }

            if (print)
            {
                System.out.print("JAVA: step = " + (n1 + 1) + "   ");
                System.out.print("energy = " + ghemical.getEnergy() +
                    " kJ/mol  ");
                System.out.print("delta energy = " + Math.abs(deltaE) + "   ");
                System.out.println("step length = " + ghemical.getStepLength());
            }

            lastEnergy = ghemical.getEnergy();
        }

        return true;
    }

    public boolean doGeometryOptimization(Molecule mol, int numSteps,
        double tresholdDeltaE, double tresholdStep, boolean print)
    {
        if (!createGhemicalModel(mol, true))
        {
            return false;
        }

        if (!createGeometryOptimizer())
        {
            return false;
        }

        if (!doGeometryOptimization(numSteps, tresholdDeltaE, tresholdStep,
                    print))
        {
            return false;
        }

        int atoms = mol.getAtomsSize();
        Atom atom;
        double x;
        double y;
        double z;

        for (int i = 1; i <= atoms; i++)
        {
            atom = mol.getAtom(i);
            x = getGOAtomPosX(i - 1);
            y = getGOAtomPosY(i - 1);
            z = getGOAtomPosZ(i - 1);
            atom.setCoords3D(x, y, z);
        }

        return true;
    }

    // CAUSES TROUBLE ??? But the delete at the begin of the create...-methods work ???
    //  public native void deleteGeometryOptimizer();
    //  public native void deleteGhemicalModel();
    // methods to minimize the energy applying transformations on
    // rotatable bonds
    public native double doRandomSearch(int cycles, int optsteps);

    public native double doSystematicSearch(int divisions, int optsteps);

    public native double getEnergy();

    public native double getGOAtomPosX(int number);

    public native double getGOAtomPosY(int number);

    public native double getGOAtomPosZ(int number);

    public native double getStepLength();

    // atom methods
    public native int numberOfGOAtoms();

    public native void setGOAtomPosX(int number, double x);

    public native void setGOAtomPosY(int number, double y);

    public native void setGOAtomPosZ(int number, double z);

    public native boolean takeCGStep();

    public native boolean takeCGStep(int stepTypeNumber);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
