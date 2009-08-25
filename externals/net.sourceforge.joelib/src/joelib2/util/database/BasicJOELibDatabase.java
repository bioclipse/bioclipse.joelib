///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicJOELibDatabase.java,v $
//  Purpose:  Descriptor base class.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.3 $
//            $Date: 2005/02/17 16:48:41 $
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
package joelib2.util.database;

import joelib2.io.BasicIOType;
import joelib2.io.BasicIOTypeHolder;
import joelib2.io.MoleculeFileHelper;
import joelib2.io.MoleculeFileIO;

import joelib2.molecule.BasicConformerMolecule;
import joelib2.molecule.Molecule;

import joelib2.util.types.BasicStringString;

import wsi.ra.database.DatabaseConnection;

import java.io.FileInputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Category;


/**
 * Helper class to access and store molecules.
 *
 * @.author     wegnerj
 */
public class BasicJOELibDatabase extends AbstractDatabase
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            BasicJOELibDatabase.class.getName());
    private static final BasicIOType SDF = BasicIOTypeHolder.instance()
                                                            .getIOType("SDF");
    private static final BasicIOType CML = BasicIOTypeHolder.instance()
                                                            .getIOType("CML");
    private static final BasicIOType SMILES = BasicIOTypeHolder.instance()
                                                               .getIOType(
            "SMILES");

    //~ Instance fields ////////////////////////////////////////////////////////

    public String defaultTableName = "MOLECULES";
    public final BasicStringString propertyCML = new BasicStringString("CML",
            "LONGTEXT");
    public final BasicStringString propertySDF = new BasicStringString("SDF",
            "LONGTEXT");
    public final BasicStringString propertySMILES = new BasicStringString(
            "SMILES", "LONGTEXT");
    private int defaultID = 1;
    private BasicStringString[] tableProperties =
        new BasicStringString[]
        {
            propertyNAME, propertyID, propertyHASH, propertySHASH, propertySDF,
            propertyCML, propertySMILES
        };

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  The main program for the TestSmarts class
     *
     * @param  args  The command line arguments
     */
    public static void main(String[] args)
    {
        BasicJOELibDatabase dbTest = new BasicJOELibDatabase();

        logger.info("Open file:" + args[0]);
        logger.info("with input type:" + args[1]);

        dbTest.storeMolsInDatabase(args[0],
            BasicIOTypeHolder.instance().getIOType(args[1]));
    }

    public void createTable()
    {
        try
        {
            createTable(defaultTableName);
        }
        catch (Exception e)
        {
            logger.warn(e.getMessage());
        }
    }

    public void createTable(String tableName) throws Exception
    {
        Statement statement = null;

        //              PreparedStatement insertStatement = null;
        //              Connection connection = null;
        if (DatabaseConnection.instance().isConnectionAvailable())
        {
            //                  connection = DatabaseConnection.instance().getConnection();
            statement = DatabaseConnection.instance().getStatement();

            // create new table if necessary
            try
            {
                if (!DatabaseConnection.instance().existsTable(tableName))
                {
                    statement.execute("CREATE TABLE " + tableName + " (" +
                        propertyNAME.getStringValue1() + " " +
                        propertyNAME.getStringValue2() + " NOT NULL , " +
                        propertyID.getStringValue1() + " " +
                        propertyID.getStringValue2() + " NOT NULL , " +
                        propertyHASH.getStringValue1() + " " +
                        propertyHASH.getStringValue2() + " NOT NULL , " +
                        propertySHASH.getStringValue1() + " " +
                        propertySHASH.getStringValue2() + " NOT NULL , " +
                        propertySDF.getStringValue1() + " " +
                        propertySDF.getStringValue2() + ", " +
                        propertyCML.getStringValue1() + " " +
                        propertyCML.getStringValue2() + ", " +
                        propertySMILES.getStringValue1() + " " +
                        propertySMILES.getStringValue2() + " NOT NULL , " +
                        "CONSTRAINT PURPOSEDMOLS PRIMARY KEY (" +
                        propertyID.getStringValue1() + ", " +
                        propertyHASH.getStringValue1() + ", " +
                        propertySHASH.getStringValue1() + ") )");
                }

                if (logger.isDebugEnabled())
                {
                    logger.debug("Created table: " + tableName);
                }
            }
            catch (Exception d)
            {
                d.printStackTrace();
            }
        }
        else
        {
            logger.error("Database connection can not be established.");
            System.exit(1);
        }
    }

    public boolean existsTable()
    {
        try
        {
            return existsTable(defaultTableName);
        }
        catch (Exception e)
        {
            logger.warn(e.getMessage());

            return false;
        }
    }

    public boolean existsTable(String tableName) throws Exception
    {
        if (DatabaseConnection.instance().isConnectionAvailable())
        {
            return DatabaseConnection.instance().existsTable(tableName);
        }

        return false;
    }

    public BasicStringString[] getTableProperties()
    {
        return tableProperties;
    }

    public void insertMolecule(String tableName, Molecule mol, int id)
    {
        try
        {
            insertMolecule(tableName, mol, id, true, false);
        }
        catch (Exception e)
        {
            logger.warn(e.getMessage());
        }
    }

    public void insertMolecule(String tableName, Molecule mol, int id,
        boolean ignoreDuplicateHashes, boolean ignoreDuplicateSHashes)
        throws Exception
    {
        //              Statement statement = null;
        PreparedStatement insertStatement = null;
        Connection connection = null;
        boolean addMolecule = true;

        if (DatabaseConnection.instance().isConnectionAvailable())
        {
            connection = DatabaseConnection.instance().getConnection();

            //                  statement = DatabaseConnection.instance().getStatement();
            String sdf = null;
            String cml = null;
            String smiles = null;
            String moleculeHASHExistsEntry = null;
            String moleculeSHASHExistsEntry = null;

            sdf = mol.toString(SDF);
            cml = mol.toString(CML);
            smiles = mol.toString(SMILES);

            if (sdf == null)
            {
                logger.error(SDF.toString() + " type can not be created.");
            }

            if (cml == null)
            {
                logger.error(CML.toString() + " type can not be created.");
            }

            if (smiles == null)
            {
                logger.error(SMILES.toString() + " type can not be created.");
            }

            //System.out.print(mol.getTitle() + " " + smiles);
            // define prepared statement
            insertStatement = connection.prepareStatement("INSERT INTO " +
                    tableName +
                    " (NAME, ID, HASH, SHASH, SDF, CML, SMILES) VALUES (?, ?, ?, ?, ?, ?, ?)");

            // calculate molecule HASH formaly known as Hashcode !;-)
            int[] hashes = getMoleculeHASH(mol);

            // sloppy Hashcode which uses only topological informations
            // without E/Z isomerism and S/R chirality
            int molHASH = hashes[0];
            int molSHASH = hashes[1];

            moleculeSHASHExistsEntry = null;

            ResultSet resultset = selectBy(tableName,
                    propertyNAME.getStringValue1(),
                    propertySHASH.getStringValue1(),
                    Integer.toString(molSHASH));

            while (resultset.next())
            {
                //moleculeExistsEntry = resultset.getString("HASH");
                moleculeSHASHExistsEntry = resultset.getString(propertyNAME
                        .getStringValue1());
            }

            if (moleculeSHASHExistsEntry != null)
            {
                if (!ignoreDuplicateSHashes)
                {
                    addMolecule = false;
                }

                logger.warn(mol.getTitle() + " exists as " +
                    moleculeSHASHExistsEntry + ", it is not added.");
            }
            else
            {
                moleculeHASHExistsEntry = null;
                resultset = selectBy(tableName, propertyNAME.getStringValue1(),
                        propertyHASH.getStringValue1(),
                        Integer.toString(molHASH));

                while (resultset.next())
                {
                    //moleculeExistsEntry = resultset.getString("HASH");
                    moleculeHASHExistsEntry = resultset.getString(propertyNAME
                            .getStringValue1());
                }

                if (moleculeHASHExistsEntry != null)
                {
                    if (!ignoreDuplicateSHashes)
                    {
                        addMolecule = false;
                    }

                    logger.warn("Check if equal molecules ! " + mol.getTitle() +
                        " exists as " + moleculeHASHExistsEntry +
                        ". Both have HASH='" + molHASH + "'");
                }
            }

            if (addMolecule)
            {
                try
                {
                    insertStatement.setString(1, mol.getTitle());
                    insertStatement.setInt(2, id);
                    insertStatement.setInt(3, molHASH);
                    insertStatement.setInt(4, molSHASH);
                    insertStatement.setString(5, sdf);
                    insertStatement.setString(6, cml);
                    insertStatement.setString(7, smiles);

                    //insertStatement.execute();
                    insertStatement.executeUpdate();
                }
                catch (SQLException e)
                {
                    //e.printStackTrace();
                    logger.error("Entry exists already in database: Name:" +
                        mol.getTitle() + " Id:" + id);
                }

                if (logger.isDebugEnabled())
                {
                    logger.debug("Molecule '" + mol.getTitle() +
                        "' was added to database.");
                }
            }
        }
    }

    public void storeMolsInDatabase(String inputFile, BasicIOType inType)
    {
        if (!existsTable())
        {
            createTable();
        }

        FileInputStream in = null;
        MoleculeFileIO loader = null;

        try
        {
            in = new FileInputStream(inputFile);
            loader = MoleculeFileHelper.getMolReader(in, inType);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (!loader.readable())
        {
            logger.error(inType.getRepresentation() + " is not readable.");
            logger.error("You're invited to write one !;-)");
            System.exit(1);
        }

        // load molecules and handle test
        Molecule mol = new BasicConformerMolecule(inType, inType);
        boolean success;

        for (;;)
        {
            try
            {
                success = loader.read(mol);

                if (!success)
                {
                    break;
                }

                if (mol.isEmpty())
                {
                    logger.error("No molecule loaded.");
                    System.exit(1);
                }

                if (logger.isDebugEnabled())
                {
                    logger.debug("try to add " + mol.getTitle());
                }

                insertMolecule(defaultTableName, mol, defaultID, true, false);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.exit(1);
            }

            defaultID++;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
