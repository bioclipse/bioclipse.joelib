///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Bibitem.java,v $
//  Purpose:  PropertyHolder for java property file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/01/26 12:07:32 $
//            $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
///////////////////////////////////////////////////////////////////////////////
package jtt.latex.bibtex;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.log4j.Category;


class Bibitem
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Category logger = Category.getInstance(Bibitem.class
            .getName());
    public static final String TYPE_ARTICLE = "article";
    public static final String TYPE_BOOK = "book";
    public static final String TYPE_INBOOK = "inbook";
    public static final String TYPE_BOOKLET = "booklet";
    public static final String TYPE_INCOLLECTION = "incollection";
    public static final String TYPE_INPROCEEDINGS = "inproceedings";
    public static final String TYPE_MANUAL = "manual";
    public static final String TYPE_MASTERSTHESIS = "mastersthesis";
    public static final String TYPE_MISC = "misc";
    public static final String TYPE_PERIODICAL = "periodical";
    public static final String TYPE_PHDTHESIS = "phdthesis";
    public static final String TYPE_PROCEEDINGS = "proceedings";
    public static final String TYPE_TECHREPORT = "techreport";
    public static final String TYPE_UNPUBLISHED = "unpublished";
    public static String[] DEFAULT_TYPES =
        {
            Bibitem.TYPE_ARTICLE, Bibitem.TYPE_BOOK, Bibitem.TYPE_INBOOK,
            Bibitem.TYPE_BOOKLET, Bibitem.TYPE_INCOLLECTION,
            Bibitem.TYPE_INPROCEEDINGS, Bibitem.TYPE_MANUAL,
            Bibitem.TYPE_MASTERSTHESIS, Bibitem.TYPE_MISC,
            Bibitem.TYPE_PERIODICAL, Bibitem.TYPE_PHDTHESIS,
            Bibitem.TYPE_PROCEEDINGS, Bibitem.TYPE_TECHREPORT,
            Bibitem.TYPE_UNPUBLISHED
        };
    public static final String TAG_AUTHOR = "author";
    public static final String TAG_TITLE = "title";
    public static final String TAG_JOURNAL = "journal";
    public static final String TAG_YEAR = "year";
    public static final String TAG_VOLUME = "volume";
    public static final String TAG_NUMBER = "number";
    public static final String TAG_PAGES = "pages";
    public static final String TAG_MONTH = "month";
    public static final String TAG_NOTE = "note";
    public static final String TAG_EDITOR = "editor";
    public static final String TAG_PUBLISHER = "publisher";
    public static final String TAG_SERIES = "series";
    public static final String TAG_ADDRESS = "address";
    public static final String TAG_EDITION = "edition";
    public static final String TAG_URL = "url";
    public static final String TAG_HOWPUBLISHED = "howpublished";
    public static final String TAG_BOOKTITLE = "booktitle";
    public static final String TAG_ORGANIZATION = "organization";
    public static final String TAG_CHAPTER = "chapter";
    public static final String TAG_SCHOOL = "school";
    public static final String TAG_CONTENTS = "contents";
    public static final String TAG_ABSTRACT = "abstract";
    public static final String TAG_TYPE = "type";
    public static final String TAG_INSTITUTION = "institution";
    static String[] DEFAULT_TAGS_ARTICLE =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_JOURNAL, TAG_YEAR, TAG_VOLUME,
            TAG_NUMBER, TAG_PAGES, TAG_MONTH, TAG_NOTE
        };
    static String[] DEFAULT_TAGS_BOOK =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_EDITOR, TAG_PUBLISHER, TAG_YEAR,
            TAG_VOLUME, TAG_NUMBER, TAG_SERIES, TAG_ADDRESS, TAG_EDITION,
            TAG_MONTH, TAG_NOTE
        };
    static String[] DEFAULT_TAGS_BOOKLET =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_HOWPUBLISHED, TAG_ADDRESS, TAG_YEAR,
            TAG_MONTH, TAG_NOTE
        };
    static String[] DEFAULT_TAGS_CONFERENCE =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_BOOKTITLE, TAG_EDITOR, TAG_YEAR,
            TAG_VOLUME, TAG_NUMBER, TAG_SERIES, TAG_PAGES, TAG_ADDRESS,
            TAG_MONTH, TAG_ORGANIZATION, TAG_PUBLISHER, TAG_NOTE
        };
    static String[] DEFAULT_TAGS_INBOOK =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_EDITOR, TAG_CHAPTER, TAG_PAGES,
            TAG_PUBLISHER, TAG_YEAR, TAG_MONTH, TAG_VOLUME, TAG_NUMBER,
            TAG_SERIES, TAG_ADDRESS, TAG_EDITION, TAG_TYPE, TAG_NOTE
        };
    static String[] DEFAULT_TAGS_INCOLLECTION =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_BOOKTITLE, TAG_EDITOR, TAG_YEAR,
            TAG_MONTH, TAG_PUBLISHER, TAG_TYPE, TAG_SERIES, TAG_CHAPTER,
            TAG_PAGES, TAG_ADDRESS, TAG_EDITION, TAG_NOTE
        };
    static String[] DEFAULT_TAGS_INPROCEEDINGS =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_BOOKTITLE, TAG_EDITOR, TAG_YEAR,
            TAG_MONTH, TAG_PUBLISHER, TAG_VOLUME, TAG_NUMBER, TAG_SERIES,
            TAG_PAGES, TAG_ADDRESS, TAG_ORGANIZATION, TAG_NOTE
        };
    static String[] DEFAULT_TAGS_MANUAL =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_ORGANIZATION, TAG_ADDRESS, TAG_EDITION,
            TAG_YEAR, TAG_MONTH, TAG_NOTE
        };
    static String[] DEFAULT_TAGS_MASTERSTHESIS =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_YEAR, TAG_MONTH, TAG_ADDRESS, TAG_TYPE,
            TAG_SCHOOL, TAG_NOTE
        };
    static String[] DEFAULT_TAGS_MISC =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_YEAR, TAG_MONTH, TAG_HOWPUBLISHED,
            TAG_NOTE
        };
    static String[] DEFAULT_TAGS_PERIODICAL =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_YEAR, TAG_MONTH, TAG_ADDRESS, TAG_TYPE,
            TAG_JOURNAL, TAG_VOLUME, TAG_NUMBER, TAG_NOTE
        };
    static String[] DEFAULT_TAGS_PHDTHESIS =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_YEAR, TAG_MONTH, TAG_ADDRESS, TAG_TYPE,
            TAG_SCHOOL, TAG_NOTE
        };
    static String[] DEFAULT_TAGS_PROCEEDINGS =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_EDITOR, TAG_YEAR, TAG_MONTH,
            TAG_PUBLISHER, TAG_VOLUME, TAG_NUMBER, TAG_SERIES, TAG_PAGES,
            TAG_ADDRESS, TAG_ORGANIZATION, TAG_NOTE
        };
    static String[] DEFAULT_TAGS_TECHREPORT =
        new String[]
        {
            TAG_AUTHOR, TAG_TITLE, TAG_INSTITUTION, TAG_YEAR, TAG_MONTH,
            TAG_ADDRESS, TAG_NUMBER, TAG_TYPE, TAG_NOTE
        };
    static String[] DEFAULT_TAGS_UNPUBLISHED =
        new String[]{TAG_AUTHOR, TAG_TITLE, TAG_YEAR, TAG_MONTH, TAG_NOTE};
    static final String[] letters =
        {
            "", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
            "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y",
            "z"
        };
    private static int numSpacesBeforeTagValue = 18;
    private static int numSpacesBeforeTag = 2;
    private static String spacesBeforeTagValue = null;
    private static String spacesBeforeTag = null;
    private static String tagValueSeparator = " =";
    private static String startValueChar = "\"";
    private static String endValueChar = "\"";

    //~ Instance fields ////////////////////////////////////////////////////////

    private String id;
    private String key;

    private HashMap tags;
    private String type;

    //~ Constructors ///////////////////////////////////////////////////////////

    Bibitem(String s)
    {
        id = "";
        parseBibtexEntry(s);
    }

    Bibitem(String bibtexEntry, String _id) throws Exception
    {
        parseBibtexEntry(bibtexEntry);

        if (tags == null)
        {
            throw new Exception("BibTeX entry could not be parsed.");
        }

        id = _id;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void addTag(String s, String s1)
    {
        tags.put(s, s1);
    }

    public String getKey()
    {
        return key;
    }

    public String getTag(String s)
    {
        return (String) tags.get(s);
    }

    public String getType()
    {
        return type;
    }

    public HashMap parseBibtexEntry(String s)
    {
        HashMap tmpTags = parseBibtexTags(separateBibtexTags(s), s);

        if (tmpTags != null)
        {
            tags = tmpTags;

            //System.out.println("KEY original:"+key+" generated:"+createAuthorYearKey());
            if ((key == null) || ((key != null) && key.trim().equals("")))
            {
                String tmpKey = createAuthorYearKey();

                if (tmpKey != null)
                {
                    key = tmpKey;
                }
                else
                {
                    //logger.warn("Key could not bew generated for "+s);
                    System.out.println(
                        "WARN: Key could not bew generated for " + s);
                }
            }

            BibitemHolder.instance().putBibitem(key, this);
        }

        return tmpTags;
    }

    public void setID(int i)
    {
        id = "" + i;
    }

    public void setKey(String s)
    {
        key = s;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("@");
        stringbuffer.append(type);
        stringbuffer.append("{");
        stringbuffer.append(key);
        stringbuffer.append(",");
        stringbuffer.append(BibitemHolder.eol);

        writeTag(stringbuffer, TAG_AUTHOR, 60);
        writeTag(stringbuffer, TAG_TITLE, 60);
        writeTag(stringbuffer, TAG_JOURNAL, 60);
        writeTag(stringbuffer, TAG_YEAR, 60);

        String tag;

        for (Iterator iterator = tags.keySet().iterator(); iterator.hasNext();)
        {
            tag = (String) iterator.next();

            if (!tag.equals(TAG_AUTHOR) && !tag.equals(TAG_TITLE) &&
                    !tag.equals(TAG_JOURNAL) && !tag.equals(TAG_YEAR))
            {
                writeTag(stringbuffer, tag, 60);
            }
        }

        stringbuffer.append("}" + BibitemHolder.eol);

        return stringbuffer.toString();
    }

    private static String wrapAfter(String s, int numberOfDigits)
    {
        if (s.length() <= numberOfDigits)
        {
            return s;
        }

        if (spacesBeforeTagValue == null)
        {
            StringBuffer space = new StringBuffer(numSpacesBeforeTagValue);

            for (int i = 0; i < numSpacesBeforeTagValue; i++)
            {
                space.append(' ');
            }

            spacesBeforeTagValue = space.toString();
        }

        StringBuffer stringbuffer = new StringBuffer(s.replaceAll(
                    "[ \\t\\n\\r]+", " "));

        for (int j = s.length() - numberOfDigits; j > 0; j -= numberOfDigits)
        {
            j = stringbuffer.lastIndexOf(" ", j);

            if ((j <= 0) || (j <= 20))
            {
                break;
            }

            stringbuffer.insert(j, BibitemHolder.eol + spacesBeforeTagValue);
        }

        return stringbuffer.toString();
    }

    private String createAuthorYearKey()
    {
        String tmpKey = null;
        Pattern pattern = Pattern.compile("\\band\\b");

        if (tags.containsKey(TAG_AUTHOR))
        {
            String allAuthors = (String) tags.get(TAG_AUTHOR);
            String generatedKey;

            if (!allAuthors.replaceAll(" ", "").equals(""))
            {
                String[] authors = pattern.split(allAuthors);
                String oneAuthor = null;

                if (authors.length == 1)
                {
                    oneAuthor = authors[0].replaceAll(" [A-Z]*[ |\\.]", "");
                    generatedKey = stripMiddleNames(oneAuthor);

                    //System.out.println("One author: "+s1);
                    if (generatedKey.length() > 3)
                    {
                        generatedKey = generatedKey.substring(0, 3);
                    }
                }
                else
                {
                    StringBuffer initials = new StringBuffer(5);
                    String lastName;

                    for (int authorIdx = 0; authorIdx < authors.length;
                            authorIdx++)
                    {
                        //System.out.print(authors[i]+" AND ");
                        oneAuthor = authors[authorIdx].replaceAll(
                                " [A-Z]*[ |\\.]", "");
                        lastName = stripMiddleNames(oneAuthor);
                        initials.append(lastName.charAt(0));
                    }

                    generatedKey = initials.toString();
                }

                generatedKey = generatedKey.toLowerCase();
                tmpKey = generatedKey;
            }
            else
            {
                return null;
            }

            if (tags.keySet().contains(TAG_YEAR))
            {
                String year = tags.get(TAG_YEAR).toString();

                if (year.length() == 4)
                {
                    year = year.substring(2, 4);
                }

                tmpKey = generatedKey + year;

                for (int i = 0; i < letters.length; i++)
                {
                    String s5 = letters[i];

                    //System.out.println("contains "+tmpKey+"?: "+BibitemHolder.instance().containsKey(tmpKey + s5));
                    if (BibitemHolder.instance().containsKey(tmpKey + s5))
                    {
                        continue;
                    }

                    tmpKey = tmpKey + s5;
                    tmpKey = tmpKey.replaceAll(" ", "");

                    break;
                }
            }
        }

        return tmpKey;
    }

    private HashMap parseBibtexTags(List list, String string)
    {
        String[] stringArr = new String[list.size()];

        for (int i = 0; i < list.size(); i++)
        {
            stringArr[i] = (String) list.get(i);
        }

        if (stringArr.length == 0)
        {
            return null;
        }

        int index = stringArr[0].indexOf("{");
        String tmpType;

        if (index > 0)
        {
            tmpType = stringArr[0].substring(1, index);
        }
        else
        {
            return null;
        }

        String tmpKey = "";

        if (stringArr[0].length() >= 3)
        {
            tmpKey = stringArr[0].substring(index + 1,
                    stringArr[0].length() - 2);
        }

        HashMap hashmap = new HashMap();

        for (int k = 1; k < stringArr.length; k++)
        {
            String tmpString;

            if (k == (stringArr.length - 1))
            {
                tmpString = stringArr[k].replaceAll("\\}\\}", "\\}");
            }
            else
            {
                tmpString = stringArr[k];
            }

            int indexEqual = tmpString.indexOf("=");

            if (indexEqual >= 0)
            {
                hashmap.put(tmpString.substring(0, indexEqual).trim()
                    .toLowerCase(),
                    tmpString.substring(indexEqual + 1, tmpString.length())
                    .trim().replaceAll("^[\"\\{]|[\"\\}],?$|,$", ""));
            }
        }

        // store type and key
        key = tmpKey.trim();
        type = tmpType.trim();

        return hashmap;
    }

    private Vector separateBibtexTags(String s)
    {
        String[] as = s.split("\n");
        StringBuffer stringbuffer = null;
        stringbuffer = new StringBuffer();

        Vector v = new Vector();

        for (int i = 0; i < as.length; i++)
        {
            String s1 = as[i];
            int j;

            if (((j = s1.indexOf("%")) > 0) && (s1.charAt(j - 1) != '\\'))
            {
                s1 = s1.substring(0, j);
            }

            if (s1.indexOf("@") == 0)
            {
                stringbuffer.append(s1 + " ");

                if (s1.charAt(s1.length() - 1) != '}')
                {
                    v.add(stringbuffer.toString());
                    stringbuffer = new StringBuffer();
                }
            }
            else
            {
                s1 = s1.trim();
                stringbuffer.append(s1 + " ");

                if (s1.length() >= 2)
                {
                    String s2 = s1.substring(s1.length() - 2, s1.length());

                    if (s2.equals("},") || s2.equals("\","))
                    {
                        v.add(stringbuffer.toString());
                        stringbuffer = new StringBuffer();
                    }
                    else if (s1.indexOf("=") > 2)
                    {
                        String[] as1 = s1.split("=");

                        if (as1.length == 2)
                        {
                            as1[1] = as1[1].trim();

                            if ((s1.charAt(s1.length() - 1) == ',') &&
                                    (as1[1].charAt(0) != '"') &&
                                    (as1[1].charAt(0) != '{'))
                            {
                                v.add(stringbuffer.toString());
                                stringbuffer = new StringBuffer();
                            }
                        }
                    }
                }
            }
        }

        return v;
    }

    /*-------------------------------------------------------------------------*
     * private methods
     *------------------------------------------------------------------------- */
    private String stripMiddleNames(String author)
    {
        StringTokenizer stringtokenizer = new StringTokenizer(author, " ",
                false);
        String s8 = "";

        while (stringtokenizer.hasMoreTokens())
        {
            s8 = stringtokenizer.nextToken();

            if (s8.replaceAll(".", "").length() >= 3)
            {
                break;
            }
        }

        return s8.replaceAll(",", "");
    }

    private void writeTag(StringBuffer sb, String tag, int numberOfDigits)
    {
        StringBuffer space;

        if (spacesBeforeTag == null)
        {
            space = new StringBuffer(numSpacesBeforeTag);

            for (int i = 0; i < numSpacesBeforeTag; i++)
            {
                space.append(' ');
            }

            spacesBeforeTag = space.toString();
        }

        int max = numSpacesBeforeTagValue - numSpacesBeforeTag -
            tagValueSeparator.length() - tag.length();

        if (max <= 0)
        {
            space = new StringBuffer();
        }
        else
        {
            space = new StringBuffer(max);

            for (int i = 0; i < max; i++)
            {
                space.append(' ');
            }
        }

        if (tags.containsKey(tag))
        {
            String s = (String) tags.get(tag);
            sb.append(spacesBeforeTag);
            sb.append(tag);
            sb.append(tagValueSeparator);
            sb.append(space);
            sb.append(startValueChar);
            sb.append(wrapAfter(s, 60));
            sb.append(endValueChar);
            sb.append(",");
            sb.append(BibitemHolder.eol);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
