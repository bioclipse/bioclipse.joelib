///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BFSResult.java,v $
//  Purpose:  Atom representation.
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
package joelib2.algo;

import joelib2.feature.FeatureResult;

import joelib2.io.BasicIOTypeHolder;
import joelib2.io.IOType;

import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.PairData;

import joelib2.util.BasicArrayHelper;
import joelib2.util.BasicLineArrayHelper;
import joelib2.util.HelperMethods;

import java.io.LineNumberReader;
import java.io.StringReader;

import java.util.List;


/**
 * Result of a BFS.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:28 $
 */
public class BFSResult extends BasicPairData implements FeatureResult,
    Cloneable, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;
    private final static String basicFormat = "startAtomIndex\n" +
        "nTraverse<e0,...e(nTraverse-1)>nParent<e0,...e(nParent-1)>\n" +
        "with nTraverse, e0,...,e(nTraverse-1) of type 32-bit integer" +
        "with traverse numbers" +
        "with nParent, e0,...,e(nParent-1) of type 32-bit integer" +
        "with parent of traverse as atom idx, -1 if start atom or undiscovered";
    private final static String lineFormat = "startAtomIndex\n" +
        "nTraverse\n" + "e0\n" + "...\n" + "e(nTraverse-1)>\n" + "nParent\n" +
        "e0\n" + "...\n" + "e(nParent-1)>\n" + "<empty line>\n" +
        "with nTraverse, e0,...,e(nTraverse-1) of type 32-bit integer" +
        "with traverse numbers" +
        "with nParent, e0,...,e(nParent-1) of type 32-bit integer" +
        "with parent of traverse as atom idx, -1 if start atom or undiscovered";

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    private int[] parent;

    /**
     * Description of the Field
     */
    private int startAtomIndex;

    /**
     *  Description of the Field
     */
    private int[] traverse;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the BFSResult object
     */
    public BFSResult()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public Object clone()
    {
        BFSResult newObj = new BFSResult();

        newObj.traverse = new int[this.traverse.length];
        newObj.parent = new int[this.parent.length];

        return clone(newObj);
    }

    public BFSResult clone(BFSResult _target)
    {
        _target.startAtomIndex = startAtomIndex;
        System.arraycopy(_target.traverse, 0, this.traverse, 0,
            traverse.length);
        System.arraycopy(_target.parent, 0, this.parent, 0, parent.length);

        return _target;
    }

    /**
     *  Description of the Method
     *
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
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
        StringReader sr = new StringReader(sValue);
        LineNumberReader lnr = new LineNumberReader(sr);

        // get start atom index
        try
        {
            startAtomIndex = Integer.parseInt(lnr.readLine());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }

        String tmp = null;

        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            int index = sValue.indexOf("\n");
            tmp = sValue.substring(index).trim();

            List list;
            list = BasicLineArrayHelper.intArrayFromString(tmp);
            traverse = (int[]) list.get(0);
            parent = (int[]) list.get(1);
        }
        else
        {
            int index = sValue.indexOf("\n");
            tmp = sValue.substring(index).trim();

            List list;
            list = BasicArrayHelper.instance().intArrayFromString(tmp);
            traverse = (int[]) list.get(0);
            parent = (int[]) list.get(1);
        }

        return true;
    }

    /**
     * @return Returns the parent.
     */
    public int[] getParent()
    {
        return parent;
    }

    /**
     * @return Returns the startAtomIndex.
     */
    public int getStartAtomIndex()
    {
        return startAtomIndex;
    }

    /**
     * @return Returns the traverse.
     */
    public int[] getTraverse()
    {
        return traverse;
    }

    public boolean init(String _descName)
    {
        this.setKey(_descName);

        return true;
    }

    /**
     * @param parent The parent to set.
     */
    public void setParent(int[] parent)
    {
        this.parent = parent;
    }

    /**
     * @param startAtomIndex The startAtomIndex to set.
     */
    public void setStartAtomIndex(int startAtomIndex)
    {
        this.startAtomIndex = startAtomIndex;
    }

    /**
     * @param traverse The traverse to set.
     */
    public void setTraverse(int[] traverse)
    {
        this.traverse = traverse;
    }

    /**
     *  Description of the Method
     *
     * @param ioType  Description of the Parameter
     * @return        Description of the Return Value
     */
    public String toString(IOType ioType)
    {
        StringBuffer sb = new StringBuffer();

        // start atom index
        sb.append(startAtomIndex);
        sb.append(HelperMethods.eol);

        // bfs result arrays
        if (ioType.equals(BasicIOTypeHolder.instance().getIOType("SDF")))
        {
            BasicLineArrayHelper.toString(sb, traverse).toString();
            sb.append(HelperMethods.eol);
            BasicLineArrayHelper.toString(sb, parent).toString();
        }
        else
        {
            BasicArrayHelper.instance().toString(sb, traverse).toString();
            sb.append(HelperMethods.eol);
            BasicArrayHelper.instance().toString(sb, parent).toString();
        }

        // could be a nice JUnit test routine !!!
        //    Vector vvv = LineArrayHelper.instance().intArrayFromString(sb.toString());
        //    System.out.println("Generated/Parsed:");
        //    StringBuffer sb2 = new StringBuffer();
        //    ArrayHelper.instance().toString(sb2, (int[])vvv.get(0)).toString();
        //    ArrayHelper.instance().toString(sb2, (int[])vvv.get(1)).toString();
        //    System.out.println(""+sb2.toString());
        return sb.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
