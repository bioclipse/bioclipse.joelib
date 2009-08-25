///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: AbstractAtom.java,v $
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

import java.util.Properties;

/**
 *
 * @.author       wegner
 * @.wikipedia Atom
 * @.license      GPL
 * @.cvsversion   $Revision: 1.2 $, $Date: 2005/02/17 16:48:36 $
 */
public abstract class AbstractAtom implements Atom
{
	Properties CustomTypes = new Properties();
	
	
    //~ Methods ////////////////////////////////////////////////////////////////

    public void addCustomType(String type, String SMARTSPattern) 
    {
		if(CustomTypes.containsKey(type)) return;
		CustomTypes.put(type, SMARTSPattern);
	}


	public Properties getCustomTypes()
	{
		return CustomTypes;
	}


	public abstract Object clone();
}

///////////////////////////////////////////////////////////////////////////////
//END OF FILE.
///////////////////////////////////////////////////////////////////////////////
