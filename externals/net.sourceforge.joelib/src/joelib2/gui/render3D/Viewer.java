///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Viewer.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:32 $
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
package joelib2.gui.render3D;

import joelib2.gui.render3D.util.Java3DHelper;

import joelib2.molecule.Molecule;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.UIManager;

import org.apache.log4j.Category;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:32 $
 */
public class Viewer
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(Viewer.class
            .getName());

    /**
     * Description of the Field
     */
    public static Viewer viewer;

    //~ Instance fields ////////////////////////////////////////////////////////

    boolean packFrame = false;
    private ViewerFrame frame;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Construct the application
     */
    public Viewer()
    {
        this(null, null);
    }

    public Viewer(String inputFile)
    {
        this(null, inputFile);
    }

    /**
     *Constructor for the Viewer object
     *
     * @param type       Description of the Parameter
     * @param inputFile  Description of the Parameter
     */
    public Viewer(String type, String inputFile)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        frame = new ViewerFrame(type, inputFile);

        //Validate frames that have preset sizes
        //Pack frames that have useful preferred size info, e.g. from their layout
        if (packFrame)
        {
            frame.pack();
        }
        else
        {
            frame.validate();
        }

        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();

        if (frameSize.height > screenSize.height)
        {
            frameSize.height = screenSize.height;
        }

        if (frameSize.width > screenSize.width)
        {
            frameSize.width = screenSize.width;
        }

        frame.setLocation((screenSize.width - frameSize.width) / 2,
            (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    public static synchronized Viewer instance()
    {
        if (viewer == null)
        {
            viewer = new Viewer();
        }

        viewer.frame.setVisible(true);

        return viewer;
    }

    /**
     *Main method
     *
     * @param args  The command line arguments
     */
    public static void main(String[] args)
    {
        if (Java3DHelper.configOK())
        {
            try
            {
                UIManager.setLookAndFeel(UIManager
                    .getSystemLookAndFeelClassName());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if (args.length == 2)
            {
                new Viewer(args[0], args[1]);
            }
            else if (args.length == 1)
            {
                new Viewer(args[0]);
            }
            else
            {
                new Viewer();
            }
        }
        else
        {
            logger.error("Java3D can't be properly configured.");
            logger.error(
                "The graphics board is too old or the driver isn't properly configured.");
            System.exit(0);
        }
    }

    /**
     * Adds a feature to the Molecule attribute of the Viewer object
     *
     * @param mol  The feature to be added to the Molecule attribute
     */
    public void addMolecule(Molecule mol)
    {
        frame.addMolecule(mol);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
