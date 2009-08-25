///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MolInstance.java,v $
//  Purpose:  Molecule Weka instance.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.8 $
//            $Date: 2005/02/17 16:48:28 $
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
package joelib2.algo.datamining.weka;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.AtomIterator;

import weka.core.Instance;


/**
 * Molecule Weka instance.
 *
 * @.author    wegnerj
 * @.wikipedia QSAR
 * @.wikipedia Data mining
 * @.license    GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:28 $
 */
public class MolInstance extends Instance
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    protected static final BasicIOType smiles = BasicIOTypeHolder.instance()
                                                                 .getIOType(
            "SMILES");

    //~ Instance fields ////////////////////////////////////////////////////////

    /** The instance identifier. */
    protected Molecule mol;
    protected boolean showCoords = true;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Constructor that copies the attribute values and the weight from
     * the given instance. Reference to the dataset is set to null.
     * (ie. the instance doesn't have access to information about the
     * attribute types)
     *
     * @param instance the instance from which the attribute
     * values and the weight are to be copied
     */
    public MolInstance(MolInstance instance)
    {
        m_AttValues = instance.m_AttValues;
        m_Weight = instance.m_Weight;
        m_Dataset = null;
        mol = instance.mol;
    }

    /**
     * Constructor of an instance that sets weight to one, all values to
     * be missing, and the reference to the dataset to null. (ie. the instance
     * doesn't have access to information about the attribute types)
     *
     * @param numAttributes the size of the instance
     */
    public MolInstance(int numAttributes)
    {
        m_AttValues = new double[numAttributes];

        for (int i = 0; i < m_AttValues.length; i++)
        {
            m_AttValues[i] = MISSING_VALUE;
        }

        m_Weight = 1;
        m_Dataset = null;
        mol = null;
    }

    /**
     * Constructor that inititalizes instance variable with given
     * values. Reference to the dataset is set to null. (ie. the instance
     * doesn't have access to information about the attribute types)
     *
     * @param _identifier the instance's identifier
     * @param weight the instance's weight
     * @param attValues a vector of attribute values
     */
    public MolInstance(Molecule _mol, double weight, double[] attValues)
    {
        m_AttValues = attValues;
        m_Weight = weight;
        m_Dataset = null;
        mol = _mol;
    }

    protected MolInstance()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Produces a shallow copy of this instance. The copy has
     * access to the same dataset. (if you want to make a copy
     * that doesn't have access to the dataset, use
     * <code>new Instance(instance)</code>
     *
     * @return the shallow copy
     */
    public Object copy()
    {
        MolInstance result = new MolInstance(this);
        result.m_Dataset = m_Dataset;

        return result;
    }

    /**
     * Returns molecule.
     */
    public Molecule getMolecule()
    {
        return mol;
    }

    /**
     * Sets molecule.
     */
    public void setMolecule(Molecule _mol)
    {
        mol = _mol;
    }

    /**
     * Returns the description of one instance. If the instance
     * doesn't have access to a dataset, it returns the internal
     * floating-point values. Quotes string
     * values that contain whitespace characters.
     *
     * @return the instance's description as a string
     */
    public String toString()
    {
        StringBuffer text = new StringBuffer();

        // create H depleted molecule
        Molecule molHdepleted = (Molecule) mol.clone(false);
        molHdepleted.deleteHydrogens();

        // add SMILES
        text.append(molHdepleted.toString(smiles).trim());
        text.append(',');

        // add title
        String title = molHdepleted.getTitle();
        title = title.replace(',', '_');

        if ((title == null) || (title.trim().length() == 0))
        {
            text.append("unkown");
        }
        else
        {
            text.append(title);
        }

        if (showCoords)
        {
            // add heavy atom coordinates
            Atom atom;
            AtomIterator ait = molHdepleted.atomIterator();

            while (ait.hasNext())
            {
                atom = ait.nextAtom();
                text.append(',');
                text.append(atom.get3Dx());
                text.append(',');
                text.append(atom.get3Dy());
                text.append(',');
                text.append(atom.get3Dz());
            }
        }

        // add attributes
        text.append(',');

        for (int i = 0; i < m_AttValues.length; i++)
        {
            if (i > 0)
            {
                text.append(",");
            }

            text.append(toString(i));
        }

        //System.out.println(molHdepleted);
        return text.toString();
    }

    /**
     * @return Returns the showCoords.
     */
    protected boolean isShowCoords()
    {
        return showCoords;
    }

    /**
     * @param showCoords The showCoords to set.
     */
    protected void setShowCoords(boolean showCoords)
    {
        this.showCoords = showCoords;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
