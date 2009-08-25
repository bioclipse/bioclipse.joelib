///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BFS.java,v $
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

import wsi.ra.tool.Deque;
import wsi.ra.tool.DequeNode;

import java.util.Map;

import org.apache.log4j.Category;


/**
 *  Breadh First Search.
 *
 * @.author     wegnerj
 * @.wikipedia    Breadth-first search
 * @.wikipedia    Graph (data structure)
 * @.wikipedia    Graph theory
 * @.license GPL
 * @.cvsversion    $Revision: 1.9 $, $Date: 2005/02/17 16:48:28 $
 * @.cite clr98complexity
 * @.cite clr98bfs
 */
public class BFS implements Feature
{
    //~ Static fields/initializers /////////////////////////////////////////////

    public final static String STARTING_ATOM = "STARTING_ATOM";
    private final static BasicProperty[] ACCEPTED_PROPERTIES =
        new BasicProperty[]
        {
            new BasicProperty(STARTING_ATOM,
                joelib2.molecule.Atom.class.getName(), "The start atom.", true),
        };

    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.9 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:28 $";
    private static final Class[] DEPENDENCIES = new Class[]{};
    private static Category logger = Category.getInstance(BFS.class.getName());
    private final static int WHITE = 0;
    private final static int GRAY = 1;
    private final static int BLACK = 2;

    //~ Instance fields ////////////////////////////////////////////////////////

    private BasicFeatureInfo descInfo;

    private Atom startAtom;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the KierShape1 object
     */
    public BFS()
    {
        String representation = this.getClass().getName();
        descInfo = new BasicFeatureInfo(getName(),
                BasicFeatureInfo.TYPE_NO_COORDINATES, representation,
                "docs/algo/BreadthFirstSearch", "joelib2.algo.BFSInit",
                "joelib2.algo.BFSResult");
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    public static String getName()
    {
        return BFS.class.getName();
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
        BFSResult result = null;

        if (mol.isEmpty())
        {
            logger.error("Empty molecule '" + mol.getTitle() + "'.");
        }
        else
        {
            // check if the result type is correct
            if (!(descResult instanceof BFSResult))
            {
                logger.error(descInfo.getName() + " result should be of type " +
                    BFSResult.class.getName() + " but it's of type " +
                    descResult.getClass().toString());

                return null;
            }
            else
            {
                // initialize result type, if not already initialized
                result = (BFSResult) descResult;

                int atoms = mol.getAtomsSize();

                if (result.getTraverse() == null)
                {
                    result.setTraverse(new int[atoms]);
                    result.setParent(new int[atoms]);
                }
                else if (result.getTraverse().length != atoms)
                {
                    result.setTraverse(new int[atoms]);
                    result.setParent(new int[atoms]);
                }

                // check if the init type is correct
                boolean isInitialized = true;

                if (properties == null)
                {
                    if (startAtom == null)
                    {
                        logger.error(descInfo.getName() +
                            " properties is not defined. Please define: " +
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
                    calculateBFS(mol, result);
                }
            }
        }

        return result;
    }

    /**
     *  Description of the Method
     */
    public void clear()
    {
        setStartAtom(null);
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
     * @return Returns the startAtom.
     */
    public Atom getStartAtom()
    {
        return startAtom;
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
        boolean allFine = true;

        if (!PropertyHelper.checkProperties(this, properties))
        {
            logger.error(
                "Empty property definition or missing property entry.");

            allFine = false;
        }
        else
        {
            Atom atom = (Atom) PropertyHelper.getProperty(this, STARTING_ATOM,
                    properties);

            if (atom == null)
            {
                setStartAtom(null);
            }
            else
            {
                setStartAtom(atom);
            }
        }

        return allFine;
    }

    /**
     * @param startAtom The startAtom to set.
     */
    public void setStartAtom(Atom startAtom)
    {
        this.startAtom = startAtom;
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
     * @param result
     */
    private void calculateBFS(Molecule mol, BFSResult result)
    {
        // create temporary arrays
        // position 0 will be unused, because the
        // lowest atom index will be 1 !
        int atoms = mol.getAtomsSize();
        int[] traverse = new int[atoms + 1];
        int[] parent = new int[atoms + 1];
        int[] color = new int[atoms + 1];
        Deque fifo = new Deque();

        // all atoms are not visited
        for (int i = 0; i < (atoms + 1); i++)
        {
            color[startAtom.getIndex()] = WHITE;
            traverse[i] = Integer.MAX_VALUE;
            parent[i] = -1;
        }

        // start atom for the first time visited
        color[startAtom.getIndex()] = GRAY;
        traverse[startAtom.getIndex()] = 0;

        // add start node to the FIFO
        fifo.pushBack(startAtom);

        NbrAtomIterator nbrIterator;

        // repeat until the unfinished stack contains elements
        while (!fifo.isEmpty())
        {
            Atom atom = (Atom) ((DequeNode) fifo.getFront()).key;
            nbrIterator = atom.nbrAtomIterator();

            Atom nbr;

            while (nbrIterator.hasNext())
            {
                nbr = nbrIterator.nextNbrAtom();

                // visit undeveloped neighbour node
                if (color[nbr.getIndex()] == WHITE)
                {
                    // mark as developed
                    color[nbr.getIndex()] = GRAY;

                    // set time number node has been traversed
                    traverse[nbr.getIndex()] = traverse[atom.getIndex()] + 1;

                    // set previous visited node
                    parent[nbr.getIndex()] = atom.getIndex();

                    // add this unfinished node to the FIFO
                    fifo.pushBack(nbr);
                }
            }

            // mark node as finished and remove it from the stack
            fifo.popFront();
            color[atom.getIndex()] = BLACK;
        }

        // create temporary array contents to
        // result arrays
        System.arraycopy(traverse, 1, result.getTraverse(), 0, atoms);
        System.arraycopy(parent, 1, result.getParent(), 0, atoms);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
