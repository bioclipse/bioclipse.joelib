///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Author.java,v $
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

import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.Tag;


/**
 * A Taglet that defines the <code>@.author</code> tag for Javadoc
 * comments.
 */
public class Author extends ListTag
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final Map authorTextMap = new HashMap(20);

    private static final String WEGNER_LINK =
        "<a href=\"http://en.wikipedia.org/wiki/User:Joerg_Kurt_Wegner\">J&ouml;rg Kurt Wegner</a>.";

    static
    {
        authorTextMap.put("wegner", WEGNER_LINK);
        authorTextMap.put("wegnerj", WEGNER_LINK);
        authorTextMap.put("abolmaal", "Seyed Foad Badreddin Abolmaali");
        authorTextMap.put("rapp", "Fred Rapp");
        authorTextMap.put("egonw",
            "<a href=\"http://www.openscience.org/~egonw/\" target=\"_top\">Egon Willighagen</a> (<a href=\"mailto:egonw@sci.kun.nl\">egonw@sci.kun.nl</A>)");
        authorTextMap.put("John E. Lloyd",
            "<a href=\"http://www.cs.ubc.ca//~lloyd/index.html\" target=\"_top\">John E. Lloyd</a>");
        authorTextMap.put("Stephen Jelfs",
            "Stephen Jelfs at <a href=\"http://cisrg.shef.ac.uk\" target=\"_top\">ChemoInformatics at Sheffield</a>");
        authorTextMap.put("Serguei Patchkovskii",
            "Serguei Patchkovskii (<a href=\"mailto:Serguei.Patchkovskii@sympatico.ca\">Serguei.Patchkovskii@sympatico.ca</a>)");
        authorTextMap.put("steinbeck",
            "Christoph Steinbeck (<a href=\"mailto:c.steinbeck@uni-koeln.de\">c.steinbeck@uni-koeln.de</a>)");
    }

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Create a new License tag.
     */
    public Author()
    {
        super(".author", "Author:", ListTag.UNORDERED_LIST);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Register this taglet with the given name.
     */
    public static void register(Map tagletMap)
    {
        ListTag.register(tagletMap, new Author());
    }

    public String toString()
    {
        return this.getClass().getName() +
            " which contains also a list of predefined authors (including E-mail adress).";
    }

    public String toString(Tag tag)
    {
        StringBuffer sbuf = new StringBuffer(1000);

        // XXX make it an option to emit single entries with the list header/etc.
        startingTags();

        emitHeader(sbuf, false);
        emitTag(getAuthorTag(tag), sbuf, false);
        emitFooter(sbuf, false);

        endingTags(sbuf);

        return sbuf.toString();
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
            emitTag(getAuthorTag(tags[i]), sbuf, true);
        }

        emitFooter(sbuf, true);

        endingTags(sbuf);

        return sbuf.toString();
    }

    protected final String getAuthorTag(final Tag tag)
    {
        String expLicense = (String) authorTextMap.get(tag.text());

        if (expLicense == null)
        {
            return tag.text();
        }
        else
        {
            return expLicense;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
