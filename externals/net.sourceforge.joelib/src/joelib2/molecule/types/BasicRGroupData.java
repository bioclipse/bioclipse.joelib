///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicRGroupData.java,v $
//  Purpose:  Rgroup data.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
//            $Date: 2005/02/17 16:48:37 $
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
package joelib2.molecule.types;

import joelib2.util.types.BasicIntInt;

import java.util.List;
import java.util.Vector;


/**
 * Stores Rgroup data.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:37 $
 */
public class BasicRGroupData extends BasicPairData implements RGroupData
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    protected List<BasicIntInt> rGroups = new Vector<BasicIntInt>();

    //~ Constructors ///////////////////////////////////////////////////////////

    public BasicRGroupData()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static String getName()
    {
        return BasicRGroupData.class.getName();
    }

    /**
     * Add new atom-Rgroup entry.
     * {@link BasicIntInt} contains in the first value the atom index and in the
     * second value the rgroup number.
     *
     * @param  r  Description of the Parameter
     */
    public void add(BasicIntInt r)
    {
        rGroups.add(r);
    }

    /**
     * Gets the Rgroup informations.
     * {@link BasicIntInt} contains in the first value the atom index and in the
     * second value the rgroup number.
     */
    public List<BasicIntInt> getRGroups()
    {
        return rGroups;
    }

    /**
     * Set all atom-Rgroup entries.
     * {@link BasicIntInt} contains in the first value the atom index and in the
     * second value the rgroup number.
     */
    public void setRGroups(List<BasicIntInt> rGroups)
    {
        this.rGroups = rGroups;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
