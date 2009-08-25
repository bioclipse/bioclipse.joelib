///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicNativeValueIterator.java,v $
//  Purpose:  Iterator for the standard Vector.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:42 $
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
package joelib2.util.iterator;

import joelib2.feature.NativeValue;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.PairData;

import java.util.Iterator;


/**
 *  Gets an iterator over native descriptor values (<tt>int</tt> or <tt>double</tt>)
 * of this molecule.
 *
 * <blockquote><pre>
 * NativeValueIterator nativeIt = mol.nativeValueIterator();
 * double value;
 * String descName;
 * while (nativeIt.hasNext())
 * {
 *   value = nativeIt.nextDouble();
 *   descName = nativeIt.actualName();
 *
 * }
 * </pre></blockquote>
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:42 $
 * @see joelib2.molecule.Molecule#nativeValueIterator()
 */
public class BasicNativeValueIterator implements Iterator, NativeValueIterator
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private PairDataIterator gdit;
    private Molecule mol;
    private PairData pairData;
    private boolean valueTaken;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the GenericDataIterator object
     *
     * @param v  Description of the Parameter
     * @param h  Description of the Parameter
     */
    public BasicNativeValueIterator(Molecule _mol, BasicPairDataIterator _gdit)
    {
        gdit = _gdit;
        mol = _mol;
        valueTaken = true;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public synchronized String actualName()
    {
        return pairData.getKey();
    }

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    public synchronized boolean hasNext()
    {
        if (valueTaken == false)
        {
            return true;
        }

        while (gdit.hasNext())
        {
            pairData = gdit.nextPairData();
            pairData = mol.getData(pairData.getKey(), true);
            pairData = (BasicPairData) pairData;

            if (pairData instanceof NativeValue)
            {
                valueTaken = false;

                return true;
            }
        }

        return false;
    }

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    public synchronized Object next()
    {
        if (valueTaken == false)
        {
            valueTaken = true;

            return pairData;
        }

        while (gdit.hasNext())
        {
            pairData = gdit.nextPairData();

            pairData = mol.getData(pairData.getKey(), true);
            pairData = (BasicPairData) pairData;

            if (pairData instanceof NativeValue)
            {
                valueTaken = true;

                return pairData;
            }
        }

        return null;
    }

    public synchronized double nextDouble()
    {
        return ((NativeValue) next()).getDoubleNV();
    }

    public synchronized int nextInt()
    {
        return ((NativeValue) next()).getIntNV();
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public synchronized NativeValue nextNativeValue()
    {
        return (NativeValue) next();
    }

    public synchronized String nextString()
    {
        return ((NativeValue) next()).getStringNV();
    }

    /**
     * Description of the Method
     */
    public synchronized void remove()
    {
        try
        {
            throw new NoSuchMethodException("Method not implemented.");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
