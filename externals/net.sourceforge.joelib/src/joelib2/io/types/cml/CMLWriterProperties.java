///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: CMLWriterProperties.java,v $
//Purpose:  Chemical Markup Language.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.7 $
//                      $Date: 2005/02/17 16:48:35 $
//                      $Author: wegner $
//
//Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
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
package joelib2.io.types.cml;

/**
 * Interface for a CML molecule writer.
 *
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:35 $
 * @.cite rr99b
 * @.cite mr01
 * @.cite gmrw01
 * @.cite wil01
 * @.cite mr03
 * @.cite mrww04
 */
public interface CMLWriterProperties
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean forceWriteFormalCharge();

    public float getCMLversion();

    public String getNamespace();

    public String getXMLDeclaration();

    public boolean storeChemistryKernelInfo();

    public boolean useNamespace();

    public boolean writeImpliciteHydrogens();

    public boolean writePartialCharge();

    public boolean writeSymmetryInformations();
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
