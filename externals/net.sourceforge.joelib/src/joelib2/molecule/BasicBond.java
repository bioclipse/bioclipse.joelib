///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicBond.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2007/03/03 00:03:49 $
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
package joelib2.molecule;

import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;

import joelib2.feature.result.AtomDynamicResult;
import joelib2.feature.result.BondDynamicResult;

import joelib2.feature.types.atomlabel.AtomInAromaticSystem;
import joelib2.feature.types.bondlabel.BondInAromaticSystem;
import joelib2.feature.types.bondlabel.BondKekuleType;

import org.apache.log4j.Category;


/**
 *  Bond representation.
 *
 * @.author     wegnerj
 * @.wikipedia Chemical bond
 * @.license    GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2007/03/03 00:03:49 $
 */
public class BasicBond extends AbstractBond implements Cloneable,
    java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(BasicBond.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * Begin atom
     */
    protected Node begin;

    /**
     * Bond order
     */
    protected char bondOrder;

    /**
     * End atom
     */
    protected Node end;

    /**
     * Bond flags
     */
    protected int flags;

    /**
     * Bond index
     */
    protected int index;

    /**
     * Parent molecule
     */
    protected Graph parent;
    transient private int hash = 0;

    //~ Constructors ///////////////////////////////////////////////////////////

    protected BasicBond()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Clone the <tt>Bond</tt> <tt>src</tt> to the <tt>Bond</tt> <tt>to
     *  </tt>.
     *
     * @param  fromBond  the source <tt>Bond</tt>
     * @param  toBond   the destination <tt>Bond</tt>
     * @return      the destination <tt>Bond</tt>
     */
    public static Object clone(Bond fromBond, Bond toBond)
    {
        toBond.setIndex(fromBond.getIndex());
        toBond.setBondOrder(fromBond.getBondOrder());
        toBond.setFlags(fromBond.getFlags());
        toBond.setBegin(fromBond.getBegin());
        toBond.setEnd(fromBond.getEnd());

        return toBond;
    }

    public void clear()
    {
        index = 0;
        bondOrder = 0;
        flags = 0;
        this.setBegin(null);
        this.setEnd(null);
    }

    public Object clone()
    {
        return clone(this, new BasicBond());
    }

    /**
     * Checks if two bonds are equal.
     *
     * Compares order, flags and equality of begin and end atom.
     * AND the bond index number AND the full equality check of
     * the begin and end atom.
     *
     * @param type
     * @return
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if (obj instanceof Bond)
        {
            isEqual = equals((Bond) obj, true);
        }
        else
        {
            isEqual = false;
        }

        return isEqual;
    }

    /**
     * Checks if two bonds are equal.
     *
     * Compares order, flags and equality of begin and end atom.
     * When <tt>fullComparison</tt> is set to <tt>false</tt> the bond index number
     * is ignored. The atom are compared also using the <tt>fullComparison</tt> flag.
     *
     * @param type
     * @return
     */
    public boolean equals(Bond type, boolean compareAll)
    {
        boolean equal = true;

        if (compareAll)
        {
            if ((getParent() != type.getParent()) || (index != type.getIndex()))
            {
                equal = false;
            }
        }
        else
        {
            if ((bondOrder != type.getBondOrder()) ||
                    (flags != type.getFlags()) ||
                    !getEnd().equals(type.getEnd(), compareAll) ||
                    !getBegin().equals(type.getBegin(), compareAll))
            {
                equal = false;
            }
        }

        return equal;
    }

    /**
     *  Gets the beginAtom attribute of the <tt>Bond</tt> object
     *
     * @return    The beginAtom value
     */
    public Atom getBegin()
    {
        return (Atom) begin;
    }

    /**
     *  Gets the beginAtomIdx attribute of the <tt>Bond</tt> object
     *
     * @return    The beginAtomIdx value
     */
    public int getBeginIndex()
    {
        return begin.getIndex();
    }

    /**
     * Gets the bond order for this bond.
     *
     * Please remember that you can check the aromaticity also via the
     * aromaticity flag, which is the better way.
     *
     * The Bond.JOE_AROMATIC_BOND_ORDER which can be assigned to the bond
     * order is only needed for some awkward import/export methods.
     *
     * Please remember that the aromaticity typer JOEAromaticTyper.assignAromaticFlags(Molecule)
     * assign ONLY aromaticity flags and NOT the internal aromatic bond order Bond.JOE_AROMATIC_BOND_ORDER.
     *
     * @return    The bond order
     */
    public int getBondOrder()
    {
        return ((int) bondOrder);
    }

    /**
     *  Gets the endAtom attribute of the <tt>Bond</tt> object
     *
     * @return    The endAtom value
     */
    public Atom getEnd()
    {
        return (Atom) end;
    }

    /**
     *  Gets the endAtomIdx attribute of the <tt>Bond</tt> object
     *
     * @return    The endAtomIdx value
     */
    public int getEndIndex()
    {
        return (end.getIndex());
    }

    /**
     *  Gets the flags attribute of the <tt>Bond</tt> object
     *
     * @return    The flags value
     */
    public int getFlags()
    {
        return (flags);
    }

    /**
     *  Gets the idx attribute of the <tt>Bond</tt> object
     *
     * @return    The idx value
     */
    public int getIndex()
    {
        return (index);
    }

    public Node getNeighbor(Node ptr)
    {
        Node nbr = null;

        if (ptr.getParent() != this.getParent())
        {
            logger.error(
                "Nodes must have same parent graph. Check node object (cloned?)");
        }
        else
        {
            nbr = ((ptr != begin) ? begin : end);
        }

        return nbr;
    }

    /**
     *  Gets the nbrAtom attribute of the <tt>Bond</tt> object
     *
     * @param  ptr  Description of the Parameter
     * @return      The nbrAtom value
     */
    public Atom getNeighbor(Atom ptr)
    {
        return (Atom) this.getNeighbor((Node) ptr);
    }

    public int getNeighborIndex(Node ptr)
    {
        int nbrIdx = -1;

        if (ptr.getParent() != this.getParent())
        {
            logger.error(
                "Nodes must have same parent graph. Check node object (cloned?)");
        }
        else
        {
            nbrIdx = ((ptr != begin) ? begin.getIndex() : end.getIndex());
        }

        return nbrIdx;
    }

    /**
     *  Gets the nbrAtomIdx attribute of the <tt>Bond</tt> object
     *
     * @param  ptr  Description of the Parameter
     * @return      The nbrAtomIdx value
     */
    public int getNeighborIndex(Atom ptr)
    {
        return this.getNeighborIndex((Node) ptr);
    }

    /**
     *  Gets the parent attribute of the <tt>Bond</tt> object
     *
     * @return    The parent value
     */
    public Molecule getParent()
    {
        return (Molecule) parent;
    }

    /**
     *  Description of the Method
     *
     * @param  flag  Description of the Parameter
     * @return       Description of the Return Value
     */
    public boolean hasFlag(int flag)
    {
        return ((flags & flag) != 0);
    }

    /**
     * Calculates the hashcode for a bond.
     *
     * Includes order, flags and equality of begin and end atom.
     * Excludes the bond index number. The begin and end atom are included
     * using their hashcode methods.
     *
     * @param type
     * @return
     */
    public synchronized int hashCode()
    {
        if (hash == 0)
        {
            int hashCode = bondOrder;
            hashCode = (31 * hashCode) + flags;
            hashCode = (31 * hashCode) + getEnd().hashCode();
            hashCode = (31 * hashCode) + getBegin().hashCode();
            hash = hashCode;
        }

        return hash;
    }

    public boolean isBondOrderAromatic()
    {
        return (this.getBondOrder() == BondHelper.AROMATIC_BO);
    }

    /**
     *  Gets the double attribute of the <tt>Bond</tt> object
     *
     * @return    The double value
     */
    public boolean isDouble()
    {
        boolean isDouble = false;

        if (BondInAromaticSystem.isAromatic(this))
        {
            isDouble = false;
        }
        else
        {
            if ((this.getBondOrder() == 2) &&
                    !BondInAromaticSystem.isAromatic(this))
            {
                isDouble = true;
            }
        }

        return isDouble;
    }

    /**
     *  Gets the down attribute of the <tt>Bond</tt> object
     *
     * @return    The down value
     */
    public boolean isDown()
    {
        return (hasFlag(BondHelper.IS_TORDOWN));
    }

    /**
     *  Gets the hash attribute of the <tt>Bond</tt> object
     *
     * @return    The hash value
     */
    public boolean isHash()
    {
        return (hasFlag(BondHelper.IS_HASH));
    }

    /**
     *  Gets the single attribute of the <tt>Bond</tt> object
     *
     * @return    The single value
     */
    public boolean isSingle()
    {
        boolean isSingle = false;

        if (BondInAromaticSystem.isAromatic(this))
        {
            isSingle = false;
        }
        else
        {
            if ((this.getBondOrder() == 1) &&
                    !BondInAromaticSystem.isAromatic(this))
            {
                isSingle = true;
            }
        }

        return isSingle;
    }

    public boolean isTriple()
    {
        boolean isTriple = false;

        if ((this.getBondOrder() == 3))
        {
            isTriple = true;
        }

        return isTriple;
    }

    /**
     *  Gets the up attribute of the <tt>Bond</tt> object
     *
     * @return    The up value
     */
    public boolean isUp()
    {
        return (hasFlag(BondHelper.IS_TORUP));
    }

    /**
     *  Gets the wedge attribute of the <tt>Bond</tt> object
     *
     * @return    The wedge value
     */
    public boolean isWedge()
    {
        return (hasFlag(BondHelper.IS_WEDGE));
    }

    public synchronized int reHash()
    {
        hash = 0;

        return hashCode();
    }

    /**
     *  Description of the Method
     *
     * @param  idx    Description of the Parameter
     * @param  begin  Description of the Parameter
     * @param  end    Description of the Parameter
     * @param  order  Description of the Parameter
     * @param  flags  Description of the Parameter
     */
    public void set(int idx, Atom begin, Atom end, int order, int flags)
    {
        setIndex(idx);
        setBegin(begin);
        setEnd(end);
        setBondOrder(order);
        setFlags(flags);
    }

    public void setBegin(Node begin)
    {
        if (begin.getParent() != this.getParent())
        {
            logger.error(
                "Node and edge must have same parent graph. Check node object (cloned?)");
        }
        else
        {
            this.begin = begin;
        }
    }

    /**
     *  Sets the begin attribute of the <tt>Bond</tt> object
     *
     * @param  begin  The new begin value
     */
    public void setBegin(Atom begin)
    {
        if (begin.getParent() != this.getParent())
        {
            logger.error(
                "Atom and bond must have same parent molecule. Check atom object (cloned?)");
        }
        else
        {
            this.begin = begin;
        }
    }

    public void setBondOrder(int order)
    {
        setBondOrder(order, false);
    }

    /**
         *  Sets the bond order for this bond.
         *
         * This causes no change in the aromaticity flag for this bond.
         *
         * Please remember that the aromaticity typer JOEAromaticTyper.assignAromaticFlags(Molecule)
         * assign ONLY aromaticity flags and NOT the internal aromatic bond order Bond.JOE_AROMATIC_BOND_ORDER.
         *
         * @param  order  the new bond order
         */
    public void setBondOrder(int order, boolean overwriteAromaticity)
    {
        this.bondOrder = (char) order;

        if ((getParent().getModificationCounter() == 0) && overwriteAromaticity)
        {
            if (order == BondHelper.AROMATIC_BO)
            {
                AtomDynamicResult aromAtoms;
                BondDynamicResult aromBonds;

                try
                {
                    aromAtoms = (AtomDynamicResult) FeatureHelper.instance()
                                                                 .featureFrom(
                            getParent(), AtomInAromaticSystem.getName());
                    aromBonds = (BondDynamicResult) FeatureHelper.instance()
                                                                 .featureFrom(
                            getParent(), BondInAromaticSystem.getName());
                }
                catch (FeatureException e)
                {
                    logger.error(e.getMessage());
                }

                BondInAromaticSystem.setAromatic(this, true);
                AtomInAromaticSystem.setValue(getBegin(), true);
                AtomInAromaticSystem.setValue(getEnd(), true);
            }
            else
            {
                BondInAromaticSystem.setAromatic(this, false);
            }
        }
    }

    public void setBondOrderAromatic()
    {
        setBondOrder(BondHelper.AROMATIC_BO);
    }

    /**
     *  Sets the down attribute of the <tt>Bond</tt> object
     */
    public void setDown()
    {
        setFlags(BondHelper.IS_TORDOWN);
    }

    public void setEnd(Node end)
    {
        if (end.getParent() != this.getParent())
        {
            logger.error(
                "Node and edge must have same parent graph. Check node object (cloned?)");
        }
        else
        {
            this.end = end;
        }
    }

    /**
     *  Sets the end attribute of the <tt>Bond</tt> object
     *
     * @param  end  The new end value
     */
    public void setEnd(Atom end)
    {
        if (end.getParent() != this.getParent())
        {
            logger.error(
                "Atom and bond must have same parent molecule. Check atom object (cloned?)");
        }
        else
        {
            this.end = end;
        }
    }

    /**
     *  Sets the flag attribute of the <tt>Bond</tt> object
     *
     * @param  flag  The new flag value
     */
    public void setFlags(int flag)
    {
        flags |= flag;
        //System.out.println(this.getParent().getTitle()+" down="+this.isDown()+" up="+this.isUp());
    }

    /**
     *  Sets the idx attribute of the <tt>Bond</tt> object
     *
     * @param  idx  The new idx value
     */
    public void setIndex(int idx)
    {
        index = idx;
    }

    public void setParent(Graph ptr)
    {
        parent = ptr;
    }

    /**
     *  Sets the parent attribute of the <tt>Bond</tt> object
     *
     * @param  ptr  The new parent value
     */
    public void setParent(Molecule ptr)
    {
        parent = ptr;
    }

    /**
     *  The JUnit setup method
     */
    public void setUp()
    {
        setFlags(BondHelper.IS_TORUP);
    }

    public String toString()
    {
        // default bond is single bond
        String bond = "-";

        if ((BondKekuleType.getKekuleType(this) ==
                    KekuleHelper.KEKULE_DOUBLE) ||
                BondInAromaticSystem.isAromatic(this))
        {
            bond = "=";
        }
        else if (BondKekuleType.getKekuleType(this) ==
                KekuleHelper.KEKULE_TRIPLE)
        {
            bond = "#";
        }

        return bond;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
