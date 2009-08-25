///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: CMLCoreModule.java,v $
//Purpose:  Chemical Markup Language.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu,
//          egonw@sci.kun.nl, wegner@users.sourceforge.net
//Version:  $Revision: 1.10 $
//                      $Date: 2005/02/17 16:48:35 $
//                      $Author: wegner $
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

import joelib2.feature.result.DoubleArrayResult;
import joelib2.feature.result.DoubleMatrixResult;
import joelib2.feature.result.DoubleResult;
import joelib2.feature.result.IntArrayResult;
import joelib2.feature.result.IntResult;

import joelib2.io.BasicIOTypeHolder;

import joelib2.io.types.cml.elements.ArrayCML;
import joelib2.io.types.cml.elements.Elements;
import joelib2.io.types.cml.elements.MatrixCML;
import joelib2.io.types.cml.elements.ScalarCML;

import joelib2.math.CrystalGeometryTools;

import joelib2.util.BasicArrayHelper;
import joelib2.util.BasicMatrixHelper;

import joelib2.util.types.BasicStringObject;
import joelib2.util.types.BasicStringString;
import joelib2.util.types.StringString;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Category;

import org.xml.sax.Attributes;


/**
 * Core CML 1.x and 2.0 elements are parsed by this class.
 *
 * <p>Please file a bug report if this parser fails to parse
 * a certain element or attribute value in a valid CML document.
 *
 * @.author egonw
 * @.author c.steinbeck@uni-koeln.de
 * @.author gezelter@maul.chem.nd.edu
 * @.author wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license LGPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:35 $
 * @.cite rr99b
 * @.cite mr01
 * @.cite gmrw01
 * @.cite wil01
 * @.cite mr03
 * @.cite mrww04
 **/
