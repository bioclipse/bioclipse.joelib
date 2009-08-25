///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicTieResolver.java,v $
//  Purpose:  Interface for resolving renumbering ties.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.algo.morgan.types;

import joelib2.algo.morgan.SingleTieResolver;
import joelib2.algo.morgan.TieResolver;

import joelib2.molecule.Molecule;


/**
 * Interface for resolving renumbering ties.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:29 $
 */
public class BasicTieResolver implements TieResolver
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final SingleTieResolver[] resolvers =
        new SingleTieResolver[]
        {
            new SingleTieResolverBO(), new SingleTieResolverElement(),
            new SingleTieResolverPartCh()
        };

    //~ Methods ////////////////////////////////////////////////////////////////

    public int getTieResolverHashcode()
    {
        return this.getClass().getName().hashCode();
    }

    public final SingleTieResolver[] getTieResolvers()
    {
        return resolvers;
    }

    public boolean init(Molecule mol)
    {
        boolean allFine = true;

        for (int i = 0; i < resolvers.length; i++)
        {
            resolvers[i].init(mol);
        }

        return allFine;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
