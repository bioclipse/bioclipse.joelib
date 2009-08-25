///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: RenderTable.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.7 $
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

import joelib2.data.BasicElementHolder;

import joelib2.gui.render3D.molecule.ViewerAtom;
import joelib2.gui.render3D.molecule.ViewerBond;

import java.awt.Color;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.media.j3d.Link;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.SharedGroup;

import javax.vecmath.Color3f;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:33 $
 */
public class RenderTable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static RenderTable table = null;
    private static boolean useAtomAndBondCaching = false;

    /**
     * The Radius of the sticks *
     */
    public static float STICK_RADIUS = 0.14f;

    /**
     * The Radius of the balls int ball and stick mode *
     */
    public static float BALL_RADIUS = 0.3f;
    final static int STICK_QUALITY = 7;

    //~ Instance fields ////////////////////////////////////////////////////////

    public String atomPropertyName = null; //"Gasteiger_Marsili";
    private Color3f eColor = new Color3f(0.0f, 0.0f, 0.0f);
    private Hashtable nodeCache = new Hashtable();
    private List renderList = new Vector(100);
    private int renderStyle = RenderStyle.WIRE;
    private Color3f sColor = new Color3f(1.0f, 1.0f, 1.0f);

    //~ Constructors ///////////////////////////////////////////////////////////

    private RenderTable()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * the accessor method for rthe instance of the RenderTable *
     *
     * @return   The table value
     */
    public static RenderTable getTable()
    {
        if (table == null)
        {
            init();
        }

        return table;
    }

    public void clear()
    {
        nodeCache.clear();
        renderList.clear();
    }

    /**
     * get the material for atom a*
     *
     * @param a  Description of the Parameter
     * @return   The material value
     */
    public Material getMaterial(ViewerAtom a)
    {
        float[] f = getRGBFloats(a);
        Color3f aColor = new Color3f(f[0], f[1], f[2]);
        Color3f dColor = new Color3f(f[0], f[1], f[2]);

        return new Material(aColor, eColor, dColor, sColor, 20.0f);
    }

    /**
     * get the radius for atom a *
     *
     * @param a  Description of the Parameter
     * @return   The radius value
     */
    public float getRadius(ViewerAtom a)
    {
        BasicElementHolder etab = BasicElementHolder.instance();
        String name = a.getName();

        int atomNum = etab.getAtomicNum(name);
        double vdw;
        vdw = etab.correctedVdwRad(atomNum);

        return (float) vdw;
    }

    /**
     * get the R,G,B values (between 0.0f and 1.0f)
     *of the color for atom a *
     *
     * @param a  Description of the Parameter
     * @return   The rGBFloats value
     */
    public float[] getRGBFloats(ViewerAtom a)
    {
        //              JOEElementTable etab = JOEElementTable.instance();
        //              String name = a.getName();
        //int atomNum = etab.getAtomicNum(name);
        //Color color = etab.getColor(atomNum);
        if (a.getParent().getAtomPropertyColoring().getMoleculeForColoring() !=
                a.getJOEAtom().getParent())
        {
            a.getParent().getAtomPropertyColoring().useAtomPropertyColoring(a
                .getJOEAtom().getParent(), atomPropertyName);
        }

        Color color = a.getParent().getAtomPropertyColoring().getAtomColor(a
                .getJOEAtom());

        float[] res = new float[3];
        float d = 1.0f / 255.0f;
        res[0] = color.getRed() * d;
        res[1] = color.getGreen() * d;
        res[2] = color.getBlue() * d;

        return res;
    }

    /**
     * Gets the sharedAtomGroup attribute of the RenderTable object
     *
     * @param a  Description of the Parameter
     * @return   The sharedAtomGroup value
     */
    public Node getSharedAtomGroup(ViewerAtom a)
    {
        String pref = "sag";

        // SharedAtomGroup
        String key = makeCacheKey(pref, a);
        SharedAtomGroup sag = null;

        Link link = null;

        if (useAtomAndBondCaching)
        {
            sag = (SharedAtomGroup) nodeCache.get(key);

            if (sag != null)
            {
                //System.out.println("Using shared Cyl");
                link = new Link(sag);

                //link.setCapability(Link.ALLOW_SHARED_GROUP_READ);
                //link.setCapability(Link.ALLOW_SHARED_GROUP_WRITE);
                return link;
            }
        }

        sag = new SharedAtomGroup(a);
        addCachedNode(key, sag);
        link = new Link(sag);

        //link.setCapability(Link.ALLOW_SHARED_GROUP_READ);
        //link.setCapability(Link.ALLOW_SHARED_GROUP_WRITE);
        return link;
    }

    /**
     * Gets the sharedBondGroup attribute of the RenderTable object
     *
     * @param a  Description of the Parameter
     * @return   The sharedBondGroup value
     */
    public Node getSharedBondGroup(ViewerBond b, ViewerAtom a, float radius)
    {
        String pref = "sbg";

        // SharedBondGroup
        String key = makeCacheKey(pref, a);
        SharedBondGroup sbg = null;

        if (useAtomAndBondCaching)
        {
            sbg = (SharedBondGroup) nodeCache.get(key);

            if (sbg != null)
            {
                //System.out.println("Using shared Cyl");
                return new Link(sbg);
            }
        }

        sbg = new SharedBondGroup(a, radius);
        addCachedNode(key, sbg);

        b.getParent().pickBondMapping.put(sbg.getShape(), b);
        b.shapes.add(sbg.getShape());

        return new Link(sbg);
    }

    /**
     * What is the current rendering style ? *
     *
     * @return   The style value
     */
    public int getStyle()
    {
        return renderStyle;
    }

    /**
     * Set the current rendering style *
     *
     * @param style  The new style value
     */
    public void setStyle(int style)
    {
        renderStyle = style;

        //Loop over renderList
        Iterator iter = renderList.iterator();

        while (iter.hasNext())
        {
            RenderStyle rs = (RenderStyle) iter.next();
            rs.setStyle(style);
        }
    }

    /**
     * initializes the table *
     */
    static void init()
    {
        table = new RenderTable();
    }

    /**
     * Adds a feature to the CachedNode attribute of the RenderTable object
     *
     * @param key  The feature to be added to the CachedNode attribute
     * @param gr   The feature to be added to the CachedNode attribute
     */
    void addCachedNode(String key, SharedGroup gr)
    {
        nodeCache.put(key, gr);
        renderList.add(gr);
    }

    /**
     * Description of the Method
     *
     * @param pref  Description of the Parameter
     * @param a     Description of the Parameter
     * @return      Description of the Return Value
     */
    String makeCacheKey(String pref, ViewerAtom a)
    {
        String k = a.getName();
        String key = pref + "_" + k;

        return key;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
