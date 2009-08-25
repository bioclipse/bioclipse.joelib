///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: APropDoubleArrResult.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
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
import joelib2.feature.NumberFormatResult;

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.PairData;

import joelib2.util.BasicArrayHelper;
import joelib2.util.BasicLineArrayHelper;
import joelib2.util.HelperMethods;

import wsi.ra.text.DecimalFormatHelper;
import wsi.ra.text.DecimalFormatter;

import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.StringReader;

import java.util.Hashtable;
import java.util.List;


/**
 * Atom representation.
 *
 * @.author wegnerj
 * @.license GPL
 * @.cvsversion $Revision: 1.10 $, $Date: 2005/02/17 16:48:30 $
 */
public class APropDoubleArrResult extends BasicPairData implements Cloneable,
    FeatureResult, NumberFormatResult, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    private final static String basicFormat = "<atom_property>\n" +
        "n<e0,...e(n-1)>\n" + "with n of type 32-bit integer" +
        "with e0,...,e(n-1) of type 64-bit floating point value IEEE 754";

    private final static String lineFormat = "<atom_property>\n" + "n\n" +
        "e0\n" + "...\n" + "e(n-1)>\n" +

        //            "<empty line>\n" +
        "with n of type 32-bit integer" +
        "with e0,...,e(n-1) of type 64-bit floating point value IEEE 754";

    //~ Instance fields ////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////

    /**
     * Description of the Field
     */
    public String atomProperty;

    /**
     * Description of the Field
     */
    public double[] value;

    //~ Constructors ///////////////////////////////////////////////////////////

    // ///////////////////////////////////////////////////////////

    /**
     * Constructor for the BFSResult object
     */
    public APropDoubleArrResult()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        APropDoubleArrResult newObj = new APropDoubleArrResult();

        if (this.value != null)
        {
            newObj.value = new double[this.value.length];
        }

        return clone(newObj);
    }

    public APropDoubleArrResult clone(APropDoubleArrResult other)
    {
        super.clone(other);

        other.atomProperty = this.atomProperty;

        if (this.value != null)
        {
            System.arraycopy(this.value, 0, other.value, 0, value.length);
        }

        return other;
    }

    /**
     * Description of the Method
     *
     * @param ioType
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public String formatDescription(IOType ioType)
    {
        String format = basicFormat;

        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            format = lineFormat;
        }

        return format;
    }

    /**
     * Description of the Method
     *
     * @param pairData
     *            Description of the Parameter
     * @param ioType
     *            Description of the Parameter
     * @return Description of the Return Value
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
     * Description of the Method
     *
     * @param sValue
     *            Description of the Parameter
     * @param ioType
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public boolean fromString(IOType ioType, String sValue)
    {
        StringReader sr = new StringReader(sValue);
        LineNumberReader lnr = new LineNumberReader(sr);

        boolean success = true;

        try
        {
            atomProperty = lnr.readLine();

            if ((atomProperty != null) && atomProperty.equals("?"))
            {
                atomProperty = "";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            success = false;
        }

        if (success)
        {
            List list;

            if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
            {
                list = BasicLineArrayHelper.doubleArrayFromString(lnr, -1);
            }
            else
            {
                int index = sValue.indexOf("\n");
                String sArray = sValue.substring(index).trim();
                list = BasicArrayHelper.instance().doubleArrayFromString(
                        sArray);
            }

            value = (double[]) list.get(0);
        }

        return success;
    }

    public double getDoubleValue(int atomIdx)
    {
        return value[atomIdx - 1];
    }

    public int getIntValue(int atomIdx)
    {
        return (int) value[atomIdx - 1];
    }

    public String getStringValue(int atomIdx)
    {
        return DecimalFormatHelper.instance().format(value[atomIdx - 1]);
    }

    public Object getValue(int atomIdx)
    {
        return new Double(value[atomIdx - 1]);
    }

    /**
     * Description of the Method
     *
     * @param _descName
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public boolean init(String _descName)
    {
        this.setKey(_descName);

        return true;
    }

    public void setDoubleValue(int atomIdx, double _value)
    {
        value[atomIdx - 1] = _value;
    }

    public void setIntValue(int atomIdx, int _value)
    {
        value[atomIdx - 1] = (double) _value;
    }

    public void setStringValue(int atomIdx, String _value)
    {
        value[atomIdx - 1] = Double.parseDouble(_value);
    }

    public void setValue(int atomIdx, Object _value)
    {
        value[atomIdx - 1] = ((Double) _value).doubleValue();
    }

    /**
     * Description of the Method
     *
     * @param ioType
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public String toString(IOType ioType)
    {
        return toString(ioType, DecimalFormatHelper.instance());
    }

    /**
     * Description of the Method
     *
     * @param ioType
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public String toString(IOType ioType, DecimalFormatter format)
    {
        StringBuffer sb = new StringBuffer();

        // write property type, data description and data unit
        if (atomProperty.trim().length() == 0)
        {
            sb.append('?');
        }
        else
        {
            sb.append(atomProperty);
        }

        sb.append(HelperMethods.eol);

        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            BasicLineArrayHelper.toString(sb, value, format);
        }
        else
        {
            BasicArrayHelper ah = BasicArrayHelper.instance();

            ah.toString(sb, value, format);
        }

        return sb.toString();
    }

    /**
     * Write <code>APropDoubleResult</code> entries for every array index
     * entry.
     *
     * @param _mol
     *            Description of the Parameter
     * @param _descName
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public boolean writeSingleResults(Molecule _mol, String _descName)
    {
        return writeSingleResults(_mol, _descName, true, true, null, null,
                true);
    }

    /**
     * Write <code>APropDoubleResult</code> entries for every array index
     * entry.
     *
     * @param _mol
     *            Description of the Parameter
     * @param _descName
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public boolean writeSingleResults(Molecule _mol, String _descName,
        PrintStream _names, Hashtable _exist)
    {
        return writeSingleResults(_mol, _descName, true, true, _names, _exist,
                true);
    }

    /**
     * Description of the Method
     *
     * @param _mol
     *            Description of the Parameter
     * @param _descName
     *            Description of the Parameter
     * @param _writeAPropName
     *            Description of the Parameter
     * @param _useAPropDouble
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    public boolean writeSingleResults(Molecule _mol, String _descName,
        boolean _writeAPropName, boolean _useAPropDouble, PrintStream _names,
        Hashtable _exist, boolean _overwrite)
    {
        //System.out.println("write: "+_descName);
        int size = value.length;
        BasicPairData pairData;
        FeatureResult single;
        String nameBase;

        if ((_descName == null) || (_descName.trim().length() == 0))
        {
            nameBase = "";
        }
        else
        {
            nameBase = _descName + ":";
        }

        if (_writeAPropName)
        {
            nameBase = nameBase + atomProperty + ":";
        }

        for (int i = 0; i < size; i++)
        {
            // set single atom property descriptor
            if (_useAPropDouble)
            {
                single = new APropDoubleResult();
                ((APropDoubleResult) single).value = value[i];
                ((APropDoubleResult) single).atomProperty = atomProperty;
            }
            else
            {
                single = new DoubleResult();
                ((DoubleResult) single).value = value[i];
            }

            // add descriptor data to molecule
            String name = nameBase + i;
            pairData = new BasicPairData();
            pairData.setKey(name);
            pairData.setKeyValue(single);

            //if(i==0)System.out.println(name+"[0]=>"+value[i]);
            if (_names != null)
            {
                if (!_exist.containsKey(name))
                {
                    _names.println(name);
                    _exist.put(name, "");
                }
            }

            _mol.addData(pairData, _overwrite);
        }

        return true;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
