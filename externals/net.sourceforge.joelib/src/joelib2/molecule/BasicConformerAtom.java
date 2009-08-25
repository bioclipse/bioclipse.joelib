///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicConformerAtom.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.13 $
//            $Date: 2005/02/17 16:48:36 $
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

import joelib2.data.BasicAtomTyper;
import joelib2.data.BasicElementHolder;

import joelib2.feature.types.atomlabel.AtomFreeElectronsCount;
import joelib2.feature.types.atomlabel.AtomHybridisation;
import joelib2.feature.types.atomlabel.AtomPartialCharge;
import joelib2.feature.types.atomlabel.AtomType;

import joelib2.math.BasicVector3D;
import joelib2.math.Vector3D;

import joelib2.molecule.types.BasicResidue;
import joelib2.molecule.types.Residue;

import joelib2.util.iterator.BasicBondIterator;
import joelib2.util.iterator.BasicEdgeIterator;
import joelib2.util.iterator.BasicNbrAtomIterator;
import joelib2.util.iterator.BasicNbrNodeIterator;
import joelib2.util.iterator.EdgeIterator;
import joelib2.util.iterator.ListIterator;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 *  Atom representation.
 *
 * @.author     wegnerj
 * @.wikipedia Atom
 * @.license    GPL
 * @.cvsversion    $Revision: 1.13 $, $Date: 2005/02/17 16:48:36 $
 */
