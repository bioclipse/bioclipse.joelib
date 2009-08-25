///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: SMILESGenerator.java,v $
//  Purpose:  Reader/Writer for SDF files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.12 $
//            $Date: 2005/02/17 16:48:40 $
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
package joelib2.smiles;

import joelib2.data.BasicElementHolder;

import joelib2.feature.types.atomlabel.AtomHeavyValence;
import joelib2.feature.types.atomlabel.AtomImplicitHydrogenCount;
import joelib2.feature.types.atomlabel.AtomImplicitValence;
import joelib2.feature.types.atomlabel.AtomInAromaticSystem;
import joelib2.feature.types.atomlabel.AtomIsChiral;
import joelib2.feature.types.atomlabel.AtomIsHydrogen;
import joelib2.feature.types.atomlabel.AtomIsNitrogen;
import joelib2.feature.types.atomlabel.AtomKekuleBondOrderSum;
import joelib2.feature.types.bondlabel.BondInAromaticSystem;

import joelib2.math.BasicVector3D;
import joelib2.math.Vector3D;

import joelib2.molecule.Atom;
import joelib2.molecule.AtomHelper;
import joelib2.molecule.Bond;
import joelib2.molecule.IsomerismHelper;
import joelib2.molecule.Molecule;

import joelib2.util.BasicBitVector;
import joelib2.util.BitVector;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BasicAtomIterator;
import joelib2.util.iterator.BasicBondIterator;
import joelib2.util.iterator.BasicNbrAtomIterator;
import joelib2.util.iterator.BondIterator;
import joelib2.util.iterator.NbrAtomIterator;

import joelib2.util.types.AtomIntInt;
import joelib2.util.types.BasicAtomIntInt;
import joelib2.util.types.BasicBondInt;
import joelib2.util.types.BasicIntInt;
import joelib2.util.types.BondInt;

import wsi.ra.tool.BasicPropertyHolder;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Molecule to SMILES methods.
 *
 * @.author     wegnerj
 * @.wikipedia  Simplified molecular input line entry specification
 * @.license GPL
 * @.cvsversion    $Revision: 1.12 $, $Date: 2005/02/17 16:48:40 $
 */
