///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: TestInterface.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
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

import org.apache.log4j.Category;


/**
 * Small self-running class used to test the native methods of the 'oelib'
 * interface.
 *
 * @.author wegnerj
 * @.license GPL
 * @.cvsversion $Revision: 1.8 $, $Date: 2005/02/17 16:48:41 $
 */
public class TestInterface
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Category logger = Category.getInstance(TestInterface.class
            .getName());

    private static final String delimiter =
        "-------------------------------------------------------";

    //~ Instance fields ////////////////////////////////////////////////////////

    private GhemicalInterface ghemical;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Gets an instance of the oelib JNI interface class. Before using this
     * constructor, you should set the library location to it with the method
     * 'OEInterface.setLibrary'.
     *
     * @see OEInterface.setLibrary
     */
    public TestInterface()
    {
        // get instance of oelib JNI interface class
        ghemical = GhemicalInterface.instance();
    }

    public TestInterface(GhemicalInterface ghemical)
    {
        this.ghemical = ghemical;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        // create instance
        GhemicalInterface.setLibraryPath("lib");

        GhemicalInterface ghemical = GhemicalInterface.instance();
        TestInterface test = new TestInterface(ghemical);

        // perform some test
        test.performTests(true);
    }

    public boolean complexInit(boolean print)
    {
        //      int atomId[] = new int[6];
        //      float charges[] = new float[6];
        //      atomId[0] = 6;
        //      charges[0] = 0.0f;
        //      atomId[1] = 6;
        //      charges[1] = 0.0f;
        //      atomId[2] = 6;
        //      charges[2] = 0.0f;
        //      atomId[3] = 6;
        //      charges[3] = 0.0f;
        //      atomId[4] = 6;
        //      charges[4] = 0.0f;
        //      atomId[5] = 6;
        //      charges[5] = 0.0f;
        //
        //      int startBonds[] = new int[6];
        //      int endBonds[] = new int[6];
        //      int bondTypes[] = new int[6];
        //      startBonds[0] = 0;
        //      endBonds[0] = 1;
        //      bondTypes[0] = 1;
        //      startBonds[1] = 1;
        //      endBonds[1] = 2;
        //      bondTypes[1] = 1;
        //      startBonds[2] = 2;
        //      endBonds[2] = 3;
        //      bondTypes[2] = 1;
        //      startBonds[3] = 3;
        //      endBonds[3] = 4;
        //      bondTypes[3] = 1;
        //      startBonds[4] = 4;
        //      endBonds[4] = 5;
        //      bondTypes[4] = 1;
        //      startBonds[5] = 5;
        //      endBonds[5] = 0;
        //      bondTypes[5] = 1;
        //
        //      double coords[] = new double[atomId.length * 3];
        //      coords[0] = 7.7267;
        //      coords[1] = -8.1193;
        //      coords[2] = 1.0;
        //      coords[3] = 9.3480;
        //      coords[4] = -5.8647;
        //      coords[5] = 2.0;
        //      coords[6] = 11.4380;
        //      coords[7] = -7.2833;
        //      coords[8] = 3.0;
        //      coords[9] = 14.5667;
        //      coords[10] = -6.5740;
        //      coords[11] = 3.0;
        //      coords[12] = 10.7667;
        //      coords[13] = -9.4240;
        //      coords[14] = 2.0;
        //      coords[15] = 10.6527;
        //      coords[16] = -11.3620;
        //      coords[17] = 1.0;
        //
        //      if (!ghemical
        //              .createGhemicalModel(
        //                      atomId,
        //                      charges,
        //                      startBonds,
        //                      endBonds,
        //                      bondTypes,
        //                      coords))
        //              return false;
        return true;
    }

    public boolean doRandomSearch(boolean print)
    {
        if (ghemical.createGhemicalModel())
        {
            if (ghemical.addAtom(6, 0.0f, 7.7267, -8.1193, 1.0))
            {
                if (ghemical.addAtom(6, 0.0f, 9.3480, -5.8647, 2.0))
                {
                    if (ghemical.addAtom(6, 0.0f, 11.4380, -7.2833, 3.0))
                    {
                        if (ghemical.addAtom(6, 0.0f, 14.5667, -6.5740, 3.0))
                        {
                            if (ghemical.addAtom(6, 0.0f, 10.7667, -9.4240,
                                        2.0))
                            {
                                if (ghemical.addAtom(6, 0.0f, 10.6527, -11.3620,
                                            1.0))
                                {
                                    if (ghemical.addBond(0, 1, 1))
                                    {
                                        if (ghemical.addBond(1, 2, 1))
                                        {
                                            if (ghemical.addBond(2, 3, 1))
                                            {
                                                if (ghemical.addBond(3, 4, 1))
                                                {
                                                    if (ghemical.addBond(4, 5,
                                                                1))
                                                    {
                                                        if (!ghemical
                                                                .createGeometryOptimizer())
                                                        {
                                                            return false;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Minimum energy (Random search): " +
            ghemical.doRandomSearch(50, 100));

        //ghemical.deleteGeometryOptimizer();
        //ghemical.deleteGhemicalModel();
        return true;
    }

    public boolean doSystematicSearch(boolean print)
    {
        if (ghemical.createGhemicalModel())
        {
            if (ghemical.addAtom(6, 0.0f, 7.7267, -8.1193, 1.0))
            {
                if (ghemical.addAtom(6, 0.0f, 9.3480, -5.8647, 2.0))
                {
                    if (ghemical.addAtom(6, 0.0f, 11.4380, -7.2833, 3.0))
                    {
                        if (ghemical.addAtom(6, 0.0f, 14.5667, -6.5740, 3.0))
                        {
                            if (ghemical.addAtom(6, 0.0f, 10.7667, -9.4240,
                                        2.0))
                            {
                                if (ghemical.addAtom(6, 0.0f, 10.6527, -11.3620,
                                            1.0))
                                {
                                    if (ghemical.addBond(0, 1, 1))
                                    {
                                        if (ghemical.addBond(1, 2, 1))
                                        {
                                            if (ghemical.addBond(2, 3, 1))
                                            {
                                                if (ghemical.addBond(3, 4, 1))
                                                {
                                                    if (ghemical.addBond(4, 5,
                                                                1))
                                                    {
                                                        if (!ghemical
                                                                .createGeometryOptimizer())
                                                        {
                                                            return false;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Minimum energy (Systematic search): " +
            ghemical.doSystematicSearch(5, 50));

        //ghemical.deleteGeometryOptimizer();
        //ghemical.deleteGhemicalModel();
        return true;
    }

    public boolean geomOptimizer(boolean print)
    {
        if (ghemical.createGhemicalModel())
        {
            if (ghemical.addAtom(6, 0.0f, 7.7267, -8.1193, 1.0))
            {
                if (ghemical.addAtom(6, 0.0f, 9.3480, -5.8647, 2.0))
                {
                    if (ghemical.addAtom(6, 0.0f, 11.4380, -7.2833, 3.0))
                    {
                        if (ghemical.addAtom(6, 0.0f, 14.5667, -6.5740, 3.0))
                        {
                            if (ghemical.addAtom(6, 0.0f, 10.7667, -9.4240,
                                        2.0))
                            {
                                if (ghemical.addAtom(6, 0.0f, 10.6527, -11.3620,
                                            1.0))
                                {
                                    if (ghemical.addBond(0, 1, 1))
                                    {
                                        if (ghemical.addBond(1, 2, 1))
                                        {
                                            if (ghemical.addBond(2, 3, 1))
                                            {
                                                if (ghemical.addBond(3, 4, 1))
                                                {
                                                    if (ghemical.addBond(4, 5,
                                                                1))
                                                    {
                                                        if (ghemical.addBond(5,
                                                                    0, 1))
                                                        {
                                                            if (!ghemical
                                                                    .createGeometryOptimizer(
                                                                        100,
                                                                        (float)
                                                                        0.025))
                                                            {
                                                                return false;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        double lastEnergy = Double.MAX_VALUE;
        double tresholdDeltaE = 1.0e-14;
        double tresholdStep = 6.0e-11;
        double deltaE;

        for (int n1 = 0; n1 < 200; n1++)
        {
            if (!ghemical.takeCGStep())
            {
                return false;
            }

            boolean terminate = false;
            deltaE = lastEnergy - ghemical.getEnergy();

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

        //ghemical.deleteGeometryOptimizer();
        //ghemical.deleteGhemicalModel();
        return true;
    }

    public boolean getSetAtomPositions(boolean print)
    {
        if (ghemical.createGhemicalModel())
        {
            if (ghemical.addAtom(6, 0.0f, 7.7267, -8.1193, 1.0))
            {
                if (ghemical.addAtom(6, 0.0f, 9.3480, -5.8647, 2.0))
                {
                    if (ghemical.addAtom(6, 0.0f, 11.4380, -7.2833, 3.0))
                    {
                        if (ghemical.addAtom(6, 0.0f, 14.5667, -6.5740, 3.0))
                        {
                            if (ghemical.addAtom(6, 0.0f, 10.7667, -9.4240,
                                        2.0))
                            {
                                if (ghemical.addAtom(6, 0.0f, 10.6527, -11.3620,
                                            1.0))
                                {
                                    if (ghemical.addBond(0, 1, 1))
                                    {
                                        if (ghemical.addBond(1, 2, 1))
                                        {
                                            if (ghemical.addBond(2, 3, 1))
                                            {
                                                if (ghemical.addBond(3, 4, 1))
                                                {
                                                    if (ghemical.addBond(4, 5,
                                                                1))
                                                    {
                                                        if (ghemical.addBond(5,
                                                                    0, 1))
                                                        {
                                                            if (!ghemical
                                                                    .createGeometryOptimizer(
                                                                        100,
                                                                        (float)
                                                                        0.025))
                                                            {
                                                                return false;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        int numAtoms = ghemical.numberOfGOAtoms();
        double[] posX = new double[numAtoms];
        double[] posY = new double[numAtoms];
        double[] posZ = new double[numAtoms];

        for (int n1 = 0; n1 < 5; n1++)
        {
            for (int i = 0; i < numAtoms; i++)
            {
                if (n1 == 0)
                {
                    posX[i] = ghemical.getGOAtomPosX(i);
                }

                if (n1 == 0)
                {
                    posY[i] = ghemical.getGOAtomPosY(i);
                }

                if (n1 == 0)
                {
                    posZ[i] = ghemical.getGOAtomPosZ(i);
                }

                System.out.println("atom " + i + "--> x:" +
                    ghemical.getGOAtomPosX(i) + " y:" +
                    ghemical.getGOAtomPosY(i) + " z:" +
                    ghemical.getGOAtomPosZ(i));
            }

            if (n1 == 1)
            {
                System.out.println("Set atoms to start position:");

                for (int i = 0; i < numAtoms; i++)
                {
                    ghemical.setGOAtomPosX(i, posX[i]);
                    ghemical.setGOAtomPosY(i, posY[i]);
                    ghemical.setGOAtomPosZ(i, posZ[i]);
                    System.out.println("atom " + i + "--> x:" +
                        ghemical.getGOAtomPosX(i) + " y:" +
                        ghemical.getGOAtomPosY(i) + " z:" +
                        ghemical.getGOAtomPosZ(i));
                }
            }

            if (!ghemical.takeCGStep())
            {
                return false;
            }

            if (print)
            {
                System.out.print("JAVA: step = " + (n1 + 1) + "   ");
                System.out.print("energy = " + ghemical.getEnergy() +
                    " kJ/mol  ");
                System.out.println("step length = " + ghemical.getStepLength());
            }
        }

        //ghemical.deleteGeometryOptimizer();
        //ghemical.deleteGhemicalModel();
        return true;
    }

    //  public boolean doMonteCarloSearch(boolean print)
    //  {
    //          if (!ghemical.createGhemicalModel())
    //                                  return false;
    //
    //          if (!ghemical.addAtom(6,0.0f,7.7267, -8.1193, 1.0))
    //                                  return false;
    //          if (!ghemical.addAtom(6,0.0f,9.3480, -5.8647, 2.0))
    //                                  return false;
    //          if (!ghemical.addAtom(6,0.0f,11.4380, -7.2833, 3.0))
    //                                  return false;
    //          if (!ghemical.addAtom(6,0.0f,14.5667, -6.5740, 3.0))
    //                                  return false;
    //          if (!ghemical.addAtom(6,0.0f,10.7667, -9.4240, 2.0))
    //                                  return false;
    //          if (!ghemical.addAtom(6,0.0f,10.6527, -11.3620, 1.0))
    //                                  return false;
    //          if (!ghemical.addBond(0,1,1))
    //                                  return false;
    //          if (!ghemical.addBond(1,2,1))
    //                                  return false;
    //          if (!ghemical.addBond(2,3,1))
    //                                  return false;
    //          if (!ghemical.addBond(3,4,1))
    //                                  return false;
    //          if (!ghemical.addBond(4,5,1))
    //                                  return false;
    //
    //          if (!ghemical.createGeometryOptimizer())
    //                  return false;
    //
    //          System.out.println("Minimum energy (Monte carlo search):
    // "+ghemical.doMonteCarloSearch(5,5,50));
    //
    //          //ghemical.deleteGeometryOptimizer();
    //          //ghemical.deleteGhemicalModel();
    //
    //          return true;
    //  }
    //  public boolean doGeomOpt(boolean print)
    //  {
    //          if (!ghemical.createGhemicalModel())
    //                                  return false;
    //
    //          if (!ghemical.addAtom(6,0.0f,7.7267, -8.1193, 1.0))
    //                                  return false;
    //          if (!ghemical.addAtom(6,0.0f,9.3480, -5.8647, 2.0))
    //                                  return false;
    //          if (!ghemical.addAtom(6,0.0f,11.4380, -7.2833, 3.0))
    //                                  return false;
    //          if (!ghemical.addAtom(6,0.0f,14.5667, -6.5740, 3.0))
    //                                  return false;
    //          if (!ghemical.addAtom(6,0.0f,10.7667, -9.4240, 2.0))
    //                                  return false;
    //          if (!ghemical.addAtom(6,0.0f,10.6527, -11.3620, 1.0))
    //                                  return false;
    //          if (!ghemical.addBond(0,1,1))
    //                                  return false;
    //          if (!ghemical.addBond(1,2,1))
    //                                  return false;
    //          if (!ghemical.addBond(2,3,1))
    //                                  return false;
    //          if (!ghemical.addBond(3,4,1))
    //                                  return false;
    //          if (!ghemical.addBond(4,5,1))
    //                                  return false;
    //          if (!ghemical.addBond(5,0,1))
    //                                  return false;
    //
    //          if (!ghemical.createGeometryOptimizer())
    //                  return false;
    //
    //          if (!ghemical.doGeomOpt(true, 200, true,1e-12, true,1e-25))
    //                                  return false;
    //
    //          //ghemical.deleteGeometryOptimizer();
    //          //ghemical.deleteGhemicalModel();
    //
    //          return true;
    //  }

    /**
     * Performs a number of test methodName calls to determine if everything is
     * ok.
     */
    public boolean performTests(boolean print)
    {
        // check oelib interface class
        boolean success = (ghemical != null);

        // call test methods
        if (success)
        {
            success = geomOptimizer(print);
        }

        //              for (int i = 0; i < 100000; i++)
        //              {
        //                      if (success)
        //                              success = geomOptimizer(false);
        //              }
        if (print)
        {
            System.out.println(delimiter);
        }

        if (success)
        {
            success = getSetAtomPositions(print);
        }

        if (print)
        {
            System.out.println(delimiter);
        }

        if (success)
        {
            success = doRandomSearch(print);
        }

        if (print)
        {
            System.out.println(delimiter);
        }

        if (success)
        {
            success = doSystematicSearch(print);
        }

        //              DOES NOT WORK !!!
        //              if (success)
        //                      success = doGeomOpt(print);
        //              if (success)
        //                      success = doMonteCarloSearch(print);
        if (success && print)
        {
            logger.info("ok, everything seemed to work fine.");
        }

        return success;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
