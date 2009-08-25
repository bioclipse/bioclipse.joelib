///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Edge.java,v $
//Purpose:  TODO description.
//Language: Java
//Compiler: JDK 1.4
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.2 $
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

/**
 *
 * @.author       wegner
 * @.wikipedia Graph (mathematics)
 * @.license      GPL
 * @.cvsversion   $Revision: 1.2 $, $Date: 2005/02/17 16:48:36 $
 */
public interface Edge
{
    //~ Methods ////////////////////////////////////////////////////////////////

    Atom getBegin();

    int getBeginIndex();

    Atom getEnd();

    int getEndIndex();

    int getIndex();

    Node getNeighbor(Node ptr);

    int getNeighborIndex(Node ptr);

    Graph getParent();

    void setBegin(Node begin);

    void setEnd(Node end);

    void setIndex(int idx);

    void setParent(Graph ptr);
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
