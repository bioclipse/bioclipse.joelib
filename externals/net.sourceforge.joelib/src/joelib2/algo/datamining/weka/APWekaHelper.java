///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: APWekaHelper.java,v $
//  Purpose:  Atom pair descriptor helper for Weka data mining algorithms.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Nikolas H. Fechner
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:28 $
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
package joelib2.algo.datamining.weka;

import weka.core.Attribute;

import java.io.File;
import java.io.FileWriter;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


/**
 * Atom pair descriptor helper for Weka data mining algorithms.
 *
 * @.author Nikolas H. Fechner
 * @.wikipedia QSAR
 * @.wikipedia Data mining
 * @.license    GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:28 $
 */
public class APWekaHelper
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public static String[] getAttributeNames(Hashtable H)
    {
        int n = H.size();
        Enumeration e = H.keys();
        String[] names = new String[n];
        int i = 0;
        String s = "";

        while (e.hasMoreElements())
        {
            Object o = e.nextElement();

            //            if (!(o instanceof String))
            //            {
            //                s = ((SmoothedAtomTypePair) o).toString();
            //            }
            //            else
            //            {
            s = (String) o;

            //            }
            names[i] = s;
            i++;
        }

        return names;
    }

    public static double[][] getAttributeValues(Hashtable H)
    {
        int x = H.size();
        Enumeration e = H.keys();

        //SmoothedAtomTypePair satp = (SmoothedAtomTypePair)e.nextElement();
        Object o = e.nextElement();
        Vector v = (Vector) H.get(o);
        int y = v.size();
        double[][] values = new double[y][x];
        int i = 0;
        e = H.keys();

        while (e.hasMoreElements())
        {
            //satp = (SmoothedAtomTypePair)e.nextElement();
            o = e.nextElement();

            int j = 0;
            v = (Vector) H.get(o);

            for (j = 0; j < y; j++)
            {
                double d = ((Double) v.get(j)).doubleValue();
                values[j][i] = d;
            }

            i++;
        }

        return values;
    }

    public static List getDistinct(List list)
    {
        List x = new Vector();
        Iterator i = list.iterator();

        while (i.hasNext())
        {
            Object o = i.next();

            if (!x.contains(o))
            {
                x.add(o);
            }
        }

        return x;
    }

    /*public static Hashtable getInstances(JOEMolVector molecules, SmoothedTAP TAP, Map properties) throws Exception
    {
            Hashtable H = new Hashtable();
            for(int i = 0; i < molecules.getSize(); i++)
            {
                    Molecule mol = molecules.getMol(i);
                    SmoothedAtomPairResult sapr = (SmoothedAtomPairResult)TAP.calculate(mol, properties);
                    Hashtable temp = sapr.getNameValueTable();
                    Enumeration e = H.keys();
                    while(e.hasMoreElements())
                    {
                            SmoothedAtomTypePair satp = (SmoothedAtomTypePair)e.nextElement();
                            if(temp.containsKey(satp))
                            {
                                    ((Vector)H.get(satp)).add(temp.get(satp));
                                    temp.remove(satp);
                            }else
                            {
                                    ((Vector)H.get(satp)).add(new Double(Double.NaN)); //Missing Value
                            }
                    }
                    e = temp.keys();
                    while(e.hasMoreElements())
                    {
                            SmoothedAtomTypePair satp = (SmoothedAtomTypePair)e.nextElement();
                            Vector v = new Vector();
                            v.add((Double)temp.get(satp));
                            H.put(satp,v);
                    }
            }
            return H;
    }*/

    //    public static Hashtable getInstances(JOEMolVector molecules,
    //        SmoothedTAP TAP, Map properties) throws Exception
    //    {
    //        Hashtable H = new Hashtable();
    //
    //        for (int i = 0; i < molecules.getSize(); i++)
    //        {
    //            Molecule mol = molecules.getMol(i);
    //            SmoothedAtomPairResult sapr = (SmoothedAtomPairResult) TAP.calculate(mol,
    //                    properties);
    //            Hashtable temp = sapr.getNameValueTable();
    //            Enumeration e = temp.keys();
    //
    //            while (e.hasMoreElements())
    //            {
    //                SmoothedAtomTypePair satp = (SmoothedAtomTypePair) e.nextElement();
    //
    //                if (!H.containsKey(satp))
    //                {
    //                    H.put(satp, new Vector());
    //                }
    //            }
    //        }
    //
    //        H.put("SUP_CODE", new Vector());
    //
    //        for (int i = 0; i < molecules.getSize(); i++)
    //        {
    //            Molecule mol = molecules.getMol(i);
    //            SmoothedAtomPairResult sapr = (SmoothedAtomPairResult) TAP.calculate(mol,
    //                    properties);
    //            Hashtable temp = sapr.getNameValueTable();
    //            Enumeration e = H.keys();
    //
    //            while (e.hasMoreElements())
    //            {
    //                Object o = e.nextElement();
    //
    //                if (!(o instanceof String))
    //                {
    //                    SmoothedAtomTypePair satp = (SmoothedAtomTypePair) o;
    //
    //                    if (temp.containsKey(satp))
    //                    {
    //                        ((Vector) H.get(satp)).add((Double) temp.get(satp));
    //                    }
    //                    else
    //                    {
    //                        ((Vector) H.get(satp)).add(new Double(Double.NaN));
    //                    }
    //                }
    //                else
    //                {
    //                    //String s = mol.getData("TRUE_CLASS").toString();
    //                    String s = mol.getData("hia_measured").toString();
    //                    double d = Double.parseDouble(s);
    //                    ((Vector) H.get(o)).add(new Double(d));
    //                }
    //            }
    //        }
    //
    //        return H;
    //    }
    public static int getNominalIndex(int[] x)
    {
        for (int i = 0; i < x.length; i++)
        {
            if (x[i] == 1)
            {
                return i;
            }
        }

        return -1;
    }

    public static int[] getNumericTypes(Hashtable H)
    {
        String[] names = getAttributeNames(H);
        int[] types = new int[names.length];

        for (int i = 0; i < types.length; i++) // ;
        {
            types[i] = Attribute.NUMERIC;

            if (names[i] == "SUP_CODE")
            {
                types[i] = Attribute.NOMINAL;
            }
        }

        return types;
    }

    //    public static MolInstances matrix2instances(JOEMolVector molecules,
    //        SmoothedTAP TAP, Map properties) throws Exception
    //    {
    //        Hashtable H = getInstances(molecules, TAP, properties);
    //        double[][] matrix = getAttributeValues(H);
    //        String[] descriptors = getAttributeNames(H);
    //        int[] attributeTypes = getNumericTypes(H);
    //        int n_mols = matrix.length;
    //
    //        if (matrix.length < 1)
    //        {
    //            logger.error("Matrix contains no elements.");
    //
    //            return null;
    //        }
    //
    //        int x = getNominalIndex(attributeTypes);
    //        Vector v = (Vector) H.get("SUP_CODE");
    //        v = getDistinct(v);
    //
    //        FastVector noms = new FastVector();
    //        Enumeration e = v.elements();
    //
    //        while (e.hasMoreElements())
    //        {
    //            noms.addElement(((Double) e.nextElement()).toString());
    //        }
    //
    //        FastVector attributesV = new FastVector(0);
    //
    //        //int mols = matrix.length;
    //        //boolean numericFlag = true;
    //        e = noms.elements();
    //        System.out.println("Nominal Class Attribute Values:");
    //
    //        while (e.hasMoreElements())
    //        {
    //            System.out.println((String) e.nextElement());
    //        }
    //
    //        x = 0;
    //
    //        int y = 0;
    //
    //        for (int i = 0; i < descriptors.length; i++)
    //        {
    //            if (attributeTypes[i] == Attribute.NUMERIC)
    //            {
    //                // numeric
    //                attributesV.addElement(new Attribute(descriptors[i], i));
    //                x++;
    //            }
    //            else
    //            {
    //                attributesV.addElement(new Attribute(descriptors[i], noms, i));
    //                y++;
    //            }
    //        }
    //
    //        System.out.println("Number of Attributs: " + attributesV.size() + "\n" +
    //            x + " numeric and " + y + " nominal attributs");
    //
    //        int descriptorSize = attributesV.size();
    //        Attribute attribute = null;
    //
    //        // create molecule instances
    //        MolInstances instances = new MolInstances("MatrixInstances",
    //                attributesV, descriptorSize);
    //
    //        // iterate over all instances (to generate them)
    //        double[] instance;
    //
    //        for (int i = 0; i < n_mols; i++)
    //        {
    //            instance = new double[descriptorSize];
    //
    //            for (int j = 0; j < descriptorSize; j++)
    //            {
    //                attribute = (Attribute) attributesV.elementAt(j);
    //
    //                if (Double.isNaN(matrix[i][j]))
    //                {
    //                    instance[attribute.index()] = MolInstance.missingValue();
    //                }
    //                else
    //                {
    //                    if (attributeTypes[j] == Attribute.NUMERIC)
    //                    {
    //                        // numeric
    //                        instance[attribute.index()] = matrix[i][j];
    //                    }
    //                    else
    //                    {
    //                        instance[attribute.index()] = matrix[i][j] - 1.0;
    //                    }
    //                }
    //
    //                attribute.index();
    //            }
    //
    //            // add created molecule instance to molecule instances
    //            MolInstance inst = new MolInstance(molecules.getMol(i), 1, instance);
    //            instances.add(inst);
    //
    //            //System.out.println("instance (attr.:"+inst.numAttributes()+", vals:"+inst.numValues()+"): "+inst);
    //        }
    //
    //        //System.out.println(instances.toString());
    //        return instances;
    //    }
    public static String toString(Hashtable H)
    {
        String[] names = getAttributeNames(H);
        double[][] values = getAttributeValues(H);
        System.out.println("Number of Atom Pairs: " + values[0].length +
            "\nNumber of Molecules: " + values.length +
            "\nNumber of Attributes: " + names.length);

        String s = "";

        for (int i = 0; i < names.length; i++)
        {
            s += names[i];
            s += "\n";
        }

        s += "\n\n";
        System.out.println("Writing values");

        for (int i = 0; i < values.length; i++)
        {
            System.out.println("Writing Values for Molecule: " + i);

            for (int j = 0; j < values[i].length; j++)
            {
                s += values[i][j];
                s += "\t";
            }

            s += "\n";
        }

        return s;
    }

    public static void writeHash(Hashtable H, File o) throws Exception
    {
        String[] names = getAttributeNames(H);
        double[][] values = getAttributeValues(H);
        System.out.println("Number of Atom Pairs: " + values[0].length +
            "\nNumber of Molecules: " + values.length +
            "\nNumber of Attributes: " + names.length);

        FileWriter out = new FileWriter(o);

        for (int i = 0; i < names.length; i++)
        {
            out.write(names[i] + "\n");
        }

        out.write("\n\n");

        for (int i = 0; i < values.length; i++)
        {
            //System.out.println("Writing Values for Molecule: " + i);
            for (int j = 0; j < values[i].length; j++)
            {
                out.write(values[i][j] + "\t");
            }

            out.write("\n");
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
