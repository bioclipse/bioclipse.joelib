///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: MoleculeFileCDO.java,v $
//Purpose:  Chemical Markup Language.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu,
//                      egonw@sci.kun.nl, wegner@users.sourceforge.net
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

import joelib2.data.BasicElementHolder;

import joelib2.feature.FeatureHelper;

import joelib2.feature.result.AtomDoubleResult;
import joelib2.feature.result.BitArrayResult;
import joelib2.feature.result.BooleanResult;
import joelib2.feature.result.DoubleArrayResult;
import joelib2.feature.result.DoubleMatrixResult;
import joelib2.feature.result.DoubleResult;
import joelib2.feature.result.IntArrayResult;
import joelib2.feature.result.IntMatrixResult;
import joelib2.feature.result.IntResult;
import joelib2.feature.result.StringResult;

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.MoleculeCallback;

import joelib2.molecule.Atom;
import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Bond;
import joelib2.molecule.BondHelper;
import joelib2.molecule.IsomerismHelper;
import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeVector;

import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.PairData;

import joelib2.util.types.BasicBondInt;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * CDO object needed as interface with the JCFL library for reading CML
 * encoded data.
 *
 * @.author    egonw
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license LGPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:35 $
 * @.cite rr99b
 * @.cite mr01
 * @.cite gmrw01
 * @.cite wil01
 * @.cite mr03
 * @.cite mrww04
 */
