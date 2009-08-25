///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: CMLSymmetryWriter.java,v $
//Purpose:  Chemical Markup Language.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu,
//                      egonw@sci.kun.nl, wegner@users.sourceforge.net
//Version:  $Revision: 1.9 $
//                      $Date: 2005/02/17 16:48:35 $
//                      $Author: wegner $
//
//Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.io.types.cml;

import joelib2.feature.result.DoubleArrayResult;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;

import joelib2.math.symmetry.PointGroup;
import joelib2.math.symmetry.Symmetry;
import joelib2.math.symmetry.SymmetryElement;
import joelib2.math.symmetry.SymmetryException;

import joelib2.molecule.Molecule;

import joelib2.util.HelperMethods;

import java.io.PrintStream;

import org.apache.log4j.Category;


/**
 * Helper class for a CML molecule symmetry writer.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:35 $
 * @.cite rr99b
 * @.cite mr01
 * @.cite gmrw01
 * @.cite wil01
 * @.cite mr03
 * @.cite mrww04
 */
public class CMLSymmetryWriter
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "joelib2.io.types.cml.CMLSymmetryWriter");
    private static BasicIOType cml = BasicIOTypeHolder.instance().getIOType(
            "CML");

    //~ Methods ////////////////////////////////////////////////////////////////

    public static void writeAxes(PrintStream ps, String type,
        SymmetryElement[] symElements)
    {
        if (symElements == null)
        {
            return;
        }

        for (int i = 0; i < symElements.length; i++)
        {
            ps.print("    <symmetryElement type=\"" + type + "\" >" +
                HelperMethods.eol);

            double[] tmp = new double[3];
            tmp[0] = symElements[i].distance * symElements[i].normal[0];
            tmp[1] = symElements[i].distance * symElements[i].normal[1];
            tmp[2] = symElements[i].distance * symElements[i].normal[2];
            writeDoubleArray(ps, "position", tmp);
            writeDoubleArray(ps, "direction", symElements[i].direction);
            writeAxisTypeScalar(ps, "axisType", symElements[i].order);

            ps.print("    </symmetryElement>" + HelperMethods.eol);
        }
    }

    public static void writeAxisTypeScalar(PrintStream ps, String title,
        int value)
    {
        ps.print("      <scalar dataType=\"xsd:string\" title=\"" + title +
            "\" >");
        ps.print('C');
        ps.print(value);
        ps.print("</scalar>" + HelperMethods.eol);
    }

    public static void writeDoubleArray(PrintStream ps, String title,
        double[] array)
    {
        DoubleArrayResult arrayResult = new DoubleArrayResult();
        arrayResult.setDoubleArray(array);

        ps.print("      <array dataType=\"xsd:double\" title=\"" + title +
            "\" >");
        ps.print(arrayResult.toString(cml));
        ps.print("</array>" + HelperMethods.eol);
    }

    public static void writeDoubleScalar(PrintStream ps, String title,
        double value)
    {
        ps.print("      <scalar dataType=\"xsd:double\" title=\"" + title +
            "\" >");
        ps.print(value);
        ps.print("</scalar>" + HelperMethods.eol);
    }

    public static void writeInversionCenter(PrintStream ps,
        SymmetryElement symElement)
    {
        if (symElement == null)
        {
            return;
        }

        ps.print("    <symmetryElement type=\"inversionCenter\" >" +
            HelperMethods.eol);

        double[] tmp = new double[3];
        tmp[0] = symElement.distance * symElement.normal[0];
        tmp[1] = symElement.distance * symElement.normal[1];
        tmp[2] = symElement.distance * symElement.normal[2];
        writeDoubleArray(ps, "position", tmp);

        ps.print("    </symmetryElement>" + HelperMethods.eol);
    }

    public static void writePlanes(PrintStream ps,
        SymmetryElement[] symElements)
    {
        if (symElements == null)
        {
            return;
        }

        for (int i = 0; i < symElements.length; i++)
        {
            ps.print("    <symmetryElement type=\"mirrorPlane\" >" +
                HelperMethods.eol);

            writeDoubleArray(ps, "direction", symElements[i].normal);
            writeDoubleScalar(ps, "distance", symElements[i].distance);

            ps.print("    </symmetryElement>" + HelperMethods.eol);
        }
    }

    public static void writeSymmetry(PrintStream ps, Molecule mol)
    {
        Symmetry symmetry = new Symmetry();
        symmetry.readCoordinates(mol);

        try
        {
            symmetry.findSymmetryElements();
        }
        catch (SymmetryException e)
        {
            logger.error(e.getMessage());
        }

        if (symmetry.getBadOptimization())
        {
            logger.warn(
                "Refinement of some symmetry elements was terminated before convergence was reached.\n" +
                "Some symmetry elements may remain unidentified.");
        }

        PointGroup pointGroup = symmetry.identifyPointGroup();
        SymmetryElement symElement;
        SymmetryElement[] symElements;

        if (pointGroup != null)
        {
            ps.print("  <symmetry pointGroup=\"" + pointGroup.getGroupName() +
                "\" elements=\"" + pointGroup.getSymmetryCode().trim() +
                "\" id=\"s1\">" + HelperMethods.eol);

            symElement = symmetry.getInversionCenter();

            if (symElement != null)
            {
                writeInversionCenter(ps, symElement);
            }

            symElements = symmetry.getAxes();

            if (symElements != null)
            {
                writeAxes(ps, "normalAxis", symElements);
            }

            symElements = symmetry.getImproperAxes();

            if (symElements != null)
            {
                writeAxes(ps, "improperAxis", symElements);
            }

            symElements = symmetry.getPlanes();

            if (symElements != null)
            {
                writePlanes(ps, symElements);
            }

            ps.print("  </symmetry>" + HelperMethods.eol);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
