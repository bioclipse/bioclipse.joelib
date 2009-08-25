///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ListTag.java,v $
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
import com.sun.tools.doclets.Taglet;


/**
 * A generic Taglet implementation that provides "list-like" default
 * behavior for handling multiple instances of the tag which occur in
 * a single block.  This class does not actually provide any tags, but
 * instead provides infrastructure for tags.  See {@link ToDo} and
 * for tags that are built from this tag.
 *
 * <p> Customization of the tag is through user Preferences.  The
 * preferences are all stored in the 'ListTag.class' user node (maps
 * to something like '<code>org/tullmann/taglets</code>' in the
 * per-user repository).  In that node, the preferences are prefixed
 * with the tag-name plus "." (e.g., For an '<code>@todo</code>' tag
 * the prefix is '<code>todo</code>').  The following suffixes are
 * used to customize how the tag shows up:
 * <table>
 * <tr>
 *   <th>Suffix</th><th>Meaning</th><th>Default</th>
 * </tr>
 * <tr>
 *   <td align="left">listtype</td>
 *   <td align="left">The type of HTML list (currently just 'ordered' or 'unordered')</td>
 *   <td align="left">Passed to ListTag constructor</td>
 * </tr>
 * <tr>
 *   <td>header.text</td>
 *   <td>The header text to prefix the list (e.g., 'To Do:')</td>
 *   <td>Passed to ListTag constructor</td>
 * </tr>
 * <tr>
 *   <td>header.color.fg</td>
 *   <td>Foreground color of the header text, any legal HTML color spec is valid.</td>
 *   <td>- (no color)</td>
 * </tr>
 * <tr>
 *   <td>header.color.bg</td>
 *   <td>Background color of the header text, any legal HTML color spec is valid.</td>
 *   <td>- (no color)</td>
 * </tr>
 * <tr>
 *   <td>header.relsize</td>
 *   <td>Relative size of the header text (e.g., +1, -1, etc.)</td>
 *   <td>- (no change)</td>
 * </tr>
 * <tr>
 *   <td>text.color.fg</td>
 *   <td>Foreground color of the text body, any legal HTML color spec is valid.</td>
 *   <td>- (no color)</td>
 * </tr>
 * <tr>
 *   <td>text.color.bg</td>
 *   <td>Background color of the text body, any legal HTML color spec is valid.</td>
 *   <td>- (no color)</td>
 * </tr>
 * <tr>
 *   <td>text.relsize</td>
 *   <td>Relative size of the text body (e.g., +1, -1, etc.)</td>
 *   <td>- (no change)</td>
 * </tr>
 * </table>
 *
 * @todo Support for supressing a tag (e.g., @todo -> @done).
 * @todo Support customization of the list bullets.
 * @todo Add comments to the methods customization of the list bullets.
 * @todo Add other list types?  (e.g., the comma-delimited list?).
 * @todo Support plural/singular labels.
 * @.author Patrick Tullmann &lt;<a href="mailto:taglets@tullmann.org">taglets@tullmann.org</a>&gt;
 */
