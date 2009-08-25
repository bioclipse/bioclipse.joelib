///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DFS.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
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
package joelib2.algo;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.BasicFeatureDescription;
import joelib2.feature.BasicFeatureInfo;
import joelib2.feature.Feature;
import joelib2.feature.FeatureDescription;
import joelib2.feature.FeatureException;
import joelib2.feature.FeatureResult;
import joelib2.feature.ResultFactory;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.util.BasicProperty;
import joelib2.util.PropertyHelper;

import joelib2.util.iterator.NbrAtomIterator;

import java.util.Map;

import org.apache.log4j.Category;


/**
 * Depth First Search.
 *
 * @.author     wegnerj
 * @.wikipedia    Depth-first search
 * @.wikipedia    Graph (data structure)
 * @.wikipedia    Graph theory
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:28 $
 * @.cite clr98complexity
 * @.cite clr98dfs
 */
public class DFS implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.9 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:28 $";
    private static final Class[] DEPENDENCIES = new Class[]{};
    private static Category logger = Category.getInstance(DFS.class.getName());
    public final static String STARTING_ATOM = "STARTING_ATOM";
    private final static BasicProperty[] ACCEPTED_PROPERTIES =
        new BasicProperty[]
        {
            new BasicProperty(STARTING_ATOM, "joelib2.molecule.Atom",
                "The start atom.", true),
        };
    private final static int WHITE = 0;
    private final static int GRAY = 1;
    private final static int BLACK = 2;

    //~ Instance fields ////////////////////////////////////////////////////////

    private int[] color;

    private BasicFeatureInfo descInfo;

    private int[] discovered;
    private int[] finished;
    private int[] parent;
    private Atom startAtom;
    private int traverseTime;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape1 object
     */
    public DFS()
    {
        String representation = this.getClass().getName();
        descInfo = new BasicFeatureInfo(getName(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, representation,
                "docs/algo/DepthFirstSearch", "joelib2.algo.DFSInit",
                "joelib2.algo.DFSResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return DFS.class.getName();
    }

    public static String getReleaseDate()
    {
        return VENDOR;
    }

    public static String getReleaseVersion()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_VERSION);
    }

    public static String getVendor()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_DATE);
    }

    public BasicProperty[] acceptedProperties()
    {
        return ACCEPTED_PROPERTIES;
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  FeatureException  Description of the Exception
     */
    public FeatureResult calculate(Molecule mol) throws FeatureException
    {
        FeatureResult result = ResultFactory.instance().getFeatureResult(
                descInfo.getName());

        if (startAtom == null)
        {
            startAtom = mol.getAtom(1);
        }

        return calculate(mol, result, null);
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  initData                 Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  FeatureException  Description of the Exception
     */
    public FeatureResult calculate(Molecule mol, Map properties)
        throws FeatureException
    {
        FeatureResult result = ResultFactory.instance().getFeatureResult(
                descInfo.getName());

        return calculate(mol, result, properties);
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  descResult               Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  FeatureException  Description of the Exception
     */
    public FeatureResult calculate(Molecule mol, FeatureResult descResult)
        throws FeatureException
    {
        if (startAtom == null)
        {
            startAtom = mol.getAtom(1);
        }

        return calculate(mol, descResult, null);
    }

    /**
     *  Description of the Method
     *
     * @param  mol                      Description of the Parameter
     * @param  initData                 Description of the Parameter
     * @param  descResult               Description of the Parameter
     * @return                          Description of the Return Value
     * @exception  FeatureException  Description of the Exception
     */
    public FeatureResult calculate(Molecule mol, FeatureResult descResult,
        Map properties) throws FeatureException
    {
        DFSResult result = null;
        int atoms = mol.getAtomsSize();

        if (mol.isEmpty())
        {
            logger.error("Empty molecule '" + mol.getTitle() + "'.");
        }
        else
        {
            // check if the result type is correct
            if (!(descResult instanceof DFSResult))
            {
                logger.error(descInfo.getName() + " result should be of type " +
                    DFSResult.class.getName() + " but it's of type " +
                    descResult.getClass().toString());
            }
            else
            {
                // initialize result type, if not already initialized
                result = (DFSResult) descResult;

                if (result.getDiscovered() == null)
                {
                    result.setDiscovered(new int[atoms]);
                    result.setFinished(new int[atoms]);
                    result.setParent(new int[atoms]);
                }
                else if (result.getDiscovered().length != atoms)
                {
                    result.setDiscovered(new int[atoms]);
                    result.setFinished(new int[atoms]);
                    result.setParent(new int[atoms]);
                }
            }

            boolean isInitialized = true;

            // check if the init type is correct
            if (properties == null)
            {
                if (startAtom == null)
                {
                    logger.error(descInfo.getName() +
                        " initData is not defined. Please define: " +
                        STARTING_ATOM);

                    isInitialized = false;
                }
            }
            else
            {
                if (!initialize(properties))
                {
                    isInitialized = false;
                }
            }

            if (isInitialized)
            {
                calculateDFS(mol, result);
            }
        }

        return result;
    }

    /**
     *  Description of the Method
     */
    public void clear()
    {
        this.setDiscovered(null);
        this.setFinished(null);
        this.setParent(null);
        this.setColor(null);
        this.setTraverseTime(0);
        this.setStartAtom(null);
    }

    /**
     * @return Returns the color.
     */
    public int[] getColor()
    {
        return color;
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public BasicFeatureInfo getDescInfo()
    {
        return descInfo;
    }

    /**
     *  Gets the description attribute of the Descriptor object
     *
     * @return    The description value
     */
    public FeatureDescription getDescription()
    {
        return new BasicFeatureDescription(descInfo.getDescriptionFile());
    }

    /**
     * @return Returns the discovered.
     */
    public int[] getDiscovered()
    {
        return discovered;
    }

    /**
     * @return Returns the finished.
     */
    public int[] getFinished()
    {
        return finished;
    }

    /**
     * @return Returns the parent.
     */
    public int[] getParent()
    {
        return parent;
    }

    /**
     * @return Returns the startAtom.
     */
    public Atom getStartAtom()
    {
        return startAtom;
    }

    /**
     * @return Returns the traverseTime.
     */
    public int getTraverseTime()
    {
        return traverseTime;
    }

    public int hashedDependencyTreeVersion()
    {
        return IdentifierExpertSystem.getDependencyTreeHash(getName());
    }

    /**
     *  Description of the Method
     *
     * @param  initData  Description of the Parameter
     * @return           Description of the Return Value
     */
    public boolean initialize(Map properties)
    {
        if (!PropertyHelper.checkProperties(this, properties))
        {
            logger.error(
                "Empty property definition or missing property entry.");

            return false;
        }

        Atom atom = (Atom) PropertyHelper.getProperty(this, STARTING_ATOM,
                properties);

        if (atom == null)
        {
            this.setStartAtom(null);
        }
        else
        {
            this.setStartAtom(atom);
        }

        return true;
    }

    /**
     * @param color The color to set.
     */
    public void setColor(int[] color)
    {
        this.color = color;
    }

    /**
     * @param discovered The discovered to set.
     */
    public void setDiscovered(int[] discovered)
    {
        this.discovered = discovered;
    }

    /**
     * @param finished The finished to set.
     */
    public void setFinished(int[] finished)
    {
        this.finished = finished;
    }

    /**
     * @param parent The parent to set.
     */
    public void setParent(int[] parent)
    {
        this.parent = parent;
    }

    /**
     * @param startAtom The startAtom to set.
     */
    public void setStartAtom(Atom startAtom)
    {
        this.startAtom = startAtom;
    }

    /**
     * @param traverseTime The traverseTime to set.
     */
    public void setTraverseTime(int traverseTime)
    {
        this.traverseTime = traverseTime;
    }

    /**
     * Test the implementation of this descriptor.
     *
     * @return <tt>true</tt> if the implementation is correct
     */
    public boolean testDescriptor()
    {
        return true;
    }

    /**
     * @param mol
     */
    private void calculateDFS(Molecule mol, DFSResult result)
    {
        int atoms = mol.getAtomsSize();

        // create temporary arrays
        // position 0 will be unused, because the
        // lowest atom index will be 1 !
        finished = new int[atoms + 1];
        discovered = new int[atoms + 1];
        parent = new int[atoms + 1];
        color = new int[atoms + 1];

        // all atoms are not visited
        for (int i = 0; i < atoms; i++)
        {
            color[i] = WHITE;
            discovered[i] = Integer.MAX_VALUE;
            finished[i] = Integer.MAX_VALUE;
            parent[i] = -1;
        }

        // start with this atom
        visit(startAtom);

        // visit now all nodes that are still WHITE (not visited)
        for (int i = 1; i <= atoms; i++)
        {
            if (color[i] == WHITE)
            {
                visit(mol.getAtom(i));
            }
        }

        // create temporary array contents to
        // result arrays
        System.arraycopy(finished, 1, result.getFinished(), 0, atoms);
        System.arraycopy(discovered, 1, result.getDiscovered(), 0, atoms);
        System.arraycopy(parent, 1, result.getParent(), 0, atoms);
    }

    /**
     *  Description of the Method
     *
     * @param  atom  Description of the Parameter
     */
    private void visit(Atom atom)
    {
        // mark node as developed
        color[atom.getIndex()] = GRAY;

        // store time when node was discovered
        discovered[atom.getIndex()] = ++traverseTime;

        NbrAtomIterator nbrIterator;
        nbrIterator = atom.nbrAtomIterator();

        Atom nbr;

        while (nbrIterator.hasNext())
        {
            nbr = nbrIterator.nextNbrAtom();

            // visit undeveloped neighbour node
            if (color[nbr.getIndex()] == WHITE)
            {
                parent[nbr.getIndex()] = atom.getIndex();
                visit(nbr);
            }
        }

        // mark node as developed and finished
        color[atom.getIndex()] = BLACK;

        // store time when node was finished
        finished[atom.getIndex()] = ++traverseTime;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
