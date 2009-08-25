///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: TagPrefs.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:43 $
//            $Author: wegner $
//  Original Author: Patrick Tullmann <taglets@tullmann.org>
//  Original Version: ???
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
package wsi.ra.taglets;

import java.util.prefs.Preferences;


/**
 * Common prefs usage for Pat's Taglet Collection.
 *
 * @todo Provide pointers to tools for editing user preferences.
 * @.author Patrick Tullmann &lt;<a href="mailto:taglets@tullmann.org">taglets@tullmann.org</a>&gt;
 */
class TagPrefs
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Preferences prefs = Preferences.userNodeForPackage(
            TagPrefs.class);
    static final String PREF_NOVALUE = "-";

    //~ Instance fields ////////////////////////////////////////////////////////

    private final String prefix;

    //~ Constructors ///////////////////////////////////////////////////////////

    TagPrefs(String prefix)
    {
        if (prefix == null)
        {
            throw new NullPointerException(
                "TagPrefs requires a non-null prefs prefix");
        }

        this.prefix = prefix;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    static void flush() throws java.util.prefs.BackingStoreException
    {
        TagPrefs.prefs.flush();
    }

    /**
     * Force a pref to exist in the external prefs DB.  Give it
     * the default value, if it doesn't already have value.
     */
    void forcePref(String prefName, String defaultValue)
    {
        String realPrefName = this.prefix + "." + prefName;

        String val = prefs.get(realPrefName, defaultValue);
        prefs.put(realPrefName, val);
    }

    /**
     * Return the value associated with the given pref.  Returns
     * PREF_NOVALUE as the default.
     */
    String getPref(String prefName)
    {
        String realPrefName = this.prefix + "." + prefName;
        String val = prefs.get(realPrefName, PREF_NOVALUE);

        return val;
    }

    String prefix()
    {
        return this.prefix;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
