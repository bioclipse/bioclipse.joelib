///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: JEditorPaneAndPagePrinter.java,v $
//  Purpose:  Aromatic typer.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:34 $
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
package joelib2.gui.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import java.io.IOException;

import java.net.URL;

import javax.swing.JEditorPane;


/**
* An {@link JEditorPane} which can print his Text.
*
* @.author     wegnerj
* @.license GPL
* @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:34 $
*/
public class JEditorPaneAndPagePrinter extends JEditorPane implements Printable
{
    //~ Instance fields ////////////////////////////////////////////////////////

    public int m_maxNumPage = 1;
    protected BufferedImage m_bi = null;

    //~ Constructors ///////////////////////////////////////////////////////////

    public JEditorPaneAndPagePrinter()
    {
        super();
    }

    public JEditorPaneAndPagePrinter(URL url) throws IOException
    {
        super(url);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * The method <code>print</code> must be implemented for <code>Printable</code>
     * interface. Parameters are supplied by system.
     */
    public int print(Graphics g, PageFormat pf, int pageIndex)
        throws PrinterException
    {
        Graphics2D g2 = (Graphics2D) g;

        //Graphics2D g2 = (Graphics2D)this.getComponent(1).getGraphics();//g;
        g2.setColor(Color.black); //set default foreground color to black
                                  //for faster printing, turn off double buffering
                                  //RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);

        Dimension d = this.getSize(); //get size of document

        double panelWidth = d.width; //width in pixels
        double panelHeight = d.height; //height in pixels

        double pageHeight = pf.getImageableHeight(); //height of printer page
        double pageWidth = pf.getImageableWidth(); //width of printer page

        double scale = pageWidth / panelWidth;
        int totalNumPages = (int) Math.ceil((scale * panelHeight) / pageHeight);

        //      System.out.println("Pages: "+totalNumPages+" actual:"+pageIndex);
        //make sure not print empty pages
        if (pageIndex >= totalNumPages)
        {
            return Printable.NO_SUCH_PAGE;
        }

        //shift Graphic to line up with beginning of print-imageable region
        g2.translate(pf.getImageableX(), pf.getImageableY());

        //shift Graphic to line up with beginning of next page to print
        g2.translate(0f, -pageIndex * pageHeight);

        //scale the page so the width fits...
        g2.scale(scale, scale);

        //this.getComponent(1).paint(g2);
        this.paint(g2); //repaint the page for printing

        return Printable.PAGE_EXISTS;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
