///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicNbrNodeIterator.java,v $
//  Purpose:  Iterator for the standard Vector.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:42 $
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
package joelib2.util.iterator;

import joelib2.molecule.Edge;
import joelib2.molecule.Node;

import java.util.List;


/**
 * Gets an iterator over all neighbour atoms in a atom.
 *
 * <blockquote><pre>
 * NbrAtomIterator nait = atom.nbrAtomIterator();
 * Bond bond;
 * Atom nbrAtom;
 * while (nait.hasNext())
 * {
 *          nbrAtom=nait.nextNbrAtom();
 *   bond = nait.actualBond();
 *
 * }
 * </pre></blockquote>
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:42 $
 * @see VectorIterator
 * @see joelib2.molecule.Atom#nbrAtomIterator()
 */
public class BasicNbrNodeIterator extends BasicListIterator
    implements NbrNodeIterator
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private Node node;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the NbrAtomIterator object
     *
     * @param  bonds  Description of the Parameter
     * @param  atom   Description of the Parameter
     */
    public BasicNbrNodeIterator(List<Edge> edges, Node node)
    {
        super(edges);
        this.node = node;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Object actual()
    {
        Edge edge = (Edge) super.actual();

        return edge.getNeighbor(node);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Edge actualEdge()
    {
        return (Edge) super.actual();
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Object next()
    {
        Edge edge = (Edge) super.next();

        return edge.getNeighbor(node);
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public Node nextNbrNode()
    {
        return (Node) next();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