public class CMLCoreModule implements ModuleInterface
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.io.types.cml.CMLCoreModule");
    public final static int UNKNOWN = -1;
    public final static int STRING = 1;
    public final static int LINK = 2;
    public final static int FLOAT = 3;
    public final static int INTEGER = 4;
    public final static int STRINGARRAY = 5;
    public final static int FLOATARRAY = 6;
    public final static int INTEGERARRAY = 7;
    public final static int FLOATMATRIX = 8;
    public final static int COORDINATE2 = 9;
    public final static int COORDINATE3 = 10;
    public final static int ANGLE = 11;
    public final static int TORSION = 12;
    public final static int LIST = 13;
    public final static int MOLECULE = 14;
    public final static int ATOM = 15;
    public final static int ATOMARRAY = 16;
    public final static int BOND = 17;
    public final static int BONDARRAY = 18;
    public final static int ELECTRON = 19;
    public final static int REACTION = 20;
    public final static int CRYSTAL = 21;
    public final static int SEQUENCE = 22;
    public final static int FEATURE = 23;
    public final static int MATRIX = 24;
    public final static int ARRAY = 25;
    public final static int SCALAR = 26;
    public final static int BOND_STEREO = 27;
    public final static int NAME = 28;
    public final static int SYMMETRY = 29;

    //~ Instance fields ////////////////////////////////////////////////////////

    public Map<String, List<String>> atomElements = new Hashtable<String, List<String>>(
            23);
    public Map<String, Integer> elements = new Hashtable<String, Integer>(23);
    protected double[] a;

    // array
    protected ArrayCML array;
    protected List<BasicStringObject> arrays;
    protected String arrayTitle;
    protected int atomCounter;
    protected List<String> atomDictRefs;
    protected List<String> atomParities;
    protected double[] b;
    protected List<String> bondARef1;
    protected List<String> bondARef2;

    //scalar
    protected int bondCounter;
    protected List<String> bondDictRefs;
    protected List<String> bondid;
    protected List<String> bondStereo;
    protected String BUILTIN;
    protected double[] c;
    protected boolean cartesianAxesSet = false;
    protected CDOInterface cdo;
    protected int crystalScalar;
    protected int curRef;
    protected String currentChars;
    protected int currentElement;
    protected String delimiter;
    protected String DICTREF;
    protected String elementTitle;
    protected List<String> elid;
    protected List<String> elsym;
    protected List<String> eltitles;
    protected List<String> formalCharges;
    protected List<String> hCounts;
    protected List<String> isotopes;
    protected List<BasicStringObject> matrices;

    // matrix
    protected MatrixCML matrix;
    protected String matrixColumns;
    protected String matrixDelimiter;
    protected String matrixRows;
    protected String matrixTitle;
    protected String moleculeName;
    protected List<String> order;
    protected List<String> partialCharges;
    protected ScalarCML scalar;

    //descriptors
    protected List<BasicStringObject> scalars;
    protected boolean stereoGiven;
    protected List<StringString> strings;
    protected final String SYSTEMID = "CML-1999-05-15";

    //crystal
    protected double[] unitcellparams;
    protected List<String> x2;
    protected List<String> x3;
    protected List<String> xfract;
    protected List<String> y2;
    protected List<String> y3;
    protected List<String> yfract;
    protected List<String> z3;
    protected List<String> zfract;

    //~ Constructors ///////////////////////////////////////////////////////////

    public CMLCoreModule(CDOInterface cdo)
    {
        initialize();
        this.cdo = cdo;
    }

    public CMLCoreModule(ModuleInterface conv)
    {
        initialize();
        inherit(conv);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void characterData(CMLStack xpath, char[] ch, int start, int length)
    {
        currentChars = currentChars + new String(ch, start, length);

        //              if (logger.isDebugEnabled())
        //                      logger.debug("CD: " + currentChars);
        String s = (new String(ch, start, length));

        //System.out.println("start:"+start+" length:"+length+" s:"+s);
        //        if (s.trim().length() == 0)
        //        {
        //            return;
        //        }
        //System.out.println(BUILTIN+" "+elementTitle+"="+s);
        switch (currentElement)
        {
        case MATRIX:

            if (!matrix.characterData(s))
            {
                logger.error("Error storing matrix character data.");
            }

            break;

        case ARRAY:

            if (!array.characterData(s))
            {
                logger.error("Error storing array character data.");
            }

            break;

        case SCALAR:

            if (!scalar.characterData(s))
            {
                logger.error("Error storing scalar character data.");
            }

            break;
        }
    }

    public void endDocument()
    {
        cdo.endDocument();

        if (logger.isDebugEnabled())
        {
            logger.debug("End XML Doc");
        }
    }

    public void endElement(CMLStack xpath, String uri, String name, String raw)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("EndElement: " + name);
        }

        setCurrentElement(name);

        currentChars = currentChars.trim();

        switch (currentElement)
        {
        case BOND:

            if (!stereoGiven)
            {
                bondStereo.add("");

                //System.out.println("Add stereo info <empty>");
            }

            if (bondStereo.size() > bondDictRefs.size())
            {
                bondDictRefs.add(null);
            }

            break;

        case ATOM:

            if (atomCounter > eltitles.size())
            {
                eltitles.add(null);
            }

            if (elsym.size() > formalCharges.size())
            {
                /* while strictly undefined, assume zero
                charge when no number is given */
                formalCharges.add("0");
            }

            if (elsym.size() > hCounts.size())
            {
                /* while strictly undefined, assume zero
                implicit hydrogens when no number is given */
                hCounts.add("0");
            }

            if (elsym.size() > isotopes.size())
            {
                /* while strictly undefined, assume zero
                charge when no number is given */
                isotopes.add("0");
            }

            /* It may happen that not all atoms have
               associated 2D coordinates. accept that */
            if ((elsym.size() > x2.size()) && (x2.size() != 0))
            {
                /* apparently, the previous atoms had atomic
                   coordinates, add 'null' for this atom */
                x2.add(null);
                y2.add(null);
            }

            break;

        case BOND_STEREO:
            addArrayElementsTo(bondStereo, currentChars);

            break;

        case MOLECULE:
            storeData();
            cdo.endObject("Molecule");

            break;

        case CRYSTAL:

            if (crystalScalar > 0)
            {
                // convert unit cell parameters to cartesians
                double[][] axes = CrystalGeometryTools.notionalToCartesian(
                        unitcellparams[0], unitcellparams[1], unitcellparams[2],
                        unitcellparams[3], unitcellparams[4],
                        unitcellparams[5]);
                a[0] = axes[0][0];
                a[1] = axes[0][1];
                a[2] = axes[0][2];
                b[0] = axes[1][0];
                b[1] = axes[1][1];
                b[2] = axes[1][2];
                c[0] = axes[2][0];
                c[1] = axes[2][1];
                c[2] = axes[2][2];
                cartesianAxesSet = true;
                cdo.startObject(Elements.A_AXIS);
                cdo.setObjectProperty(Elements.A_AXIS, Elements.X,
                    Double.toString(a[0]));
                cdo.setObjectProperty(Elements.A_AXIS, Elements.Y,
                    Double.toString(a[1]));
                cdo.setObjectProperty(Elements.A_AXIS, Elements.Z,
                    Double.toString(a[2]));
                cdo.endObject(Elements.A_AXIS);
                cdo.startObject(Elements.B_AXIS);
                cdo.setObjectProperty(Elements.B_AXIS, Elements.X,
                    Double.toString(b[0]));
                cdo.setObjectProperty(Elements.B_AXIS, Elements.Y,
                    Double.toString(b[1]));
                cdo.setObjectProperty(Elements.B_AXIS, Elements.Z,
                    Double.toString(b[2]));
                cdo.endObject(Elements.B_AXIS);
                cdo.startObject(Elements.C_AXIS);
                cdo.setObjectProperty(Elements.C_AXIS, Elements.X,
                    Double.toString(c[0]));
                cdo.setObjectProperty(Elements.C_AXIS, Elements.Y,
                    Double.toString(c[1]));
                cdo.setObjectProperty(Elements.C_AXIS, Elements.Z,
                    Double.toString(c[2]));
                cdo.endObject(Elements.C_AXIS);
            }
            else
            {
                logger.error("Could not find crystal unit cell parameters");
            }

            cdo.endObject(Elements.CRYSTAL);

            break;

        case COORDINATE3:

            if (BUILTIN.equals(Elements.XYZ3))
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("New coord3 xyz3 found: " + currentChars);
                }

                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars);
                    x3.add(st.nextToken());
                    y3.add(st.nextToken());
                    z3.add(st.nextToken());

                    if (logger.isDebugEnabled())
                    {
                        logger.debug("coord3 x3.length: " + x3.size());
                        logger.debug("coord3 y3.length: " + y3.size());
                        logger.debug("coord3 z3.length: " + z3.size());
                    }
                }
                catch (Exception e)
                {
                    logger.error("CMLParsing error while setting coordinate3!");
                }
            }
            else
            {
                logger.warn("Unknown coordinate3 BUILTIN: " + BUILTIN);
            }

            break;

        case MATRIX:
            matrix.endElement(name);

            break;

        case ARRAY:
            array.endElement(name);

            break;

        case SCALAR:

            if (xpath.toString().endsWith("crystal/scalar/"))
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Going to set a crystal parameter: " +
                        crystalScalar + " to " + currentChars);
                }

                try
                {
                    unitcellparams[crystalScalar - 1] = Double.parseDouble(
                            currentChars.trim());
                }
                catch (NumberFormatException exception)
                {
                    logger.error("Content must a float: " + currentChars);
                }
            }
            else if (xpath.toString().endsWith("bond/scalar/"))
            {
                if (DICTREF.equals("mdl:stereo"))
                {
                    bondStereo.add(currentChars.trim());
                    stereoGiven = true;
                }
            }
            else if (xpath.toString().endsWith("atom/scalar/"))
            {
                if (DICTREF.endsWith(Elements.PARTIALCHARGE))
                {
                    partialCharges.add(scalar.getAllCharacterData());
                }
            }
            else
            {
                // store as descriptor
                if (scalar.getTitle() == null)
                {
                    //logger.error("xpath: "+xpath.toString());
                    logger.error("No title defined for scalar element: " +
                        scalar.getAllCharacterData());
                }

                scalar.endElement(name);
            }

            break;

        case NAME:

            //System.out.println("Molecule name :"+s.trim());
            moleculeName = currentChars.trim();

            break;

        case STRING:

            if (BUILTIN.equals("elementType"))
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Element: " + currentChars);
                }

                elsym.add(currentChars);
            }
            else if (BUILTIN.equals("atomRef"))
            {
                curRef++;

                //logger.debug("Bond: ref #" + curRef);
                if (curRef == 1)
                {
                    bondARef1.add(currentChars.trim());
                }
                else if (curRef == 2)
                {
                    bondARef2.add(currentChars.trim());
                }
            }
            else if (BUILTIN.equals("atomRefs"))
            {
                StringTokenizer st = new StringTokenizer(currentChars.trim());
                bondARef1.add(st.nextToken());
                bondARef2.add(st.nextToken());
            }
            else if (BUILTIN.equals(Elements.ORDER))
            {
                //logger.debug("Bond: order " + s.trim());
                order.add(currentChars.trim());
            }
            else if (BUILTIN.equals(Elements.STEREO))
            {
                //logger.debug("Bond: stereo " + s.trim());
                //System.out.println("Bond: stereo " + s.trim());
                bondStereo.add(currentChars.trim());
                stereoGiven = true;
            }
            else if (BUILTIN.equals(Elements.FORMALCHARGE))
            {
                // NOTE: this combination is in violation of the CML DTD!!!
                //logger.debug("Charge: " + s.trim());
                formalCharges.add(currentChars.trim());
            }
            else if (BUILTIN.equals(Elements.ISOTOPE))
            {
                isotopes.add(currentChars.trim());
            }
            else
            {
                String tmp = currentChars.trim();

                if (tmp.length() != 0)
                {
                    strings.add(new BasicStringString(elementTitle, tmp));
                }
            }

            break;

        case FLOAT:

            if (BUILTIN.equals(Elements.X3))
            {
                x3.add(currentChars.trim());
            }
            else if (BUILTIN.equals(Elements.Y3))
            {
                y3.add(currentChars.trim());
            }
            else if (BUILTIN.equals(Elements.Z3))
            {
                z3.add(currentChars.trim());
            }
            else if (BUILTIN.equals(Elements.X2))
            {
                x2.add(currentChars.trim());
            }
            else if (BUILTIN.equals(Elements.Y2))
            {
                y2.add(currentChars.trim());
            }
            else if (BUILTIN.equals(Elements.ORDER))
            {
                // NOTE: this combination is in violation of the CML DTD!!!
                order.add(currentChars.trim());
            }
            else if (BUILTIN.equals("charge") ||
                    BUILTIN.equals(Elements.PARTIALCHARGE))
            {
                partialCharges.add(currentChars.trim());
            }
            else
            {
                String tmp = currentChars.trim();
                DoubleResult dr = new DoubleResult();

                if (!dr.fromString(
                            BasicIOTypeHolder.instance().getIOType("CML"), tmp))
                {
                    logger.error("Double entry " + elementTitle + "=" + tmp +
                        " was not successfully parsed.");
                }
                else
                {
                    scalars.add(new BasicStringObject(elementTitle, dr));
                }
            }

            break;

        case INTEGER:

            if (BUILTIN.equals(Elements.FORMALCHARGE))
            {
                formalCharges.add(currentChars.trim());
            }
            else if (BUILTIN.equals(Elements.HYDROGENCOUNT))
            {
                hCounts.add(currentChars.trim());
            }
            else if (BUILTIN.equals(Elements.ISOTOPE))
            {
                isotopes.add(currentChars.trim());
            }
            else
            {
                IntResult ir = new IntResult();

                if (!ir.fromString(
                            BasicIOTypeHolder.instance().getIOType("CML"),
                            currentChars.trim()))
                {
                    logger.error("Integer entry " + elementTitle + "=" +
                        currentChars.trim() + " was not successfully parsed.");
                }
                else
                {
                    scalars.add(new BasicStringObject(elementTitle, ir));
                }
            }

            break;

        case COORDINATE2:

            if (BUILTIN.equals("xy2"))
            {
                //logger.debug("New coord2 xy2 found." + s);
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());
                    x2.add(st.nextToken());
                    y2.add(st.nextToken());
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 175, 1);
                }
            }

            break;

        case STRINGARRAY:

            if (BUILTIN.equals("id") || BUILTIN.equals("atomId"))
            {
                // use of "id" seems incorrect by quick look at DTD
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        String token = st.nextToken();

                        //logger.debug("StringArray (Token): " + token);
                        elid.add(token);
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 186, 1);
                }
            }
            else if (BUILTIN.equals("elementType"))
            {
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        elsym.add(st.nextToken());
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 194, 1);
                }
            }
            else if (BUILTIN.equals("atomRefs"))
            {
                curRef++;

                //logger.debug("New atomRefs found: " + curRef);
                try
                {
                    boolean countBonds = (bondCounter == 0) ? true : false;
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        if (countBonds)
                        {
                            bondCounter++;
                        }

                        String token = st.nextToken();

                        //logger.debug("Token: " + token);
                        if (curRef == 1)
                        {
                            bondARef1.add(token);
                        }
                        else if (curRef == 2)
                        {
                            bondARef2.add(token);
                        }
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 194, 1);
                }
            }
            else if (BUILTIN.equals(Elements.ORDER))
            {
                //logger.debug("New bond order found.");
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        String token = st.nextToken();

                        //logger.debug("Token: " + token);
                        order.add(token);
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 194, 1);
                }
            }
            else if (BUILTIN.equals(Elements.STEREO))
            {
                //logger.debug("New bond order found.");
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        String token = st.nextToken();

                        //logger.debug("Token: " + token);
                        bondStereo.add(token);
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 194, 1);
                }
            }

            break;

        case INTEGERARRAY:

            //logger.debug("IntegerArray: builtin = " + BUILTIN);
            if (BUILTIN.equals(Elements.FORMALCHARGE))
            {
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        String token = st.nextToken();

                        //logger.debug("Charge added: " + token);
                        formalCharges.add(token);
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 205, 1);
                }
            }
            else if (BUILTIN.equals(Elements.HYDROGENCOUNT))
            {
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars);

                    while (st.hasMoreTokens())
                    {
                        String token = st.nextToken();

                        //logger.debug("Hydrogen count added: " + token);
                        hCounts.add(token);
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 205, 1);
                }
            }
            else if (BUILTIN.equals(Elements.ISOTOPE))
            {
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        String token = st.nextToken();

                        //logger.debug("Isotope added: " + token);
                        isotopes.add(token);
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 205, 1);
                }
            }
            else
            {
                String tmp = currentChars.trim();

                if (tmp.length() != 0)
                {
                    //System.out.println("intArray " + elementTitle);
                    IntArrayResult iar = new IntArrayResult();

                    if (delimiter == null)
                    {
                        delimiter = " \t\r\n";
                    }
                    else
                    {
                        iar.addCMLProperty(new BasicStringString("delimiter",
                                delimiter));
                    }

                    if (!iar.fromString(
                                BasicIOTypeHolder.instance().getIOType("CML"),
                                tmp))
                    {
                        logger.error("Integer array entry " + arrayTitle + "=" +
                            tmp + " was not successfully parsed.");
                    }

                    iar.setIntArray(BasicArrayHelper.intArrayFromSimpleString(
                            tmp, delimiter));
                    arrays.add(new BasicStringObject(elementTitle, iar));
                }
            }

            break;

        case FLOATARRAY:

            //System.out.println(BUILTIN+"="+s);
            if (BUILTIN.equals(Elements.X3))
            {
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        x3.add(st.nextToken());
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 205, 1);
                }
            }
            else if (BUILTIN.equals(Elements.Y3))
            {
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        y3.add(st.nextToken());
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 213, 1);
                }
            }
            else if (BUILTIN.equals(Elements.Z3))
            {
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        z3.add(st.nextToken());
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 221, 1);
                }
            }
            else if (BUILTIN.equals(Elements.X2))
            {
                //logger.debug("New floatArray found.");
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        x2.add(st.nextToken());
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 205, 1);
                }
            }
            else if (BUILTIN.equals(Elements.Y2))
            {
                //logger.debug("New floatArray found.");
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        y2.add(st.nextToken());
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 454, 1);
                }
            }
            else if (BUILTIN.equals(Elements.PARTIALCHARGE))
            {
                //logger.debug("New floatArray with partial charges found.");
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        partialCharges.add(st.nextToken());
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 462, 1);
                }
            }
            else if (BUILTIN.equals(Elements.FORMALCHARGE))
            {
                //logger.debug("New floatArray with partial charges found.");
                try
                {
                    StringTokenizer st = new StringTokenizer(currentChars
                            .trim());

                    while (st.hasMoreTokens())
                    {
                        formalCharges.add(st.nextToken());
                    }
                }
                catch (Exception e)
                {
                    notify("CMLParsing error: " + e, SYSTEMID, 462, 1);
                }
            }
            else
            {
                String tmp = currentChars.trim();

                if (tmp.length() != 0)
                {
                    //System.out.println("floatArray " + elementTitle);
                    DoubleArrayResult dar = new DoubleArrayResult();

                    if (delimiter == null)
                    {
                        delimiter = " \t\r\n";
                    }
                    else
                    {
                        dar.addCMLProperty(new BasicStringString("delimiter",
                                delimiter));
                    }

                    if (!dar.fromString(
                                BasicIOTypeHolder.instance().getIOType("CML"),
                                tmp))
                    {
                        logger.error("Double array entry " + arrayTitle + "=" +
                            tmp + " was not successfully parsed.");
                    }
                    else
                    {
                        dar.setDoubleArray(BasicArrayHelper
                            .doubleArrayFromSimpleString(tmp, delimiter));
                        arrays.add(new BasicStringObject(elementTitle, dar));
                    }
                }
            }

            break;

        case FLOATMATRIX:

            //logger.debug("FloatMatrix: builtin = " + BUILTIN);
            //if (BUILTIN.equals("???"))
            //{
            //}
            //else
            //{
            String tmp = currentChars.trim();

            if (tmp.length() != 0)
            {
                // much more efficient and more standard
                DoubleMatrixResult matrix = new DoubleMatrixResult();

                if (matrixDelimiter == null)
                {
                    matrixDelimiter = " \t\r\n";
                }
                else
                {
                    matrix.addCMLProperty(new BasicStringString("delimiter",
                            matrixDelimiter));
                }

                if (matrixRows == null)
                {
                    logger.error("Number of rows is missing in FloatMatrix '" +
                        matrixTitle + "'.");
                }
                else if (matrixColumns == null)
                {
                    logger.error(
                        "Number of columns is missing in FloatMatrix '" +
                        matrixTitle + "'.");
                }
                else
                {
                    int rows = Integer.parseInt(matrixRows);
                    int columns = Integer.parseInt(matrixColumns);
                    matrix.value = BasicMatrixHelper
                        .doubleMatrixFromSimpleString(tmp, rows, columns,
                            matrixDelimiter);

                    if (matrix.value != null)
                    {
                        matrices.add(new BasicStringObject(matrixTitle,
                                matrix));
                    }

                    //floatMatrices.add(new StringObject(matrixTitle, tmp));
                }

                //}
            }

            break;

        case LIST:
            cdo.endObject("SetOfMolecules");

            break;
        }

        currentChars = "";
        BUILTIN = "";
        elementTitle = "";
    }

    public void inherit(ModuleInterface convention)
    {
        if (convention instanceof CMLCoreModule)
        {
            CMLCoreModule conv = (CMLCoreModule) convention;
            this.cdo = conv.returnCDO();
            this.BUILTIN = conv.BUILTIN;
            this.elsym = conv.elsym;
            this.eltitles = conv.eltitles;
            this.atomCounter = conv.atomCounter;
            this.elid = conv.elid;
            this.formalCharges = conv.formalCharges;
            this.partialCharges = conv.partialCharges;
            this.isotopes = conv.isotopes;
            this.x3 = conv.x3;
            this.y3 = conv.y3;
            this.z3 = conv.z3;
            this.x2 = conv.x2;
            this.y2 = conv.y2;
            this.hCounts = conv.hCounts;
            this.atomParities = conv.atomParities;
            this.bondid = conv.bondid;
            this.bondARef1 = conv.bondARef1;
            this.bondARef2 = conv.bondARef2;
            this.order = conv.order;
            this.bondStereo = conv.bondStereo;
            this.curRef = conv.curRef;
            this.bondCounter = conv.bondCounter;
            this.atomDictRefs = conv.atomDictRefs;
            this.bondDictRefs = conv.bondDictRefs;

            this.atomElements = conv.atomElements;

            //descriptors
            this.scalars = conv.scalars;
            this.strings = conv.strings;
            this.matrices = conv.matrices;
            this.arrays = conv.arrays;

            // parser
            this.scalar = conv.scalar;
            this.array = conv.array;
            this.matrix = conv.matrix;
        }
    }

    public CDOInterface returnCDO()
    {
        return (CDOInterface) this.cdo;
    }

    public void startDocument()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Start XML Doc");
        }

        cdo.startDocument();
        newMolecule();
        BUILTIN = "";
        curRef = 0;
        currentChars = "";
    }

    public void startElement(CMLStack xpath, String uri, String local,
        String raw, Attributes atts)
    {
        String name = local;

        if (logger.isDebugEnabled())
        {
            logger.debug("StartElement: " + name);
        }

        currentChars = "";

        BUILTIN = "";
        DICTREF = "";

        for (int i = 0; i < atts.getLength(); i++)
        {
            String qname = atts.getQName(i);

            if (qname.equals("builtin"))
            {
                BUILTIN = atts.getValue(i);

                if (logger.isDebugEnabled())
                {
                    logger.debug(name + "->BUILTIN found: " + atts.getValue(i));
                }
            }
            else if (qname.equals("dictRef"))
            {
                DICTREF = atts.getValue(i);

                if (logger.isDebugEnabled())
                {
                    logger.debug(name + "->DICTREF found: " + atts.getValue(i));
                }
            }
            else if (qname.equals(Elements.TITLE))
            {
                elementTitle = atts.getValue(i);

                if (logger.isDebugEnabled())
                {
                    logger.debug(name + "->TITLE found: " + atts.getValue(i));
                }
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Qname: " + qname);
                }
            }
        }

        setCurrentElement(name);

        switch (currentElement)
        {
        case ATOM:
            atomCounter++;

            for (int i = 0; i < atts.getLength(); i++)
            {
                String att = atts.getQName(i);
                String value = atts.getValue(i);

                if (att.equals("id"))
                { // this is supported in CML 1.x
                    elid.add(value);
                }

                // this is supported in CML 2.0
                else if (att.equals("elementType"))
                {
                    elsym.add(value);
                }

                // this is supported in CML 2.0
                else if (att.equals(Elements.TITLE))
                {
                    eltitles.add(value);
                }

                // this is supported in CML 2.0
                else if (att.equals(Elements.X2))
                {
                    x2.add(value);
                }

                // this is supported in CML 2.0
                else if (att.equals("xy2"))
                {
                    StringTokenizer tokenizer = new StringTokenizer(value);
                    x2.add(tokenizer.nextToken());
                    y2.add(tokenizer.nextToken());
                }

                // this is supported in CML 2.0
                else if (att.equals("xyzFract"))
                {
                    StringTokenizer tokenizer = new StringTokenizer(value);
                    xfract.add(tokenizer.nextToken());
                    yfract.add(tokenizer.nextToken());
                    zfract.add(tokenizer.nextToken());
                }

                // this is supported in CML 2.0
                else if (att.equals(Elements.XYZ3))
                {
                    StringTokenizer tokenizer = new StringTokenizer(value);
                    x3.add(tokenizer.nextToken());
                    y3.add(tokenizer.nextToken());
                    z3.add(tokenizer.nextToken());
                }

                // this is supported in CML 2.0
                else if (att.equals(Elements.Y2))
                {
                    y2.add(value);
                }

                // this is supported in CML 2.0
                else if (att.equals(Elements.X3))
                {
                    x3.add(value);
                }

                // this is supported in CML 2.0
                else if (att.equals(Elements.Y3))
                {
                    y3.add(value);
                }

                // this is supported in CML 2.0
                else if (att.equals(Elements.Z3))
                {
                    z3.add(value);
                }

                // this is supported in CML 2.0
                else if (att.equals("xFract"))
                {
                    xfract.add(value);
                }

                // this is supported in CML 2.0
                else if (att.equals("yFract"))
                {
                    yfract.add(value);
                }

                // this is supported in CML 2.0
                else if (att.equals("zFract"))
                {
                    zfract.add(value);
                }

                // this is supported in CML 2.0
                else if (att.equals(Elements.FORMALCHARGE))
                {
                    formalCharges.add(value);
                }

                // this is supported in CML 2.0
                else if (att.equals(Elements.HYDROGENCOUNT))
                {
                    hCounts.add(value);
                }
                else if (att.equals(Elements.ISOTOPE))
                {
                    isotopes.add(value);
                }
                else if (att.equals("dictRef"))
                {
                    atomDictRefs.add(value);
                }
                else
                {
                    logger.warn("Unparsed attribute: " + att);
                }
            }

            break;

        case BOND:
            stereoGiven = false;
            bondCounter++;

            for (int i = 0; i < atts.getLength(); i++)
            {
                String att = atts.getQName(i);

                if (logger.isDebugEnabled())
                {
                    logger.debug("B2 " + att + "=" + atts.getValue(i));
                }

                if (att.equals("id"))
                {
                    bondid.add(atts.getValue(i));

                    if (logger.isDebugEnabled())
                    {
                        logger.debug("B3 " + bondid);
                    }
                }
                else if (att.equals("atomRefs") || // this is CML 1.x support
                        att.equals("atomRefs2")) // this is CML 1.x support
                { // this is CML 2.0 support

                    // expect exactly two references
                    try
                    {
                        StringTokenizer st = new StringTokenizer(atts.getValue(
                                    i));
                        bondARef1.add((String) st.nextElement());
                        bondARef2.add((String) st.nextElement());
                    }
                    catch (Exception e)
                    {
                        logger.error("Error in CML file: " + e.toString());
                    }
                }
                else if (att.equals(Elements.ORDER))
                { // this is CML 2.0 support
                    order.add(atts.getValue(i).trim());
                }
                else if (att.equals(Elements.STEREO))
                { // this is CML 2.0 support
                    bondStereo.add(atts.getValue(i).trim());
                    stereoGiven = true;
                    System.out.println("Add stereo info " +
                        atts.getValue(i).trim());
                }
                else if (att.equals("dictRef"))
                {
                    bondDictRefs.add(atts.getValue(i).trim());
                }
            }

            curRef = 0;

            break;

        case BONDARRAY:

            boolean bondsCounted = false;

            for (int i = 0; i < atts.getLength(); i++)
            {
                String att = atts.getQName(i);
                int count = 0;

                if (att.equals("bondID"))
                {
                    count = addArrayElementsTo(bondid, atts.getValue(i));
                }
                else if (att.equals("atomRefs1"))
                {
                    count = addArrayElementsTo(bondARef1, atts.getValue(i));
                }
                else if (att.equals("atomRefs2"))
                {
                    count = addArrayElementsTo(bondARef2, atts.getValue(i));
                }
                else if (att.equals("atomRef1"))
                {
                    count = addArrayElementsTo(bondARef1, atts.getValue(i));
                }
                else if (att.equals("atomRef2"))
                {
                    count = addArrayElementsTo(bondARef2, atts.getValue(i));
                }
                else if (att.equals(Elements.ORDER))
                {
                    count = addArrayElementsTo(order, atts.getValue(i));
                }
                else if (att.equals("bondStereo") ||
                        att.equals(Elements.STEREO))
                {
                    //System.out.println("bondStereo "+atts.getValue(i));
                    count = addArrayElementsTo(bondStereo, atts.getValue(i));
                }
                else
                {
                    logger.warn("Unparsed attribute: " + att);
                }

                if (!bondsCounted)
                {
                    bondCounter += count;
                    bondsCounted = true;
                }
            }

            curRef = 0;

            break;

        case ATOMARRAY:

            boolean atomsCounted = false;

            for (int i = 0; i < atts.getLength(); i++)
            {
                String att = atts.getQName(i);
                int count = 0;

                if (att.equals("atomID"))
                {
                    count = addArrayElementsTo(elid, atts.getValue(i));
                }
                else if (att.equals("elementType"))
                {
                    count = addArrayElementsTo(elsym, atts.getValue(i));
                }
                else if (att.equals(Elements.X2))
                {
                    count = addArrayElementsTo(x2, atts.getValue(i));
                }
                else if (att.equals(Elements.Y2))
                {
                    count = addArrayElementsTo(y2, atts.getValue(i));
                }
                else if (att.equals(Elements.X3))
                {
                    count = addArrayElementsTo(x3, atts.getValue(i));
                }
                else if (att.equals(Elements.Y3))
                {
                    count = addArrayElementsTo(y3, atts.getValue(i));
                }
                else if (att.equals(Elements.Z3))
                {
                    count = addArrayElementsTo(z3, atts.getValue(i));
                }
                else if (att.equals("xFract"))
                {
                    count = addArrayElementsTo(xfract, atts.getValue(i));
                }
                else if (att.equals("yFract"))
                {
                    count = addArrayElementsTo(yfract, atts.getValue(i));
                }
                else if (att.equals("zFract"))
                {
                    count = addArrayElementsTo(zfract, atts.getValue(i));
                }
                else
                {
                    logger.warn("Unparsed attribute: " + att);
                }

                if (!atomsCounted)
                {
                    atomCounter += count;
                    atomsCounted = true;
                }
            }

            break;

        case FLOATMATRIX:
            matrixRows = null;
            matrixColumns = null;
            matrixDelimiter = null;

            for (int i = 0; i < atts.getLength(); i++)
            {
                if (atts.getQName(i).equals(Elements.TITLE))
                {
                    matrixTitle = atts.getValue(i);
                }
                else if (atts.getQName(i).equals("rows"))
                {
                    matrixRows = atts.getValue(i);
                }
                else if (atts.getQName(i).equals("columns"))
                {
                    matrixColumns = atts.getValue(i);
                }
                else if (atts.getQName(i).equals("delimiter"))
                {
                    matrixDelimiter = atts.getValue(i);
                }
            }

            break;

        case MOLECULE:
            newMolecule();
            BUILTIN = "";
            cdo.startObject("Molecule");

            for (int i = 0; i < atts.getLength(); i++)
            {
                if (atts.getQName(i).equals(Elements.TITLE))
                {
                    moleculeName = atts.getValue(i);
                }

                //else if (atts.getQName(i).equals("id"))
                //{
                //    //moleculeID= atts.getValue(i);
                //}
            }

            break;

        case LIST:
            cdo.startObject("SetOfMolecules");

            break;

        case MATRIX:
            matrix.clear();

            for (int i = 0; i < atts.getLength(); i++)
            {
                matrix.startElement(atts.getQName(i), atts.getValue(i));
            }

            break;

        case ARRAY:
            array.clear();

            for (int i = 0; i < atts.getLength(); i++)
            {
                array.startElement(atts.getQName(i), atts.getValue(i));
            }

            break;

        case SCALAR:

            if (xpath.toString().endsWith("crystal/scalar/"))
            {
                crystalScalar++;
            }
            else
            {
                scalar.clear();

                for (int i = 0; i < atts.getLength(); i++)
                {
                    scalar.startElement(atts.getQName(i), atts.getValue(i));
                }
            }

            break;

        case CRYSTAL:
            newCrystalData();
            cdo.startObject(Elements.CRYSTAL);

            for (int i = 0; i < atts.getLength(); i++)
            {
                String att = atts.getQName(i);

                if (att.equals(Elements.Z))
                {
                    cdo.setObjectProperty(Elements.CRYSTAL, Elements.Z,
                        atts.getValue(i));
                }
            }

            break;

        case SYMMETRY:

            for (int i = 0; i < atts.getLength(); i++)
            {
                String att = atts.getQName(i);

                if (att.equals("spaceGroup"))
                {
                    cdo.setObjectProperty(Elements.CRYSTAL, "spacegroup",
                        atts.getValue(i));
                }
            }

            break;
        }
    }

    /**
     * Clean all data about read bonds.
     */
    protected void newCrystalData()
    {
        unitcellparams = new double[6];
        cartesianAxesSet = false;
        crystalScalar = 0;
        a = new double[3];
        b = new double[3];
        c = new double[3];
    }

    protected void newMolecule()
    {
        atomCounter = 0;
        elsym = new Vector<String>();
        eltitles = new Vector<String>();
        elid = new Vector<String>();
        formalCharges = new Vector<String>();
        partialCharges = new Vector<String>();
        isotopes = new Vector<String>();
        x3 = new Vector<String>();
        y3 = new Vector<String>();
        z3 = new Vector<String>();
        x2 = new Vector<String>();
        y2 = new Vector<String>();
        hCounts = new Vector<String>();
        atomParities = new Vector<String>();
        bondid = new Vector<String>();
        bondARef1 = new Vector<String>();
        bondARef2 = new Vector<String>();
        order = new Vector<String>();
        bondStereo = new Vector<String>();
        bondCounter = 0;
        atomDictRefs = new Vector<String>();
        bondDictRefs = new Vector<String>();

        atomElements.put("id", elid);
        atomElements.put("elementType", elsym);
        atomElements.put(Elements.X2, x2);
        atomElements.put(Elements.Y2, y2);
        atomElements.put(Elements.X3, x3);
        atomElements.put(Elements.Y3, y3);
        atomElements.put(Elements.Z3, z3);
        atomElements.put(Elements.FORMALCHARGE, formalCharges);
        atomElements.put("isotopes", isotopes);
        atomElements.put(Elements.HYDROGENCOUNT, hCounts);

        //descriptors
        scalars = new Vector<BasicStringObject>();
        strings = new Vector<StringString>();
        arrays = new Vector<BasicStringObject>();
        matrices = new Vector<BasicStringObject>();

        // parser
        scalar = new ScalarCML(scalars);
        array = new ArrayCML(arrays);
        matrix = new MatrixCML(matrices);

        // no molecule name
        moleculeName = null;
    }

    protected void notify(String message, String systemId, int line, int column)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Message=" + message + " systemID=" + systemId +
                " line=" + line + " column=" + column);
        }
    }

    protected void setCurrentElement(String name)
    {
        //logger.debug("Current element: " + name);
        //System.out.println("Current element: " + name);
        Integer integer = (Integer) elements.get(name);

        if (integer != null)
        {
            currentElement = integer.intValue();
        }
        else
        {
            currentElement = UNKNOWN;
        }
    }

    protected void storeData()
    {
        int atomcount = elid.size();

        //        if (logger.isDebugEnabled())
        //        {
        //            logger.debug("No atom ids: " + atomcount);
        //        }
        boolean has3D = false;
        boolean has2D = false;
        boolean hasFormalCharge = false;
        boolean hasPartialCharge = false;
        boolean hasHCounts = false;
        boolean hasSymbols = false;
        boolean hasIsotopes = false;

        if (elsym.size() == atomcount)
        {
            hasSymbols = true;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("No atom symbols: " + elsym.size() + " != " +
                    atomcount);
            }
        }

        if ((x3.size() == atomcount) && (y3.size() == atomcount) &&
                (z3.size() == atomcount))
        {
            has3D = true;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                for (int i = 0; i < x3.size(); i++)
                {
                    System.out.println("x3(" + i + ")=" + x3.get(i));
                }

                for (int i = 0; i < y3.size(); i++)
                {
                    System.out.println("y3(" + i + ")=" + y3.get(i));
                }

                for (int i = 0; i < z3.size(); i++)
                {
                    System.out.println("z3(" + i + ")=" + z3.get(i));
                }

                logger.debug("No 3D info: " + x3.size() + " " + y3.size() +
                    " " + z3.size() + " != " + atomcount);
            }
        }

        if ((x2.size() == atomcount) && (y2.size() == atomcount))
        {
            has2D = true;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("No 2D info: " + x2.size() + " " + y2.size() +
                    " != " + atomcount);
            }
        }

        if (formalCharges.size() == atomcount)
        {
            hasFormalCharge = true;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("No formal Charge info: " + formalCharges.size() +
                    " != " + atomcount);
            }
        }

        if (isotopes.size() == atomcount)
        {
            hasIsotopes = true;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("No formal Charge info: " + isotopes.size() +
                    " != " + atomcount);
            }
        }

        if (partialCharges.size() == atomcount)
        {
            hasPartialCharge = true;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("No partial Charge info: " +
                    partialCharges.size() + " != " + atomcount);
            }
        }

        if (hCounts.size() == atomcount)
        {
            hasHCounts = true;
        }
        else
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("No hydrogen Count info: " + hCounts.size() +
                    " != " + atomcount);
            }
        }

        for (int i = 0; i < atomcount; i++)
        {
            //logger.info("Storing atom: " + i+", '"+elsym.elementAt(i)+"'");
            cdo.startObject(Elements.ATOM);
            cdo.setObjectProperty(Elements.ATOM, "id", (String) elid.get(i));

            // store optional atom properties
            if (hasSymbols)
            {
                cdo.setObjectProperty(Elements.ATOM, "type",
                    (String) elsym.get(i));
            }

            if (has3D)
            {
                cdo.setObjectProperty(Elements.ATOM, Elements.X3,
                    (String) x3.get(i));
                cdo.setObjectProperty(Elements.ATOM, Elements.Y3,
                    (String) y3.get(i));
                cdo.setObjectProperty(Elements.ATOM, Elements.Z3,
                    (String) z3.get(i));
            }

            if (hasFormalCharge)
            {
                //System.out.println("formalCharge="+ (String) formalCharges.elementAt(i));
                cdo.setObjectProperty(Elements.ATOM, Elements.FORMALCHARGE,
                    (String) formalCharges.get(i));
            }

            if (hasIsotopes)
            {
                //System.out.println("isotope="+ (String) isotopes.elementAt(i));
                cdo.setObjectProperty(Elements.ATOM, Elements.ISOTOPE,
                    (String) isotopes.get(i));
            }

            if (hasPartialCharge)
            {
                //logger.debug("Storing partial atomic charge...");
                cdo.setObjectProperty(Elements.ATOM, Elements.PARTIALCHARGE,
                    (String) partialCharges.get(i));
            }

            if (hasHCounts)
            {
                cdo.setObjectProperty(Elements.ATOM, Elements.HYDROGENCOUNT,
                    (String) hCounts.get(i));
            }

            if (has2D)
            {
                if (x2.get(i) != null)
                {
                    cdo.setObjectProperty(Elements.ATOM, Elements.X2,
                        (String) x2.get(i));
                }

                if (y2.get(i) != null)
                {
                    cdo.setObjectProperty(Elements.ATOM, Elements.Y2,
                        (String) y2.get(i));
                }
            }

            cdo.endObject(Elements.ATOM);
        }

        int bondcount = order.size();

        if (logger.isDebugEnabled())
        {
            logger.debug(

                //System.out.println(
            "Testing a1,a2,stereo, order: " + bondARef1.size() + "," +
                bondARef2.size() + "," + bondStereo.size() + "," +
                order.size());
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("bondcount:" + bondcount + " bondARef1:" +
                bondARef1.size() + " bondARef2:" + bondARef2.size());
        }

        if ((bondARef1.size() == bondcount) && (bondARef2.size() == bondcount))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("About to add bond info to " +
                    cdo.getClass().getName());
            }

            Iterator orders = order.iterator();
            Iterator bar1s = bondARef1.iterator();
            Iterator bar2s = bondARef2.iterator();
            Iterator stereos = bondStereo.iterator();

            while (orders.hasNext())
            {
                cdo.startObject(Elements.BOND);
                cdo.setObjectProperty(Elements.BOND, "atom1",
                    Integer.toString(elid.indexOf((String) bar1s.next())));
                cdo.setObjectProperty(Elements.BOND, "atom2",
                    Integer.toString(elid.indexOf((String) bar2s.next())));

                String bondOrder = (String) orders.next();

                if ("S".equals(bondOrder))
                {
                    cdo.setObjectProperty(Elements.BOND, Elements.ORDER, "1");
                }
                else if ("D".equals(bondOrder))
                {
                    cdo.setObjectProperty(Elements.BOND, Elements.ORDER, "2");
                }
                else if ("T".equals(bondOrder))
                {
                    cdo.setObjectProperty(Elements.BOND, Elements.ORDER, "3");
                }
                else if ("A".equals(bondOrder))
                {
                    cdo.setObjectProperty(Elements.BOND, Elements.ORDER, "1.5");
                }
                else
                {
                    cdo.setObjectProperty(Elements.BOND, Elements.ORDER,
                        bondOrder);
                }

                if (stereos.hasNext())
                {
                    cdo.setObjectProperty(Elements.BOND, Elements.STEREO,
                        (String) stereos.next());
                }

                cdo.endObject(Elements.BOND);
            }
        }
        else
        {
            logger.error("Wrong atom references: bondcount:" + bondcount +
                " bondARef1:" + bondARef1.size() + " bondARef2:" +
                bondARef2.size());
        }

        // set molecule name/title if available
        if (moleculeName != null)
        {
            cdo.setObjectProperty("Molecule", Elements.TITLE, moleculeName);
        }

        int size;
        StringString ss;
        BasicStringObject so;

        size = scalars.size();

        //System.out.println("Number of IntArrays: " + size);
        if (logger.isDebugEnabled())
        {
            logger.debug("Number of scalars: " + size);
        }

        for (int i = 0; i < size; i++)
        {
            so = (BasicStringObject) scalars.get(i);
            cdo.setObjectProperty("scalar", so.string, so.object);
        }

        size = strings.size();

        if (logger.isDebugEnabled())
        {
            logger.debug("Number of strings: " + size);
        }

        for (int i = 0; i < size; i++)
        {
            //System.out.println(strings.get(i).getClass().getName());
            ss = (StringString) strings.get(i);

            //System.out.println(ss.s2.getClass().getName());
            cdo.setObjectProperty("String", ss.getStringValue1(),
                ss.getStringValue2());
        }

        size = arrays.size();

        if (logger.isDebugEnabled())
        {
            logger.debug("Number of arrays: " + size);
        }

        for (int i = 0; i < size; i++)
        {
            so = (BasicStringObject) arrays.get(i);
            cdo.setObjectProperty("array", so.string, so.object);
        }

        size = matrices.size();

        //System.out.println("Number of FloatMatrices: " + size);
        if (logger.isDebugEnabled())
        {
            logger.debug("Number of matrices: " + size);
        }

        for (int i = 0; i < size; i++)
        {
            //System.out.println(floatMatrices.get(i).getClass().getName());
            so = (BasicStringObject) matrices.get(i);
            cdo.setObjectProperty("matrix", so.string, so.object);
        }
    }

    private int addArrayElementsTo(List<String> toAddto, String array)
    {
        StringTokenizer tokenizer = new StringTokenizer(array);
        int i = 0;

        while (tokenizer.hasMoreElements())
        {
            toAddto.add(tokenizer.nextToken());
            i++;
        }

        return i;
    }

    private void initialize()
    {
        elements.put("string", new Integer(STRING));
        elements.put("link", new Integer(LINK));
        elements.put("float", new Integer(FLOAT));
        elements.put("integer", new Integer(INTEGER));
        elements.put("stringArray", new Integer(STRINGARRAY));
        elements.put("floatArray", new Integer(FLOATARRAY));
        elements.put("integerArray", new Integer(INTEGERARRAY));
        elements.put("floatMatrix", new Integer(FLOATMATRIX));
        elements.put("coordinate2", new Integer(COORDINATE2));
        elements.put("coordinate3", new Integer(COORDINATE3));
        elements.put("angle", new Integer(ANGLE));
        elements.put("torsion", new Integer(TORSION));
        elements.put("list", new Integer(LIST));
        elements.put("molecule", new Integer(MOLECULE));
        elements.put("atom", new Integer(ATOM));
        elements.put("atomArray", new Integer(ATOMARRAY));
        elements.put("bond", new Integer(BOND));
        elements.put("bondArray", new Integer(BONDARRAY));
        elements.put("electron", new Integer(ELECTRON));
        elements.put("reaction", new Integer(REACTION));
        elements.put("crystal", new Integer(CRYSTAL));
        elements.put("sequence", new Integer(SEQUENCE));
        elements.put("feature", new Integer(FEATURE));
        elements.put("matrix", new Integer(MATRIX));
        elements.put("array", new Integer(ARRAY));
        elements.put("scalar", new Integer(SCALAR));
        elements.put("bondStereo", new Integer(BOND_STEREO));
        elements.put("name", new Integer(NAME));
        elements.put("symmetry", new Integer(SYMMETRY));
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
