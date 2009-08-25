///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ConvertPanel.java,v $
//  Purpose:  JOELib Test GUI.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.9 $
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

import joelib2.feature.FeatureHelper;

import joelib2.smarts.BasicSMARTSPatternMatcher;
import joelib2.smarts.SMARTSPatternMatcher;

import joelib2.util.HelperMethods;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Category;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.9 $
 */
public class ConvertPanel extends javax.swing.JPanel
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(ConvertPanel.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private javax.swing.JCheckBox addDescriptors;
    private javax.swing.JCheckBox addHydrogens;

    private javax.swing.JButton addNativeButton;
    private javax.swing.JCheckBox addPolarHydrogens;
    private javax.swing.JButton addSMARTSButton;
    private JPanel booleanOptions;
    private JTextArea description;
    private JPanel filters;
    private JPanel filtersNative;
    private JPanel filtersSMARTS;
    private JComboBox nativeDescriptor;
    private JComboBox nativeRelation;
    private JTextField nativeValue;
    private JPanel options;
    private javax.swing.JCheckBox removeDescriptors;
    private javax.swing.JCheckBox removeEmpty;
    private javax.swing.JCheckBox removeHydrogens;
    private JTextArea rules;
    private JComboBox skipOrConvertNative;
    private JComboBox skipOrConvertSMARTS;
    private JTextField smartsRule;
    private javax.swing.JCheckBox splitOutputFile;
    private javax.swing.JCheckBox stripSalts;
    private javax.swing.JCheckBox usePHvalueCorrection;

    //~ Constructors ///////////////////////////////////////////////////////////

    public ConvertPanel()
    {
        super();
        initComponents();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void startConvert(String inputFile, String outputFile)
    {
        if ((inputFile == null) || (inputFile.trim().length() == 0))
        {
            logger.error("No input file defined.");

            return;
        }

        Vector argsV = new Vector();

        if (removeHydrogens.isSelected())
        {
            argsV.add("-h");
        }

        if (addHydrogens.isSelected())
        {
            argsV.add("+h");

            if (addPolarHydrogens.isEnabled())
            {
                argsV.add("+p");
            }

            if (usePHvalueCorrection.isEnabled())
            {
                argsV.add("+pH");
            }
            else
            {
                argsV.add("-pH");
            }

            argsV.add("+h");
        }

        if (removeDescriptors.isSelected())
        {
            argsV.add("-d");
        }

        if (addDescriptors.isSelected())
        {
            argsV.add("+d");
        }

        if (stripSalts.isSelected())
        {
            argsV.add("-salt");
        }

        if (splitOutputFile.isSelected())
        {
            argsV.add("+split");
        }

        if (removeEmpty.isSelected())
        {
            argsV.add("-e");
        }

        Vector tmpRules = new Vector();
        HelperMethods.tokenize(tmpRules, rules.getText(), " \t\r\n");

        for (int i = 0; i < tmpRules.size(); i++)
        {
            argsV.add(tmpRules.get(i));
        }

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

        ConvertExample convert = new ConvertExample();

        int status = convert.parseCommandLine(args);

        if (status == ConvertExample.CONTINUE)
        {
            convert.convert();
        }
        else if (status == ConvertExample.STOP_USAGE)
        {
            convert.usage();

            //System.exit(1);
        }

        //else if (status == Convert.STOP)
        //{
        //    //System.exit(0);
        //}
    }

    void addHydrogens_actionPerformed(ActionEvent e)
    {
        if (addHydrogens.isSelected())
        {
            addPolarHydrogens.setEnabled(true);
            usePHvalueCorrection.setEnabled(true);
        }
        else
        {
            addPolarHydrogens.setEnabled(false);
            usePHvalueCorrection.setEnabled(false);
        }
    }

    void addNativeButton_actionPerformed(ActionEvent e)
    {
        if (nativeValue.getText().trim().length() == 0)
        {
            logger.error("No value defined in 'native-descriptor-rule'.");

            return;
        }

        try
        {
            Double.parseDouble(nativeValue.getText());
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage());

            return;
        }

        if (rules.getText().trim().length() > 0)
        {
            rules.append("\n");
        }

        if (((String) skipOrConvertNative.getSelectedItem()).equals("Convert"))
        {
            rules.append("+r" + nativeDescriptor.getSelectedItem() +
                nativeRelation.getSelectedItem() + nativeValue.getText());
        }
        else
        {
            rules.append("-r" + nativeDescriptor.getSelectedItem() +
                nativeRelation.getSelectedItem() + nativeValue.getText());
        }
    }

    void addSMARTSButton_actionPerformed(ActionEvent e)
    {
        SMARTSPatternMatcher smarts = new BasicSMARTSPatternMatcher();

        if (!smarts.init(smartsRule.getText()))
        {
            logger.error("Invalid SMARTS pattern: " + smartsRule.getText());
        }
        else
        {
            if (rules.getText().trim().length() > 0)
            {
                rules.append("\n");
            }

            if (((String) skipOrConvertSMARTS.getSelectedItem()).equals(
                        "Convert"))
            {
                rules.append("+m" + smartsRule.getText() + ">0");
            }
            else
            {
                rules.append("-m" + smartsRule.getText() + ">0");
            }
        }
    }

    private void initComponents()
    {
        options = new JPanel();
        booleanOptions = new JPanel();
        filters = new JPanel();
        filtersSMARTS = new JPanel();
        description = new JTextArea();
        removeHydrogens = new javax.swing.JCheckBox();
        addHydrogens = new javax.swing.JCheckBox();
        addPolarHydrogens = new javax.swing.JCheckBox();
        usePHvalueCorrection = new javax.swing.JCheckBox();
        removeDescriptors = new javax.swing.JCheckBox();
        addDescriptors = new javax.swing.JCheckBox();
        removeEmpty = new javax.swing.JCheckBox();
        stripSalts = new javax.swing.JCheckBox();
        splitOutputFile = new javax.swing.JCheckBox();
        skipOrConvertSMARTS = new JComboBox();
        smartsRule = new JTextField();
        addSMARTSButton = new JButton();
        rules = new JTextArea();
        filtersNative = new JPanel();
        skipOrConvertNative = new JComboBox();
        nativeRelation = new JComboBox();
        nativeDescriptor = new JComboBox();
        nativeValue = new JTextField();
        addNativeButton = new JButton();

        setLayout(new java.awt.BorderLayout());
        booleanOptions.setLayout(new java.awt.GridLayout(7, 0));

        description.setText("Convert molecule files and some properties\n\n" +
            "Command line version can be used with:\n" +
            "Windows: convert.bat\n" +
            "Linux (or Windows with Cygwin): sh convert.sh");
        description.setEditable(false);
        description.setToolTipText("Convert application description");
        add(description, BorderLayout.NORTH);

        options.setLayout(new java.awt.BorderLayout());
        add(options, BorderLayout.CENTER);

        options.add(booleanOptions, BorderLayout.CENTER);

        options.add(filters, BorderLayout.SOUTH);
        filters.setLayout(new java.awt.GridLayout(4, 0));
        filters.add(filtersSMARTS);
        filters.add(filtersNative);
        filters.add(new JLabel("Filter rules:"));
        filters.add(new JScrollPane(rules));

        removeHydrogens.setText("Remove Hydrogens");

        ConvertActionListener convertActions = new ConvertActionListener();
        addHydrogens.addActionListener(convertActions);
        addHydrogens.setText("Add Hydrogens");
        addHydrogens.setSelected(false);
        addPolarHydrogens.setText("   polar only");
        addPolarHydrogens.setSelected(false);
        addPolarHydrogens.setEnabled(false);
        usePHvalueCorrection.setText("   use pH value correction");
        usePHvalueCorrection.setSelected(false);
        usePHvalueCorrection.setEnabled(false);

        removeDescriptors.setText("Remove Descriptors");
        addDescriptors.setText("Add Descriptors (only native)");
        removeEmpty.setText("Remove empty molecules");
        stripSalts.setText("Strip salts (remove multiple fragments)");
        splitOutputFile.setText("Split output file");

        skipOrConvertSMARTS.addItem("Convert");
        skipOrConvertSMARTS.addItem("Skip");
        filtersSMARTS.add(skipOrConvertSMARTS);
        filtersSMARTS.add(new JLabel("when containing SMARTS"));

        Dimension smartsRuleMinDim = new Dimension(218, 24);
        smartsRule.setPreferredSize(smartsRuleMinDim);
        smartsRule.setMaximumSize(smartsRuleMinDim);
        smartsRule.setMinimumSize(smartsRuleMinDim);

        filtersSMARTS.add(smartsRule);
        addSMARTSButton.setText("Add rule");
        addSMARTSButton.addActionListener(convertActions);
        filtersSMARTS.add(addSMARTSButton);

        skipOrConvertNative.addItem("Convert");
        skipOrConvertNative.addItem("Skip");
        filtersNative.add(skipOrConvertNative);
        filtersNative.add(new JLabel("when"));

        List nativeDescsL = FeatureHelper.instance().getNativeFeatures();
        int s = nativeDescsL.size();
        String[] nativeDescs = new String[s];

        for (int i = 0; i < s; i++)
        {
            nativeDescs[i] = (String) nativeDescsL.get(i);
        }

        Arrays.sort(nativeDescs);

        for (int i = 0; i < s; i++)
        {
            nativeDescriptor.addItem(nativeDescs[i]);
        }

        filtersNative.add(nativeDescriptor);
        nativeRelation.addItem("<");
        nativeRelation.addItem("<=");
        nativeRelation.addItem("==");
        nativeRelation.addItem(">");
        nativeRelation.addItem(">=");
        nativeRelation.addItem("!=");
        filtersNative.add(nativeRelation);

        Dimension nativeValueMinDim = new Dimension(60, 24);
        nativeValue.setPreferredSize(nativeValueMinDim);
        nativeValue.setMaximumSize(nativeValueMinDim);
        nativeValue.setMinimumSize(nativeValueMinDim);
        filtersNative.add(nativeValue);
        addNativeButton.setText("Add rule");
        addNativeButton.addActionListener(convertActions);
        filtersNative.add(addNativeButton);

        rules.setEditable(true);

        booleanOptions.add(removeHydrogens);
        booleanOptions.add(addHydrogens);
        booleanOptions.add(removeDescriptors);
        booleanOptions.add(addPolarHydrogens);
        booleanOptions.add(removeEmpty);
        booleanOptions.add(usePHvalueCorrection);
        booleanOptions.add(addDescriptors);

        JCheckBox empty = new javax.swing.JCheckBox();
        empty.setVisible(false);
        booleanOptions.add(empty);
        booleanOptions.add(stripSalts);

        //booleanOptions.add(empty);
        booleanOptions.add(splitOutputFile);
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    /*==========================================================================*
    * CLASS DECLARATION OF ADAPTER-CLASSES
    *==========================================================================*/
    class ConvertActionListener implements java.awt.event.ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            Object object = event.getSource();

            if (object == addHydrogens)
            {
                addHydrogens_actionPerformed(event);
            }
            else if (object == addSMARTSButton)
            {
                addSMARTSButton_actionPerformed(event);
            }
            else if (object == addNativeButton)
            {
                addNativeButton_actionPerformed(event);
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
