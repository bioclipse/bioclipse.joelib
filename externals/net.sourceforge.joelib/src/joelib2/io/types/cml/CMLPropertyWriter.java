///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: CMLPropertyWriter.java,v $
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
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.io.types.cml;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.FeatureResult;

import joelib2.feature.result.BitArrayResult;
import joelib2.feature.result.BitResult;
import joelib2.feature.result.BooleanResult;
import joelib2.feature.result.DoubleArrayResult;
import joelib2.feature.result.DoubleMatrixResult;
import joelib2.feature.result.DoubleResult;
import joelib2.feature.result.IntArrayResult;
import joelib2.feature.result.IntMatrixResult;
import joelib2.feature.result.IntResult;
import joelib2.feature.result.StringArrayResult;
import joelib2.feature.result.StringResult;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;

import joelib2.io.types.ChemicalMarkupLanguage;
import joelib2.io.types.cml.elements.Elements;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.PairData;

import joelib2.util.iterator.PairDataIterator;

import joelib2.util.types.StringString;

import java.io.PrintStream;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;


/**
 * Helper class for a CML molecule property (descriptor) writer.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:35 $
 * @.cite rr99b
 * @.cite mr01
 * @.cite gmrw01
 * @.cite wil01
 * @.cite mr03
 * @.cite mrww04
 */
