///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: MoleculeAttributeArray.java,v $
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
public class MoleculeAttributeArray extends CMLMoleculeWriterBase
{
    //~ Constructors ///////////////////////////////////////////////////////////

    public MoleculeAttributeArray(PrintStream _output,
        CMLWriterProperties _writerProp)
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

        Map<String, String> attributes = new Hashtable<String, String>();
        attributes.put("atomID", aids.toString());
        attributes.put("elementType", elements.toString());
        attributes.put("formalCharge", formalCharge.toString());

        if (has3D)
        {
            attributes.put("x3", x3.toString());
            attributes.put("y3", y3.toString());
            attributes.put("z3", z3.toString());
        }

        if (has2D)
        {
            attributes.put("x2", x2.toString());
            attributes.put("y2", y2.toString());
        }

        //        if (writerProp.writePartialCharge())
        //        {
        //            //CML1
        //            attributes.clear();
        //            attributes.put("builtin", "partialCharge");
        //            writeOpenTag(output,writerProp,"floatArray", attributes, false);
        //            write(output,partCharge.toString());
        //            writeCloseTag(output,writerProp,"floatArray");
        //
        //            // CML2
        //            //<array dataType="xsd:float" dictRef="joelib:partialCharge" units="units:electron">.234 1,456 4.678</scalar>
        //        }
        if (writerProp.writeImpliciteHydrogens())
        {
            attributes.put("hydrogenCount", impValence.toString());
        }

        if (hasIsotopes)
        {
            attributes.put("isotope", isotope.toString());
        }

        writeOpenTag(output, writerProp, "atomArray", attributes, false);
        writeCloseTag(output, writerProp, "atomArray");
    }

    protected void writeBonds(Molecule mol, String molID,
        Map<String, StringInt> atomIDs, Map<String, StringInt> bondIDs)
    {
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

            beginAtomID = atomIDs.get(molID + ":" + beginAtom.getIndex());
            endAtomID = atomIDs.get(molID + ":" + endAtom.getIndex());

            begins.append(beginAtomID.getStringValue());
            ends.append(endAtomID.getStringValue());

            // is aromatic bond ?
            if (bond.getBondOrder() == BondHelper.AROMATIC_BO)
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
        attributes.put("atomRef1", begins.toString());
        attributes.put("atomRef2", ends.toString());
        attributes.put("order", orders.toString());

        if (hasStereo)
        {
            attributes.put("bondStereo", bondStereo.toString());
        }

        writeOpenTag(output, writerProp, "bondArray", attributes, false);
        writeCloseTag(output, writerProp, "bondArray");
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
