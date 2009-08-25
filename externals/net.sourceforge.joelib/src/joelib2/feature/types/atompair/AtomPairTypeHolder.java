///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AtomPairTypeHolder.java,v $
//  Purpose:  Atom pair descriptor.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:32 $
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
package joelib2.feature.types.atompair;

import joelib2.molecule.Atom;

import joelib2.molecule.types.AtomProperties;

import java.util.Hashtable;


/**
 * Atom type singleton class to cache the types (depends on atom properties used).
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:32 $
 */
public class AtomPairTypeHolder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static AtomPairTypeHolder instance;

    //~ Instance fields ////////////////////////////////////////////////////////

    private Hashtable types = new Hashtable();

    //~ Constructors ///////////////////////////////////////////////////////////

    private AtomPairTypeHolder()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public static synchronized AtomPairTypeHolder instance()
    {
        if (instance == null)
        {
            instance = new AtomPairTypeHolder();
        }

        return instance;
    }

    public AtomPairAtomType addReturn(AtomPairAtomType type)
    {
        if (type == null)
        {
            return null;
        }

        if (types.containsKey(type))
        {
            return (AtomPairAtomType) types.get(type);
        }
        else
        {
            types.put(type, type);
        }

        return type;
    }

    public AtomPairAtomType getAPType(AtomProperties[] nominal,
        AtomProperties[] numeric, Atom atom)
    {
        String[] nominalVals = null;

        if (nominal != null)
        {
            nominalVals = new String[nominal.length];

            for (int i = 0; i < nominal.length; i++)
            {
                nominalVals[i] = nominal[i].getStringValue(atom.getIndex());
            }
        }

        double[] numericVals = null;

        if (numeric != null)
        {
            numericVals = new double[numeric.length];

            for (int i = 0; i < numeric.length; i++)
            {
                numericVals[i] = numeric[i].getDoubleValue(atom.getIndex());
            }
        }

        AtomPairAtomType type = new AtomPairAtomType(atom.getAtomicNumber(),
                nominalVals, numericVals);

        if (types.containsKey(type))
        {
            return (AtomPairAtomType) types.get(type);
        }
        else
        {
            types.put(type, type);
        }

        return type;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