public abstract class ListTag implements Taglet
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String LISTTYPE = "listtype";
    private static final String HEADER_TEXT = "header.text";
    public static final ListType ORDERED_LIST = new ListType("ordered", "<ol>",
            "</ol>", "<li>", "</li>");
    public static final ListType UNORDERED_LIST = new ListType("unordered",
            "<ul>", "</ul>", "<li>", "</li>");
    public static final ListType TABLE_LIST = new ListType("table",
            "<table cellpadding=\"2\" width=\"100%\">", "</table>", "<tr>",
            "</tr>");
    public static final ListType VISIBLETABLE_LIST = new ListType("table",
            "<table BORDER=\"1\" CELLPADDING=\"2\" width=\"100%\">", "</table>",
            "<tr>", "</tr>");

    //~ Instance fields ////////////////////////////////////////////////////////

    protected final TagPrefs tagPrefs;

    //  private static final String TEXT_FGCOLOR = "text.color.fg";
    //  private static final String TEXT_BGCOLOR = "text.color.bg";
    //  private static final String TEXT_RELSIZE = "text.relsize";
    private final String tagName;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Create a new list-behaviour tag.  The <code>tagHeader</code> and
     * <code>listType</code> parameters can be overridden in properties.
     */
    public ListTag(String tagName, String tagHeader, ListType listType)
    {
        this(tagName, tagName, tagHeader, listType);
    }

    public ListTag(String tagName, String prefsName, String tagHeader,
        ListType listType)
    {
        this.tagName = tagName;
        this.tagPrefs = new TagPrefs(prefsName);

        forceDefaultPrefs(tagHeader, listType);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Register the given taglet in the given map.  Uses
     * the ListTag.name() to get the name of the tag.
     */
    public static void register(Map tagletMap, ListTag lt)
    {
        Taglet oldt = (Taglet) tagletMap.get(lt.tagName);

        if (oldt != null)
        {
            System.err.println("Warning(ListTag): replacing taglet " + oldt +
                " with " + lt + ".");
            tagletMap.remove(lt.tagName);
        }

        tagletMap.put(lt.tagName, lt);
    }

    public String getName()
    {
        return this.tagName;
    }

    public boolean inConstructor()
    {
        return true;
    }

    public boolean inField()
    {
        return true;
    }

    public boolean inMethod()
    {
        return true;
    }

    public boolean inOverview()
    {
        return true;
    }

    public boolean inPackage()
    {
        return true;
    }

    public boolean inType()
    {
        return true;
    }

    public boolean isInlineTag()
    {
        return false;
    }

    public String toString(Tag tag)
    {
        StringBuffer sbuf = new StringBuffer(1000);

        // XXX make it an option to emit single entries with the list header/etc.
        startingTags();

        emitHeader(sbuf, false);
        emitTag(tag, sbuf, false);
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
            emitTag(tags[i], sbuf, true);
        }

        emitFooter(sbuf, true);

        endingTags(sbuf);

        return sbuf.toString();
    }

    /**
     * Override to insert custom text after the list is complete,
     * but before the list closing tags
     */
    protected void emitCustomFooter(StringBuffer sbuf, boolean multi)
    {
    }

    /**
     * Override to insert custom text after the list start,
     * but before the first bit of tag text
     */
    protected void emitCustomHeader(StringBuffer sbuf, boolean multi)
    {
    }

    /**
     * Emit footer for HTML version of tag.  All generated text is
     * put in the given sbuf.  The multi parameter indicates if
     * this header is for more than one element.
     */
    protected void emitFooter(StringBuffer sbuf, boolean multi)
    {
        String listTypeKey = tagPrefs.getPref(LISTTYPE);
        ListType listType = ListType.lookup(listTypeKey);

        emitCustomFooter(sbuf, multi);

        if (multi)
        {
            sbuf.append(listType.getEndHtml());
        }

        sbuf.append("</dd>\n");
    }

    /**
     * Emit header for HTML version of tag.  All generated text is
     * put in the given sbuf.  The multi parameter indicates if
     * this header is for more than one element.
     */
    protected void emitHeader(StringBuffer sbuf, boolean multi)
    {
        String tagHeader = tagPrefs.getPref(HEADER_TEXT);
        String listTypeKey = tagPrefs.getPref(LISTTYPE);
        ListType listType = ListType.lookup(listTypeKey);

        sbuf.append("<dt><b>");
        formatText(sbuf, tagHeader, "header");
        sbuf.append("</b></dt>").append("<dd>\n");

        if (multi)
        {
            sbuf.append(listType.getStartHtml());
        }

        sbuf.append("\n");

        emitCustomHeader(sbuf, multi);
    }

    protected void emitTag(Tag tag, StringBuffer sbuf, boolean multi)
    {
        String listTypeKey = tagPrefs.getPref(LISTTYPE);
        ListType listType = ListType.lookup(listTypeKey);

        if (multi)
        {
            sbuf.append(listType.getEntryStartHtml());
        }

        parseTagText(sbuf, tag.text(), multi);

        if (multi)
        {
            sbuf.append(listType.getEntryEndHtml());
        }
    }

    protected void emitTag(String text, StringBuffer sbuf, boolean multi)
    {
        String listTypeKey = tagPrefs.getPref(LISTTYPE);
        ListType listType = ListType.lookup(listTypeKey);

        if (multi)
        {
            sbuf.append(listType.getEntryStartHtml());
        }

        parseTagText(sbuf, text, multi);

        if (multi)
        {
            sbuf.append(listType.getEntryEndHtml());
        }
    }

    protected void endingTags(StringBuffer sbuf)
    {
    }

    protected void forceColorPrefs(TagPrefs tagPrefs, String tagPrefix)
    {
        tagPrefs.forcePref(tagPrefix + ".color.fg", TagPrefs.PREF_NOVALUE);
        tagPrefs.forcePref(tagPrefix + ".color.bg", TagPrefs.PREF_NOVALUE);
        tagPrefs.forcePref(tagPrefix + ".relsize", TagPrefs.PREF_NOVALUE);
    }

    protected void forceCustomDefaultPrefs(TagPrefs tagPrefs) throws Exception
    {
    }

    /**
     * Format the given text using the properties under the givne propName into
     * the given StringBuffer.
     */
    protected void formatText(StringBuffer sbuf, String text, String propName)
    {
        String fgcolor = tagPrefs.getPref(propName + ".color.fg");
        String bgcolor = tagPrefs.getPref(propName + ".color.bg");
        String relsize = tagPrefs.getPref(propName + ".relsize");

        boolean hasBgColor = false;

        if (!bgcolor.equals(TagPrefs.PREF_NOVALUE))
        {
            sbuf.append("<table><tr><td bgcolor=\"").append(bgcolor).append(
                "\">");
            hasBgColor = true;
        }

        if (fgcolor.equals(TagPrefs.PREF_NOVALUE) &&
                relsize.equals(TagPrefs.PREF_NOVALUE))
        {
            sbuf.append(text);
        }
        else
        {
            sbuf.append("<font ");

            if (!fgcolor.equals(TagPrefs.PREF_NOVALUE))
            {
                sbuf.append("color=\"").append(fgcolor).append("\" ");
            }

            if (!relsize.equals(TagPrefs.PREF_NOVALUE))
            {
                sbuf.append("size=\"").append(relsize).append("\" ");
            }

            sbuf.append(">").append(text).append("</font>");
        }

        if (hasBgColor)
        {
            sbuf.append("</td></tr></table>");
        }
    }

    /**
     * Generate formatted HTML for the given tag text.  Put the
     * HTML in the given StringBuffer.
     *
     * @return the given StringBuffer.
     */
    protected void parseTagText(StringBuffer sbuf, String text, boolean multi)
    {
        String listTypeKey = tagPrefs.getPref(LISTTYPE);
        ListType listType = ListType.lookup(listTypeKey);
        boolean doTable = false;

        if (multi && (listType == ListTag.TABLE_LIST))
        {
            doTable = true;
        }

        if (doTable)
        {
            sbuf.append("<td>");
        }

        formatText(sbuf, text, "text");

        if (doTable)
        {
            sbuf.append("</td>\n");
        }
    }

    protected void startingTags()
    {
    }

    private void forceDefaultPrefs(String tagHeader, ListType listType)
    {
        try
        {
            tagPrefs.forcePref(LISTTYPE, listType.mapKey());
            tagPrefs.forcePref(HEADER_TEXT, tagHeader);
            forceColorPrefs(tagPrefs, "header");
            forceColorPrefs(tagPrefs, "text");
            forceCustomDefaultPrefs(tagPrefs);
            tagPrefs.flush();
        }
        catch (Exception e)
        {
            System.err.println("(ignored) prefs exception: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    private static class ListType
    {
        private static final Map nameToType = new HashMap(5);
        private final String e;
        private final String entryE;
        private final String entryS;
        private final String s;
        private final String type;

        ListType(String type, String s, String e, String entryS, String entryE)
        {
            this.type = type;
            this.s = s;
            this.e = e;
            this.entryS = entryS;
            this.entryE = entryE;

            nameToType.put(this.mapKey(), this);
        }

        public static ListType lookup(String key)
        {
            ListType lt;
            lt = (ListType) (nameToType.get(key));

            return lt;
        }

        public String getEndHtml()
        {
            return this.e;
        }

        public String getEntryEndHtml()
        {
            return this.entryE;
        }

        public String getEntryStartHtml()
        {
            return this.entryS;
        }

        public String getStartHtml()
        {
            return this.s;
        }

        public String mapKey()
        {
            return this.type;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
