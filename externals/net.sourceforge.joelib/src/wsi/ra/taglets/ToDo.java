///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ToDo.java,v $
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

import java.util.Map;


/**
 * A Taglet that defines the <code>@todo</code> tag for Javadoc
 * comments.  The ToDo taglet is customizable and handles multiple
 * entries cleanly.  See below, for an example.
 *
 * <p> Run <code>javadoc</code> with a <code>-tagletpath</code> that
 * points to the compiled version of this class (and its dependents)
 * and pass <code>-taglet wsi.ra.taglets.ToDo</code> to javadoc.
 * That will cause the <code>@todo</code> tag to be registered.
 *
 * <p>Examples: See the end of this documentation block for examples
 * of how multiple @todo items are handled.  See {@link #ToDo the ToDo constructor}
 * for an example of a single todo item tag.
 *
 * <p>See the {@link ListTag} documentation for a list of preferences
 * that can be used to customize the output of this tag.
 *
 * @todo Add a 'done' tag for converting todo items into done items.
 * @todo This is a fake todo item so you can see what multiple todo items look like.
 * @todo This is another fake todo item.
 * @.author Patrick Tullmann &lt;<a href="mailto:taglets@tullmann.org">taglets@tullmann.org</a>&gt;
 */
public class ToDo extends ListTag
{
    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Create a new ListTag, with tag name 'todo'.  Default
     * the tag header to 'To Do:' and default to an
     * unordered list.
     *
     * @todo a single todo entry
     */
    public ToDo()
    {
        super(".todo", "To Do:", ListTag.UNORDERED_LIST);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static void register(Map tagletMap)
    {
        ListTag.register(tagletMap, new ToDo());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
