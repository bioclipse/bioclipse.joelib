///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicMoleculeCacheHolder.java,v $
//  Purpose:  Holds all native value descriptors as double matrix for all known molecules.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
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
package joelib2.util;

import joelib2.feature.data.MoleculeCache;

import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Category;


/**
 * Molecule-Data matrix cache.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:41 $
 */
public final class BasicMoleculeCacheHolder implements MoleculeCacheHolder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicMoleculeCacheHolder.class.getName());
    private static BasicMoleculeCacheHolder instance;

    //~ Instance fields ////////////////////////////////////////////////////////

    private Map<String, MoleculeCache> mdMatrix;

    //~ Constructors ///////////////////////////////////////////////////////////

    private BasicMoleculeCacheHolder() throws Exception
    {
        mdMatrix = new Hashtable<String, MoleculeCache>(10);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static synchronized BasicMoleculeCacheHolder instance()
        throws Exception
    {
        if (instance == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Getting " +
                    BasicMoleculeCacheHolder.class.getName() + " instance.");
            }

            instance = new BasicMoleculeCacheHolder();
        }

        return instance;
    }

    public boolean contains(String _file)
    {
        return mdMatrix.containsKey(_file);
    }

    public MoleculeCache get(String _file)
    {
        return (MoleculeCache) mdMatrix.get(_file);
    }

    public Object put(String file, MoleculeCache obj)
    {
        return mdMatrix.put(file, obj);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
