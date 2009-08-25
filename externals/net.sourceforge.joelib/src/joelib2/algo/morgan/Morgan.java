///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Morgan.java,v $
//  Purpose:  Morgan number generation and unique molecule numbering.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.algo.morgan;

import joelib2.algo.BFS;
import joelib2.algo.BFSResult;

import joelib2.feature.FeatureException;
import joelib2.feature.FeatureFactory;

import joelib2.molecule.Atom;
import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.molecule.fragmentation.ContiguousFragments;

import joelib2.sort.QuickInsertSort;
import joelib2.sort.XYIntArray;

import joelib2.util.iterator.NbrAtomIterator;

import wsi.ra.tool.Deque;
import wsi.ra.tool.DequeIterator;
import wsi.ra.tool.DequeNode;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Morgan number generation and unique molecule numbering.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:29 $
 * @.cite mor65
 */
public class Morgan
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.algo.morgan.Morgan");

    //~ Instance fields ////////////////////////////////////////////////////////

    private int newNumberCounter;
    private AtomDouble[] newNumbers;

    private TieResolver tieResolver = null;
    private boolean tieResolvingProblem;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the Morgan object
     */
    public Morgan(TieResolver _tieResolver)
    {
        tieResolver = _tieResolver;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Calculate the morgan numbers of a molecule.
     *
     * @param mol  the molecule
     */
    public boolean calculate(Molecule mol)
    {
        // reset new numbers
        newNumbers = null;

        // check for empty molecule
        if (mol.isEmpty())
        {
            logger.warn("Can not calculate morgan numbers for empty molecule.");
            tieResolvingProblem = true;

            return false;
        }

        tieResolvingProblem = false;
        initNewNumbers(mol);
        newNumberCounter = 1;

        //initialize helper variables
        int nAtoms = mol.getAtomsSize();
        double[] prevNumbers = new double[nAtoms + 1];
        double[] actNumbers = new double[nAtoms + 1];
        Map<Double, String> numbers = new Hashtable<Double, String>();
        int differentNumbers;
        int breakAfterEqualTimes = 3;

        //initialize first numbers
        for (int i = 1; i <= nAtoms; i++)
        {
            prevNumbers[i] = mol.getAtom(i).getValence();
            actNumbers[i] = 0.0;
        }

        // iterate nAtoms^2-times over the atoms
        // break morgan number calculation, if the
        // numbers don't change 'breakAfterEqualTimes'-times
        Atom atom;
        Atom nbr;
        int equalCounter = breakAfterEqualTimes;
        differentNumbers = -1;

        for (int outer = 1; outer <= nAtoms; outer++)
        {
            numbers.clear();

            for (int inner = 1; inner <= nAtoms; inner++)
            {
                atom = mol.getAtom(inner);
                actNumbers[inner] = 0.0;

                NbrAtomIterator nait = atom.nbrAtomIterator();

                while (nait.hasNext())
                {
                    nbr = nait.nextNbrAtom();
                    actNumbers[inner] += prevNumbers[nbr.getIndex()];
                }

                numbers.put(new Double(actNumbers[inner]), "");
            }

            // copy actual numbers to previous array
            // use faster arraycopy without arraychecking every time
            System.arraycopy(actNumbers, 0, prevNumbers, 0, actNumbers.length);

            // check for multiple numbers
            if (differentNumbers == numbers.size())
            {
                // and quit if different numbers occur breakAfterEqualTimes-times
                equalCounter--;

                if (equalCounter == 0)
                {
                    break;
                }
            }
            else
            {
                equalCounter = breakAfterEqualTimes;
            }

            differentNumbers = numbers.size();

            // verbose output
            //System.out.println("Morgan-Algorithm round "+outer+". "+differentNumbers+" different numbers.");
            //for(int i=1; i<=mol.numAtoms(); i++)
            //{
            //   System.out.println("atom #"+i+": "+actNumbers[i]);
            //}
        }

        for (int i = 1; i <= mol.getAtomsSize(); i++)
        {
            newNumbers[i - 1].tmpAtomIdx = actNumbers[i];
        }

        return true;
    }

    /**
     * Renumber a molecule and use the calulated morgan
     * numbers and try to resolve renumbering ties.
     *
     * @param mol  molecule to be renumbered
     * @return     renumbered molecule
     */
    public Molecule renumber(Molecule mol)
    {
        if (newNumbers == null)
        {
            logger.warn("No morgan numbers available. '" + mol.getTitle() +
                "' was not renumbered.");

            return mol;
        }

        Molecule newMolecule = null;

        Vector tmp = new Vector();
        ContiguousFragments.contiguousFragments(mol, tmp);

        if (tmp.size() > 1)
        {
            logger.warn("" + tmp.size() +
                " contiguous fragments in molecule '" + mol.getTitle() +
                "' (salt ?). Molecule was not renumbered.");

            return mol;
        }

        //              int itmp[];
        //              for (int i = 0; i < tmp.size(); i++)
        //              {
        //                      itmp=(int[])tmp.get(i);
        //                      for (int j = 0; j < itmp.length; j++)
        //                      {
        //                              System.out.print(itmp[j]);
        //                              System.out.print(' ');
        //                      }
        //                      System.out.println();
        //              }
        // get the breadth first search and start
        // from the atom with the highest morgan
        // number
        double maxNumber = -1.0;
        int maxNumberIndex = -1;

        for (int i = 0; i < mol.getAtomsSize(); i++)
        {
            if (maxNumber < newNumbers[i].tmpAtomIdx)
            {
                maxNumber = newNumbers[i].tmpAtomIdx;
                maxNumberIndex = newNumbers[i].atomIdx;
            }
        }

        BFSResult bfs = getBFS(mol, mol.getAtom(maxNumberIndex));

        // build sorted increasing deques for atom renumbering
        int maxBFSnumber = -1;

        for (int i = 0; i < bfs.getTraverse().length; i++)
        {
            if (maxBFSnumber < bfs.getTraverse()[i])
            {
                maxBFSnumber = bfs.getTraverse()[i];
            }
        }

        Deque[] deques = new Deque[maxBFSnumber + 1];
        buildDeques(mol, bfs, deques, maxBFSnumber);

        // initialize tie resolvers
        tieResolver.init(mol);

        //get new numbers and resolve ties
        for (int i = 0; i <= maxBFSnumber; i++)
        {
            if (logger.isDebugEnabled())
            {
                System.out.print("BFS " + i + ": ");
                showDeque(deques[i]);
            }

            if (!getNewNumbers(mol, deques[i]))
            {
                //System.out.println("problem "+mol.numAtoms());
                tieResolvingProblem = true;
            }
        }

        //if (tieResolvingProblem)
        //      logger.debug("Renumbering problem in " + mol.getTitle());
        //System.out.println("Unsorted morgan numbers (hopefully with resolved ties):");
        //for(int i=0; i<mol.numAtoms(); i++)
        //{
        //  System.out.println(newNumbers[i]);
        //}
        newMolecule = buildNewMolecule(mol);

        return newMolecule;
    }

    public boolean tieResolvingProblem()
    {
        return tieResolvingProblem;
    }

    /**
     * Build the sorted BFS spheres stored in deques.
     * The deques are sorted upwards to set the 'renumbering tie'-flags.
     *
     * @param mol           the molecule
     * @param bfs           the BFS result started from the atom with the atom
     *                      with the highest morgan number
     * @param deques        the array to store the deques
     * @param maxBFSnumber  the number of deques (BFS spheres)
     */
    private void buildDeques(Molecule mol, BFSResult bfs, Deque[] deques,
        int maxBFSnumber)
    {
        for (int i = 0; i <= maxBFSnumber; i++)
        {
            deques[i] = new Deque();
        }

        Deque deque;
        DequeNode front;
        DequeNode back;
        boolean insertAfter = true;
        DequeNode dNode = null;
        int bfsIndex;
        AtomDoubleParent tmpNumber;

        for (int i = 0; i < mol.getAtomsSize(); i++)
        {
            bfsIndex = newNumbers[i].atomIdx - 1;
            deque = deques[bfs.getTraverse()[bfsIndex]];
            front = deque.getFront();
            back = deque.getBack();
            tmpNumber = new AtomDoubleParent(newNumbers[i].atomIdx,
                    newNumbers[i].tmpAtomIdx, bfs.getParent()[bfsIndex], false);

            if ((back == null) || (front == null))
            {
                deque.pushBack(tmpNumber);
            }
            else
            {
                DequeIterator dit = deque.getDequeIterator();
                insertAfter = true;

                while (dit.hasNext())
                {
                    dNode = (DequeNode) dit.next();

                    if (newNumbers[i].tmpAtomIdx <=
                            ((AtomDoubleParent) dNode.key).tmpAtomIdx)
                    {
                        if (newNumbers[i].tmpAtomIdx ==
                                ((AtomDoubleParent) dNode.key).tmpAtomIdx)
                        {
                            ((AtomDoubleParent) dNode.key).tie = true;
                            tmpNumber.tie = true;
                        }

                        deque.insertBefore(dNode, tmpNumber);
                        insertAfter = false;

                        break;
                    }
                }

                if (insertAfter)
                {
                    deque.insertAfter(dNode, tmpNumber);
                }
            }
        }
    }

    /**
     * Build a new molecule and use the morgan numbers to
     * get the new numbers.
     * Be carefull:
     * Data elements like descriptors (JOEPairdata) or comment
     * data will not be copied !!!!
     * Data elements like SSSR and all typers should not be copied !!!
     *
     * @param mol  the molecule
     * @return     the new renumbered molecule
     */
    private Molecule buildNewMolecule(Molecule mol)
    {
        int checkBondNumber = mol.getBondsSize();
        int checkAtomNumber = mol.getAtomsSize();

        // sort the given numbers in newNumbers in
        // ascending order
        sortNewNumbers();

        Molecule newMolecule = new BasicConformerMolecule(mol.getInputType(),
                mol.getOutputType());
        newMolecule.beginModify();
        newMolecule.reserveAtoms(mol.getAtomsSize());

        Atom atom = mol.newAtom();
        Atom oldAtom;

        for (int i = 0; i < mol.getAtomsSize(); i++)
        {
            //System.out.println("add atom:"+mol.getAtom(newNumbers[i].atomIdx));
            oldAtom = mol.getAtom(newNumbers[i].atomIdx);
            atom.clear();
            atom.setCoords3D(oldAtom.getCoords3D());
            atom.setAtomicNumber(oldAtom.getAtomicNumber());
            atom.setType(oldAtom.getType());

            if (!newMolecule.addAtomClone(atom))
            {
                logger.error("Could not add atom.");

                return null;
            }
        }

        // create transformation hashtable to enable fast bond transformations
        Hashtable transform = new Hashtable(mol.getAtomsSize());

        for (int i = 0; i < mol.getAtomsSize(); i++)
        {
            // build look up table with
            // old number --> new number !!!
            transform.put(new Integer(newNumbers[i].atomIdx),
                new Integer((int) newNumbers[i].tmpAtomIdx));
        }

        // create renumbered bonds
        // bonds begin with index 0 !!!
        // for a correct unique or canonical SMILES
        // it's also necessarry to sort the bonds !!!
        Bond oldBond;
        int start;
        int end;
        int tmp;
        XYIntArray sortedBonds = new XYIntArray(mol.getBondsSize());
        Vector bonds = new Vector(mol.getBondsSize());

        for (int i = 0; i < mol.getBondsSize(); i++)
        {
            oldBond = mol.getBond(i);
            start =
                ((Integer) transform.get(
                        new Integer(oldBond.getBegin().getIndex()))).intValue();
            end =
                ((Integer) transform.get(
                        new Integer(oldBond.getEnd().getIndex()))).intValue();

            if (start >= end)
            {
                tmp = start;
                start = end;
                end = tmp;
            }

            sortedBonds.x[i] = start << (16 + end);
            sortedBonds.y[i] = i;

            bonds.add(
                new int[]
                {
                    start, end, oldBond.getBondOrder(), oldBond.getFlags()
                });
        }

        sortedBonds.sortX();

        int[] itmp;

        for (int i = 0; i < sortedBonds.x.length; i++)
        {
            itmp = (int[]) bonds.get(sortedBonds.y[i]);

            //System.out.println("Add Bond "+i+" "+start+" "+end);
            //System.out.println("add bond #"+i+": ("+start+"<--"+oldBond.getBeginAtom().getIdx()+") ("+end+"<--"+oldBond.getEndAtom().getIdx()+")");
            if (!newMolecule.addBond(itmp[0], itmp[1], itmp[2], itmp[3]))
            {
                logger.error("Could not add bond.");

                return null;
            }
        }

        newMolecule.endModify();

        newMolecule.setTitle(mol.getTitle());

        //System.out.println("new molecule:");
        //System.out.println(newMolecule);
        if (checkBondNumber != newMolecule.getBondsSize())
        {
            logger.error("Wrong number of bonds in renumbered molecule.");
        }

        if (checkAtomNumber != newMolecule.getAtomsSize())
        {
            logger.error("Wrong number of atoms in renumbered molecule.");
        }

        return newMolecule;
    }

    /**
     * Get the result of a breath first search of the given molecule
     * after starting from the given start atom. The start atom
     * should be the atom with the highest morgan number.
     *
     * @param mol        the molecule
     * @param startAtom  the start atom
     * @return           the result of the breath first search
     */
    private BFSResult getBFS(Molecule mol, Atom startAtom)
    {
        BFS bfs = null;
        BFSResult result = null;

        //        BFSInit init = new BFSInit(startAtom);
        Hashtable init = new Hashtable();
        init.put(BFS.STARTING_ATOM, startAtom);

        try
        {
            bfs = (BFS) FeatureFactory.getFeature(BFS.getName());
            result = (BFSResult) bfs.calculate(mol, init);
        }
        catch (FeatureException ex)
        {
            ex.printStackTrace();

            return null;
        }

        return result;
    }

    /**
     * Recalculate the given numbers of the morgan algorithm.
     * The first number begins at index 1. All index numbers
     * have after recalculation a difference of 1.
     * If renumbering ties occur in the same BFS sphere, they
     * are tried to be resolved by bond orders or atomic numbers.
     *
     * @param mol    the molecule
     * @param deque  the actual BFS sphere stored in a deque
     */
    private boolean getNewNumbers(Molecule mol, Deque deque)
    {
        DequeIterator dit;
        DequeNode node;
        dit = deque.getDequeIterator();

        AtomDoubleParent tmp;
        Vector ties = new Vector();
        Vector tieNumbers = new Vector();

        // renumber atoms
        while (dit.hasNext())
        {
            node = (DequeNode) dit.next();
            tmp = (AtomDoubleParent) node.key;

            if (tmp.tie)
            {
                ties.add(tmp);
                tieNumbers.add(new Integer(newNumberCounter++));
            }
            else
            {
                newNumbers[tmp.atomIdx - 1].tmpAtomIdx = (double)
                    newNumberCounter++;
            }
        }

        SingleTieResolver[] resolver = tieResolver.getTieResolvers();
        SingleTieResolver singleResolver;

        // resolves ties
        // try to resolve tie with bond orders
        int counter = ties.size();
        double maxResolverValue;
        double actResolverValue;
        int pickAtomIndex;
        int minNumber;
        int pickNumberIndex;

        for (int j = 0; j < resolver.length; j++)
        {
            singleResolver = resolver[j];

            for (int i = 0; i < counter; i++)
            {
                pickAtomIndex = -1;
                maxResolverValue = -Double.MAX_VALUE;
                pickNumberIndex = -1;
                minNumber = Integer.MAX_VALUE;

                for (int n = 0; n < ties.size(); n++)
                {
                    tmp = (AtomDoubleParent) ties.get(n);
                    actResolverValue = singleResolver.getResolvingValue(tmp,
                            mol);

                    if (maxResolverValue < actResolverValue)
                    {
                        maxResolverValue = actResolverValue;
                        pickAtomIndex = n;
                    }
                    else if (maxResolverValue == actResolverValue)
                    {
                        // can not resolve tie
                        pickAtomIndex = -1;
                    }

                    if (minNumber > ((Integer) tieNumbers.get(n)).intValue())
                    {
                        minNumber = ((Integer) tieNumbers.get(n)).intValue();
                        pickNumberIndex = n;
                    }
                }

                if (pickAtomIndex != -1)
                {
                    tmp = (AtomDoubleParent) ties.get(pickAtomIndex);
                    newNumbers[tmp.atomIdx - 1].tmpAtomIdx =
                        ((Integer) tieNumbers.get(pickNumberIndex)).intValue();

                    // done, remove atom and number from list
                    ties.remove(pickAtomIndex);
                    tieNumbers.remove(pickNumberIndex);
                }
            }
        }

        // use normal numbering if tie can not be resolved
        boolean tiesResolved = true;

        if (ties.size() != 0)
        {
            //System.out.println("WARN: Can not resolves tie.");
            tiesResolved = false;
        }

        for (int n = 0; n < ties.size(); n++)
        {
            tmp = (AtomDoubleParent) ties.get(n);
            newNumbers[tmp.atomIdx - 1].tmpAtomIdx =
                ((Integer) tieNumbers.get(n)).intValue();

            //System.out.println("REST: tmp.atomIdx:"+tmp.atomIdx+" tie_number:"+((Integer)tieNumbers.get(n)).intValue());
        }

        return tiesResolved;
    }

    /**
     * Initialize morgan numbers.
     *
     * @param mol  the molecule
     */
    private void initNewNumbers(Molecule mol)
    {
        newNumbers = new AtomDouble[mol.getAtomsSize()];

        for (int i = 0; i < mol.getAtomsSize(); i++)
        {
            newNumbers[i] = new AtomDouble();

            newNumbers[i].atomIdx = mol.getAtom(i + 1).getIndex();
            newNumbers[i].tmpAtomIdx = 0.0;
        }
    }

    /**
     * Show deque.
     *
     * @param deque  the deque
     */
    private void showDeque(Deque deque)
    {
        // show deques
        DequeIterator dit;
        DequeNode node;
        dit = deque.getDequeIterator();

        while (dit.hasNext())
        {
            node = (DequeNode) dit.next();
            System.out.print((AtomDoubleParent) node.key);
        }

        System.out.println();
    }

    /**
     * Sort morgan numbers.
     */
    private void sortNewNumbers()
    {
        QuickInsertSort sorting = new QuickInsertSort();
        sorting.sort(newNumbers, new AtomDoubleComparator());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
