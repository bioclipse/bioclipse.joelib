///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: GraphPotentials.java,v $
//  Purpose:  Graph potentials.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.11 $
//            $Date: 2005/02/17 16:48:31 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
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
package joelib2.feature.types;

import jmat.data.Matrix;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.AbstractDoubleAtomProperty;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.FeatureHelper;

import joelib2.feature.types.atomlabel.AtomHybridisation;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.AtomIterator;

import org.apache.log4j.Category;


/**
 * External rotational symmetry or graph potentials.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2005/02/17 16:48:31 $
 * @.cite wy96
 */
public class GraphPotentials extends AbstractDoubleAtomProperty
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.11 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:31 $";
    private static Category logger = Category.getInstance(GraphPotentials.class
            .getName());
    private static final Class[] DEPENDENCIES =
        new Class[]{AtomHybridisation.class};

    //~ Constructors ///////////////////////////////////////////////////////////

    public GraphPotentials()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        descInfo = FeatureHelper.generateFeatureInfo(this.getClass(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, null,
                "joelib2.feature.result.AtomDoubleResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return GraphPotentials.class.getName();
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

    /**
     *  Calculate the Graph Potentials of a molecule based on V.E. Rozenblit, A.B.
     *  Golender Logical and Combinatorial Algorithms for Drug Design for an
     *  example see:<br>
     *  W.P. Walters, S. H. Yalkowsky, 'ESCHER-A Computer Program for the
     *  Determination of External Rotational Symmetry Numbers from Molecular
     *  Topology', J. Chem. Inf. Comput. Sci., 1996, 36(5), 1015-1017
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static double[] graphPotentials(Molecule mol)
    {
        Matrix g = gMatrix(mol);

        //    System.out.println("G-matrix"+MathHelper.matrixToString(g));
        Matrix inverseG = g.inverse();

        //    System.out.println("inverse G-matrix"+MathHelper.matrixToString(inverseG));
        Matrix c = cMatrix(mol);

        //    System.out.println("C-matrix"+MathHelper.matrixToString(c));
        Matrix h = inverseG.times(c);

        int nAtoms = mol.getAtomsSize();
        double[] graphPotentials = new double[nAtoms];

        for (int i = 0; i < nAtoms; i++)
        {
            graphPotentials[i] = h.get(i, 0);

            //      System.out.println("H("+i+"):"+h.get(i,0) );
        }

        return graphPotentials;
    }

    public double[] getDoubleAtomProperties(Molecule mol)
    {
        // get graph potentials for all atoms
        double[] grPot = graphPotentials(mol);

        return grPot;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }

    /**
     *  Construct the matrix C, which is simply a column vector consisting of the
     *  valence for each atom
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    private static Matrix cMatrix(Molecule mol)
    {
        Matrix c = new Matrix(mol.getAtomsSize(), 1);
        Atom atom;
        AtomIterator ait = mol.atomIterator();
        int cIndex = 0;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            c.set(cIndex, 0, atom.getValence());
            cIndex++;
        }

        //      ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);
        //      PrintWriter pw = new PrintWriter(baos);
        //      c.print(pw, 5,2);
        //      System.out.println("TEST"+baos.toString());
        return c;
    }

    /**
     *  Construct the matrix G, which puts each atoms valence+1 on the diagonal
     *  and and -1 on the off diagonal if two atoms are connected.
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    private static Matrix gMatrix(Molecule mol)
    {
        Atom atom1;
        Atom atom2;
        Matrix gMatrix = new Matrix(mol.getAtomsSize(), mol.getAtomsSize());
        AtomIterator ait1 = mol.atomIterator();
        AtomIterator ait2 = mol.atomIterator();
        int row = 0;
        int column;
        double value;

        while (ait1.hasNext())
        {
            atom1 = ait1.nextAtom();
            ait2.reset();
            column = 0;

            while (ait2.hasNext())
            {
                atom2 = ait2.nextAtom();

                if (atom1 == atom2)
                {
                    value = (atom1.getValence() + 1);
                    value += ((double) atom1.getAtomicNumber() / 10.0);
                    value += ((double) AtomHybridisation.getIntValue(atom1) /
                            100.0);
                    gMatrix.set(row, column, value);
                }
                else
                {
                    if (atom1.isConnected(atom2))
                    {
                        gMatrix.set(row, column, -1.0);
                    }
                    else
                    {
                        gMatrix.set(row, column, 0.0);
                    }
                }

                column++;
            }

            row++;
        }

        return gMatrix;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
