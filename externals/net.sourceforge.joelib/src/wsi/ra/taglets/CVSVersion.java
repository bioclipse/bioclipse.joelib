///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: CVSVersion.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:43 $
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
package wsi.ra.taglets;

import java.util.Map;

import com.sun.javadoc.Tag;


/**
 * A Taglet that defines the <code>@.cvsversion</code> tag for Javadoc
 * comments.
 *
 * @.author Joerg Kurt Wegner
 */
public class CVSVersion extends ListTag
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private String _path;

    //~ Constructors ///////////////////////////////////////////////////////////

    public CVSVersion()
    {
        super(".cvsversion", "CVS Version:", ListTag.TABLE_LIST);

        String clName = this.getClass().getName();
        _path = System.getProperty(clName + ".path");

        if (_path == null)
        {
            System.err.println("WARNING: System property " + clName + ".path" +
                " must be defined.");
        }
        else
        {
            //                  System.out.println(
            //                          "INFO: " + "Cite taglet uses base path: " + _path);
            _path = _path.toUpperCase();
            _path = _path.replace('\\', '/');
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static void register(Map tagletMap)
    {
        ListTag.register(tagletMap, new CVSVersion());
    }

    public String toString(Tag tag)
    {
        StringBuffer sbuf = new StringBuffer(1000);

        // XXX make it an option to emit single entries with the list header/etc.
        startingTags();

        emitHeader(sbuf, false);

        String[] version = getCVSTag(tag);
        emitTag(version[0], sbuf, true);
        emitTag(version[1], sbuf, true);
        emitTag(version[2], sbuf, true);
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
            String[] version = getCVSTag(tags[i]);
            emitTag(version[0], sbuf, true);
            emitTag(version[1], sbuf, true);
            emitTag(version[2], sbuf, true);

            //System.out.println(version[1]);
        }

        emitFooter(sbuf, true);

        endingTags(sbuf);

        return sbuf.toString();
    }

    /**
     * Very primitive parser, but that will do for the first time.
     *
     * @param tag
     * @return String
     */
    protected final String[] getCVSTag(final Tag tag)
    {
        String headLink = "";
        StringBuffer sb = new StringBuffer(200);

        if (_path != null)
        {
            String file = tag.position().file().toString();
            String actfile = file;
            actfile = actfile.toUpperCase();
            actfile = actfile.replace('\\', '/');

            int index = actfile.indexOf(_path);
            String sub = file.substring(index + _path.length());
            sub = sub.replace('\\', '/');

            //System.out.println("sub:"+sub);
            sb.append("<a href=\"");
            sb.append(
                "http://cvs.sourceforge.net/cgi-bin/viewcvs.cgi/joelib/joelib2/src/");
            sb.append(sub);
            sb.append("?rev=HEAD&content-type=text/vnd.viewcvs-markup");
            sb.append("\">source code (CVS head)</a>");
            headLink = sb.toString();
        }

        String raw = tag.text();
        String woDollar = raw.replace('$', ' ');

        // exmaple:
        // $Revision: 1.6 $, $Date: 2005/02/17 16:48:43 $
        int index = woDollar.indexOf(",");

        if (index == -1)
        {
            System.err.println("WARNING: CVS version has wrong format in" +
                tag.position());

            return new String[]{"", ""};
        }
        else
        {
            return
                new String[]
                {
                    woDollar.substring(0, index).trim(),
                    woDollar.substring(index + 1).trim(), headLink
                };
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
