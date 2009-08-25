///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Renderer2D.java,v $
//Purpose:  Renderer for a 2D layout.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.9 $
//                      $Date: 2005/02/17 16:48:32 $
//                      $Author: wegner $
//Original Author: steinbeck, gzelter, egonw
//Original Version: Copyright (C) 1997-2003
//                  The Chemistry Development Kit (CDK) project
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

import joelib2.data.BasicElementHolder;

import joelib2.feature.types.atomlabel.AtomImplicitValence;
import joelib2.feature.types.atomlabel.AtomIsCarbon;
import joelib2.feature.types.bondlabel.BondInAromaticSystem;
import joelib2.feature.types.bondlabel.BondInRing;
import joelib2.feature.types.bondlabel.BondKekuleType;

import joelib2.math.BasicVector3D;

import joelib2.molecule.Atom;
import joelib2.molecule.AtomHelper;
import joelib2.molecule.Bond;
import joelib2.molecule.BondHelper;
import joelib2.molecule.KekuleHelper;
import joelib2.molecule.Molecule;

import joelib2.ring.Ring;

import joelib2.smarts.SMARTSPatternMatcher;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.apache.log4j.Category;


/**
 * A Renderer class which draws 2D representations of molecules onto a given
 * graphics objects using information from a Renderer2DModel.
 *
 * <p>This renderer uses two coordinate systems. One that is a world
 * coordinates system which is generated from the document coordinates.
 * Additionally, the screen coordinates make up the second system, and
 * are calculated by applying a zoom factor to the world coordinates.
 *
 * @.author     steinbeck
 * @.author     egonw
 * @.author     wegnerj
 * @.license    LGPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:32 $
 */
