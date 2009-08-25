///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: CDKConvention.java,v $
//  Purpose:  Chemical Markup Language.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu,
//            egonw@sci.kun.nl, wegner@users.sourceforge.net
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:35 $
//            $Author: wegner $
//
//  Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//  This program is free software; you can redistribute it and/or
//  modify it under the terms of the GNU Lesser General Public License
//  as published by the Free Software Foundation; either version 2.1
//  of the License, or (at your option) any later version.
//  All we ask is that proper credit is given for our work, which includes
//  - but is not limited to - adding the above copyright notice to the beginning
//  of your source code files, and to any copyright notice that you may distribute
//  with programs based on this work.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////
package joelib2.io.types.cml;

import java.util.StringTokenizer;

import org.apache.log4j.Category;

import org.xml.sax.Attributes;


/**
 * This is an implementation for the CDK convention.
 *
 * @.author egonw
 * @.author steinbeck
 * @.author gezelter@maul.chem.nd.edu
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license LGPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:35 $
 */
public class CDKConvention extends CMLCoreModule
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "joelib2.io.types.cml.CDKConvention");

    //~ Instance fields ////////////////////////////////////////////////////////

    private CMLStack conventionStack = new CMLStack();
    private boolean isBond;
    private CMLStack xpath = new CMLStack();

    //~ Constructors ///////////////////////////////////////////////////////////

    public CDKConvention(CDOInterface cdo)
    {
        super(cdo);
    }

    public CDKConvention(ModuleInterface conv)
    {
        super(conv);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void characterData(char[] ch, int start, int length)
    {
        String s = new String(ch, start, length).trim();

        if (isBond)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("CharData (bond): " + s);
            }

            StringTokenizer st = new StringTokenizer(s);

            while (st.hasMoreElements())
            {
                String border = (String) st.nextElement();

                if (logger.isDebugEnabled())
                {
                    logger.debug("new bond order: " + border);
                }

                // assume cdk bond object has already started
                cdo.setObjectProperty("Bond", "order", border);
            }
        }
        else
        {
            super.characterData(xpath, ch, start, length);
        }
    }

    public void endDocument()
    {
        super.endDocument();
    }

    public void endElement(String uri, String local, String raw)
    {
        super.endElement(xpath, uri, local, raw);
    }

    public CDOInterface returnCDO()
    {
        return this.cdo;
    }

    public void startDocument()
    {
        super.startDocument();
        isBond = false;
    }

    public void startElement(String uri, String local, String raw,
        Attributes atts)
    {
        String name = raw;
        setCurrentElement(name);
        isBond = false;

        if (currentElement == STRING)
        {
            for (int i = 0; i < atts.getLength(); i++)
            {
                if (atts.getQName(i).equals("buildin") &&
                        atts.getValue(i).equals("order"))
                {
                    isBond = true;
                }
            }
        }
        else
        {
            super.startElement(xpath, uri, local, raw, atts);
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
