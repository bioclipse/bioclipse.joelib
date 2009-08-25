///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: WikipediaEN.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.1 $
//            $Date: 2005/02/17 16:48:43 $
//            $Author: wegner $
//
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
import java.util.StringTokenizer;

import com.sun.javadoc.Tag;


/**
 * A Taglet that defines the <code>@.cite</code> tag for Javadoc
 * comments.
 *
 * -J-Dwsi.ra.taglets.Cite.path=D:/workingAt/joelib/src/<br>
 * -J-Dwsi.ra.taglets.Cite.file=literature.html<br>
 * or under Ant:<br>
 * additionalparam=" -J-Dwsi.ra.taglets.Cite.path=D:/workingAt/joelib/src/ -J-Dwsi.ra.taglets.Cite.file=literature.html "<br>
 *
 * @.author Joerg Kurt Wegner
 */
public class WikipediaEN extends ListTag
{
    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Create a new ListTag, with tag name 'todo'.  Default
     * the tag header to 'To Do:' and default to an
     * unordered list.
     *
     * @todo a single todo entry
     */
    public WikipediaEN()
    {
        super(".wikipedia", "See also Wikipedia (english):",
            ListTag.UNORDERED_LIST);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static void register(Map tagletMap)
    {
        ListTag.register(tagletMap, new WikipediaEN());
    }

    public String toString(Tag tag)
    {
        String wikipedia = tag.text();

        if (wikipedia == null)
        {
            System.err.println("WARNING: Could not find Wikipedia entry " +
                tag.text() + " in ???");
        }
        else
        {
            return "<a href=\"http://en.wikipedia.org/wiki/" +
                wikipedia.replace(' ', '_') + "\">" + wikipedia + "</a>";
        }

        return "";
    }

    public String toString(Tag[] tags)
    {
        if (tags.length == 0)
        {
            return "";
        }

        StringBuffer sbuf = new StringBuffer(200 + (800 * tags.length));

        startingTags();

        emitHeader(sbuf, true);

        for (int i = 0; i < tags.length; i++)
        {
            emitTag(toString(tags[i]), sbuf, true);
        }

        emitFooter(sbuf, true);

        endingTags(sbuf);

        return sbuf.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
