///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: DatabaseConnection.java,v $
//  Purpose:  Descriptor base class.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Fred Rapp, Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/01/16 18:08:07 $
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
package wsi.ra.database;

import wsi.ra.tool.BasicPropertyHolder;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Category;


/**
 * Simple JDBC database connection.
 */
public class DatabaseConnection
{
    //~ Static fields/initializers /////////////////////////////////////////////

    // Obtain a suitable logger.
    private static Category logger = Category.getInstance(
            "wsi.ra.database.DatabaseConnection");
    static private DatabaseConnection instance = null;

    //~ Instance fields ////////////////////////////////////////////////////////

    private Connection connection = null;
    private String driver;
    private boolean isAvailable = false;
    private String location;
    private String password;
    private BasicPropertyHolder propertyHolder;
    private String username;

    //~ Constructors ///////////////////////////////////////////////////////////

    private DatabaseConnection()
    {
        propertyHolder = BasicPropertyHolder.instance();

        String value;

        if ((value = propertyHolder.getProperty(this, "username")) == null)
        {
            logger.error("Database 'username' not defined.");
        }
        else
        {
            username = value;

            if (logger.isDebugEnabled())
            {
                logger.debug("Database user name=" + value);
            }
        }

        if ((value = propertyHolder.getProperty(this, "password")) == null)
        {
            logger.error("Database 'password' not defined.");
        }
        else
        {
            password = value;

            if (logger.isDebugEnabled())
            {
                logger.debug("Database password=" + value);
            }
        }

        if ((value = propertyHolder.getProperty(this, "location")) == null)
        {
            logger.error("Database 'location' not defined.");
        }
        else
        {
            location = value;

            if (logger.isDebugEnabled())
            {
                logger.debug("Database location=" + value);
            }
        }

        if ((value = propertyHolder.getProperty(this, "driver")) == null)
        {
            logger.error("Database 'driver' not defined.");
        }
        else
        {
            driver = value;

            if (logger.isDebugEnabled())
            {
                logger.debug("Database driver=" + value);
            }
        }

        // check driver name
        if (driver.equals(""))
        {
            return;
        }

        // try to load given database driver
        try
        {
            Class.forName(driver).newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return;
        }

        // try to get a connection
        getConnection();

        isAvailable = true;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Returns a valid instance of this class.
     */
    static public DatabaseConnection instance()
    {
        if (instance == null)
        {
            instance = new DatabaseConnection();
        }

        return instance;
    }

    /**
     * Checks if a table of the given name is available through the given database
     * connection (looks only for standard, not system tables).
     */
    public boolean existsTable(String name) throws Exception
    {
        boolean found = false;

        //    try {
        // get database meta data
        DatabaseMetaData metaData = connection.getMetaData();

        // get table names
        ResultSet resultSet = metaData.getTables(null, null, null,
                new String[]{"TABLE"});

        // try to find table name
        while (resultSet.next())
        {
            String tableName = resultSet.getString("TABLE_NAME");

            if (name.equalsIgnoreCase(tableName))
            {
                found = true;

                break;
            }
        }

        //    }
        //    catch (Exception e) { e.printStackTrace(); }
        return found;
    }

    public Connection getConnection()
    {
        if (connection == null)
        {
            try
            {
                connection = DriverManager.getConnection(location, username,
                        password);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                connection = null;
            }
        }
        else if (connection != null)
        {
            try
            {
                existsTable("connectionTest");
            }
            catch (Exception e)
            {
                connection = null;
            }
        }

        return connection;
    }

    public Statement getStatement()
    {
        if (!isAvailable || (connection == null))
        {
            return null;
        }

        Statement statement = null;

        try
        {
            statement = connection.createStatement();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return statement;
    }

    public boolean isAvailable()
    {
        return isAvailable;
    }

    public boolean isConnectionAvailable()
    {
        if (getConnection() != null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
