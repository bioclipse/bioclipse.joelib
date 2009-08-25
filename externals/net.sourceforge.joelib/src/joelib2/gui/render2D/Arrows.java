///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Arrows.java,v $
//Purpose:  Renderer for a 2D layout.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.7 $
//                      $Date: 2005/02/17 16:48:32 $
//                      $Author: wegner $
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
package joelib2.gui.render2D;

import joelib2.molecule.Molecule;

import joelib2.util.HelperMethods;

import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Holding multiple arrow informations.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:32 $
 */
public class Arrows
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.gui.render2D.Arrows");

    //~ Instance fields ////////////////////////////////////////////////////////

    public Arrow[] arrows;

    public Molecule molecule;

    //~ Constructors ///////////////////////////////////////////////////////////

    public Arrows(Molecule mol, String entries)
    {
        if (!parseFromToAtoms(mol, entries))
        {
            logger.error(
                "from-option-to;from-option-to;... line could not be parsed.");
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Parse entries which start with a non-digit.
     *
     * @param optionEntry
     * @return
     */
    public boolean parseFromToAtoms(Molecule mol, String fromto)
    {
        molecule = mol;

        Vector entries = new Vector();
        Arrow arrow;
        Vector arrowsV = new Vector();
        HelperMethods.tokenize(entries, fromto, ";");

        String entry;

        for (int i = 0; i < entries.size(); i++)
        {
            entry = (String) entries.get(i);
            arrow = new Arrow();

            if (!arrow.parseFromToAtom(entry))
            {
                return false;
            }

            arrowsV.add(arrow);
        }

        arrows = new Arrow[arrowsV.size()];

        for (int i = 0; i < arrowsV.size(); i++)
        {
            arrows[i] = (Arrow) arrowsV.get(i);
        }

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
