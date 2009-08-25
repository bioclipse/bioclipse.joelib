///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Cylinder.java,v $
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
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;

import com.sun.j3d.utils.picking.PickTool;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class Cylinder
{
    //~ Instance fields ////////////////////////////////////////////////////////

    float div = 3.0f;
    float[] normals;

    QuadArray quad = null;
    Shape3D shape;
    float[] verts;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the Cylinder object
     *
     * @param radius   Description of the Parameter
     * @param length   Description of the Parameter
     * @param quality  Description of the Parameter
     * @param a        Description of the Parameter
     */
    public Cylinder(float radius, float length, int quality, Appearance a)
    {
        if (quality < 3)
        {
            quality = 3;
        }

        div = (float) quality;

        verts = new float[quality * 12];
        normals = new float[quality * 12];

        double inc = (2.0 * Math.PI) / (double) div;

        for (int i = 0; i < quality; i++)
        {
            float z1 = radius * (float) Math.sin((double) i * inc);
            float x1 = radius * (float) Math.cos((double) i * inc);
            float z2 = radius * (float) Math.sin((double) (i + 1) * inc);
            float x2 = radius * (float) Math.cos((double) (i + 1) * inc);

            verts[12 * i] = x1;
            verts[(12 * i) + 1] = 0.0f;
            verts[(12 * i) + 2] = z1;
            verts[(12 * i) + 3] = x1;
            verts[(12 * i) + 4] = length;
            verts[(12 * i) + 5] = z1;
            verts[(12 * i) + 6] = x2;
            verts[(12 * i) + 7] = length;
            verts[(12 * i) + 8] = z2;
            verts[(12 * i) + 9] = x2;
            verts[(12 * i) + 10] = 0.0f;
            verts[(12 * i) + 11] = z2;

            float nz1 = (float) Math.sin((double) i * inc);
            float nx1 = (float) Math.cos((double) i * inc);
            float nz2 = (float) Math.sin((double) (i + 1) * inc);
            float nx2 = (float) Math.cos((double) (i + 1) * inc);

            normals[12 * i] = nx1;
            normals[(12 * i) + 1] = 0.0f;
            normals[(12 * i) + 2] = nz1;
            normals[(12 * i) + 3] = nx1;
            normals[(12 * i) + 4] = 0.0f;
            normals[(12 * i) + 5] = nz1;
            normals[(12 * i) + 6] = nx2;
            normals[(12 * i) + 7] = 0.0f;
            normals[(12 * i) + 8] = nz2;
            normals[(12 * i) + 9] = nx2;
            normals[(12 * i) + 10] = 0.0f;
            normals[(12 * i) + 11] = nz2;
        }

        quad = new QuadArray(quality * 4,
                QuadArray.COORDINATES | QuadArray.NORMALS);
        quad.setCoordinates(0, verts);
        quad.setNormals(0, normals);

        // Try the stripifier
        /*GeometryInfo geom = new GeometryInfo(GeometryInfo.QUAD_ARRAY);
         *geom.setCoordinates(verts);
         *geom.setNormals(normals);
         *Stripifier strip = new Stripifier();
         *strip.stripify(geom);
         *shape = new Shape3D();
         *shape.setGeometry(geom.getGeometryArray());
         *shape.setAppearance(a);
         */
        shape = new Shape3D(quad, a);
        shape.setCapability(shape.ALLOW_APPEARANCE_READ);
        shape.setCapability(shape.ALLOW_APPEARANCE_WRITE);
        shape.setCapability(Shape3D.ENABLE_PICK_REPORTING);
        PickTool.setCapabilities(shape, PickTool.INTERSECT_FULL);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets the shape attribute of the Cylinder object
     *
     * @return   The shape value
     */
    public Shape3D getShape()
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
