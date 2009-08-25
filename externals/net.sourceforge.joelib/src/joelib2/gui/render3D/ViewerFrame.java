///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: ViewerFrame.java,v $
//  Purpose:  Counts the number of descriptors and molecules in a molecule file.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.10 $
//            $Date: 2005/02/17 16:48:33 $
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
package joelib2.gui.render3D;

import joelib2.feature.FeatureException;
import joelib2.feature.FeatureHelper;
import joelib2.feature.FeatureResult;

import joelib2.gui.render3D.graphics3D.JPanel3D;
import joelib2.gui.render3D.graphics3D.RenderStyle;
import joelib2.gui.render3D.util.CentralDisplayAdapter;
import joelib2.gui.render3D.util.CentralLookup;
import joelib2.gui.render3D.util.MolViewerEventAdapter;

import joelib2.gui.util.MolFileChooser;
import joelib2.gui.util.MolFileFilter;

import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;
import joelib2.io.MoleculeIOException;

import joelib2.molecule.Molecule;

import joelib2.molecule.types.BasicPairData;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import java.io.IOException;

import java.net.URL;

import java.util.Enumeration;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.apache.log4j.Category;


/**
 * Description of the Class
 *
 * @.author     wegnerj
 * @.license    GPL
 * @.cvsversion    $Revision: 1.10 $, $Date: 2005/02/17 16:48:33 $
 */
