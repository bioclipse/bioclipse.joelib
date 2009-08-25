///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MoleculeCache.java,v $
//  Purpose:  Molecule caching interface for data mining classes.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Nikolas H. Fechner
//  Version:  $Revision: 1.9 $
//            $Date: 2005/02/17 16:48:30 $
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
package joelib2.feature.data;

import joelib2.io.IOType;

import joelib2.molecule.Molecule;

import joelib2.process.types.DescriptorStatistic;

import java.util.Hashtable;


/**
 * Molecule caching interface for data mining classes.
 *
 * @.author Nikolas H. Fechner
 *
 * @.license    GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:30 $
 */
public interface MoleculeCache
{
    //~ Methods ////////////////////////////////////////////////////////////////

    boolean calcVarianceNorm(DescriptorStatistic _statistic);

    MoleculeCache clone(MoleculeCache target);

    boolean existsMatrixFileFor(String fileName);

    boolean fromFileFor(String fileName);

    Hashtable getBinning(int _bins);

    Hashtable getBinning(int _bins, boolean forceCalculation);

    String[] getDescContainsNaN();

    double[] getDescFromMolByIdentifier(String _moleculeIdentifier);

    double[] getDescFromMolByIndex(int position);

    double[] getDescFromMolByName(String _moleculeName);

    String[] getDescNames();

    double[] getDescValues(String _descriptorName);

    double[][] getDescValues(String[] _descriptorNames);

    double[][] getDescValues(String[] _descriptorNames, int[] ifMolID,
        int[] ifNotMolID);

    double[][] getMatrix();

    String[] getMolNames();

    DescriptorStatistic getStatistic();

    boolean loadMatrix(IOType _inType, String _inFile) throws Exception;

    boolean loadMatrix(IOType _inType, String _inFile, boolean useCaching)
        throws Exception;

    int numberOfDescriptors();

    int numberOfMolecules();

    boolean setMoleculeDescriptors(Molecule mol, int moleculeEntry);

    void setMolIdentifier(String _moleculeIdentifier);

    String toString();

    void writeMatrixFileFor(String _inFile);

    // BAD !!! Do never use this !!!
    // Only historical for autocorrelation and RDF !!!
    //public double[] getDescFromMolByIdentifier(String _moleculeIdentifier, boolean replaceNaN);
    //public double[] getDescFromMolByIndex(int position, boolean replaceNaN);
    //public double[] getDescFromMolByName(String _moleculeName, boolean replaceNaN);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
