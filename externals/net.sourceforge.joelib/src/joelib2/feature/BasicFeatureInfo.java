///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicFeatureInfo.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:29 $
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
package joelib2.feature;

import joelib2.util.types.BasicFactoryInfo;


/**
 * Informations for a descriptor.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.3 $, $Date: 2005/02/17 16:48:29 $
 */
public class BasicFeatureInfo extends BasicFactoryInfo implements FeatureInfo
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;
    public final static String TYPE_UNKNOWN = "unknown";
    public final static String TYPE_NO_COORDINATES = "no_coordinates";
    public final static String TYPE_TOPOLOGICAL = "topological";
    public final static String TYPE_GEOMETRICAL = "geometrical";
    public final static String TYPE_ENERGETIC = "energegetic";
    public final static int REQUIRED_DIMENSION_UNKNOWN = -1;
    public final static int REQUIRED_DIMENSION_NO_COORDINATES = 0;
    public final static int REQUIRED_DIMENSION_TOPOLOGICAL = 2;
    public final static int REQUIRED_DIMENSION_GEOMETRICAL = 3;
    public final static int REQUIRED_DIMENSION_ENERGETIC = 4;

    //~ Instance fields ////////////////////////////////////////////////////////

    /**
     * Required descriptor coordinates dimensions: 0, 2 or 3.
     *
     * @see #REQUIRED_DIMENSION_UNKNOWN
     * @see #REQUIRED_DIMENSION_NO_COORDINATES
     * @see #REQUIRED_DIMENSION_TOPOLOGICAL
     * @see #REQUIRED_DIMENSION_GEOMETRICAL
     * @see #REQUIRED_DIMENSION_ENERGETIC
     */
    protected int dimension;

    /**
     * Base path to description file.
     */
    protected String docFile;

    /**
     * Initialization class for this descriptor.
     */
    protected String init;

    /**
     * Result class for this descriptor.
     */
    protected String result;

    /**
     * Descriptor type, e.g. structural, topological, geometrical, energegetic.
     *
     * @see #TYPE_UNKNOWN
     * @see #TYPE_NO_COORDINATES
     * @see #TYPE_TOPOLOGICAL
     * @see #TYPE_GEOMETRICAL
     * @see #TYPE_ENERGETIC
     */
    protected String type;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Initializes a descriptor information.
     *
     * @param _name            descriptor name
     * @param _type            descriptor type, e.g. no_coordinates, topological, geometrical, energegetic
     * @param _repr  descriptor representation
     * @param _docFile base path to description file
     * @param _init  initialization class for this descriptor
     * @param _result          result class for this descriptor
     *
     * @see #TYPE_UNKNOWN
     * @see #TYPE_NO_COORDINATES
     * @see #TYPE_TOPOLOGICAL
     * @see #TYPE_GEOMETRICAL
     * @see #TYPE_ENERGETIC
     */
    public BasicFeatureInfo(String _name, String _type, String _repr,
        String _docFile, String _init, String _result)
    {
        super(_name, _repr, _docFile);

        name = _name;
        representation = _repr;
        type = _type;
        dimension = getDimensionFromType(_type);
        init = _init;
        docFile = _docFile;
        result = _result;
    }

    /**
     * Initializes a descriptor information.
     *
     * @param _name            descriptor name
     * @param _type            descriptor type, e.g. no_coordinates, topological, geometrical, energegetic
     * @param _repr  descriptor representation
     * @param _docFile base path to description file
     * @param _init  initialization class for this descriptor
     * @param _result          result class for this descriptor
     *
     * @see #TYPE_UNKNOWN
     * @see #TYPE_NO_COORDINATES
     * @see #TYPE_TOPOLOGICAL
     * @see #TYPE_GEOMETRICAL
     * @see #TYPE_ENERGETIC
     */
    public BasicFeatureInfo(String _name, int _dimension, String _repr,
        String _docFile, String _init, String _result)
    {
        super(_name, _repr, _docFile);

        name = _name;
        representation = _repr;
        type = getTypeFromDimension(_dimension);
        dimension = _dimension;
        init = _init;
        docFile = _docFile;
        result = _result;
    }

    /**
     * Initializes a descriptor information.
     *
     * @param _name            descriptor name
     * @param _type            descriptor type, e.g. no_coordinates, topological, geometrical, energegetic
     * @param _dimension   descriptor coordinate dimensions: 0, 2 or 3
     * @param _repr  descriptor representation
     * @param _docFile base path to description file
     * @param _init  initialization class for this descriptor
     * @param _result          result class for this descriptor
     * @deprecated the required dimension is strongly connected to the descriptor type,
     * so it would be better to use the internal resolving used in
     * {@link #DescriptorInfo(String, String, String, String, String, String)}
     *
     * @see #TYPE_UNKNOWN
     * @see #TYPE_NO_COORDINATES
     * @see #TYPE_TOPOLOGICAL
     * @see #TYPE_GEOMETRICAL
     * @see #TYPE_ENERGETIC
     */
    public BasicFeatureInfo(String _name, String _type, String _repr,
        int _dimension, String _docFile, String _init, String _result)
    {
        super(_name, _repr, _docFile);

        name = _name;
        representation = _repr;
        type = _type;
        dimension = _dimension;
        init = _init;
        docFile = _docFile;
        result = _result;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Gets required descriptor dimension.
     *
     * @return the required dimension
     * @see #REQUIRED_DIMENSION_UNKNOWN
     * @see #REQUIRED_DIMENSION_NO_COORDINATES
     * @see #REQUIRED_DIMENSION_TOPOLOGICAL
     * @see #REQUIRED_DIMENSION_GEOMETRICAL
     * @see #REQUIRED_DIMENSION_ENERGETIC
     * @see #TYPE_UNKNOWN
     * @see #TYPE_NO_COORDINATES
     * @see #TYPE_TOPOLOGICAL
     * @see #TYPE_GEOMETRICAL
     * @see #TYPE_ENERGETIC
     */
    public static int getDimensionFromType(String _type)
    {
        int type = REQUIRED_DIMENSION_UNKNOWN;

        if (_type.equals(TYPE_NO_COORDINATES))
        {
            type = REQUIRED_DIMENSION_NO_COORDINATES;
        }
        else if (_type.equals(TYPE_TOPOLOGICAL))
        {
            type = REQUIRED_DIMENSION_TOPOLOGICAL;
        }
        else if (_type.equals(TYPE_GEOMETRICAL))
        {
            type = REQUIRED_DIMENSION_GEOMETRICAL;
        }
        else if (_type.equals(TYPE_ENERGETIC))
        {
            type = REQUIRED_DIMENSION_ENERGETIC;
        }

        return type;
    }

    /**
     * Gets descriptor type, e.g. no_coordinates, topological, geometrical, energegetic.
     *
     * @return the descriptor type
     * @see #TYPE_UNKNOWN
     * @see #TYPE_NO_COORDINATES
     * @see #TYPE_TOPOLOGICAL
     * @see #TYPE_GEOMETRICAL
     * @see #TYPE_ENERGETIC
     * @see #REQUIRED_DIMENSION_UNKNOWN
     * @see #REQUIRED_DIMENSION_NO_COORDINATES
     * @see #REQUIRED_DIMENSION_TOPOLOGICAL
     * @see #REQUIRED_DIMENSION_GEOMETRICAL
     * @see #REQUIRED_DIMENSION_ENERGETIC
     */
    public static String getTypeFromDimension(int _dimension)
    {
        String dimension = TYPE_UNKNOWN;

        switch (_dimension)
        {
        case REQUIRED_DIMENSION_NO_COORDINATES:
            dimension = TYPE_NO_COORDINATES;

        case REQUIRED_DIMENSION_TOPOLOGICAL:
            dimension = TYPE_TOPOLOGICAL;

        case REQUIRED_DIMENSION_GEOMETRICAL:
            dimension = TYPE_GEOMETRICAL;

        case REQUIRED_DIMENSION_ENERGETIC:
            dimension = TYPE_ENERGETIC;

        default:
            dimension = TYPE_UNKNOWN;
        }

        return dimension;
    }

    /**
     * Gets descriptor name.
     *
     * @return the decsriptor name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the descriptor result class representation name.
     *
     * @return the descriptor result class representation name
     */
    public String getResult()
    {
        return result;
    }

    /**
     * Gets descriptor type, e.g. no_coordinates, topological, geometrical, energegetic.
     *
     * @return the descriptor type
     * @see #TYPE_UNKNOWN
     * @see #TYPE_NO_COORDINATES
     * @see #TYPE_TOPOLOGICAL
     * @see #TYPE_GEOMETRICAL
     * @see #TYPE_ENERGETIC
     */
    public String getType()
    {
        return type;
    }

    /**
     * Gets descriptor type dimensions.
     *
     * @return the descriptor type dimensions
     * @see #REQUIRED_DIMENSION_UNKNOWN
     * @see #REQUIRED_DIMENSION_NO_COORDINATES
     * @see #REQUIRED_DIMENSION_TOPOLOGICAL
     * @see #REQUIRED_DIMENSION_GEOMETRICAL
     * @see #REQUIRED_DIMENSION_ENERGETIC
     */
    public int getTypeDimension()
    {
        return dimension;
    }

    /**
     * Gets the descriptor informations.
     *
     * @return the descriptor informations
     */
    public String toString()
    {
        StringBuffer sbuffer = new StringBuffer(100);

        sbuffer.append("<name:");
        sbuffer.append(name);
        sbuffer.append(", type:");
        sbuffer.append(type);
        sbuffer.append(", requiredDimension:");
        sbuffer.append(dimension);
        sbuffer.append(", representation class:");
        sbuffer.append(representation);
        sbuffer.append(", description:");
        sbuffer.append(docFile);
        sbuffer.append(", result class:");
        sbuffer.append(result);
        sbuffer.append(">");

        return sbuffer.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
