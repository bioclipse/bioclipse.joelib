///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: Renderer2DChangeListener.java,v $
//Purpose:  Renderer for a 2D layout.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Joerg Kurt Wegner
//Version:  $Revision: 1.6 $
//                      $Date: 2005/02/17 16:48:32 $
//                      $Author: wegner $
//Original Author: steinbeck, gzelter, egonw
//Original Version: Copyright (C) 1997-2003
//                                The Chemistry Development Kit (CDK) project
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation; either version 2.1
// of the License, or (at your option) any later version.
// All we ask is that proper credit is given for our work, which includes
// - but is not limited to - adding the above copyright notice to the beginning
// of your source code files, and to any copyright notice that you may distribute
// with programs based on this work.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////
package joelib2.gui.render2D;

import java.util.EventListener;
import java.util.EventObject;


/**
 * A ChangeListener for the 2D layout.
 *
 * @.author     steinbeck
 * @.author     egonw
 * @.license    LGPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:32 $
 */
public interface Renderer2DChangeListener extends EventListener
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param   e  The EventObject
     */
    void stateChanged(EventObject e);
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
