///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: RingReducer.java,v $
//  Purpose:  Find the smallest set of smallest rings.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2006/02/22 02:18:22 $
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

import joelib2.molecule.Atom;
import joelib2.molecule.AtomTree;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.sort.QuickInsertSort;

import joelib2.util.BasicBitVector;
import joelib2.util.BitVector;

import joelib2.util.iterator.BasicRingIterator;
import joelib2.util.iterator.NbrAtomIterator;

import wsi.ra.tool.Deque;
import wsi.ra.tool.DequeIterator;
import wsi.ra.tool.DequeNode;

import java.util.List;
import java.util.Vector;


/**
 * Finds the Smallest Set of Smallest Rings (SSSR).
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2006/02/22 02:18:22 $
 * @.cite fig96
 */
public class RingReducer implements java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    private final static int RTREE_CUTOFF = 20;

    //~ Instance fields ////////////////////////////////////////////////////////

    private List<Ring> ringList;

    //~ Constructors ///////////////////////////////////////////////////////////

    protected RingReducer()
    {
        ringList = new Vector<Ring>();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Adds a feature to the RingFromClosure attribute of the JOERingSearch
     *  object
     *
     * @param  mol    The feature to be added to the RingFromClosure attribute
     * @param  cbond  The feature to be added to the RingFromClosure attribute
     * @param  level  The feature to be added to the RingFromClosure attribute
     */
    protected void addRingFromClosure(Molecule mol, Bond cbond, int level)
    {
        AtomTree[] atomTree1 = new AtomTree[mol.getAtomsSize() + 1];
        AtomTree[] atomTree2 = new AtomTree[mol.getAtomsSize() + 1];
        BitVector bits1 = new BasicBitVector();
        BitVector bits2 = new BasicBitVector();
        bits1.setBitOn(cbond.getEndIndex());
        bits2.setBitOn(cbond.getBeginIndex());

        buildRTreeVector(cbond.getBegin(), null, atomTree1, bits1);
        buildRTreeVector(cbond.getEnd(), null, atomTree2, bits2);

        Deque deque1 = new Deque();
        Deque deque2 = new Deque();
        List<Atom> path1 = new Vector<Atom>();
        List<Atom> path2 = new Vector<Atom>();
        AtomTree rtTmp1;
        AtomTree rtTmp2;

        for (int i = 0; i < atomTree1.length; i++)
        {
            rtTmp1 = atomTree1[i];

            if (rtTmp1 != null)
            {
                path1.clear();
                rtTmp1.pathToRoot(path1);
                rtTmp2 = atomTree2[rtTmp1.getAtomIdx()];

                if (rtTmp2 != null)
                {
                    path2.clear();
                    rtTmp2.pathToRoot(path2);
                    deque1.removeAll();
                    checkPaths(deque1, path1, deque2, path2,mol);
                }
            }
        }
    }

    /**
     *  Gets the ringIterator attribute of the JOERingSearch object
     *
     * @return    The ringIterator value
     */
    protected BasicRingIterator getRingIterator()
    {
        return new BasicRingIterator(ringList);
    }

    /**
     *  Description of the Method
     *
     * @param  frj  Description of the Parameter
     */
    protected void removeRedundant(int frj)
    {
        BasicBitVector tmp = new BasicBitVector();
        int ring1Idx;
        int ring2Idx;

        // remove identical rings
        removeIdenticalRings();

        //make sure tmp is the same size as the rings
        for (ring1Idx = 0; ring1Idx < ringList.size(); ring1Idx++)
        {
            tmp.set(ringList.get(ring1Idx).getAtomBits());
        }

        //remove larger rings that cover the same atoms as smaller rings
        for (ring1Idx = ringList.size() - 1; ring1Idx >= 0; ring1Idx--)
        {
            tmp.clear();

            for (ring2Idx = 0; ring2Idx < ringList.size(); ring2Idx++)
            {
                if ((ringList.get(ring2Idx).size() <=
                            ringList.get(ring1Idx).size()) &&
                        (ring1Idx != ring2Idx))
                {
                    tmp.orSet(ringList.get(ring2Idx).getAtomBits());
                }
            }

            tmp.andSet(ringList.get(ring1Idx).getAtomBits());

            if (tmp.equals(ringList.get(ring1Idx).getAtomBits()))
            {
                ringList.remove(ring1Idx);
            }

            if (ringList.size() == frj)
            {
                break;
            }
        }
    }

    /**
     * @param  deque1  <tt>Deque</tt> of <tt>int[1]</tt>
     * @param  deque2  <tt>Deque</tt> of <tt>int[1]</tt>
     * @return     Description of the Return Value
     */
    protected boolean saveUniqueRing(Deque deque1, Deque deque2, Molecule mol)
    {
        List<Integer> path = new Vector<Integer>();
        BasicBitVector bv = new BasicBitVector();
        DequeIterator di1 = deque1.getDequeIterator();
        DequeNode dNode;
        int[] itmp;

        while (di1.hasNext())
        {
            dNode = (DequeNode) di1.next();
            itmp = (int[]) dNode.key;
            bv.setBitOn(itmp[0]);
            path.add(new Integer(itmp[0]));
        }

        DequeIterator di2 = deque2.getDequeIterator();

        while (di2.hasNext())
        {
            dNode = (DequeNode) di2.next();
            itmp = (int[]) dNode.key;
            bv.setBitOn(itmp[0]);
            path.add(new Integer(itmp[0]));
        }

        boolean uniqueRing = true;
        BasicRingIterator rit = new BasicRingIterator(ringList);
        Ring ring;

        while (rit.hasNext())
        {
            ring = rit.nextRing();

            if (bv.equals(ring.getAtomBits()))
            {
                uniqueRing = false;
            }
        }

        if (uniqueRing)
        {
            ring = new BasicRing();
            ring.setAtomIndices(path);
            ring.setParent(mol);
            ringList.add(ring);
        }

        return uniqueRing;
    }

    /**
     *  Description of the Method
     */
    protected void sortRings()
    {
        QuickInsertSort sorting = new QuickInsertSort();

        RingSizeComparator ringSizeComparator = new RingSizeComparator();
        sorting.sort(ringList, ringSizeComparator);
    }

    /**
     * @param  previous   {@link java.util.Vector} of <tt>JOERTree</tt>
     * @param  atom  Description of the Parameter
     * @param  atomTree    Description of the Parameter
     * @param  bitVector    Description of the Parameter
     */
    private void buildRTreeVector(Atom atom, AtomTree previous,
        AtomTree[] atomTree, BitVector bitVector)
    {
        atomTree[atom.getIndex()] = new AtomTree(atom, previous);

        int i;
        Atom nbr;
        Molecule mol = atom.getParent();
        BitVector curr = new BasicBitVector();
        BitVector next = new BasicBitVector();
        BitVector used;

        curr.set(atom.getIndex());
        used = BasicBitVector.or(bitVector, curr);

        int level = 0;

        for (;;)
        {
            next.clear();

            for (i = curr.nextBit(0); i != bitVector.endBit();
                    i = curr.nextBit(i))
            {
                atom = mol.getAtom(i);

                NbrAtomIterator nait = atom.nbrAtomIterator();

                while (nait.hasNext())
                {
                    nbr = nait.nextNbrAtom();

                    if (!used.get(nbr.getIndex()))
                    {
                        next.set(nbr.getIndex());
                        used.set(nbr.getIndex());
                        atomTree[nbr.getIndex()] = new AtomTree(nbr,
                                atomTree[atom.getIndex()]);
                    }
                }
            }

            if (next.size() == 0)
            {
                break;
            }

            curr.set(next);
            level++;

            if (level > RTREE_CUTOFF)
            {
                break;
            }
        }
    }

    /**
     * @param deque1
     * @param path1
     * @param deque2
     * @param path2
     */
    private void checkPaths(Deque deque1, List<Atom> path1, Deque deque2,
        List<Atom> path2, Molecule mol)
    {
        boolean isPathOk = true;
        Atom atom1 = (Atom) path1.get(0);

        if (atom1 != path1.get(path1.size() - 1))
        {
            deque1.pushBack(new int[]{atom1.getIndex()});
        }

        Atom atom2;

        for (int path1Idx = 1; path1Idx < path1.size(); path1Idx++)
        {
            atom1 = path1.get(path1Idx);
            deque1.pushBack(new int[]{atom1.getIndex()});
            deque2.removeAll();

            for (int path2Idx = 1; path2Idx < path2.size(); path2Idx++)
            {
                atom2 = path2.get(path2Idx);
                deque2.pushFront(new int[]{atom2.getIndex()});

                if (atom2 == atom1)
                {
                    //don't traverse across identical atoms
                    deque2.popFront();

                    if ((deque1.size() + deque2.size()) > 2)
                    {
                        saveUniqueRing(deque1, deque2,mol);
                    }

                    isPathOk = false;

                    break;
                }

                if (atom2.isConnected(atom1) &&
                        ((deque1.size() + deque2.size()) > 2))
                {
                    saveUniqueRing(deque1, deque2, mol);
                }
            }

            if (!isPathOk)
            {
                break;
            }
        }
    }

    /**
     *  Description of the Method
     */
    private void debugRings()
    {
        for (int i = 0; i < ringList.size(); i++)
        {
            System.out.println(ringList.get(i).getAtomBits());
        }
    }

    /**
     *
     */
    private void removeIdenticalRings()
    {
        //remove identical rings
        for (int ring1Idx = ringList.size() - 1; ring1Idx > 0; ring1Idx--)
        {
            for (int ring2Idx = ring1Idx - 1; ring2Idx >= 0; ring2Idx--)
            {
                if (ringList.get(ring1Idx).equals(ringList.get(ring2Idx)))
                {
                    ringList.remove(ring1Idx);

                    break;
                }
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
