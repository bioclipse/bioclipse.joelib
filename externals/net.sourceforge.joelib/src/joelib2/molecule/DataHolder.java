///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: DataHolder.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.5
//Created:  Jan 15, 2005
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.10 $
//          $Date: 2005/02/17 16:48:36 $
//          $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.molecule;

import joelib2.molecule.types.PairData;

import joelib2.util.iterator.BasicPairDataIterator;

import java.util.List;


/**
 * TODO description.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion $Revision: 1.10 $, $Date: 2005/02/17 16:48:36 $
 */
public interface DataHolder
{
    //~ Methods ////////////////////////////////////////////////////////////////

    void addData(PairData d, boolean overwrite);

    /**
     * Description of the Method
     */
    void clear();

    /**
     * Description of the Method
     *
     * @param s  Description of the Parameter
     * @return   Description of the Return Value
     */
    boolean deleteData(String s);

    /**
     * Description of the Method
     *
     * @param gd  Description of the Parameter
     */
    void deleteData(PairData gd);

    /**
     * Description of the Method
     *
     * @param vg  Description of the Parameter
     */
    void deleteData(List vg);

    /**
     *  Gets the data attribute of the <tt>Molecule</tt> object
     *
     * @return   The data value
     */
    BasicPairDataIterator genericDataIterator();

    /**
     * Gets the data attribute of the GenericDataHolder object
     *
     * @param s      Description of the Parameter
     * @param parse  Description of the Parameter
     * @return       <tt>null</tt> if this data don't exist
     */
    PairData getData(String s, boolean parse);

    /**
     * Description of the Method
     *
     * @param s  Description of the Parameter
     * @return   Description of the Return Value
     */
    boolean hasData(String s);

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    int size();
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
