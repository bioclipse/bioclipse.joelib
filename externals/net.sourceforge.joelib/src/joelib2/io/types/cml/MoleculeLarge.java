///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MoleculeLarge.java,v $
//  Purpose:  Reader/Writer for CML files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.11 $
//            $Date: 2005/03/03 07:13:50 $
//            $Author: wegner $
//  Original Author: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
//  Original Version: Chemical Development Kit,  http://sourceforge.net/projects/cdk
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
package joelib2.io.types.cml;

import joelib2.feature.result.AtomDoubleResult;

import joelib2.math.BasicVector3D;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import joelib2.util.types.StringInt;

import java.io.PrintStream;

import java.util.Hashtable;
import java.util.Map;


/**
 * CML molecule writer which uses some arrays to avoid overhead.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license GPL
 * @.cvsversion    $Revision: 1.11 $, $Date: 2005/03/03 07:13:50 $
 * @.cite rr99b
 * @.cite mr01
 * @.cite gmrw01
 * @.cite wil01
 * @.cite mr03
 * @.cite mrww04
 */
public class MoleculeLarge extends CMLMoleculeWriterBase
{
    //~ Constructors ///////////////////////////////////////////////////////////

    public MoleculeLarge(PrintStream _output, CMLWriterProperties _writerProp)
    {
        super(_output, _writerProp);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    protected synchronized void write2D(BasicVector3D p, AtomDoubleResult x,
        AtomDoubleResult y, int index)
    {
        if ((x != null) && (y != null))
        {
            Map<String, String> attributes = new Hashtable<String, String>();
            attributes.put("builtin", "xy2");
            writeOpenTag(output, writerProp, "coordinate2", attributes);
            write(output, x.getStringValue(index));
            write(output, " ");
            write(output, y.getStringValue(index));
            writeCloseTag(output, writerProp, "coordinate2");
        }
        else if (p != null)
        {
            Map<String, String> attributes = new Hashtable<String, String>();
            attributes.put("builtin", "xy2");
            writeOpenTag(output, writerProp, "coordinate2", attributes);
            write(output, Double.toString(p.getX3D()));
            write(output, " ");
            write(output, Double.toString(p.getY3D()));
            writeCloseTag(output, writerProp, "coordinate2");
        }
    }

    /**
     * Description of the Method
     *
     * @param p  Description of the Parameter
     */
    protected synchronized void write3D(BasicVector3D p)
    {
        if (p != null)
        {
            Map<String, String> attributes = new Hashtable<String, String>();
            attributes.put("builtin", "xyz3");
            writeOpenTag(output, writerProp, "coordinate3", attributes);
            write(output, Double.toString(p.getX3D()));
            write(output, " ");
            write(output, Double.toString(p.getY3D()));
            write(output, " ");
            write(output, Double.toString(p.getZ3D()));
            writeCloseTag(output, writerProp, "coordinate3");
        }
    }

    protected void writeAtoms(Molecule mol, String molID, boolean has3D,
        boolean has2D, AtomDoubleResult adrX, AtomDoubleResult adrY,
        Map<String, StringInt> atomIDs)
    {
        Atom atom;
        AtomIterator ait = mol.atomIterator();
        StringInt atomID;
        int index = 0;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            atomID = (StringInt) atomIDs.get(molID + ":" + atom.getIndex());

            //System.out.println("get "+molID+":"+atom.getIdx()+"= id '"+ atomID.s+"'");
            writeAtom(atom, atomID, has2D, has3D, adrX, adrY);
            index++;
        }
    }

    /**
     * Description of the Method
     *
     * @param bond  Description of the Parameter
     */
    protected synchronized void writeBond(String molID, Bond bond,
        StringInt bondID, Map<String, StringInt> atomIDs)
    {
        Map<String, String> attributes = new Hashtable<String, String>();

        attributes.put("id", bondID.getStringValue());
        writeOpenTag(output, writerProp, "bond", attributes);

        Atom beginAtom = bond.getBegin();
        Atom endAtom = bond.getEnd();
        StringInt beginAtomID = (StringInt) atomIDs.get(molID + ":" +
                beginAtom.getIndex());
        StringInt endAtomID = (StringInt) atomIDs.get(molID + ":" +
                endAtom.getIndex());
        attributes.clear();
        attributes.put("builtin", "atomRefs");
        writeOpenTag(output, writerProp, "string", attributes, false);
        write(output, beginAtomID.getStringValue());
        write(output, " ");
        write(output, endAtomID.getStringValue());
        writeCloseTag(output, writerProp, "string");

        this.writeBondOrder(bond);
        this.writeBondStereo(bond);

        writeCloseTag(output, writerProp, "bond");
    }

    protected void writeBonds(Molecule mol, String molID,
        Map<String, StringInt> atomIDs, Map<String, StringInt> bondIDs)
    {
        BondIterator bit = mol.bondIterator();
        Bond bond;
        StringInt bondID;
        int index = 0;

        while (bit.hasNext())
        {
            bond = bit.nextBond();
            bondID = (StringInt) bondIDs.get(molID + ":" + bond.getIndex());

            //System.out.println("get "+molID+":"+bond.getIdx()+"= id '"+ bondID.s+"'");
            writeBond(molID, bond, bondID, atomIDs);
            index++;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
