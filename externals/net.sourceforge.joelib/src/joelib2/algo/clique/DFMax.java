///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DFMax.java,v $
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

import org.apache.log4j.Category;


/**
 * @author     wegnerj
 */
public class DFMax extends CliqueFinder
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(DFMax.class.getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private int breakNumCliques = 0;
    private int numCliques = 0;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Bron Kerbosch algorithm.
     */
    public DFMax()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  connected  Description of the Parameter
     * @return            Description of the Return Value
     */
    public int findCliques(byte[][] connected)
    {
        int minimumClique2find = 2;
        int storingLimit = 0;

        return findCliques(connected, storingLimit, minimumClique2find);
    }

    /**
     *  Description of the Method
     *
     * @param  connected  Description of the Parameter
     * @param  setlim     Description of the Parameter
     * @return            Description of the Return Value
     */
    public int findCliques(byte[][] connected, int storingLimit)
    {
        int minimumClique2find = 2;

        return findCliques(connected, storingLimit, minimumClique2find);
    }

    public int findCliques(byte[][] connected, int storingLimit,
        int minimumClique2find)
    {
        breakNumCliques = storingLimit;

        return dfmax(connected, minimumClique2find);
    }

    /**
     *  Description of the Method
     *
     * @param  bitmap  Description of the Parameter
     * @param  setlim  Description of the Parameter
     * @return         Description of the Return Value
     */
    private int dfmax(byte[][] bitmap, int minimumClique2find)
    {
        numCliques = 0;
        clear();

        int i;
        int j;
        Set set = new Set(bitmap.length);
        Set best = new Set(bitmap.length);

        int[] vertex = new int[bitmap.length];

        //begin reorder
        int cand = 0;
        int newcand = 0;
        int dmax;

        // sort so terms with highest degrees are at the end of "vertex"
        // vertex degrees with resp. to uncolored vertices
        int[] degree = new int[bitmap.length];

        // count num edges for each node
        for (i = 0; i < bitmap.length; i++)
        {
            degree[i] = 0;

            for (j = 0; j < bitmap.length; j++)
            {
                if (bitmap[j][i] == 0)
                {
                    degree[i]++;
                }
            }
        }

        // get max degree
        dmax = -1;

        for (i = 0; i < bitmap.length; i++)
        {
            if (degree[i] > dmax)
            {
                dmax = degree[i];
                cand = i;
            }
        }

        vertex[bitmap.length - 1] = cand;

        for (j = bitmap.length - 2; j >= 0; j--)
        {
            degree[cand] = -9;

            // dummy values
            dmax = -1;

            for (i = 0; i < bitmap.length; i++)
            {
                if (bitmap[i][cand] == 0)
                {
                    degree[i]--;
                }

                if (degree[i] > dmax)
                {
                    dmax = degree[i];
                    newcand = i;
                }
            }

            vertex[j] = cand = newcand;
        }

        degree = null;

        // end reorder
        // or simple without reorder
        // for (j=0;j<N;j++) vertex[j] = j;
        best.size = 0;
        i = dfmaxDescend(bitmap, bitmap.length - 1, minimumClique2find, vertex,
                1, set, best);
        set = null;
        vertex = null;

        return i;
    }

    /**
     *  Description of the Method
     *
     * @param  bitmap   Description of the Parameter
     * @param  top      Description of the Parameter
     * @param  goal     Description of the Parameter
     * @param  array    Description of the Parameter
     * @param  depth    Description of the Parameter
     * @param  set      Description of the Parameter
     * @param  bestset  Description of the Parameter
     * @return          Description of the Return Value
     */
    private int dfmaxDescend(byte[][] bitmap, int top, int goal, int[] array,
        int depth, Set set, Set bestset)
    {
        int i;

        if ((breakNumCliques > 0) && (numCliques >= breakNumCliques))
        {
            return breakNumCliques;
        }

        //System.out.println("goal: "+goal+" top:"+top);
        if (top <= 0)
        {
            if (top == -1)
            {
                depth--;
            }

            if (depth > bestset.size)
            {
                bestset.size = depth;

                if (top == 0)
                {
                    set.vertex[bestset.size - 1] = array[top];
                }

                for (i = 0; i < bestset.size; i++)
                {
                    bestset.vertex[i] = set.vertex[i];
                }

                // Have a new max clique here
            }

            // Have a new clique here
            Set s = new Set(bitmap.length);
            int result;

            s.size = bestset.size;
            s.vertex = bestset.vertex;

            int[] tmpResult = new int[bestset.size];
            System.arraycopy(bestset.vertex, 0, tmpResult, 0, bestset.size);
            addClique(tmpResult);
            numCliques++;

            //System.out.println("numCliques: "+numCliques);
            return top;
        }

        int best;
        int restbest;
        int newgoal;
        int w;
        int z;
        int[] pnew;
        int[] pold;
        int canthrow;

        int[] newarray;
        int newArrayIndex = 0;
        newarray = new int[top + 1];

        best = 0;
        newgoal = goal - 1;

        if (newgoal <= 0)
        {
            newgoal = 0;
        }

        for (i = top; i >= goal; i--)
        {
            boolean breakout = false;
            pnew = newarray;

            int pnewIndex = 0;
            w = array[i];
            set.vertex[depth - 1] = w;
            canthrow = i - goal;

            int poldIndex = 0;
            int arrayIndex = 0;
            pold = array;

            while (poldIndex < (arrayIndex + i))
            {
                z = pold[poldIndex++];

                if (bitmap[w][z] != 0)
                {
                    pnew[pnewIndex++] = z;
                }
                else
                {
                    if (canthrow == 0)
                    {
                        breakout = true;

                        break;
                    }

                    canthrow--;
                }
            }

            if (!breakout)
            {
                restbest = dfmaxDescend(bitmap, pnewIndex - newArrayIndex - 1,
                        newgoal, newarray, depth + 1, set, bestset);

                if (restbest >= newgoal)
                {
                    best = newgoal = restbest + 1;
                    goal = best + 1;
                }
            }
        }

        newarray = null;

        return best;
    }
}
///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
