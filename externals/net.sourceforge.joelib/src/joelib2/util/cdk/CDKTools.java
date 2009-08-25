///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: CDKTools.java,v $
//  Purpose:  Connection to the tools of the Chemical Development Kit (CDK).
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
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
package joelib2.util.cdk;

import joelib2.molecule.Molecule;


/**
 * Helper class to faciliate access to tools of the
 * <a href="http://sourceforge.net/projects/cdk" target="_top">Chemical
 * Development Kit (CDK)</a>.
 *
 * @.author     wegnerj
 * @.license GPL
 */
public class CDKTools
{
    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the CDKTools.
     *
     */
    public CDKTools()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static boolean generate2D(joelib2.molecule.Molecule mol)
    {
        /*Molecule cdkMol = Convertor.convert(mol);
        StructureDiagramGenerator layout = new StructureDiagramGenerator(cdkMol);

        try
        {
            layout.generateCoordinates();
        }
         catch (Exception ex)
        {
            //logger.error(ex.toString());
            ex.printStackTrace();

            return false;
        }

        Molecule tmpCDKmol = layout.getMolecule();
        Atom[] cdkAtoms = tmpCDKmol.getAtoms();

        for (int i = 0; i < cdkAtoms.length; i++)
        {
            mol.getAtom(i + 1).setCoords3D(cdkAtoms[i].getX2D(),
                cdkAtoms[i].getY2D(), 0.0);
        }

        // remove all 3D coordinates in all atoms
        // or the converter will return the 3D
        // coordinates, which were all zero.
        //
        //for (int i = 0; i < cdkAtoms.length; i++) {
        //  cdkAtoms[i].setPoint3D(null);
        //}
        //Molecule newMol=Convertor.convert(layout.getMolecule());
        return true;
        */
        return false;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
