///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Node.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.4 $
//          $Date: 2005/02/17 16:48:36 $
//          $Author: wegner $
//
//Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                     Tuebingen, Germany, 2001,2002,2003,2004,2005
//Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                     2003,2004,2005
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation version 2 of the License.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
///////////////////////////////////////////////////////////////////////////////
package joelib2.molecule;

import joelib2.util.iterator.EdgeIterator;
import joelib2.util.iterator.NbrNodeIterator;

import java.util.List;


/**
 *
 * @.author       wegner
 * @.wikipedia Graph (mathematics)
 * @.license      GPL
 * @.cvsversion   $Revision: 1.4 $, $Date: 2005/02/17 16:48:36 $
 */
public interface Node
{
    //~ Methods ////////////////////////////////////////////////////////////////

    EdgeIterator edgeIterator();

    Edge getEdge(Node nbr);

    List getEdges();

    int getEdgesSize();

    int getIndex();

    Graph getParent();

    int getValence();

    NbrNodeIterator nbrNodeIterator();

    void setEdges(List<Edge> edges);

    void setIndex(int idx);

    void setParent(Graph ptr);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