public class BasicConformerAtom extends AbstractConformerAtom
    implements Cloneable, java.io.Serializable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicConformerAtom.class.getName());

    /**
     *  Atom flag: is a clockwise stereo atom.
     *
     * @see    joelib2.molecule.Atom#IS_ACSTEREO
     * @see    joelib2.molecule.Atom#IS_CHIRAL
     */
    public final static int IS_CSTEREO = (1 << 1);

    /**
     *  Atom flag: is a anti-clockwise stereo atom.
     *
     * @see    joelib2.molecule.Atom#IS_CSTEREO
     * @see    joelib2.molecule.Atom#IS_CHIRAL
     */
    public final static int IS_ACSTEREO = (1 << 2);

    /**
     *  Atom flag: is a chiral atom (stereo center).
     *
     * @see    joelib2.molecule.Atom#IS_CSTEREO
     * @see    joelib2.molecule.Atom#IS_ACSTEREO
     */
    public final static int IS_CHIRAL = (1 << 3);

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Atomic number. Element number in the periodic table.
     */
    protected int atomicNumber;

    /**
     *  Index into coordinate array.
     */
    protected int coord3DIndex;

    /**
     *  x,y and z coordinate of the molecule.
     */
    protected BasicVector3D coords3D;

    /**
     *  Coordinate array.
     */
    protected double[] coords3Darr;

    /**
     *  Bonds of this atom.
     */
    protected List edges;

    /**
     *  Atom flags.
     *
     * @see    joelib2.molecule.Atom#IS_CSTEREO
     * @see    joelib2.molecule.Atom#IS_ACSTEREO
     * @see    joelib2.molecule.Atom#IS_CHIRAL
     */
    protected int flags;

    /**
     *  Formal charge of this atom.
     */
    protected int formalCharge;

    /**
     *  Description of the Field
     */
    protected int freeElectrons;

    /**
     *  Index number as loaded from molecule file.
     */
    protected int index;

    /**
     * Isotope value (0 = most abundant) for this atom.
     */
    protected int isotope;

    /**
     *  Molecule which contains this atom.
     */
    protected Graph parent;

    protected Residue residue;

    /**
     *  Description of the Field
     */
    protected String type;
    transient private int hash = 0;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the <tt>Atom</tt> object.
     */
    protected BasicConformerAtom()
    {
        coords3D = new BasicVector3D();
        edges = new Vector();
        freeElectrons = ELECTRONS_UNDEFINED;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Clone the <tt>Atom</tt> <tt>src</tt> to the <tt>Atom</tt> <tt>to
     *  </tt>. Bond info is not copied here as pointers may be invalid.
     *
     * @param  src  the source <tt>Atom</tt>
     * @param  dest   the destination <tt>Atom</tt>
     * @return      the destination <tt>Atom</tt>
     */
    public static Object clone(Atom src, Atom dest)
    {
        dest.clear();

        dest.setIndex(src.getIndex());
        dest.setAtomicNumber(src.getAtomicNumber());
        dest.setFormalCharge(src.getFormalCharge());
        dest.setIsotope(src.getIsotope());

        //to._stereo   = src.getStereo();
        dest.setType(src.getType(), false);

        // heavy weight cloning you can use to.setVector( src ); for light weight cloning
        dest.setCoords3D(src.getCoords3D().getX3D(), src.getCoords3D()
            .getY3D(), src.getCoords3D().getZ3D());
        dest.setFlags(src.getFlags());
        dest.setResidue(null);

        return (dest);
    }

    /**
     *  Adds a bond to this atom.
     *
     * @param  bond  the new bond to add
     */
    public boolean addBond(Bond bond)
    {
        boolean added = false;

        if (bond.getParent() != this.getParent())
        {
            logger.error("Bond and atom must have same parent molecule.");
        }
        else
        {
            edges.add(bond);
            added = true;
        }

        return added;
    }

    /**
     *  Returns a <tt>BondIterator</tt> for all bonds in this atom.
     *
     * <blockquote><pre>
     * BondIterator bit = atom.bondIterator();
     * Bond bond;
     * while (bit.hasNext())
     * {
     *   bond = bit.nextBond();
     *
     * }
     * </pre></blockquote>
     *
     * @return    the <tt>BondIterator</tt> for all bonds in this atom
     * @see #nbrAtomIterator()
     */
    public BasicBondIterator bondIterator()
    {
        return new BasicBondIterator(edges);
    }

    /**
     *  Deletes all contained informations in this atom.
     */
    public final void clear()
    {
        clearCoords3Darr();
        setParent(parent);
        coord3DIndex = 0;
        flags = 0;
        index = 0;
        atomicNumber = 0;
        formalCharge = 0;
        isotope = 0;

        //_stereo   = 0;
        type = "";
        edges.clear();
        setResidue(null);
    }

    /**
     *  Clear coordinate array.
     */
    public void clearCoords3Darr()
    {
        setCoords3Darr(null);
    }

    /**
     *  Clone this <tt>Atom</tt> object.
     *
     * @return    cloned atom
     */
    public Object clone()
    {
        return clone(this, new BasicConformerAtom());
    }

    /**
     *  Delete a bond from this atom.
     *
     * @param  bond2delete  the bond to delete
     * @return        <tt>true</tt> if the given bond was deleted succesfully
     */
    public boolean deleteBond(Bond bond2delete)
    {
        boolean isDeleted = false;

        if (bond2delete.getParent() != this.getParent())
        {
            logger.error(
                "Bond and atom must have same parent molecule.  Check atom object (cloned?)");
        }
        else
        {
            BasicBondIterator bit = this.bondIterator();
            Bond bond;

            while (bit.hasNext())
            {
                bond = bit.nextBond();

                if (bond == bond2delete)
                {
                    bit.remove();

                    isDeleted = true;

                    break;
                }
            }
        }

        return isDeleted;
    }

    public void deleteResidue()
    {
        if (residue != null)
        {
            setResidue(null);
        }
    }

    public EdgeIterator edgeIterator()
    {
        return new BasicEdgeIterator(edges);
    }

    /**
     * Checks if two atoms are equal.
     *
     * Compares hybridization, charge, isotope, atom type and flags,
     * AND atom index number, partial charge and position.
     *
     * @param type
     * @return
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        if (obj instanceof Atom)
        {
            isEqual = equals((Atom) obj, true);
        }

        return isEqual;
    }

    /**
     * Checks if two atoms are equal.
     *
     * Compares hybridization, charge, isotope, atom type and flags.
     * When <tt>fullComparison</tt> is set to <tt>false</tt> the parent molecule,
     * atom index number, partial charge and position are ignored.
     *
     * @param type
     * @param full When set to <tt>false</tt> the parent molecule,
     * atom index number, partial charge and position are ignored.
     * @return
     */
    public boolean equals(Atom type, boolean full)
    {
        boolean isEqual = true;

        if ((AtomHybridisation.getIntValue(this) !=
                    AtomHybridisation.getIntValue(type)) ||
                (atomicNumber != type.getAtomicNumber()) ||
                (formalCharge != type.getFormalCharge()) ||
                (this.type != type.getType()) || (flags != type.getFlags()))
        {
            isEqual = false;
        }

        if (full)
        {
            if ((parent != type.getParent()) || (index != type.getIndex()) ||
                    (AtomPartialCharge.getPartialCharge(this) !=
                        AtomPartialCharge.getPartialCharge(type)) ||
                    (getCoords3D().getX3D() != type.getCoords3D().getX3D()) ||
                    (getCoords3D().getY3D() != type.getCoords3D().getY3D()) ||
                    (getCoords3D().getZ3D() != type.getCoords3D().getZ3D()))
            {
                isEqual = false;
            }
        }

        return isEqual;
    }

    /**
     *  Gets the x coordinate of the <tt>Atom</tt> .
     *
     * @return    the x coordinate
     */
    public double get3Dx()
    {
        double position;

        if (coords3Darr != null)
        {
            position = coords3Darr[coord3DIndex];
        }
        else
        {
            position = coords3D.x3D;
        }

        return position;
    }

    /**
     *  Gets the y coordinate of the <tt>Atom</tt> .
     *
     * @return    the y coordinate
     */
    public double get3Dy()
    {
        double position;

        if (coords3Darr != null)
        {
            position = coords3Darr[coord3DIndex + 1];
        }
        else
        {
            position = coords3D.y3D;
        }

        return position;
    }

    /**
     *  Gets the z coordinate of the <tt>Atom</tt> .
     *
     * @return    the z coordinate
     */
    public double get3Dz()
    {
        double position;

        if (coords3Darr != null)
        {
            position = coords3Darr[coord3DIndex + 2];
        }
        else
        {
            position = coords3D.z3D;
        }

        return position;
    }

    /**
     * Gets the atomic number of the <tt>Atom</tt> object.
     *
     * @return    the atomic number
     */
    public final int getAtomicNumber()
    {
        return (int) atomicNumber;
    }

    /**
     *  Gets the bond attribute of the <tt>Atom</tt> object
     *
     * @param  nbr  Description of the Parameter
     * @return      The bond value
     */
    public Bond getBond(Atom nbr)
    {
        return (Bond) getEdge(nbr);
    }

    /**
     *  Returns a {@link java.util.Vector} for all bonds in this atom.
     *
     * @return    the {@link java.util.Vector} for all bonds in this atom
     */
    public final List getBonds()
    {
        return getEdges();
    }

    public final int getBondsSize()
    {
        return getEdgesSize();
    }

    /**
     *  Gets the coordinateIdx attribute of the <tt>Atom</tt> object.
     *
     * @return    The coordinateIdx value
     */
    public int getCoordinateIdx()
    {
        return ((int) coord3DIndex);
    }

    /**
     *  Gets the vector attribute of the <tt>Atom</tt> object
     *
     * @return    The vector value
     */
    public Vector3D getCoords3D()
    {
        Vector3D c3D;

        if (coords3Darr == null)
        {
            c3D = coords3D;
        }
        else
        {
            coords3D.set(coords3Darr, coord3DIndex);
            c3D = coords3D;
        }

        return c3D;
    }

    /**
     *  Gets the coordinate attribute of the <tt>Atom</tt> object
     *
     * @return    The coordinate value
     */
    public double[] getCoords3Darr()
    {
        return (coords3Darr);
    }

    public Edge getEdge(Node nbr)
    {
        EdgeIterator nit = this.edgeIterator();
        Edge edge = null;

        while (nit.hasNext())
        {
            edge = nit.nextEdge();

            if (edge.getNeighbor(this) == nbr)
            {
                break;
            }
        }

        return edge;
    }

    public List getEdges()
    {
        return edges;
    }

    public int getEdgesSize()
    {
        int size = 0;

        if (edges != null)
        {
            size = edges.size();
        }

        return size;
    }

    /**
     *  Gets the flag attribute of the Atom object
     *
     * @return    The flag value
     * @see       joelib2.molecule.Atom#IS_4RING
     * @see       joelib2.molecule.Atom#IS_3RING
     * @see       joelib2.molecule.Atom#IS_AROMATIC
     * @see       joelib2.molecule.Atom#IS_RING
     * @see       joelib2.molecule.Atom#IS_CSTEREO
     * @see       joelib2.molecule.Atom#IS_ACSTEREO
     * @see       joelib2.molecule.Atom#IS_ACCEPTOR
     * @see       joelib2.molecule.Atom#IS_CHIRAL
     */
    public final int getFlags()
    {
        return (flags);
    }

    /**
     *  Gets the formal charge of the <tt>Atom</tt> .
     *
     * @return    the formal charge of this atom
     */
    public final int getFormalCharge()
    {
        return (formalCharge);
    }

    /**
     * Set the number of free electrons.
     *
     * @param the number of free electrons or <tt>FREE_ELECTRONS_NOT_DEF</tt>
     * @see #ELECTRONS_UNDEFINED
     */
    public int getFreeElectrons()
    {
        int freeEl = 0;

        if (freeElectrons == Atom.ELECTRONS_UNDEFINED)
        {
            this.setFreeElectrons(0);
            freeEl = AtomFreeElectronsCount.getIntValue(this);
        }

        return freeEl;
    }

    /**
     *  Gets the index number of the <tt>Atom</tt> object.
     *
     * @return    the index number of this atom
     */
    public final int getIndex()
    {
        return ((int) index);
    }

    /**
     * Isotpe value for this atom.
     *
     * @return byte
     */
    public int getIsotope()
    {
        return isotope;
    }

    /**
     *  Gets the nextAtom attribute of the <tt>Atom</tt> object
     *
     * @return    The nextAtom value
     */
    public Atom getNextAtom()
    {
        Molecule mol = getParent();

        return ((getIndex() == mol.getAtomsSize())
                ? null : mol.getAtom(getIndex() + 1));
    }

    /**
     *  Gets the parent attribute of the <tt>Atom</tt> object
     *
     * @return    The parent value
     */
    public Molecule getParent()
    {
        return (Molecule) parent;
    }

    public Residue getResidue()
    {
        Residue res = null;

        if (residue != null)
        {
            res = residue;
        }
        else if (!getParent().hasChainsPerceived())
        {
            //        JOEChainsParser.instance().perceiveChains( getParent() );
            res = residue;
        }

        return res;
    }

    /**
     *  Gets the type of the <tt>Atom</tt>.
     *
     * This is the JOELib internal atom type, which can be used via the
     * look-up table in {@link joelib2.data.BasicAtomTypeConversionHolder} to export molecules to other
     * formats, like Synyl MOL2, MM2, Tinker, etc.
     *
     * @return    the type of this atom
     */
    public String getType()
    {
        if (getParent().getModificationCounter() == 0)
        {
            if ((type == null) || (type.trim().length() == 0))
            {
                type = AtomType.getAtomType(this);
            }
        }

        return type;
    }

    /**
     *  Gets the valence of the <tt>Atom</tt> object.
     *
     * @return    the atom valence
     */
    public final int getValence()
    {
        return getEdgesSize();
    }

    /**
     * Returns <tt>true</tt> if atom has an aromatic bond.
     *
     * @return  <tt>true</tt> if atom has an aromatic bond
     */
    public boolean hasAromaticBondOrder()
    {
        return (hasBondOfOrder(BondHelper.AROMATIC_BO));
    }

    /**
     * Returns <tt>true</tt> if atom has bond of given <tt>order</tt>.
     *
     * @param  order  The bond order
     * @return        <tt>true</tt> if atom has bond of given <tt>order</tt>
     */
    public boolean hasBondOfOrder(int order)
    {
        BasicBondIterator bit = this.bondIterator();
        Bond bond;
        boolean hasBond = false;

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (bond.getBondOrder() == order)
            {
                hasBond = true;

                break;
            }
        }

        return hasBond;
    }

    /**
     * Returns <tt>true</tt> if the chirality was specified for this atom.
     *
     * @return  <tt>true</tt> if the chirality was specified for this atom
     */
    public boolean hasChiralitySpecified()
    {
        return (hasFlag(IS_CSTEREO | IS_ACSTEREO));
    }

    /**
     * Returns <tt>true</tt> if atom has a double bond.
     *
     * @return  <tt>true</tt> if atom has a double bond
     */
    public boolean hasDoubleBond()
    {
        return (hasBondOfOrder(2));
    }

    /**
     *  Description of the Method
     *
     * @param  flag  Description of the Parameter
     * @return       Description of the Return Value
     * @see          joelib2.molecule.Atom#IS_4RING
     * @see          joelib2.molecule.Atom#IS_3RING
     * @see          joelib2.molecule.Atom#IS_AROMATIC
     * @see          joelib2.molecule.Atom#IS_RING
     * @see          joelib2.molecule.Atom#IS_CSTEREO
     * @see          joelib2.molecule.Atom#IS_ACSTEREO
     * @see          joelib2.molecule.Atom#IS_ACCEPTOR
     * @see          joelib2.molecule.Atom#IS_CHIRAL
     */
    public boolean hasFlag(int flag)
    {
        return (((flags & flag) != 0) ? true : false);
    }

    /**
     * Calculates the hashcode for an atom.
     *
     * Includes hybridization, charge, isotope, atom type and flags.
     * Excludes atom index number, partial charge and position.
     *
     * @param type
     * @return
     */
    public synchronized int hashCode()
    {
        if (hash == 0)
        {
            int hashCode = atomicNumber;
            hashCode = (31 * hashCode) + AtomHybridisation.getIntValue(this);

            if (type != null)
            {
                hashCode = (31 * hashCode) + type.hashCode();
            }
            else
            {
                hashCode = (31 * hashCode) + 0;
            }

            hashCode = (31 * hashCode) + flags;

            long bits;
            int tmpI;

            // similar code of Double.hashcode() !!!
            // without need to get a Double instance
            bits = Double.doubleToLongBits(formalCharge);

            // unsigned right shift operator
            tmpI = (int) (bits ^ (bits >>> 32));
            hashCode = (31 * hashCode) + tmpI;
            hash = hashCode;
        }

        return hash;
    }

    public boolean hasResidue()
    {
        return (residue != null);
    }

    /**
     * Returns <tt>true</tt> if atom has a single bond.
     *
     * @return  <tt>true</tt> if atom has a single bond
     */
    public boolean hasSingleBond()
    {
        return (hasBondOfOrder(1));
    }

    /**
     *  Insert a bond to this atom.
     *
     * @param  listIter    Description of the Parameter
     * @param  bond  Description of the Parameter
     */
    public void insertBond(ListIterator listIter, Bond bond)
    {
        if ((listIter instanceof BasicNbrAtomIterator) ||
                (listIter instanceof BasicBondIterator))
        {
            listIter.insert(bond);
        }
        else
        {
            logger.error(
                "Iterator must be of type BondIterator or NbrAtomIterator");

            //throw new IteratorException("Iterator must be of type BondIterator or NbrAtomIterator");
        }
    }

    /**
     *  Gets the antiClockwise attribute of the Atom object
     *
     * @return    The antiClockwise value
     */
    public boolean isAntiClockwise()
    {
        return (hasFlag(IS_ACSTEREO));
    }

    public boolean isChiral()
    {
        return hasFlag(IS_CHIRAL);
    }

    /**
     *  Gets the clockwise attribute of the Atom object
     *
     * @return    The clockwise value
     */
    public boolean isClockwise()
    {
        return (hasFlag(IS_CSTEREO));
    }

    /**
     *  Returns <tt>true</tt> if this atom is connected to the atom <tt>at</tt> .
     *
     * @param  atom  Description of the Parameter
     * @return     The connected value
     */
    public boolean isConnected(Atom atom)
    {
        BasicBondIterator bit = this.bondIterator();
        Bond bond;
        boolean isConnected = false;

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if ((bond.getBegin() == atom) || (bond.getEnd() == atom))
            {
                isConnected = true;

                break;
            }
        }

        return isConnected;
    }

    /**
     *  Returns a <tt>NbrAtomIterator</tt> for all neighbour atoms in this atom.
     *
     * <blockquote><pre>
     * NbrAtomIterator nait = atom.nbrAtomIterator();
     * Bond bond;
     * Atom nbrAtom;
     * while (nait.hasNext())
     * {
     *          nbrAtom=nait.nextNbrAtom();
     *   bond = nait.actualBond();
     *
     * }
     * </pre></blockquote>
     *
     * @return    the <tt>NbrAtomIterator</tt> for all neighbour atoms in this atom
     * @see #bondIterator()
     */
    public BasicNbrAtomIterator nbrAtomIterator()
    {
        return new BasicNbrAtomIterator(edges, this);
    }

    public BasicNbrNodeIterator nbrNodeIterator()
    {
        return new BasicNbrNodeIterator(edges, this);
    }

    public void newResidue()
    {
        if (residue == null)
        {
            residue = new BasicResidue();
        }
    }

    public synchronized int reHash()
    {
        hash = 0;

        return hashCode();
    }

    /**
     *  Sets the 'anti clockwise stereo' flag of the <tt>Atom</tt> .
     */
    public void setAntiClockwiseStereo()
    {
        setFlags(IS_ACSTEREO | IS_CHIRAL);
    }

    /**
     *  Sets the atomic number of the <tt>Atom</tt> object. E.g.: carbon atoms
     *  have the atomic number 6, H atoms the atomic number 1.
     *
     * @param  atomicnum  the new atomic number
     */
    public void setAtomicNumber(int atomicnum)
    {
        atomicNumber = atomicnum;
    }

    /**
     * @param bonds The bonds to set.
     */
    public void setBonds(List bonds)
    {
        this.edges = bonds;
    }

    /**
     *  Sets the chiral flag of the <tt>Atom</tt> .
     */
    public void setChiral()
    {
        setFlags(IS_CHIRAL);
    }

    /**
     *  Sets the 'clockwise stereo' flag of the <tt>Atom</tt> .
     */
    public void setClockwiseStereo()
    {
        setFlags(IS_CSTEREO | IS_CHIRAL);
    }

    /**
     *  Sets the vector of the <tt>Atom</tt> object. Copies the x,y and z
     *  values from the coordinate array into the <tt>XYZVector</tt> .
     */
    public void setCoords3D()
    {
        //    assert (_c != null) == true;
        if (coords3Darr != null)
        {
            coords3D.set(coords3Darr[coord3DIndex],
                coords3Darr[coord3DIndex + 1], coords3Darr[coord3DIndex + 2]);
        }
    }

    /**
     *  Sets the new vector values of the <tt>Atom</tt> object to the given
     *  values from <tt></tt> .
     *
     * @param  c3D  the new vector values
     */
    public void setCoords3D(Vector3D c3D)
    {
        if (coords3Darr == null)
        {
            coords3D.set(c3D);

            //            _v = v;
        }
        else
        {
            coords3Darr[coord3DIndex] = c3D.getX3D();
            coords3Darr[coord3DIndex + 1] = c3D.getY3D();
            coords3Darr[coord3DIndex + 2] = c3D.getZ3D();
        }
    }

    /**
     *  Sets the vector attribute of the <tt>Atom</tt> object
     *
     * @param  xPos  The new vector value
     * @param  yPos  The new vector value
     * @param  zPos  The new vector value
     */
    public void setCoords3D(double xPos, double yPos, double zPos)
    {
        if (coords3Darr == null)
        {
            coords3D.set(xPos, yPos, zPos);
        }
        else
        {
            coords3Darr[coord3DIndex] = xPos;
            coords3Darr[coord3DIndex + 1] = yPos;
            coords3Darr[coord3DIndex + 2] = zPos;
        }
    }

    /**
     *  Sets the coordPtr attribute of the <tt>Atom</tt> object
     *
     * @param  coordsArray  The new coordPtr value
     */
    public void setCoords3Darr(double[] coordsArray)
    {
        coord3DIndex = (getIndex() - 1) * 3;
        coords3Darr = coordsArray;
    }

    public void setEdges(List<Edge> edges)
    {
        this.edges = edges;
    }

    /**
     *  Sets the flag attribute of the Atom object
     *
     * @param  flag  The new flag value
     * @see          joelib2.molecule.Atom#IS_4RING
     * @see          joelib2.molecule.Atom#IS_3RING
     * @see          joelib2.molecule.Atom#IS_AROMATIC
     * @see          joelib2.molecule.Atom#IS_RING
     * @see          joelib2.molecule.Atom#IS_CSTEREO
     * @see          joelib2.molecule.Atom#IS_ACSTEREO
     * @see          joelib2.molecule.Atom#IS_ACCEPTOR
     * @see          joelib2.molecule.Atom#IS_CHIRAL
     */
    public void setFlags(int flag)
    {
        flags |= flag;
    }

    /**
     *  Sets the formal charge of the <tt>Atom</tt> object.
     *
     * @param  fcharge  the new formal charge of this atom
     */
    public void setFormalCharge(int fcharge)
    {
        formalCharge = fcharge;
    }

    /**
     * Set the number of free electrons.
     *
     * @param the number of free electrons or <tt>FREE_ELECTRONS_NOT_DEF</tt>
     * @see #ELECTRONS_UNDEFINED
     */
    public void setFreeElectrons(int freeEl)
    {
        freeElectrons = freeEl;
    }

    /**
     *  Sets the index number of the <tt>Atom</tt> object.
     *
     * @param  idx  the new index number
     */
    public void setIndex(int idx)
    {
        index = idx;
        coord3DIndex = (idx - 1) * 3;
    }

    /**
     * Set isotpe value for this atom.
     *
     * @return byte
     */
    public void setIsotope(int isotopeValue)
    {
        isotope = isotopeValue;
    }

    public void setParent(Graph ptr)
    {
        parent = ptr;
    }

    /**
     *  Sets the parent molecule for this <tt>Atom</tt> .
     *
     * @param  ptr  the parent molecule for this <tt>Atom</tt>
     */
    public void setParent(Molecule ptr)
    {
        parent = ptr;
    }

    public void setResidue(Residue res)
    {
        residue = res;
    }

    public void setType(String type)
    {
        setType(type, true);
    }

    /**
     *  Sets the type of the <tt>Atom</tt> object.
     *
     * @param  type  the new type
     */
    public void setType(String type, boolean validate)
    {
        if (validate)
        {
            if (!BasicAtomTyper.instance().isValidType(type))
            {
                throw new IllegalArgumentException(
                    "Invalid JOELib2 atom type '" + type + "'.");
            }
        }

        this.type = type;
        //AtomType.setAtomType(this,type);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public String toString()
    {
        return BasicElementHolder.instance().getSymbol(this.getAtomicNumber());
    }

    /**
     *  Clears the stereo flag of this atom.
     */
    public void unsetStereo()
    {
        flags &= ~(IS_ACSTEREO);
        flags &= ~(IS_CSTEREO);
        flags &= ~(IS_CHIRAL);
    }

    /**
     *  Destructor for the <tt>Atom</tt> object.
     */
    protected void finalize() throws Throwable
    {
        residue.removeAtom(this);
        super.finalize();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
