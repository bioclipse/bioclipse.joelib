///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ViewerMolecule.java,v $
//  Purpose:  Molecule class for Java3D viewer.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
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

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicAtomPropertyColoring;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import java.util.Hashtable;
import java.util.Map;


/**
 * Molecule class for Java3D viewer.
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:34 $
 */
public class ViewerMolecule
{
    //~ Instance fields ////////////////////////////////////////////////////////

    public Hashtable pickAtomMapping = new Hashtable();
    public Hashtable pickBondMapping = new Hashtable();

    /**
     * Atom id to index map.  Does not require atom ids to start at 1 or be
     *sequential.
     */
    protected Map<Integer, Integer> atomIdToIndex;

    /**
     * List of highlighted atom
     */
    protected ViewerAtoms highlightAtoms = new ViewerAtoms(10);

    /**
     * Molecule id
     */
    protected int id;

    /**
     * The transformation matrix
     */
    protected Matrix3D mat;
    protected Molecule mol;

    /**
     * Atom list
     */
    protected ViewerAtoms myAtoms;

    /**
     * Bond list
     */
    protected ViewerBonds myBonds;

    /**
     * Number of atoms
     *
     */
    protected int numAtoms;

    /**
     * Number of bonds
     */
    protected int numBonds;

    /**
     * List of selected atoms
     */
    protected ViewerAtoms selectAtoms = new ViewerAtoms(10);

    /**
     * The bounding extents of the molecule
     */
    protected float xmax;

    /**
     * The bounding extents of the molecule
     */
    protected float xmin;

    /**
     * The bounding extents of the molecule
     */
    protected float ymax;

    /**
     * The bounding extents of the molecule
     */
    protected float ymin;

    /**
     * The bounding extents of the molecule
     */
    protected float zmax;

    /**
     * The bounding extents of the molecule
     */
    protected float zmin;
    private BasicAtomPropertyColoring aPropColoring =
        new BasicAtomPropertyColoring();

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Default renderer so that pre-existing programs can
     *  still call Molecule.draw( Graphics )
     *
     * @param id    Description of the Parameter
     * @param name  Description of the Parameter
     */

    //  protected static Renderer  rendererDefault = new Renderer();

    /**
     * Default constructor
     */
    public ViewerMolecule(Molecule molecule)
    {
        mol = molecule;

        //aPropColoring.useAtomPropertyColoring(mol,"Gasteiger_Marsili");
        // initialize internal variables
        myAtoms = new ViewerAtoms(mol.getAtomsSize(), 30);
        atomIdToIndex = new Hashtable(mol.getAtomsSize());
        myBonds = new ViewerBonds(mol.getBondsSize(), 30);
        numAtoms = numBonds = 0;
        mat = new Matrix3D();
        xmax = xmin = ymax = ymin = zmax = zmin = 0.0f;

        // adding viewer atoms and bonds
        ViewerAtom viewerAtom;
        Atom atom;
        AtomIterator ait = mol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();
            viewerAtom = new ViewerAtom(this, atom);
            this.addAtom(viewerAtom);
        }

        BondIterator bit = mol.bondIterator();
        Bond bond;

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            int from = bond.getBeginIndex();
            int to = bond.getEndIndex();

