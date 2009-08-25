///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: CMLHandler.java,v $
//Purpose:  Chemical Markup Language.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu,
//                      egonw@sci.kun.nl, wegner@users.sourceforge.net
//Version:  $Revision: 1.6 $
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
//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public License
//as published by the Free Software Foundation; either version 2.1
//of the License, or (at your option) any later version.
//All we ask is that proper credit is given for our work, which includes
//- but is not limited to - adding the above copyright notice to the beginning
//of your source code files, and to any copyright notice that you may distribute
//with programs based on this work.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////
package joelib2.io.types.cml;

import java.util.Hashtable;

import org.apache.log4j.Category;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


/**
 * SAX2 implementation for CML XML fragment reading. CML Core is supported
 * as well is the CRML module.
 *
 * <p>Data is stored into the Chemical Document Object which is passed when
 * instantiating this class.
 *
 * @.author egonw
 * @.author c.steinbeck@uni-koeln.de
 * @.author gezelter@maul.chem.nd.edu
 * @.author     wegnerj
 * @.wikipedia  Chemical Markup Language
 * @.license LGPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:35 $
 **/
public class CMLHandler extends DefaultHandler
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(CMLHandler.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private ModuleInterface conv;

    private CMLStack conventionStack;
    private Hashtable userConventions;
    private CMLStack xpath;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Constructor for the CMLHandler.
     *
     * @param cdo The Chemical Document Object in which data is stored
     **/
    public CMLHandler(CDOInterface cdo)
    {
        conv = new CMLCoreModule(cdo);
        userConventions = new Hashtable();
        xpath = new CMLStack();
        conventionStack = new CMLStack();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Implementation of the characters() procedure overwriting the DefaultHandler interface.
     *
     * @param ch        characters to handle
     */
    public void characters(char[] ch, int start, int length)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(new String(ch, start, length));
        }

        conv.characterData(xpath, ch, start, length);
    }

    public void doctypeDecl(String name, String publicId, String systemId)
        throws Exception
    {
    }

    /**
     * Calling this procedure signals the end of the XML document.
     */
    public void endDocument()
    {
        conv.endDocument();
    }

    public void endElement(String uri, String local, String raw)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("</" + raw + ">");
        }

        conv.endElement(xpath, uri, local, raw);
        xpath.pop();
        conventionStack.pop();
    }

    public void registerConvention(String convention, ModuleInterface conv)
    {
        userConventions.put(convention, conv);
    }

    public CDOInterface returnCDO()
    {
        return conv.returnCDO();
    }

    public void startDocument()
    {
        conv.startDocument();
        conventionStack.push("CML");
    }

    public void startElement(String uri, String local, String raw,
        Attributes atts)
    {
        xpath.push(local);

        StringBuffer sb = new StringBuffer();
        sb.append("<" + raw);

        if (uri.length() > 0)
        {
            sb.append(" xmlns=\"" + uri + "\"");
        }

        sb.append(">");

        if (logger.isDebugEnabled())
        {
            logger.debug(sb.toString());
        }

        if (local.startsWith("reaction"))
        {
            // e.g. reactionList, reaction -> CRML module
            logger.info("Detected CRML module");
            conv = new CMLReactionModule(conv);
            conventionStack.push(conventionStack.current());
        }
        else
        {
            // assume CML Core
            // Detect conventions
            String convName = "";

            for (int i = 0; i < atts.getLength(); i++)
            {
                if (atts.getQName(i).equals("convention"))
                {
                    convName = atts.getValue(i);
                }
            }

            if (convName.length() > 0)
            {
                conventionStack.push(convName);

                if (convName.equals(conventionStack.current()))
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Same convention as parent");
                    }
                }
                else
                {
                    logger.info("New Convention: " + convName);

                    if (convName.equals("CML"))
                    {
                        conv = new CMLCoreModule(conv);
                    }

                    //                                  else if (convName.equals("PDB"))
                    //                                  {
                    //                                          conv = new PDBConvention(conv);
                    //                                  }
                    //                                  else if (convName.equals("PMP"))
                    //                                  {
                    //                                          conv = new PMPConvention(conv);
                    //                                  }
                    //                                  else if (convName.equals("MDLMol"))
                    //                                  {
                    //                                          if (debug)
                    //                                                  logger.debug("MDLMolConvention instantiated...");
                    //                                          conv = new MDLMolConvention(conv);
                    //                                  }
                    //                                  else if (convName.equals("JMOL-ANIMATION"))
                    //                                  {
                    //                                          conv = new JMOLANIMATIONConvention(conv);
                    //                                  }
                    else
                    {
                        //unknown convention. userConvention?
                        if (userConventions.containsKey(convName))
                        {
                            ConventionInterface newconv = (ConventionInterface)
                                userConventions.get(convName);
                            newconv.inherit(conv);
                            conv = newconv;
                        }
                    }
                }
            }
            else
            {
                // no convention set/reset: take convention of parent
                conventionStack.push(conventionStack.current());
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Conventions -> " + conventionStack);
        }

        conv.startElement(xpath, uri, local, raw, atts);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
