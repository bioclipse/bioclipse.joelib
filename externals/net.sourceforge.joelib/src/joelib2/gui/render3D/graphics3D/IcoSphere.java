///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: IcoSphere.java,v $
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
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;

import javax.vecmath.Vector3f;

import com.sun.j3d.utils.picking.PickTool;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class IcoSphere
{
    //~ Static fields/initializers /////////////////////////////////////////////

    final static double X = 0.525731;
    final static double Z = 0.850651;
    final static double[][] vdata =
        {
            {-0.525731, 0.0, 0.850651},
            {0.525731, 0.0, 0.850651},
            {-0.525731, 0.0, -0.850651},
            {0.525731, 0.0, -0.850651},
            {0.0, 0.850651, 0.525731},
            {0.0, 0.850651, -0.525731},
            {0.0, -0.850651, 0.525731},
            {0.0, -0.850651, -0.525731},
            {0.850651, 0.525731, 0.0},
            {-0.850651, 0.525731, 0.0},
            {0.850651, -0.525731, 0.0},
            {-0.850651, -0.525731, 0.0}
        };
    final static int[][] tindices =
        {
            {0, 4, 1},
            {0, 9, 4},
            {9, 5, 4},
            {4, 5, 8},
            {4, 8, 1},
            {8, 10, 1},
            {8, 3, 10},
            {5, 3, 8},
            {5, 2, 3},
            {2, 7, 3},
            {7, 10, 3},
            {7, 6, 10},
            {7, 11, 6},
            {11, 0, 6},
            {0, 1, 6},
            {6, 1, 10},
            {9, 0, 11},
            {9, 11, 2},
            {9, 2, 5},
            {7, 2, 11}
        };
    final static int facets = 20;

    //~ Instance fields ////////////////////////////////////////////////////////

    Appearance app;
    int faces;
    int index;
    int quality = 0;
    double radius = 1.0;
    Shape3D shape;
    TriangleArray tris;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the IcoSphere object
     *
     * @param radius   Description of the Parameter
     * @param quality  Description of the Parameter
     * @param a        Description of the Parameter
     */
    public IcoSphere(float radius, int quality, Appearance a)
    {
        this.quality = quality;
        this.radius = (double) radius;
        this.app = a;

        int faces = 20;

        for (int j = 0; j < quality; j++)
        {
            faces *= 4;
        }

        tris = new TriangleArray(faces * 3,
                TriangleArray.COORDINATES | TriangleArray.NORMALS);

        for (int k = index = 0; k < 20; k++)
        {
            subdivide(vdata[tindices[k][0]], vdata[tindices[k][1]],
                vdata[tindices[k][2]], quality);
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets the shape attribute of the IcoSphere object
     *
     * @return   The shape value
     */
    public Shape3D getShape()
    {
        if (shape == null)
        {
            // Try the stripifier

            /*GeometryInfo geom = new GeometryInfo(GeometryInfo.TRIANGLE_ARRAY);
             *int nVert = tris.getVertexCount();
             *float arr[]= new float[nVert*3];
             *tris.getCoordinates(0, arr);
             *geom.setCoordinates(arr);
             *arr = new float[nVert*3];
             *tris.getNormals(0, arr);
             *geom.setNormals(arr);
             *Stripifier strip = new Stripifier();
             *strip.stripify(geom);
             *shape = new Shape3D();
             *shape.setGeometry(geom.getGeometryArray());
             */

            // if not stripifier
            shape = new Shape3D(tris);
            shape.setAppearance(app);
            shape.setCapability(shape.ALLOW_APPEARANCE_READ);
            shape.setCapability(shape.ALLOW_APPEARANCE_WRITE);
            shape.setCapability(Shape3D.ENABLE_PICK_REPORTING);
            PickTool.setCapabilities(shape, PickTool.INTERSECT_FULL);
        }

        return shape;
    }

    /**
     * Adds a feature to the Tri attribute of the IcoSphere object
     *
     * @param ad  The feature to be added to the Tri attribute
     */
    void addTri(double[] ad)
    {
        double[] tmp = new double[3];
        tmp[0] = ad[0] * radius;
        tmp[1] = ad[1] * radius;
        tmp[2] = ad[2] * radius;

        tris.setCoordinate(index, tmp);

        Vector3f norm = new Vector3f((float) ad[0], (float) ad[1],
                (float) ad[2]);

        //norm.normalize();
        tris.setNormal(index++, norm);
    }

    /**
     * Description of the Method
     *
     * @param ad  Description of the Parameter
     */
    void normalize(double[] ad)
    {
        double local = Math.sqrt((ad[0] * ad[0]) + (ad[1] * ad[1]) +
                (ad[2] * ad[2]));

        if (local == 0.0)
        {
            return;
        }

        ad[0] /= local;
        ad[1] /= local;
        ad[2] /= local;
    }

    /**
     * Description of the Method
     *
     * @param ad1  Description of the Parameter
     * @param ad2  Description of the Parameter
     * @param ad3  Description of the Parameter
     * @param i    Description of the Parameter
     */
    void subdivide(double[] ad1, double[] ad2, double[] ad3, int i)
    {
        if (i == 0)
        {
            triangle(ad1, ad2, ad3);

            return;
        }

        double[] local2 = {0.0, 0.0, 0.0};
        double[] local1 = {0.0, 0.0, 0.0};
        double[] local0 = {0.0, 0.0, 0.0};

        for (int j = 0; j < 3; j++)
        {
            local2[j] = ad1[j] + ad2[j];
            local1[j] = ad2[j] + ad3[j];
            local0[j] = ad3[j] + ad1[j];
        }

        normalize(local2);
        normalize(local1);
        normalize(local0);
        subdivide(ad1, local2, local0, i - 1);
        subdivide(ad2, local1, local2, i - 1);
        subdivide(ad3, local0, local1, i - 1);
        subdivide(local2, local1, local0, i - 1);
    }

    /**
     * Description of the Method
     *
     * @param ad1  Description of the Parameter
     * @param ad2  Description of the Parameter
     * @param ad3  Description of the Parameter
     */
    void triangle(double[] ad1, double[] ad2, double[] ad3)
    {
        addTri(ad1);
        addTri(ad3);
        addTri(ad2);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
