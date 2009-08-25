/**
 *  Filename: $RCSfile: BasicResidueIterator.java,v $
 *  Purpose:  Atom representation.
 *  Language: Java
 *  Compiler: JDK 1.4
 *  Authors:  Joerg Kurt Wegner
 *  Version:  $Revision: 1.3 $
 *            $Date: 2005/02/17 16:48:42 $
 *            $Author: wegner $
 *
 *  Copyright (c) Dept. Computer Architecture, University of Tuebingen, Germany
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */
package joelib2.util.iterator;

import joelib2.molecule.types.BasicResidue;

import java.util.List;


/**
 * Gets an iterator over all residue informations.
 *
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:42 $
 */
public class BasicResidueIterator extends BasicListIterator
    implements ResidueIterator
{
    //~ Constructors ///////////////////////////////////////////////////////////

    public BasicResidueIterator(List v)
    {
        super(v);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public BasicResidue nextResidue()
    {
        return (BasicResidue) next();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