public class Renderer2D
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.gui.render2D.Renderer2D");

    //~ Instance fields ////////////////////////////////////////////////////////

    Hashtable cachedHeaviestRing = new Hashtable();
    Hashtable cachedRingCenter = new Hashtable();
    private final int NOT_TO_CLOSE = 2;

    /**
     *  Description of the Field
     */
    private Renderer2DModel r2dm;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Constructs a Renderer2D with a default settings model.
     */
    public Renderer2D()
    {
        r2dm = new Renderer2DModel();
    }

    /**
     * @param r2dm2
     */
    public Renderer2D(Renderer2DModel r2dm2)
    {
        r2dm = r2dm2;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return
     */
    public Renderer2DModel getRenderer2DModel()
    {
        return r2dm;
    }

    public void paintBoundingBox(RenderingAtoms container, String caption,
        int side, Graphics graphics)
    {
        paintBoundingBox(container, caption, side, graphics, true, false);
    }

    public void paintBoundingBox(RenderingAtoms container, String caption,
        int side, Graphics graphics, boolean showBox, boolean labelAtBottom)
    {
        double[] minmax = RenderHelper.getMinMax(container);
        int[] ints = new int[4];
        ints[0] = (int) minmax[0] - side;
        ints[1] = (int) minmax[1] - side;
        ints[2] = (int) minmax[2] + side;
        ints[3] = (int) minmax[3] + side;

        graphics.setColor(r2dm.getForeColor());

        int[] screenCoords = getScreenCoordinates(ints);
        int heigth = screenCoords[3] - screenCoords[1];
        int width = screenCoords[2] - screenCoords[0];

        if (showBox)
        {
            graphics.drawRect((int) screenCoords[0], (int) screenCoords[1],
                width, heigth);
        }

        // draw reaction ID
        Font unscaledFont = graphics.getFont();
        int fontSize = getScreenSize(unscaledFont.getSize());
        graphics.setFont(unscaledFont.deriveFont((float) fontSize));

        if (labelAtBottom)
        {
            FontMetrics fm = graphics.getFontMetrics();
            int captionWidth = (new Integer(fm.stringWidth(caption) / 2))
                .intValue();
            int capionHeight = (new Integer(fm.getAscent() / 2)).intValue();
            graphics.drawString(caption,
                (int) ((screenCoords[2] + screenCoords[0]) / 2) - captionWidth,
                (int) screenCoords[3] + capionHeight + (NOT_TO_CLOSE * 2));
        }
        else
        {
            graphics.drawString(caption, (int) screenCoords[0],
                (int) screenCoords[1] - NOT_TO_CLOSE);
        }

        graphics.setFont(unscaledFont);
    }

    /**
     *  triggers the methods to make the molecule fit into the frame and to paint
     *  it.
     *
     *@param  atomCon  Description of the Parameter
     *@param  graphics        Description of the Parameter
     */
    public void paintMolecule(RenderingAtoms atomCon, Graphics graphics)
    {
        if ((r2dm.getPointerVectorStart() != null) &&
                (r2dm.getPointerVectorEnd() != null))
        {
            paintPointerVector(graphics);
        }

        paintBonds(atomCon, atomCon.getRenderRings(), graphics);
        paintAtoms(atomCon, graphics);
        paintNumbers(atomCon, atomCon.getRenderAtomCount(), graphics);

        if (r2dm.getArrows() != null)
        {
            paintArrows(r2dm.getArrows(), atomCon, graphics);
        }

        if (r2dm.getOLines() != null)
        {
            paintOrthogonalLines(r2dm.getOLines(), atomCon, graphics);
        }

        if (r2dm.getCRings() != null)
        {
            paintDelocalizedRing(r2dm.getCRings(), atomCon, graphics);
        }

        if (r2dm.getSelectRect() != null)
        {
            graphics.setColor(r2dm.getHighlightColor());
            graphics.drawPolygon(r2dm.getSelectRect());
        }

        paintLassoLines(graphics);
    }

    public void paintReaction(Molecule[] reactants, Molecule[] products,
        String caption, Graphics graphics)
    {
        RenderingAtoms container = new RenderingAtoms();
        RenderingAtoms reactantContainer = new RenderingAtoms();
        RenderingAtoms productContainer = new RenderingAtoms();

        for (int i = 0; i < reactants.length; i++)
        {
            container.add(reactants[i]);
            reactantContainer.add(reactants[i]);
        }

        for (int i = 0; i < products.length; i++)
        {
            container.add(products[i]);
            productContainer.add(products[i]);
        }

        paintBoundingBox(container, caption, 20, graphics);

        // paint reactants content
        paintBoundingBox(reactantContainer, "Reactants", 10, graphics);
        paintMolecule(reactantContainer, graphics);

        // paint products content
        paintBoundingBox(productContainer, "Products", 10, graphics);
        paintMolecule(productContainer, graphics);
    }

    public void selectSMARTSPatterns(RenderingAtoms atomCon,
        SMARTSPatternMatcher smarts)
    {
        if (smarts == null)
        {
            return;
        }

        Molecule mol;
        List matchList;
        Vector highlight = new Vector();
        int[] itmp;

        for (int i = 0; i < atomCon.getRenderFragments().size(); i++)
        {
            mol = (Molecule) atomCon.getRenderFragments().get(i);
            smarts.match(mol);
            matchList = smarts.getMatchesUnique();

            for (int j = 0; j < matchList.size(); j++)
            {
                itmp = (int[]) matchList.get(j);

                for (int k = 0; k < itmp.length; k++)
                {
                    highlight.add(mol.getAtom(itmp[k]));

                    //System.out.println("SMARTS matching: "+(this.getAtomNumber(mol.getAtom(itmp[k]))+1));
                }
            }
        }

        Atom[] atoms = new Atom[highlight.size()];

        for (int i = 0; i < highlight.size(); i++)
        {
            atoms[i] = (Atom) highlight.get(i);
        }

        getRenderer2DModel().setHighlightedAtoms(atoms);
    }

    public void selectSMARTSPatterns(Molecule mol, RenderingAtoms atomCon,
        SMARTSPatternMatcher smarts)
    {
        List matchList;
        Vector highlight = new Vector();
        RenderAtom ra;
        int[] itmp;

        if (!smarts.match(mol))
        {
            //System.out.println("No match");
            return;
        }

        matchList = smarts.getMatchesUnique();

        for (int j = 0; j < matchList.size(); j++)
        {
            itmp = (int[]) matchList.get(j);

            for (int k = 0; k < itmp.length; k++)
            {
                ra = atomCon.getRenderAtom(mol.getAtom(itmp[k]));
                highlight.add(ra.frAtom);

                //System.out.println("SMARTS matching: "+ra.frAtom.getIdx());
            }
        }

        Atom[] atoms = new Atom[highlight.size()];

        for (int i = 0; i < highlight.size(); i++)
        {
            atoms[i] = (Atom) highlight.get(i);
        }

        getRenderer2DModel().setHighlightedAtoms(atoms);
    }

    /**
     *  Paints the given bond as a dashed wedge bond.
     *
     *@param  bond       The singlebond to be drawn
     *@param  bondColor  Description of the Parameter
     */
    void paintDashedWedgeBond(Bond bond, Color bondColor, Graphics graphics)
    {
        graphics.setColor(bondColor);

        double bondLength = BondHelper.getLength(bond);
        int numberOfLines = (int) (bondLength / 4.0);

        // this value should be made customizable
        double wedgeWidth = r2dm.getBondWidth() * 2.0;

        // this value should be made customazible
        double widthStep = wedgeWidth / (double) numberOfLines;
        Point2d p1 = new Point2d(bond.getBegin().get3Dx(),
                bond.getBegin().get3Dy());
        Point2d p2 = new Point2d(bond.getEnd().get3Dx(),
                bond.getEnd().get3Dy());

        //              if (bond.isWedge()) {
        //                      // draw the wedge bond the other way around
        //                      p1 = new Point2d(bond.getEndAtom().getX(),bond.getEndAtom().getY());
        //                      p2 = new Point2d(bond.getBeginAtom().getX(),bond.getBeginAtom().getY());
        //              }
        Vector2d lengthStep = new Vector2d(p2);
        lengthStep.sub(p1);
        lengthStep.scale(1.0 / numberOfLines);

        Vector2d p = RenderHelper.calculatePerpendicularUnitVector(p1, p2);

        Point2d currentPoint = new Point2d(p1);
        Point2d q1 = new Point2d();
        Point2d q2 = new Point2d();

        for (int i = 0; i <= numberOfLines; ++i)
        {
            Vector2d offset = new Vector2d(p);
            offset.scale(i * widthStep);
            q1.add(currentPoint, offset);
            q2.sub(currentPoint, offset);

            int[] lineCoords = {(int) q1.x, (int) q1.y, (int) q2.x, (int) q2.y};
            lineCoords = getScreenCoordinates(lineCoords);
            graphics.drawLine(lineCoords[0], lineCoords[1], lineCoords[2],
                lineCoords[3]);
            currentPoint.add(lengthStep);
        }
    }

    /**
     *  Paints the given bond as a wedge bond.
     *
     *@param  bond       The singlebond to be drawn
     *@param  bondColor  Description of the Parameter
     */
    void paintWedgeBond(Bond bond, Color bondColor, Graphics graphics)
    {
        double wedgeWidth = r2dm.getBondWidth() * 2.0;

        // this value should be made customazible
        int[] coords = RenderHelper.getBondCoordinates(bond);
        graphics.setColor(bondColor);

        int[] newCoords = RenderHelper.distanceCalculator(coords, wedgeWidth);

        if (bond.isUp())
        {
            int[] xCoords = {coords[0], newCoords[6], newCoords[4]};
            int[] yCoords = {coords[1], newCoords[7], newCoords[5]};
            xCoords = getScreenCoordinates(xCoords);
            yCoords = getScreenCoordinates(yCoords);
            graphics.fillPolygon(xCoords, yCoords, 3);
        }
        else
        {
            int[] xCoords = {coords[2], newCoords[0], newCoords[2]};
            int[] yCoords = {coords[3], newCoords[1], newCoords[3]};
            xCoords = getScreenCoordinates(xCoords);
            yCoords = getScreenCoordinates(yCoords);
            graphics.fillPolygon(xCoords, yCoords, 3);
        }
    }

    /**
             * We define the heaviest ring as the one with the highest number of double bonds.
             * Needed for example for the placement of in-ring double bonds.
             *
             * @param   bond  A bond which must be contained by the heaviest ring
             * @return  The ring with the higest number of double bonds connected to a given bond
             */
    private Ring getHeaviestRing(Ring[] ringSet, Bond bond)
    {
        if (cachedHeaviestRing.containsKey(bond))
        {
            return (Ring) cachedHeaviestRing.get(bond);
        }

        int maxOrderSum = 0;
        Ring ring = null;
        int[] bonds;
        int tmpSum;
        Ring maxRing = null;

        for (int i = 0; i < ringSet.length; i++)
        {
            ring = ringSet[i];

            if (logger.isDebugEnabled())
            {
                logger.debug("check ring " + ring);
            }

            if (!ring.isMember(bond))
            {
                continue;
            }

            bonds = ring.getBonds();
            tmpSum = 0;

            for (int j = 0; j < bonds.length; j++)
            {
                tmpSum += ring.getParent().getBond(bonds[j]).getBondOrder();
            }

            if (maxOrderSum < tmpSum)
            {
                maxRing = ring;
                maxOrderSum = tmpSum;
            }
        }

        if (maxRing != null)
        {
            cachedHeaviestRing.put(bond, maxRing);
        }
        else
        {
            logger.error("No ring found for bond " + bond.getBeginIndex() +
                bond + bond.getEndIndex());
        }

        return maxRing;
    }

    private Point getScreenCoordinates(Point p)
    {
        Point screenCoordinate = new Point();
        double zoomFactor = r2dm.getZoomFactor();
        screenCoordinate.x = (int) ((double) p.x * zoomFactor);
        screenCoordinate.y = (int) ((double) p.y * zoomFactor);

        return screenCoordinate;
    }

    private int[] getScreenCoordinates(int[] coords)
    {
        int[] screenCoordinates = new int[coords.length];
        double zoomFactor = r2dm.getZoomFactor();

        for (int i = 0; i < coords.length; i++)
        {
            screenCoordinates[i] = (int) ((double) coords[i] * zoomFactor);
        }

        return screenCoordinates;
    }

    private int getScreenSize(int size)
    {
        return (int) ((double) size * r2dm.getZoomFactor());
    }

    private void paintArrow(Atom[] from, Atom[] to, boolean alignRight,
        Graphics graphics)
    {
        paintArrow(from, to, alignRight, graphics,
            getRenderer2DModel().getArrowColor(),
            getRenderer2DModel().getArrowOffset(),
            getRenderer2DModel().getArrowSize());
    }

    private void paintArrow(Molecule mol, int[] fromIdx, int[] toIdx,
        boolean alignRight, RenderingAtoms atomCon, Graphics graphics)
    {
        Atom[] from = new Atom[fromIdx.length];
        Atom[] to = new Atom[toIdx.length];

        for (int i = 0; i < from.length; i++)
        {
            from[i] = atomCon.getRenderAtom(mol.getAtom(fromIdx[i])).frAtom;
        }

        for (int i = 0; i < to.length; i++)
        {
            to[i] = atomCon.getRenderAtom(mol.getAtom(toIdx[i])).frAtom;
        }

        paintArrow(from, to, alignRight, graphics,
            getRenderer2DModel().getArrowColor(),
            getRenderer2DModel().getArrowOffset(),
            getRenderer2DModel().getArrowSize());
    }

    private void paintArrow(Atom[] from, Atom[] to, boolean alignRight,
        Graphics graphics, Color color, int offset, int arrowSize)
    {
        double fromX = 0.0;
        double fromY = 0.0;

        for (int i = 0; i < from.length; i++)
        {
            if (from[i] == null)
            {
                logger.error("from atom " + i + " not found.");

                return;
            }

            fromX += from[i].get3Dx();
            fromY += from[i].get3Dy();
        }

        fromX = fromX / (double) from.length;
        fromY = fromY / (double) from.length;

        double toX = 0.0;
        double toY = 0.0;

        for (int i = 0; i < to.length; i++)
        {
            if (to[i] == null)
            {
                logger.error("to atom " + i + " not found.");

                return;
            }

            toX += to[i].get3Dx();
            toY += to[i].get3Dy();
        }

        toX = toX / (double) to.length;
        toY = toY / (double) to.length;

        double dx = toX - fromX;
        double dy = toY - fromY;
        BasicVector3D xyz = new BasicVector3D(dx, dy, 0);
        BasicVector3D ortho = new BasicVector3D();
        xyz.createOrthoXYZVector(ortho);
        ortho.normalize();

        if (alignRight)
        {
            ortho.muling(-1);
        }

        graphics.setColor(color);

        //graphics.drawLine((int)fromX,(int)fromY,(int)((double)fromX+ortho._vx*(double)offset),(int)((double)fromY+ortho._vy*(double)offset));
        double px = fromX + (dx * 0.25) + (ortho.x3D * offset * 0.75);
        double py = fromY + (dy * 0.25) + (ortho.y3D * offset * 0.75);

        graphics.drawLine((int) fromX, (int) fromY, (int) px, (int) py);

        double px2 = fromX + (dx * 0.5) + (ortho.x3D * offset);
        double py2 = fromY + (dy * 0.5) + (ortho.y3D * offset);
        graphics.drawLine((int) px, (int) py, (int) px2, (int) py2);
        px = fromX + (dx * 0.75) + (ortho.x3D * offset * 0.75);
        py = fromY + (dy * 0.75) + (ortho.y3D * offset * 0.75);
        graphics.drawLine((int) px, (int) py, (int) px2, (int) py2);
        graphics.drawLine((int) px, (int) py, (int) toX, (int) toY);

        // draw end of arrow
        int[] arrowX = new int[3];
        int[] arrowY = new int[3];
        dx = toX - px2;
        dy = toY - py2;
        xyz = new BasicVector3D(dx, dy, 0);
        xyz.normalize();
        ortho = new BasicVector3D();
        xyz.createOrthoXYZVector(ortho);
        ortho.normalize();
        px = toX - ((xyz.x3D * arrowSize) + (ortho.x3D * arrowSize));
        py = toY - ((xyz.y3D * arrowSize) + (ortho.y3D * arrowSize));
        arrowX[0] = (int) px;
        arrowY[0] = (int) py;

        //graphics.drawLine((int)px,(int)py,(int)toX,(int)toY);
        ortho.muling(-1);
        px = toX - ((xyz.x3D * arrowSize) + (ortho.x3D * arrowSize));
        py = toY - ((xyz.y3D * arrowSize) + (ortho.y3D * arrowSize));
        arrowX[1] = (int) px;
        arrowY[1] = (int) py;
        arrowX[2] = (int) toX;
        arrowY[2] = (int) toY;

        //graphics.drawLine((int)px,(int)py,(int)toX,(int)toY);
        graphics.fillPolygon(arrowX, arrowY, 3);
    }

    private void paintArrows(Arrows arrows, RenderingAtoms atomCon,
        Graphics graphics)
    {
        Arrow arrow;

        for (int i = 0; i < arrows.arrows.length; i++)
        {
            arrow = arrows.arrows[i];

            if (logger.isDebugEnabled())
            {
                logger.debug("paint arrow: " + arrow);
            }

            if ((arrow.from != null) && (arrow.to != null))
            {
                paintArrow(arrows.molecule, arrow.from, arrow.to,
                    arrow.alignRight, atomCon, graphics);
            }
        }
    }

    private void paintAtom(RenderingAtoms container, Atom atom,
        Graphics graphics)
    {
        Color atomForeColor = r2dm.getAtomForeColor(atom);
        Color atomBackColor = r2dm.getAtomBackColor(atom);

        if (r2dm.isAtomHighlighted(atom))
        {
            //atomForeColor = r2dm.getForeColor();
            //atomBackColor = r2dm.getHighlightColor();
            atomForeColor = r2dm.getHighlightColor();
            atomBackColor = r2dm.getBackColor();
            paintColouredAtom(atom, r2dm.getHighlightColor(), graphics);
        }
        else
        {
            paintColouredAtom(atom, atomForeColor, graphics);
        }

        int alignment = RenderHelper.getBestAlignmentForLabel(container, atom);

        if (!AtomIsCarbon.isCarbon(atom) || r2dm.isDrawCarbonAtoms())
        {
            /*
             *  only show element for non-carbon atoms,
             *  unless (see below)...
             */
            paintAtomSymbol(atom, atomForeColor, atomBackColor, graphics,
                alignment);
            paintAtomCharge(atom, graphics);
        }
        else if (r2dm.getKekuleStructure())
        {
            // ... unless carbon must be drawn because in Kekule mode
            paintAtomSymbol(atom, atomForeColor, atomBackColor, graphics,
                alignment);
        }
        else if (atom.getFormalCharge() != 0)
        {
            // ... unless carbon is charged
            paintAtomSymbol(atom, atomForeColor, atomBackColor, graphics,
                alignment);
            paintAtomCharge(atom, graphics);
        }
        else if (atom.getValence() < 1)
        {
            // ... unless carbon is unbonded
            paintAtomSymbol(atom, atomForeColor, atomBackColor, graphics,
                alignment);
        }
        else if (r2dm.getShowEndCarbons() && (atom.getValence() == 1))
        {
            // ... unless carbon is an methyl, and the user wants those with symbol
            paintAtomSymbol(atom, atomForeColor, atomBackColor, graphics,
                alignment);
        }
    }

    /**
     *  Paints the given atom symbol. It first outputs some empty space using the
     *  background color, slightly larger than the space that the symbol occupies.
     *  The atom symbol is then printed into the empty space.
     *
     *@param  atom       The atom to be drawn
     *@param  backColor  Description of the Parameter
     */
    private void paintAtomCharge(Atom atom, Graphics graphics)
    {
        FontMetrics fm = graphics.getFontMetrics();
        int xSymbOffset =
            (new Integer(
                    fm.stringWidth(
                        BasicElementHolder.instance().getSymbol(
                            atom.getAtomicNumber())) / 2)).intValue();
        int ySymbOffset = (new Integer(fm.getAscent() / 2)).intValue();

        // show formal charge
        if (atom.getFormalCharge() != 0)
        {
            // print charge in smaller font size
            Font unscaledFont = graphics.getFont();
            int fontSize = getScreenSize(unscaledFont.getSize() - 1);
            graphics.setFont(unscaledFont.deriveFont((float) fontSize));

            int charge = atom.getFormalCharge();
            String chargeString = (new Integer(charge)).toString();

            if (charge == 1)
            {
                chargeString = "+";
            }
            else if (charge > 1)
            {
                chargeString = charge + "+";
            }
            else if (charge == -1)
            {
                chargeString = "-";
            }
            else if (charge < -1)
            {
                chargeString = chargeString.substring(1) + "-";
            }

            // draw string
            int[] hCoords =
                {
                    (int) atom.get3Dx() + xSymbOffset,
                    (int) atom.get3Dy() - ySymbOffset
                };
            hCoords = getScreenCoordinates(hCoords);
            graphics.drawString(chargeString, hCoords[0], hCoords[1]);

            /** Put circles around + or - sign
            Rectangle2D stringBounds = fm.getStringBounds(chargeString, graphics);
            int width = (int)stringBounds.getWidth();
            int height = (int)stringBounds.getHeight();
            int[] coords = {(int)atom.getX2D() + xSymbOffset - (width/2),
                            (int)atom.getY2D() - ySymbOffset - (height/2),
                            (int)stringBounds.getWidth(),
                            (int)stringBounds.getWidth()};
            coords = getScreenCoordinates(coords);
            graphics.drawOval(coords[0], coords[1], coords[2], coords[3]); */
            graphics.setFont(unscaledFont);
        }
    }

    /**
     *  Searches through all the atoms in the given array of atoms, triggers the
     *  paintColouredAtoms method if the atom has got a certain color and triggers
     *  the paintAtomSymbol method if the symbol of the atom is not C.
     *
     *@param  atomCon  Description of the Parameter
     */
    private void paintAtoms(RenderingAtoms atomCon, Graphics graphics)
    {
        Atom[] atoms = atomCon.getRenderAtoms();

        for (int i = 0; i < atoms.length; i++)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("atom " + atoms[i].getIndex() + ": " +
                    atoms[i].getCoords3D());
            }

            paintAtom(atomCon, atoms[i], graphics);
        }
    }

    /**
     *  Paints the given atom symbol. It first outputs some empty space using the
     *  background color, slightly larger than the space that the symbol occupies.
     *  The atom symbol is then printed into the empty space.
     *
     *@param  atom       The atom to be drawn
     *@param  backColor  Description of the Parameter
     */
    private void paintAtomSymbol(Atom atom, Color foreColor, Color backColor,
        Graphics graphics, int alignment)
    {
        if ((atom.get3Dx() == 0.0) && (atom.get3Dy() == 0.0))
        {
            return;
        }

        // but first determine symbol
        String symbol = BasicElementHolder.instance().getSymbol(atom
                .getAtomicNumber());

        // if there are implicit hydrogens, add them to string to display
        int implicitHydrogen = AtomImplicitValence.getImplicitValence(atom) -
            atom.getValence();
        boolean alignSubscriptLeft = false;

        if (implicitHydrogen > 0)
        {
            List bonds;

            if ((bonds = atom.getBonds()).size() == 1)
            {
                // use more clever layout for atoms with only heavy atom neigbour and one hydrogen
                Bond bond = (Bond) bonds.get(0);
                Atom nbr = bond.getNeighbor(atom);

                if (implicitHydrogen == 1)
                {
                    if ((atom.get3Dx() - nbr.get3Dx()) >= 0)
                    {
                        symbol = symbol + "H";
                    }
                    else
                    {
                        symbol = "H" + symbol;
                    }
                }
                else
                {
                    if ((atom.get3Dx() - nbr.get3Dx()) >= 0)
                    {
                        symbol = symbol + "H";
                    }
                    else
                    {
                        symbol = "H " + symbol;
                        alignSubscriptLeft = true;
                    }
                }
            }
            else
            {
                symbol = symbol + "H";
            }
        }

        // draw string:

        /* determine where to put the string, as seen from the atom coordinates
           in model coordinates */
        FontMetrics fm = graphics.getFontMetrics();

        // left align
        int xSymbOffset =
            (new Integer(fm.stringWidth(symbol.substring(0, 1)) / 2))
            .intValue();

        if (alignment == -1)
        {
            // right align
            xSymbOffset =
                (new Integer(
                        (fm.stringWidth(
                                symbol.substring(symbol.length() - 1)) / 2) +
                        fm.stringWidth(symbol.substring(1)))).intValue();
        }

        int ySymbOffset = (new Integer(fm.getAscent() / 2)).intValue();

        int xSymbOffsetForSubscript = (new Integer(fm.stringWidth(symbol)))
            .intValue();
        int ySymbOffsetForSubscript = (new Integer(fm.getAscent())).intValue();

        // make empty space
        graphics.setColor(backColor);

        Rectangle2D stringBounds = fm.getStringBounds(symbol, graphics);
        int[] coords =
            {
                (int) (atom.get3Dx() - (xSymbOffset * 1.2)),
                (int) (atom.get3Dy() - (ySymbOffset * 1.2)),
                (int) (stringBounds.getWidth() * 1.2),
                (int) (stringBounds.getHeight() * 1.2)
            };
        coords = getScreenCoordinates(coords);
        graphics.fillRect(coords[0], coords[1], coords[2], coords[3]);

        int[] hCoords =
            {
                (int) (atom.get3Dx() - xSymbOffset),
                (int) (atom.get3Dy() + ySymbOffset)
            };
        hCoords = getScreenCoordinates(hCoords);
        graphics.setColor(foreColor);

        // apply zoom factor to font size
        Font unscaledFont = graphics.getFont();
        int fontSize = getScreenSize(unscaledFont.getSize());
        graphics.setFont(unscaledFont.deriveFont((float) fontSize));
        graphics.drawString(symbol, hCoords[0], hCoords[1]);
        graphics.setColor(r2dm.getForeColor());

        if (logger.isDebugEnabled())
        {
            logger.debug("draw atom " + atom.getIndex() + ": " + hCoords[0] +
                " " + hCoords[1]);
        }

        if (implicitHydrogen > 1)
        {
            // draw subscript part
            int[] h2Coords;

            if (alignSubscriptLeft)
            {
                xSymbOffsetForSubscript = (new Integer(fm.stringWidth("H")))
                    .intValue();
            }

            h2Coords =
                new int[]
                {
                    (int) (atom.get3Dx() - xSymbOffset +
                        xSymbOffsetForSubscript),
                    (int) (atom.get3Dy() + ySymbOffsetForSubscript)
                };

            h2Coords = getScreenCoordinates(h2Coords);
            graphics.setColor(r2dm.getForeColor());

            // apply zoom factor to font size
            unscaledFont = graphics.getFont();
            fontSize = getScreenSize(unscaledFont.getSize()) - 1;
            graphics.setFont(unscaledFont.deriveFont((float) fontSize));
            graphics.setColor(foreColor);

            if (h2Coords[0] < 0)
            {
                h2Coords[0] = 0;
            }

            graphics.drawString(Integer.toString(implicitHydrogen), h2Coords[0],
                h2Coords[1]);
        }

        // reset old font
        graphics.setFont(unscaledFont);
    }

    /**
     *  Triggers the paint method suitable to the bondorder of the given bond.
     *
     *@param  bond       The Bond to be drawn.
     *@param  bondColor  Description of the Parameter
     */
    private void paintBond(Bond bond, Color bondColor, Graphics graphics)
    {
        if ((bond.getBegin() == null) || (bond.getEnd() == null))
        {
            return;
        }

        if (bond.isWedge() || bond.isHash())
        {
            // Draw stero information if available
            if (bond.isHash())
            {
                paintWedgeBond(bond, bondColor, graphics);
            }
            else
            {
                paintDashedWedgeBond(bond, bondColor, graphics);
            }
        }
        else
        {
            // Draw bond order when no stereo info is available
            if (bond.isSingle())
            {
                paintSingleBond(bond, bondColor, graphics);
            }
            else if (bond.isDouble())
            {
                paintDoubleBond(bond, bondColor, graphics);
            }
            else if (bond.isTriple())
            {
                paintTripleBond(bond, bondColor, graphics);
            }
        }
    }

    /**
     *  Triggers the suitable method to paint each of the given bonds and selects
     *  the right color.
     *
     *@param  ringSet  The set of rings the molecule contains
     *@param  atomCon  Description of the Parameter
     */
    private void paintBonds(RenderingAtoms atomCon, Ring[] ringSet,
        Graphics graphics)
    {
        Color bondColor;
        Ring ring;
        Bond bond;
        List bonds = atomCon.getRenderBonds();
        Hashtable delocRingCache = new Hashtable();

        for (int i = 0; i < bonds.size(); i++)
        {
            bond = (Bond) bonds.get(i);
            bondColor = r2dm.getBondColor(bond);

            if (bondColor == null)
            {
                bondColor = r2dm.getForeColor();
            }

            if (r2dm.isBondHighlighted(bond))
            {
                bondColor = r2dm.getHighlightColor();
                paintColouredAtom(bond.getBegin(), bondColor, graphics);
                paintColouredAtom(bond.getEnd(), bondColor, graphics);
            }

            if (BondInRing.isInRing(bond))
            {
                ring = getHeaviestRing(ringSet, bond);

                if (logger.isDebugEnabled())
                {
                    logger.debug("bond " + bond.getBeginIndex() + bond +
                        bond.getEndIndex() + " in ring " + ring);
                }

                if (ring != null)
                {
                    paintRingBond(atomCon, bond, ring, bondColor, graphics,
                        delocRingCache);
                }
                else
                {
                    paintBond(bond, bondColor, graphics);
                }
            }
            else
            {
                paintBond(bond, bondColor, graphics);
            }
        }
    }

    /**
     *  Paints a rectangle of the given color at the position of the given atom.
     *  For example when the atom is highlighted.
     *
     *@param  atom   The atom to be drawn
     *@param  color  The color of the atom to be drawn
     */
    private void paintColouredAtom(Atom atom, Color color, Graphics graphics)
    {
        int atomRadius = r2dm.getAtomRadius();
        graphics.setColor(color);

        int[] coords =
            {
                (int) atom.get3Dx() - (atomRadius / 2),
                (int) atom.get3Dy() - (atomRadius / 2), atomRadius, atomRadius
            };
        coords = getScreenCoordinates(coords);
        graphics.fillRect(coords[0], coords[1], coords[2], coords[3]);
    }

    private void paintDelocalizedRing(ConjugatedRings cRings,
        RenderingAtoms atomCon, Graphics graphics)
    {
        ConjugatedRing cRing;
        Atom[] renderAtoms;
        Atom atom;

        for (int i = 0; i < cRings.cRings.length; i++)
        {
            cRing = cRings.cRings[i];

            if (logger.isDebugEnabled())
            {
                logger.debug("paint conjRing: " + cRing);
            }

            if (cRing.ring != null)
            {
                renderAtoms = new Atom[cRing.ring.length];

                for (int j = 0; j < cRing.ring.length; j++)
                {
                    atom = cRings.molecule.getAtom(cRing.ring[j]);
                    renderAtoms[j] = atomCon.getRenderAtom(atom).frAtom;

                    //System.out.println(atom.getIdx()+" "+atomCon.getRenderAtomNumber(renderAtoms[j]));
                }

                paintDelocalizedRing(renderAtoms, r2dm.getConjRingColor(),
                    graphics, cRing.charge);
            }
        }
    }

    private void paintDelocalizedRing(Atom[] renderAtoms, Color ringColor,
        Graphics graphics, String label)
    {
        double cX = 0.0;
        double cY = 0.0;

        for (int i = 0; i < renderAtoms.length; i++)
        {
            cX += renderAtoms[i].get3Dx();
            cY += renderAtoms[i].get3Dy();
        }

        cX = cX / renderAtoms.length;
        cY = cY / renderAtoms.length;

        Atom atom1;
        Atom atom2;
        double dx;
        double dy;
        double minR = Double.MAX_VALUE;

        for (int i = 1; i < renderAtoms.length; i++)
        {
            atom1 = renderAtoms[i - 1];
            atom2 = renderAtoms[i];
            dx = Math.abs(((atom1.get3Dx() + atom2.get3Dx()) / 2) - cX);
            dy = Math.abs(((atom1.get3Dy() + atom2.get3Dy()) / 2) - cY);
            minR = Math.min(Math.sqrt((dx * dx) + (dy * dy)), minR);
        }

        atom1 = renderAtoms[0];
        atom2 = renderAtoms[renderAtoms.length - 1];
        dx = Math.abs(((atom1.get3Dx() + atom2.get3Dx()) / 2) - cX);
        dy = Math.abs(((atom1.get3Dy() + atom2.get3Dy()) / 2) - cY);
        minR = Math.min(Math.sqrt((dx * dx) + (dy * dy)), minR);

        double r = minR * 1.5;
        double r_2 = r / 2;
        graphics.setColor(ringColor);

        int s = 45;
        int s_2 = s / 2;

        for (int i = 0; i < 360; i += s)
        {
            graphics.drawArc((int) (cX - r_2), (int) (cY - r_2), (int) r,
                (int) r, i, s_2);
            graphics.drawArc((int) (cX - r_2 - (NOT_TO_CLOSE / 2)),
                (int) (cY - r_2 - (NOT_TO_CLOSE / 2)), (int) r + NOT_TO_CLOSE,
                (int) r + NOT_TO_CLOSE, i, s_2);
        }

        if (label != null)
        {
            FontMetrics fm = graphics.getFontMetrics();
            int labelWidth_2 = (new Integer(fm.stringWidth(label) / 2))
                .intValue();
            int labelHeight_2 = (new Integer(fm.getAscent() / 2)).intValue();
            graphics.setColor(getRenderer2DModel().getForeColor());
            graphics.drawString(label, (int) (cX - labelWidth_2),
                (int) (cY + labelHeight_2));

            //            if (label.length() == 1)
            //            {
            //                int rr = Math.max(labelWidth_2 + (NOT_TO_CLOSE * 2),
            //                        labelHeight_2 + (NOT_TO_CLOSE * 2));
            //                graphics.drawOval((int) (cX - (rr / 2)), (int) (cY - (rr / 2)),
            //                    rr, rr);
            //            }
            //            else
            //            {
            //                graphics.drawRect((int) (cX - labelWidth_2 - NOT_TO_CLOSE),
            //                    (int) (cY - labelHeight_2 - NOT_TO_CLOSE),
            //                    (labelWidth_2 * 2) + (NOT_TO_CLOSE * 2),
            //                    (labelHeight_2 * 2) + (NOT_TO_CLOSE * 2));
            //            }
        }
    }

    private void paintDelocalizedRing(RenderingAtoms atomCon, Ring ring,
        Color ringColor, Graphics graphics, Hashtable delocRingCache)
    {
        if (delocRingCache != null)
        {
            if (delocRingCache.containsKey(ring))
            {
                return;
            }

            delocRingCache.put(ring, "");
        }

        int[] rAtoms = ring.getAtomIndices();
        Atom[] rrAtoms = new Atom[rAtoms.length];
        Atom atom;

        for (int i = 0; i < rAtoms.length; i++)
        {
            atom = ring.getParent().getAtom(rAtoms[i]);
            rrAtoms[i] = atomCon.getRenderAtomAtom(atomCon.getRenderAtomNumber(
                        atom));
        }

        paintDelocalizedRing(rrAtoms, ringColor, graphics, null);
    }

    /**
     *  Paints The given doublebond.
     *
     *@param  bond       The doublebond to be drawn
     *@param  bondColor  Description of the Parameter
     */
    private void paintDoubleBond(Bond bond, Color bondColor, Graphics graphics)
    {
        int[] coords = RenderHelper.distanceCalculator(RenderHelper
                .getBondCoordinates(bond), r2dm.getBondDistance() / 2);

        int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
        paintOneBond(newCoords1, bondColor, graphics);

        int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
        paintOneBond(newCoords2, bondColor, graphics);
    }

    /**
     *  Paints the inner bond of a doublebond that is part of a ring.
     *
     *@param  bond       The bond to be drawn
     *@param  ring       The ring the bond is part of
     *@param  bondColor  Description of the Parameter
     */
    private void paintInnerBond(Bond bond, Ring ring, Color bondColor,
        Graphics graphics)
    {
        BasicVector3D centerXYZ = new BasicVector3D();
        BasicVector3D norm1 = new BasicVector3D();
        BasicVector3D norm2 = new BasicVector3D();

        //              if(cachedRingCenter.containsKey(ring))
        //              {
        //                      centerXYZ=(XYZVector)cachedRingCenter.get(ring);
        //              }
        //              else
        //              {
        ring.findCenterAndNormal(centerXYZ, norm1, norm2);

        //                      cachedRingCenter.put(ring, centerXYZ);
        //              }
        if (logger.isDebugEnabled())
        {
            logger.debug(

                //logger.info(
            "bond " + bond.getBeginIndex() + bond + bond.getEndIndex() +
                " in ring " + ring + " center " + centerXYZ);
            graphics.drawLine((int) centerXYZ.x3D - 2, (int) centerXYZ.y3D - 2,
                (int) centerXYZ.x3D + 2, (int) centerXYZ.y3D + 2);
            graphics.drawLine((int) centerXYZ.x3D - 2, (int) centerXYZ.y3D + 2,
                (int) centerXYZ.x3D + 2, (int) centerXYZ.y3D - 2);
        }

        Point2d center = new Point2d(centerXYZ.getX3D(), centerXYZ.getY3D());

        int[] coords = RenderHelper.distanceCalculator(RenderHelper
                .getBondCoordinates(bond),
                ((r2dm.getBondWidth() / 2) + r2dm.getBondDistance()));
        double dist1 = Math.sqrt(Math.pow((coords[0] - center.x), 2) +
                Math.pow((coords[1] - center.y), 2));
        double dist2 = Math.sqrt(Math.pow((coords[2] - center.x), 2) +
                Math.pow((coords[3] - center.y), 2));

        if (dist1 < dist2)
        {
            int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
            paintOneBond(shortenBond(newCoords1, ring.size()), bondColor,
                graphics);
        }
        else
        {
            int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
            paintOneBond(shortenBond(newCoords2, ring.size()), bondColor,
                graphics);
        }
    }

    /**
     *  Description of the Method
     */
    private void paintLassoLines(Graphics graphics)
    {
        List points = r2dm.getLassoPoints();

        if (points.size() > 1)
        {
            Point point1 = (Point) points.get(0);
            Point point2;

            for (int i = 1; i < points.size(); i++)
            {
                point2 = (Point) points.get(i);
                graphics.drawLine(point1.x, point1.y, point2.x, point2.y);
                point1 = point2;
            }
        }
    }

    /*
     *  Paints the numbers
     *
     *  @param   atom    The atom to be drawn
     */

    /**
     *  Description of the Method
     *
     *@param  atom  Description of the Parameter
     */
    private void paintNumber(RenderingAtoms container, Atom atom,
        Graphics graphics)
    {
        if ((atom.get3Dx() == 0.0) && (atom.get3Dy() == 0.0))
        {
            return;
        }

        int index = container.getRenderAtomNumber(atom);

        if (!r2dm.drawNumbers() && !container.hasRenderAtomLabel(index))
        {
            return;
        }

        FontMetrics fm = graphics.getFontMetrics();
        String showAlso = "";

        if (container.hasRenderAtomLabel(index))
        {
            showAlso = container.getRenderAtomLabel(index);
        }

        int number = container.getRenderAtomNumber(atom) + 1;
        String numberString = Integer.toString(number);

        if (!r2dm.drawNumbers())
        {
            numberString = "";
        }
        else
        {
            if (container.hasRenderAtomLabel(index))
            {
                numberString = numberString + ",";
            }
        }

        int numberWidth =
            (new Integer(fm.stringWidth(numberString + showAlso) / 2))
            .intValue();
        int symbolWidth2 =
            (new Integer(
                    fm.stringWidth(
                        BasicElementHolder.instance().getSymbol(
                            atom.getAtomicNumber())) / 2)).intValue();
        int xSymbOffset = symbolWidth2;
        int ySymbOffset = (new Integer(fm.getAscent() / 2)).intValue();

        BasicVector3D newPos = new BasicVector3D();
        AtomHelper.getNewBondVector3D(atom, newPos, 1.0);
        newPos.x3D -= atom.get3Dx();
        newPos.y3D -= atom.get3Dy();
        newPos.y3D *= -1;
        newPos.z3D = 0;
        newPos.normalize();

        double radius = Math.sqrt((symbolWidth2 * symbolWidth2 * 4) +
                (ySymbOffset * ySymbOffset * 4));
        int height = (new Integer(fm.getAscent() / 2)).intValue();
        xSymbOffset = (int) (newPos.getX3D() * radius) - numberWidth;
        ySymbOffset = (int) (newPos.getY3D() * radius) - height;

        if (ySymbOffset > 0)
        {
            ySymbOffset = Math.max(ySymbOffset, height);
        }

        if (xSymbOffset < 0)
        {
            // move left number for hetero atoms or terminal methyl groups
            if ((atom.getAtomicNumber() != 6) ||
                    ((atom.getAtomicNumber() == 6) && (atom.getValence() <=
                            1)))
            {
                xSymbOffset -= (symbolWidth2 + numberWidth);

                if (atom.getAtomicNumber() == 6)
                {
                    xSymbOffset -= (symbolWidth2 / 2);
                }
            }
        }

        //System.out.println("atom "+atom.getIdx()+" "+newPos+" "+xSymbOffset+" "+ySymbOffset);
        try
        {
            //              graphics.setColor(r2dm.getBackColor());
            //              graphics.fillRect((int)(atom.getPoint2D().x - (xSymbOffset * 1.8)),(int)(atom.getPoint2D().y - (ySymbOffset * 0.8)),(int)fontSize,(int)fontSize);
            graphics.setColor(r2dm.getNumberColor());

            double x = atom.get3Dx() + xSymbOffset;

            if (x < 0)
            {
                x = 0;
            }

            graphics.drawString(numberString + showAlso, (int) x,
                (int) (atom.get3Dy() - (ySymbOffset)));
            graphics.setColor(r2dm.getBackColor());
            graphics.drawLine((int) atom.get3Dx(), (int) atom.get3Dy(),
                (int) atom.get3Dx(), (int) atom.get3Dy());
        }
        catch (Exception exception)
        {
            logger.error("Error while drawing atom number:" +
                exception.toString());
        }
    }

    /**
     *  Draw all numbers of all atoms in the molecule
     *
     *@param  atoms   The array of atoms
     *@param  number  The number of atoms in this array
     */
    private void paintNumbers(RenderingAtoms container, int number,
        Graphics graphics)
    {
        Atom[] atoms = container.getRenderAtoms();

        for (int i = 0; i < number; i++)
        {
            paintNumber(container, atoms[i], graphics);
        }
    }

    /**
     *  Really paints the bond. It is triggered by all the other paintbond methods
     *  to draw a polygon as wide as bondwidth.
     *
     *@param  coords
     *@param  bondColor  Description of the Parameter
     */
    private void paintOneBond(int[] coords, Color bondColor, Graphics graphics)
    {
        graphics.setColor(bondColor);

        int[] newCoords = RenderHelper.distanceCalculator(coords,
                r2dm.getBondWidth() / 2);
        int[] xCoords =
            {newCoords[0], newCoords[2], newCoords[4], newCoords[6]};
        int[] yCoords =
            {newCoords[1], newCoords[3], newCoords[5], newCoords[7]};
        xCoords = getScreenCoordinates(xCoords);
        yCoords = getScreenCoordinates(yCoords);
        graphics.fillPolygon(xCoords, yCoords, 4);
    }

    private void paintOrthogonalLine(Atom[] from, Atom[] to, String label,
        boolean lAligRight, Graphics graphics)
    {
        paintOrthogonalLine(from, to, label, lAligRight, graphics,
            getRenderer2DModel().getOrthoLineColor(),
            (int) getRenderer2DModel().getOrthoLineOffset());
    }

    private void paintOrthogonalLine(Molecule mol, int[] fromIdx, int[] toIdx,
        String label, boolean lAligRight, RenderingAtoms atomCon,
        Graphics graphics)
    {
        Atom[] from = new Atom[fromIdx.length];
        Atom[] to = new Atom[toIdx.length];

        for (int i = 0; i < to.length; i++)
        {
            from[i] = atomCon.getRenderAtom(mol.getAtom(fromIdx[i])).frAtom;
            to[i] = atomCon.getRenderAtom(mol.getAtom(toIdx[i])).frAtom;
        }

        paintOrthogonalLine(from, to, label, lAligRight, graphics,
            getRenderer2DModel().getOrthoLineColor(),
            (int) getRenderer2DModel().getOrthoLineOffset());
    }

    private void paintOrthogonalLine(Atom[] from, Atom[] to, String label,
        boolean lAligRight, Graphics graphics, Color color, int offset)
    {
        double fromX = 0.0;
        double fromY = 0.0;

        for (int i = 0; i < from.length; i++)
        {
            if (from[i] == null)
            {
                logger.error("from atom " + i + " not found.");

                return;
            }

            fromX += from[i].get3Dx();
            fromY += from[i].get3Dy();
        }

        fromX = fromX / (double) from.length;
        fromY = fromY / (double) from.length;

        double toX = 0.0;
        double toY = 0.0;

        for (int i = 0; i < to.length; i++)
        {
            if (to[i] == null)
            {
                logger.error("to atom " + i + " not found.");

                return;
            }

            toX += to[i].get3Dx();
            toY += to[i].get3Dy();
        }

        toX = toX / (double) to.length;
        toY = toY / (double) to.length;

        double dx = toX - fromX;
        double dy = toY - fromY;
        BasicVector3D xyz = new BasicVector3D(dx, dy, 0);
        BasicVector3D ortho = new BasicVector3D();
        xyz.createOrthoXYZVector(ortho);
        ortho.normalize();
        xyz.normalize();

        int px1 = (int) (fromX + (dx * 0.5) + (ortho.x3D * offset));
        int py1 = (int) (fromY + (dy * 0.5) + (ortho.y3D * offset));
        int px2 = (int) ((fromX + (dx * 0.5)) - (ortho.x3D * offset));
        int py2 = (int) ((fromY + (dy * 0.5)) - (ortho.y3D * offset));

        graphics.setColor(color);
        graphics.drawLine(px1, py1, px2, py2);

        FontMetrics fm = graphics.getFontMetrics();
        int w = (new Integer(fm.stringWidth(label) / 2)).intValue();
        int h = (new Integer(fm.getAscent() / 2)).intValue();
        double r = Math.sqrt((w * w) + (h * h));

        if (label != null)
        {
            if (lAligRight)
            {
                graphics.drawString(label,
                    (int) ((double) px1 + (ortho.x3D * r)),
                    (int) (((double) py1 + (ortho.y3D * r)) - (xyz.y3D * h)));
            }
            else
            {
                graphics.drawString(label,
                    (int) ((double) px2 - (ortho.x3D * r)),
                    (int) ((double) py2 - (ortho.y3D * r) - (xyz.y3D * h)));
            }
        }
    }

    private void paintOrthogonalLines(OrthoLines oLines, RenderingAtoms atomCon,
        Graphics graphics)
    {
        OrthoLine oLine;

        for (int i = 0; i < oLines.orthoLines.length; i++)
        {
            oLine = oLines.orthoLines[i];

            if (logger.isDebugEnabled())
            {
                logger.debug("paint orthoLine: " + oLine);
            }

            if ((oLine.from != null) && (oLine.to != null))
            {
                paintOrthogonalLine(oLines.molecule, oLine.from, oLine.to,
                    oLine.label, oLine.alignRight, atomCon, graphics);
            }
        }
    }

    /**
     *  Paints a line between the startpoint and endpoint of the pointervector that
     *  is stored in the Renderer2DModel.
     */
    private void paintPointerVector(Graphics graphics)
    {
        Point startPoint = r2dm.getPointerVectorStart();
        Point endPoint = r2dm.getPointerVectorEnd();
        int[] points = {startPoint.x, startPoint.y, endPoint.x, endPoint.y};
        int[] newCoords = RenderHelper.distanceCalculator(points,
                r2dm.getBondWidth() / 2);
        int[] xCoords =
            {newCoords[0], newCoords[2], newCoords[4], newCoords[6]};
        int[] yCoords =
            {newCoords[1], newCoords[3], newCoords[5], newCoords[7]};
        graphics.setColor(r2dm.getForeColor());

        // apply zoomFactor
        xCoords = getScreenCoordinates(xCoords);
        yCoords = getScreenCoordinates(yCoords);
        graphics.fillPolygon(xCoords, yCoords, 4);
    }

    /**
     *  Triggers the paint method suitable to the bondorder of the given bond that
     *  is part of a ring.
     *
     *@param  bond       The Bond to be drawn.
     *@param  ring       Description of the Parameter
     *@param  bondColor  Description of the Parameter
     */
    private void paintRingBond(RenderingAtoms atomCon, Bond bond, Ring ring,
        Color bondColor, Graphics graphics, Hashtable delocRingCache)
    {
        if (getRenderer2DModel().getKekuleStructure())
        {
            if (BondKekuleType.getKekuleType(bond) ==
                    KekuleHelper.KEKULE_SINGLE)
            {
                if (bond.isDown() || bond.isUp() || bond.isWedge() ||
                        bond.isHash())
                {
                    // Draw stero information if available
                    if (bond.isHash())
                    {
                        paintWedgeBond(bond, bondColor, graphics);
                    }
                    else
                    {
                        paintDashedWedgeBond(bond, bondColor, graphics);
                    }
                }
                else
                {
                    paintSingleBond(bond, bondColor, graphics);
                }
            }
            else if (BondKekuleType.getKekuleType(bond) ==
                    KekuleHelper.KEKULE_DOUBLE)
            {
                paintSingleBond(bond, bondColor, graphics);
                paintInnerBond(bond, ring, bondColor, graphics);
            }
            else if (BondKekuleType.getKekuleType(bond) ==
                    KekuleHelper.KEKULE_TRIPLE)
            {
                paintTripleBond(bond, bondColor, graphics);
            }
        }
        else
        {
            if (bond.isSingle())
            {
                if (bond.isDown() || bond.isUp() || bond.isWedge() ||
                        bond.isHash())
                {
                    // Draw stero information if available
                    if (bond.isHash())
                    {
                        paintWedgeBond(bond, bondColor, graphics);
                    }
                    else
                    {
                        paintDashedWedgeBond(bond, bondColor, graphics);
                    }
                }
                else
                {
                    paintSingleBond(bond, bondColor, graphics);
                }
            }
            else if (bond.isDouble())
            {
                paintSingleBond(bond, bondColor, graphics);
                paintInnerBond(bond, ring, bondColor, graphics);
            }
            else if (BondInAromaticSystem.isAromatic(bond))
            {
                paintSingleBond(bond, bondColor, graphics);

                if (getRenderer2DModel().isBondHighlighted(bond))
                {
                    paintDelocalizedRing(atomCon, ring,
                        getRenderer2DModel().getHighlightColor(), graphics,
                        delocRingCache);
                }
                else
                {
                    paintDelocalizedRing(atomCon, ring,
                        getRenderer2DModel().getConjRingColor(), graphics,
                        delocRingCache);
                }

                //paintInnerBond(bond, ring, getRenderer2DModel().getAromaticBondColor(), graphics);
            }
            else if (bond.isTriple())
            {
                paintTripleBond(bond, bondColor, graphics);
            }
        }
    }

    /**
     *  Paints the given singlebond.
     *
     *@param  bond       The singlebond to be drawn
     *@param  bondColor  Description of the Parameter
     */
    private void paintSingleBond(Bond bond, Color bondColor, Graphics graphics)
    {
        if (RenderHelper.has2DCoordinates(bond))
        {
            paintOneBond(RenderHelper.getBondCoordinates(bond), bondColor,
                graphics);
        }
    }

    /**
     *  Paints the given triplebond.
     *
     *@param  bond       The triplebond to be drawn
     *@param  bondColor  Description of the Parameter
     */
    private void paintTripleBond(Bond bond, Color bondColor, Graphics graphics)
    {
        paintSingleBond(bond, bondColor, graphics);

        int[] coords = RenderHelper.distanceCalculator(RenderHelper
                .getBondCoordinates(bond),
                ((r2dm.getBondWidth() / 2) + r2dm.getBondDistance()));

        int[] newCoords1 = {coords[0], coords[1], coords[6], coords[7]};
        paintOneBond(newCoords1, bondColor, graphics);

        int[] newCoords2 = {coords[2], coords[3], coords[4], coords[5]};
        paintOneBond(newCoords2, bondColor, graphics);
    }

    /**
     *  Calculates the coordinates for the inner bond of a doublebond that is part
     *  of a ring. It is drawn shorter than a normal bond.
     *
     *@param  coords  The original coordinates of the bond
     *@param  edges   Number of edges of the ring it is part of
     *@return         The calculated coordinates of the now shorter bond
     */
    private int[] shortenBond(int[] coords, int edges)
    {
        int xDiff = (coords[0] - coords[2]) / (edges * 2);
        int yDiff = (coords[1] - coords[3]) / (edges * 2);
        int[] newCoords =
            {
                coords[0] - xDiff, coords[1] - yDiff, coords[2] + xDiff,
                coords[3] + yDiff
            };

        return newCoords;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
