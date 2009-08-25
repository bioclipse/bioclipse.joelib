///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: JPanel3D.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:33 $
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
package joelib2.gui.render3D.graphics3D;

import joelib2.gui.render3D.util.MolViewerEventListener;

import joelib2.molecule.Molecule;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.j3d.Canvas3D;
import javax.media.j3d.GraphicsConfigTemplate3D;

import javax.swing.JPanel;


/**
 * JPanel for Java3D molecule viewer.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class JPanel3D extends JPanel
{
    //~ Instance fields ////////////////////////////////////////////////////////

    Canvas3D moleculeCanvas;
    MolecularScene mScene;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the Panel3D object
     */
    public JPanel3D()
    {
        GraphicsConfigTemplate3D tmpl = new GraphicsConfigTemplate3D();
        GraphicsEnvironment env = GraphicsEnvironment
            .getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getBestConfiguration(tmpl);

        setLayout(new BorderLayout());

        moleculeCanvas = new Canvas3D(config);
        mScene = new MolecularScene(moleculeCanvas);
        add(moleculeCanvas, BorderLayout.CENTER);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Adds a feature to the Molecule attribute of the Panel3D object
     *
     * @param m  The feature to be added to the Molecule attribute
     */
    public void addMolecule(Molecule mol)
    {
        moleculeCanvas.stopRenderer();
        mScene.addMolecule(mol);
        moleculeCanvas.startRenderer();
    }

    public void addMolViewerEventListener(MolViewerEventListener l)
    {
        mScene.addMolViewerEventListener(l);
    }

    /**
     * Sets the fast attribute of the JPanel3D object
     *
     * Sets the nice attribute of the JPanel3D object
     *
     * Description of the Method
     *
     */
    public void clear()
    {
        mScene.clear();
    }

    public void removeMolecule(Molecule mol)
    {
        moleculeCanvas.stopRenderer();
        mScene.removeMolecule(mol);
        moleculeCanvas.startRenderer();
    }

    /**
     * Remove an answer listener from our list of interested listeners
     */
    public void removeMolViewerEventListener(MolViewerEventListener l)
    {
        mScene.removeMolViewerEventListener(l);
    }

    /**
     * Sets the renderStyle attribute of the Panel3D object
     *
     * @param style  The new renderStyle value
     */
    public void setRenderStyle(int style)
    {
        mScene.setRenderStyle(style);
    }

    public void useAtomPropertyColoring(String atomPropertyName)
    {
        mScene.useAtomPropertyColoring(atomPropertyName);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
