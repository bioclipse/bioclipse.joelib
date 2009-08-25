///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: FromToAtoms.java,v $
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

import joelib2.util.HelperMethods;

import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Abstract base class for two point informations generated from the atom positions.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:32 $
 */
public abstract class FromToAtoms
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.gui.render2D.FromToAtoms");

    //~ Instance fields ////////////////////////////////////////////////////////

    public int[] from;
    public int[] to;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Parse entries which start with a non-digit.
     *
     * @param optionEntry
     * @return
     */
    public abstract boolean parseOption(String optionEntry);

    /**
     * Parse from-option-to string, where from and to are colon delimited atom indices and
     * option is a colon delimited option string which must start with a non-digit character.
     */
    public boolean parseFromToAtom(String fromto)
    {
        if ((fromto == null) || (fromto.trim().length() == 0))
        {
            return true;
        }

        Vector entries = new Vector();
        HelperMethods.tokenize(entries, fromto, ",");

        String entry;
        Vector fromV = new Vector();
        Vector toV = new Vector();
        boolean store2to = false;

        for (int i = 0; i < entries.size(); i++)
        {
            entry = ((String) entries.get(i)).trim();

            if ((Character.isDigit(entry.charAt(0)) == false) && (i == 0))
            {
                logger.error("First from-option-to entry must be a number.");

                return false;
            }

            if (Character.isDigit(entry.charAt(0)) == false)
            {
                store2to = true;

                if (!parseOption(entry))
                {
                    return false;
                }
            }
            else
            {
                if (store2to)
                {
                    toV.add(new Integer(entry));
                }
                else
                {
                    fromV.add(new Integer(entry));
                }
            }
        }

        if (toV.size() == 0)
        {
            logger.error(
                "from-option-to entry must contain at least one non-digit-delimiter (option) to distinguish between FROM and TO atoms.");

            return false;
        }

        from = new int[fromV.size()];

        for (int i = 0; i < fromV.size(); i++)
        {
            from[i] = ((Integer) fromV.get(i)).intValue();
        }

        to = new int[toV.size()];

        for (int i = 0; i < toV.size(); i++)
        {
            to[i] = ((Integer) toV.get(i)).intValue();
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
