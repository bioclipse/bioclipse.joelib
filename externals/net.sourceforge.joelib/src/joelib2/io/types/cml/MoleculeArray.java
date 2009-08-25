///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MoleculeArray.java,v $
//  Purpose:  Reader/Writer for CML files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.12 $
//            $Date: 2005/02/17 16:48:35 $
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

import joelib2.feature.types.atomlabel.AtomExplicitHydrogenCount;
import joelib2.feature.types.atomlabel.AtomImplicitValence;
import joelib2.feature.types.atomlabel.AtomPartialCharge;

import joelib2.math.Vector3D;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.BondHelper;
import joelib2.molecule.IsomerismHelper;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import joelib2.util.types.BasicStringInt;
import joelib2.util.types.StringInt;

import java.io.PrintStream;

import java.util.Hashtable;
import java.util.Map;


/**
 * CML molecule array writer.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license GPL
 * @.cvsversion    $Revision: 1.12 $, $Date: 2005/02/17 16:48:35 $
 * @.cite rr99b
 * @.cite mr01
 * @.cite gmrw01
 * @.cite wil01
 * @.cite mr03
 * @.cite mrww04
 */
public class MoleculeArray extends CMLMoleculeWriterBase
{
    //~ Constructors ///////////////////////////////////////////////////////////

    public MoleculeArray(PrintStream _output, CMLWriterProperties _writerProp)
    {
        super(_output, _writerProp);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    protected void writeAtoms(Molecule mol, String molID, boolean has3D,
        boolean has2D, AtomDoubleResult adrX, AtomDoubleResult adrY,
        Map<String, StringInt> atomIDs)
    {
        int size = 10 * mol.getAtomsSize();
        StringBuffer aids = new StringBuffer(size);
        StringBuffer elements = new StringBuffer(size);
        StringBuffer x2 = new StringBuffer(size);
        StringBuffer y2 = new StringBuffer(size);
        StringBuffer x3 = new StringBuffer(size);
        StringBuffer y3 = new StringBuffer(size);
        StringBuffer z3 = new StringBuffer(size);
        StringBuffer partCharge = new StringBuffer(size);
        StringBuffer formalCharge = new StringBuffer(size);
        StringBuffer impValence = new StringBuffer(size);
        StringBuffer isotope = new StringBuffer(size);
        boolean hasIsotopes = false;
        Atom atom;
        AtomIterator ait = mol.atomIterator();
        Vector3D xyz;
        int index = 0;
        StringInt atomID;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            index++;
            xyz = atom.getCoords3D();
            atomID = (BasicStringInt) atomIDs.get(molID + ":" + atom.getIndex());
            aids.append(atomID.getStringValue());
            elements.append(atom);

            if (has3D)
            {
                x3.append(xyz.getX3D());
                y3.append(xyz.getY3D());
                z3.append(xyz.getZ3D());
            }

            if (has2D)
            {
                if ((adrX != null) && (adrY != null))
                {
                    x2.append(adrX.getStringValue(index));
                    y2.append(adrY.getStringValue(index));
                }
                else
                {
                    x2.append(xyz.getX3D());
                    y2.append(xyz.getY3D());
                }
            }

            formalCharge.append(atom.getFormalCharge());

            if (writerProp.writePartialCharge())
            {
                partCharge.append(AtomPartialCharge.getPartialCharge(atom));
            }

            if (writerProp.writeImpliciteHydrogens())
            {
                impValence.append(AtomImplicitValence.getImplicitValence(atom) +
                    AtomExplicitHydrogenCount.getIntValue(atom));
            }

            if (atom.getIsotope() != 0)
            {
                hasIsotopes = true;
            }

            isotope.append(atom.getIsotope());

            if (ait.hasNext())
            {
                aids.append(' ');
                elements.append(' ');

                if (has3D)
                {
                    x3.append(' ');
                    y3.append(' ');
                    z3.append(' ');
                }

                if (has2D)
                {
                    x2.append(' ');
                    y2.append(' ');
                }

                formalCharge.append(' ');

                if (writerProp.writePartialCharge())
                {
                    partCharge.append(' ');
                }

                if (writerProp.writeImpliciteHydrogens())
                {
                    impValence.append(' ');
                }

                isotope.append(' ');
            }
        }

        writeOpenTag(output, writerProp, "atomArray");

        Map<String, String> attributes = new Hashtable<String, String>();
        attributes.put("builtin", "id");
        writeOpenTag(output, writerProp, "stringArray", attributes, false);
        write(output, aids.toString());
        writeCloseTag(output, writerProp, "stringArray");

        attributes.clear();
        attributes.put("builtin", "elementType");
        writeOpenTag(output, writerProp, "stringArray", attributes, false);
        write(output, elements.toString());
        writeCloseTag(output, writerProp, "stringArray");

        if (has3D)
        {
            attributes.clear();
            attributes.put("builtin", "x3");
            writeOpenTag(output, writerProp, "floatArray", attributes, false);
            write(output, x3.toString());
            writeCloseTag(output, writerProp, "floatArray");

            attributes.clear();
            attributes.put("builtin", "y3");
            writeOpenTag(output, writerProp, "floatArray", attributes, false);
            write(output, y3.toString());
            writeCloseTag(output, writerProp, "floatArray");

            attributes.clear();
            attributes.put("builtin", "z3");
            writeOpenTag(output, writerProp, "floatArray", attributes, false);
            write(output, z3.toString());
            writeCloseTag(output, writerProp, "floatArray");
        }

        if (has2D)
        {
            attributes.clear();
            attributes.put("builtin", "x2");
            writeOpenTag(output, writerProp, "floatArray", attributes, false);
            write(output, x2.toString());
            writeCloseTag(output, writerProp, "floatArray");

            attributes.clear();
            attributes.put("builtin", "y2");
            writeOpenTag(output, writerProp, "floatArray", attributes, false);
            write(output, y2.toString());
            writeCloseTag(output, writerProp, "floatArray");
        }

        attributes.clear();
        attributes.put("builtin", "formalCharge");
        writeOpenTag(output, writerProp, "floatArray", attributes, false);
        write(output, formalCharge.toString());
        writeCloseTag(output, writerProp, "floatArray");

        if (writerProp.writePartialCharge())
        {
            //CML1
            attributes.clear();
            attributes.put("builtin", "partialCharge");
            writeOpenTag(output, writerProp, "floatArray", attributes, false);
            write(output, partCharge.toString());
            writeCloseTag(output, writerProp, "floatArray");

            // CML2
            //<array dataType="xsd:float" dictRef="joelib:partialCharge" units="units:electron">.234 1,456 4.678</scalar>
        }

        if (writerProp.writeImpliciteHydrogens())
        {
            attributes.clear();
            attributes.put("builtin", "hydrogenCount");
            writeOpenTag(output, writerProp, "integerArray", attributes, false);
            write(output, impValence.toString());
            writeCloseTag(output, writerProp, "integerArray");
        }

        if (hasIsotopes)
        {
            attributes.clear();
            attributes.put("builtin", "isotope");
            writeOpenTag(output, writerProp, "integerArray", attributes, false);
            write(output, isotope.toString());
            writeCloseTag(output, writerProp, "integerArray");
        }

        writeCloseTag(output, writerProp, "atomArray");
    }

