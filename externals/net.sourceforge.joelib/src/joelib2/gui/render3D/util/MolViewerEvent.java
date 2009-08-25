///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MolViewerEvent.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:34 $
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
package joelib2.gui.render3D.util;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import java.util.EventObject;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:34 $
 */
public class MolViewerEvent extends EventObject
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Description of the Field
     */
    public final static int REPLACE_MOLECULE = 0;

    /**
     * Description of the Field
     */
    public final static int ATOM_PICKED = 1;

    /**
     * Description of the Field
     */
    public final static int BOND_PICKED = 2;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * Description of the Field
     */
    protected int eventType;

    /**
     * Description of the Field
     */
    protected Object param = null;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the MolViewerEvent object
     *
     * @param source  Description of the Parameter
     * @param what    Description of the Parameter
     */
    public MolViewerEvent(Object source, int what)
    {
        super(source);
        eventType = what;
    }

    /**
     *Constructor for the MolViewerEvent object
     *
     * @param source  Description of the Parameter
     * @param what    Description of the Parameter
     * @param param   new Molecule molecule
     */
    public MolViewerEvent(Object source, int what, Molecule param)
    {
        this(source, what);
        this.param = param;
    }

    public MolViewerEvent(Object source, int what, Atom param)
    {
        this(source, what);
        this.param = param;
    }

    public MolViewerEvent(Object source, int what, Bond param)
    {
        this(source, what);
        this.param = param;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets the param attribute of the MolViewerEvent object
     *
     * @return   The param value
     */
    public Object getParam()
    {
        return param;
    }

    /**
     * Gets the type attribute of the MolViewerEvent object
     *
     * @return   The type value
     */
    public int getType()
    {
        return eventType;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