public class MoleculeFileCDO implements CDOInterface
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.cml.MoleculeFileCDO");

    //~ Instance fields ////////////////////////////////////////////////////////

    private Map<String, Integer> atomEnumeration = new Hashtable<String, Integer>();
    private int bond_a1;
    private int bond_a2;
    private int bond_EZ;
    private int bond_order;
    private int bond_stereo;
    private List<Double> c2Dx = new Vector<Double>();
    private List<Double> c2Dy = new Vector<Double>();
    private List<Double> c3Dx = new Vector<Double>();
    private List<Double> c3Dy = new Vector<Double>();
    private List<Double> c3Dz = new Vector<Double>();

    private Atom currentAtom;
    private Molecule currentMolecule;
    private MoleculeVector currentSetOfMolecules;

    private List<BasicBondInt> ezInformations = new Vector<BasicBondInt>(10);
    private List<Integer> hydrogenCount = new Vector<Integer>();
    private MoleculeCallback moleculeCallback;
    private int molIndex;
    private int numberOfAtoms;
    private List<Double> partialCharge = new Vector<Double>();
    private Map<String, String> strings = new Hashtable<String, String>(10);
    private Map<String, String> unparsed = new Hashtable<String, String>(10);
    private double x_2D;
    private double x_3D;
    private double y_2D;
    private double y_3D;
    private double z_3D;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Procedure required by the CDOInterface. This methodName is only
     * supposed to be called by the JCFL library
     *
     * @return   Description of the Return Value
     */
    public CDOAcceptedObjects acceptObjects()
    {
        CDOAcceptedObjects objects = new CDOAcceptedObjects();
        objects.add("SetOfMolecules");
        objects.add("Molecule");
        objects.add("Fragment");
        objects.add("Atom");
        objects.add("Bond");
        objects.add("scalar");
        objects.add("String");
        objects.add("array");
        objects.add("matrix");

        /*objects.add("Animation");
        objects.add("Frame");
        objects.add("Crystal");
        objects.add("a-axis");
        objects.add("b-axis");
        objects.add("c-axis");
        objects.add("SetOfReactions");
        objects.add("Reactions");
        objects.add("Reactant");
        objects.add("Product");*/
        return objects;
    }

    /**
     * Procedure required by the CDOInterface. This methodName is only
     * supposed to be called by the JCFL library
     */
    public void endDocument()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("CML molecule added:" + currentMolecule);
        }
    }

    /**
     * Procedure required by the CDOInterface. This methodName is only
     * supposed to be called by the JCFL library
     *
     * @param objectType  Description of the Parameter
     */
    public void endObject(String objectType)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("END: " + objectType);
        }

        if (objectType.equals("Molecule"))
        {
            currentMolecule.endModify();
            currentMolecule.setOutputType(BasicIOTypeHolder.instance()
                .getIOType("SDF"));

            //System.out.println(""+currentMolecule.toString() );
            if (logger.isDebugEnabled())
            {
                logger.debug("Molecule added: \n" + currentMolecule.toString());
            }

            if (currentMolecule.has2D())
            {
                // are 3D coordinates also available ?
                // should never happen !!!!!!!!!
                if ((c3Dx.size() != 0) && (c3Dy.size() != 0) &&
                        (c3Dz.size() != 0))
                {
                    // o.k., available
                    if ((c3Dx.size() != c3Dy.size()) &&
                            (c3Dy.size() != c3Dz.size()))
                    {
                        logger.error("3D coordinats are inconsistent (x=" +
                            c3Dx.size() + ",y=" + c3Dy.size() + ",z=" +
                            c3Dz.size() + ").");
                    }
                    else
                    {
                        double[] c3DxArray = new double[c3Dx.size()];
                        double[] c3DyArray = new double[c3Dy.size()];
                        double[] c3DzArray = new double[c3Dz.size()];

                        for (int i = 0; i < c3DxArray.length; i++)
                        {
                            c3DxArray[i] = ((Double) c3Dx.get(i)).doubleValue();
                            c3DyArray[i] = ((Double) c3Dy.get(i)).doubleValue();
                            c3DzArray[i] = ((Double) c3Dz.get(i)).doubleValue();
                        }

                        AtomDoubleResult adr = new AtomDoubleResult();
                        adr.setDoubleArray(c3DxArray);

                        BasicPairData dp = new BasicPairData();
                        dp.setKey(FeatureHelper.COORDS_3D_X_IDENTIFIER);
                        dp.setKeyValue(adr);
                        currentMolecule.addData(dp);
                        adr = new AtomDoubleResult();
                        adr.setDoubleArray(c3DyArray);
                        dp = new BasicPairData();
                        dp.setKey(FeatureHelper.COORDS_3D_Y_IDENTIFIER);
                        dp.setKeyValue(adr);
                        currentMolecule.addData(dp);
                        adr = new AtomDoubleResult();
                        adr.setDoubleArray(c3DzArray);
                        dp = new BasicPairData();
                        dp.setKey(FeatureHelper.COORDS_3D_Z_IDENTIFIER);
                        dp.setKeyValue(adr);
                        currentMolecule.addData(dp);
                    }
                }
            }

            if (currentMolecule.has3D())
            {
                // are 2D coordinates also available ?
                if ((c2Dx.size() != 0) && (c2Dy.size() != 0))
                {
                    // o.k., available
                    if (c2Dx.size() != c2Dy.size())
                    {
                        logger.error("2D coordinats are inconsistent (x=" +
                            c3Dx.size() + ",y=" + c3Dy.size() + ").");
                    }
                    else
                    {
                        double[] c2DxArray = new double[c2Dx.size()];
                        double[] c2DyArray = new double[c2Dy.size()];

                        for (int i = 0; i < c2DxArray.length; i++)
                        {
                            c2DxArray[i] = ((Double) c2Dx.get(i)).doubleValue();
                            c2DyArray[i] = ((Double) c2Dy.get(i)).doubleValue();
                        }

                        AtomDoubleResult adr = new AtomDoubleResult();
                        adr.setDoubleArray(c2DxArray);

                        BasicPairData dp = new BasicPairData();
                        dp.setKey(FeatureHelper.COORDS_2D_X_IDENTIFIER);
                        dp.setKeyValue(adr);
                        currentMolecule.addData(dp);
                        adr = new AtomDoubleResult();
                        adr.setDoubleArray(c2DyArray);
                        dp = new BasicPairData();
                        dp.setKey(FeatureHelper.COORDS_2D_Y_IDENTIFIER);
                        dp.setKeyValue(adr);
                        currentMolecule.addData(dp);
                    }
                }
            }

            // resolve cis/trans isomerism informations
            BasicBondInt bi = null;

            for (int i = 0; i < ezInformations.size(); i++)
            {
                bi = (BasicBondInt) ezInformations.get(i);

                //System.out.println("Bond isomerism: "+bi.bond+" "+ bi.i);
                IsomerismHelper.setCisTransBond(bi.bond, bi.intValue);
            }

            PairData pairData;

            // try to parse string entries, to check format and
            // enable correct storage functionality for other output
            // formats
            String tmpS;

            for (Iterator e = unparsed.keySet().iterator(); e.hasNext();)
            {
                tmpS = (String) e.next();

                // convert descriptor entry from StringBuffer to String
                pairData = currentMolecule.getData(tmpS, false);

                StringBuffer sb = (StringBuffer) pairData.getKeyValue();
                pairData.setKeyValue(sb.toString());

                // parse descriptor entry
                //System.out.println("Try parsing: "+currentMolecule.getData(tmpS, false));
                try
                {
                    pairData = currentMolecule.getData(tmpS, true);
                }
                catch (Exception pe)
                {
                    logger.error("Error in parsing '" + tmpS + "': " +
                        currentMolecule.getData(tmpS, false));
                    pe.printStackTrace();
                }
            }

            for (Iterator e = strings.keySet().iterator(); e.hasNext();)
            {
                tmpS = (String) e.next();

                // parse descriptor entry
                //                              System.out.println("Try parsing: "+currentMolecule.getData(tmpS, false));
                try
                {
                    pairData = currentMolecule.getData(tmpS, true);

                    //PairData pairData;
                    //pairData = (PairData) genericData;
                    //System.out.println(pairData.getValue().getClass().getName());
                    //System.out.println("parsed: "+((DescResult)pairData.getValue()).toString(IOTypeHolder.instance().getIOType("SDF")));
                }
                catch (Exception pe)
                {
                    logger.error("Error in parsing '" + tmpS + "': " +
                        currentMolecule.getData(tmpS, false));
                    pe.printStackTrace();
                }
            }

            if (currentSetOfMolecules != null)
            {
                // store cloned molecule with all descriptors
                currentSetOfMolecules.addMol((Molecule) currentMolecule.clone(
                        true));

                //System.out.println("Add molecule.");
            }

            if (moleculeCallback != null)
            {
                moleculeCallback.handleMolecule((Molecule) currentMolecule
                    .clone(true));
            }

            //currentMolecule = null;
        }

        //              else if (objectType.equals("Frame"))
        //              {
        //              }
        //              else if (objectType.equals("Animation"))
        //              {
        //              }
        else if (objectType.equals("Atom"))
        {
            if (!Double.isNaN(z_3D))
            {
                currentAtom.setCoords3D(x_3D, y_3D, z_3D);
            }
            else
            {
                currentAtom.setCoords3D(x_2D, y_2D, 0.0);
            }

            //System.out.println("currentAtom:"+currentAtom.getIdx()+" "+currentAtom);
            //currentMolecule.addAtom(currentAtom);
        }
        else if (objectType.equals("Bond"))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Bond: " + bond_a1 + ", " + bond_a2 + ", order=" +
                    bond_order + ", stereo=" + bond_stereo);
            }

            currentMolecule.addBond(bond_a1, bond_a2, bond_order, bond_stereo);

            //System.out.println("bond_a1:"+bond_a1+" bond_a2:"+bond_a2+" bond_order:"+bond_order+" "+bond_stereo);
            if (bond_EZ != IsomerismHelper.EZ_ISOMERISM_UNDEFINED)
            {
                Bond bond = currentMolecule.getBond(
                        currentMolecule.getBondsSize() - 1);
                ezInformations.add(new BasicBondInt(bond, bond_EZ));
            }
        }
    }

    // procedures required by CDOInterface

    /**
     * Procedure required by the CDOInterface. This methodName is only
     * supposed to be called by the JCFL library
     *
     * @param type   The new documentProperty value
     * @param value  The new documentProperty value
     */
    public void setDocumentProperty(String type, Object value)
    {
    }

    //    public MoleculeFileCDO(Molecule mol)
    //    {
    //      currentMolecule = mol;
    //    }
    public void setMolecule(Molecule mol)
    {
        currentMolecule = mol;
    }

    public void setMoleculeCallback(MoleculeCallback _moleculeCallback)
    {
        moleculeCallback = _moleculeCallback;
    }

    public void setMoleculeSetOfMolecules(MoleculeVector _currentSetOfMolecules)
    {
        currentSetOfMolecules = _currentSetOfMolecules;
    }

    /**
     * Procedure required by the CDOInterface. This methodName is only
     * supposed to be called by the JCFL library
     *
     * @param objectType     The new objectProperty value
     * @param propertyType   The new objectProperty value
     * @param propertyValue  The new objectProperty value
     */
    public void setObjectProperty(String objectType, String propertyType,
        Object propertyValue)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("objectType: " + objectType);
            logger.debug("propType: " + propertyType);
            logger.debug("property: " + propertyValue);
        }

        String stringValue = null;

        if (propertyValue instanceof String)
        {
            stringValue = (String) propertyValue;
        }

        if (objectType.equals("Atom"))
        {
            Double dTmp;

            if (propertyType.equals("type"))
            {
                currentAtom.setType(stringValue);
                currentAtom.setAtomicNumber(BasicElementHolder.instance()
                    .getAtomicNum(stringValue));
            }
            else if (propertyType.equals("x2"))
            {
                dTmp = new Double(stringValue);
                x_2D = dTmp.doubleValue();
                c2Dx.add(dTmp);
            }
            else if (propertyType.equals("y2"))
            {
                dTmp = new Double(stringValue);
                y_2D = dTmp.doubleValue();
                c2Dy.add(dTmp);
            }
            else if (propertyType.equals("x3"))
            {
                dTmp = new Double(stringValue);
                x_3D = dTmp.doubleValue();
                c3Dx.add(dTmp);
            }
            else if (propertyType.equals("y3"))
            {
                dTmp = new Double(stringValue);
                y_3D = dTmp.doubleValue();
                c3Dy.add(dTmp);
            }
            else if (propertyType.equals("z3"))
            {
                dTmp = new Double(stringValue);
                z_3D = dTmp.doubleValue();
                c3Dz.add(dTmp);
            }
            else if (propertyType.equals("formalCharge"))
            {
                currentAtom.setFormalCharge(new Integer(stringValue)
                    .intValue());
            }
            else if (propertyType.equals("charge"))
            {
                partialCharge.add(new Double(stringValue));

                //TODO: add those elements to molecule after endModify was called !!!
                //-->partial charge
            }
            else if (propertyType.equals("hydrogenCount"))
            {
                hydrogenCount.add(new Integer(stringValue));

                //TODO: add those elements to molecule after endModify was called !!!
                //-->implicite valence
            }
            else if (propertyType.equals("isotope"))
            {
                //System.out.println("SET ISOTOPE: "+stringValue);
                currentAtom.setIsotope(new Integer(stringValue).intValue());
            }
            else if (propertyType.equals("id"))
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("id" + stringValue);
                }

                atomEnumeration.put(stringValue, new Integer(numberOfAtoms));
            }
        }
        else if (objectType.equals("Bond"))
        {
            //System.out.println(propertyType+"="+stringValue);
            if (propertyType.equals("atom1"))
            {
                bond_a1 = new Integer(stringValue).intValue() + 1;

                //                System.out.println("atom1:"+bond_a1);
            }
            else if (propertyType.equals("atom2"))
            {
                bond_a2 = new Integer(stringValue).intValue() + 1;
            }
            else if (propertyType.equals("order"))
            {
                double BO = 1.0;
                bond_order = (int) BO;

                //bond_order = new Integer(propertyValue).intValue();
                try
                {
                    bond_order = (int) Double.parseDouble(stringValue);
                }
                catch (Exception e)
                {
                    logger.error("Cannot convert to double: " + stringValue);
                }

                if (BO == 1.5)
                {
                    bond_order = BondHelper.AROMATIC_BO;
                }

                //System.out.println("CDO BO="+bond_order);
            }
            else if (propertyType.equals("stereo"))
            {
                //System.out.println("Bond: stereo '"+stringValue+"'");
                bond_stereo = 0;
                bond_EZ = IsomerismHelper.EZ_ISOMERISM_UNDEFINED;

                if (stringValue.equalsIgnoreCase("H"))
                {
                    bond_stereo |= BondHelper.IS_HASH;
                }
                else if (stringValue.equalsIgnoreCase("W"))
                {
                    bond_stereo |= BondHelper.IS_WEDGE;
                }
                else if (stringValue.equalsIgnoreCase("T"))
                {
                    bond_EZ = IsomerismHelper.E_ISOMERISM;
                }
                else if (stringValue.equalsIgnoreCase("C"))
                {
                    bond_EZ = IsomerismHelper.Z_ISOMERISM;
                }
            }
        }
        else if (objectType.equals("scalar"))
        {
            if (propertyValue instanceof DoubleResult)
            {
                DoubleResult dr;

                //System.out.println(propertyType+"="+propertyValue);
                dr = (DoubleResult) propertyValue;

                //                      if (ir.value == null)
                //                      {
                //                              logger.error(
                //                                      "Double entry " + propertyType + "=" + propertyValue + " was not successfully parsed.");
                //                      }
                //                      else
                //                      {
                BasicPairData dp = new BasicPairData();
                dp.setKey(propertyType);
                dp.setKeyValue(dr);
                currentMolecule.addData(dp);

                //                      }
            }
            else if (propertyValue instanceof IntResult)
            {
                IntResult ir;

                //System.out.println(propertyType+"="+propertyValue);
                ir = (IntResult) propertyValue;

                //                      if (ir.value == null)
                //                      {
                //                              logger.error(
                //                                      "Integer entry " + propertyType + "=" + propertyValue + " was not successfully parsed.");
                //                      }
                //                      else
                //                      {
                BasicPairData dp = new BasicPairData();
                dp.setKey(propertyType);
                dp.setKeyValue(ir);
                currentMolecule.addData(dp);

                //                      }
            }
            else if (propertyValue instanceof StringResult)
            {
                StringResult sr;

                //System.out.println("SR:"+propertyType+"="+propertyValue);
                sr = (StringResult) propertyValue;

                if (propertyType.length() != 0)
                {
                    // use Hashtable to combine the String pieces
                    if (currentMolecule.hasData(propertyType))
                    {
                        PairData pairData = currentMolecule.getData(
                                propertyType, false);

                        StringResult previous = (StringResult) pairData
                            .getKeyValue();
                        StringBuffer sb = new StringBuffer(
                                previous.value.length() + 100);
                        sb.append(previous.value);
                        sb.append(sr.value);
                        previous.value = sb.toString();
                    }
                    else
                    {
                        BasicPairData dp = new BasicPairData();
                        dp.setKey(propertyType);
                        dp.setKeyValue(sr);
                        currentMolecule.addData(dp);
                        strings.put(propertyType, "");
                    }
                }
            }
            else if (propertyValue instanceof BooleanResult)
            {
                BooleanResult br;

                //System.out.println(propertyType+"="+propertyValue);
                br = (BooleanResult) propertyValue;

                //                      if (ir.value == null)
                //                      {
                //                              logger.error(
                //                                      "Boolean entry " + propertyType + "=" + propertyValue + " was not successfully parsed.");
                //                      }
                //                      else
                //                      {
                BasicPairData dp = new BasicPairData();
                dp.setKey(propertyType);
                dp.setKeyValue(br);
                currentMolecule.addData(dp);

                //                      }
            }
        }
        else if (objectType.equals("String"))
        {
            //System.out.println("propertyType:"+propertyType);
            //System.out.println("stringValue:"+stringValue);
            if (propertyType.length() != 0)
            {
                // use Hashtable to combine the String pieces
                if (currentMolecule.hasData(propertyType))
                {
                    PairData pairData = currentMolecule.getData(propertyType,
                            false);
                    StringBuffer sb = (StringBuffer) pairData.getKeyValue();
                    sb.append(stringValue);
                }
                else
                {
                    BasicPairData dp = new BasicPairData();
                    dp.setKey(propertyType);

                    // use StringBuffer instead of String to be more efficient
                    // The StringBuffer will be replaced by String, when
                    // Molecule-End-Tag occurs
                    StringBuffer sb = new StringBuffer(200);
                    sb.append(stringValue);
                    dp.setKeyValue(sb);
                    currentMolecule.addData(dp);
                }

                unparsed.put(propertyType, "");
            }
            else
            {
                logger.error("No title defined for String entry: " +
                    stringValue);
            }
        }
        else if (objectType.equals("array"))
        {
            if (propertyValue instanceof DoubleArrayResult)
            {
                DoubleArrayResult dar;
                dar = (DoubleArrayResult) propertyValue;

                if (dar.getDoubleArray() == null)
                {
                    logger.error("Double array entry " + propertyType + "=" +
                        propertyValue + " was not successfully parsed.");
                }
                else
                {
                    BasicPairData dp = new BasicPairData();
                    dp.setKey(propertyType);
                    dp.setKeyValue(dar);
                    currentMolecule.addData(dp);
                }
            }
            else if (propertyValue instanceof IntArrayResult)
            {
                IntArrayResult iar;
                iar = (IntArrayResult) propertyValue;

                if (iar.getIntArray() == null)
                {
                    logger.error("Integer array entry " + propertyType + "=" +
                        propertyValue + " was not successfully parsed.");
                }
                else
                {
                    BasicPairData dp = new BasicPairData();
                    dp.setKey(propertyType);
                    dp.setKeyValue(iar);
                    currentMolecule.addData(dp);
                }
            }
            else if (propertyValue instanceof BitArrayResult)
            {
                BitArrayResult bar;
                bar = (BitArrayResult) propertyValue;

                if (bar.value == null)
                {
                    logger.error("Boolean array entry " + propertyType + "=" +
                        propertyValue + " was not successfully parsed.");
                }
                else
                {
                    BasicPairData dp = new BasicPairData();
                    dp.setKey(propertyType);
                    dp.setKeyValue(bar);
                    currentMolecule.addData(dp);
                }
            }
        }
        else if (objectType.equals("matrix"))
        {
            if (propertyValue instanceof DoubleMatrixResult)
            {
                DoubleMatrixResult dmr;

                //System.out.println(propertyValue.getClass().getName());
                dmr = (DoubleMatrixResult) propertyValue;

                if (dmr.value == null)
                {
                    logger.error("Float matrix entry " + propertyType + "=" +
                        propertyValue + " was not successfully parsed.");
                }
                else
                {
                    BasicPairData dp = new BasicPairData();
                    dp.setKey(propertyType);
                    dp.setKeyValue(dmr);
                    currentMolecule.addData(dp);
                }

                //System.out.println("SDF"+dmr.toString(IOTypeHolder.instance().getIOType("SDF")));
                //System.out.println("CML"+dmr.toString(IOTypeHolder.instance().getIOType("CML")));
            }
            else if (propertyValue instanceof IntMatrixResult)
            {
                IntMatrixResult dmr;

                //System.out.println(propertyType+"="+propertyValue);
                dmr = (IntMatrixResult) propertyValue;

                if (dmr.value == null)
                {
                    logger.error("Integer matrix entry " + propertyType + "=" +
                        propertyValue + " was not successfully parsed.");
                }
                else
                {
                    BasicPairData dp = new BasicPairData();
                    dp.setKey(propertyType);
                    dp.setKeyValue(dmr);
                    currentMolecule.addData(dp);
                }
            }

            //System.out.println("Set object:"+objectType+" pType:"+propertyType+" pValue:"+propertyValue);
            if (logger.isDebugEnabled())
            {
                System.out.println("Set object:" + objectType + " pType:" +
                    propertyType + " pValue:" + propertyValue);
            }
        }
        else if (objectType.equals("Molecule"))
        {
            if (propertyType.equals("title"))
            {
                currentMolecule.setTitle((String) propertyValue);
            }
        }
    }

    /**
     * Procedure required by the CDOInterface. This methodName is only
     * supposed to be called by the JCFL library
     */
    public void startDocument()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Start new CML document");
        }

        //currentSetOfMolecules = new JOEMolVector();
        //currentMolecule = new Molecule();
    }

    /**
     * Procedure required by the CDOInterface. This methodName is only
     * supposed to be called by the JCFL library
     *
     * @param objectType  Description of the Parameter
     */
    public void startObject(String objectType)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("START:" + objectType);
        }

        if (objectType.equals("Molecule"))
        {
            molIndex++;

            if (currentMolecule == null)
            {
                if (moleculeCallback == null)
                {
                    logger.error(
                        "No molecule initialized. Use setMolecule(Molecule mol).");
                }

                currentMolecule = new BasicConformerMolecule(BasicIOTypeHolder
                        .instance().getIOType("CML"),
                        BasicIOTypeHolder.instance().getIOType("SDF"));
            }

            //                  if (currentSetOfMolecules == null)
            //                  {
            //                          currentSetOfMolecules = new JOEMolVector();
            //                  }
            //          if(molIndex!=1)   currentMolecule = new Molecule();
            //currentMolecule = new Molecule();
            atomEnumeration.clear();
            currentMolecule.clear();
            strings.clear();
            unparsed.clear();
            currentMolecule.beginModify();
            x_2D = y_2D = x_3D = y_3D = z_3D = Double.NaN;
            c2Dx.clear();
            c2Dy.clear();
            c3Dx.clear();
            c3Dy.clear();
            c3Dz.clear();
        }
        else if (objectType.equals("Atom"))
        {
            currentAtom = currentMolecule.newAtom(true);

            // set H atom
            currentAtom.setAtomicNumber(1);
            currentAtom.setType(BasicElementHolder.instance().getSymbol(1));

            if (logger.isDebugEnabled())
            {
                logger.debug("Atom # " + numberOfAtoms);
            }

            numberOfAtoms++;
        }

        //              else if (objectType.equals("Bond"))
        //              {
        //              }
        //              else if (objectType.equals("Animation"))
        //              {
        //              }
        //              else if (objectType.equals("Frame"))
        //              {
        //              }
        else if (objectType.equals("SetOfMolecules"))
        {
            //currentSetOfMolecules = new JOEMolVector();
            //currentMolecule = new Molecule();
        }

        //              else if (objectType.equals("Crystal"))
        //              {
        //              }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
