///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicPairDataIterator.java,v $
//  Purpose:  Iterator for the standard Vector.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
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

import joelib2.molecule.types.BasicPairData;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;


/**
 * Iterator over generic data elements.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:42 $
 */
public class BasicPairDataIterator implements Iterator, PairDataIterator
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private Enumeration hEnum;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the GenericDataIterator object
     *
     * @param v  Description of the Parameter
     * @param h  Description of the Parameter
     */
    public BasicPairDataIterator(Hashtable h)
    {
        hEnum = h.elements();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    public boolean hasNext()
    {
        if (hEnum.hasMoreElements())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    public Object next()
    {
        if (hEnum.hasMoreElements())
        {
            return hEnum.nextElement();
        }
        else
        {
            return null;
        }
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public BasicPairData nextPairData()
    {
        return (BasicPairData) next();
    }

    /**
     * Description of the Method
     */
    public void remove()
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
