///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicDataHolder.java,v $
//  Purpose:  Generic data holder for molecules.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:36 $
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
package joelib2.molecule;

import joelib2.feature.ResultFactory;

import joelib2.molecule.types.BasicPairData;
import joelib2.molecule.types.PairData;

import joelib2.util.iterator.BasicPairDataIterator;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Holder class of generic data objects for one molecule.
 *
 * @.author    wegnerj
 * @.license   GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:36 $
 */
public class BasicDataHolder implements java.io.Serializable, DataHolder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    private static Category logger = Category.getInstance(BasicDataHolder.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private Hashtable<Integer, Boolean> ambiguityFastControl;

    private Molecule parent;

    private Hashtable<String, PairData> singleData;

    private Hashtable<Integer, PairData> singleDataFast;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the GenericDataHolder object
     *
     * @param _parent  Description of the Parameter
     */
    public BasicDataHolder(Molecule _parent, int single)
    {
        parent = _parent;
        singleData = new Hashtable<String, PairData>(single);
        singleDataFast = new Hashtable<Integer, PairData>(single);
        ambiguityFastControl = new Hashtable<Integer, Boolean>(single);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Adds a feature to the Data attribute of the GenericDataHolder object
     *
     * @param d          The feature to be added to the Data attribute
     * @param overwrite  The feature to be added to the Data attribute
     */
    public void addData(PairData d, boolean overwrite)
    {
        if (d != null)
        {
            Integer fastKey = new Integer(d.getKey().hashCode());

            if (overwrite)
            {
                singleData.put(d.getKey(), d);

                if (!singleDataFast.containsKey(fastKey))
                {
                    // so only slow access available, if key exists already
                    // otherwise
                    singleDataFast.put(fastKey, d);
                }
                else
                {
                    ambiguityFastControl.put(fastKey, Boolean.TRUE);
                    singleDataFast.remove(fastKey);
                }
            }
            else
            {
                // add only if it not already exists
                if (!singleData.containsKey(d.getKey()))
                {
                    singleData.put(d.getKey(), d);

                    if (!singleDataFast.containsKey(fastKey))
                    {
                        // so only slow access available, if key exists already
                        // otherwise
                        singleDataFast.put(fastKey, d);
                    }
                    else
                    {
                        ambiguityFastControl.put(fastKey, Boolean.TRUE);
                        singleDataFast.remove(fastKey);
                    }
                }
            }
        }
    }

    /**
     * Description of the Method
     */
    public void clear()
    {
        singleData.clear();
        singleDataFast.clear();
        ambiguityFastControl.clear();
    }

    /**
     * Description of the Method
     *
     * @param s  Description of the Parameter
     * @return   Description of the Return Value
     */
    public boolean deleteData(String s)
    {
        if (singleData.size() == 0)
        {
            return false;
        }

        BasicPairData pairData = null;

        if (s != null)
        {
            Integer fastKey = new Integer(s.hashCode());
            singleDataFast.remove(fastKey);
            singleData.remove(s);
        }

        if (pairData != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Description of the Method
     *
     * @param gd  Description of the Parameter
     */
    public void deleteData(PairData gd)
    {
        if (singleData.size() == 0)
        {
            return;
        }

        if ((gd != null) && (gd.getKey() != null))
        {
            Integer fastKey = new Integer(gd.getKey().hashCode());
            Boolean ambigious = ambiguityFastControl.get(fastKey);

            if ((ambigious != null) && (ambigious.booleanValue() == false))
            {
                // remove only keys which are non-ambigious
                singleDataFast.remove(fastKey);
            }

            singleData.remove(gd.getKey());
        }
    }

    /**
     * Description of the Method
     *
     * @param vg  Description of the Parameter
     */
    public void deleteData(List vg)
    {
        BitSet notFound = new BitSet(vg.size());

        // to vg.size because toIndex is EXCLUSIVE
        notFound.set(0, vg.size(), true);

        // remove vector entries
        Vector vdata = new Vector();

        //boolean del;
        BasicPairData genericData;
        BasicPairData genericData2;

        //System.out.println("remove hash table entries");
        // remove hash table entries
        int index = 0;
        int oldIndex = -2;

        while (((index = notFound.nextSetBit(index)) != -1) &&
                (index != oldIndex))
        {
            //System.out.println("index "+index);
            genericData = (BasicPairData) vg.get(index);
            singleData.remove(genericData.getKey());

            Integer fastKey = new Integer(genericData.getKey().hashCode());
            Boolean ambigious = ambiguityFastControl.get(fastKey);

            if ((ambigious != null) && (ambigious.booleanValue() == false))
            {
                // remove only keys which are non-ambigious
                singleDataFast.remove(fastKey);
            }

            oldIndex = index;
        }
    }

    /**
     *  Gets the data attribute of the <tt>Molecule</tt> object
     *
     * @return   The data value
     */
    public BasicPairDataIterator genericDataIterator()
    {
        return new BasicPairDataIterator(singleData);
    }

    /**
     * Gets the data attribute of the GenericDataHolder object
     *
     * @param s      Description of the Parameter
     * @param parse  Description of the Parameter
     * @return       <tt>null</tt> if this data don't exist
     */
    public PairData getData(String s, boolean parse)
    {
        if (s == null)
        {
            logger.warn("Data name should be not null.");

            return null;
        }

        if (singleData.size() == 0)
        {
            return null;
        }

        PairData pairData = null;
        Integer fastKey = new Integer(s.hashCode());

        if (singleDataFast.containsKey(fastKey))
        {
            Boolean ambigious = ambiguityFastControl.get(fastKey);

            if ((ambigious != null) && ambigious.booleanValue())
            {
                pairData = singleData.get(s);
            }
            else
            {
                pairData = singleDataFast.get(fastKey);
            }
        }
        else
        {
            pairData = singleData.get(s);
        }

        //        System.out.println(((PairData)genericData).getValue().getClass().getName()+": "+genericData);
        if (pairData != null)
        {
            //            System.out.println("unparsed data:"+genericData);
            if (parse)
            {
                PairData newData = ResultFactory.instance().parsePairData(
                        parent, s, pairData);

                if (newData != null)
                {
                    singleData.put(s, newData);

                    //                  System.out.println("parsed data:"+newData);
                    return newData;
                }
            }

            return pairData;
        }
        else
        {
            return null;
        }
    }

    /**
     * Description of the Method
     *
     * @param s  Description of the Parameter
     * @return   Description of the Return Value
     */
    public boolean hasData(String s)
    {
        boolean hasData = false;

        if ((s == null) || (singleData.size() == 0))
        {
            hasData = false;
        }
        else
        {
            if (singleData == null)
            {
                hasData = false;
            }
            else
            {
                Integer fastKey = new Integer(s.hashCode());
                Boolean ambigious = ambiguityFastControl.get(fastKey);

                if ((ambigious != null) && ambigious.booleanValue())
                {
                    hasData = singleData.containsKey(s);
                }
                else
                {
                    hasData = singleDataFast.containsKey(fastKey);
                }
            }
        }

        return hasData;
    }

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    public int size()
    {
        return singleData.size();
    }

    /**
     * Description of the Method
     */
    protected void finalize() throws Throwable
    {
        clear();
        super.finalize();
    }

    /**
     * @return Returns the parent.
     */
    protected Molecule getParent()
    {
        return parent;
    }

    /**
     * @return Returns the singleData.
     */
    protected Hashtable getSingleData()
    {
        return singleData;
    }

    /**
     * @param parent The parent to set.
     */
    protected void setParent(Molecule parent)
    {
        this.parent = parent;
    }

    /**
     * @param singleData The singleData to set.
     */
    protected void setSingleData(Hashtable<String, PairData> singleData)
    {
        this.singleData = singleData;
        singleDataFast.clear();
        ambiguityFastControl.clear();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
