///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicSMARTSElement.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:40 $
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
package joelib2.smarts.types;

/**
 * Element.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:40 $
 */
public class BasicSMARTSElement implements java.io.Serializable, SMARTSElement
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     *  Description of the Field
     */
    public int aromatic;

    /**
     *  Description of the Field
     */
    public int organic;

    /**
     *  Description of the Field
     */
    public String symbol;

    /**
     *  Description of the Field
     */
    public double weight;

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * @return Returns the aromatic.
     */
    public int getAromatic()
    {
        return aromatic;
    }

    /**
     * @return Returns the organic.
     */
    public int getOrganic()
    {
        return organic;
    }

    /**
     * @return Returns the symbol.
     */
    public String getSymbol()
    {
        return symbol;
    }

    /**
     * @return Returns the weight.
     */
    public double getWeight()
    {
        return weight;
    }

    /**
     * @param aromatic The aromatic to set.
     */
    public void setAromatic(int aromatic)
    {
        this.aromatic = aromatic;
    }

    /**
     * @param organic The organic to set.
     */
    public void setOrganic(int organic)
    {
        this.organic = organic;
    }

    /**
     * @param symbol The symbol to set.
     */
    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    /**
     * @param weight The weight to set.
     */
    public void setWeight(double weight)
    {
        this.weight = weight;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
