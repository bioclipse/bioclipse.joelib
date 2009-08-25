///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: InfoPanel.java,v $
//  Purpose:  JOELib Test GUI.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
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

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.Feature;
import joelib2.feature.FeatureDescription;
import joelib2.feature.FeatureFactory;
import joelib2.feature.FeatureHelper;

import joelib2.gui.util.JEditorPaneAndPagePrinter;

import joelib2.io.BasicIOTypeHolder;

import wsi.ra.tool.BasicResourceLoader;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import java.io.IOException;

import java.net.URL;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.apache.log4j.Category;


/**
 * DOCUMENT ME!
 *
 * @.author $author$
 * @version $Revision: 1.10 $
 */
public class InfoPanel extends javax.swing.JTabbedPane
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(InfoPanel.class
            .getName());

    //~ Instance fields ////////////////////////////////////////////////////////

    private int actualIndex = -1;
    private URL actualURL = null;

    private JButton backJButton = new JButton();
    private String cachedListEntry;
    private JEditorPaneAndPagePrinter description;
    private JPanel descriptorPanel;
    private JList descriptors;
    private JScrollPane descriptorScrolling;
    private JTextArea expertSystemsPanel;
    private JButton forwardJButton = new JButton();
    private LinkedList historyList = new LinkedList();
    private JTextArea importExport;
    private JPanel navigationPanel;
    private JButton printJButton = new JButton();
    private JTextArea propertiesPanel;

    //~ Constructors ///////////////////////////////////////////////////////////

    public InfoPanel()
    {
        super();
        initComponents();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public HyperlinkListener createHyperLinkListener()
    {
        return new HyperlinkListener()
            {
                public void hyperlinkUpdate(HyperlinkEvent e)
                {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                    {
                        if (e instanceof HTMLFrameHyperlinkEvent)
                        {
                            ((HTMLDocument) description.getDocument())
                            .processHTMLFrameHyperlinkEvent(
                                (HTMLFrameHyperlinkEvent) e);
                        }
                        else
                        {
                            try
                            {
                                description.setPage(e.getURL());
                                actualURL = e.getURL();
                                historyList.add(actualURL);
                                actualIndex = historyList.size() - 1;
                                forwardJButton.setEnabled(false);
                                backJButton.setEnabled(true);
                            }
                            catch (IOException ioe)
                            {
                                logger.error("IOException: " + ioe);
                            }
                        }
                    }
                }
            };
    }

    void backJButton_actionPerformed(ActionEvent e)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(e);
        }

        if (actualIndex < 1)
        {
            return;
        }
        else
        {
            try
            {
                actualIndex--;
                actualURL = (URL) historyList.get(actualIndex);
                description.setPage(actualURL);
            }
            catch (IOException ioe)
            {
                logger.error("IOException: " + ioe);
            }
        }

        if (actualIndex == 0)
        {
            backJButton.setEnabled(false);
        }
        else
        {
            backJButton.setEnabled(true);
        }

        if (actualIndex == (historyList.size() - 1))
        {
            forwardJButton.setEnabled(false);
        }
        else
        {
            forwardJButton.setEnabled(true);
        }
    }

    void forwardJButton_actionPerformed(ActionEvent e)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(e);
        }

        if (actualIndex > (historyList.size() - 2))
        {
            return;
        }
        else
        {
            try
            {
                actualIndex++;
                actualURL = (URL) historyList.get(actualIndex);
                description.setPage(actualURL);
            }
            catch (IOException ioe)
            {
                logger.error("IOException: " + ioe);
            }
        }

        if (actualIndex == (historyList.size() - 1))
        {
            forwardJButton.setEnabled(false);
        }
        else
        {
            forwardJButton.setEnabled(true);
        }

        if (actualIndex == 0)
        {
            backJButton.setEnabled(false);
        }
        else
        {
            backJButton.setEnabled(true);
        }
    }

    void printJButton_actionPerformed(ActionEvent e)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(e);
        }

        try
        {
            PrinterJob prnJob = PrinterJob.getPrinterJob();
            prnJob.setPrintable((Printable) description);

            if (!prnJob.printDialog())
            {
                return;
            }

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            prnJob.print();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        catch (PrinterException ex)
        {
            //ex.printStackTrace();
            logger.error("Printing error: " + ex.toString());
        }
    }

    private void descriptors_actionPerformed(ListSelectionEvent e)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(e);
        }

        if (cachedListEntry.equals((String) descriptors.getSelectedValue()))
        {
            return;
        }

        URL html = getHTMLURL((String) descriptors.getSelectedValue());

        try
        {
            if (html != null)
            {
                description.setPage(html);
                actualURL = html;
                historyList.add(actualURL);
                actualIndex = historyList.size() - 1;
                forwardJButton.setEnabled(false);
                backJButton.setEnabled(true);
            }
        }
        catch (IOException ex)
        {
            logger.error("IOException: " + ex);
        }

        cachedListEntry = (String) descriptors.getSelectedValue();
    }

    private URL getHTMLURL(String descriptorName)
    {
        URL url = null;
        String path = null;

        try
        {
            Feature descriptor = FeatureFactory.getFeature(descriptorName);
            FeatureDescription descInfo = descriptor.getDescription();
            descInfo.getBasePath();
            path = "/" + descInfo.getBasePath() + ".html";

            //System.out.println("path:"+path);
            url = getClass().getResource(path);

            //System.out.println("url:"+url);
        }
        catch (Exception ee)
        {
            logger.error("Failed to open " + path);
            url = null;
        }

        return url;
    }

    private void initComponents()
    {
        descriptorPanel = new JPanel();
        importExport = new JTextArea();
        descriptors = new JList();
        description = new JEditorPaneAndPagePrinter();
        descriptorScrolling = new JScrollPane();
        navigationPanel = new JPanel();
        propertiesPanel = new JTextArea();
        expertSystemsPanel = new JTextArea();

        Dimension dimension = new Dimension(80, 50);

        descriptorPanel.setLayout(new java.awt.BorderLayout());

        Enumeration descsEnum = FeatureHelper.instance().getFeatureNames();
        String[] descs = new String[FeatureHelper.instance().getFeaturesSize()];

        for (int i = 0; descsEnum.hasMoreElements(); i++)
        {
            descs[i] = (String) descsEnum.nextElement();
        }

        Arrays.sort(descs);

        DescriptorListActionListener descListActions =
            new DescriptorListActionListener();
        descriptors.setListData(descs);
        descriptors.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        descriptors.setToolTipText(
            "List of descriptors, which can be calculated by JOELib");
        descriptors.addListSelectionListener(descListActions);
        descriptorScrolling = new JScrollPane(descriptors);
        descriptorPanel.add(descriptorScrolling, BorderLayout.WEST);

        description.setEditable(false);
        description.setToolTipText("Descriptor description");
        description.addHyperlinkListener(createHyperLinkListener());

        JScrollPane scroller = new JScrollPane();
        scroller.setPreferredSize(dimension);
        scroller.setMaximumSize(dimension);
        scroller.setMinimumSize(dimension);

        JViewport vp = scroller.getViewport();
        vp.add(description);
        descriptorPanel.add(scroller, BorderLayout.CENTER);

        InfoActionListener infoActions = new InfoActionListener();
        printJButton.setText("Print");
        printJButton.setToolTipText("Print descriptor description");
        printJButton.addActionListener(infoActions);
        backJButton.setText("Back");
        backJButton.setToolTipText("Browse backward");
        backJButton.addActionListener(infoActions);
        forwardJButton.setText("Forward");
        forwardJButton.setToolTipText("Browse forward");
        forwardJButton.addActionListener(infoActions);
        navigationPanel.add(backJButton);
        navigationPanel.add(forwardJButton);
        backJButton.setEnabled(false);
        forwardJButton.setEnabled(false);
        navigationPanel.add(printJButton);
        actualIndex = 0;

        if (descs.length == 0)
        {
            logger.error("No descriptors defined.");
        }
        else
        {
            cachedListEntry = descs[0];
            actualURL = getHTMLURL(descs[0]);
        }

        if (actualURL != null)
        {
            try
            {
                description.setPage(actualURL);
            }
            catch (IOException e)
            {
                logger.error("IOException: " + e);
            }
        }

        historyList.add(actualURL);
        descriptorPanel.add(navigationPanel, BorderLayout.NORTH);

        addTab("Descriptors", descriptorPanel);

        importExport.setFont(new Font("Courier",
                importExport.getFont().getStyle(),
                importExport.getFont().getSize()));
        importExport.setText(BasicIOTypeHolder.instance().toString());
        importExport.setEditable(false);
        importExport.setToolTipText("Supported import and export types");
        scroller = new JScrollPane(importExport);
        scroller.setPreferredSize(dimension);
        scroller.setMaximumSize(dimension);
        scroller.setMinimumSize(dimension);
        addTab("Import/Export", scroller);

        byte[] bytes = BasicResourceLoader.instance()
                                          .getBytesFromResourceLocation(
                "joelib2.properties");

        if (bytes != null)
        {
            String properties = new String(bytes);
            propertiesPanel.setText(properties);
            propertiesPanel.setEditable(false);
            propertiesPanel.setToolTipText(
                "Properties for JOELib defined in the joelib2.properties file");

            //propertiesPanel.setFont(new Font("Courier",propertiesPanel.getFont().getStyle(),propertiesPanel.getFont().getSize()));
            scroller = new JScrollPane(propertiesPanel);
            scroller.setPreferredSize(dimension);
            scroller.setMaximumSize(dimension);
            scroller.setMinimumSize(dimension);
            addTab("Properties", scroller);
        }

        StringBuffer sb = new StringBuffer();
        String[] titles = IdentifierExpertSystem.instance().getKernelTitles();
        String[] infos = IdentifierExpertSystem.instance()
                                               .getKernelInformations();
        String kernelHash = Integer.toString(IdentifierExpertSystem.instance()
                .getKernelHash());

        sb.append(
            "All descriptors will be calculated with the actual expert systems !\n");
        sb.append(
            "The hashed expert system (chemistry kernel) identifier is: " +
            kernelHash + "\n");
        sb.append(
            "\nHere is a detailed list of all applied expert systems :\n");

        String delimiter =
            "---------------------------------------------------";
        sb.append(delimiter);

        for (int i = 0; i < titles.length; i++)
        {
            sb.append("\n" + titles[i]);
            sb.append(":\n");
            sb.append(infos[i].replace(' ', '\n'));
            sb.append("\n");
            sb.append(delimiter);
        }

        String[] asList = IdentifierExpertSystem.getDependencyTreeClassNames();
        sb.append("\n");

        for (int i = 0; i < asList.length; i++)
        {
            sb.append("dependency class is: \t\t\t\t" + asList[i] + "\n");
            sb.append("dependency version hash code is: \t\t" +
                IdentifierExpertSystem.getDependencyTreeHash(asList[i]) +
                " (including chemistry kernel hash: " + kernelHash + ")\n");
            sb.append("dependency algorithm complexity is at least: \t" +
                IdentifierExpertSystem.getDependencyTreeComplexity(asList[i]) +
                " (+ basic user input graph, + cyclic DEPENDENCIES, + data structure DEPENDENCIES, + forgotten DEPENDENCIES)\n");
            sb.append("\n" +
                IdentifierExpertSystem.getDependencyTree(asList[i]) + "\n");

            if (i < (asList.length - 1))
            {
                sb.append(delimiter);
                sb.append("\n");
            }
        }

        expertSystemsPanel.setText(sb.toString().replace('\t', ' '));
        expertSystemsPanel.setEditable(false);
        expertSystemsPanel.setToolTipText(
            "Properties for JOELib defined in the joelib2.properties file");

        //propertiesPanel.setFont(new Font("Courier",propertiesPanel.getFont().getStyle(),propertiesPanel.getFont().getSize()));
        scroller = new JScrollPane(expertSystemsPanel);
        scroller.setPreferredSize(dimension);
        scroller.setMaximumSize(dimension);
        scroller.setMinimumSize(dimension);
        addTab("Expert systems", scroller);
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    class DescriptorListActionListener implements ListSelectionListener
    {
        public void valueChanged(ListSelectionEvent event)
        {
            Object object = event.getSource();

            if (object == descriptors)
            {
                descriptors_actionPerformed(event);
            }
        }
    }

    class InfoActionListener implements java.awt.event.ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            Object object = event.getSource();

            if (object == printJButton)
            {
                printJButton_actionPerformed(event);
            }
            else if (object == backJButton)
            {
                backJButton_actionPerformed(event);
            }
            else if (object == forwardJButton)
            {
                forwardJButton_actionPerformed(event);
            }
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
