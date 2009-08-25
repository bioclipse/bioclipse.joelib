///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: JLog4JPanel.java,v $
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import java.io.Writer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;


/**
 * {@link javax.swing.JPanel} for log4j output and general logging.
 *
 * @.author wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:34 $
 */
public class JLog4JPanel extends JPanel
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static Category root = Category.getRoot();

    //~ Instance fields ////////////////////////////////////////////////////////

    private WriterAppender appender;
    private PatternLayout layout;

    // Maximum count of lines in the Textarea
    private final int MAXLINES = 1000;
    private JScrollPane pane = new JScrollPane();
    private JTextArea textarea = new JTextArea();
    private LogWriter writer;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Contructor for a standard logframe
     */
    public JLog4JPanel()
    {
        writer = new LogWriter(this);
        appender = new WriterAppender(layout, writer);

        Appender a1 = root.getAppender("A1");

        if (a1 == null)
        {
            layout = new PatternLayout("%d{HH:mm} [%-5p] %-25c - %m%n");
        }
        else
        {
            layout = (PatternLayout) a1.getLayout();
        }

        appender.setLayout(layout);
        setLayout(new BorderLayout());
        textarea.setEditable(false);

        //              textarea.setFont(
        //                      new Font(
        //                              "Courier",
        //                              textarea.getFont().getStyle(),
        //                              textarea.getFont().getSize()));
        add(pane, BorderLayout.CENTER);
        pane.getViewport().add(textarea, null);
        root.addAppender(appender);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Appends a string to the textarea and scrolls it down.
     * If the number of lines in the area exceeds MAXLINES then the topmost
     * 5 lines will be truncated.
     */
    public void append(String s)
    {
        // Append string
        textarea.append(s);

        // check Linecount
        int len = textarea.getLineCount();

        if (len > MAXLINES)
        {
            try
            {
                textarea.getDocument().remove(0, 5);
            }
            catch (javax.swing.text.BadLocationException exception)
            {
                exception.printStackTrace();
            }
        }

        // Scroll down the textarea to the bottom
        Dimension size = textarea.getSize();
        JViewport port = pane.getViewport();

        //              Rectangle rect = port.getViewRect();
        Point point = new Point(0, size.height);

        port.setViewPosition(point);
    }

    /**
     * Method declaration
     *
     *
     * @return
     */
    public Appender getAppender()
    {
        return appender;
    }

    /**
     * Method declaration
     *
     *
     * @param Format
     */
    public void setFormat(String Format)
    {
        layout.setConversionPattern(Format);
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    /**
     * LogWriter has the ability to forward the log4j output to a LogFrame class.
     *
     * @.author Thomas Weber
     */
    private class LogWriter extends Writer
    {
        // Ref to the LogFrame instance that should receive the output
        private JLog4JPanel logpanel;

        /**
         * Constructs a new LogWriter and registers the LogFrame.
         */
        public LogWriter(JLog4JPanel logframe)
        {
            this.logpanel = logframe;
        }

        /**
         * Method declaration
         *
         *
         * @throws java.io.IOException
         */
        public void close() throws java.io.IOException
        {
            // TODO: implement this java.io.Writer abstract method
        }

        /**
         * Method declaration
         *
         * @throws java.io.IOException
         */
        public void flush() throws java.io.IOException
        {
            // TODO: implement this java.io.Writer abstract method
        }

        /**
         * Append 'text' to the LogFrame textarea.
         */
        public void write(String text)
        {
            logpanel.append(text);
        }

        /**
         * Method declaration
         *
         *
         * @param parm1
         * @param parm2
         * @param parm3
         *
         * @throws java.io.IOException
         */
        public void write(char[] parm1, int parm2, int parm3)
            throws java.io.IOException
        {
            write(String.valueOf(parm1, parm2, parm3));
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
