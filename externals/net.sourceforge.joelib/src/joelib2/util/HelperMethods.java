///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: HelperMethods.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
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
package joelib2.util;

import joelib2.ext.ExternalHelper;

import joelib2.feature.types.atomlabel.AtomHybridisation;
import joelib2.feature.types.atomlabel.AtomIsHydrogen;
import joelib2.feature.types.bondlabel.BondInAromaticSystem;

import joelib2.io.BasicIOType;
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;
import joelib2.io.PropertyWriter;

import joelib2.math.BasicMatrix3D;
import joelib2.math.BasicVector3D;
import joelib2.math.Matrix3D;
import joelib2.math.Vector3D;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.ConformerAtom;
import joelib2.molecule.Molecule;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.BondIterator;

import wsi.ra.tool.BasicPropertyHolder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * JOELib hepler methods.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2007/03/03 00:03:49 $
 */
public class HelperMethods
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(HelperMethods.class
            .getName());

    /**
     *  Description of the Field
     */
    public final static double RAD_TO_DEG = 180.0f / Math.PI;

    /**
     *  Description of the Field
     */
    public final static double DEG_TO_RAD = Math.PI / 180.0f;
    public static final String eol = System.getProperty("line.separator", "\n");
    private static HelperMethods jhm;

    /**
     *  Description of the Field
     */
    public final static String CONTACT_E_MAIL = "wegner@users.sourceforge.net";

    public static Category copyright2Logger = Category.getInstance(
            "JOELib2 Copyright (c)");

    public final static String[] COPYRIGHT =
        new String[]
        {
            "########################################################################",
            "# Don't panic! Other packages might suppress warnings and errors !      ",
            "########################################################################",
            "# Copyright OELIB:          OpenEye Scientific Software, Santa Fe,      ",
            "#                           U.S.A., 1999,2000,2001                      ",
            "# Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of  ",
            "#                           Tuebingen, Germany, 2001,2002,2003,2004,2005",
            "# Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,        ",
            "#                           2003,2004,2005                              ",
            "# Copyright JOELIB/JOELib2: J. K. Wegner, Mechelen, Belgium             ",
            "#                           2001,2002,2003,2004,2005,2006,2007          ",
            "#                                                                       ",
            "# This program is free software; you can redistribute it and/or modify  ",
            "# it under the terms of the GNU General Public License as published by  ",
            "# the Free Software Foundation version 2 of the License.                ",
            "#                                                                       ",
            "# This program is distributed in the hope that it will be useful,       ",
            "# but WITHOUT ANY WARRANTY; without even the implied warranty of        ",
            "# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         ",
            "# GNU General Public License for more details.                          ",
            "########################################################################"
        };

    /**
     *  Description of the Field
     */
    public final static int BUFF_SIZE = 1024;

    /**
     *  Description of the Field
     */
    public static int OEPolarGrid = 0x01;

    /**
     *  Description of the Field
     */
    public static int OELipoGrid = 0x02;

    /**
     *  Description of the Field
     */
    public static int PT_CATION = 1;

    /**
     *  Description of the Field
     */
    public static int PT_ANION = 2;

    /**
     *  Description of the Field
     */
    public static int PT_ACCEPTOR = 3;

    /**
     *  Description of the Field
     */
    public static int PT_POLAR = 4;

    /**
     *  Description of the Field
     */
    public static int PT_DONOR = 5;

    /**
     *  Description of the Field
     */
    public static int PT_HYDROPHOBIC = 6;

    /**
     *  Description of the Field
     */
    public static int PT_OTHER = 7;

    /**
     *  Description of the Field
     */
    public static int PT_METAL = 8;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JHM object
     */
    private HelperMethods()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param a  Description of the Parameter
     * @param b  Description of the Parameter
     * @param n  Description of the Parameter
     * @return   Description of the Return Value
     */
    public final static boolean EQn(String a, String b, int n)
    {
        return (a.substring(0, n - 1).equals(b.substring(0, n - 1)));
    }

    /*public static int PDBSort(ChainsAtom arg1[], ChainsAtom arg2[])
     *{
     *throw Exception("use ChainAtomComparator");
     *}
     *static void outputPDBFile(ChainsMolecule mol, OutputStream os)
     *{
     *int src,dst;
     *ChainsAtom atom;
     *String ptr;
     *char tmpc[]=new char[1];
     *int i;
     *PrintfStream fp = new PrintfStream(os);
     *for( i=0; i<mol.acount; i++ )
     *PDBOrder[i] = mol.atom[i];
     *qsort(PDBOrder,mol.acount, new ChainAtomComparator());
     *ptr = mol.name;
     *if( ptr!=null )
     *{
     *fp.print("COMPND    ");
     *fp.prinln( ptr.getBytes() );
     *}
     *for( i=0; i<mol.acount; i++ )
     *{
     *atom = PDBOrder[i];
     *atom.serno = i+1;
     *if( atom.hetflag )
     *{
     *fp.print( "HETATM" );
     *}
     *else
     *{
     *fp.print( "ATOM   " );
     *}
     *fp.printf("%4d ", atom.serno);
     *if( atom.atomid == -1 )
     *{
     *fp.printf("%s  ", chainsElem[atom.elem].symbol);
     *}
     *else
     *{
     *if( atom.elem == 1 )
     *{
     *if( atom.hcount )
     *{
     *tmpc[0] = atom.hcount+'0';
     *fp.write(tmpc);
     *}
     *else
     *{
     *tmpc[0] = ' ';
     *fp.write(tmpc);
     *}
     *fp.printf("H%.2s", chainsAtomName[atom.atomid]+2);
     *}
     *else
     *{
     *fp.printf( ,"%.4s", chainsAtomName[atom.atomid]);
     *}
     *fp.printf( " %s ",  chainsResName[atom.resid]);
     *fp.printf( "%c%4d", atom.chain);
     *fp.printf( "%4d",   atom.resno);
     *fp.printf( "    %8.3lf",  atom.x);
     *fp.printf( "%8.3lf",      atom.y);
     *fp.printf( "%8.3lf",      atom.z);
     *fp.println("  1.00  0.00");
     *}
     *for( i=0; i<mol.bcount; i++ )
     *{
     *if( mol.bond[i].flag & BF_DOUBLE )
     *{
     *src = mol.atom[mol.bond[i].src].serno;
     *dst = mol.atom[mol.bond[i].dst].serno;
     *fp.printf( "CONECT%5d", src);
     *fp.printf( "%5d",       dst);
     *fp.printf( "%5d\n",     dst);
     *fp.printf( "CONECT%5d", dst);
     *fp.printf( "%5d",       src);
     *fp.printf( "%5d\n",     src);
     *}
     *}
     *fp.println("END ");
     *} */

    /**
     * @param  dffv  {@link java.util.Vector} of <tt>int[1]</tt>
     * @param  mol   Description of the Parameter
     * @param  bitVector    Description of the Parameter
     * @return       The dFFVector value
     */
    public static boolean getDFFVector(Molecule mol, List dffv,
        BitVector bitVector)
    {
        dffv.clear();

        if (dffv instanceof Vector)
        {
            ((Vector) dffv).setSize(mol.getAtomsSize());
        }

        int dffcount;

        int natom;
        BitVector used = new BasicBitVector();
        BitVector curr = new BasicBitVector();
        BitVector next = new BasicBitVector();
        Atom atom;
        Atom atom1;
        Bond bond;

        next.clear();

        AtomIterator ait = mol.atomIterator();

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            if (bitVector.get(atom.getIndex()))
            {
                ((int[]) dffv.get(atom.getIndex() - 1))[0] = 0;

                continue;
            }

            dffcount = 0;
            used.clear();
            curr.clear();
            used.setBitOn(atom.getIndex());
            curr.setBitOn(atom.getIndex());

            while (!curr.isEmpty() && ((bitVector.and(curr)).size() == 0))
            {
                next.clear();

                for (natom = curr.nextBit(-1); natom != curr.endBit();
                        natom = curr.nextBit(natom))
                {
                    atom1 = mol.getAtom(natom);

                    BondIterator bit = atom1.bondIterator();

                    while (bit.hasNext())
                    {
                        bond = bit.nextBond();

                        if (!used.bitIsOn(bond.getNeighborIndex(atom1)) &&
                                !curr.bitIsOn(bond.getNeighborIndex(atom1)))
                        {
                            if (!AtomIsHydrogen.isHydrogen(
                                        (bond.getNeighbor(atom1))))
                            {
                                next.setBitOn(bond.getNeighborIndex(atom1));
                            }
                        }
                    }
                }

                used.orSet(next);
                curr.set(next);
                dffcount++;
            }

            dffv.set(atom.getIndex() - 1, new int[]{dffcount});
        }

        return true;
    }

    public static String getTempFileBase()
    {
        Properties prop = BasicPropertyHolder.instance().getProperties();
        String tempDir = "joelib2.temporary.directory." +
            ExternalHelper.getOperationSystemName();
        String temp = prop.getProperty(tempDir);

        if (temp == null)
        {
            logger.error("You must define a temporary directory in  '" +
                tempDir + "'.");

            return null;
        }

        String fileSeparator = System.getProperty("file.separator");

        return temp + fileSeparator;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public static synchronized HelperMethods instance()
    {
        if (jhm == null)
        {
            jhm = new HelperMethods();
        }

        return jhm;
    }

    /**
     *  Description of the Method
     *
     * @param  mol      Description of the Parameter
     * @param  a        Description of the Parameter
     * @param  b        Description of the Parameter
     * @param  one2one  Description of the Parameter
     * @return          Description of the Return Value
     */
    public static double minimumPairRMS(Molecule mol, double[] a, double[] b,
        boolean[] one2one)
    {
        int i;
        int j;
        int k = 0;
        double min;
        double tmp;
        double d_2 = 0.0;
        BasicBitVector bset = new BasicBitVector();
        one2one[0] = true;

        Vector _atom = new Vector();

        // of type Atom
        _atom.setSize(mol.getAtomsSize());

        for (i = 0; i < mol.getAtomsSize(); i++)
        {
            _atom.set(i, mol.getAtom(i + 1));
        }

        for (i = 0; i < mol.getAtomsSize(); i++)
        {
            min = Double.MAX_VALUE;

            for (j = 0; j < mol.getAtomsSize(); j++)
            {
                if ((((Atom) _atom.get(i)).getAtomicNumber() ==
                            ((Atom) _atom.get(j)).getAtomicNumber()) &&
                        (AtomHybridisation.getIntValue((Atom) _atom.get(i)) ==
                            AtomHybridisation.getIntValue((Atom) _atom.get(j))))
                {
                    if (!bset.get(j))
                    {
                        double tmp1 = a[3 * i] - b[3 * j];
                        double tmp2 = a[(3 * i) + 1] - b[(3 * j) + 1];
                        double tmp3 = a[(3 * i) + 2] - b[(3 * j) + 2];
                        tmp = (tmp1 * tmp1) + (tmp2 * tmp2) + (tmp3 * tmp3);

                        if (tmp < min)
                        {
                            k = j;
                            min = tmp;
                        }
                    }
                }
            }

            if (i != j)
            {
                one2one[0] = false;
            }

            bset.setBitOn(k);
            d_2 += min;
        }

        d_2 /= (double) mol.getAtomsSize();

        return Math.sqrt(d_2);
    }

    public static synchronized boolean moleculeToFile(String filename,
        Molecule mol, BasicIOType type, boolean writeDescriptors)
    {
        FileOutputStream out;

        try
        {
            out = new FileOutputStream(filename);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }

        MoleculeFileIO writer = null;

        try
        {
            writer = MoleculeFileHelper.getMolWriter(out, type);

            if (!writer.writeable())
            {
                logger.warn(type.getRepresentation() + " is not writeable.");

                return false;
            }

            if (!writeDescriptors && (writer instanceof PropertyWriter))
            {
                ((PropertyWriter) writer).write(mol, null, false, null);
            }
            else
            {
                writer.write(mol, null);
            }

            // close molecule writer
            writer.closeWriter();
            out.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();

            return false;
        }
        catch (MoleculeIOException ex)
        {
            ex.printStackTrace();

            return false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return false;
        }

        return true;
    }

    /**
     * @param  visit  {@link java.util.Vector} of <tt>int[0]</tt>
     * @param  mol    Description of the Parameter
     * @param  depth  Description of the Parameter
     */
    public static void resetVisit(Molecule mol, List visit, int depth)
    {
        Bond bond;
        BondIterator bit = mol.bondIterator();

        while (bit.hasNext())
        {
            bond = bit.nextBond();

            if (BondInAromaticSystem.isAromatic(bond) &&
                    (((int[]) visit.get(bond.getIndex()))[0] >= depth))
            {
                ((int[]) visit.get(bond.getIndex()))[0] = 0;
            }
        }
    }

    /**
     * @param  v      of type <tt>int[1]</tt>
     * @param  value  The new intVectorToValue value
     */
    public static void setIntVectorToValue(List v, int value)
    {
        int[] itmp;

        for (int i = 0; i < v.size(); i++)
        {
            itmp = (int[]) v.get(i);

            if (itmp != null)
            {
                itmp[0] = value;
            }
            else
            {
                v.set(i, new int[]{value});
            }
        }
    }

    /**
     *  This methodName will rotate the coordinates of 'atoms' such that tor == ang
     *  - atoms in 'tor' should be ordered such that the 3rd atom is the pivot
     *  around which atoms rotate ang is in degrees.
     *
     * @param  atoms  {@link java.util.Vector} of <tt>int[1]</tt>
     * @param  c      The new rotorToAngle value
     * @param  ref    The new rotorToAngle value
     * @param  ang    The new rotorToAngle value
     */
    public static void setRotorToAngle(double[] c, ConformerAtom[] ref,
        double ang, List atoms)
    {
        double v1x;
        double v1y;
        double v1z;
        double v2x;
        double v2y;
        double v2z;
        double v3x;
        double v3y;
        double v3z;
        double c1x;
        double c1y;
        double c1z;
        double c2x;
        double c2y;
        double c2z;
        double c3x;
        double c3y;
        double c3z;
        double c1mag;
        double c2mag;
        double radang;
        double costheta;
        double[] m = new double[9];
        double x;
        double y;
        double z;
        double mag;
        double rotang;
        double sn;
        double cs;
        double t;
        double tx;
        double ty;
        double tz;

        int[] tor = new int[4];
        tor[0] = ref[0].getCoordinateIdx();
        tor[1] = ref[1].getCoordinateIdx();
        tor[2] = ref[2].getCoordinateIdx();
        tor[3] = ref[3].getCoordinateIdx();

        //
        //calculate the torsion angle
        //
        v1x = c[tor[0]] - c[tor[1]];
        v2x = c[tor[1]] - c[tor[2]];
        v1y = c[tor[0] + 1] - c[tor[1] + 1];
        v2y = c[tor[1] + 1] - c[tor[2] + 1];
        v1z = c[tor[0] + 2] - c[tor[1] + 2];
        v2z = c[tor[1] + 2] - c[tor[2] + 2];
        v3x = c[tor[2]] - c[tor[3]];
        v3y = c[tor[2] + 1] - c[tor[3] + 1];
        v3z = c[tor[2] + 2] - c[tor[3] + 2];

        c1x = (v1y * v2z) - (v1z * v2y);
        c2x = (v2y * v3z) - (v2z * v3y);
        c1y = (-v1x * v2z) + (v1z * v2x);
        c2y = (-v2x * v3z) + (v2z * v3x);
        c1z = (v1x * v2y) - (v1y * v2x);
        c2z = (v2x * v3y) - (v2y * v3x);
        c3x = (c1y * c2z) - (c1z * c2y);
        c3y = (-c1x * c2z) + (c1z * c2x);
        c3z = (c1x * c2y) - (c1y * c2x);

        c1mag = (c1x * c1x) + (c1y * c1y) + (c1z * c1z);
        c2mag = (c2x * c2x) + (c2y * c2y) + (c2z * c2z);

        if ((c1mag * c2mag) < 0.01)
        {
            costheta = 1.0;
        }

        //avoid div by zero error
        else
        {
            costheta = ((c1x * c2x) + (c1y * c2y) + (c1z * c2z)) /
                (Math.sqrt(c1mag * c2mag));
        }

        if (costheta < -0.999999)
        {
            costheta = -0.999999f;
        }

        if (costheta > 0.999999)
        {
            costheta = 0.999999f;
        }

        if (((v2x * c3x) + (v2y * c3y) + (v2z * c3z)) > 0.0)
        {
            radang = -Math.acos(costheta);
        }
        else
        {
            radang = Math.acos(costheta);
        }

        //
        // now we have the torsion angle (radang) - set up the rot matrix
        //
        //find the difference between current and requested
        rotang = (DEG_TO_RAD * ang) - radang;

        sn = Math.sin(rotang);
        cs = Math.cos(rotang);
        t = 1 - cs;

        //normalize the rotation vector
        mag = Math.sqrt((v2x * v2x) + (v2y * v2y) + (v2z * v2z));
        x = v2x / mag;
        y = v2y / mag;
        z = v2z / mag;

        //set up the rotation matrix
        m[0] = (t * x * x) + cs;
        m[1] = (t * x * y) + (sn * z);
        m[2] = (t * x * z) - (sn * y);
        m[3] = (t * x * y) - (sn * z);
        m[4] = (t * y * y) + cs;
        m[5] = (t * y * z) + (sn * x);
        m[6] = (t * x * z) + (sn * y);
        m[7] = (t * y * z) - (sn * x);
        m[8] = (t * z * z) + cs;

        //
        //now the matrix is set - time to rotate the atoms
        //
        tx = c[tor[1]];
        ty = c[tor[1] + 1];
        tz = c[tor[1] + 2];

        int j;

        for (int i = 0; i < atoms.size(); i++)
        {
            int[] atom = (int[]) atoms.get(i);
            j = (atom[0] - 1) * 3;
            c[j] -= tx;
            c[j + 1] -= ty;
            c[j + 2] -= tz;

            x = (c[j] * m[0]) + (c[j + 1] * m[1]) + (c[j + 2] * m[2]);
            y = (c[j] * m[3]) + (c[j + 1] * m[4]) + (c[j + 2] * m[5]);
            z = (c[j] * m[6]) + (c[j + 1] * m[7]) + (c[j + 2] * m[8]);
            c[j] = x;
            c[j + 1] = y;
            c[j + 2] = z;
            c[j] += tx;
            c[j + 1] += ty;
            c[j + 2] += tz;
        }
    }

    /**
     * @param  a          Description of the Parameter
     * @param  b          Description of the Parameter
     * @param  c          Description of the Parameter
     * @param  d          Description of the Parameter
     * @return            Description of the Return Value
     */

    //public void getChirality(Molecule mol, Vector chirality)
    //{
    //  int itmp[];
    //
    //  chirality.setSize(mol.numAtoms()+1);
    //  setIntVectorToValue(chirality, 0);
    //
    //  Atom atom;
    //  AtomIterator ait = mol.atomIterator();
    //  while(ait.hasNext())
    //  {
    //    atom = ait.nextAtom();
    //    if (atom.isChiral())
    //    {
    //          double sv = calcSignedVolume(mol,atom);
    //          itmp    = (int[])chirality.get(atom.getIdx()-1);
    //          if (sv < 0.0f)  itmp[0] = -1;
    //          else if (sv > 0.0)  itmp[0] = 1;
    //    }
    //  }
    //}

    /**
     *  Calculate the signed volume for an atom. If the atom has a valence of 3
     *  the coordinates of an attached hydrogen are calculated
     *
     * @param  a          Description of the Parameter
     * @param  b          Description of the Parameter
     * @param  c          Description of the Parameter
     * @param  d          Description of the Parameter
     * @return            Description of the Return Value
     */

    //public static double calcSignedVolume(Molecule mol, Atom atm)
    //{
    //  XYZVector tmp_crd;
    //  Vector nbr_atms; // of type int[1]
    //  Vector nbr_crds; // of type XYZVector
    //  double hbrad = JOEElementTable.instance().correctedBondRad(1,0);
    //
    //  if (atm.getHvyValence() < 3)
    //  {
    //    logger.error("Cannot calculate a signed volume for an atom with a heavy atom valence of "+atm.getHvyValence());
    //    System.exit(0);
    //  }
    //
    //  // Create a vector with the coordinates of the neighbor atoms
    //  Atom nbr;
    //  NbrAtomIterator nait = atm.nbrAtomIterator();
    //  while(nait.hasNext())
    //  {
    //    nbr = nait.nextNbrAtom();
    //    nbr_atms.add(new int[]{nbr.getIdx()});
    //  }
    //
    //  // sort the neighbor atoms to insure a consistent ordering
    //  //QuickInsertSort sorting = new QuickInsertSort();
    //  //RingSizeComparator ringSizeComparator = new RingSizeComparator();
    //  //sorting.sort(_rlist, ringSizeComparator);
    //  sort(nbr_atms.begin(),nbr_atms.end());
    //  for (int i = 0; i < nbr_atms.size(); i++)
    //  {
    //    Atom tmp_atm = mol.getAtom( ((int[])nbr_atms.get(i))[0] );
    //    nbr_crds.add(tmp_atm.getVector());
    //  }
    //
    //  // If we have three heavy atoms we need to calculate the position of the fourth
    //  if (atm.getHvyValence() == 3)
    //  {
    //    double bondlen = hbrad+JOEElementTable.instance().correctedBondRad(atm.getAtomicNum(), atm.getHyb());
    //    atm.getNewBondVector(tmp_crd,bondlen);
    //    nbr_crds.add(tmp_crd);
    //  }
    //
    //  return signed_volume( (XYZVector) nbr_crds.get(0),
    //                        (XYZVector) nbr_crds.get(1),
    //                        (XYZVector) nbr_crds.get(2),
    //                        (XYZVector) nbr_crds.get(3));
    //}

    /**
     *  Calculate the signed volume for an atom. If the atom has a valence of 3
     *  the coordinates of an attached hydrogen are calculated
     *
     *  Calculate a signed volume given a set of 4 coordinates.
     *
     * @param  a          Description of the Parameter
     * @param  b          Description of the Parameter
     * @param  c          Description of the Parameter
     * @param  d          Description of the Parameter
     * @return            Description of the Return Value
     */
    public static double signedVolume(final Vector3D vector1,
        final Vector3D vector2, final Vector3D vector3, final Vector3D vector4)
    {
        Vector3D row1;
        Vector3D row2;
        Vector3D row3;
        row1 = BasicVector3D.sub(vector2, vector1);
        row2 = BasicVector3D.sub(vector3, vector1);
        row3 = BasicVector3D.sub(vector4, vector1);

        Matrix3D matrix = new BasicMatrix3D(row1, row2, row3);

        return matrix.determinant();
    }

    /**
     *  Description of the Method
     *
     * @param x  Description of the Parameter
     * @return   Description of the Return Value
     */
    public final static double SQUARE(double x)
    {
        return x * x;
    }

    /**
     * @param  vcr  {@link java.util.Vector} of <tt>String</tt>
     * @param  buf  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static boolean tokenize(List<String> vcr, String buf)
    {
        return tokenize(vcr, buf, " \t\n");
    }

    /**
     * @param  vcr       {@link java.util.Vector} of <tt>String</tt>
     * @param  buf       Description of the Parameter
     * @param  delimstr  Description of the Parameter
     * @return           Description of the Return Value
     */
    public static boolean tokenize(List<String> vcr, String buf,
        String delimstr)
    {
        vcr.clear();
        buf = buf + "\n";

        StringTokenizer st = new StringTokenizer(buf, delimstr);

        while (st.hasMoreTokens())
        {
            vcr.add(st.nextToken());
        }

        return true;
    }

    /**
     * @param  vcr       {@link java.util.Vector} of <tt>String</tt>
     * @param  s         Description of the Parameter
     * @param  delimstr  Description of the Parameter
     * @param  limit     Description of the Parameter
     * @return           Description of the Return Value
     */
    public static boolean tokenize(List<String> vcr, String s, String delimstr,
        int limit)
    {
        System.out.println("Warning: tokenize \"" + s + "\"");
        vcr.clear();
        s = s + "\n";

        int endpos = 0;
        int matched = 0;

        StringTokenizer st = new StringTokenizer(s, delimstr);

        while (st.hasMoreTokens())
        {
            String tmp = st.nextToken();
            vcr.add(tmp);

            matched++;

            if (matched == limit)
            {
                endpos = s.lastIndexOf(tmp);
                vcr.add(s.substring(endpos + tmp.length()));

                break;
            }
        }

        return true;
    }

    /**
     *  Description of the Method
     *
     * @param  a  Description of the Parameter
     * @param  b  Description of the Parameter
     * @return    Description of the Return Value
     */
    public boolean compareBonds(final Bond a, final Bond b)
    {
        if (a.getBeginIndex() == b.getBeginIndex())
        {
            return (a.getEndIndex() < b.getEndIndex());
        }

        return (a.getBeginIndex() < b.getBeginIndex());
    }

    /**
     *  Description of the Method
     *
     * @param  ofs  Description of the Parameter
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    public boolean writeTitles(OutputStream ofs, Molecule mol)
    {
        PrintStream ps = new PrintStream(ofs);
        ps.println(mol.getTitle());

        return true;
    }

    double calcRMS(double[] r, double[] f, int size)
    {
        int i;
        float d2 = 0.0f;

        for (i = 0; i < size; i++)
        {
            d2 += (((r[i * 3] - f[i * 3]) * (r[i * 3] - f[i * 3])) +
                    ((r[(i * 3) + 1] - f[(i * 3) + 1]) *
                        (r[(i * 3) + 1] - f[(i * 3) + 1])) +
                    ((r[(i * 3) + 2] - f[(i * 3) + 2]) *
                        (r[(i * 3) + 2] - f[(i * 3) + 2])));
        }

        d2 /= (double) size;

        return (Math.sqrt(d2));
    }

    BasicVector3D centerCoords(double[] c, int size)
    {
        int i;
        double x = 0;
        double y = 0;
        double z = 0;

        for (i = 0; i < size; i++)
        {
            x += c[i * 3];
            y += c[(i * 3) + 1];
            z += c[(i * 3) + 2];
        }

        x /= (double) size;
        y /= (double) size;
        z /= (double) size;

        for (i = 0; i < size; i++)
        {
            c[i * 3] -= x;
            c[(i * 3) + 1] -= y;
            c[(i * 3) + 2] -= z;
        }

        BasicVector3D v = new BasicVector3D(x, y, z);

        return (v);
    }

    /**
     *
     *   @param m of size[3][3]
     */
    void rotateCoords(double[] c, double[][] m, int size)
    {
        int i;
        double x;
        double y;
        double z;

        for (i = 0; i < size; i++)
        {
            x = (c[i * 3] * m[0][0]) + (c[(i * 3) + 1] * m[0][1]) +
                (c[(i * 3) + 2] * m[0][2]);
            y = (c[i * 3] * m[1][0]) + (c[(i * 3) + 1] * m[1][1]) +
                (c[(i * 3) + 2] * m[1][2]);
            z = (c[i * 3] * m[2][0]) + (c[(i * 3) + 1] * m[2][1]) +
                (c[(i * 3) + 2] * m[2][2]);
            c[i * 3] = x;
            c[(i * 3) + 1] = y;
            c[(i * 3) + 2] = z;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
