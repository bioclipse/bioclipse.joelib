///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MolFileChooser.java,v $
//  Purpose:  Connection to the tools of the Chemical Development Kit (CDK).
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:34 $
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
package joelib2.gui.util;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import java.util.Enumeration;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Category;


/**
 *
 * @.author     wegnerj
 * @.license GPL
 */
public class MolFileChooser
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(MolFileChooser.class
            .getName());
    private static MolFileChooser instance;

    //~ Instance fields ////////////////////////////////////////////////////////

    private String defaultFileFilter = null;

    private JFileChooser loadChooser;
    private JFileChooser saveChooser;

    //~ Constructors ///////////////////////////////////////////////////////////

    //  private String defaultFileFilter = "SDF";

    /**
     *  Constructor for the CDKTools.
     *
     */
    private MolFileChooser()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        loadChooser = new JFileChooser();
        saveChooser = new JFileChooser();
        init();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static synchronized MolFileChooser instance()
    {
        if (instance == null)
        {
            instance = new MolFileChooser();
        }

        return instance;
    }

    public JFileChooser getLoadFileChooser()
    {
        return loadChooser;
    }

    public JFileChooser getSaveFileChooser()
    {
        return saveChooser;
    }

    public void init()
    {
        MolFileFilter filter = null;
        BasicIOType ioType;
        FileFilter defaultLoadFilter = loadChooser.getFileFilter();
        FileFilter defaultSaveFilter = saveChooser.getFileFilter();

        for (Enumeration e = BasicIOTypeHolder.instance().getFileTypes();
                e.hasMoreElements();)
        {
            ioType = (BasicIOType) e.nextElement();

            MoleculeFileIO mfType = null;

            try
            {
                mfType = MoleculeFileHelper.getMoleculeFileType(ioType);
            }
            catch (MoleculeIOException ex)
            {
                logger.error(ex.getMessage());
            }

            if (mfType != null)
            {
                if (mfType.readable())
                {
                    filter = new MolFileFilter(ioType, mfType, true, false);
                    loadChooser.setFileFilter(filter);

                    if (defaultFileFilter != null)
                    {
                        if (ioType.equals(
                                    BasicIOTypeHolder.instance().getIOType(
                                        defaultFileFilter)))
                        {
                            defaultLoadFilter = filter;
                        }
                    }
                }

                if (mfType.writeable())
                {
                    filter = new MolFileFilter(ioType, mfType, false, true);
                    saveChooser.setFileFilter(filter);

                    if (defaultFileFilter != null)
                    {
                        if (ioType.equals(
                                    BasicIOTypeHolder.instance().getIOType(
                                        defaultFileFilter)))
                        {
                            defaultSaveFilter = filter;
                        }
                    }
                }
            }
        }

        loadChooser.setFileFilter(defaultLoadFilter);
        saveChooser.setFileFilter(defaultSaveFilter);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
