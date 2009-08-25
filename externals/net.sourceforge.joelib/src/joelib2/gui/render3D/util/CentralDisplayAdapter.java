///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: CentralDisplayAdapter.java,v $
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

import joelib2.gui.render3D.graphics3D.JPanel3D;

import joelib2.molecule.Molecule;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:34 $
 */
public class CentralDisplayAdapter extends MolViewerEventAdapter
{
    //~ Instance fields ////////////////////////////////////////////////////////

    JPanel3D myPanel;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the CentralDisplayAdapter object
     *
     * @param panel  Description of the Parameter
     */
    public CentralDisplayAdapter(JPanel3D panel)
    {
        myPanel = panel;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Description of the Method
     *
     * @param event  Description of the Parameter
     */
    public void centralDisplayChange(MolViewerEvent event)
    {
        if (event.getType() == MolViewerEvent.REPLACE_MOLECULE)
        {
            myPanel.clear();
            myPanel.addMolecule((Molecule) event.getParam());
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
