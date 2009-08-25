///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: AbstractDatabase.java,v $
//  Purpose:  Descriptor base class.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
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

import joelib2.algo.morgan.Morgan;
import joelib2.algo.morgan.types.BasicTieResolver;

import joelib2.data.IdentifierExpertSystem;

import joelib2.feature.types.atomlabel.AtomHeavyValence;
import joelib2.feature.types.atomlabel.AtomImplicitValence;
import joelib2.feature.types.atomlabel.AtomInRing;
import joelib2.feature.types.atomlabel.AtomPartialCharge;

import joelib2.molecule.Atom;
import joelib2.molecule.Molecule;

import joelib2.smiles.SMILESGenerator;

import joelib2.util.types.BasicStringString;

import wsi.ra.database.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Category;


/**
 * Helper class to access and store molecules.
 *
 * @.author     wegnerj
 */
public abstract class AbstractDatabase implements DatabaseInterface
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(AbstractDatabase.class
            .getName());
    private static final String VENDOR = "http://joelib.sf.net";
    private static final String RELEASE_VERSION = "$Revision: 1.6 $";
    private static final String RELEASE_DATE = "$Date: 2005/02/17 16:48:41 $";
    private static final Class[] DEPENDENCIES =
        new Class[]
        {
            AtomHeavyValence.class, AtomImplicitValence.class,
            AtomInRing.class, AtomPartialCharge.class
        };

    //~ Instance fields ////////////////////////////////////////////////////////

    public Morgan morgan = new Morgan(new BasicTieResolver());
    public BasicStringString propertyHASH = new BasicStringString("HASH",
            "BIGINT");
    public BasicStringString propertyID = new BasicStringString("ID", "BIGINT");
    public BasicStringString propertyNAME = new BasicStringString("NAME",
            "VARCHAR(100)");
    public BasicStringString propertySHASH = new BasicStringString("SHASH",
            "BIGINT");

    //~ Constructors ///////////////////////////////////////////////////////////

    public AbstractDatabase()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Initialize " + this.getClass().getName());
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public abstract void createTable(String tableName) throws Exception;

    public abstract boolean existsTable(String tableName) throws Exception;

    public abstract void insertMolecule(String tableName, Molecule mol, int id)
        throws Exception;

    public static Class[] getDependencies()
    {
        return DEPENDENCIES;
    }

    /**
     * Sloppy Hashcode which uses only topological informations
     * without E/Z isomerism and S/R chirality. This hascode is really helpfull
     * to identify duplicate topological molecule entries.<br>
     * <br>
     * Strict SMILES hashcode which uses only the (hopefully) unique SMILES
     * pattern. Unfortunetaly the Morgen algorithm produces not generally
     * Unique renumberings (depends on the tie resolvers !!!), so
     * different numberings causes different SMILES patterns and
     * different hashcodes !:-(<br>
     * <br>
     * But this hashcode can be used to check for molecules, which exists
     * already in their actual form taking topological, E/Z isomerism and
     * S/R chirality informations into account.
     * <br>
     * The hashcode uses also partial charges, so you will get another result
     * if you are using non-default (Gasteiger-Marsili) partial charges.
     *
     * @param mol
     * @return int value[0] contains the sloppy hashcode, value[1] the strict
     * SMILES hascode
     */
    public static int getHashcode(Molecule mol)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("get number of rotors");
        }

        int hash = mol.getRotorsSize();
        Atom atom;

        // get number of SSSR
        if (logger.isDebugEnabled())
        {
            logger.debug("get number of SSSR rings");
        }

        if (mol.getSSSR() != null)
        {
            hash = (31 * hash) + mol.getSSSR().size();
        }

        for (int i = 1; i <= mol.getAtomsSize(); i++)
        {
            atom = mol.getAtom(i);

            if (logger.isDebugEnabled())
            {
                logger.debug("get atom index");
            }

            hash = (31 * hash) + atom.getIndex();

            if (logger.isDebugEnabled())
            {
                logger.debug("get atimic number");
            }

            hash = (31 * hash) + atom.getAtomicNumber();

            if (logger.isDebugEnabled())
            {
                logger.debug("get heavy valence");
            }

            hash = (31 * hash) + AtomHeavyValence.valence(atom);

            if (logger.isDebugEnabled())
            {
                logger.debug("get implicite valence");
            }

            hash = (31 * hash) + AtomImplicitValence.getImplicitValence(atom);

            if (logger.isDebugEnabled())
            {
                logger.debug("get partial charge");
            }

            hash = (31 * hash) +
                ((int) (AtomPartialCharge.getPartialCharge(atom) * 100.0));

            if (logger.isDebugEnabled())
            {
                logger.debug("get ring flag");
            }

            if (AtomInRing.isInRing(atom))
            {
                hash = (31 * hash) + 1;
            }
            else
            {
                hash = (31 * hash) + 2;
            }

            //System.out.println("atom "+atom.getIdx()+" is aromatic "+atom.isAromatic());
        }

        return hash;
    }

    public static String getReleaseDate()
    {
        return VENDOR;
    }

    public static String getReleaseVersion()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_VERSION);
    }

    /**
     * Strict SMILES hashcode which uses the (hopefully) unique SMILES
     * pattern. Unfortunately the Morgen algorithm produces not generally
     * Unique renumberings (depends on the tie resolvers !!!), so
     * different numberings causes different SMILES patterns and
     * different hashcodes !:-(<br>
     * But this hashcode can be used to check for molecules, which exists
     * already in their actual form taking topological, E/Z isomerism and
     * S/R chirality informations into account as represented in the
     * joelib/smiles/JOEMol2Smi class.
     */
    public static int getSMILESHashcode(Molecule mol)
    {
        SMILESGenerator m2s = new SMILESGenerator();

        m2s.init();
        m2s.correctAromaticAmineCharge(mol);

        StringBuffer smiles = new StringBuffer(1000);
        m2s.createSmiString(mol, smiles);

        String smilesS = smiles.toString();

        //System.out.println("SMILES: "+smilesS);
        if (smilesS != null)
        {
            return smilesS.hashCode();
        }
        else
        {
            return 0;
        }
    }

    public static String getVendor()
    {
        return IdentifierExpertSystem.transformCVStag(RELEASE_DATE);
    }

    /**
     * Sloppy Hashcode which uses only topological informations
     * without E/Z isomerism and S/R chirality
     */
    public int[] getMoleculeHASH(Molecule mol)
    {
        Molecule tMol = (Molecule) mol.clone(false);
        tMol.deleteHydrogens();

        morgan.calculate(tMol);

        Molecule rMol = morgan.renumber(tMol);

        return new int[]{getHashcode(rMol), getSMILESHashcode(rMol)};
    }

    public ResultSet selectBy(String tableName, String select, String by,
        String value) throws Exception
    {
        Statement statement = null;

        //              PreparedStatement insertStatement = null;
        //              Connection connection = null;
        if (DatabaseConnection.instance().isConnectionAvailable())
        {
            //                  connection = DatabaseConnection.instance().getConnection();
            statement = DatabaseConnection.instance().getStatement();

            statement.execute("SELECT " + select + " FROM " + tableName +
                " WHERE " + by + " = '" + value + "'");

            return statement.getResultSet();
        }

        return null;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
