///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: POVRay.java,v $
//  Purpose:  Reader/Writer for SDF files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:34 $
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

import joelib2.feature.FeatureHelper;

import joelib2.feature.types.atomlabel.AtomInAromaticSystem;

import joelib2.io.MoleculeFileIO;

import joelib2.math.BasicVector3D;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;
import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicAtomPropertyColoring;

import joelib2.ring.Ring;

import joelib2.util.HelperMethods;

import joelib2.util.iterator.AtomIterator;
import joelib2.util.iterator.NbrAtomIterator;

import wsi.ra.tool.BasicPropertyHolder;

import java.awt.Color;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;

import org.apache.log4j.Category;


/**
 * Writer for Persistance Of Vision Raytracer (POVRay) files.
 *
 * @.author     wegnerj
 * @.wikipedia  POV-Ray
 * @.wikipedia  File format
 * @.license GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:34 $
 * @.cite povray
 */
public class POVRay implements MoleculeFileIO
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     * Description of the Field
     */
    public final static int BALL_AND_STICK = 0;
    public final static String BALL_AND_STICK_S = "ball_and_stick";

    /**
     * Description of the Field
     */
    public final static int SPHERE = 1;
    public final static String SPHERE_S = "sphere";

    /**
     * Description of the Field
     */
    public final static int STICK = 2;
    public final static String STICK_S = "stick";

    /**
     * Description of the Field
     */
    public final static int DEFAULT_OUTPUT_TYPE = BALL_AND_STICK;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.POVRay");
    private final static String version = "$Revision: 1.10 $";

    /**
     *  Description of the Field
     */
    private final static String description =
        "Persistence Of Vision (POV) Ray Tracer";
    private final static String[] extensions = new String[]{"pov"};

    //~ Instance fields ////////////////////////////////////////////////////////

    // variables for property coloring
    private BasicAtomPropertyColoring aPropColoring =
        new BasicAtomPropertyColoring();
    private String atomProperty2Use = null;
    private double atomResizeFactor = 0.25;
    private double biggerBondRadius = 0.3;
    private BasicElementHolder etab = BasicElementHolder.instance();
    private double maxX;
    private double maxY;
    private double maxZ;
    private double minX;
    private double minY;
    private double minZ;
    private int moleculeCounter = 1;
    private double mx;
    private double my;
    private double mz;
    private int outputType = DEFAULT_OUTPUT_TYPE;

    //    private LineNumberReader lnr;
    private PrintfStream ps;
    private double smallerBondRadius = 0.175;

    //    private AtomProperties data=null;
    //    private boolean dataInitialised=false;
    //    private double minDataValue;
    //    private double maxDataValue;
    //    private Color minColor = new Color(0.0f,0.0f,1.0f);
    //    private Color maxColor = new Color(1.0f,0.0f,0.0f);
    private boolean usePropertyColoring = false;
    private boolean writeAromaticRings = true;
    private boolean writePorbitals = false;

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

        ps.println("// Creator: " + this.getClass().getName() + " " + version);
        ps.println("// Author: Joerg Kurt Wegner");
        ps.println("// Version: POV-Ray 3.1" + HelperMethods.eol);

        ps.println("#include \"shapes.inc\"");
        ps.println("#include \"colors.inc\"");
        ps.println("#include \"textures.inc\"" + HelperMethods.eol);

        ps.println("// brighten up colors");
        ps.println("global_settings {assumed_gamma 1.4}");
        ps.println("// set background color");
        ps.println("background {colour<0.0, 0.0, 0.3>}" + HelperMethods.eol);

        initProperties();
    }

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String inputDescription()
    {
        return description;
    }

    /**
     *  Description of the Method
     *
     * @return   Description of the Return Value
     */
    public String[] inputFileExtensions()
    {
        return extensions;
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
            "Reading POVRay data as String representation is not implemented yet !!!");

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

    public boolean skipReaderEntry() throws IOException
    {
        return true;
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
        if ((atomProperty2Use != null) && usePropertyColoring)
        {
            //useAtomPropertyColoring(mol, atomProperty2Use);
            aPropColoring.useAtomPropertyColoring(mol, atomProperty2Use);
        }
        else
        {
            aPropColoring.usePlainColoring();
        }

        //        useAtomPropertyColoring(mol, "A_QTOT");
        //        useAtomPropertyColoring(mol, "A_POLARIZABILITY");
        StringBuffer povMolecule = new StringBuffer((mol.getAtomsSize() * 100) +
                (mol.getBondsSize() * 100));

        // calculate molecule centrum new everytime
        mx = my = mz = 0.0;
        minX = minY = minZ = Double.MAX_VALUE;
        maxX = maxY = maxZ = -Double.MAX_VALUE;

        // get molecule information
        write2Buffer(mol, povMolecule);

        // write moelcule informations
        ps.println("// minimum values");
        ps.println("#declare minX = " + minX + ";");
        ps.println("#declare minY = " + minY + ";");
        ps.println("#declare minZ = " + minZ + ";");
        ps.println("// maximum values");
        ps.println("#declare maxX = " + maxX + ";");
        ps.println("#declare maxY = " + maxY + ";");
        ps.println("#declare maxZ = " + maxZ + ";");
        ps.println("// delta values");
        ps.println("#declare deltaX = " + Math.abs(maxX - minX) + ";");
        ps.println("#declare deltaY = " + Math.abs(maxY - minY) + ";");
        ps.println("#declare deltaZ = " + Math.abs(maxZ - minZ) + ";");
        ps.println("// average values");
        ps.println("#declare mx = " + mx + ";");
        ps.println("#declare my = " + my + ";");
        ps.println("#declare mz = " + mz + ";");
        ps.println();

        if (outputType == BALL_AND_STICK)
        {
            ps.println("// atom resize factor");
            ps.println("#declare atomResizeFactor = " + atomResizeFactor + ";");
        }

        // use camera and light only for the first molecule
        String isComment = "";

        if (moleculeCounter > 1)
        {
            isComment = "//";
        }

        ps.println("// Camera" + HelperMethods.eol + "camera {");
        ps.println(isComment +
            "    location <mx, my, mz-(max(deltaX,deltaY)*1.4)> // use maximum window with a resize factor of 1.4");
        ps.println(isComment + "    direction <0.0, 0.0, 1.0>");
        ps.println(isComment + "    look_at <mx, my, 1.0>" + HelperMethods.eol +
            "}" + HelperMethods.eol);

        ps.println("// Light");
        ps.println(isComment + " light_source {<" + ((mx < 0.0) ? "-" : "") +
            "mx*2, " + ((my < 0.0) ? "-" : "") +
            "my*2, mz-max(deltaX,deltaY)>");
        ps.println(isComment + " colour White");
        ps.println(isComment + "}" + HelperMethods.eol);

        // define molecule in povray
        ps.println("// Molecule " + moleculeCounter + ":" + mol.getTitle());
        ps.println("#declare");
        ps.println("Molecule" + moleculeCounter + " = union{");
        ps.println(povMolecule.toString());
        ps.println("}");

        // draw molecule in povray
        ps.println();
        ps.println("object { Molecule" + moleculeCounter + HelperMethods.eol);

        //        ps.println("  translate <-mx,-my,-mz>"+JHM.eol);
        //        ps.println("  rotate y*clock*10"+JHM.eol);
        //        ps.println("  translate <mx,my,mz>"+JHM.eol);
        ps.println("}" + HelperMethods.eol);

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

    //public void useAtomPropertyColoring(Molecule mol, String property)
    //{
    //  aPropColoring.useAtomPropertyColoring(mol, property);

    /*      DescResult result=null;
          try
            {
                    result= DescriptorHelper.instance().descFromMol(mol,property);
            }
            catch (DescriptorException e)
            {
                    logger.error(e.toString());
                    usePropertyColoring=false;
            }


          if(result==null)logger.error("Can't get atom property "+property+" for atom coloring in "+this.getClass().getName());
          if(JOEHelper.hasInterface(result, "AtomProperties"))
          {
            dataInitialised=true;
            data = (AtomProperties)result;
            minDataValue=Double.MAX_VALUE;
            maxDataValue=-Double.MAX_VALUE;
            double value;
            for(int i=1;i<=mol.numAtoms();i++)
            {
              value = data.getDoubleValue(i);
              if(value>maxDataValue)maxDataValue=value;
              if(value<minDataValue)minDataValue=value;
    //          System.out.println(""+i+": "+value);
            }

            usePropertyColoring=true;
          }
          else
          {
            logger.error("Data for atom coloring has wrong format in "+this.getClass().getName());
          }*/

    //}
    //private Color getAtomColor(Atom atom)
    //{
    //  return aPropColoring.getAtomColor(atom);

    /*      if(usePropertyColoring)
          {
            double delta = maxDataValue-minDataValue;
            //System.out.println("data:"+data);

            float val = (float) ((data.getDoubleValue(atom.getIdx())-minDataValue)/delta);
            float r= ((maxColor.getRed()-minColor.getRed())*val +minColor.getRed())/255.0f;
            float g=((maxColor.getGreen()-minColor.getGreen())*val +minColor.getGreen())/255.0f;
            float b=((maxColor.getBlue()-minColor.getBlue())*val +minColor.getBlue())/255.0f;
    //        System.out.println(""+atom.getIdx()+": "+data.getDoubleValue(atom.getIdx())+": "+val);
    //        System.out.println("rgb:"+r+" "+g+" "+b);
            return new Color(r,g,b);
          }
          else
          {
            int atomNum = atom.getAtomicNum();
            return etab.getColor(atomNum);
          }*/

    //}

    /**
     *  Description of the Method
     *
     * @exception  IOException  Description of the Exception
     */
    private void initProperties()
    {
        String value;

        value = BasicPropertyHolder.instance().getProperty(this, "output");

        if (value == null)
        {
            outputType = DEFAULT_OUTPUT_TYPE;
        }
        else if (value.equalsIgnoreCase(STICK_S))
        {
            outputType = STICK;
        }
        else if (value.equalsIgnoreCase(SPHERE_S))
        {
            outputType = SPHERE;
        }
        else if (value.equalsIgnoreCase(BALL_AND_STICK_S))
        {
            outputType = BALL_AND_STICK;
        }
        else
        {
            logger.error("Use output type :" + STICK_S + ", " + SPHERE_S +
                " and " + BALL_AND_STICK_S);
            outputType = DEFAULT_OUTPUT_TYPE;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "atomPropertyColoring");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            usePropertyColoring = true;
        }
        else
        {
            usePropertyColoring = false;
        }

        atomProperty2Use = null;
        value = BasicPropertyHolder.instance().getProperty(this,
                "atomProperty");

        List atomPropDescs = FeatureHelper.instance().getAtomLabelFeatures();
        int s = atomPropDescs.size();

        for (int ii = 0; ii < s; ii++)
        {
            if (value == null)
            {
                break;
            }

            if (value.equalsIgnoreCase((String) atomPropDescs.get(ii)))
            {
                atomProperty2Use = value;
            }

            //System.out.println(atomPropDescs.get(ii));
        }

        if ((atomProperty2Use == null) && usePropertyColoring)
        {
            logger.warn("atomProperty=" + value +
                " is not a valid atom property. Setting to Gasteiger_Marsili");

            StringBuffer sb = new StringBuffer();
            sb.append("Or use:");

            for (int ii = 0; ii < s; ii++)
            {
                sb.append(atomPropDescs.get(ii));
                sb.append(" ");
            }

            logger.warn(sb.toString());
            atomProperty2Use = "Gasteiger_Marsili";
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

        if (outputType != SPHERE)
        {
            // write bonds
            writeBonds(mol, sb);

            if (writeAromaticRings)
            {
                // write rings
                writeRings(mol, sb);
            }
        }
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

            sb.append("  // atom " + atom.getIndex() + ": " +
                etab.getSymbol(atom.getAtomicNumber()) + HelperMethods.eol);
            sb.append("  sphere {" + HelperMethods.eol);

            if (outputType != BALL_AND_STICK)
            {
                sb.append("    <" + x + ", " + y + ", " + z + "> " + radius +
                    HelperMethods.eol);
            }
            else
            {
                sb.append("    <" + x + ", " + y + ", " + z + "> " + radius +
                    "*atomResizeFactor" + HelperMethods.eol);
            }

            sb.append("    texture {" + HelperMethods.eol);

            //color = getAtomColor(atom);
            color = aPropColoring.getAtomColor(atom);
            sb.append("      pigment {color rgb <" + (color.getRed() / 255.0f) +
                ", " + (color.getGreen() / 255.0f) + ", " +
                (color.getBlue() / 255.0f) + ">}" + HelperMethods.eol);
            sb.append("      finish {Shiny}" + HelperMethods.eol);
            sb.append("    }" + HelperMethods.eol);
            sb.append("  }" + HelperMethods.eol);
        }

        mx = mx / (double) mol.getAtomsSize();
        my = my / (double) mol.getAtomsSize();
        mz = mz / (double) mol.getAtomsSize();
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     * @param sb   Description of the Parameter
     */
    private void writeBonds(Molecule mol, StringBuffer sb)
    {
        Bond bond;
        double x3D;
        double y3D;
        double z3D;
        Color color;
        double middleX;
        double middleY;
        double middleZ;
        double vdwRadius1;
        double vdwRadius2;
        double distance;
        double scaleFactor;
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

                if (atom.getIndex() < nbr.getIndex())
                {
                    bond = nait.actualBond();

                    //            while (bit.hasNext())
                    //            {
                    //                bond = bit.nextBond();
                    atom1 = atom;
                    atom2 = nbr;

                    if (atom1.getIndex() < atom2.getIndex())
                    {
                        sb.append("  // bond " + bond.getIndex() + ": " +
                            etab.getSymbol(atom1.getAtomicNumber()) + "(" +
                            atom1.getIndex() + ")");
                        sb.append(bond.toString());
                        sb.append(etab.getSymbol(atom2.getAtomicNumber()) +
                            "(" + atom2.getIndex() + ")" + HelperMethods.eol);
                        sb.append("  union {" + HelperMethods.eol);

                        vdwRadius1 =
                            BasicElementHolder.instance().correctedVdwRad(atom1
                                .getAtomicNumber()) * atomResizeFactor;
                        vdwRadius2 =
                            BasicElementHolder.instance().correctedVdwRad(atom2
                                .getAtomicNumber()) * atomResizeFactor;
                        x3D = atom2.get3Dx() - atom1.get3Dx();
                        y3D = atom2.get3Dy() - atom1.get3Dy();
                        z3D = atom2.get3Dz() - atom1.get3Dz();
                        distance = Math.sqrt((x3D * x3D) + (y3D * y3D) +
                                (z3D * z3D));

                        // if atom radius of atom to big, cones not visible !!!!
                        if (distance < (vdwRadius1 + vdwRadius2))
                        {
                            scaleFactor = 0.5;
                        }
                        else
                        {
                            scaleFactor = (((distance - vdwRadius1 -
                                            vdwRadius2) / 2) + vdwRadius1) /
                                distance;
                        }

                        middleX = atom1.get3Dx() + (x3D * scaleFactor);
                        middleY = atom1.get3Dy() + (y3D * scaleFactor);
                        middleZ = atom1.get3Dz() + (z3D * scaleFactor);

                        // getting bond radii
                        if ((bond.isUp() || bond.isDown()) &&
                                (outputType != STICK))
                        {
                            smallerR = smallerBondRadius;
                            biggerR = biggerBondRadius;
                        }
                        else
                        {
                            smallerR = smallerBondRadius;
                            biggerR = smallerBondRadius;
                        }

                        middleR = smallerR +
                            ((biggerR - smallerR) * scaleFactor);

                        // write bond
                        sb.append("    cone {" + HelperMethods.eol);
                        sb.append("       <" + atom1.get3Dx() + ", " +
                            atom1.get3Dy() + ", " + atom1.get3Dz() + "> " +
                            smallerR + HelperMethods.eol);
                        sb.append("       <" + middleX + ", " + middleY + ", " +
                            middleZ + "> " + middleR + HelperMethods.eol);
                        sb.append("       open" + HelperMethods.eol);
                        sb.append("       texture {" + HelperMethods.eol);

                        //color = getAtomColor(atom1);
                        color = aPropColoring.getAtomColor(atom1);
                        sb.append("         pigment {color rgb <" +
                            (color.getRed() / 255.0f) + ", " +
                            (color.getGreen() / 255.0f) + ", " +
                            (color.getBlue() / 255.0f) + ">}" +
                            HelperMethods.eol);
                        sb.append("         finish {Shiny}" +
                            HelperMethods.eol);
                        sb.append("       }" + HelperMethods.eol);
                        sb.append("    }" + HelperMethods.eol);

                        sb.append("    cone {" + HelperMethods.eol);
                        sb.append("       <" + middleX + ", " + middleY + ", " +
                            middleZ + "> " + middleR + HelperMethods.eol);
                        sb.append("       <" + atom2.get3Dx() + ", " +
                            atom2.get3Dy() + ", " + atom2.get3Dz() + "> " +
                            biggerR + HelperMethods.eol);
                        sb.append("       open" + HelperMethods.eol);
                        sb.append("       texture {" + HelperMethods.eol);

                        //color = getAtomColor(atom2);
                        color = aPropColoring.getAtomColor(atom2);
                        sb.append("         pigment {color rgb <" +
                            (color.getRed() / 255.0f) + ", " +
                            (color.getGreen() / 255.0f) + ", " +
                            (color.getBlue() / 255.0f) + ">}" +
                            HelperMethods.eol);
                        sb.append("         finish {Shiny}" +
                            HelperMethods.eol);
                        sb.append("       }" + HelperMethods.eol);
                        sb.append("    }" + HelperMethods.eol);

                        sb.append("  }" + HelperMethods.eol);
                    }
                }
            }
        }
    }

    /**
     * Description of the Method
     *
     * @param mol  Description of the Parameter
     * @param sb   Description of the Parameter
     */
    private void writeRings(Molecule mol, StringBuffer sb)
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
        List sssRings = mol.getSSSR();
        Ring ring;
        BasicVector3D center = new BasicVector3D();
        BasicVector3D r1v = new BasicVector3D();
        BasicVector3D r2v = new BasicVector3D();
        int[] itmp;
        boolean allAromatic;
        double rgb_r;
        double rgb_g;
        double rgb_b;

        //sb.append("  // rings:" + JHM.eol);
        for (int i = 0; i < sssRings.size(); i++)
        {
            ring = (Ring) sssRings.get(i);

            // is ring aromatic ?
            // if yes, get the color of this ring
            allAromatic = true;
            itmp = ring.getAtomIndices();

            for (int n = 0; n < itmp.length; n++)
            {
                atom = mol.getAtom(itmp[n]);

                if (!AtomInAromaticSystem.isValue(atom))
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
            sb.append("  // ring " + i + ":");

            for (int n = 0; n < itmp.length; n++)
            {
                sb.append(" " + itmp[n]);
            }

            sb.append(HelperMethods.eol);

            // calculate color and write p orbitals
            ring.findCenterAndNormal(center, r1v, r2v);
            r1v.normalize();
            itmp = ring.getAtomIndices();
            rgb_r = 0.0;
            rgb_g = 0.0;
            rgb_b = 0.0;

            for (int n = 0; n < itmp.length; n++)
            {
                atom = mol.getAtom(itmp[n]);

                //color = getAtomColor(atom);
                color = aPropColoring.getAtomColor(atom);
                rgb_r += (color.getRed() / 255.0f);
                rgb_g += (color.getGreen() / 255.0f);
                rgb_b += (color.getBlue() / 255.0f);

                if (writePorbitals)
                {
                    sb.append("  cylinder {" + HelperMethods.eol);
                    sb.append("    <" + r1v.x3D + ",  " + r1v.y3D + ",  " +
                        r1v.z3D + ">" + HelperMethods.eol);
                    sb.append("    <" + r2v.x3D + ",  " + r2v.y3D + ",  " +
                        r2v.z3D + ">, 0.05" + HelperMethods.eol);
                    sb.append("    pigment { color rgbt<0, 0.5, 0, 0.7>}" +
                        HelperMethods.eol);
                    sb.append("    finish { ambient 1 diffuse 0 }" +
                        HelperMethods.eol);
                    sb.append("    no_shadow" + HelperMethods.eol);
                    sb.append("    translate <" + atom.get3Dx() + ",  " +
                        atom.get3Dy() + ",  " + atom.get3Dz() + ">" +
                        HelperMethods.eol);
                    sb.append("  }" + HelperMethods.eol);
                }
            }

            rgb_r /= itmp.length;
            rgb_g /= itmp.length;
            rgb_b /= itmp.length;

            // calculate radius
            atom1 = mol.getAtom(itmp[0]);
            atom2 = mol.getAtom(itmp[0]);
            x = ((atom1.get3Dx() + atom2.get3Dx()) / 2) - center.x3D;
            y = ((atom1.get3Dy() + atom2.get3Dy()) / 2) - center.y3D;
            z = ((atom1.get3Dz() + atom2.get3Dz()) / 2) - center.z3D;
            l = Math.sqrt((x * x) + (y * y) + (z * z)) * 0.45;

            // calculate rotation vector
            double rrx = -BasicVector3D.angle(BasicVector3D.XAXIS, r1v);

            if (r1v.z3D < 0.0)
            {
                rrx *= -1.0;
            }

            double rry = -BasicVector3D.angle(BasicVector3D.YAXIS, r1v);

            // write ring
            sb.append("  torus {" + HelperMethods.eol);
            sb.append("    " + l + ", 0.075" + HelperMethods.eol);
            sb.append("    rotate z*" + rry + HelperMethods.eol);
            sb.append("    rotate y*" + rrx + HelperMethods.eol);
            sb.append("    translate <" + center.x3D + ",  " + center.y3D +
                ",  " + center.z3D + ">" + HelperMethods.eol);
            sb.append("    pigment {color rgb <" + rgb_r + ", " + rgb_g + ", " +
                rgb_b + ">}" + HelperMethods.eol);
            sb.append("    finish {Shiny}" + HelperMethods.eol);
            sb.append("  }" + HelperMethods.eol);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
