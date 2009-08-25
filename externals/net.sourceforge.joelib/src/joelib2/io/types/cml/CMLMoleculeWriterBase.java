///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: CMLMoleculeWriterBase.java,v $
//Purpose:  Chemical Markup Language.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu,
//                egonw@sci.kun.nl, wegner@users.sourceforge.net
//Version:  $Revision: 1.13 $
//                $Date: 2005/02/24 16:58:58 $
//                $Author: wegner $
//
//Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public License
//as published by the Free Software Foundation; either version 2.1
//of the License, or (at your option) any later version.
//All we ask is that proper credit is given for our work, which includes
//- but is not limited to - adding the above copyright notice to the beginning
//of your source code files, and to any copyright notice that you may distribute
//with programs based on this work.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////
package joelib2.io.types.cml;

import joelib2.data.BasicElementHolder;
import joelib2.data.IdentifierExpertSystem;

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

import joelib2.molecule.types.PairData;

import joelib2.util.types.StringInt;

import java.io.PrintStream;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;


/**
 * Basic CML molecule  writer.
 *
 * @.author     egonw
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license LGPL
 * @.cvsversion    $Revision: 1.13 $, $Date: 2005/02/24 16:58:58 $
 * @.cite rr99b
 * @.cite mr01
 * @.cite gmrw01
 * @.cite wil01
 * @.cite mr03
 * @.cite mrww04
 */