public class SMILESGenerator implements java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(SMILESGenerator.class
            .getName());
    private final static boolean KEKULE = false;

    //~ Instance fields ////////////////////////////////////////////////////////

    private List<boolean[]> _aromNH;
    private boolean assignChirality = true;
    private boolean assignCisTrans = true;
    private List<int[]> atomOrdering;
    private List<Bond> closures;
    private List<AtomIntInt> opens;
    private List<int[]> stereoOrdering;
    private BitVector usedBonds;
    private BasicBitVector visitedAtoms;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the JOEMol2Smi object
     */
    public SMILESGenerator()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }

        atomOrdering = new Vector<int[]>();
        stereoOrdering = new Vector<int[]>();
        _aromNH = new Vector<boolean[]>();
        closures = new Vector<Bond>();
        opens = new Vector<AtomIntInt>();

        visitedAtoms = new BasicBitVector();
        usedBonds = new BasicBitVector();

        String value = BasicPropertyHolder.instance().getProperties()
                                          .getProperty(
                SMILESGenerator.class.getName() + ".assignCisTransInformations");

        if (((value != null) && value.equalsIgnoreCase("false")))
        {
            assignCisTrans = false;
        }
        else
        {
            assignCisTrans = true;
        }

        value = BasicPropertyHolder.instance().getProperties().getProperty(
                SMILESGenerator.class.getName() +
                ".assignChiralityInformations");

        if (((value != null) && value.equalsIgnoreCase("false")))
        {
            assignChirality = false;
        }
        else
        {
            assignChirality = true;
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Traverse the tree searching for acyclic olefins and assign
     * stereochemistry if it has at least one heavy atom attachment
     * on each end.
     *
     * @param node  Description of the Parameter
     */
    public void assignCisTrans(SMILESNode node)
    {
        if (assignCisTrans == false)
        {
            return;
        }

        Bond bond;

        for (int i = 0; i < node.size(); i++)
        {
            bond = node.getNextBond(i);

            // allow consitency with all other auto-detection methods
            IsomerismHelper.isCisTransBond(bond, true);

            // this single specialized code causes inconsitencies with
            // other code snippets
            /*            // handle only double bonds
                        if ((bond.getBO() == 2) && !bond.isInRing())
                        {
                                // get atoms of this double bond
                            Atom b = node.getAtom();
                            Atom c = bond.getNbrAtom(b);
                            //System.out.println("atoms b="+b.getIdx()+" c="+c.getIdx());

                            //skip allenes
                            if ((b.getHyb() == 1) || (c.getHyb() == 1))
                            {
                                continue;
                            }

                            if ((b.getHvyValence() > 1) && (c.getHvyValence() > 1))
                            {
                                Atom a = null;
                                Atom d = null;

                                //look for bond with assigned stereo as in poly-ene
                                NbrAtomIterator nait = b.nbrAtomIterator();

                                boolean upDownFound=false;
                                while (nait.hasNext())
                                {
                                    a = nait.nextNbrAtom();

                                    if (nait.actualBond().isUp() ||
                                            nait.actualBond().isDown())
                                    {
                                            upDownFound=true;
                                        break;
                                    }
                                }

                                if (!upDownFound)
                                {
                                    nait.reset();

                                    while (nait.hasNext())
                                    {
                                        a = nait.nextNbrAtom();

                                               if ((a != c) && !a.isHydrogen())
                                        {
                                            break;
                                        }
                                    }
                                }

                                NbrAtomIterator nait2 = c.nbrAtomIterator();

                                while (nait2.hasNext())
                                {
                                    d = nait2.nextNbrAtom();

                                    if ((d != b) && !d.isHydrogen())
                                    {
                                        break;
                                    }
                                }

                                //System.out.println("atoms: a="+a.getIdx()+" d="+d.getIdx());

                                //              assert a==null;
                                //              assert d==null;
                                if (nait.actualBond().isUp() || nait.actualBond().isDown())
                                {
                                    //stereo already assigned
                                    if (Math.abs(XYZVector.calcTorsionAngle(a.getVector(),
                                                    b.getVector(), c.getVector(),
                                                    d.getVector())) > 10.0)
                                    {
                                        if (nait.actualBond().isUp())
                                        {
                                            nait2.actualBond().setDown();
                                        }
                                        else
                                        {
                                            nait2.actualBond().setUp();
                                        }
                                    }
                                    else if (nait.actualBond().isUp())
                                    {
                                        nait2.actualBond().setUp();
                                    }
                                    else
                                    {
                                        nait2.actualBond().setDown();
                                    }
                                }
                                else
                                {
                                    //assign stereo to both ends
                                    nait.actualBond().setUp();

                                    if (Math.abs(XYZVector.calcTorsionAngle(a.getVector(),
                                                    b.getVector(), c.getVector(),
                                                    d.getVector())) > 10.0)
                                    {
                                        nait2.actualBond().setDown();
                                    }
                                    else
                                    {
                                        nait2.actualBond().setUp();
                                    }
                                }
                            }
                        }
            */
            assignCisTrans(node.getNextNode(i));
        }
    }

    /**
     * Description of the Method
     *
     * @param node  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean buildTree(SMILESNode node)
    {
        Atom nbr;
        Atom atom = node.getAtom();

        //        System.out.println("atom:"+atom.getIdx());
        //mark the atom as visited
        visitedAtoms.setBitOn(atom.getIndex());

        //store the atom ordering
        atomOrdering.add(new int[]{atom.getIndex()});

        //store the atom ordering for stereo
        stereoOrdering.add(new int[]{atom.getIndex()});

        NbrAtomIterator nait = atom.nbrAtomIterator();

        while (nait.hasNext())
        {
            nbr = nait.nextNbrAtom();

            //            System.out.println("NBR:"+nbr.getIdx());
            //            if (!nbr->IsHydrogen() && !visitedAtoms[nbr->GetIdx()])
            //            {
            //          cout<<"_ubonds.SetBitOn("<<(*i)->GetIdx()<<")"<<endl;
            //                  _ubonds.SetBitOn((*i)->GetIdx());
            //                  OESmiNode *next = new OESmiNode (nbr);
            //                  next->SetParent(atom);
            //                  node->SetNextNode(next,*i);
            //                  BuildTree(next);
            //            }
            if (!AtomIsHydrogen.isHydrogen(nbr) &&
                    !visitedAtoms.get(nbr.getIndex()))
            {
                //                System.out.println("_ubonds.setBitOn("+nait.actualBond().getIdx()+")");
                usedBonds.setBitOn(nait.actualBond().getIndex());

                SMILESNode next = new SMILESNode(nbr);
                next.setParent(atom);
                node.setNextNode(next, nait.actualBond());
                buildTree(next);
            }
        }

        return (true);
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     */
    public void correctAromaticAmineCharge(Molecule mol)
    {
        _aromNH.clear();

        if (_aromNH instanceof Vector)
        {
            ((Vector) _aromNH).ensureCapacity(mol.getAtomsSize() + 1);
        }

        for (int i = 0; i <= mol.getAtomsSize(); i++)
        {
            _aromNH.add(new boolean[]{false});
        }

        Atom atom;
        AtomIterator ait = mol.atomIterator();
        boolean[] btmp;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (AtomIsNitrogen.isNitrogen(atom) &&
                    AtomInAromaticSystem.isValue(atom))
            {
                if (AtomHeavyValence.valence(atom) == 2)
                {
                    if ((atom.getValence() == 3) ||
                            (AtomImplicitValence.getImplicitValence(atom) == 3))
                    {
                        btmp = (boolean[]) _aromNH.get(atom.getIndex());
                        btmp[0] = true;
                    }
                }
            }
        }
    }

    /**
     * Description of the Method
     *
     * @param mol     Description of the Parameter
     * @param smiles  Description of the Parameter
     */
    public void createSmiString(Molecule mol, StringBuffer smiles)
    {
        Atom atom;
        SMILESNode root;

        //    buffer[0] = '\0';
        //              BondIterator bit = mol.bondIterator();
        //              Bond bond;
        //              while (bit.hasNext())
        //              {
        //                      bond = bit.nextBond();
        //                      //    System.out.println("BOND:"+bond.getIdx()+" "+bond.getBeginAtomIdx()+" "+bond.getEndAtomIdx());
        //              }
        //  System.out.println("createSmiString");
        AtomIterator ait = mol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            //    if ((!atom.isHydrogen() || atom.getValence() == 0) && !visitedAtoms[atom.getIdx()])
            if (!AtomIsHydrogen.isHydrogen(atom) &&
                    !visitedAtoms.get(atom.getIndex()))
            {
                if (!AtomIsChiral.isChiral(atom))
                {
                    //don't use chiral atoms as root node
                    //                    System.out.println("ATOM:"+atom.getIdx());
                    //clear out closures in case structure is dot disconnected
                    closures.clear();
                    atomOrdering.clear();
                    stereoOrdering.clear();
                    opens.clear();

                    //dot disconnected structure
                    if (smiles.length() > 0)
                    {
                        smiles.append(".");
                    }

                    root = new SMILESNode(atom);
                    buildTree(root);
                    findClosureBonds(mol);

                    if (mol.has2D())
                    {
                        assignCisTrans(root);
                    }

                    toSmilesString(root, smiles);
                    root = null;
                }
            }
        }
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     */
    public void findClosureBonds(Molecule mol)
    {
        Atom atom1;
        Atom atom2;
        Bond bond;
        BitVector bitVector = new BasicBitVector();
        bitVector.fromVectorWithIntArray(stereoOrdering);

        BondIterator bit = mol.bondIterator();

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            //            System.out.println("!_ubonds.get("+bond.getIdx()+"):"+(!_ubonds.get(bond.getIdx()) ));
            //            System.out.println("bv.get("+bond.getBeginAtomIdx()+"):"+bv.get(bond.getBeginAtomIdx()) );
            if (!usedBonds.get(bond.getIndex()) &&
                    bitVector.get(bond.getBeginIndex()))
            {
                atom1 = bond.getBegin();
                atom2 = bond.getEnd();

                if (!AtomIsHydrogen.isHydrogen(atom1) &&
                        !AtomIsHydrogen.isHydrogen(atom2))
                {
                    //                  System.out.print("add");
                    closures.add(bond);
                }
            }
        }

        //modify _order to reflect ring closures
        int[] itmp;

        for (int j = closures.size() - 1; j >= 0; j--)
        {
            bond = closures.get(j);
            atom1 = atom2 = null;

            for (int k = 0; k < stereoOrdering.size(); k++)
            {
                itmp = (int[]) stereoOrdering.get(k);

                if ((bond.getBeginIndex() == itmp[0]) ||
                        (bond.getEndIndex() == itmp[0]))
                {
                    //System.out.println("found "+itmp[0]+" of "+bond.getBeginAtomIdx()+" and "+bond.getEndAtomIdx());
                    if (atom1 == null)
                    {
                        atom1 = mol.getAtom(itmp[0]);
                    }
                    else if (atom2 == null)
                    {
                        atom2 = mol.getAtom(itmp[0]);
                        stereoOrdering.remove(k);

                        break;
                    }
                }
            }

            for (int k = 0; k < stereoOrdering.size(); k++)
            {
                itmp = (int[]) stereoOrdering.get(k);

                if (atom1.getIndex() == itmp[0])
                {
                    k++;

                    // vector<int>::iterator k;
                    if (k != (stereoOrdering.size() - 1))
                    {
                        ((Vector) stereoOrdering).insertElementAt(
                            new int[]{atom2.getIndex()}, k);
                    }
                    else
                    {
                        stereoOrdering.add(new int[]{atom2.getIndex()});
                    }

                    break;
                }
            }
        }
    }

    /**
     * Gets the chiralStereo attribute of the JOEMol2Smi object
     *
     * @param node    Description of the Parameter
     * @param smiles  Description of the Parameter
     * @return        The chiralStereo value
     */
    public boolean getChiralStereo(SMILESNode node, StringBuffer smiles)
    {
        boolean is2D = false;
        double torsion;
        Atom atom1;
        Atom atom2;
        Atom atom3;
        Atom atom4;
        Atom hydrogen = node.getAtom().getParent().newAtom();

        if (assignChirality == false)
        {
            return false;
        }

        atom2 = node.getAtom();

        Molecule mol = atom2.getParent();

        if (!mol.hasNonZeroCoords())
        {
            //must have come in from smiles string
            if (!atom2.hasChiralitySpecified())
            {
                return (false);
            }

            if (atom2.isClockwise())
            {
                smiles.append("@@");
            }
            else if (atom2.isAntiClockwise())
            {
                smiles.append("@");
            }
            else
            {
                return (false);
            }

            //if (b.getHvyValence() == 3) smiles.append("H");
            return (true);
        }

        //give peudo Z coords if mol is 2D
        if (!mol.has3D())
        {
            Vector3D vector = new BasicVector3D();
            Vector3D zVector = new BasicVector3D(0.0, 0.0, 1.0);
            is2D = true;

            Atom nbr;
            Bond bond;
            BondIterator bit = atom2.bondIterator();

            while (bit.hasNext())
            {
                bond = bit.nextBond();
                nbr = bond.getEnd();

                if (nbr != atom2)
                {
                    vector = nbr.getCoords3D();

                    if (bond.isWedge())
                    {
                        vector.adding(zVector);
                    }
                    else if (bond.isHash())
                    {
                        vector.subing(zVector);
                    }

                    nbr.setCoords3D(vector);
                }
                else
                {
                    nbr = bond.getBegin();
                    vector = nbr.getCoords3D();

                    if (bond.isWedge())
                    {
                        vector.subing(zVector);
                    }
                    else if (bond.isHash())
                    {
                        vector.adding(zVector);
                    }

                    nbr.setCoords3D(vector);
                }
            }
        }

        atom3 = atom4 = null;
        atom1 = node.getParent();

        // chiral atom can't be used as root node - must have parent
        if (AtomHeavyValence.valence(atom2) == 3)
        {
            //must have attached hydrogen
            if (atom2.getValence() == 4)
            {
                //has explicit hydrogen
                NbrAtomIterator nait = atom2.nbrAtomIterator();

                while (nait.hasNext())
                {
                    atom3 = nait.nextNbrAtom();

                    if (AtomIsHydrogen.isHydrogen(atom3))
                    {
                        break;
                    }
                }
            }
            else
            {
                //implicit hydrogen
                BasicVector3D v = new BasicVector3D();
                AtomHelper.getNewBondVector3D(atom2, v, 1.0);
                hydrogen.setCoords3D(v);
                atom3 = hydrogen;
            }
        }

        //get connected atoms in order
        //try to get neighbors that are closure atoms in the order they appear in the string
        List<Atom> va = new Vector<Atom>();
        getClosureAtoms(atom2, va);

        Atom atom;
        Atom nbr;

        if (va.size() != 0)
        {
            for (int k = 0; k < va.size(); k++)
            {
                atom = (Atom) va.get(k);

                if (atom != atom1)
                {
                    if (atom3 == null)
                    {
                        atom3 = atom;
                    }
                    else if (atom4 == null)
                    {
                        atom4 = atom;
                    }
                }
            }
        }

        int[] itmp;

        for (int j = 0; j < stereoOrdering.size(); j++)
        {
            itmp = (int[]) stereoOrdering.get(j);
            nbr = mol.getAtom(itmp[0]);

            if (!atom2.isConnected(nbr))
            {
                continue;
            }

            if ((nbr == atom1) || (nbr == atom2) || (nbr == atom3))
            {
                continue;
            }

            if (atom3 == null)
            {
                atom3 = nbr;
            }
            else if (atom4 == null)
            {
                atom4 = nbr;
            }
        }

        torsion = BasicVector3D.calcTorsionAngle(atom1.getCoords3D(),
                atom2.getCoords3D(), atom3.getCoords3D(), atom4.getCoords3D());

        smiles.append((torsion < 0.0) ? "@" : "@@");

        //if (b.getHvyValence() == 3) smiles.append("H");
        //re-zero psuedo-coords
        if (is2D)
        {
            Vector3D vector;
            AtomIterator ait = mol.atomIterator();

            while (ait.hasNext())
            {
                atom = ait.nextAtom();
                vector = atom.getCoords3D();
                vector.setZ3D(0.0);
                atom.setCoords3D(vector);
            }
        }

        return (true);
    }

    /**
     * @param va    {@link java.util.List} of <tt>JJOEAtom</tt>
     * @param atom  Description of the Parameter
     */
    public void getClosureAtoms(Atom atom, List<Atom> va)
    {
        //look through closure list for start atom
        Bond bond;

        for (int i = 0; i < closures.size(); i++)
        {
            bond = closures.get(i);

            if (bond != null)
            {
                if (bond.getBegin() == atom)
                {
                    va.add(bond.getEnd());
                }

                if (bond.getEnd() == atom)
                {
                    va.add(bond.getBegin());
                }
            }
        }

        Atom nbr;
        AtomIntInt aii;
        NbrAtomIterator nait = atom.nbrAtomIterator();

        for (int j = 0; j < opens.size(); j++)
        {
            aii = opens.get(j);
            nait.reset();

            while (nait.hasNext())
            {
                nbr = nait.nextNbrAtom();

                if (nbr == aii.getAtom())
                {
                    va.add(nbr);
                }
            }
        }
    }

    /**
     * @param atom  Description of the Parameter
     * @return      {@link java.util.List} of <tt>BondInt</tt>
     */
    public List getClosureDigits(Atom atom)
    {
        List<BondInt> vc = new Vector<BondInt>();

        // of type BondInt
        //look through closure list for start atom
        int idx;

        // of type BondInt
        //look through closure list for start atom
        int bo;
        Bond bond;

        //        System.out.println("size"+_vclose.size());
        for (int i = 0; i < closures.size(); i++)
        {
            bond = (Bond) closures.get(i);

            if (bond != null)
            {
                //                System.out.println("check: "+bond.getBO());
                if ((bond.getBegin() == atom) || (bond.getEnd() == atom))
                {
                    idx = getUnusedIndex();
                    vc.add(new BasicBondInt(bond, idx));
                    bo = (BondInAromaticSystem.isAromatic(bond))
                        ? 1 : bond.getBondOrder();
                    opens.add(new BasicAtomIntInt(bond.getNeighbor(atom),
                            new BasicIntInt(idx, bo)));

                    //                    _vclose.set(i, null);
                    closures.remove(i--);

                    //remove bond from closure list
                }
            }
        }

        //try to complete closures
        if (opens.size() != 0)
        {
            AtomIntInt aii;

            for (int j = 0; j < opens.size();)
            {
                aii = opens.get(j);

                if (aii.getAtom() == atom)
                {
                    vc.add(new BasicBondInt(null, aii.getIntValue1()));
                    opens.remove(j);
                    j = 0;
                }
                else
                {
                    j++;
                }
            }
        }

        return (vc);
    }

    /**
     * @return   {@link java.util.Vector} of <tt>int[1]</tt>
     */
    public List getOutputOrder()
    {
        return (atomOrdering);
    }

    /**
     * Gets the smilesElement attribute of the JOEMol2Smi object
     *
     * @param node    Description of the Parameter
     * @param smiles  Description of the Parameter
     * @return        The smilesElement value
     */
    public boolean getSmilesElement(SMILESNode node, StringBuffer smiles)
    {
        char[] symbol;
        boolean bracketElement = false;
        boolean normalValence = true;

        Atom atom = node.getAtom();

        int bosum = AtomKekuleBondOrderSum.getIntValue(atom);

        switch (atom.getAtomicNumber())
        {
        case 0:
            break;

        case 5:

            /*bracketElement = !(normalValence = (bosum == 3)); break; */
            break;

        case 6:
            break;

        case 7:

            if (AtomInAromaticSystem.isValue(atom) &&
                    (AtomHeavyValence.valence(atom) == 2) &&
                    (AtomImplicitValence.getImplicitValence(atom) == 3))
            {
                bracketElement = !(normalValence = false);

                break;
            }
            else
            {
                bracketElement = !(normalValence = ((bosum == 3) ||
                                (bosum == 5)));
            }

            break;

        case 8:
            break;

        case 9:
            break;

        case 15:
            break;

        case 16:
            bracketElement = !(normalValence = ((bosum == 2) || (bosum == 4) ||
                            (bosum == 6)));

            break;

        case 17:
            break;

        case 35:
            break;

        case 53:
            break;

        default:
            bracketElement = true;
        }

        if ((AtomHeavyValence.valence(atom) > 2) && AtomIsChiral.isChiral(atom))
        {
            if (atom.getParent().hasNonZeroCoords() ||
                    atom.hasChiralitySpecified())
            {
                bracketElement = true;
            }
        }

        if (atom.getFormalCharge() != 0)
        {
            //bracket charged elements
            bracketElement = true;
        }

        if (!bracketElement)
        {
            if (atom.getAtomicNumber() == 0)
            {
                boolean external = false;

                //never happens !!!!
                //old code
                //        Vector externalBonds = atom.getParent().getData("extBonds");
                //        if (externalBonds!=null)
                //          for(externalBond = externalBonds->begin();externalBond != externalBonds->end();externalBond++)
                //            {
                //              if (externalBond->second.first == atom)
                //                {
                //                  external = true;
                //                  smiles.append("&");
                //                  Bond bond = externalBond->second.second;
                //                  if (bond.isUp())                              smiles.append("\\");
                //                  if (bond.isDown())                            smiles.append("/");
                //if(!KEKULE)
                //{
                //                  if (bond.getBO() == 2 && !bond.isAromatic()) smiles.append("=");
                //                  if (bond.getBO() == 2 && bond.isAromatic())  smiles.append(";");
                //}else{
                //                  if (bond.getBO() == 2)                        smiles.append("=");
                //}
                //                  if (bond.getBO() == 3)                        smiles.append("#");
                //                  smiles.append(externalBond->first);
                //                  break;
                //                }
                //            }
                if (!external)
                {
                    smiles.append("*");
                }
            }
            else
            {
                symbol =
                    (BasicElementHolder.instance()
                                       .getSymbol(atom.getAtomicNumber()))
                    .toCharArray();

                if (!KEKULE)
                {
                    if (AtomInAromaticSystem.isValue(atom))
                    {
                        symbol[0] = Character.toLowerCase(symbol[0]);
                    }
                }

                smiles.append(symbol);
            }

            return (true);
        }

        smiles.append("[");

        if (atom.getAtomicNumber() == 0)
        {
            smiles.append("*");
        }
        else
        {
            symbol =
                (BasicElementHolder.instance().getSymbol(
                        atom.getAtomicNumber())).toCharArray();

            if (!KEKULE)
            {
                if (AtomInAromaticSystem.isValue(atom))
                {
                    symbol[0] = Character.toLowerCase(symbol[0]);
                }
            }

            smiles.append(symbol);
        }

        //if (atom.isCarbon() && atom.getHvyValence() > 2 && atom.isChiral())
        if ((AtomHeavyValence.valence(atom) > 2) && AtomIsChiral.isChiral(atom))
        {
            //char stereo[5];
            //if (getChiralStereo(node,stereo))   strcat(element,stereo);
            getChiralStereo(node, smiles);
        }

        //add extra hydrogens
        //  if (!normalValence && atom.implicitHydrogenCount())
        if (AtomImplicitHydrogenCount.getIntValue(atom) != 0)
        {
            smiles.append("H");

            if (AtomImplicitHydrogenCount.getIntValue(atom) > 1)
            {
                // char tcount[10];
                // sprintf(tcount,"%d",atom.implicitHydrogenCount());
                // strcat(element,tcount);
                smiles.append(AtomImplicitHydrogenCount.getIntValue(atom));
            }
        }

        //cat charge on the end
        if (atom.getFormalCharge() != 0)
        {
            //            /*
            //             *if (atom.implicitHydrogenCount())
            //             *{
            //             *logger.error("imp = "+atom.getAtomicNum()+" "+atom.getImplicitValence());
            //             *smiles.append("H");
            //             *if (atom.implicitHydrogenCount() > 1)
            //             *{
            //             */char tcount[10];
            //             */sprintf(tcount,"%d",atom.implicitHydrogenCount());
            //             */strcat(element,tcount);
            //             *smiles.append( atom.implicitHydrogenCount() );
            //             *}
            //             *}
            //             */
            if (atom.getFormalCharge() > 0)
            {
                smiles.append("+");
            }
            else
            {
                smiles.append("-");
            }

            if (Math.abs(atom.getFormalCharge()) > 1)
            {
                //char tcharge[10];
                //sprintf(tcharge,"%d",abs(atom.getFormalCharge()));
                //strcat(element,tcharge);
                smiles.append(Math.abs(atom.getFormalCharge()));
            }
        }

        smiles.append("]");

        return (true);
    }

    /**
     * Gets the unusedIndex attribute of the JOEMol2Smi object
     *
     * @return   The unusedIndex value
     */
    public int getUnusedIndex()
    {
        int idx = 1;

        AtomIntInt aii;

        for (int j = 0; j < opens.size();)
        {
            aii = opens.get(j);

            if (aii.getIntValue1() == idx)
            {
                idx++;

                //increment idx and start over if digit is already used
                j = 0;
            }
            else
            {
                j++;
            }
        }

        return (idx);
    }

    //public void         correctAromaticAmineCharge(Molecule mol)
    //  {
    //    Atom atom;
    //
    //  _aromNH.clear();
    //  _aromNH.ensureCapacity(mol.numAtoms()+1);
    //
    //  AtomIterator ait = this.atomIterator();
    //  boolean btmp[];
    //  while(ait.hasNext())
    //  {
    //    atom = ait.nextAtom();
    //     if (atom.isNitrogen() && atom.isAromatic())
    //     {
    //      if (atom.getHvyValence() == 2)
    //  {
    //    if (atom.getValence() == 3 || atom.getImplicitValence() == 3)
    //          {
    //            btmp = (boolean[])_aromNH.get(atom.getIdx());
    //      btmp[0] = true;
    //          }
    //  }
    //    }
    //  }
    //
    //  }

    /**
     * Description of the Method
     */
    public void init()
    {
        closures.clear();
        atomOrdering.clear();
        stereoOrdering.clear();
        _aromNH.clear();
        visitedAtoms.clear();
        usedBonds.clear();
        opens.clear();
    }

    /**
     * Description of the Method
     */
    public void removeUsedClosures()
    {
    }

    /**
     * Description of the Method
     *
     * @param node    Description of the Parameter
     * @param smiles  Description of the Parameter
     */
    public void toSmilesString(SMILESNode node, StringBuffer smiles)
    {
        //      char tmpbuf[10];
        Atom atom = node.getAtom();

        //write the current atom to the string
        getSmilesElement(node, smiles);

        //handle ring closures here
        List vc = getClosureDigits(atom);

        // ot type BondInt
        if (vc.size() != 0)
        {
            BasicBondInt bi;

            for (int i = 0; i < vc.size(); i++)
            {
                bi = (BasicBondInt) vc.get(i);

                if (bi.bond != null)
                {
                    if (assignCisTrans)
                    {
                        if (bi.bond.isUp())
                        {
                            smiles.append(SMILESParser.UP_BOND_FLAG);
                        }

                        if (bi.bond.isDown())
                        {
                            smiles.append(SMILESParser.DOWN_BOND_FLAG);
                        }
                    }

                    if (!KEKULE)
                    {
                        //                      System.out.println("isArom:"+bi.bond.isAromatic());
                        if ((bi.bond.getBondOrder() == 2) &&
                                !BondInAromaticSystem.isAromatic(bi.bond))
                        {
                            smiles.append("=");
                        }
                    }
                    else
                    {
                        if (bi.bond.getBondOrder() == 2)
                        {
                            smiles.append("=");
                        }
                    }

                    if (bi.bond.getBondOrder() == 3)
                    {
                        smiles.append("#");
                    }
                }

                if (bi.intValue > 9)
                {
                    smiles.append("%");
                }

                smiles.append(bi.intValue);

                //                System.out.println("Ring'"+bi.i+"'");
            }
        }

        //follow path to child atoms
        Bond bond;

        for (int i = 0; i < node.size(); i++)
        {
            bond = node.getNextBond(i);

            if ((i + 1) < node.size())
            {
                smiles.append("(");
            }

            if (assignCisTrans)
            {
                if (bond.isUp())
                {
                    smiles.append(SMILESParser.UP_BOND_FLAG);
                }

                if (bond.isDown())
                {
                    smiles.append(SMILESParser.DOWN_BOND_FLAG);
                }
            }

            if (!KEKULE)
            {
                if ((bond.getBondOrder() == 2) &&
                        !BondInAromaticSystem.isAromatic(bond))
                {
                    smiles.append("=");
                }
            }
            else
            {
                if (bond.getBondOrder() == 2)
                {
                    smiles.append("=");
                }
            }

            if (bond.getBondOrder() == 3)
            {
                smiles.append("#");
            }

            toSmilesString(node.getNextNode(i), smiles);

            if ((i + 1) < node.size())
            {
                smiles.append(")");
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
