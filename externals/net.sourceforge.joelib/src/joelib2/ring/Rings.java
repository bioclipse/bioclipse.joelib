///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Rings.java,v $
//  Purpose:  Stores ring data.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.12 $
//            $Date: 2005/03/03 07:13:51 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
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
package joelib2.ring;

import joelib2.feature.FeatureResult;

import joelib2.io.IOType;

import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.PairData;

import joelib2.util.BasicArrayHelper;

import java.util.List;
import java.util.Vector;


/**
 * Stores ring data.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.12 $, $Date: 2005/03/03 07:13:51 $
 */
public class Rings extends BasicPairData implements java.io.Serializable,
    FeatureResult
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    private final static String lineFormat = "n\n" + "r1_a_1,...,r1_a_|r1|\n" +
        "rN_a_1,...,r1_a_|rN|\n" + "\n" +
        "with n, rx_a_y of type 32-bit integer";

    //~ Instance fields ////////////////////////////////////////////////////////

    protected List<Ring> rings;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOERingData object
     */
    public Rings()
    {
        this.setKey(this.getClass().getName());
        this.setKeyValue(this);
        rings = new Vector<Ring>();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  r  Description of the Parameter
     */
    public void add(Ring r)
    {
        rings.add(r);
    }

    /* (non-Javadoc)
     * @see joelib2.feature.DescResult#clone()
     */
    public Object clone()
    {
        Object cloned;

        if (rings instanceof Vector)
        {
            cloned = ((Vector) rings).clone();
        }
        else
        {
            cloned = new Vector(rings.size());

            for (int index = 0; index < rings.size(); index++)
            {
                ((Vector) cloned).add(rings.get(index));
            }
        }

        return cloned;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.DescResult#formatDescription(joelib2.io.IOType)
     */
    public String formatDescription(IOType ioType)
    {
        return lineFormat;
    }

    /* (non-Javadoc)
     * @see joelib2.feature.DescResult#fromPairData(joelib2.io.IOType, joelib2.data.PairData)
     */
    public boolean fromPairData(IOType ioType, PairData pairData)
        throws NumberFormatException
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

    /* (non-Javadoc)
     * @see joelib2.feature.DescResult#fromString(joelib2.io.IOType, java.lang.String)
     */
    public boolean fromString(IOType ioType, String sValue)
        throws NumberFormatException
    {
        try
        {
            List list = BasicArrayHelper.instance().intArrayFromString(sValue);

            if (list != null)
            {
                rings = new Vector<Ring>(list.size());

                for (int index = 0; index < list.size(); index++)
                {
                    rings.add(new BasicRing((int[]) list.get(index), null));
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }

        return true;
    }

    public Ring getRing(int index)
    {
        return (Ring) rings.get(index);
    }

    /**
     * @return    {@link java.util.List} of <tt>JOERing</tt>
     */
    public List getRings()
    {
        return (rings);
    }

    public int getRingSize()
    {
        return rings.size();
    }

    /* (non-Javadoc)
     * @see joelib2.feature.DescResult#init(java.lang.String)
     */
    public boolean init(String descName)
    {
        this.setKey(descName);

        return true;
    }

    public String toString(IOType ioType)
    {
        StringBuffer sb = new StringBuffer(getRingSize() * 100);

        int ringSize = getRingSize();
        int ringSize_1 = ringSize - 1;

        for (int index = 0; index < ringSize; index++)
        {
            BasicArrayHelper.instance().toString(sb,
                getRing(index).getAtomIndices());

            if (index < ringSize_1)
            {
                sb.append('\n');
            }
        }

        return sb.toString();
    }

    /**
     * @param  ringList  {@link java.util.List} of <tt>JOERing</tt>
     */
    protected void setRings(List<Ring> ringList)
    {
        rings = ringList;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