public abstract class CMLMoleculeWriterBase implements CMLMoleculeWriter
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.cml.CMLMoleculeWriterBase");
    public static final String VERSION = IdentifierExpertSystem.transformCVStag(
            "$Revision: 1.13 $");

    //~ Instance fields ////////////////////////////////////////////////////////

    protected PrintStream output;

    protected CMLWriterProperties writerProp = null;
    private int openTags;

    //~ Constructors ///////////////////////////////////////////////////////////

    public CMLMoleculeWriterBase(PrintStream _output,
        CMLWriterProperties _writerProp)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initializing " + this.getClass().getName());
        }

        writerProp = _writerProp;
        output = _output;
        openTags = 0;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static void write(PrintStream _output, double[] da)
    {
        for (int i = 0; i < da.length; i++)
        {
            write(_output, Double.toString(da[i]));

            if (i < (da.length - 1))
            {
                write(_output, " ");
            }
        }
    }

    public static void write(PrintStream _output, String s)
    {
        _output.print(s);
    }

    public static void writeCloseTag(PrintStream _output,
        CMLWriterProperties _writerProp, String name)
    {
        write(_output, "</");
        writeElementName(_output, _writerProp, name);
        write(_output, ">\n");

        //openTags--;
    }

    public static void writeElementName(PrintStream _output,
        CMLWriterProperties _writerProp, String name)
    {
        if (_writerProp.useNamespace())
        {
            write(_output, _writerProp.getNamespace() + ":");
        }

        write(_output, name);
    }

    public static void writeEmptyElement(PrintStream _output,
        CMLWriterProperties _writerProp, String name, Map atts)
    {
        write(_output, "<");
        writeElementName(_output, _writerProp, name);
        writeOpenTagAtts(_output, atts);
        write(_output, "/>\n");
    }

    public static void writeOpenTag(PrintStream _output,
        CMLWriterProperties _writerProp, String name)
    {
        writeOpenTag(_output, _writerProp, name, null, true);
    }

    public static void writeOpenTag(PrintStream _output,
        CMLWriterProperties _writerProp, String name, boolean writeEOL)
    {
        writeOpenTag(_output, _writerProp, name, null, writeEOL);
    }

    public static void writeOpenTag(PrintStream _output,
        CMLWriterProperties _writerProp, String name, Map atts)
    {
        writeOpenTag(_output, _writerProp, name, atts, true);
    }

    public static void writeOpenTag(PrintStream _output,
        CMLWriterProperties _writerProp, String name, Map atts,
        boolean writeEOL)
    {
        write(_output, "<");
        writeElementName(_output, _writerProp, name);
        writeOpenTagAtts(_output, atts);
        write(_output, ">");

        if (writeEOL)
        {
            write(_output, "\n");
        }

        //openTags++;
    }

    public static void writeOpenTagAtts(PrintStream _output, Map atts)
    {
        if (atts != null)
        {
            Iterator keys = atts.keySet().iterator();

            while (keys.hasNext())
            {
                String key = (String) keys.next();
                write(_output, " " + key + "=\"");
                write(_output, atts.get(key).toString());
                write(_output, "\"");
            }
        }
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     */
    public void writeMolecule(Molecule mol, boolean writePairData,
        List attribs2write)
    {
        Map<String, StringInt> atomIDs = new Hashtable<String, StringInt>();
        Map<String, StringInt> bondIDs = new Hashtable<String, StringInt>();

        // create CML atom and bond ids
        //if (cmlIds)
        //{
        String molID = CMLIDCreator.createMoleculeID(mol);
        CMLIDCreator.createAtomAndBondIDs(mol, molID, atomIDs, bondIDs);

        //System.out.println(atomIDs);
        //}
        Map<String, String> attributes = new Hashtable<String, String>();

        //              if (mol.getID() != null && mol.getID().length() != 0)
        //              {
        attributes.put("id", molID);

        //              }
        if (mol.getTitle() != null)
        {
            attributes.put("title",
                XMLSpecialCharacter.convertPlain2XML(mol.getTitle()));
        }

        // JOELib kernel specification
        if (this.writerProp.useNamespace())
        {
            attributes.put("xmlns:jk",
                "http://joelib.sf.net/joelib/kernel/dict");
        }

        //attributes.put("xmlns:cml", this.writerProp.getXMLDeclaration());

        writeOpenTag(output, writerProp, "molecule", attributes);

        writeMetaInformations(mol);

        if (writerProp.storeChemistryKernelInfo())
        {
            writeChemistryKernel(mol);
        }

        boolean has3D = false;
        boolean has2D = false;
        AtomDoubleResult adrX = null;
        AtomDoubleResult adrY = null;

        if (mol.has2D())
        {
            has2D = true;
        }

        if (mol.hasData(CMLPropertyWriter.COORDINATES_2D_X) &&
                mol.hasData(CMLPropertyWriter.COORDINATES_2D_Y))
        {
            PairData pairData = mol.getData(CMLPropertyWriter.COORDINATES_2D_X,
                    true);

            if (pairData.getKeyValue() instanceof AtomDoubleResult)
            {
                adrX = (AtomDoubleResult) pairData.getKeyValue();
                pairData = mol.getData(CMLPropertyWriter.COORDINATES_2D_Y,
                        true);

                if (pairData.getKeyValue() instanceof AtomDoubleResult)
                {
                    adrY = (AtomDoubleResult) pairData.getKeyValue();
                    has2D = true;
                }
            }
        }

        if (mol.has3D())
        {
            has3D = true;
        }

        // assume 3D coordinates, if both are not available !;-)
        // Any coordinates must be stored
        if (!has3D && !has2D)
        {
            has3D = true;
        }

        writeAtoms(mol, molID, has3D, has2D, adrX, adrY, atomIDs);
        writeBonds(mol, molID, atomIDs, bondIDs);

        // write molecule title
        if (mol.getTitle() != null)
        {
            attributes.clear();
            attributes.put("convention", "trivial");
            writeOpenTag(output, writerProp, "name", attributes, false);
            write(output, XMLSpecialCharacter.convertPlain2XML(mol.getTitle()));
            writeCloseTag(output, writerProp, "name");
        }

        // write symmetry informations
        if (writerProp.writeSymmetryInformations())
        {
            CMLSymmetryWriter.writeSymmetry(output, mol);
        }

        // write descriptor values
        CMLPropertyWriter.writeProperties(writerProp, output, mol,
            writePairData, attribs2write);

        writeCloseTag(output, writerProp, "molecule");
    }

    protected abstract void writeAtoms(Molecule mol, String molID,
        boolean has3D, boolean has2D, AtomDoubleResult adrX,
        AtomDoubleResult adrY, Map<String, StringInt> atomIDs);

    protected abstract void writeBonds(Molecule mol, String molID,
        Map<String, StringInt> atomIDs, Map<String, StringInt> bondIDs);

    protected synchronized void write2D(Vector3D p, AtomDoubleResult x,
        AtomDoubleResult y, int index)
    {
        if ((x != null) && (y != null))
        {
            Map<String, String> attributes = new Hashtable<String, String>();
            attributes.clear();
            attributes.put("builtin", "x2");
            writeOpenTag(output, writerProp, "float", attributes, false);
            write(output, x.getStringValue(index));
            writeCloseTag(output, writerProp, "float");

            attributes.clear();
            attributes.put("builtin", "y2");
            writeOpenTag(output, writerProp, "float", attributes, false);
            write(output, y.getStringValue(index));
            writeCloseTag(output, writerProp, "float");
        }
        else if (p != null)
        {
            Map<String, String> attributes = new Hashtable<String, String>();
            attributes.clear();
            attributes.put("builtin", "x2");
            writeOpenTag(output, writerProp, "float", attributes, false);
            write(output, Double.toString(p.getX3D()));
            writeCloseTag(output, writerProp, "float");

            attributes.clear();
            attributes.put("builtin", "y2");
            writeOpenTag(output, writerProp, "float", attributes, false);
            write(output, Double.toString(p.getY3D()));
            writeCloseTag(output, writerProp, "float");
        }
    }

    /**
     * Description of the Method
     *
     * @param p  Description of the Parameter
     */
    protected synchronized void write3D(Vector3D p)
    {
        if (p != null)
        {
            Map<String, String> attributes = new Hashtable<String, String>();
            attributes.clear();
            attributes.put("builtin", "x3");
            writeOpenTag(output, writerProp, "float", attributes, false);
            write(output, Double.toString(p.getX3D()));
            writeCloseTag(output, writerProp, "float");

            attributes.clear();
            attributes.put("builtin", "y3");
            writeOpenTag(output, writerProp, "float", attributes, false);
            write(output, Double.toString(p.getY3D()));
            writeCloseTag(output, writerProp, "float");

            attributes.clear();
            attributes.put("builtin", "z3");
            writeOpenTag(output, writerProp, "float", attributes, false);
            write(output, Double.toString(p.getZ3D()));
            writeCloseTag(output, writerProp, "float");
        }
    }

    /**
     * Description of the Method
     *
     * @param atom  Description of the Parameter
     */
    protected synchronized void writeAtom(Atom atom, StringInt atomID,
        boolean write2D, boolean write3D, AtomDoubleResult x,
        AtomDoubleResult y)
    {
        Map<String, String> attributes = new Hashtable<String, String>();

        attributes.put("id", atomID.getStringValue());
        writeOpenTag(output, writerProp, "atom", attributes);

        attributes.clear();
        attributes.put("builtin", "elementType");
        writeOpenTag(output, writerProp, "string", attributes, false);
        write(output,
            BasicElementHolder.instance().getSymbol(atom.getAtomicNumber()));
        writeCloseTag(output, writerProp, "string");

        //        write(atom.getPoint2D());
        if (write3D)
        {
            write3D(atom.getCoords3D());
        }

        if (write2D)
        {
            write2D(atom.getCoords3D(), x, y, atom.getIndex());
        }

        if (writerProp.forceWriteFormalCharge() ||
                (atom.getFormalCharge() != 0))
        {
            attributes.clear();
            attributes.put("builtin", "formalCharge");
            writeOpenTag(output, writerProp, "integer", attributes, false);
            write(output, Integer.toString(atom.getFormalCharge()));
            writeCloseTag(output, writerProp, "integer");
        }

        if (writerProp.writePartialCharge())
        {
            if (writerProp.getCMLversion() == 1.0)
            {
                //CML1
                attributes.clear();
                attributes.put("builtin", "partialCharge");
                writeOpenTag(output, writerProp, "float", attributes, false);
                write(output,
                    Double.toString(AtomPartialCharge.getPartialCharge(atom)));
                writeCloseTag(output, writerProp, "float");
            }
            else
            {
                // CML2
                attributes.clear();
                attributes.put("dataType", "xsd:float");
                attributes.put("dictRef",
                    atom.getParent().getPartialChargeVendor());
                attributes.put("units", "units:electron");
                writeOpenTag(output, writerProp, "scalar", attributes, false);
                write(output,
                    Double.toString(AtomPartialCharge.getPartialCharge(atom)));
                writeCloseTag(output, writerProp, "scalar");
            }
        }

        if (writerProp.writeImpliciteHydrogens())
        {
            attributes.clear();
            attributes.put("builtin", "hydrogenCount");
            writeOpenTag(output, writerProp, "integer", attributes, false);
            write(output,
                Integer.toString(
                    AtomImplicitValence.getImplicitValence(atom) +
                    AtomExplicitHydrogenCount.getIntValue(atom)));
            writeCloseTag(output, writerProp, "integer");
        }

        if (atom.getIsotope() != 0)
        {
            attributes.clear();
            attributes.put("builtin", "isotope");
            writeOpenTag(output, writerProp, "integer", attributes, false);
            write(output, Integer.toString(atom.getIsotope()));
            writeCloseTag(output, writerProp, "integer");
        }

        writeCloseTag(output, writerProp, "atom");
    }

    protected void writeBondOrder(Bond bond)
    {
        Map<String, String> attributes = new Hashtable<String, String>();
        attributes.put("builtin", "order");
        writeOpenTag(output, writerProp, "string", attributes, false);

        // is aromatic bond ?
        if (bond.getBondOrder() == BondHelper.AROMATIC_BO)
        {
            write(output, "1.5");
        }
        else
        {
            write(output, Integer.toString(bond.getBondOrder()));
        }

        writeCloseTag(output, writerProp, "string");
    }

    protected void writeBondStereo(Bond bond)
    {
        // check stereochemistry: up/down and cis/trans
        int isomerism = IsomerismHelper.isCisTransBond(bond);

        if ((isomerism != IsomerismHelper.EZ_ISOMERISM_UNDEFINED) ||
                ((bond.getFlags() & BondHelper.IS_WEDGE) != 0) ||
                ((bond.getFlags() & BondHelper.IS_HASH) != 0))
        {
            Map<String, String> attributes = new Hashtable<String, String>();

            if (writerProp.getCMLversion() == 1.0)
            {
                attributes.put("builtin", "stereo");
                writeOpenTag(output, writerProp, "string", attributes, false);
            }
            else
            {
                attributes.put("dataType", "xsd:string");
                attributes.put("dictRef", "mdl:stereo");
                writeOpenTag(output, writerProp, "scalar", attributes, false);
            }

            if (bond.isWedge())
            {
                // wedge bond
                write(output, "W");
            }
            else if (bond.isHash())
            {
                // hatch bond
                write(output, "H");
            }
            else if (isomerism != IsomerismHelper.EZ_ISOMERISM_UNDEFINED)
            {
                if (isomerism == IsomerismHelper.Z_ISOMERISM)
                {
                    // cis bond
                    write(output, "C");
                }
                else if (isomerism == IsomerismHelper.E_ISOMERISM)
                {
                    // trans bond
                    write(output, "T");
                }
            }

            if (writerProp.getCMLversion() == 1.0)
            {
                writeCloseTag(output, writerProp, "string");
            }
            else
            {
                writeCloseTag(output, writerProp, "scalar");
            }
        }
    }

    /**
     * @param mol
     */
    protected void writeMetaInformations(Molecule mol)
    {
        Map<String, String> attributes = new Hashtable<String, String>();
        attributes.clear();
        attributes.put("title", "generated automatically from JOELib");
        writeOpenTag(output, writerProp, "metadataList", attributes);

        attributes.clear();
        attributes.put("name", "dc:creator");
        attributes.put("content",
            "Used JOELib chemistry kernel (expert systems) ID is " +
            IdentifierExpertSystem.instance().getKernelHash() +
            " and the used CML writer is " + this.getClass().getName() +
            "(version " + VERSION + ")");
        writeOpenTag(output, writerProp, "metadata", attributes, false);
        writeCloseTag(output, writerProp, "metadata");

        attributes.clear();
        attributes.put("name", "dc:description");
        attributes.put("content", "Conversion of legacy filetype to CML");
        writeOpenTag(output, writerProp, "metadata", attributes, false);
        writeCloseTag(output, writerProp, "metadata");

        attributes.clear();
        attributes.put("name", "dc:identifier");
        attributes.put("content", "unknown");
        writeOpenTag(output, writerProp, "metadata", attributes, false);
        writeCloseTag(output, writerProp, "metadata");

        attributes.clear();
        attributes.put("name", "dc:content");
        writeOpenTag(output, writerProp, "metadata", attributes, false);
        writeCloseTag(output, writerProp, "metadata");

        attributes.clear();
        attributes.put("name", "dc:rights");
        attributes.put("content", "unknown");
        writeOpenTag(output, writerProp, "metadata", attributes, false);
        writeCloseTag(output, writerProp, "metadata");

        attributes.clear();
        attributes.put("name", "dc:type");
        attributes.put("content", "chemistry");
        writeOpenTag(output, writerProp, "metadata", attributes, false);
        writeCloseTag(output, writerProp, "metadata");
        attributes.clear();
        attributes.put("name", "dc:contributor");
        attributes.put("content",
            "see http://joelib.sf.net for a full list of contributors");
        writeOpenTag(output, writerProp, "metadata", attributes, false);
        writeCloseTag(output, writerProp, "metadata");

        attributes.clear();
        attributes.put("name", "dc:date");
        attributes.put("content", (new Date()).toGMTString());
        writeOpenTag(output, writerProp, "metadata", attributes, false);
        writeCloseTag(output, writerProp, "metadata");

        attributes.clear();
        attributes.put("name", "cmlm:structure");
        attributes.put("content", "yes");
        writeOpenTag(output, writerProp, "metadata", attributes, false);
        writeCloseTag(output, writerProp, "metadata");

        writeCloseTag(output, writerProp, "metadataList");
    }

    private void writeChemistryKernel(Molecule mol)
    {
        String[] infos = IdentifierExpertSystem.instance()
                                               .getKernelInformations();
        String[] titles = IdentifierExpertSystem.instance().getKernelTitles();
        Map<String, String> attributes = new Hashtable<String, String>();
        int kernelHash = IdentifierExpertSystem.instance().getKernelHash();
        StringBuffer sb = new StringBuffer();
        String id;
        String title;

        for (int i = 0; i < infos.length; i++)
        {
            attributes.clear();
            title = IdentifierExpertSystem.CML_KERNEL_REFERENCE_PREFIX +
                kernelHash + ":" + titles[i];

            if (mol.hasData(title))
            {
                //remove old kernels from descriptor data base
                mol.deleteData(title);
            }

            attributes.put("title", title);
            id = IdentifierExpertSystem.CML_KERNEL_REFERENCE_PREFIX + "s" +
                Integer.toString(infos[i].hashCode());
            sb.append(id);

            if (i < (infos.length - 1))
            {
                sb.append(",");
            }

            attributes.put("id", id);
            attributes.put("dataType", "xsd:string");
            attributes.put("dictRef",
                IdentifierExpertSystem.CML_KERNEL_REFERENCE_PREFIX +
                Integer.toString(kernelHash));
            writeOpenTag(output, writerProp, "scalar", attributes, false);
            write(output, infos[i]);
            writeCloseTag(output, writerProp, "scalar");
        }

        attributes.clear();
        attributes.put("title",
            IdentifierExpertSystem.CML_KERNEL_REFERENCE_PREFIX + kernelHash);
        attributes.put("id",
            IdentifierExpertSystem.CML_KERNEL_REFERENCE_PREFIX +
            Integer.toString(kernelHash));
        attributes.put("dataType", "xsd:string");
        attributes.put("dictRef",
            IdentifierExpertSystem.CML_KERNEL_REFERENCE_PREFIX +
            Integer.toString(kernelHash));
        writeOpenTag(output, writerProp, "array", attributes, false);
        write(output, sb.toString());
        writeCloseTag(output, writerProp, "array");
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
