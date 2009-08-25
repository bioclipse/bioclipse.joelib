///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: CliqueFinder.java,v $
//  Purpose:  Clique detection.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.1 $
//            $Date: 2006/03/03 07:13:24 $
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
package joelib2.algo.clique;

import joelib2.sort.QuickInsertSort;

import wsi.ra.tool.SortedHashedIntArray;
import wsi.ra.tool.StopWatch;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 *  Bron Kerbosch algorithm.
 *
 * @author     wegnerj
 */
public abstract class CliqueFinder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(CliqueFinder.class.getName());

    /**
     *  Description of the Field
     */
    public final static int ADD_ALL_CLIQUES = 0;

    /**
     *  Description of the Field
     */
    public final static int ADD_UNIQUE_CLIQUES = 1;

    /**
     *  Description of the Field
     */
    public final static int JUST_COUNT_CLIQUES = 2;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    protected int store = ADD_UNIQUE_CLIQUES;

    /**
     *  Description of the Field
     */
    private Hashtable cliquesUnsorted;
    private Vector cliques;
    private SortedHashedIntArray[] sorted;
    private int counter;
    private int toStoreSize;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Abstract class for finding cliques.
     */
    public CliqueFinder()
    {
        this(500000);
    }

    /**
     *  Constructor for the CliqueFinder object
     *
     * @param  _expectedCliques  Description of the Parameter
     */
    public CliqueFinder(int _expectedCliques)
    {
        cliquesUnsorted = new Hashtable(_expectedCliques);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  connected  Description of the Parameter
     * @return            Description of the Return Value
     */
    public abstract int findCliques(byte[][] connected);

    public abstract int findCliques(byte[][] connected, int _breakNumCliques);

    /**
     *  Gets the clique attribute of the CliqueFinder object
     *
     * @param  cliqueSize  Description of the Parameter
     * @return             The clique value
     */
    public Hashtable getClique(int cliqueSize)
    {
        if ((cliques == null) || (cliques.size() == 0))
        {
            logger.error("No cliques available");

            return null;
        }

        if (cliqueSize > cliques.size())
        {
            logger.error("Size of maximum clique found can only be " +
                cliques.size());

            return null;
        }

        if (store == JUST_COUNT_CLIQUES)
        {
            logger.error(
                "Method getClique not available in storing mode: JUST_COUNT_CLIQUES");

            return null;
        }

        Hashtable cl = (Hashtable) cliques.get(cliqueSize - 1);

        return cl;
    }

    /**
     *  Gets the nbrClique attribute of the CliqueFinder object
     *
     * @param  nbr     Description of the Parameter
     * @param  clique  Description of the Parameter
     */
    public void getNbrClique(LinkedList nbr, SortedHashedIntArray clique)
    {
        if (store == JUST_COUNT_CLIQUES)
        {
            logger.error(
                "Method getNbrClique not available in storing mode: JUST_COUNT_CLIQUES");

            return;
        }

        int size = clique.length();
        Hashtable cl = (Hashtable) cliques.get(size - 1);
        Enumeration enumeration = cl.elements();
        SortedHashedIntArray shia;
        int equal;
        int difference;

        StopWatch watch = new StopWatch();

        while (enumeration.hasMoreElements())
        {
            shia = (SortedHashedIntArray) enumeration.nextElement();
            equal = clique.equalEntries(shia);
            difference = Math.abs(shia.length() - equal);

            if (difference == 1)
            {
                nbr.add(shia);
            }
        }

        System.out.println("Needed " + watch.getPassedTime() +
            " ms for getting nbrCliques.");
    }

    /**
     *  Sets the storingMode attribute of the CliqueFinder object
     *
     * @param  _mode  The new storingMode value
     */
    public void setStoringMode(int _mode)
    {
        store = _mode;
        cliques = null;
    }

    /**
     *  Gets the storingMode attribute of the CliqueFinder object
     *
     * @return    The storingMode value
     */
    public int getStoringMode()
    {
        return store;
    }

    /**
     *  Description of the Method
     */
    public void clear()
    {
        cliquesUnsorted.clear();
        sorted = null;
        counter = 0;

        if (cliques == null)
        {
            return;
        }

        int size = cliques.size();

        for (int i = 0; i < size; i++)
        {
            if (store == JUST_COUNT_CLIQUES)
            {
                ((int[]) cliques.get(i))[0] = 0;
            }
            else
            {
                ((Hashtable) cliques.get(i)).clear();
            }
        }
    }

    /**
     *  Returns enumeration with <tt>int[]</tt> .
     *
     * @return    Description of the Return Value
     */
    public Enumeration cliques()
    {
        if (store == JUST_COUNT_CLIQUES)
        {
            Object obj = null;
            obj.toString();
            logger.error(
                "Method cliques returns always empty Enumeration in storing mode: JUST_COUNT_CLIQUES");

            //      return null;
        }

        return cliquesUnsorted.elements();
    }

    public void countOnly(int _toStoreSize)
    {
        toStoreSize = _toStoreSize;
    }

    /**
     *  Returns enumeration with <tt>SortedHashedIntArray</tt> .
     *
     * @return    Description of the Return Value
     */
    public Enumeration hashedCliques()
    {
        if (store == JUST_COUNT_CLIQUES)
        {
            logger.error(
                "Method hashedCliques returns always empty Enumeration in storing mode: JUST_COUNT_CLIQUES");

            //      return null;
        }

        return cliquesUnsorted.keys();
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public final int numberOfCliques()
    {
        if (store == JUST_COUNT_CLIQUES)
        {
            if (cliques == null)
            {
                return -1;
            }

            int num = 0;
            int size = cliques.size();

            for (int i = 0; i < size; i++)
            {
                num += ((int[]) cliques.get(i))[0];
            }

            return num;
        }
        else
        {
            return cliquesUnsorted.size();
        }

        //    return 0;
    }

    public int getMaximumCliqueSize()
    {
        return cliques.size();
    }

    public SortedHashedIntArray[] sortedCliques()
    {
        if (store == JUST_COUNT_CLIQUES)
        {
            logger.error(
                "Method cliques returns always empty Enumeration in storing mode: JUST_COUNT_CLIQUES");

            //      return null;
        }

        if (sorted == null)
        {
            sorted = new SortedHashedIntArray[cliquesUnsorted.size()];
        }
        else
        {
            return sorted;
        }

        Enumeration enumeration = cliques();
        int index = 0;

        while (enumeration.hasMoreElements())
        {
            sorted[index] = (SortedHashedIntArray) enumeration.nextElement();
            index++;
        }

        QuickInsertSort quickInsertSort = new QuickInsertSort();
        quickInsertSort.sort(sorted, new SHIAComparator());

        return sorted;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String toString()
    {
        int size = cliques.size();
        StringBuffer sb = new StringBuffer(size * 10);
        sb.append("Cliques (" + numberOfCliques() + "):");

        for (int i = 0; i < size; i++)
        {
            sb.append('s');
            sb.append(i + 1);
            sb.append('=');

            if (store == JUST_COUNT_CLIQUES)
            {
                sb.append(((int[]) cliques.get(i))[0]);
            }
            else
            {
                sb.append(((Hashtable) cliques.get(i)).size());
            }

            sb.append(' ');
        }

        return sb.toString();
    }

    /**
     *  Adds a feature to the Clique attribute of the CliqueFinder object
     *
     * @param  array  The feature to be added to the Clique attribute
     */
    protected void addClique(int[] array)
    {
        // count and store only defined cliques
        if (toStoreSize != 0)
        {
            if (array.length != toStoreSize)
            {
                return;
            }
        }

        if (store == ADD_UNIQUE_CLIQUES)
        {
            SortedHashedIntArray hashedClique = new SortedHashedIntArray(array);

            if (!cliquesUnsorted.containsKey(hashedClique))
            {
                cliquesUnsorted.put(hashedClique, hashedClique);
                binClique(hashedClique);
            }

            hashedClique = null;
        }
        else if (store == JUST_COUNT_CLIQUES)
        {
            countClique(array);
        }
        else
        {
            SortedHashedIntArray hashedClique = new SortedHashedIntArray(array);
            cliquesUnsorted.put(new Integer(counter++), hashedClique);
            binClique(hashedClique);
        }
    }

    /**
     *  Description of the Method
     *
     * @param  clique  Description of the Parameter
     */
    private void binClique(SortedHashedIntArray clique)
    {
        int cliqueSize = clique.length();
        buildCliqueHolder(cliqueSize);

        Hashtable cl = (Hashtable) cliques.get(cliqueSize - 1);

        if (!cl.containsKey(clique))
        {
            cl.put(clique, clique);
        }
    }

    /**
     *  Description of the Method
     *
     * @param  _size  Description of the Parameter
     */
    private void buildCliqueHolder(int _size)
    {
        if (cliques == null)
        {
            cliques = new Vector(_size);
        }

        int actSize = cliques.size();

        for (int i = actSize; i < _size; i++)
        {
            cliques.add(new Hashtable(10000));
        }
    }

    /**
     *  Description of the Method
     *
     * @param  array  Description of the Parameter
     */
    private void countClique(int[] array)
    {
        int size = array.length;
        countCliqueHolder(size);

        int[] ta = (int[]) cliques.get(size - 1);
        ta[0]++;
    }

    /**
     *  Description of the Method
     *
     * @param  _size  Description of the Parameter
     */
    private void countCliqueHolder(int _size)
    {
        if (cliques == null)
        {
            cliques = new Vector(_size);
        }

        int actSize = cliques.size();

        for (int i = actSize; i < _size; i++)
        {
            cliques.add(new int[]{0});
        }
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
