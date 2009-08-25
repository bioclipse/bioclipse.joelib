/*
 * Copyright (c) 2002 Sun Microsystems, Inc. All  Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * -Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduct the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or intended for
 * use in the design, construction, operation or maintenance of any nuclear
 * facility.
 */
package joelib2.gui.util;

import joelib2.io.BasicIOType;
import joelib2.io.MoleculeFileIO;

import java.io.File;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;


/**
 * A convenience implementation of FileFilter that filters out
 * all files except for those type extensions that it knows about.
 *
 * Extensions are of the type ".foo", which is typically found on
 * Windows and Unix boxes, but not on Macinthosh. Case is ignored.
 *
 * Example - create a new filter that filerts out all files
 * but gif and jpg image files:
 *
 *     JFileChooser chooser = new JFileChooser();
 *     MolFileFilter filter = new MolFileFilter(
 *                   new String{"sdf", "mol"}, "Structured Data File")
 *     chooser.addChoosableFileFilter(filter);
 *     chooser.showOpenDialog(this);
 *
 * @version 1.13 06/13/02
 * @.author Jeff Dinkins
 * @.author wegnerj
 */
public class MolFileFilter extends FileFilter
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private String description = null;

    //private static String TYPE_UNKNOWN = "Type Unknown";
    //private static String HIDDEN_FILE = "Hidden File";
    private Hashtable filters = null;
    private String fullDescription = null;
    private BasicIOType ioType;
    private boolean useExtensionsInDescription = true;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Creates a file filter. If no filters are added, then all
     * files are accepted.
     *
     * @see #addExtension
     */
    public MolFileFilter()
    {
        this.filters = new Hashtable();
    }

    /**
     * Creates a file filter that accepts files with the given extension.
     * Example: new ExampleFileFilter("jpg");
     *
     * @see #addExtension
     */
    public MolFileFilter(String extension)
    {
        this(extension, null);
    }

    /**
     * Creates a file filter from the given string array.
     * Example: new ExampleFileFilter(String {"gif", "jpg"});
     *
     * Note that the "." before the extension is not needed adn
     * will be ignored.
     *
     * @see #addExtension
     */
    public MolFileFilter(String[] filters)
    {
        this(filters, null);
    }

    /**
     * Creates a file filter that accepts the given file type.
     * Example: new ExampleFileFilter("jpg", "JPEG Image Images");
     *
     * Note that the "." before the extension is not needed. If
     * provided, it will be ignored.
     *
     * @see #addExtension
     */
    public MolFileFilter(String extension, String description)
    {
        this();

        if (extension != null)
        {
            addExtension(extension);
        }

        if (description != null)
        {
            setDescription(description);
        }
    }

    /**
     * Creates a file filter from the given string array and description.
     * Example: new ExampleFileFilter(String {"gif", "jpg"}, "Gif and JPG Images");
     *
     * Note that the "." before the extension is not needed and will be ignored.
     *
     * @see #addExtension
     */
    public MolFileFilter(String[] filters, String description)
    {
        this();

        for (int i = 0; i < filters.length; i++)
        {
            // add filters one by one
            addExtension(filters[i]);
        }

        if (description != null)
        {
            setDescription(description);
        }
    }

    /**
     * Creates a file filter. If no filters are added, then all
     * files are accepted.
     *
     * @see #addExtension
     */
    public MolFileFilter(BasicIOType _ioType, MoleculeFileIO mfType,
        boolean forReading, boolean forWriting)
    {
        this.filters = new Hashtable();

        ioType = _ioType;

        String[] extensions = null;

        if (mfType != null)
        {
            if (mfType.readable() && forReading)
            {
                if ((mfType.inputDescription() != null) &&
                        (mfType.inputFileExtensions() != null))
                {
                    extensions = mfType.inputFileExtensions();

                    for (int i = 0; i < extensions.length; i++)
                    {
                        addExtension(extensions[i]);
                    }

                    setDescription(mfType.inputDescription());
                }
            }

            if (mfType.writeable() && forWriting)
            {
                if ((mfType.outputDescription() != null) &&
                        (mfType.outputFileExtensions() != null))
                {
                    extensions = mfType.outputFileExtensions();

                    for (int i = 0; i < extensions.length; i++)
                    {
                        addExtension(extensions[i]);
                    }

                    setDescription(mfType.outputDescription());
                }
            }
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Return true if this file should be shown in the directory pane,
     * false if it shouldn't.
     *
     * Files that begin with "." are ignored.
     *
     * @see #getExtension
     * @see javax.swing.filechooser.FileFilter#accept(File)
     */
    public boolean accept(File f)
    {
        if (f != null)
        {
            if (f.isDirectory())
            {
                return true;
            }

            String extension = getExtension(f);

            if ((extension != null) && (filters.get(getExtension(f)) != null))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds a filetype "dot" extension to filter against.
     *
     * For example: the following code will create a filter that filters
     * out all files except those that end in ".jpg" and ".tif":
     *
     *   ExampleFileFilter filter = new ExampleFileFilter();
     *   filter.addExtension("jpg");
     *   filter.addExtension("tif");
     *
     * Note that the "." before the extension is not needed and will be ignored.
     */
    public void addExtension(String extension)
    {
        if (filters == null)
        {
            filters = new Hashtable(5);
        }

        filters.put(extension.toLowerCase(), this);
        fullDescription = null;
    }

    /**
     * Returns the human readable description of this filter. For
     * example: "JPEG and GIF Image Files (*.jpg, *.gif)"
     *
     * @see #setDescription(String)
     * @see #setExtensionListInDescription(boolean)
     * @see #isExtensionListInDescription()
     * @see #getDescription()
     */
    public String getDescription()
    {
        if (fullDescription == null)
        {
            if ((description == null) || isExtensionListInDescription())
            {
                fullDescription = (description == null) ? "("
                                                        : (description + " (");

                // build the description from the extension list
                Enumeration extensions = filters.keys();

                if (extensions != null)
                {
                    fullDescription += ("." +
                            (String) extensions.nextElement());

                    while (extensions.hasMoreElements())
                    {
                        fullDescription += (", ." +
                                (String) extensions.nextElement());
                    }
                }

                fullDescription += ")";
            }
            else
            {
                fullDescription = description;
            }
        }

        return fullDescription;
    }

    /**
     * Return the extension portion of the file's name .
     *
     * @see #getExtension
     * @see FileFilter#accept
     */
    public String getExtension(File f)
    {
        if (f != null)
        {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');

            if ((i > 0) && (i < (filename.length() - 1)))
            {
                return filename.substring(i + 1).toLowerCase();
            }
        }

        return null;
    }

    public BasicIOType getIOType()
    {
        return ioType;
    }

    /**
     * Returns whether the extension list (.jpg, .gif, etc) should
     * show up in the human readable description.
     *
     * Only relevent if a description was provided in the constructor
     * or using setDescription();
     *
     * @see #getDescription()
     * @see #setDescription(String)
     * @see #setExtensionListInDescription(boolean)
     */
    public boolean isExtensionListInDescription()
    {
        return useExtensionsInDescription;
    }

    /**
     * Sets the human readable description of this filter. For
     * example: filter.setDescription("Gif and JPG Images");
     *
     * @see #setDescription(String)
     * @see #setExtensionListInDescription(boolean)
     * @see #isExtensionListInDescription()
     */
    public void setDescription(String description)
    {
        this.description = description;
        fullDescription = null;
    }

    /**
     * Determines whether the extension list (.jpg, .gif, etc) should
     * show up in the human readable description.
     *
     * Only relevent if a description was provided in the constructor
     * or using setDescription();
     *
     * @see #getDescription()
     * @see #setDescription(String)
     * @see #isExtensionListInDescription()
     */
    public void setExtensionListInDescription(boolean b)
    {
        useExtensionsInDescription = b;
        fullDescription = null;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
