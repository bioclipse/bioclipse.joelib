///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SMILESResult.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:30 $
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
package joelib2.feature.result;

import joelib2.feature.FeatureResult;

import joelib2.io.IOType;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairDataCML;
import joelib2.molecule.types.PairData;

import joelib2.smiles.SMILESGenerator;
import joelib2.smiles.SMILESParser;

import org.apache.log4j.Category;


/**
 * String array results of variable size.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:30 $
 */
public class SMILESResult extends BasicPairDataCML implements Cloneable,
    FeatureResult, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(SMILESResult.class
            .getName());
    private final static String basicFormat = "SMILES pattern";

    //~ Instance fields ////////////////////////////////////////////////////////

    protected Molecule molecule;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the IntArrayResult object
     */
    public SMILESResult()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        SMILESResult newObj = new SMILESResult();

        return clone(newObj);
    }

    public SMILESResult clone(SMILESResult other)
    {
        super.clone(other);

        if (molecule != null)
        {
            other.molecule = (Molecule) molecule.clone();
        }

        return other;
    }

    /**
     *  Description of the Method
     *
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
     */
    public String formatDescription(IOType ioType)
    {
        return basicFormat;
    }

    /**
     *  Description of the Method
     *
     * @param pairData  Description of the Parameter
     * @param ioType    Description of the Parameter
     * @return          Description of the Return Value
     */
    public boolean fromPairData(IOType ioType, PairData pairData)
    {
        this.setKey(pairData.getKey());

        Object value = pairData.getKeyValue();
        boolean success = false;

        if ((value != null) && (value instanceof String))
        {
            success = fromString(ioType, (String) value);
        }

        return success;
    }

    /**
     *  Description of the Method
     *
     * @param sValue  Description of the Parameter
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
     */
    public boolean fromString(IOType ioType, String sValue)
    {
        molecule = new BasicConformerMolecule();

        if (!SMILESParser.smiles2molecule(molecule, sValue, null))
        {
            return false;
        }

        return true;
    }

    /**
     *  Gets the double attribute of the IntArrayResult object
     *
     * @return   The double value
     */
    public Molecule getMolecule()
    {
        return molecule;
    }

    public boolean init(String _descName)
    {
        this.setKey(_descName);

        return true;
    }

    /**
     *  Sets the double attribute of the IntArrayResult object
     *
     * @param _iarray  The new double value
     */
    public void setMolecule(Molecule molecule)
    {
        this.molecule = molecule;
    }

    /**
     *  Description of the Method
     *
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
     */
    public String toString(IOType ioType)
    {
        SMILESGenerator m2s = new SMILESGenerator();
        m2s.init();

        StringBuffer smiles = new StringBuffer(1000);
        m2s.correctAromaticAmineCharge(molecule);
        m2s.createSmiString(molecule, smiles);

        return smiles.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
