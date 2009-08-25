///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: StatisticPanel.java,v $
//  Purpose:  JOELib Test GUI.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:32 $
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
package joelib2.gui.example;

import joelib2.example.ConvertExample;
import joelib2.example.DescriptorStatisticExample;

import java.awt.BorderLayout;

import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.Category;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.6 $
 */
public class StatisticPanel extends javax.swing.JPanel
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(StatisticPanel.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private JTextArea description;

    private JPanel options;

    //~ Constructors ///////////////////////////////////////////////////////////

    public StatisticPanel()
    {
        super();
        initComponents();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void startStatistic(String inputFile, String outputFile)
    {
        if ((inputFile == null) || (inputFile.trim().length() == 0))
        {
            logger.error("No input file defined.");

            return;
        }

        Vector argsV = new Vector();

        if ((inputFile != null) && (inputFile.trim().length() != 0))
        {
            argsV.add(inputFile);
        }

        if ((outputFile != null) && (outputFile.trim().length() != 0))
        {
            argsV.add(outputFile);
        }

        String[] args = new String[argsV.size()];

        for (int i = 0; i < argsV.size(); i++)
        {
            args[i] = (String) argsV.get(i);
        }

        DescriptorStatisticExample convert = new DescriptorStatisticExample();

        int status = convert.parseCommandLine(args);

        if (status == ConvertExample.CONTINUE)
        {
            convert.test();
        }
        else if (status == ConvertExample.STOP_USAGE)
        {
            convert.usage();

            //System.exit(1);
        }

        //        else if (status == Convert.STOP)
        //        {
        //            //System.exit(0);
        //        }
    }

    private void initComponents()
    {
        options = new JPanel();
        description = new JTextArea();

        setLayout(new java.awt.BorderLayout());
        options.setLayout(new java.awt.GridLayout(5, 0));

        description.setText(
            "Calculates the statistic for a molecule descriptor file\n\n" +
            "Command line version can be used with:\n" +
            "Windows: statistic.bat\n" +
            "Linux (or Windows with Cygwin): sh statistic.sh");
        description.setEditable(false);
        description.setToolTipText("Statistic application description");
        add(description, BorderLayout.NORTH);

        add(options, BorderLayout.CENTER);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
