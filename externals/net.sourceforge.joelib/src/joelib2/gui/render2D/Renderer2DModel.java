///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Renderer2DModel.java,v $
//Purpose:  Renderer for a 2D layout.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.8 $
//                      $Date: 2005/02/17 16:48:32 $
//                      $Author: wegner $
//Original Author: steinbeck, gzelter, egonw
//Original Version: Copyright (C) 1997-2003
//                                The Chemistry Development Kit (CDK) project
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation; either version 2.1
// of the License, or (at your option) any later version.
// All we ask is that proper credit is given for our work, which includes
// - but is not limited to - adding the above copyright notice to the beginning
// of your source code files, and to any copyright notice that you may distribute
// with programs based on this work.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////
package joelib2.gui.render2D;

import joelib2.molecule.Atom;
import joelib2.molecule.Bond;

import joelib2.molecule.types.BasicAtomPropertyColoring;

import wsi.ra.tool.BasicPropertyHolder;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;

import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;


/**
 * Model for Renderer2D that contains settings for drawing objects.
 *
 * @.author     steinbeck
 * @.author     egonw
 * @.author     wegnerj
 * @.license    LGPL
 * @.cvsversion    $Revision: 1.8 $, $Date: 2005/02/17 16:48:32 $
 */
public class Renderer2DModel implements java.io.Serializable, Cloneable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final double DEFAULT_BOND_LENGTH = 30.0;
    private static final double DEFAULT_BOND_DISTANCE = 6.0;
    private static final double DEFAULT_BOND_WIDTH = 2.0;
    private static final boolean DEFAULT_SHOW_END_CARBON = true;
    private static final boolean DEFAULT_DRAW_NUMBERS = true;
    private static final boolean DEFAULT_KEKULE_STRUCTURE = false;
    private static final boolean DEFAULT_USE_ATOM_COLORS = false;
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.white;
    private static final Color DEFAULT_FOREGROUND_COLOR = Color.black;
    private static final Color DEFAULT_HIGHLIGHT_COLOR = Color.red; //Color.lightGray;
    private static final Color DEFAULT_NUMBER_COLOR = Color.blue;
    private static final Color DEFAULT_CONJ_RING_COLOR = Color.lightGray;
    private static final Color DEFAULT_ARROW_COLOR = Color.green;
    private static final Color DEFAULT_ORTHOLINE_COLOR = Color.blue;
    private static final double DEFAULT_HIGHLIGHTRADIUS = 10.0;
    private static final double DEFAULT_SCALEFACTOR = 60.0;
    private static final double DEFAULT_ZOOM_FACTOR = 1.0;
    private static final int DEFAULT_ATOM_RADIUS = 8;
    private static final int DEFAULT_ORTHO_LINE_OFFSET = 20;
    private static final int DEFAULT_ARROW_OFFSET = 10;
    private static final int DEFAULT_ARROW_SIZE_OFFSET = 5;
    private static final boolean DEFAULT_DRAW_CARBON_ATOMS = false;

    //~ Instance fields ////////////////////////////////////////////////////////

    /** Misc. */
    private BasicAtomPropertyColoring aPropColoring =
        new BasicAtomPropertyColoring();
    private Color arrowColor = DEFAULT_ARROW_COLOR;
    private int arrowOffset = DEFAULT_ARROW_OFFSET;
    private Arrows arrows;
    private int arrowSize = DEFAULT_ARROW_SIZE_OFFSET;
    private int atomRadius = DEFAULT_ATOM_RADIUS;
    private Color backColor = DEFAULT_BACKGROUND_COLOR;
    private double bondDistance = DEFAULT_BOND_DISTANCE;
    private double bondLength = DEFAULT_BOND_LENGTH;
    private double bondWidth = DEFAULT_BOND_WIDTH;
    private Color conjRingColor = DEFAULT_CONJ_RING_COLOR;
    private ConjugatedRings cRings;
    private boolean drawCarbonAtoms = DEFAULT_DRAW_CARBON_ATOMS;
    private boolean drawNumbers = DEFAULT_DRAW_NUMBERS;
    private Color foreColor = DEFAULT_FOREGROUND_COLOR;
    private Color highlightColor = DEFAULT_HIGHLIGHT_COLOR;
    private Hashtable highlightedAtoms = new Hashtable();
    private Hashtable highlightedBonds = new Hashtable();
    private double highlightRadius = DEFAULT_HIGHLIGHTRADIUS;
    private List lassoPoints = new Vector();
    private transient List listeners = new Vector();
    private Color numberColor = DEFAULT_NUMBER_COLOR;
    private OrthoLines oLines;
    private Color orthoLineColor = DEFAULT_ORTHOLINE_COLOR;
    private int orthoLineOffset = DEFAULT_ORTHO_LINE_OFFSET;
    private Point pointerVectorEnd = null;
    private Point pointerVectorStart = null;
    private double scaleFactor = DEFAULT_SCALEFACTOR;
    private List selectedPart = null;
    private Polygon selectRect = null;

    /** Determines wether methyl carbons' symbols should be drawn explicit
     *  for methyl carbons. Example C/\C instead of /\.
     */
    private boolean showEndCarbons = DEFAULT_SHOW_END_CARBON;
    private boolean useAtomColors = DEFAULT_USE_ATOM_COLORS;

    /** Determines wether structures should be drawn as Kekule structures,
     *  thus giving each carbon element explicitely, instead of not displaying
     *  the element symbol. Example C-C-C instead of /\.
     */
    private boolean useKekuleStructure = DEFAULT_KEKULE_STRUCTURE;

    /** Determines how much the image is zoomed into on. */
    private double zoomFactor = DEFAULT_ZOOM_FACTOR;

    //~ Constructors ///////////////////////////////////////////////////////////

    protected Renderer2DModel()
    {
        loadProperties();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Adds a change listener to the list of listeners
     *
     * @param   listener  The listener added to the list
     */
    public void addChangeListener(Renderer2DChangeListener listener)
    {
        if (listeners == null)
        {
            listeners = new Vector();
        }

        if (!listeners.contains(listener))
        {
            listeners.add(listener);
        }
    }

    /**
     * Adds a point to the list of lasso points
     *
     * @param   lassoPoints
     */
    public void addLassoPoint(Point point)
    {
        this.lassoPoints.add(point);
        fireChange();
    }

    public void clearHighlightedAtoms()
    {
        highlightedAtoms.clear();
    }

    public void clearHighlightedBonds()
    {
        highlightedBonds.clear();
    }

    /**
     * Returns if the drawing of atom numbers is switched on for this model
     *
     * @return  true if the drawing of atom numbers is switched on for this model
     */
    public boolean drawNumbers()
    {
        return this.drawNumbers;
    }

    /**
     * Notifies registered listeners of certain changes
     * that have occurred in this model.
     */
    public void fireChange()
    {
        EventObject event = new EventObject(this);

        if (listeners == null)
        {
            listeners = new Vector();
        }

        for (int i = 0; i < listeners.size(); i++)
        {
            ((Renderer2DChangeListener) listeners.get(i)).stateChanged(event);
        }
    }

    /**
     * @return
     */
    public Color getArrowColor()
    {
        return arrowColor;
    }

    /**
     * @return
     */
    public int getArrowOffset()
    {
        return arrowOffset;
    }

    /**
     * @return
     */
    public Arrows getArrows()
    {
        return arrows;
    }

    /**
     * @return
     */
    public int getArrowSize()
    {
        return arrowSize;
    }

    /**
     * @param atom
     * @return
     */
    public Color getAtomBackColor(Atom atom)
    {
        return this.getBackColor();
    }

    /**
     * @param atom
     * @return
     */
    public Color getAtomForeColor(Atom atom)
    {
        if (useAtomColors)
        {
            return aPropColoring.getAtomColor(atom);
        }
        else
        {
            return this.getForeColor();
        }
    }

    /**
     * XXX No idea what this is about
     *
     * @return an unknown int
     */
    public int getAtomRadius()
    {
        return this.atomRadius;
    }

    /**
     * Returns the background color
     *
     * @return the background color
     */
    public Color getBackColor()
    {
        return this.backColor;
    }

    /**
     * @param bond
     * @return
     */
    public Color getBondColor(Bond bond)
    {
        return this.getForeColor();
    }

    /**
     * Returns the distance between two lines in a double or triple bond
     *
     * @return     the distance between two lines in a double or triple bond
     */
    public double getBondDistance()
    {
        return this.bondDistance;
    }

    /**
     * Returns the length of a bond line.
     *
     * @return     the length of a bond line
     */
    public double getBondLength()
    {
        return this.bondLength;
    }

    /**
     * Returns the thickness of a bond line.
     *
     * @return     the thickness of a bond line
     */
    public double getBondWidth()
    {
        return this.bondWidth;
    }

    /**
     * @return
     */
    public Color getConjRingColor()
    {
        return conjRingColor;
    }

    public ConjugatedRings getCRings()
    {
        return cRings;
    }

    /**
     * returns the foreground color for the drawing
     *
     * @return the foreground color for the drawing
     */
    public Color getForeColor()
    {
        return this.foreColor;
    }

    /**
     * Returns the color used for highlighting things in this model
     *
     * @return     the color used for highlighting things in this model
     */
    public Color getHighlightColor()
    {
        return this.highlightColor;
    }

    /**
     * Returns the atom currently highlighted
     *
     * @return the atom currently highlighted
     */
    public Atom[] getHighlightedAtoms()
    {
        Atom[] tmp = new Atom[highlightedAtoms.size()];
        Atom atom;
        int i = 0;

        for (Enumeration e = highlightedAtoms.elements(); e.hasMoreElements();)
        {
            atom = (Atom) e.nextElement();
            tmp[i] = atom;
            i++;
        }

        return tmp;
    }

    /**
     * Returns the Bond currently highlighted
     *
     * @return the Bond currently highlighted
     */
    public Bond[] getHighlightedBond()
    {
        Bond[] tmp = new Bond[highlightedBonds.size()];
        Bond bond;
        int i = 0;

        for (Enumeration e = highlightedBonds.elements(); e.hasMoreElements();)
        {
            bond = (Bond) e.nextElement();
            tmp[i] = bond;
            i++;
        }

        return tmp;
    }

    /**
     * Returns the radius around an atoms, for which the atom is
     * marked highlighted if a pointer device is placed within this radius
     *
     * @return The highlight radius for all atoms
     */
    public double getHighlightRadius()
    {
        return this.highlightRadius;
    }

    public boolean getKekuleStructure()
    {
        return this.useKekuleStructure;
    }

    /**
     * Returns a set of points constituating a selected region
     *
     * @return a vector with points
     */
    public List getLassoPoints()
    {
        return this.lassoPoints;
    }

    /**
     * @return
     */
    public Color getNumberColor()
    {
        return numberColor;
    }

    /**
     * @return
     */
    public OrthoLines getOLines()
    {
        return oLines;
    }

    /**
     * @return
     */
    public Color getOrthoLineColor()
    {
        return orthoLineColor;
    }

    /**
     * @return
     */
    public int getOrthoLineOffset()
    {
        return orthoLineOffset;
    }

    /**
     * Returns the end of the pointer vector
     *
     * @return the end point
     */
    public Point getPointerVectorEnd()
    {
        return this.pointerVectorEnd;
    }

    /**
     * Returns the start of a pointer vector
     *
     * @return the start point
     */
    public Point getPointerVectorStart()
    {
        return this.pointerVectorStart;
    }

    /**
     * A scale factor for the drawing.
     *
     * @return a scale factor for the drawing
     */
    public double getScaleFactor()
    {
        return this.scaleFactor;
    }

    /**
     * Get selected atoms
     *
     * @return an atomcontainer with the selected atoms
     */
    public List getSelectedPart()
    {
        return this.selectedPart;
    }

    /**
     * Returns selected rectangular
     *
     * @return the selection
     */
    public Polygon getSelectRect()
    {
        return this.selectRect;
    }

    public boolean getShowEndCarbons()
    {
        return this.showEndCarbons;
    }

    /**
     * A zoom factor for the drawing.
     *
     * @return a zoom factor for the drawing
     */
    public double getZoomFactor()
    {
        return this.zoomFactor;
    }

    /**
     * @param atom
     * @return
     */
    public boolean isAtomHighlighted(Atom atom)
    {
        if (highlightedAtoms.containsKey(atom))
        {
            return true;
        }

        return false;
    }

    /**
     * @param bond
     * @return
     */
    public boolean isBondHighlighted(Bond bond)
    {
        Atom a1 = bond.getBegin();
        Atom a2 = bond.getEnd();

        if (highlightedAtoms.containsKey(a1) &&
                highlightedAtoms.containsKey(a2))
        {
            return true;
        }

        if (highlightedBonds.containsKey(bond))
        {
            return true;
        }

        return false;
    }

    /**
     * @return Returns the drawCarbonAtoms.
     */
    public boolean isDrawCarbonAtoms()
    {
        return drawCarbonAtoms;
    }

    /**
     * Removes a change listener from the list of listeners
     *
     * @param   listener  The listener removed from the list
     */
    public void removeChangeListener(Renderer2DChangeListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * @param arrowColor
     */
    public void setArrowColor(Color arrowColor)
    {
        this.arrowColor = arrowColor;
    }

    /**
     * @param arrowOffset
     */
    public void setArrowOffset(int arrowOffset)
    {
        this.arrowOffset = arrowOffset;
    }

    /**
     * @param arrows
     */
    public void setArrows(Arrows arrows)
    {
        this.arrows = arrows;
    }

    /**
     * @param arrowSize
     */
    public void setArrowSize(int arrowSize)
    {
        this.arrowSize = arrowSize;
    }

    /**
    * XXX No idea what this is about
    *
    * @param   atomRadius   XXX No idea what this is about
    */
    public void setAtomRadius(int atomRadius)
    {
        this.atomRadius = atomRadius;
    }

    /**
     * Sets the background color
     *
     * @param   backColor the background color
     */
    public void setBackColor(Color backColor)
    {
        this.backColor = backColor;
    }

    /**
     * Sets the distance between two lines in a double or triple bond
     *
     * @param   bondDistance  the distance between two lines in a double or triple bond
     */
    public void setBondDistance(double bondDistance)
    {
        this.bondDistance = bondDistance;
    }

    /**
     * Sets the length of a bond line.
     *
     * @param   bondLength  the length of a bond line
     */
    public void setBondLength(double bondLength)
    {
        this.bondLength = bondLength;
    }

    /**
     * Sets the thickness of a bond line.
     *
     * @param   bondWidth  the thickness of a bond line
     */
    public void setBondWidth(double bondWidth)
    {
        this.bondWidth = bondWidth;
    }

    /**
     * @param rings
     */
    public void setCRings(ConjugatedRings rings)
    {
        cRings = rings;
    }

    /**
     * @param drawCarbonAtoms The drawCarbonAtoms to set.
     */
    public void setDrawCarbonAtoms(boolean drawCarbonAtoms)
    {
        this.drawCarbonAtoms = drawCarbonAtoms;
    }

    /**
     * Sets if the drawing of atom numbers is switched on for this model
     *
     * @param   drawNumbers  true if the drawing of atom numbers is to be switched on for this model
     */
    public void setDrawNumbers(boolean drawNumbers)
    {
        this.drawNumbers = drawNumbers;
    }

    /**
     * Sets the foreground color with which bonds and atoms are drawn
     *
     * @param   foreColor  the foreground color with which bonds and atoms are drawn
     */
    public void setForeColor(Color foreColor)
    {
        this.foreColor = foreColor;
    }

    /**
     * Sets the color used for highlighting things in this model
     *
     * @param   highlightColor  the color to be used for highlighting things in this model
     */
    public void setHighlightColor(Color highlightColor)
    {
        this.highlightColor = highlightColor;
    }

    /**
     * Sets the atom currently highlighted
     *
     * @param   highlightedAtom The atom to be highlighted
     */
    public void setHighlightedAtom(Atom highlightedAtom)
    {
        if ((highlightedAtoms.size() != 0) || (highlightedAtom != null))
        {
            highlightedAtoms.clear();
            highlightedAtoms.put(highlightedAtom, highlightedAtom);
            fireChange();
        }
    }

    /**
     * Sets the atom currently highlighted
     *
     * @param   highlightedAtom The atom to be highlighted
     */
    public void setHighlightedAtoms(Atom[] atoms)
    {
        if ((highlightedAtoms.size() != 0) || (atoms != null))
        {
            highlightedAtoms.clear();

            for (int i = 0; i < atoms.length; i++)
            {
                highlightedAtoms.put(atoms[i], atoms[i]);
            }

            fireChange();
        }
    }

    /**
     * Sets the Bond currently highlighted
     *
     * @param   highlightedBond  The Bond to be currently highlighted
     */
    public void setHighlightedBond(Bond highlightedBond)
    {
        if ((highlightedBonds.size() != 0) || (highlightedBond != null))
        {
            highlightedBonds.clear();
            highlightedBonds.put(highlightedBond, highlightedBond);
            fireChange();
        }
    }

    /**
            * Sets the Bond currently highlighted
            *
            * @param   highlightedBond  The Bond to be currently highlighted
            */
    public void setHighlightedBonds(Bond[] bonds)
    {
        if ((highlightedBonds.size() != 0) || (bonds != null))
        {
            highlightedBonds.clear();

            for (int i = 0; i < bonds.length; i++)
            {
                highlightedBonds.put(bonds[i], bonds[i]);
            }

            fireChange();
        }
    }

    /**
     * Sets the radius around an atoms, for which the atom is
     * marked highlighted if a pointer device is placed within this radius
     *
     * @param   highlightRadius  the highlight radius of all atoms
     */
    public void setHighlightRadius(double highlightRadius)
    {
        this.highlightRadius = highlightRadius;
    }

    public void setKekuleStructure(boolean kekule)
    {
        this.useKekuleStructure = kekule;
    }

    /**
     * @param numberColor
     */
    public void setNumberColor(Color numberColor)
    {
        this.numberColor = numberColor;
    }

    /**
     * @param lines
     */
    public void setOLines(OrthoLines lines)
    {
        oLines = lines;
    }

    /**
     * @param orthoLineColor
     */
    public void setOrthoLineColor(Color orthoLineColor)
    {
        this.orthoLineColor = orthoLineColor;
    }

    /**
     * @param orthoLineOffset
     */
    public void setOrthoLineOffset(int orthoLineOffset)
    {
        this.orthoLineOffset = orthoLineOffset;
    }

    /**
     * Sets the end of a pointer vector
     *
     * @param   pointerVectorEnd
     */
    public void setPointerVectorEnd(Point pointerVectorEnd)
    {
        this.pointerVectorEnd = pointerVectorEnd;
        fireChange();
    }

    /**
     * Sets the start point of a pointer vector
     *
     * @param   pointerVectorStart
     */
    public void setPointerVectorStart(Point pointerVectorStart)
    {
        this.pointerVectorStart = pointerVectorStart;
        fireChange();
    }

    /**
     * Returns the scale factor for the drawing
     *
     * @param   scaleFactor  the scale factor for the drawing
     */
    public void setScaleFactor(double scaleFactor)
    {
        this.scaleFactor = scaleFactor;
    }

    /**
     * Sets a selected region
     *
     * @param   selectRect
     */
    public void setSelectRect(Polygon selectRect)
    {
        this.selectRect = selectRect;
        fireChange();
    }

    public void setShowEndCarbons(boolean showThem)
    {
        this.showEndCarbons = showThem;
    }

    /**
     * Returns the zoom factor for the drawing
     *
     * @param   scaleZoom  the zoom factor for the drawing
     */
    public void setZoomFactor(double zoomFactor)
    {
        this.zoomFactor = zoomFactor;
    }

    private Color getColor(String property, Color defaultC)
    {
        int r;
        int g;
        int b;
        BasicPropertyHolder holder = BasicPropertyHolder.instance();
        r = holder.getInt(this, property + ".r", defaultC.getRed());
        g = holder.getInt(this, property + ".g", defaultC.getGreen());
        b = holder.getInt(this, property + ".b", defaultC.getBlue());

        return new Color(r, g, b);
    }

    /**
         *
         */
    private void loadProperties()
    {
        BasicPropertyHolder holder = BasicPropertyHolder.instance();
        String value;

        bondLength = holder.getDouble(this, "bond.length", DEFAULT_BOND_LENGTH);
        bondDistance = holder.getDouble(this, "bond.distance",
                DEFAULT_BOND_DISTANCE);
        bondWidth = holder.getDouble(this, "bond.width", DEFAULT_BOND_WIDTH);

        highlightRadius = holder.getDouble(this, "highlightRadius",
                DEFAULT_HIGHLIGHTRADIUS);
        scaleFactor = holder.getDouble(this, "scaleFactor",
                DEFAULT_SCALEFACTOR);
        zoomFactor = holder.getDouble(this, "zoomFactor", DEFAULT_ZOOM_FACTOR);
        atomRadius = holder.getInt(this, "atomRadius", DEFAULT_ATOM_RADIUS);
        orthoLineOffset = holder.getInt(this, "orthoLineOffset",
                DEFAULT_ORTHO_LINE_OFFSET);
        arrowOffset = holder.getInt(this, "arrowOffset", DEFAULT_ARROW_OFFSET);
        arrowSize = holder.getInt(this, "arrowSize", DEFAULT_ARROW_SIZE_OFFSET);

        aPropColoring.usePlainColoring();

        value = BasicPropertyHolder.instance().getProperty(this, "drawNumbers");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            drawNumbers = true;
        }
        else
        {
            drawNumbers = false;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "useKekuleStructure");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            useKekuleStructure = true;
        }
        else
        {
            useKekuleStructure = false;
        }

        //System.out.println("useKekuleStructure: "+useKekuleStructure);
        value = BasicPropertyHolder.instance().getProperty(this,
                "showEndCarbons");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            showEndCarbons = true;
        }
        else
        {
            showEndCarbons = false;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "atomColoring");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            useAtomColors = true;
        }
        else
        {
            useAtomColors = false;
        }

        value = BasicPropertyHolder.instance().getProperty(this,
                "drawCarbonAtoms");

        if (((value != null) && value.equalsIgnoreCase("true")))
        {
            drawCarbonAtoms = true;
        }
        else
        {
            drawCarbonAtoms = false;
        }

        backColor = getColor("background.color", DEFAULT_BACKGROUND_COLOR);
        foreColor = getColor("foreground.color", DEFAULT_FOREGROUND_COLOR);
        highlightColor = getColor("highlight.color", DEFAULT_HIGHLIGHT_COLOR);
        numberColor = getColor("number.color", DEFAULT_NUMBER_COLOR);
        conjRingColor = getColor("conjugatedRing.color",
                DEFAULT_CONJ_RING_COLOR);
        arrowColor = getColor("arrow.color", DEFAULT_ARROW_COLOR);
        orthoLineColor = getColor("orthogonalLine.color",
                DEFAULT_ORTHOLINE_COLOR);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
