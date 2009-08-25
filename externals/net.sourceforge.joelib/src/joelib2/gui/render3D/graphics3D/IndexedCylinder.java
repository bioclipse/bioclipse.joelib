///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: IndexedCylinder.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:33 $
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
package joelib2.gui.render3D.graphics3D;

import javax.media.j3d.Appearance;
import javax.media.j3d.IndexedQuadArray;
import javax.media.j3d.Shape3D;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class IndexedCylinder
{
    //~ Instance fields ////////////////////////////////////////////////////////

    float div = 3.0f;
    int[] idx;
    float[] normals;
    int[] normidx;

    IndexedQuadArray quad = null;
    Shape3D shape;
    float[] verts;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the IndexedCylinder object
     *
     * @param radius   Description of the Parameter
     * @param length   Description of the Parameter
     * @param quality  Description of the Parameter
     * @param a        Description of the Parameter
     */
    public IndexedCylinder(float radius, float length, int quality,
        Appearance a)
    {
        //System.out.println("Radius: "+radius);
        if (quality < 3)
        {
            quality = 3;
        }

        div = (float) quality;

        verts = new float[quality * 6];
        normals = new float[quality * 3];
        normidx = new int[quality * 4];
        idx = new int[quality * 4];

        double inc = (2.0 * Math.PI) / (double) div;

        for (int i = 0; i < quality; i++)
        {
            float z = radius * (float) Math.sin((double) i * inc);
            float x = radius * (float) Math.cos((double) i * inc);
            verts[3 * i] = x;
            verts[(3 * i) + 1] = 0.0f;
            verts[(3 * i) + 2] = z;

            System.out.println("coor: " + verts[3 * i] + " " +
                verts[(3 * i) + 1] + " " + verts[(3 * i) + 2]);
            verts[3 * (i + quality)] = x;
            verts[(3 * (i + quality)) + 1] = length;
            verts[(3 * (i + quality)) + 2] = z;

            //System.out.println("coor: "+verts[3*(i+quality)]+" "+
            //  verts[3*(i+quality)+1]+" "+
            //  verts[3*(i+quality)+2]);
            normals[3 * i] = (float) Math.sin(((double) i * inc) + (0.5 * inc));
            normals[(3 * i) + 1] = 0.0f;
            normals[(3 * i) + 2] = (float) Math.cos(((double) i * inc) +
                    (0.5 * inc));
            System.out.println("nx/ny/nz: " + normals[3 * i] + "/" +
                normals[(3 * i) + 1] + "/" + normals[(3 * i) + 2]);

            normidx[i * 4] = i;
            normidx[(i * 4) + 1] = i;
            normidx[(i * 4) + 2] = i;
            normidx[(i * 4) + 3] = i;

            idx[i * 4] = i;
            idx[(i * 4) + 1] = i + quality;
            idx[(i * 4) + 2] = i + quality + 1;
            idx[(i * 4) + 3] = i + 1;
        }

        idx[((quality - 1) * 4) + 1] = 0;
        idx[((quality - 1) * 4) + 2] = quality;

        normidx[((quality - 1) * 4) + 1] = 0;
        normidx[((quality - 1) * 4) + 2] = 0;

        quad = new IndexedQuadArray(quality * 2,
                IndexedQuadArray.COORDINATES | IndexedQuadArray.NORMALS,
                quality * 4);
        quad.setCoordinates(0, verts);
        quad.setCoordinateIndices(0, idx);

        quad.setNormals(0, normals);
        quad.setNormalIndices(0, normidx);

        shape = new Shape3D(quad, a);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets the shape attribute of the IndexedCylinder object
     *
     * @return   The shape value
     */
    Shape3D getShape()
    {
        return shape;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
