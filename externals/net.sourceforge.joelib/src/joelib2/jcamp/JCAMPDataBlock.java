///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: JCAMPDataBlock.java,v $
//  Purpose:  Reader/Writer for SDF files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:35 $
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
package joelib2.jcamp;

/**
 * Defines a JCAMP data block and type of the block.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:35 $
 * @.cite dl93
 * @.cite dw88
 * @.cite ghhjs91
 * @.cite lhdl94
 * @.cite dhl90
 */
public class JCAMPDataBlock
{
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final int UNDEFINED_TYPE = -1;
    public static final int LINK_TYPE = 0;
    public static final int CS_TYPE = 1;
    public static final int DX_TYPE = 2;
    public static final String labelStartString = "##"; // marks start of Label Data Record (LDR)
    public static final String labelEndString = "="; // marks end of Label Data Record (LDR)

    //~ Instance fields ////////////////////////////////////////////////////////

    private String blockData;
    private int blockType;
    private int depth;
    private int id;

    //~ Constructors ///////////////////////////////////////////////////////////

    public JCAMPDataBlock()
    {
        blockType = UNDEFINED_TYPE;
        blockData = null;
        depth = 0;
        id = 0;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets the 'Label Data Record'-data of the actual line.
     */
    static public String getDataInLine(String nextLine)
    {
        int endLabelPos = nextLine.indexOf(labelEndString);

        if (endLabelPos != -1)
        {
            return nextLine.substring(endLabelPos + 1).trim();
        }
        else
        {
            return null;
        }
    }

    /**
     * Gets the 'Label Data Record'-label of the actual line.
     */
    static public String getLabelInLine(String nextLine)
    {
        int startLabelPos = nextLine.indexOf(labelStartString);
        int endLabelPos = nextLine.indexOf(labelEndString);

        //no start- or endstring
        if ((startLabelPos == -1) || (endLabelPos == -1))
        {
            return null;
        }

        if (startLabelPos < endLabelPos)
        {
            return nextLine.substring(startLabelPos + 2, endLabelPos).trim();
        }
        else
        {
            return null;
        }
    }

    /**
     * Removes the comments from the actual line.
     */
    static public String removeCommentsInLine(String nextLine)
    {
        int commentPos = -1;
        String uncommented = null;

        // remove comments
        if ((commentPos = nextLine.indexOf("$$")) != -1)
        {
            uncommented = nextLine.substring(0, commentPos);
        }
        else
        {
            // contains no comments, return original line
            return nextLine;
        }

        return uncommented;
    }

    /**
     * Gets the data of this JCAMP block.
     */
    public String getBlockData()
    {
        return blockData;
    }

    /**
     * Gets the ID of this JCAMP block.
     */
    public int getBlockID()
    {
        return id;
    }

    /**
     * Gets the type of this JCAMP block.
     */
    public int getBlockType()
    {
        return blockType;
    }

    /**
     * Gets the depth of this JCAMP block.
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * Sets the data of this JCAMP block.
     */
    public void setBlockData(String data, int depth)
    {
        blockData = data;
        this.depth = depth;
    }

    /**
     * Sets the ID of this JCAMP block.
     */
    public void setBlockID(int id)
    {
        this.id = id;
    }

    /**
     * Sets the type of this JCAMP block.
     */
    public void setBlockType(int type)
    {
        blockType = type;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
