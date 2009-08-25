///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicRing.java,v $
//  Purpose:  Ring representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
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

import joelib2.feature.types.atomlabel.AtomInAromaticSystem;
import joelib2.feature.types.atomlabel.AtomIsHeteroatom;

import joelib2.math.BasicVector3D;
import joelib2.math.Vector3D;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.util.BasicBitVector;

import java.util.List;

import org.apache.log4j.Category;


/**
 * Ring representation.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2006/02/22 02:18:22 $
 */
public class BasicRing implements java.io.Serializable, Ring
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Category logger = Category.getInstance(BasicRing.class
            .getName());
    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    private BasicBitVector atomBits = new BasicBitVector();
    private int[] atomIndices;
    private Molecule parent;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOERing object
     */
    public BasicRing()
    {
    }

    /**
     * @param  path  {@link java.util.Vector} of <tt>int[1]</tt>
     * @param  size  Description of the Parameter
     */
    public BasicRing(List path, Molecule mol)
    {
        setAtomIndices(path);
        setParent(mol);
    }

    /**
     * @param  path  {@link java.util.Vector} of <tt>int[1]</tt>
     * @param  size  Description of the Parameter
     */
    public BasicRing(int[] ringAtoms, Molecule mol)
    {
        setAtomIndices(ringAtoms);
        setParent(mol);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object otherObj)
    {
        boolean isEqual = false;

        if (otherObj instanceof Ring)
        {
            Ring other = (Ring) otherObj;

            if ((other.getAtomBits().equals(this.getAtomBits())))
            {
                isEqual = true;
            }
        }

        return isEqual;
    }

    /**
     *  Description of the Method
     *
     * @param  center  Description of the Parameter
     * @param  norm1   Description of the Parameter
     * @param  norm2   Description of the Parameter
     * @return         Description of the Return Value
     */
    public boolean findCenterAndNormal(Vector3D center, Vector3D norm1,
        Vector3D norm2)
    {
        Molecule mol = this.parent;
        int index = 0;
        final int psize = atomIndices.length;
        Vector3D tmp = new BasicVector3D();
        center.set(0.0f, 0.0f, 0.0f);
        norm1.set(0.0f, 0.0f, 0.0f);
        norm2.set(0.0f, 0.0f, 0.0f);

        for (index = 0; index < psize; index++)
        {
            center.adding((mol.getAtom(atomIndices[index])).getCoords3D());
        }

        center.diving((double) psize);

        BasicVector3D vec1 = new BasicVector3D();
        BasicVector3D vec2 = new BasicVector3D();

        for (index = 0; index < psize; index++)
        {
            vec1.set((mol.getAtom(atomIndices[index])).getCoords3D().sub(
                    center));

            if ((index + 1) == psize)
            {
                vec2.set((mol.getAtom(atomIndices[0])).getCoords3D().sub(
                        center));
            }
            else
            {
                vec2.set((mol.getAtom(atomIndices[index + 1])).getCoords3D()
                    .sub(center));
            }

            BasicVector3D.cross(tmp, vec1, vec2);
            norm1.adding(tmp);
        }

        norm1.diving(psize);
        norm1.normalize();
        norm1.setTo(norm2);
        norm2.muling(-1.0f);

        return true;
    }

    /**
     * @return Returns the atomBits.
     */
    public BasicBitVector getAtomBits()
    {
        return atomBits;
    }

    /**
     * Returns all atom indexes of atoms which are contained in this ring.
     *
     * @return    atom indexes of atoms which are contained in this ring
     */
    public int[] getAtomIndices()
    {
        return atomIndices;
    }

    public int[] getBonds()
    {
        if (parent == null)
        {
            //logger.error("No parent molecule available.");
//          this is serious and a developer problem
        	throw new RuntimeException("No parent molecule available.");
            //return null;
        }

        Bond bond;
        int numBonds = 0;

        for (int i = 0; i < parent.getBondsSize(); i++)
        {
            bond = parent.getBond(i);

            if ((atomBits.bitIsOn(bond.getBeginIndex())) &&
                    (atomBits.bitIsOn(bond.getEndIndex())))
            {
                numBonds++;
            }
        }

        int[] bonds = new int[numBonds];
        int index = 0;

        if (numBonds != 0)
        {
            for (int i = 0; i < parent.getBondsSize(); i++)
            {
                bond = parent.getBond(i);

                if ((atomBits.bitIsOn(bond.getBeginIndex())) &&
                        (atomBits.bitIsOn(bond.getEndIndex())))
                {
                    bonds[index] = bond.getIndex();
                    index++;
                }
            }
        }

        return bonds;
    }

    /**
     * Returns the parent molecule for this ring.
     *
     * @return    the parent molecule for this ring
     */
    public Molecule getParent()
    {
        return parent;
    }

    public int hashCode()
    {
        return this.getAtomBits().hashCode();
    }

    /**
    * Returns <tt>true</tt> if this ring is a aromatic ring.
    *
    * @return    <tt>true</tt> if this ring is a aromatic ring
    */
    public boolean isAromatic()
    {
        if (parent == null)
        {
            //logger.error("No parent molecule available.");
        	// this is serious and a developer problem
        	throw new RuntimeException("No parent molecule available.");
            //return false;
        }

        Molecule mol = parent;
        int[] itmp;
        boolean isAromatic = true;

        for (int i = 0; i < atomIndices.length; i++)
        {
            if (!AtomInAromaticSystem.isValue(mol.getAtom(atomIndices[i])))
            {
                isAromatic = false;

                break;
            }
        }

        return isAromatic;
    }

    /**
     * Returns <tt>true</tt> if this ring is a heterocycle.
     *
     * @return <tt>true</tt> if this ring is a heterocycle
     */
    public boolean isHetero()
    {
        if (parent == null)
        {
            //logger.error("No parent molecule available.");
//          this is serious and a developer problem
        	throw new RuntimeException("No parent molecule available.");
            //return false;
        }

        Molecule mol = parent;
        boolean isHetero = false;

        //System.out.println("Ring: molecule " + mol);
        //System.out.println("Ring: atomIndices " + atomIndices);

        for (int i = 0; i < atomIndices.length; i++)
        {
            if (AtomIsHeteroatom.isHeteroatom(mol.getAtom(atomIndices[i])))
            {
                isHetero = true;

                break;
            }
        }

        return isHetero;
    }

    /**
     * Returns <tt>true</tt> if the atom with index <tt>i</tt> is contained in this ring.
     *
     * @param  atomIndex  the index number of the atom
     * @return    <tt>true</tt> if the atom with index <tt>i</tt> is contained in this ring.
     */
    public boolean isInRing(int atomIndex)
    {
        return getAtomBits().bitIsOn(atomIndex);
    }

    /**
     * Returns <tt>true</tt> if the atom is contained in this ring.
     *
     * @param  atom  the atom
     * @return    <tt>true</tt> if the atom is contained in this ring.
     */
    public boolean isMember(Atom atom)
    {
        return atomBits.bitIsOn(atom.getIndex());
    }

    /**
     * Returns <tt>true</tt> if the bond is contained in this ring.
     *
     * @param  bond  the bond to check
     * @return    <tt>true</tt> if the bond is contained in this ring
     */
    public boolean isMember(Bond bond)
    {
        return (atomBits.bitIsOn(bond.getBeginIndex())) &&
            (atomBits.bitIsOn(bond.getEndIndex()));
    }

    public void setAtomIndices(int[] path)
    {
        atomIndices = new int[path.length];
        System.arraycopy(path, 0, atomIndices, 0, path.length);
        setAtomBits();
    }

    public void setAtomIndices(List<Integer> path)
    {
        atomIndices = new int[path.size()];

        for (int index = 0; index < path.size(); index++)
        {
            atomIndices[index] = path.get(index).intValue();
        }

        setAtomBits();
    }

    /**
     *  Sets the parent attribute of the JOERing object
     *
     * @param  vec1  The new parent value
     */
    public void setParent(Molecule vec1)
    {
        parent = vec1;
    }

    /**
     * Returns the size of the ring.
     *
     * @return   the size of this ring
     */
    public final int size()
    {
        return atomIndices.length;
    }

    public String toString()
    {
        StringBuffer sbuffer = new StringBuffer();
        int[] tmp = getAtomIndices();
        sbuffer.append("<");

        for (int i = 0; i < tmp.length; i++)
        {
            sbuffer.append(tmp[i]);

            if (i < (tmp.length - 1))
            {
                sbuffer.append(",");
            }
        }

        sbuffer.append(">");

        return sbuffer.toString();
    }

    private void setAtomBits()
    {
        atomBits.fromIntArray(this.atomIndices);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
