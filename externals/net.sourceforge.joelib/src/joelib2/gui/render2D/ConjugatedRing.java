///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: ConjugatedRing.java,v $
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
 * Holding special conjugated ring informations for a single ring.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:32 $
 */
public class ConjugatedRing
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.gui.render2D.ConjugatedRing");

    //~ Instance fields ////////////////////////////////////////////////////////

    public String charge;
    public int[] ring;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Parse from-option-to string, where from and to are colon delimited atom indices and
     * option is a colon delimited option string which must start with a non-digit character.
     */
    public boolean parseCRing(String ringS)
    {
        if ((ringS == null) || (ringS.trim().length() == 0))
        {
            return true;
        }

        Vector entries = new Vector();
        HelperMethods.tokenize(entries, ringS, ",");

        String entry;
        Vector ringV = new Vector();

        for (int i = 0; i < entries.size(); i++)
        {
            entry = ((String) entries.get(i)).trim();

            if ((Character.isDigit(entry.charAt(0)) == false) && (i == 0))
            {
                logger.error("Conjugated ring-option entry must be a number.");

                return false;
            }

            if (Character.isDigit(entry.charAt(0)) == false)
            {
                if (!parseOption(entry))
                {
                    return false;
                }
            }
            else
            {
                ringV.add(new Integer(entry));
            }
        }

        if (ringV.size() == 0)
        {
            logger.error("Conjugated ring-option entry can not be empty.");

            return false;
        }

        ring = new int[ringV.size()];

        for (int i = 0; i < ringV.size(); i++)
        {
            ring[i] = ((Integer) ringV.get(i)).intValue();

            //System.out.println("cRingAtom "+ring[i]);
        }

        return true;
    }

    /**
     * Parse entries which start with a non-digit.
     *
     * @param optionEntry
     * @return
     */
    public boolean parseOption(String optionEntry)
    {
        char firstChar = optionEntry.charAt(0);

        if ((firstChar == 'c') && (optionEntry.length() > 1))
        {
            charge = optionEntry.substring(1);
        }

        return true;
    }

    public String toString()
    {
        if (ring == null)
        {
            return null;
        }

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < ring.length; i++)
        {
            sb.append(ring[i]);

            if ((charge == null) && (i < (ring.length - 1)))
            {
                sb.append(',');
            }
            else
            {
                sb.append(',');
            }
        }

        if (charge != null)
        {
            sb.append('c');
            sb.append(charge);
        }

        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
