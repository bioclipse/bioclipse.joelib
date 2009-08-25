///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Sphere.java,v $
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

import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:33 $
 */
public class Sphere
{
    //~ Instance fields ////////////////////////////////////////////////////////

    int nPhi = 6;
    int nPsi = 4;

    Shape3D shape;
    float[][][] verts;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Constructor for the Sphere object
     */
    public Sphere()
    {
        createShape();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Description of the Method
     */
    void createShape()
    {
        fillVerts();

        float[] coords = new float[nPhi * nPsi * 9];
        float[] norms = new float[nPhi * nPsi * 9];

        int n = 0;

        for (int psi = 0; psi < (nPsi - 1); psi++)
        {
            for (int phi = 0; phi < (nPhi - 2); phi++)
            {
                norms[n] = verts[psi][phi][0];
                norms[n + 1] = verts[psi][phi][1];
                norms[n + 2] = verts[psi][phi][2];
                norms[n + 3] = verts[psi + 1][phi][0];
                norms[n + 4] = verts[psi + 1][phi][1];
                norms[n + 5] = verts[psi + 1][phi][2];
                norms[n + 6] = verts[psi + 1][phi + 1][0];
                norms[n + 7] = verts[psi + 1][phi + 1][1];
                norms[n + 8] = verts[psi + 1][phi + 1][2];

                n += 9;
            }
        }

        for (int i = 0; i < norms.length; i++)
        {
            coords[i] = norms[i] * 2.0f;
        }

        System.out.println("Creating tri");

        TriangleArray tri = new TriangleArray(nPhi * nPsi * 3,
                QuadArray.COORDINATES | QuadArray.NORMALS);
        System.out.println("Setting norms and coords");
        tri.setCoordinates(0, coords);
        tri.setNormals(0, norms);
        shape = new Shape3D(tri);
    }

    /**
     * Description of the Method
     */
    void fillVerts()
    {
        double mPhi = Math.PI / ((1.0 * nPhi) - 1.0) * 2.0;
        double mPsi = Math.PI / ((1.0 * nPsi) - 1.0) * 1.0;

        verts = new float[nPsi][nPhi][3];

        float r;

        float z;
        float phiAngle;
        float psiAngle;

        psiAngle = 0.0f;

        for (int psi = 0; psi < nPsi; psi++)
        {
            z = (float) Math.cos(psiAngle);
            r = (float) Math.sin(psiAngle);
            phiAngle = 0.0f;

            for (int phi = 0; phi < nPhi; phi++)
            {
                verts[psi][phi][0] = r * (float) Math.cos(phiAngle);
                verts[psi][phi][1] = r * (float) Math.sin(phiAngle);
                verts[psi][phi][2] = z;
                phiAngle += mPhi;
            }

            psiAngle += mPsi;
        }
    }

    /*
     *protected Cylinder( float radius, float length, int quality, Appearance a ){
     *if (quality < 3){
     *quality = 3;
     *}
     *div = (float)quality;
     *verts = new float[quality*12];
     *normals = new float[quality*12];
     *double inc = 2.0*Math.PI/(double)div;
     *for (int i=0; i< quality; i++){
     *float z1 = radius * (float)Math.sin((double)i*inc);
     *float x1 = radius * (float)Math.cos((double)i*inc);
     *float z2 = radius * (float)Math.sin((double)(i+1)*inc);
     *float x2 = radius * (float)Math.cos((double)(i+1)*inc);
     *verts[12*i] = x1;
     *verts[12*i+1] = 0.0f;
     *verts[12*i+2] = z1;
     *verts[12*i+3] = x1;
     *verts[12*i+4] = length;
     *verts[12*i+5] = z1;
     *verts[12*i+6] = x2;
     *verts[12*i+7] = length;
     *verts[12*i+8] = z2;
     *verts[12*i+9] = x2;
     *verts[12*i+10] = 0.0f;
     *verts[12*i+11] = z2;
     *float nz1 = (float)Math.sin((double)i*inc);
     *float nx1 = (float)Math.cos((double)i*inc);
     *float nz2 = (float)Math.sin((double)(i+1)*inc);
     *float nx2 = (float)Math.cos((double)(i+1)*inc);
     *normals[12*i] = nx1;
     *normals[12*i+1] = 0.0f;
     *normals[12*i+2] = nz1;
     *normals[12*i+3] = nx1;
     *normals[12*i+4] = 0.0f;
     *normals[12*i+5] = nz1;
     *normals[12*i+6] = nx2;
     *normals[12*i+7] = 0.0f;
     *normals[12*i+8] = nz2;
     *normals[12*i+9] = nx2;
     *normals[12*i+10] = 0.0f;
     *normals[12*i+11] = nz2;
     *}
     *quad =
     *new QuadArray(quality*4, QuadArray.COORDINATES|QuadArray.NORMALS );
     *quad.setCoordinates(0, verts);
     *quad.setNormals(0, normals);
     *shape = new Shape3D(quad, a);
     *}
     */

    /**
     * Gets the shape attribute of the Sphere object
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