            ViewerAtom a1 = myAtoms.getAtom(from - 1);
            ViewerAtom a2 = myAtoms.getAtom(to - 1);
            ViewerBond b = new ViewerBond(this, bond, a1, a2);
            this.addBond(b);
        }

        //        this(0, "");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Add an atom to this molecule
     *
     * @param a  atom to be added
     */
    public void addAtom(ViewerAtom a)
    {
        myAtoms.append(a);
        atomIdToIndex.put(new Integer(a.getId()), new Integer(numAtoms));
        numAtoms++;
    }

    /**
     * Add a bond to this molecule
     *
     * @param b  bond to be added
     */
    public void addBond(ViewerBond b)
    {
        myBonds.append(b);
        numBonds++;
    }

    /**
     * Calculates 2D vector perpendicular to bond
     *
     * @param x1   Description of the Parameter
     * @param y1   Description of the Parameter
     * @param x2   Description of the Parameter
     * @param y2   Description of the Parameter
     * @param pex  Description of the Parameter
     * @param pey  Description of the Parameter
     */
    public void calcPerpUnitVec(float x1, float y1, float x2, float y2,
        float[] pex, float[] pey)
    {
        float len = (float) java.lang.Math.sqrt(((x2 - x1) * (x2 - x1)) +
                ((y2 - y1) * (y2 - y1)));

        //
        // Unit vector of the bond
        //
        float ex = (x2 - x1) / len;
        float ey = (y2 - y1) / len;

        //
        // Unit vector perpendicular to bond
        //
        pex[0] = -1.0f * ey;
        pey[0] = ex;
    }

    public void clear()
    {
        myAtoms.clear();
        myBonds.clear();
        atomIdToIndex.clear();
        pickBondMapping.clear();
        pickAtomMapping.clear();
    }

    /**
     * Return the Renderer which draws this molecule
     *
     * @param a  Description of the Parameter
     * @return   Description of the Return Value
     */

    //  protected static Renderer getRenderer()
    //  {
    //  System.out.println("return renderer:");
    //   return rendererDefault; }

    /**
     * Return the Renderer which draws this molecule
     *
     * Return true iff the atom in the parameter is one of the atoms in this molecule
     *
     * @param a  Description of the Parameter
     * @return   Description of the Return Value
     */
    public boolean contains(ViewerAtom a)
    {
        return myAtoms.contains(a);
    }

    /**
     * Return true iff the bond in the parameter is one of the bonds in this molecule
     *
     * @param b  bond in this query
     * @return   Description of the Return Value
     */
    public boolean contains(ViewerBond b)
    {
        return myBonds.contains(b);
    }

    /**
     * Dehighlighted all highlighted atoms.
     */
    public void dehighlight()
    {
        if (highlightAtoms != null)
        {
            for (int i = 0; i < highlightAtoms.size(); i++)
            {
                ViewerAtom a = highlightAtoms.getAtom(i);
                a.highlight = false;
            }

            highlightAtoms = new ViewerAtoms(10);
        }
    }

    /**
     * Deselects all <tt>selected</tt> atoms
     *
     */
    public void deselect()
    {
        if (selectAtoms != null)
        {
            for (int i = 0; i < selectAtoms.size(); i++)
            {
                ViewerAtom a = selectAtoms.getAtom(i);
                a.select = false;
            }
        }

        selectAtoms = new ViewerAtoms(10);
    }

    /**
     * Compares this molecule with another
     *
     * @param jmol  molecule to compare with
     * @return      <tt>true</tt> if they are equal, else <tt>false</tt>
     */
    public boolean equals(Object obj)
    {
        if ((obj instanceof ViewerMolecule) && (obj != null))
        {
            if (((ViewerMolecule) obj).mol.equals(mol))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns closest atom to point within FINDRADIUS, <tt>null</tt> if nothing is found,
     *
     * @param x3D           Description of the Parameter
     * @param y3D           Description of the Parameter
     * @param z3D           Description of the Parameter
     * @param findRadius  Description of the Parameter
     * @return            Description of the Return Value
     */
    public ViewerAtom findAtom(float x3D, float y3D, float z3D,
        float findRadius)
    {
        int result = -1;
        float minDist = 1000000000.0f;

        for (int i = 0; i < numAtoms; i++)
        {
            ViewerAtom a = myAtoms.getAtom(i);
            float dist = ((a.tx - x3D) * (a.tx - x3D)) +
                ((a.ty - y3D) * (a.ty - y3D));

            //+ (a.tz-z)*(a.tz-z);
            if ((dist < findRadius) && (dist < minDist))
            {
                minDist = dist;
                result = i;
            }
        }

        if (result == -1)
        {
            return null;
        }
        else
        {
            return myAtoms.getAtom(result);
        }
    }

    /**
     * Determines bounding box of a molecule (sets xmax,xmin,ymax,ymin,zmax,zmin)
     */
    public void findBB()
    {
        if (numAtoms == 0)
        {
            xmin = xmax = ymin = ymax = zmin = zmax = 0;

            return;
        }

        ViewerAtom a = myAtoms.getAtom(0);

        xmin = xmax = a.getX();
        ymin = ymax = a.getY();
        zmin = zmax = a.getZ();

        for (int i = 1; i < numAtoms; i++)
        {
            a = myAtoms.getAtom(i);

            if (a.getX() < xmin)
            {
                xmin = a.getX();
            }

            if (a.getX() > xmax)
            {
                xmax = a.getX();
            }

            if (a.getY() < ymin)
            {
                ymin = a.getY();
            }

            if (a.getY() > ymax)
            {
                ymax = a.getY();
            }

            if (a.getZ() < zmin)
            {
                zmin = a.getZ();
            }

            if (a.getZ() > zmax)
            {
                zmax = a.getZ();
            }
        }
    }

    /**
     * Return an atom in this molecule whose id matches the input id
     *
     * @param atomId  id of queried atom
     * @return        The atomFromId value
     */
    public ViewerAtom getAtomFromId(int atomId)
    {
        ViewerAtom a;

        for (int i = 0; i < myAtoms.size(); i++)
        {
            a = myAtoms.getAtom(i);

            if (atomId == a.getId())
            {
                return a;
            }
        }

        return null;
    }

    /**
     * Return atomIdToIndex
     *
     * @return   The atomIdToIndex value
     */
    public Map<Integer, Integer> getAtomIdToIndex()
    {
        return atomIdToIndex;
    }

    public BasicAtomPropertyColoring getAtomPropertyColoring()
    {
        return aPropColoring;
    }

    /**
     * Return a bond in this molecule whose id matches the input id
     *
     * @param bondId  id of queried bond
     * @return        The bondFromId value
     */
    public ViewerBond getBondFromId(int bondId)
    {
        ViewerBond b;

        for (int i = 0; i < myBonds.size(); i++)
        {
            b = myBonds.getBond(i);

            if (bondId == b.getId())
            {
                return b;
            }
        }

        return null;
    }

    /**
     * Return the highlighted atoms vector
     *
     * @return   The highlightAtoms value
     */
    public ViewerAtoms getHighlightAtoms()
    {
        return highlightAtoms;
    }

    /**
     * Draw the molecule
     * recommend to avoid, but to use renderer approach
     *
     * @return   The id value
     */

    //  public void draw( Graphics g ) {
    //    rendererDefault.setMol( this );
    //    rendererDefault.draw( g );
    //  }

    /**
     * Draw the molecule
     * recommend to avoid, but to use renderer approach
     *
     * Returns id of this molecule
     *
     * @return   The id value
     */
    public int getId()
    {
        return id;
    }

    public Molecule getJOEMol()
    {
        return mol;
    }

    /**
     * Return 3D transformation matrix
     *
     * @return   The matrix3D value
     */
    public Matrix3D getMatrix3D()
    {
        return mat;
    }

    /**
     * Returns atom vector of this molecule
     *
     * @return   The myAtoms value
     */
    public ViewerAtoms getMyAtoms()
    {
        return myAtoms;
    }

    /**
     * Return bond vector of this molecule
     *
     * @return   The myBonds value
     */
    public ViewerBonds getMyBonds()
    {
        return myBonds;
    }

    /**
     * Returns name of molecule
     *
     * @return   The name value
     */
    public String getName()
    {
        return mol.getTitle();
    }

    /**
     * Return ring vector of this molecule
     *
     * @return   The numAtoms value
     */

    //  public RingVector getMyRings() { return myRings; }

    /**
     * Return ring vector of this molecule
     *
     * Return number of atoms in this molecule
     *
     * @return   The numAtoms value
     */
    public int getNumAtoms()
    {
        return myAtoms.size();
    }

    /**
     * Return number of bonds in this molecule
     *
     * @return   The numBonds value
     */
    public int getNumBonds()
    {
        return myBonds.size();
    }

    /**
     * Return the selected atoms vector
     *
     * @return   The selectAtoms value
     */
    public ViewerAtoms getSelectAtoms()
    {
        return selectAtoms;
    }

    /**
     * Return the maxmum x coordinate
     *
     * @return   The xmax value
     */
    public float getXmax()
    {
        return xmax;
    }

    /**
     * Return the minimum x coordinate of this molelcule
     *
     * @return   The xmin value
     */
    public float getXmin()
    {
        return xmin;
    }

    /**
     * Return the maxmum y coordinate
     *
     * @return   The ymax value
     */
    public float getYmax()
    {
        return ymax;
    }

    /**
     * Return the minimum y coordinate
     *
     * @return   The ymin value
     */
    public float getYmin()
    {
        return ymin;
    }

    /**
     * Return the maxmum z coordinate
     *
     * @return   The zmax value
     */
    public float getZmax()
    {
        return zmax;
    }

    /**
     * Return the minimum z coordinate
     *
     * @return   The zmin value
     */
    public float getZmin()
    {
        return zmin;
    }

    public int hashCode()
    {
        if (mol == null)
        {
            return 0;
        }
        else
        {
            return mol.hashCode();
        }
    }

    /**
     * Returns <tt>true</tt> if molecule has <tt>selected</tt> atoms
     *
     * @return   Description of the Return Value
     */
    public boolean hasSelectedAtoms()
    {
        return (selectAtoms.size() > 0);
    }

    /**
     * Designates an atom as <tt>highlighted</tt>.  The specified atom gets
     * appended to the current highlight list.
     *
     * @param a  atom to highlight
     */
    public void highlight(ViewerAtom a)
    {
        if ((a == null) || highlightAtoms.contains(a))
        {
            return;
        }

        highlightAtoms.append(a);
        a.highlight = true;
    }

    /**
     * Designates an atom list as <tt>highlighted</tt>.  The new list replaces
     * any current list of highlighted atoms.
     *
     * @param av  list (vector) of atoms to highlight
     */
    public void highlight(ViewerAtoms av)
    {
        if (av == null)
        {
            return;
        }

        //
        // Unhighlight any currently highlighted atoms.
        //
        if (highlightAtoms != null)
        {
            for (int i = 0; i < highlightAtoms.size(); i++)
            {
                ViewerAtom a = highlightAtoms.getAtom(i);
                a.highlight = false;
            }
        }

        highlightAtoms = new ViewerAtoms(10);

        for (int i = 0; i < av.size(); i++)
        {
            ViewerAtom a = av.getAtom(i);

            if (a != null)
            {
                highlightAtoms.append(a);
                a.highlight = true;
            }
        }
    }

    /**
     * Flags specified atom as <tt>selected</tt>
     *
     * @param a  atom to be selected
     */
    public void select(ViewerAtom a)
    {
        if ((a == null) || a.select)
        {
            return;
        }

        a.select = true;
        selectAtoms.append(a);
    }

    /**
     * Set atomIdToIndex
     *
     * @param index  atomIdToIndex
     */
    public void setAtomIdToIndex(Hashtable index)
    {
        atomIdToIndex = index;
    }

    /**
     * Set highlighted atoms vector
     *
     * @param av  atom vector
     */
    public void setHighlightAtoms(ViewerAtoms av)
    {
        highlightAtoms = av;
    }

    /**
     * Set id of this molecule
     *
     * @param id  molecule id
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Set property table
     *
     * @param mat   The new matrix3D value
     */

    //  public void setProperties( Hashtable prop ) { properties = prop; }

    /**
     * Set property table
     *
     * Set 3D transformation matrix
     *
     * @param mat   The new matrix3D value
     */
    public void setMatrix3D(Matrix3D mat)
    {
        this.mat = mat;
    }

    /**
     * Set atom vector
     *
     * @param atoms  The new myAtoms value
     */
    public void setMyAtoms(ViewerAtoms atoms)
    {
        myAtoms = atoms;
    }

    /**
     * Set bond vector
     *
     * @param bv  bond vector
     */
    public void setMyBonds(ViewerBonds bv)
    {
        myBonds = bv;
    }

    /**
     * Set ring vector
     *
     * @param numAtoms  The new numAtoms value
     */

    //  public void setMyRings( RingVector rv ) { myRings = rv; }

    /**
     * Set ring vector
     *
     * Set number of atoms
     *
     * @param numAtoms  The new numAtoms value
     */
    public void setNumAtoms(int numAtoms)
    {
        this.numAtoms = numAtoms;
    }

    /**
     * Set selected atoms vector
     *
     * @param av  atom vector
     */
    public void setSelectAtoms(ViewerAtoms av)
    {
        selectAtoms = av;
    }

    /**
     * Set maxmum of X-coordinate
     *
     * @param xmax  maximum X-coordinate
     */
    public void setXmax(float xmax)
    {
        this.xmax = xmax;
    }

    /**
     * Set minimum of X-coordinate of this molecule
     *
     * @param xmin  The new xmin value
     */
    public void setXmin(float xmin)
    {
        this.xmin = xmin;
    }

    /**
     * Set maxmum of Y-coordinate
     *
     * @param ymax  maximum Y-coordinate
     */
    public void setYmax(float ymax)
    {
        this.ymax = ymax;
    }

    /**
     * Set minimum of Y-coordinate
     *
     * @param ymin  minimum Y-coordinate
     */
    public void setYmin(float ymin)
    {
        this.ymin = ymin;
    }

    /**
     * Set maxmum of Z-coordinate
     *
     * @param zmax  maximum Z-coordinate
     */
    public void setZmax(float zmax)
    {
        this.zmax = zmax;
    }

    /**
     * Set minimum of Z-coordinate
     *
     * @param zmin  The new zmin value
     */
    public void setZmin(float zmin)
    {
        this.zmin = zmin;
    }

    /**
     * Returns <tt>true</tt> if molecule has <tt>highlighted</tt> atoms
     *
     * @return   Description of the Return Value
     */
    protected boolean hasHighlightedAtoms()
    {
        return (highlightAtoms.size() > 0);
    }

    //  protected static void setRenderer( Renderer r ) { rendererDefault = r; }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
