///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicQueryBondSpecification.java,v $
//  Purpose:  Bond specification of a SMARTS bond expression.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:39 $
//            $Author: wegner $
//  Original Author: ???, OpenEye Scientific Software
//  Original Version: babel 2.0a1
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
package joelib2.smarts;

import joelib2.smarts.bondexpr.QueryBond;


/**
 * Bond specification of a SMARTS bond expression.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:39 $
 */
public class BasicQueryBondSpecification implements java.io.Serializable,
    QueryBondSpecification
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Representation of the SMARTS bond expression.
     */
    public QueryBond bond;

    /**
     * Index of the SMARTS atom expression which is the destination of this bond expression.
     */
    public int destination;

    /**
     * Growing evaluation flag.
     *  <tt>true</tt> if this bond expression should evaluate the destination
     *  atom expression or <tt>false</tt> if the destination atom expression
     *  has already been evaluated. If <tt>false</tt> only the bond expression
     *  is evaluated (faster), typically the closure bond of a ring.
     */
    public boolean grow;

    /**
     * Index of the SMARTS atom expression which is the source of this bond expression.
     */
    public int source;

    /**
     * Visited flag.
     */
    public int visit;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the bond.
     */
    public QueryBond getBond()
    {
        return bond;
    }

    /**
     * @return Returns the destination.
     */
    public int getDestination()
    {
        return destination;
    }

    /**
     * @return Returns the source.
     */
    public int getSource()
    {
        return source;
    }

    /**
     * @return Returns the visit.
     */
    public int getVisit()
    {
        return visit;
    }

    /**
     * @return Returns the grow.
     */
    public boolean isGrow()
    {
        return grow;
    }

    /**
     * @param bond The bond to set.
     */
    public void setBond(QueryBond bond)
    {
        this.bond = bond;
    }

    /**
     * @param destination The destination to set.
     */
    public void setDestination(int destination)
    {
        this.destination = destination;
    }

    /**
     * @param grow The grow to set.
     */
    public void setGrow(boolean grow)
    {
        this.grow = grow;
    }

    /**
     * @param source The source to set.
     */
    public void setSource(int source)
    {
        this.source = source;
    }

    /**
     * @param visit The visit to set.
     */
    public void setVisit(int visit)
    {
        this.visit = visit;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
