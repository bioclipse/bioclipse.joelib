///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ViewerAtom.java,v $
//  Purpose:  Molecule class for Java3D viewer.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:34 $
//            $Author: wegner $
//  Original Author: Jason Plurad (jplurad@tripos.com),
//                   Mike Brusati (brusati@tripos.com)
//                   Zhidong Xie (zxie@tripos.com)
//  Original Version: ftp.tripos.com/pub/java3d/
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
package joelib2.gui.render3D.molecule;

import joelib2.data.BasicElementHolder;

import joelib2.molecule.Atom;

import java.util.LinkedList;


/**
 * Atom class for Java3D viewer.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:34 $
 */
public class ViewerAtom
{
    //~ Instance fields ////////////////////////////////////////////////////////

    public LinkedList shapes = new LinkedList();

    /**
     * Description of the Field
     */
    protected Atom atom;

    /**
     * Flag indicating if atom label should be displayed
     */
    protected boolean display = true;

    /**
     * Flag indicating if atom is highlighted
     */
    protected boolean highlight = false;

    /**
     * Flag indicating if atom is selcted
     */
    protected boolean select = false;

    /**
     * Transformed atom coordinates
     */
    protected float tx;

    /**
     * Transformed atom coordinates
     */
    protected float ty;

    /**
     * Transformed atom coordinates
     */
    protected float tz;
    private ViewerMolecule parent;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the ViewerAtom object
     *
     * @param _atom  Description of the Parameter
     */
    public ViewerAtom(ViewerMolecule _parent, Atom _atom)
    {
        atom = _atom;
        parent = _parent;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Returns the atomic charge
     *
     * @return   The charge value
     */
    public int getCharge()
    {
        return atom.getFormalCharge();
    }

    /**
     * Compares this atom with another
     *
     * Returns String representation of Atom
     *
     * Returns id of the atom
     *
     * @return   <tt>true</tt> if atoms are equal, else <tt>false</tt>
     */
    public int getId()
    {
        return atom.getIndex() - 1;
    }

    public Atom getJOEAtom()
    {
        return atom;
    }

    /**
     * Returns the elemental name
     *
     * @return   The name value
     */
    public String getName()
    {
        return BasicElementHolder.instance().getSymbol(atom.getAtomicNumber());
    }

    public ViewerMolecule getParent()
    {
        return parent;
    }

    /**
     * Returns the transformed x coordinate of the atom
     *
     * @return   The tx value
     */
    public float getTx()
    {
        return tx;
    }

    /**
     * Returns the transformed y coordinate of the atom
     *
     * @return   The ty value
     */
    public float getTy()
    {
        return ty;
    }

    /**
     * Returns SYBYL force field atom type
     *
     * @return   The type value
     */
    public String getType()
    {
        return BasicElementHolder.instance().getSymbol(atom.getAtomicNumber());
    }

    /**
     * Returns the transformed z coordinate of the atom
     *
     * @return   The tz value
     */
    public float getTz()
    {
        return tz;
    }

    /**
     * Returns the x coordinate of the atom
     *
     * @return   The x value
     */
    public float getX()
    {
        return (float) atom.get3Dx();
    }

    /**
     * Returns the y coordinate of the atom
     *
     * @return   The y value
     */
    public float getY()
    {
        return (float) atom.get3Dy();
    }

    /**
     * Returns the z coordinate of the atom
     *
     * @return   The z value
     */
    public float getZ()
    {
        return (float) atom.get3Dz();
    }

    /**
     * Returns true if the atom is highlighted by user
     *
     * @return   The highlighted value
     */
    public boolean isHighlighted()
    {
        return highlight;
    }

    /**
     * Gets the colorFromType attribute of the ViewerAtom object
     *
     * @return   The colorFromType value
     */

    //    public Color getColorFromType()
    //    {
    //        int atomNum = JOEElementTable.instance().getAtomicNum(this.getName());
    //
    //        Color color =this.parent.getAtomPropertyColoring().getAtomColor(atom);
    //
    //        //Color color = JOEElementTable.instance().getColor(atomNum);
    //
    //        return color;
    //    }

    /**
     * Returns true if the atom is selected by user
     *
     * @return   The selected value
     */
    public boolean isSelected()
    {
        return select;
    }

    /**
     * Return true if the atom need to be display
     *
     * @return   Description of the Return Value
     */
    public boolean needDisplay()
    {
        return display;
    }

    /**
     * Set the state to indicate whether the atom is to be formally displayed
     *
     * @param displayOrNot  highlight state of the atom
     */
    public void setDisplay(boolean displayOrNot)
    {
        display = displayOrNot;
    }

    /**
     * Set the state to indicate whether the atom is hightlighted
     *
     * @param highlightOrNot  highlight state of the atom
     */
    public void setHighlight(boolean highlightOrNot)
    {
        highlight = highlightOrNot;
    }

    /**
     * Set the state to indicate whether the atom is selected
     *
     * @param selectOrNot  select state of the atom
     */
    public void setSelect(boolean selectOrNot)
    {
        select = selectOrNot;
    }

    /**
     * Set the transformed x coordinate of the atom
     *
     * @param tx  transformed X-coordinate of the atom
     */
    public void setTx(float tx)
    {
        this.tx = tx;
    }

    /**
     * Set the transformed y coordinate of the atom
     *
     * @param ty  transformed Y-coordinate of the atom
     */
    public void setTy(float ty)
    {
        this.ty = ty;
    }

    /**
     * Set the transformed z coordinate of the atom
     *
     * @param tz  transformed Z-coordinate of the atom
     */
    public void setTz(float tz)
    {
        this.tz = tz;
    }

    /**
     * Set the atom id
     *
     * Set the x coordinate of the atom
     *
     * @param x   The new x value
     */
    public void setX(float x)
    {
        atom.setCoords3D((double) x, atom.get3Dy(), atom.get3Dz());
    }

    /**
     * Set the y coordinate of the atom
     *
     * @param y  Y-coordinate of the atom
     */
    public void setY(float y)
    {
        atom.setCoords3D(atom.get3Dx(), (double) y, atom.get3Dz());
    }

    /**
     * Set the z coordinate of the atom
     *
     * @param z  Z-coordinate of the atom
     */
    public void setZ(float z)
    {
        atom.setCoords3D(atom.get3Dx(), atom.get3Dy(), (double) z);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