public class CMLPropertyWriter
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            CMLPropertyWriter.class.getName());
    private static BasicIOType cml = BasicIOTypeHolder.instance().getIOType(
            "CML");
    public static String COORDINATES_2D_X = "coordinates2Dx";
    public static String COORDINATES_2D_Y = "coordinates2Dy";
    public static String COORDINATES_3D_X = "coordinates3Dx";
    public static String COORDINATES_3D_Y = "coordinates3Dy";
    public static String COORDINATES_3D_Z = "coordinates3Dz";

    //~ Methods ////////////////////////////////////////////////////////////////

    public static void writePairData(CMLWriterProperties writerProp,
        PrintStream ps, PairData pairData)
    {
        String attrib = pairData.getKey();
        Object value = pairData.getKeyValue();

        if (logger.isDebugEnabled())
        {
            logger.debug(attrib + "=" + value + " is of type " +
                value.getClass().getName());
        }

        // don't write coordinate atom property helper arrays
        if (attrib.equals(CMLPropertyWriter.COORDINATES_2D_X) ||
                attrib.equals(CMLPropertyWriter.COORDINATES_2D_Y) ||
                attrib.equals(CMLPropertyWriter.COORDINATES_3D_X) ||
                attrib.equals(CMLPropertyWriter.COORDINATES_3D_Y) ||
                attrib.equals(CMLPropertyWriter.COORDINATES_3D_Z))
        {
            return;
        }

        if (writerProp.getCMLversion() == ChemicalMarkupLanguage.CML_VERSION_1)
        {
            if (value instanceof IntResult)
            {
                writeScalarCML1(writerProp, ps, Elements.INTEGER, attrib,
                    value.toString(), ((ResultCMLProperties) value));
            }
            else if (value instanceof DoubleResult)
            {
                writeScalarCML1(writerProp, ps, Elements.FLOAT, attrib,
                    value.toString(), ((ResultCMLProperties) value));
            }
            else if (value instanceof IntArrayResult)
            {
                if (((IntArrayResult) value).getIntArray() == null)
                {
                    logger.error("Integer array '" + attrib +
                        "' is  not defined.");
                }
                else
                {
                    writeArrayCML1(writerProp, ps, Elements.INTEGERARRAY,
                        attrib, ((IntArrayResult) value).toString(cml),
                        ((IntArrayResult) value).getIntArray().length,
                        (ResultCMLProperties) value);
                }
            }
            else if (value instanceof DoubleArrayResult)
            {
                if (((DoubleArrayResult) value).getDoubleArray() == null)
                {
                    logger.error("Double array '" + attrib +
                        "' is  not defined.");
                }
                else
                {
                    writeArrayCML1(writerProp, ps, Elements.FLOATARRAY, attrib,
                        ((DoubleArrayResult) value).toString(cml),
                        ((DoubleArrayResult) value).getDoubleArray().length,
                        (ResultCMLProperties) value);
                }
            }
            else if (value instanceof StringArrayResult)
            {
                if (((StringArrayResult) value).getStringArray() == null)
                {
                    logger.error("String array '" + attrib +
                        "' is  not defined.");
                }
                else
                {
                    writeArrayCML1(writerProp, ps, Elements.STRINGARRAY, attrib,
                        ((DoubleArrayResult) value).toString(cml),
                        ((DoubleArrayResult) value).getDoubleArray().length,
                        (ResultCMLProperties) value);
                }
            }
            else if (value instanceof DoubleMatrixResult)
            {
                if (((DoubleMatrixResult) value).value == null)
                {
                    logger.error("Double matrix array '" + attrib +
                        "' is  not defined.");
                }
                else
                {
                    Map<String, String> attributes = new Hashtable<String, String>();
                    attributes.clear();
                    attributes.put(Elements.TITLE, attrib);
                    attributes.put(Elements.ROWS,
                        Integer.toString(
                            ((DoubleMatrixResult) value).value.length));
                    attributes.put(Elements.COLUMNS,
                        Integer.toString(
                            ((DoubleMatrixResult) value).value[0].length));

                    Enumeration<StringString> e = ((ResultCMLProperties) value)
                        .getCMLProperties();
                    writeMatrixProperties(e, attributes);

                    CMLMoleculeWriterBase.writeOpenTag(ps, writerProp,
                        "floatMatrix", attributes, false);
                    CMLMoleculeWriterBase.write(ps,
                        ((DoubleMatrixResult) value).toString(cml));
                    CMLMoleculeWriterBase.writeCloseTag(ps, writerProp,
                        "floatMatrix");
                }
            }
            else
            {
                Map<String, String> attributes = new Hashtable<String, String>();
                attributes.clear();
                attributes.put(Elements.TITLE, attrib);
                CMLMoleculeWriterBase.writeOpenTag(ps, writerProp,
                    Elements.STRING, attributes, false);
                CMLMoleculeWriterBase.write(ps,
                    XMLSpecialCharacter.convertPlain2XML(
                        (String) value.toString()));
                CMLMoleculeWriterBase.writeCloseTag(ps, writerProp,
                    Elements.STRING);
            }
        }
        else
        {
            if (value instanceof IntResult)
            {
                writeScalarCML2(writerProp, ps, Elements.XSD_INTEGER, attrib,
                    value.toString(), ((ResultCMLProperties) value));
            }
            else if (value instanceof DoubleResult)
            {
                writeScalarCML2(writerProp, ps, Elements.XSD_DOUBLE, attrib,
                    value.toString(), ((ResultCMLProperties) value));
            }
            else if (value instanceof StringResult)
            {
                writeScalarCML2(writerProp, ps, Elements.XSD_STRING, attrib,
                    XMLSpecialCharacter.convertPlain2XML(
                        ((StringResult) value).toString(cml)),
                    ((ResultCMLProperties) value));
            }
            else if (value instanceof BooleanResult)
            {
                writeScalarCML2(writerProp, ps, Elements.XSD_BOOLEAN, attrib,
                    value.toString(), ((ResultCMLProperties) value));
            }
            else if (value instanceof IntArrayResult)
            {
                if (((IntArrayResult) value).getIntArray() == null)
                {
                    logger.error("Integer array '" + attrib +
                        "' is  not defined.");
                }
                else
                {
                    writeArrayCML2(writerProp, ps, Elements.XSD_INTEGER, attrib,
                        ((IntArrayResult) value).toString(cml),
                        ((IntArrayResult) value).getIntArray().length,
                        (ResultCMLProperties) value);
                }
            }
            else if (value instanceof DoubleArrayResult)
            {
                if (((DoubleArrayResult) value).getDoubleArray() == null)
                {
                    logger.error("Double array '" + attrib +
                        "' is  not defined.");
                }
                else
                {
                    writeArrayCML2(writerProp, ps, Elements.XSD_DOUBLE, attrib,
                        ((DoubleArrayResult) value).toString(cml),
                        ((DoubleArrayResult) value).getDoubleArray().length,
                        (ResultCMLProperties) value);
                }
            }
            else if (value instanceof StringArrayResult)
            {
                if (((StringArrayResult) value).getStringArray() == null)
                {
                    logger.error("String array '" + attrib +
                        "' is  not defined.");
                }
                else
                {
                    writeArrayCML2(writerProp, ps, Elements.XSD_STRING, attrib,
                        ((StringArrayResult) value).toString(cml),
                        ((StringArrayResult) value).getStringArray().length,
                        (ResultCMLProperties) value);
                }
            }
            else if (value instanceof BitArrayResult)
            {
                if (((BitArrayResult) value).value == null)
                {
                    logger.error("Boolean array '" + attrib +
                        "' is  not defined.");
                }
                else
                {
                    boolean[] array = ((BitArrayResult) value).value
                        .toBoolArray();
                    writeArrayCML2(writerProp, ps, Elements.XSD_BOOLEAN, attrib,
                        ((BitArrayResult) value).toString(cml), array.length,
                        (ResultCMLProperties) value);
                }
            }
            else if (value instanceof BitResult)
            {
                if (((BitResult) value).value == null)
                {
                    logger.error("Boolean array '" + attrib +
                        "' is  not defined.");
                }
                else
                {
                    boolean[] array = ((BitResult) value).value.toBoolArray();

                    writeArrayCML2(writerProp, ps, Elements.XSD_BOOLEAN, attrib,
                        ((BitResult) value).toString(cml), array.length,
                        (ResultCMLProperties) value);
                }
            }
            else if (value instanceof DoubleMatrixResult)
            {
                if (((DoubleMatrixResult) value).value == null)
                {
                    logger.error("Double matrix '" + attrib +
                        "' is  not defined.");
                }
                else
                {
                    writeMatrixCML2(writerProp, ps, Elements.XSD_DOUBLE, attrib,
                        ((DoubleMatrixResult) value).toString(cml),
                        ((DoubleMatrixResult) value).value.length,
                        ((DoubleMatrixResult) value).value[0].length,
                        ((ResultCMLProperties) value));
                }
            }
            else if (value instanceof IntMatrixResult)
            {
                if (((IntMatrixResult) value).value == null)
                {
                    logger.error("Integer matrix '" + attrib +
                        "' is  not defined.");
                }
                else
                {
                    writeMatrixCML2(writerProp, ps, Elements.XSD_INTEGER,
                        attrib, ((IntMatrixResult) value).toString(cml),
                        ((IntMatrixResult) value).value.length,
                        ((IntMatrixResult) value).value[0].length,
                        ((ResultCMLProperties) value));
                }
            }
            else
            {
                Map<String, String> attributes = new Hashtable<String, String>();
                attributes.clear();
                attributes.put(Elements.TITLE, attrib);
                attributes.put(Elements.DATATYPE, Elements.XSD_STRING);
                CMLMoleculeWriterBase.writeOpenTag(ps, writerProp,
                    Elements.SCALAR, attributes, false);
                CMLMoleculeWriterBase.write(ps,
                    XMLSpecialCharacter.convertPlain2XML(
                        (String) value.toString()));
                CMLMoleculeWriterBase.writeCloseTag(ps, writerProp,
                    Elements.SCALAR);
            }
        }
    }

    public static void writeProperties(CMLWriterProperties _writerProp,
        PrintStream ps, Molecule mol, boolean writePairData, List attribs2write)
    {
        // write additional descriptor data
        if (writePairData)
        {
            PairData pairData;

            // write all descriptors
            if (attribs2write == null)
            {
                PairDataIterator gdit = mol.genericDataIterator();

                while (gdit.hasNext())
                {
                    pairData = gdit.nextPairData();

                    if (pairData.getKeyValue() instanceof FeatureResult)
                    {
                        // all o.k.
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Descriptor '" + pairData.getKey() +
                                "' is (parsed) PairData");
                        }
                    }
                    else
                    {
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Descriptor '" + pairData.getKey() +
                                "' is (unparsed) String");
                        }

                        pairData = (BasicPairData) mol.getData(pairData
                                .getKey(), true);
                    }

                    writePairData(_writerProp, ps, pairData);
                }
            }

            // write only descriptors specified in attrib2write
            else
            {
                int size = attribs2write.size();

                for (int i = 0; i < size; i++)
                {
                    // get unparsed data
                    //                          System.out.println("write "+ attribs2write.get(i));
                    pairData = mol.getData((String) attribs2write.get(i), true);

                    if (pairData == null)
                    {
                        logger.warn((String) attribs2write.get(i) +
                            " data entry doesn't exist in molecule: " +
                            mol.getTitle());
                    }
                    else
                    {
                        writePairData(_writerProp, ps, pairData);
                    }
                }
            }
        }
    }

    private static void writeArrayCML1(CMLWriterProperties _writerProp,
        PrintStream ps, String dataType, String title, String value, int size,
        ResultCMLProperties props)
    {
        Map<String, String> attributes = new Hashtable<String, String>();
        attributes.clear();
        attributes.put(Elements.TITLE, title);
        attributes.put(Elements.SIZE, Integer.toString(size));

        Enumeration<StringString> e = props.getCMLProperties();
        writeArrayProperties(e, attributes);

        CMLMoleculeWriterBase.writeOpenTag(ps, _writerProp, dataType,
            attributes, false);
        CMLMoleculeWriterBase.write(ps, value);
        CMLMoleculeWriterBase.writeCloseTag(ps, _writerProp, dataType);
    }

    private static void writeArrayCML2(CMLWriterProperties _writerProp,
        PrintStream ps, String dataType, String title, String value, int size,
        ResultCMLProperties props)
    {
        Map<String, String> attributes = new Hashtable<String, String>();
        attributes.clear();
        attributes.put(Elements.TITLE, title);
        attributes.put(Elements.DATATYPE, dataType);
        attributes.put(Elements.SIZE, Integer.toString(size));

        Enumeration<StringString> e = props.getCMLProperties();
        writeArrayProperties(e, attributes);

        CMLMoleculeWriterBase.writeOpenTag(ps, _writerProp, Elements.ARRAY,
            attributes, false);
        CMLMoleculeWriterBase.write(ps, value);
        CMLMoleculeWriterBase.writeCloseTag(ps, _writerProp, Elements.ARRAY);
    }

    private static void writeArrayProperties(Enumeration<StringString> e,
        Map<String, String> attributes)
    {
        StringString ss;
        boolean delimiterWritten = false;

        if (e != null)
        {
            while (e.hasMoreElements())
            {
                ss = e.nextElement();

                // ignore 'size' in CML properties
                if (!ss.getStringValue1().equals(Elements.SIZE))
                {
                    if (ss.getStringValue1().equals(Elements.DELIMITER))
                    {
                        delimiterWritten = true;
                    }

                    attributes.put(ss.getStringValue1(), ss.getStringValue2());
                }
            }
        }

        if (!delimiterWritten &&
                !ChemicalMarkupLanguage.getDefaultDelimiter().equals(" "))
        {
            attributes.put(Elements.DELIMITER,
                ChemicalMarkupLanguage.getDefaultDelimiter());
        }
    }

    private static void writeMatrixCML2(CMLWriterProperties _writerProp,
        PrintStream ps, String dataType, String title, String value, int rows,
        int columns, ResultCMLProperties props)
    {
        Map<String, String> attributes = new Hashtable<String, String>();
        attributes.clear();
        attributes.put(Elements.TITLE, title);
        attributes.put(Elements.DATATYPE, dataType);
        attributes.put(Elements.ROWS, Integer.toString(rows));
        attributes.put(Elements.COLUMNS, Integer.toString(columns));

        Enumeration<StringString> e = props.getCMLProperties();
        writeMatrixProperties(e, attributes);

        CMLMoleculeWriterBase.writeOpenTag(ps, _writerProp, Elements.MATRIX,
            attributes, false);
        CMLMoleculeWriterBase.write(ps, value);
        CMLMoleculeWriterBase.writeCloseTag(ps, _writerProp, Elements.MATRIX);
    }

    private static void writeMatrixProperties(Enumeration<StringString> e,
        Map<String, String> attributes)
    {
        StringString ss;
        boolean delimiterWritten = false;

        if (e != null)
        {
            while (e.hasMoreElements())
            {
                ss = e.nextElement();

                // ignore 'rows' and 'columns' in CML properties
                if (!ss.getStringValue1().equals(Elements.ROWS) &&
                        !ss.getStringValue1().equals(Elements.COLUMNS))
                {
                    if (ss.getStringValue1().equals(Elements.DELIMITER))
                    {
                        delimiterWritten = true;
                    }

                    attributes.put(ss.getStringValue1(), ss.getStringValue2());
                }
            }
        }

        if (!delimiterWritten &&
                !ChemicalMarkupLanguage.getDefaultDelimiter().equals(" "))
        {
            attributes.put(Elements.DELIMITER,
                ChemicalMarkupLanguage.getDefaultDelimiter());
        }
    }

    private static void writeProperties(CMLWriterProperties _writerProp,
        Enumeration<StringString> e, Map<String, String> attributes)
    {
        StringString ss;

        if (e != null)
        {
            while (e.hasMoreElements())
            {
                ss = e.nextElement();

                if (!_writerProp.storeChemistryKernelInfo())
                {
                    if (ss.getStringValue1().equals(
                                IdentifierExpertSystem.CML_KERNEL_REFERENCE))
                    {
                        continue;
                    }
                }

                attributes.put(ss.getStringValue1(), ss.getStringValue2());
            }
        }
    }

    private static void writeScalarCML1(CMLWriterProperties _writerProp,
        PrintStream ps, String dataType, String title, String value,
        ResultCMLProperties props)
    {
        Map<String, String> attributes = new Hashtable<String, String>();
        attributes.clear();
        attributes.put(Elements.TITLE, title);

        Enumeration<StringString> e = props.getCMLProperties();
        writeProperties(_writerProp, e, attributes);

        CMLMoleculeWriterBase.writeOpenTag(ps, _writerProp, dataType,
            attributes, false);
        CMLMoleculeWriterBase.write(ps, value);
        CMLMoleculeWriterBase.writeCloseTag(ps, _writerProp, dataType);
    }

    private static void writeScalarCML2(CMLWriterProperties _writerProp,
        PrintStream ps, String dataType, String title, String value,
        ResultCMLProperties props)
    {
        Map<String, String> attributes = new Hashtable<String, String>();
        attributes.clear();
        attributes.put(Elements.TITLE, title);
        attributes.put(Elements.DATATYPE, dataType);

        Enumeration<StringString> e = props.getCMLProperties();
        writeProperties(_writerProp, e, attributes);

        CMLMoleculeWriterBase.writeOpenTag(ps, _writerProp, Elements.SCALAR,
            attributes, false);
        CMLMoleculeWriterBase.write(ps, value);
        CMLMoleculeWriterBase.writeCloseTag(ps, _writerProp, Elements.SCALAR);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
