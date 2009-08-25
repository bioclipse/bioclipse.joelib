///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: RSSHandler.java,v $
//Purpose:  Chemical Markup Language.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  steinbeck@ice.mpg.de, egonw@sci.kun.nl,
//          wegner@users.sourceforge.net
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
package joelib2.io.types.chemrss;

import org.xml.sax.helpers.DefaultHandler;


/**
 * SAX2 implementation for a RSS handler.
 *
 * @.author  egonw
 */
public class RSSHandler extends DefaultHandler
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private String cData;

    //    private ChemSequence channelSequence;
    private String cmlString;
    private String itemDate;
    private String itemDesc;
    private String itemLink;
    private String itemTitle;
    private boolean readdedNamespace;

    /*
        public RSSHandler() {
            logger = new LoggingTool(this.getClass().getName());
        }

        public ChemSequence getChemSequence() {
            return channelSequence;
        }

        // XML SAX2 methods

        public void characters(char ch[], int start, int length) {
            if (cData == null) {
                cData = new String();
            }
            cData += new String(ch, start, length);
        }

        public void doctypeDecl(String name, String publicId, String systemId) throws Exception {
        }

        public void startDocument() {
            channelSequence = new ChemSequence();
            cmlString = "";
            readdedNamespace = false;
        }

        public void endDocument() {
        }

        public void endElement(String uri, String local, String raw) {
           logger.debug("</" + raw + ">");
            if (uri.equals("http://www.xml-cml.org/schema/cml2/core")) {
                cmlString += cData;
                cmlString += toEndTag(raw);
            } else if (local.equals("item")) {
                ChemModel model = null;
                if (cmlString.length() > 0) {
                    StringReader reader = new StringReader(cmlString);
                    logger.debug("Parsing CML String: " + cmlString);
                    CMLReader cmlReader = new CMLReader(reader);
                    try {
                        ChemFile file = (ChemFile)cmlReader.read(new ChemFile());
                        if (file.getChemSequenceCount() > 0) {
                            ChemSequence sequence = file.getChemSequence(0);
                            if (sequence.getChemModelCount() > 0) {
                                model = sequence.getChemModel(0);
                            } else {
                                logger.warn("ChemSequence contains no ChemModel");
                            }
                        } else {
                            logger.warn("ChemFile contains no ChemSequene");
                        }
                    } catch (Exception exception) {
                        logger.error("Error while parsing CML");
                        logger.debug(exception);
                    }
                } else {
                    logger.warn("No CML content found");
                }
                if (model == null) {
                    logger.warn("Read empty model");
                    model = new ChemModel();
                }
                model.setProperty(ChemicalRSSReader.RSS_ITEM_TITLE, itemTitle);
                model.setProperty(ChemicalRSSReader.RSS_ITEM_DATE, itemDate);
                model.setProperty(ChemicalRSSReader.RSS_ITEM_LINK, itemLink);
                model.setProperty(ChemicalRSSReader.RSS_ITEM_DESCRIPTION, itemDesc);
                channelSequence.addChemModel(model);
                cmlString = "";
            } else if (local.equals("title")) {
                itemTitle = cData;
            } else if (local.equals("link")) {
                itemLink = cData;
            } else if (local.equals("description")) {
                itemDesc = cData;
            } else if (local.equals("date")) {
                itemDate = cData;
            } else {
                logger.debug("Unparsed element: " + local);
                logger.debug("  uri: " + uri);
            }
            cData = "";
        }

        public void startElement(String uri, String local, String raw, Attributes atts) {
            logger.debug("<" + raw + ">");
            if (uri.equals("http://www.xml-cml.org/schema/cml2/core")) {
                if (readdedNamespace) {
                    cmlString += toStartTag(raw, atts);
                } else {
                    cmlString += toStartTag(raw, atts, uri);
                }
            } else if (local.equals("item")) {
                itemTitle = "";
                itemDesc = "";
                itemDate = "";
                itemLink = "";
            }
            cData = "";
        }

        private String toStartTag(String raw, Attributes atts) {
            return toStartTag(raw, atts, null);
        }

        private String toStartTag(String raw, Attributes atts, String uri) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("<");
            buffer.append(raw);
            for (int i = 0; i < atts.getLength(); i++) {
                buffer.append(" ");
                String qName = atts.getQName(i);
                buffer.append(qName);
                buffer.append("=\"");
                String value = atts.getValue(i);
                buffer.append(value);
                buffer.append("\"");
            }
            if (uri != null) {
                buffer.append(" ");
                buffer.append("xmlns:");
                String namespace = raw.substring(0, raw.indexOf(":"));
                buffer.append(namespace);
                buffer.append("=\"");
                buffer.append(uri);
                buffer.append("\"");
            }
            buffer.append(">");
            return buffer.toString();
        }

        private String toEndTag(String raw) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("</");
            buffer.append(raw);
            buffer.append(">");
            return buffer.toString();
        }
    */
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
