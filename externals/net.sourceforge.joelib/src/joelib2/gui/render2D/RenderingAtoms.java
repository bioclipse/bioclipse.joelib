///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: RenderingAtoms.java,v $
//Purpose:  Renderer for a 2D layout.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.7 $
//                      $Date: 2005/02/17 16:48:32 $
//                      $Author: wegner $
//Original Author: steinbeck gzelter, egonw
//Original Version: Copyright (C) 1997-2003
//                  The Chemistry Development Kit (CDK) project
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation; either version 2.1
// of the License, or (at your option) any later version.
// All we ask is that proper credit is given for our work, which includes
// - but is not limited to - adding the above copyright notice to the beginning
// of your source code files, and to any copyright notice that you may distribute
// with programs based on this work.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////
package joelib2.gui.render2D;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;
import joelib2.molecule.MoleculeVector;

import joelib2.molecule.fragmentation.ContiguousFragments;

import joelib2.ring.Ring;

import joelib2.util.HelperMethods;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Rendering atoms.
 *
 * @.author     steinbeck
 * @.author     wegnerj
 * @.license    LGPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:32 $
 */
public class RenderingAtoms implements java.io.Serializable, Cloneable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.gui.render2D.RenderingAtoms");

    //~ Instance fields ////////////////////////////////////////////////////////

    private Hashtable aMap = new Hashtable();
    private List bonds = new Vector();
    private Hashtable fraMap = new Hashtable();
    private List frAtoms = new Vector();
    private List molecules = new Vector();
    private List molFragments = new Vector();
    private List rings = new Vector();

    //~ Constructors ///////////////////////////////////////////////////////////

    public RenderingAtoms()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void add(Molecule mol)
    {
        molecules.add(mol);

        Vector origAtomIdx = new Vector();
        ContiguousFragments fragments = new ContiguousFragments();
        MoleculeVector frags = fragments.getFragmentation(mol, false,
                origAtomIdx);

        //JOEMolVector frags = new JOEMolVector();
        //frags.addMol(mol);
        Molecule tmpMol;
        List tmpRings;
        Bond bond;
        Atom atom;

        RenderAtom ra;
        int[] orgIdx;

        for (int i = 0; i < frags.getSize(); i++)
        {
            tmpMol = frags.getMol(i);
            molFragments.add(tmpMol);
            orgIdx = (int[]) origAtomIdx.get(i);

            //System.out.println("frag "+i+" has "+tmpMol.numAtoms()+" atoms");
            for (int j = 1; j <= tmpMol.getAtomsSize(); j++)
            {
                atom = tmpMol.getAtom(j);

                //atoms.add(atom);
                //atomLabels.add(null);
                if (logger.isDebugEnabled())
                {
                    logger.debug("fragmented atom " + j + " " +
                        atom.getCoords3D());
                }

                //                      if(atom.getBonds().size()!=0)connectedAtoms.add(atom);
                ra = new RenderAtom();
                ra.mol = mol;
                ra.frMol = tmpMol;
                ra.atom = mol.getAtom(orgIdx[j - 1]);
                ra.frAtom = atom;
                ra.fraLabel = null;
                frAtoms.add(ra);

                fraMap.put(ra.frAtom, ra);
                aMap.put(ra.atom, ra);
            }

            tmpRings = tmpMol.getSSSR();

            for (int j = 0; j < tmpRings.size(); j++)
            {
                rings.add(tmpRings.get(j));

                if (logger.isDebugEnabled())
                {
                    logger.debug("ring " + tmpRings.get(j) + " in fragment " +
                        i);
                }
            }

            for (int j = 0; j < tmpMol.getBondsSize(); j++)
            {
                bond = tmpMol.getBond(j);
                bonds.add(bond);

                //if (bond.isDown() || bond.isUp() || bond.isWedge() ||bond.isHash())
                //                                                                      {
                //logger.info("bond "+bond.getBeginAtomIdx()+bond+bond.getEndAtomIdx()+" has stereo flags" +bond.getFlags());
                //                                                                      }
            }
        }
    }

    public Atom getAtom(Atom renderAtom)
    {
        RenderAtom ra = (RenderAtom) fraMap.get(renderAtom);

        return ra.atom;
    }

    /**
     * Gets analogue rendering atom for the original atom of the original molecule.
     */
    public RenderAtom getRenderAtom(Atom atom)
    {
        return (RenderAtom) aMap.get(atom);
    }

    public RenderAtom getRenderAtom(int i)
    {
        return (RenderAtom) frAtoms.get(i);
    }

    /**
     * Gets rendering atom.
     *
     * @param i
     * @return
     */
    public Atom getRenderAtomAtom(int i)
    {
        RenderAtom ra = (RenderAtom) frAtoms.get(i);

        return ra.frAtom;
    }

    /**
     * @return
     */
    public int getRenderAtomCount()
    {
        return frAtoms.size();
    }

    public String getRenderAtomLabel(int i)
    {
        RenderAtom ra = (RenderAtom) frAtoms.get(i);

        return ra.fraLabel;
    }

    public int getRenderAtomNumber(Atom renderAtom)
    {
        RenderAtom ra;

        for (int i = 0; i < frAtoms.size(); i++)
        {
            ra = (RenderAtom) frAtoms.get(i);

            if (ra.frAtom == renderAtom)
            {
                return i;
            }
        }

        return -1;
    }

    public Atom[] getRenderAtoms()
    {
        Atom[] tmp = new Atom[frAtoms.size()];
        RenderAtom ra;

        for (int i = 0; i < frAtoms.size(); i++)
        {
            ra = (RenderAtom) frAtoms.get(i);
            tmp[i] = ra.frAtom;
        }

        return tmp;
    }

    /**
     * @return
     */
    public List getRenderBonds()
    {
        return bonds;
    }

    public List getRenderFragments()
    {
        return molFragments;
    }

    public Ring[] getRenderRings()
    {
        Ring[] tmp = new Ring[rings.size()];

        for (int i = 0; i < rings.size(); i++)
        {
            tmp[i] = (Ring) rings.get(i);
        }

        return tmp;
    }

    public boolean hasRenderAtomLabel(int i)
    {
        RenderAtom ra = (RenderAtom) frAtoms.get(i);

        if ((ra.fraLabel != null) && (ra.fraLabel.length() != 0))
        {
            return true;
        }

        return false;
    }

    public void setRenderAtomLabel(int i, String label)
    {
        RenderAtom ra = (RenderAtom) frAtoms.get(i);
        ra.fraLabel = label;
    }

    public void setRenderAtomLabels(Molecule molecule, String labels,
        String delimiter, String labelDelim)
    {
        if (labels != null)
        {
            Vector lV = new Vector();
            HelperMethods.tokenize(lV, labels, delimiter);

            String s;

            //Vector lEntry = new Vector();
            int index;
            String label;
            RenderAtom renderAtom;

            for (int i = 0; i < lV.size(); i++)
            {
                s = (String) lV.get(i);
                index = s.indexOf(labelDelim);

                if (index != -1)
                {
                    Integer integer = new Integer(s.substring(0, index));
                    label = s.substring(index + 1);
                    renderAtom = this.getRenderAtom(molecule.getAtom(
                                integer.intValue()));
                    renderAtom.fraLabel = label;
                }
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
