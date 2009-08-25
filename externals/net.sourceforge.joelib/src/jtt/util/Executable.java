///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Executable.java,v $
//  Purpose:  Calls corina to create 3D structures.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:43 $
//            $Author: wegner $
//
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
///////////////////////////////////////////////////////////////////////////////
package jtt.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.log4j.Category;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;


/*==========================================================================*
 * CLASS DECLARATION
 *==========================================================================   */

/**
 *  Calls corina to create 3D structures.
 *
 * @.author     wegnerj
 */
public class Executable
{
    //~ Static fields/initializers /////////////////////////////////////////////

    /*-------------------------------------------------------------------------*
     * private static member variables
     *-------------------------------------------------------------------------   */

    /**
     *  Obtain a suitable logger.
     */
    private static Category logger = Category.getInstance(
            "jtt.util.Executable");

    //~ Constructors ///////////////////////////////////////////////////////////

    /*-------------------------------------------------------------------------*
     * private member variables
     *-------------------------------------------------------------------------   */
    /*-------------------------------------------------------------------------*
     * constructor
     *-------------------------------------------------------------------------   */

    /**
     *  Constructor for the Corina object
     */
    public Executable()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @param  mol  Description of the Parameter
     * @return      Description of the Return Value
     */
    public static boolean execute(String[] arguments, boolean wait)
    {
        for (int i = 0; i < arguments.length; i++)
        {
            System.out.println(arguments[i]);
        }

        //String args[]=new String[arguments.length+1];
        //System.arraycopy(arguments,0,args,1,arguments.length);
        //arguments[0]="jade";
        String[] args = new String[arguments.length];
        System.arraycopy(arguments, 0, args, 0, arguments.length);

        // execute
        Process process;
        StringBuffer buffer = new StringBuffer(1000);
        StringBuffer errors = new StringBuffer(1000);

        try
        {
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec(args);

            // set input pipe
            BufferedReader in = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));

            // set output pipe
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                        process.getOutputStream()));
            out.close();

            BufferedReader error = new BufferedReader(new InputStreamReader(
                        process.getErrorStream()));

            // wait for extern process termination
            if (wait)
            {
                process.waitFor();
            }

            // get input pipe data
            String nextLine = null;

            System.out.println("get in");

            while ((nextLine = in.readLine()) != null)
            {
                buffer.append(nextLine + "\n");

                //System.out.println("IN: "+nextLine);
            }

            while ((nextLine = error.readLine()) != null)
            {
                errors.append(nextLine + "\n");

                //System.out.println("ERROR: "+nextLine);
            }
        }
        catch (Exception e)
        {
            logger.error("Could not start executable: " + args[0]);
            e.printStackTrace();

            return false;
        }

        //ByteArrayInputStream sReader = new ByteArrayInputStream(buffer.toString().getBytes());
        return true;
    }

    /*-------------------------------------------------------------------------*
     * public static methods
     *-------------------------------------------------------------------------   */
    public static boolean execute(String dir, String exe, String[] arguments)
    {
        System.out.println("EXE: " + dir);
        System.out.println("EXE: " + exe);

        for (int i = 0; i < arguments.length; i++)
        {
            System.out.println("EXE: " + arguments[i]);
        }

        Execute exec = new Execute();
        exec.setAntRun(new Project());
        exec.setWorkingDirectory(new File(dir));

        String[] args = new String[arguments.length + 1];
        System.arraycopy(arguments, 0, args, 1, arguments.length);
        args[0] = exe;
        exec.setCommandline(args);

        try
        {
            exec.execute();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //        Jikes t;
        return true;
    }

    /**
      *  The main program for the TestSmarts class
      *
      * @param  args  The command line arguments
      */
    public static void main(String[] args)
    {
        //Executable.execute(args, true);
        Executable.execute(args[0], args[1], new String[]{args[2]});
        System.exit(0);
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
