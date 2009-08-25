///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicAtomPropertyColoring.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:37 $
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
package joelib2.molecule.types;

import joelib2.data.BasicElementHolder;

import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import java.awt.Color;

import org.apache.log4j.Category;


/**
 * Some methods to faciliate the work with descriptors.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:37 $
 */
public class BasicAtomPropertyColoring implements AtomPropertyColoring
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicAtomPropertyColoring.class.getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    // variables for property coloring
    private AtomProperties data = null;
    private BasicElementHolder etab = BasicElementHolder.instance();
    private Color maxColor = new Color(1.0f, 0.0f, 0.0f);
    private double maxDataValue;
    private Color minColor = new Color(0.0f, 0.0f, 1.0f);
    private double minDataValue;
    private Molecule mol4coloring;
    private boolean usePropertyColoring = false;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the DescriptorHelper object
     *
     * @param  _value           Description of the Parameter
     * @param  _name            Description of the Parameter
     * @param  _representation  Description of the Parameter
     */
    public BasicAtomPropertyColoring()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Color getAtomColor(Atom atom)
    {
        if (atom == null)
        {
            return null;
        }

        if ((mol4coloring != null) && (mol4coloring != atom.getParent()))
        {
            logger.warn("Coloring (" + mol4coloring.getTitle() +
                ") should be newly initialized for molecule " +
                atom.getParent().getTitle());
        }

        if (usePropertyColoring)
        {
            double delta = maxDataValue - minDataValue;

            //System.out.println("data:"+data);
            float val = (float) ((data.getDoubleValue(atom.getIndex()) -
                        minDataValue) / delta);
            float r = (((maxColor.getRed() - minColor.getRed()) * val) +
                    minColor.getRed()) / 255.0f;
            float g = (((maxColor.getGreen() - minColor.getGreen()) * val) +
                    minColor.getGreen()) / 255.0f;
            float b = (((maxColor.getBlue() - minColor.getBlue()) * val) +
                    minColor.getBlue()) / 255.0f;

            //        System.out.println(""+atom.getIdx()+": "+data.getDoubleValue(atom.getIdx())+": "+val);
            //        System.out.println("rgb:"+r+" "+g+" "+b);
            return new Color(r, g, b);
        }
        else
        {
            int atomNum = atom.getAtomicNumber();

            return etab.getColor(atomNum);
        }
    }

    public Molecule getMoleculeForColoring()
    {
        return mol4coloring;
    }

    public void useAtomPropertyColoring(Molecule mol, String property)
    {
        if ((property == null) || (mol == null))
        {
            usePlainColoring();

            return;
        }

        mol4coloring = mol;

        FeatureResult result = null;

        try
        {
            result = FeatureHelper.instance().featureFrom(mol, property);
        }
        catch (FeatureException e)
        {
            logger.error(e.toString());
            logger.error("Use plain atom coloring.");
            usePropertyColoring = false;
        }

        if (result == null)
        {
            logger.error("Can't get atom property " + property +
                " for atom coloring in " + this.getClass().getName());
        }

        //      System.out.println(""+JOEHelper.hasInterface(genericData, "joelib2.molecule.types.AtomProperties"));
        //      System.out.println(""+(genericData instanceof AtomDynamicResult));
        //      System.out.println(""+(genericData instanceof PairData));
        if (result instanceof AtomProperties)
        {
            data = (AtomProperties) result;
            minDataValue = Double.MAX_VALUE;
            maxDataValue = -Double.MAX_VALUE;

            double value;

            for (int i = 1; i <= mol.getAtomsSize(); i++)
            {
                value = data.getDoubleValue(i);

                if (value > maxDataValue)
                {
                    maxDataValue = value;
                }

                if (value < minDataValue)
                {
                    minDataValue = value;
                }

                //          System.out.println(""+i+": "+value);
            }

            usePropertyColoring = true;
        }
        else
        {
            logger.error("Data for atom coloring has wrong format in " +
                this.getClass().getName());
        }
    }

    public void usePlainColoring()
    {
        usePropertyColoring = false;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
