///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: ConjugatedRings.java,v $
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
 * Holding special conjugated ring informations for multiple rings.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:32 $
 */
public class ConjugatedRings
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.gui.render2D.ConjugatedRings");

    //~ Instance fields ////////////////////////////////////////////////////////

    public ConjugatedRing[] cRings;

    public Molecule molecule;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ConjugatedRings(Molecule mol, String entries)
    {
        if (!parseCRing(mol, entries))
        {
            logger.error(
                "ring-option;ring-option;... line could not be parsed.");
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Parse entries which start with a non-digit.
     *
     * @param optionEntry
     * @return
     */
    public boolean parseCRing(Molecule mol, String cRingsS)
    {
        molecule = mol;

        Vector entries = new Vector();
        ConjugatedRing cRing;
        Vector cRingV = new Vector();
        HelperMethods.tokenize(entries, cRingsS, ";");

        String entry;

        for (int i = 0; i < entries.size(); i++)
        {
            entry = (String) entries.get(i);
            cRing = new ConjugatedRing();

            if (!cRing.parseCRing(entry))
            {
                return false;
            }

            cRingV.add(cRing);
        }

        cRings = new ConjugatedRing[cRingV.size()];

        for (int i = 0; i < cRingV.size(); i++)
        {
            cRings[i] = (ConjugatedRing) cRingV.get(i);
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
