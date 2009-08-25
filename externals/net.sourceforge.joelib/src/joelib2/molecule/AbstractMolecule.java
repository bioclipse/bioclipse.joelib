///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: AbstractMolecule.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.4 $
//          $Date: 2005/02/17 16:48:36 $
//          $Author: wegner $
//
//Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                     Tuebingen, Germany, 2001,2002,2003,2004,2005
//Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                     2003,2004,2005
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

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;


/**
 *
 * @.author       wegner
 * @.wikipedia Molecule
 * @.license      GPL
 * @.cvsversion   $Revision: 1.4 $, $Date: 2005/02/17 16:48:36 $
 */
public abstract class AbstractMolecule implements Molecule
{
    //~ Static fields/initializers /////////////////////////////////////////////

    public final static IOType DEFAULT_IO_TYPE = BasicIOTypeHolder.instance()
                                                                  .getIOType(
            "SDF");

    //~ Constructors ///////////////////////////////////////////////////////////

    public AbstractMolecule()
    {
        this(DEFAULT_IO_TYPE, DEFAULT_IO_TYPE);
    }

    public AbstractMolecule(final Molecule source)
    {
        this(source, false, null);
    }

    public AbstractMolecule(IOType itype, IOType otype)
    {
    }

    public AbstractMolecule(final Molecule source, boolean cloneDesc)
    {
        this(source, cloneDesc, null);
    }

    public AbstractMolecule(final Molecule source, boolean cloneDesc,
        String[] descriptors)
    {
        this();
        set(source, cloneDesc, descriptors);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public abstract Object clone();
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
