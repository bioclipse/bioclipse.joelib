package joelib2.gui.render3D.graphics3D;

import joelib2.gui.render3D.molecule.ViewerAtom;
import joelib2.gui.render3D.molecule.ViewerBond;
import joelib2.gui.render3D.molecule.ViewerMolecule;
import joelib2.gui.render3D.util.MolViewerEvent;

import java.util.Enumeration;
import java.util.ListIterator;

import javax.media.j3d.Appearance;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;

import javax.vecmath.Color3f;

import org.apache.log4j.Category;

import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.4 $
 */
public class PickHighlightBehavior extends PickMouseBehavior
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.gui.render3D.graphics3D.PickHighlightBehavior");

    //~ Instance fields ////////////////////////////////////////////////////////

    Appearance highlightAppearance;
    boolean marked;
    Shape3D[] oldShape = new Shape3D[10];
    Appearance[] savedAppearance = new Appearance[10];
    MolecularScene scene;
    int stored = 0;

    //~ Constructors ///////////////////////////////////////////////////////////

    public PickHighlightBehavior(MolecularScene _scene, Canvas3D canvas,
        BranchGroup root, Bounds bounds)
    {
        super(canvas, root, bounds);
        this.setSchedulingBounds(bounds);
        root.addChild(this);

        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f highlightColor = new Color3f(0.0f, 1.0f, 0.0f);

        //              Material highlightMaterial =
        //                      new Material(highlightColor, black, highlightColor, white, 80.0f);
        highlightAppearance = new Appearance();
        highlightAppearance.setMaterial(new Material(highlightColor, black,
                highlightColor, white, 80.0f));

        pickCanvas.setMode(PickTool.GEOMETRY);

        //pickCanvas.setMode(PickTool.BOUNDS);
        scene = _scene;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void updateScene(int xpos, int ypos)
    {
        PickResult pickResult = null;
        Shape3D shape = null;

        //              Group group = null;
        //              BranchGroup bGroup = null;
        //              Primitive prim = null;
        pickCanvas.setShapeLocation(xpos, ypos);

        pickResult = pickCanvas.pickClosest();

        if (pickResult != null)
        {
            //Object obj=pickResult.getObject();
            //if(obj!=null)System.out.println(obj.getClass().getName());
            shape = (Shape3D) pickResult.getNode(PickResult.SHAPE3D);

            //group=(Group) pickResult.getNode(PickResult.GROUP);
            //bGroup=(BranchGroup) pickResult.getNode(PickResult.BRANCH_GROUP);
            //prim=(Primitive) pickResult.getNode(PickResult.PRIMITIVE);
        }

        //System.out.println(shape + " " + group + " " + bGroup + " " + prim);
        clearHighlight();

        if (shape != null)
        {
            ViewerMolecule vMol = null;

            for (Enumeration e = scene.getMolecules(); e.hasMoreElements();)
            {
                vMol = (ViewerMolecule) e.nextElement();

                //vMol
                ViewerBond vBond = (ViewerBond) vMol.pickBondMapping.get(shape);

                if (vBond != null)
                {
                    ListIterator lit = vBond.shapes.listIterator();
                    int index = 0;

                    while (lit.hasNext())
                    {
                        shape = (Shape3D) lit.next();
                        savedAppearance[index] = shape.getAppearance();
                        oldShape[index] = shape;
                        shape.setAppearance(highlightAppearance);

                        //System.out.println("mark " + savedAppearance[index]);
                        index++;
                    }

                    stored = index;

                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Bond " + vBond.getId() + " picked.");
                    }

                    scene.fireEvent(new MolViewerEvent(scene,
                            MolViewerEvent.BOND_PICKED, vBond.getJOEBond()));
                }

                ViewerAtom vAtom = (ViewerAtom) vMol.pickAtomMapping.get(shape);

                if (vAtom != null)
                {
                    ListIterator lit = vAtom.shapes.listIterator();
                    int index = 0;

                    while (lit.hasNext())
                    {
                        shape = (Shape3D) lit.next();
                        savedAppearance[index] = shape.getAppearance();
                        oldShape[index] = shape;
                        shape.setAppearance(highlightAppearance);

                        //System.out.println("mark " + savedAppearance[index]);
                        index++;
                    }

                    stored = index;

                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Atom " + vAtom.getId() + " picked.");
                    }

                    scene.fireEvent(new MolViewerEvent(scene,
                            MolViewerEvent.ATOM_PICKED, vAtom.getJOEAtom()));
                }
            }
        }
    }

    void clearHighlight()
    {
        for (int i = 0; i < stored; i++)
        {
            if (oldShape[i] != null)
            {
                oldShape[i].setAppearance(savedAppearance[i]);
                oldShape[i] = null;
                savedAppearance[i] = null;

                //System.out.println("unmark " + savedAppearance[i]);
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