public class ViewerFrame extends JFrame
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final long serialVersionUID = 1L;

    /**
     * Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(ViewerFrame.class
            .getName());
    private static final String iconLocation =
        "joelib2/data/images/joe_bws.gif";

    //~ Instance fields ////////////////////////////////////////////////////////

    //    JLabel statusBar = new JLabel();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel contentPane;
    JCheckBoxMenuItem jMenuFast;
    Molecule jmol = null;
    MoleculeFileIO loader = null;
    CentralLookup lookup;
    private ButtonGroup atomColoringGroup = new ButtonGroup();
    private JPanel3D molPanel;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *Construct the frame
     *
     * @param type       Description of the Parameter
     * @param inputFile  Description of the Parameter
     */
    public ViewerFrame(String type, String inputFile)
    {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.getJOEMol(type, inputFile);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Adds a feature to the Molecule attribute of the ViewerFrame object
     *
     * @param mol  The feature to be added to the Molecule attribute
     */
    public void addMolecule(Molecule mol)
    {
        molPanel.addMolecule(mol);

        this.setTitle(mol.getTitle());
    }

    /**
     * Description of the Method
     */
    public void clear()
    {
        molPanel.clear();
    }

    public void jMenuAddDescriptors_actionPerformed(ActionEvent e)
    {
        Enumeration enumeration = FeatureHelper.instance().getFeatureNames();
        String tmp;

        while (enumeration.hasMoreElements())
        {
            tmp = (String) enumeration.nextElement();

            FeatureResult result = null;

            try
            {
                result = FeatureHelper.instance().featureFrom(jmol, tmp);
            }
            catch (FeatureException ex)
            {
                logger.error(ex.toString());
            }

            if (result == null)
            {
                logger.error("Descriptor '" + tmp +
                    "' was not calculated and will not be stored.");
            }
            else
            {
                BasicPairData dp = new BasicPairData();
                dp.setKey(tmp);
                dp.setKeyValue(result);
                jmol.addData(dp);
            }
        }

        logger.info("Descriptors calculated and added to molecule " +
            jmol.getTitle());
    }

    /**
     * Description of the Method
     *
     * @param e  Description of the Parameter
     */
    public void jMenuAddHydrogens_actionPerformed(ActionEvent e)
    {
        if (!jmol.addHydrogens(false, true, true))
        {
            logger.warn("Hydrogens could not be added successfully.");
        }

        this.clear();
        this.addMolecule(jmol);
    }

    public void jMenuAddPolarHydrogens_actionPerformed(ActionEvent e)
    {
        if (!jmol.addPolarHydrogens())
        {
            logger.warn("Polar Hydrogens could not be added successfully.");
        }

        this.clear();
        this.addMolecule(jmol);
    }

    /**
     * Description of the Method
     *
     * @param e  Description of the Parameter
     */
    public void jMenuAtomColoring_actionPerformed(ActionEvent e)
    {
        //              System.out.println(atomColoringGroup.getSelection().getActionCommand());
        useAtomPropertyColoring(atomColoringGroup.getSelection()
            .getActionCommand());
        this.clear();
        this.addMolecule(jmol);
    }

    /**
     * Description of the Method
     *
     * @param e  Description of the Parameter
     */
    public void jMenuBallStick_actionPerformed(ActionEvent e)
    {
        molPanel.setRenderStyle(RenderStyle.BALL_AND_STICK);
    }

    /**
     *File | Exit action performed
     *
     * @param e  Description of the Parameter
     */
    public void jMenuFileExit_actionPerformed(ActionEvent e)
    {
        //        this.setVisible(false);
        System.exit(0);
    }

    /**
     *File | Open action performed
     *
     * @param e  Description of the Parameter
     */
    public void jMenuFileOpen_actionPerformed(ActionEvent e)
    {
        MolFileChooser fileChooser = MolFileChooser.instance();
        JFileChooser load = fileChooser.getLoadFileChooser();

        int returnVal = load.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            logger.info("Loading: " + load.getSelectedFile().getName());
            getJOEMol(null, load.getSelectedFile().getAbsolutePath());
        }

        //        this.setVisible(false);
    }

    /**
     *File | Save action performed
     *
     * @param e  Description of the Parameter
     */
    public void jMenuFileSave_actionPerformed(ActionEvent e)
    {
        MolFileChooser fileChooser = MolFileChooser.instance();
        JFileChooser save = fileChooser.getSaveFileChooser();

        int returnVal = save.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                if (save.getFileFilter() instanceof MolFileFilter)
                {
                    MolFileFilter filter = (MolFileFilter) save.getFileFilter();
                    logger.info("Saving (" + filter.getIOType().getName() +
                        "): " + save.getSelectedFile().getName());

                    if (!MoleculeFileHelper.saveMolFromFile(jmol,
                                save.getSelectedFile().getAbsolutePath(),
                                filter.getIOType().getName()))
                    {
                        logger.error("Molecule could not be saved in " +
                            save.getSelectedFile().getName());
                    }
                }
                else
                {
                    logger.info("Saving: " + save.getSelectedFile().getName());

                    if (!MoleculeFileHelper.saveMolFromFile(jmol,
                                save.getSelectedFile().getAbsolutePath(), null))
                    {
                        logger.error("Molecule could not be saved in " +
                            save.getSelectedFile().getName());
                    }
                }
            }
            catch (IOException e1)
            {
                System.err.println(e1.getMessage());
            }
            catch (MoleculeIOException e1)
            {
                System.err.println(e1.getMessage());
            }
        }

        //        this.setVisible(false);
    }

    /**
     * Description of the Method
     *
     * @param e  Description of the Parameter
     */

    //    public void jMenuFast_actionPerformed(ActionEvent e)
    //    {
    //        if (jMenuFast.getState())
    //        {
    //            molPanel.setFast();
    //        }
    //        else
    //        {
    //            molPanel.setNice();
    //        }
    //    }

    /**
     * Description of the Method
     *
     *Help | About action performed
     *
     * @param e  Description of the Parameter
     */
    public void jMenuHelpAbout_actionPerformed(ActionEvent e)
    {
        ViewerUsage dlg = new ViewerUsage(this);
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation(((frmSize.width - dlgSize.width) / 2) + loc.x,
            ((frmSize.height - dlgSize.height) / 2) + loc.y);
        dlg.setModal(true);
        dlg.show();
    }

    public void jMenuRemoveDescriptors_actionPerformed(ActionEvent e)
    {
    }

    /**
     * Description of the Method
     *
     * @param e  Description of the Parameter
     */
    public void jMenuRemoveHydrogens_actionPerformed(ActionEvent e)
    {
        // use begein and end modify to update
        // rotamer informations
        jmol.beginModify();

        if (!jmol.deleteHydrogens())
        {
            logger.warn("Hydrogens could not be removed successfully.");
        }

        jmol.endModify();

        this.clear();
        this.addMolecule(jmol);
    }

    /**
     * Description of the Method
     *
     * @param e  Description of the Parameter
     */
    public void jMenuRemoveNonPolarHydrogens_actionPerformed(ActionEvent e)
    {
        // use begein and end modify to update
        // rotamer informations
        jmol.beginModify();

        if (!jmol.deleteNonPolarHydrogens())
        {
            logger.warn(
                "Non-Polar Hydrogens could not be removed successfully.");
        }

        jmol.endModify();
        this.clear();
        this.addMolecule(jmol);
    }

    public void jMenuSpacefill_actionPerformed(ActionEvent e)
    {
        molPanel.setRenderStyle(RenderStyle.CPK);
    }

    /**
     * Description of the Method
     *
     * @param e  Description of the Parameter
     */
    public void jMenuStick_actionPerformed(ActionEvent e)
    {
        molPanel.setRenderStyle(RenderStyle.STICK);
    }

    /**
     * Description of the Method
     *
     * @param e  Description of the Parameter
     */
    public void jMenuWire_actionPerformed(ActionEvent e)
    {
        molPanel.setRenderStyle(RenderStyle.WIRE);
    }

    /**
     * Description of the Method
     *
     * @param e  Description of the Parameter
     */
    public void nextButton_actionPerformed(ActionEvent e)
    {
        boolean success = true;

        //        for (; ; )
        //        {
        jmol.clear();

        try
        {
            success = loader.read(jmol);

            if (!success)
            {
                logger.info("No molecule loaded");

                return;

                //                    break;
            }

            if (jmol.isEmpty())
            {
                logger.error("No molecule loaded.");
                System.exit(1);
            }

            this.clear();
            this.addMolecule(jmol);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
        catch (MoleculeIOException ex)
        {
            ex.printStackTrace();
            logger.info("Molecule '" + jmol.getTitle() + "' was skipped.");

            //          continue;
        }

        //        }
        //useAtomPropertyColoring("Gasteiger_Marsili");
        //useAtomPropertyColoring("Atom_valence");
    }

    public void removeMolecule(Molecule mol)
    {
        molPanel.removeMolecule(mol);
    }

    public void useAtomPropertyColoring(String atomPropertyName)
    {
        molPanel.useAtomPropertyColoring(atomPropertyName);
    }

    /**
     * Description of the Method
     *
     * @return   Description of the Return Value
     */
    protected JMenuBar createMenubar()
    {
        JMenuBar jMenuBar1 = new JMenuBar();
        JMenu jMenuFile = new JMenu();
        JMenuItem jMenuFileOpen = new JMenuItem();
        JMenuItem jMenuFileSave = new JMenuItem();
        JMenuItem jMenuFileExit = new JMenuItem();

        jMenuFile.setText("File");
        jMenuFileOpen.setText("Open");
        jMenuFileOpen.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuFileOpen_actionPerformed(e);
                }
            });
        jMenuFileSave.setText("Save");
        jMenuFileSave.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuFileSave_actionPerformed(e);
                }
            });
        jMenuFileExit.setText("Exit");
        jMenuFileExit.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuFileExit_actionPerformed(e);
                }
            });

        JMenu jMenuView = new JMenu();
        JCheckBoxMenuItem jMenuBallStick = new JCheckBoxMenuItem();
        JCheckBoxMenuItem jMenuStick = new JCheckBoxMenuItem();
        JCheckBoxMenuItem jMenuWire = new JCheckBoxMenuItem();
        JCheckBoxMenuItem jMenuSpacefill = new JCheckBoxMenuItem();
        jMenuView.setText("View");
        jMenuBallStick.setText("Ball & Stick");
        jMenuBallStick.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuBallStick_actionPerformed(e);
                }
            });
        jMenuStick.setText("Stick");
        jMenuStick.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuStick_actionPerformed(e);
                }
            });
        jMenuWire.setText("Wire");
        jMenuWire.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuWire_actionPerformed(e);
                }
            });
        jMenuSpacefill.setText("Spacefill");
        jMenuSpacefill.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuSpacefill_actionPerformed(e);
                }
            });

        ButtonGroup group = new ButtonGroup();
        jMenuView.add(jMenuBallStick);
        group.add(jMenuBallStick);
        jMenuView.add(jMenuStick);
        group.add(jMenuStick);
        jMenuView.add(jMenuWire);
        group.add(jMenuWire);
        jMenuView.add(jMenuSpacefill);
        group.add(jMenuSpacefill);
        jMenuView.addSeparator();

        // initialize atom coloring menu items
        ActionListener atomColoringListener = new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuAtomColoring_actionPerformed(e);
                }
            };

        JCheckBoxMenuItem jMenuAPItem = null;
        jMenuAPItem = new JCheckBoxMenuItem();
        jMenuAPItem.setText("NONE");
        jMenuAPItem.setActionCommand(null);
        jMenuAPItem.addActionListener(atomColoringListener);
        atomColoringGroup.add(jMenuAPItem);
        jMenuAPItem.setSelected(true);
        jMenuView.add(jMenuAPItem);

        List atomPropDescs = FeatureHelper.instance().getAtomLabelFeatures();
        int s = atomPropDescs.size();

        for (int ii = 0; ii < s; ii++)
        {
            jMenuAPItem = new JCheckBoxMenuItem();
            jMenuAPItem.setText((String) atomPropDescs.get(ii));
            jMenuAPItem.setActionCommand((String) atomPropDescs.get(ii));
            jMenuAPItem.addActionListener(atomColoringListener);
            atomColoringGroup.add(jMenuAPItem);
            jMenuView.add(jMenuAPItem);
        }

        //Electronegativity_pauling
        //Graph_potentials
        //Atom_mass
        //Atom_van_der_waals_volume
        //Atom_valence
        //Electron_affinity
        //Gasteiger_Marsili
        //        jMenuWire.setState(true);
        //        molPanel.setRenderStyle(RenderStyle.WIRE);
        molPanel.setRenderStyle(RenderStyle.BALL_AND_STICK);
        jMenuBallStick.setState(true);

        //        jMenuView.addSeparator();
        //        jMenuFast = new JCheckBoxMenuItem();
        //        jMenuFast.setText("Fast");
        //        jMenuFast.addActionListener(
        //            new ActionListener()
        //            {
        //                public void actionPerformed(ActionEvent e)
        //                {
        //                    jMenuFast_actionPerformed(e);
        //                }
        //            });
        //        jMenuView.add(jMenuFast);
        JMenu jMenuTools = new JMenu();
        jMenuTools.setText("Tools");

        JMenuItem jMenuAddHydrogens = new JMenuItem();
        jMenuAddHydrogens.setText("Add Hydrogens");
        jMenuAddHydrogens.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuAddHydrogens_actionPerformed(e);
                }
            });

        JMenuItem jMenuRemoveHydrogens = new JMenuItem();
        jMenuRemoveHydrogens.setText("Remove Hydrogens");
        jMenuRemoveHydrogens.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuRemoveHydrogens_actionPerformed(e);
                }
            });

        JMenuItem jMenuAddPolarHydrogens = new JMenuItem();
        jMenuAddPolarHydrogens.setText("Add Polar Hydrogens");
        jMenuAddPolarHydrogens.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuAddPolarHydrogens_actionPerformed(e);
                }
            });

        JMenuItem jMenuRemoveNonPolarHydrogens = new JMenuItem();
        jMenuRemoveNonPolarHydrogens.setText("Remove Non-Ploar Hydrogens");
        jMenuRemoveNonPolarHydrogens.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuRemoveNonPolarHydrogens_actionPerformed(e);
                }
            });

        JMenuItem jMenuAddDescriptors = new JMenuItem();
        jMenuAddDescriptors.setText("Add Descriptors");
        jMenuAddDescriptors.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuAddDescriptors_actionPerformed(e);
                }
            });

        JMenuItem jMenuRemoveDescriptors = new JMenuItem();
        jMenuRemoveDescriptors.setText("Remove Descriptors");
        jMenuRemoveDescriptors.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuRemoveDescriptors_actionPerformed(e);
                }
            });

        //jMenuTools.add(jMenuAddPolarHydrogens);
        jMenuTools.add(jMenuAddHydrogens);

        //jMenuTools.add(jMenuRemoveNonPolarHydrogens);
        jMenuTools.add(jMenuRemoveHydrogens);
        jMenuTools.addSeparator();
        jMenuTools.add(jMenuAddDescriptors);

        //jMenuTools.add(jMenuRemoveDescriptors);
        JMenu jMenuHelp = new JMenu();
        JMenuItem jMenuHelpAbout = new JMenuItem();
        jMenuHelp.setText("Help");
        jMenuHelpAbout.setText("Usage");
        jMenuHelpAbout.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    jMenuHelpAbout_actionPerformed(e);
                }
            });

        jMenuFile.add(jMenuFileOpen);
        jMenuFile.add(jMenuFileSave);
        jMenuFile.addSeparator();
        jMenuFile.add(jMenuFileExit);

        jMenuHelp.add(jMenuHelpAbout);
        jMenuBar1.add(jMenuFile);
        jMenuBar1.add(jMenuView);
        jMenuBar1.add(jMenuTools);
        jMenuBar1.add(jMenuHelp);

        return jMenuBar1;
    }

    /**
     * Gets the jOEMol attribute of the ViewerFrame object
     *
     * @param type       Description of the Parameter
     * @param inputFile  Description of the Parameter
     */
    protected void getJOEMol(String type, String inputFile)
    {
        Molecule tmpMol = jmol;

        MoleculeFileIO[] tmpLoader = new MoleculeFileIO[1];

        try
        {
            jmol = MoleculeFileHelper.loadMolFromFile(tmpLoader, null,
                    inputFile, type);
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
        catch (MoleculeIOException e)
        {
            System.err.println(e.getMessage());
        }

        if ((tmpLoader != null) && (tmpLoader[0] != null))
        {
            loader = tmpLoader[0];
        }

        if (jmol != null)
        {
            if (tmpMol != null)
            {
                this.removeMolecule(tmpMol);
            }

            this.clear();
            this.addMolecule(jmol);
        }
    }

    /**
     *Overridden so we can exit when window is closed
     *
     * @param e  Description of the Parameter
     */
    protected void processWindowEvent(WindowEvent e)
    {
        super.processWindowEvent(e);

        if (e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            jMenuFileExit_actionPerformed(null);
        }
    }

    /*-------------------------------------------------------------------------*
     * public member variables
     *------------------------------------------------------------------------- */

    /**
     *Component initialization
     *
     * @exception Exception  Description of the Exception
     */
    private void jbInit() throws Exception
    {
        URL icon = this.getClass().getClassLoader().getResource(iconLocation);

        if (icon == null)
        {
            logger.error("Icon not found at " + iconLocation);
        }
        else
        {
            setIconImage(Toolkit.getDefaultToolkit().createImage(icon));
        }

        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout1);
        this.setSize(new Dimension(400, 300));
        this.setTitle("Molecule Viewer");

        //        statusBar.setText("Welcome to viewer");
        lookup = CentralLookup.getLookup();
        this.getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        molPanel = new JPanel3D();

        CentralDisplayAdapter c = new CentralDisplayAdapter(molPanel);
        lookup.addObject("CentralDisplay", c);

        panel.add(molPanel, BorderLayout.CENTER);
        this.getContentPane().add(panel, BorderLayout.CENTER);

        this.setJMenuBar(createMenubar());

        JButton nextButton = new JButton();
        nextButton.setText("Load next molecule");
        nextButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    nextButton_actionPerformed(e);
                }
            });
        contentPane.add(nextButton, BorderLayout.SOUTH);

        molPanel.addMolViewerEventListener(new MolViewerEventAdapter());
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