    protected void writeBonds(Molecule mol, String molID,
        Map<String, StringInt> atomIDs, Map<String, StringInt> bondIDs)
    {
        writeOpenTag(output, writerProp, "bondArray");

        StringBuffer orders = new StringBuffer(10 * mol.getBondsSize());
        StringBuffer begins = new StringBuffer(10 * mol.getBondsSize());
        StringBuffer ends = new StringBuffer(10 * mol.getBondsSize());
        StringBuffer bondStereo = new StringBuffer(10 * mol.getBondsSize());
        BondIterator bit = mol.bondIterator();
        Bond bond;

        Atom beginAtom;
        Atom endAtom;
        StringInt beginAtomID;
        StringInt endAtomID;
        boolean hasStereo = false;

        while (bit.hasNext())
        {
            bond = bit.nextBond();
            beginAtom = bond.getBegin();
            endAtom = bond.getEnd();

            beginAtomID = (BasicStringInt) atomIDs.get(molID + ":" +
                    beginAtom.getIndex());
            endAtomID = (BasicStringInt) atomIDs.get(molID + ":" +
                    endAtom.getIndex());

            begins.append(beginAtomID.getStringValue());
            ends.append(endAtomID.getStringValue());

            // is aromatic bond ?
            if (bond.getBondOrder() == 4)
            {
                orders.append("1.5");
            }
            else
            {
                orders.append(bond.getBondOrder());
            }

            int isomerism = IsomerismHelper.isCisTransBond(bond);

            if ((isomerism != IsomerismHelper.EZ_ISOMERISM_UNDEFINED) ||
                    ((bond.getFlags() & BondHelper.IS_WEDGE) != 0) ||
                    ((bond.getFlags() & BondHelper.IS_HASH) != 0))
            {
                hasStereo = true;
            }

            if (bond.isWedge())
            {
                // wedge bond
                bondStereo.append("W");
            }
            else if (bond.isHash())
            {
                // hatch bond
                bondStereo.append("H");
            }
            else if (isomerism != IsomerismHelper.EZ_ISOMERISM_UNDEFINED)
            {
                if (isomerism == IsomerismHelper.Z_ISOMERISM)
                {
                    // cis bond
                    bondStereo.append("C");
                }
                else if (isomerism == IsomerismHelper.E_ISOMERISM)
                {
                    // trans bond
                    bondStereo.append("T");
                }
            }
            else
            {
                bondStereo.append("none");
            }

            if (bit.hasNext())
            {
                begins.append(' ');
                ends.append(' ');
                orders.append(' ');
                bondStereo.append(' ');
            }
        }

        Map<String, String> attributes = new Hashtable<String, String>();
        attributes.put("builtin", "atomRefs");
        writeOpenTag(output, writerProp, "stringArray", attributes, false);
        write(output, begins.toString());
        writeCloseTag(output, writerProp, "stringArray");

        attributes.clear();
        attributes.put("builtin", "atomRefs");
        writeOpenTag(output, writerProp, "stringArray", attributes, false);
        write(output, ends.toString());
        writeCloseTag(output, writerProp, "stringArray");

        attributes.clear();
        attributes.put("builtin", "order");
        writeOpenTag(output, writerProp, "stringArray", attributes, false);
        write(output, orders.toString());
        writeCloseTag(output, writerProp, "stringArray");

        writeCloseTag(output, writerProp, "bondArray");

        if (hasStereo)
        {
            writeOpenTag(output, writerProp, "bondStereo", null, false);
            write(output, bondStereo.toString());
            writeCloseTag(output, writerProp, "bondStereo");
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
