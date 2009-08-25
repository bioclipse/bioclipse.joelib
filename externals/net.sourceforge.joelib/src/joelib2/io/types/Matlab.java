///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Matlab.java,v $
//  Purpose:  Reader/Writer for SDF files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
//            $Date: 2005/03/03 07:13:50 $
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
package joelib2.io.types;

import cformat.PrintfStream;

import joelib2.data.BasicElementHolder;

import joelib2.io.MoleculeFileIO;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.AtomProperties;
import joelib2.molecule.types.PairData;

import joelib2.util.HelperMethods;

import joelib2.util.iterator.AtomIterator;

import java.awt.Color;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Category;


/**
 * Reader/Writer for Matlab files.
 *
 * @.author     wegnerj
 * @.wikipedia  MATLAB
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/03/03 07:13:50 $
 */
public class Matlab implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Description of the Field
     */
    public final static int BALL_AND_STICK = 0;

    /**
     * Description of the Field
     */
    public final static int SPHERE = 1;

    /**
     * Description of the Field
     */
    public final static int STICK = 2;

    /**
     * Description of the Field
     */
    public final static int DEFAULT_OUTPUT_TYPE = SPHERE;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.Matlab");
    private final static String version = "$Revision: 1.9 $";

    /**
     *  Description of the Field
     */
    private final static String description = "Matlab";
    private final static String[] extensions = new String[]{"m", "mat"};

    //~ Instance fields ////////////////////////////////////////////////////////

    private double atomResizeFactor = 0.25;
    private Hashtable colormap = new Hashtable();
    private List colormapRGB = new Vector();

    // variables for property coloring
    private AtomProperties data = null;
    private BasicElementHolder etab = BasicElementHolder.instance();
    private Color maxColor = new Color(1.0f, 0.0f, 0.0f);
    private double maxDataValue;
    private double maxX;
    private double maxY;
    private double maxZ;
    private Color minColor = new Color(0.0f, 0.0f, 1.0f);
    private double minDataValue;
    private double minX;
    private double minY;
    private double minZ;
    private int moleculeCounter = 1;
    private double mx;
    private double my;
    private double mz;

    //    private double biggerBondRadius = 0.3;
    private int outputType = DEFAULT_OUTPUT_TYPE;

    //    private LineNumberReader lnr;
    private PrintfStream ps;
    private double smallerBondRadius = 0.175;
    private boolean usePropertyColoring = false;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Description of the Method
     *
     * @exception IOException  Description of the Exception
     */
    public void closeReader() throws IOException
    {
    }

    /**
     * Description of the Method
     *
     * @exception IOException  Description of the Exception
     */
    public void closeWriter() throws IOException
    {
        ps.close();
    }

    /**
     * Gets the outputType attribute of the POVRay object
     *
     * @return   The outputType value
     */
    public int getOutputType()
    {
        return outputType;
    }

    /**
     *  Description of the Method
     *
     * @param is               Description of the Parameter
     * @exception IOException  Description of the Exception
     */
    public void initReader(InputStream is) throws IOException
    {
        //        lnr = new LineNumberReader(new InputStreamReader(is));
    }

    /**
     *  Description of the Method
     *
     * @param os               Description of the Parameter
     * @exception IOException  Description of the Exception
     */
    public void initWriter(OutputStream os) throws IOException
    {
        ps = new PrintfStream(os);

        colormap.clear();

        ps.println("% Creator: " + this.getClass().getName() + " " + version);
        ps.println("% Author: Joerg Kurt Wegner");

        ps.println("% global settings");
    }

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String inputDescription()
    {
        return null;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String[] inputFileExtensions()
    {
        return null;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String outputDescription()
    {
        return description;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String[] outputFileExtensions()
    {
        return extensions;
    }

    /**
     *  Reads an molecule entry as (unparsed) <tt>String</tt> representation.
     *
     * @return                  <tt>null</tt> if the reader contains no more
     *      relevant data. Otherwise the <tt>String</tt> representation of the
     *      whole molecule entry is returned.
     * @exception  IOException  typical IOException
     */
    public String read() throws IOException
    {
        logger.error(
            "Reading Matlab data as String representation is not implemented yet !!!");

        return null;
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public synchronized boolean read(Molecule mol) throws IOException
    {
        return read(mol, null);
    }

    /**
     * Loads an molecule in MDL SD-MOL format and sets the title.
     * If <tt>title</tt> is <tt>null</tt> the title line in
     * the molecule file is used.
     *
     * @param mol              Description of the Parameter
     * @param title            Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public synchronized boolean read(Molecule mol, String title)
        throws IOException
    {
        return false;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public boolean readable()
    {
        return false;
    }

    /**
     * Sets the outputType attribute of the POVRay object
     *
     * @param _outputType  The new outputType value
     */
    public void setOutputType(int _outputType)
    {
        outputType = _outputType;
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     * @param sb   Description of the Parameter
     */

    /*    private void writeBonds(Molecule mol, StringBuffer sb)
        {
            BondIterator bit = mol.bondIterator();
            Bond bond;
            double x;
            double y;
            double z;
            Color color;
            double middleX;
            double middleY;
            double middleZ;
            double r1;
            double r2;
            double l;
            double s;
            Atom atom1;
            Atom atom2;
            double smallerR;
            double biggerR;
            double middleR;

            Atom nbr;
            Atom atom;
            AtomIterator ait = mol.atomIterator();
            while (ait.hasNext())
            {
                atom = ait.nextAtom();
                NbrAtomIterator nait = atom.nbrAtomIterator();
                while (nait.hasNext())
                {
                    nbr = nait.nextNbrAtom();

                    if (atom.getIdx() < nbr.getIdx())
                    {
                        bond = nait.actualBond();

    //            while (bit.hasNext())
    //            {
    //                bond = bit.nextBond();
                        atom1 = atom;
                        atom2 = nbr;
                        if (atom1.getIdx() < atom2.getIdx())
                        {

                            sb.append("  // bond " + bond.getIdx() + ": " + etab.getSymbol(atom1.getAtomicNum()) + "(" + atom1.getIdx() + ")");
                            sb.append(bond.toString());
                            sb.append(etab.getSymbol(atom2.getAtomicNum()) + "(" + atom2.getIdx() + ")" + JHM.eol);
                            sb.append("  union {" + JHM.eol);

                            r1 = JOEElementTable.instance().correctedVdwRad(atom1.getAtomicNum()) * atomResizeFactor;
                            r2 = JOEElementTable.instance().correctedVdwRad(atom2.getAtomicNum()) * atomResizeFactor;
                            x = atom2.getX() - atom1.getX();
                            y = atom2.getY() - atom1.getY();
                            z = atom2.getZ() - atom1.getZ();
                            l = Math.sqrt(x * x + y * y + z * z);
                            // if atom radius of atom to big, cones not visible !!!!
                            if (l < r1 + r2)
                            {
                                s = 0.5;
                            }
                            else
                            {
                                s = ((l - r1 - r2) / 2 + r1) / l;
                            }
                            middleX = atom1.getX() + x * s;
                            middleY = atom1.getY() + y * s;
                            middleZ = atom1.getZ() + z * s;

                            // getting bond radii
                            if ((bond.isUp() || bond.isDown()) && outputType != STICK)
                            {
                                smallerR = smallerBondRadius;
                                biggerR = biggerBondRadius;
                            }
                            else
                            {
                                smallerR = smallerBondRadius;
                                biggerR = smallerBondRadius;
                            }
                            middleR = smallerR + (biggerR - smallerR) * s;

                            // write bond
                            sb.append("    cone {" + JHM.eol);
                            sb.append("       <" + atom1.getX() + ", " + atom1.getY() + ", " + atom1.getZ() + "> " + smallerR + JHM.eol);
                            sb.append("       <" + middleX + ", " + middleY + ", " + middleZ + "> " + middleR + JHM.eol);
                            sb.append("       open" + JHM.eol);
                            sb.append("       texture {" + JHM.eol);
                            color = getAtomColor(atom1);
                            sb.append("         pigment {color rgb <" + color.getRed() / 255.0f + ", " + color.getGreen() / 255.0f + ", " + color.getBlue() / 255.0f + ">}" + JHM.eol);
                            sb.append("         finish {Shiny}" + JHM.eol);
                            sb.append("       }" + JHM.eol);
                            sb.append("    }" + JHM.eol);

                            sb.append("    cone {" + JHM.eol);
                            sb.append("       <" + middleX + ", " + middleY + ", " + middleZ + "> " + middleR + JHM.eol);
                            sb.append("       <" + atom2.getX() + ", " + atom2.getY() + ", " + atom2.getZ() + "> " + biggerR + JHM.eol);
                            sb.append("       open" + JHM.eol);
                            sb.append("       texture {" + JHM.eol);
                            color = getAtomColor(atom2);
                            sb.append("         pigment {color rgb <" + color.getRed() / 255.0f + ", " + color.getGreen() / 255.0f + ", " + color.getBlue() / 255.0f + ">}" + JHM.eol);
                            sb.append("         finish {Shiny}" + JHM.eol);
                            sb.append("       }" + JHM.eol);
                            sb.append("    }" + JHM.eol);

                            sb.append("  }" + JHM.eol);
                        }
                    }
                }
            }

        }
    */

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     * @param sb   Description of the Parameter
     */

    /*    private void writeRings(Molecule mol, StringBuffer sb)
        {
            Atom atom;
            Color color;
            Atom atom1;
            Atom atom2;
            double l;
            double x;
            double y;
            double z;
            // write rings
            Vector sssRings = mol.getSSSR();
            JOERing ring;
            XYZVector center = new XYZVector();
            XYZVector r1v = new XYZVector();
            XYZVector r2v = new XYZVector();
            int itmp[];
            double r;
            boolean allAromatic;
            double rgb_r;
            double rgb_g;
            double rgb_b;

            //sb.append("  // rings:" + JHM.eol);
            for (int i = 0; i < sssRings.size(); i++)
            {
                ring = (JOERing) sssRings.get(i);

                // is ring aromatic ?
                // if yes, get the color of this ring
                allAromatic = true;
                itmp = ring.getAtoms();
                for (int n = 0; n < itmp.length; n++)
                {
                    atom = mol.getAtom(itmp[n]);
                    if (!atom.isAromatic())
                    {
                        allAromatic = false;
                        break;
                    }
                }
                if (!allAromatic)
                {
                    continue;
                }

                // show ring atoms in command line
                sb.append("  // ring "+i+":");
                for (int n = 0; n < itmp.length; n++)
                {
                    sb.append(" "+itmp[n]);
                }
                sb.append(JHM.eol);

                // calculate color and write p orbitals
                ring.findCenterAndNormal(center, r1v, r2v);
                r1v.normalize();
                itmp = ring.getAtoms();
                rgb_r = 0.0;
                rgb_g = 0.0;
                rgb_b = 0.0;
                for (int n = 0; n < itmp.length; n++)
                {
                    atom = mol.getAtom(itmp[n]);
                    color = getAtomColor(atom);
                    rgb_r += color.getRed() / 255.0f;
                    rgb_g += color.getGreen() / 255.0f;
                    rgb_b += color.getBlue() / 255.0f;
                    if (writePorbitals)
                    {
                        sb.append("  cylinder {" + JHM.eol);
                        sb.append("    <" + r1v._vx + ",  " + r1v._vy + ",  " + r1v._vz + ">" + JHM.eol);
                        sb.append("    <" + r2v._vx + ",  " + r2v._vy + ",  " + r2v._vz + ">, 0.05" + JHM.eol);
                        sb.append("    pigment { color rgbt<0, 0.5, 0, 0.7>}" + JHM.eol);
                        sb.append("    finish { ambient 1 diffuse 0 }" + JHM.eol);
                        sb.append("    no_shadow" + JHM.eol);
                        sb.append("    translate <" + atom.getX() + ",  " + atom.getY() + ",  " + atom.getZ() + ">" + JHM.eol);
                        sb.append("  }" + JHM.eol);
                    }
                }
                rgb_r /= itmp.length;
                rgb_g /= itmp.length;
                rgb_b /= itmp.length;

                // calculate radius
                atom1 = mol.getAtom(itmp[0]);
                atom2 = mol.getAtom(itmp[0]);
                x = (atom1.getX() + atom2.getX()) / 2 - center._vx;
                y = (atom1.getY() + atom2.getY()) / 2 - center._vy;
                z = (atom1.getZ() + atom2.getZ()) / 2 - center._vz;
                l = Math.sqrt(x * x + y * y + z * z) * 0.45;

                // calculate rotation vector
                double rrx = -XYZVector.angle(XYZVector.vX, r1v);
                if (r1v._vz < 0.0)
                {
                    rrx *= -1.0;
                }
                double rry = -XYZVector.angle(XYZVector.vY, r1v);
                double rrz = 0.0;

                // write ring
                sb.append("  torus {" + JHM.eol);
                sb.append("    " + l + ", 0.075" + JHM.eol);
                sb.append("    rotate z*" + rry + JHM.eol);
                sb.append("    rotate y*" + rrx + JHM.eol);
                sb.append("    translate <" + center._vx + ",  " + center._vy + ",  " + center._vz + ">" + JHM.eol);
                sb.append("    pigment {color rgb <" + rgb_r + ", " + rgb_g + ", " + rgb_b + ">}" + JHM.eol);
                sb.append("    finish {Shiny}" + JHM.eol);
                sb.append("  }" + JHM.eol);
            }
        }*/
    public boolean skipReaderEntry() throws IOException
    {
        return true;
    }

    public void useAtomPropertyColoring(Molecule mol, String property)
    {
        PairData pairData = mol.getData(property);

        if (pairData == null)
        {
            logger.error("Can't get atom properties for atom coloring in " +
                this.getClass().getName());
        }

        //      System.out.println(""+JOEHelper.hasInterface(genericData, "joelib2.molecule.types.AtomProperties"));
        //      System.out.println(""+(genericData instanceof AtomDynamicResult));
        //      System.out.println(""+(genericData instanceof PairData));
        if (pairData instanceof AtomProperties)
        {
            data = (AtomProperties) pairData;
            minDataValue = Double.MAX_VALUE;
            maxDataValue = -Double.MAX_VALUE;

            double value;

            for (int i = 1; i <= mol.getAtomsSize(); i++)
            {
                value = data.getDoubleValue(i);

                if (value > maxDataValue)
                {
                    maxDataValue = value;
                }

                if (value < minDataValue)
                {
                    minDataValue = value;
                }

                //          System.out.println(""+i+": "+value);
            }

            usePropertyColoring = true;
        }
        else
        {
            logger.error("Data for atom coloring has wrong format in " +
                this.getClass().getName());
        }
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public boolean write(Molecule mol) throws IOException
    {
        return write(mol, null);
    }

    /**
     *  Description of the Method
     *
     * @param mol              Description of the Parameter
     * @param title            Description of the Parameter
     * @return                 Description of the Return Value
     * @exception IOException  Description of the Exception
     */
    public boolean write(Molecule mol, String title) throws IOException
    {
        //        useAtomPropertyColoring(mol, "A_QTOT");
        //        useAtomPropertyColoring(mol, "A_POLARIZABILITY");
        StringBuffer matlabMolecule = new StringBuffer((mol.getAtomsSize() *
                    100) + (mol.getBondsSize() * 100));

        // calculate molecule centrum new everytime
        mx = my = mz = 0.0;
        minX = minY = minZ = Double.MAX_VALUE;
        maxX = maxY = maxZ = -Double.MAX_VALUE;

        // get molecule information
        write2Buffer(mol, matlabMolecule);

        // write moelcule informations
        ps.println("figure;");
        ps.println("hold on;");
        ps.println();

        if (outputType == BALL_AND_STICK)
        {
            ps.println("% atom resize factor");
            ps.println("atomResizeFactor = " + atomResizeFactor + ";");
        }

        //String isComment = "";
        //if (moleculeCounter > 1)
        //{
        //    isComment = "%";
        //}
        ps.println("title('Molecule " + moleculeCounter + ": " +
            mol.getTitle() + "')");

        ps.println("[smx smy smz]=sphere (15);");
        ps.println();
        ps.println("% colormap");
        ps.println("colormap( [");

        float[] rgb;

        for (int i = 0; i < colormapRGB.size(); i++)
        {
            rgb = (float[]) colormapRGB.get(i);
            ps.print(rgb[0]);
            ps.print(',');
            ps.print(rgb[1]);
            ps.print(',');
            ps.print(rgb[2]);

            if (i < (colormapRGB.size() - 1))
            {
                ps.println("; ... ");
            }
        }

        ps.println("]);");

        ps.println(matlabMolecule.toString());

        ps.println("shading interp;");
        ps.println("lightangle(-45,30);");
        ps.println("% switch axes off");
        ps.println("h=gca; set(h,'Visible', 'off')");

        moleculeCounter++;

        return (true);
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public boolean writeable()
    {
        return true;
    }

    private int add2Colormap(int r, int g, int b)
    {
        StringBuffer sb = new StringBuffer(20);
        sb.append(r);
        sb.append('_');
        sb.append(g);
        sb.append('_');
        sb.append(b);

        String color = sb.toString();

        if (!colormap.containsKey(color))
        {
            Integer integer = new Integer(colormap.size() + 1);
            colormap.put(color, integer);
            colormapRGB.add(
                new float[]
                {
                    ((float) r) / 255.0f, ((float) g) / 255.0f,
                    ((float) b) / 255.0f,
                });

            return integer.intValue();
        }
        else
        {
            return ((Integer) colormap.get(color)).intValue();
        }
    }

    private Color getAtomColor(Atom atom)
    {
        if (usePropertyColoring)
        {
            double delta = maxDataValue - minDataValue;
            float val = (float) ((data.getDoubleValue(atom.getIndex()) -
                        minDataValue) / delta);
            float r = (((maxColor.getRed() - minColor.getRed()) * val) +
                    minColor.getRed()) / 255.0f;
            float g = (((maxColor.getGreen() - minColor.getGreen()) * val) +
                    minColor.getGreen()) / 255.0f;
            float b = (((maxColor.getBlue() - minColor.getBlue()) * val) +
                    minColor.getBlue()) / 255.0f;

            //        System.out.println(""+atom.getIdx()+": "+data.getDoubleValue(atom.getIdx())+": "+val);
            //        System.out.println("rgb:"+r+" "+g+" "+b);
            return new Color(r, g, b);
        }
        else
        {
            int atomNum = atom.getAtomicNumber();

            return etab.getColor(atomNum);
        }
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     * @param sb   Description of the Parameter
     */
    private void write2Buffer(Molecule mol, StringBuffer sb)
    {
        // write atoms
        writeAtoms(mol, sb);

        //        if (outputType != SPHERE)
        //        {
        //            // write bonds
        //
        //            writeBonds(mol, sb);
        //            if (writeAromaticRings)
        //            {
        //                // write rings
        //                writeRings(mol, sb);
        //            }
        //        }
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     * @param sb   Description of the Parameter
     */
    private void writeAtoms(Molecule mol, StringBuffer sb)
    {
        Atom atom;
        AtomIterator ait = mol.atomIterator();
        double x;
        double y;
        double z;
        double radius;
        Color color;

        while (ait.hasNext())
        {
            atom = ait.nextAtom();

            int atomNum = atom.getAtomicNumber();
            x = atom.get3Dx();
            y = atom.get3Dy();
            z = atom.get3Dz();
            mx += x;
            my += y;
            mz += z;

            if (x > maxX)
            {
                maxX = x;
            }

            if (x < minX)
            {
                minX = x;
            }

            if (y > maxY)
            {
                maxY = y;
            }

            if (y < minY)
            {
                minY = y;
            }

            if (z > maxZ)
            {
                maxZ = z;
            }

            if (z < minZ)
            {
                minZ = z;
            }

            if (outputType != STICK)
            {
                radius = etab.correctedVdwRad(atomNum);
            }
            else
            {
                radius = smallerBondRadius;
            }

            sb.append("  % atom " + atom.getIndex() + ": " +
                etab.getSymbol(atom.getAtomicNumber()) + HelperMethods.eol);
            color = getAtomColor(atom);

            int cindex = add2Colormap(color.getRed(), color.getGreen(),
                    color.getBlue());

            if (outputType != BALL_AND_STICK)
            {
                sb.append("  radius=" + radius + ";");
            }
            else
            {
                sb.append("  radius=" + radius + "*atomResizeFactor;");
            }

            sb.append("  surface(");
            sb.append(" (smx.*radius)+(" + x + "),  (smy.*radius)+(" + y +
                "), (smz.*radius)+(" + z + "), repmat(" + cindex +
                ", size(smx,1), size(smx,2))");

            /*            sb.append("    texture {" + JHM.eol);
                        color = getAtomColor(atom);
                        sb.append("      pigment {color rgb <" + color.getRed() / 255.0f + ", " + color.getGreen() / 255.0f + ", " + color.getBlue() / 255.0f + ">}" + JHM.eol);
                        sb.append("      finish {Shiny}" + JHM.eol);
                        sb.append("    }" + JHM.eol);*/
            sb.append("  );" + HelperMethods.eol);
        }

        mx = mx / (double) mol.getAtomsSize();
        my = my / (double) mol.getAtomsSize();
        mz = mz / (double) mol.getAtomsSize();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
