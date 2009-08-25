///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: RandomNumber.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.2 $
//            $Date: 2005/02/17 16:48:41 $
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
package joelib2.util;

import java.util.Random;


/**
 * Random generator.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.2 $, $Date: 2005/02/17 16:48:41 $
 */
public class RandomNumber
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private int seed;

    //  private DoubleType d;
    //  private int m,a,c;
    //  private int p;
    //  private int i;
    //  private int x;
    //  private boolean jOERandomUseSysRand;
    private Random wheel;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOERandom object
     */
    public RandomNumber()
    {
        this(0);
    }

    /**
     *  Constructor for the JOERandom object
     *
     * @param  _seed  Description of the Parameter
     */
    public RandomNumber(int _seed)
    {
        //      this.jOERandomUseSysRand= useSysRand;
        //      p = 70092;
        //      determineSequence(p,m,a,c);
        seed = _seed;

        /* seed  */
        wheel = new Random(seed);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public double nextFloat()
    {
        //      if (OERandomUseSysRand) { return(rand()/RAND_MAX); }
        //      do {
        //        doubleMultiply(a,x,d);
        //        doubleAdd(d,c);
        //        x = doubleModulus(d,m);
        //      } while( x >= p );
        //
        //      return((double)x/p);
        return wheel.nextDouble();
    }

    /**
     *  Description of the Method
     *
     * @return    Description of the Return Value
     */
    public int nextInt()
    {
        //      if (OERandomUseSysRand) { return(rand()); }
        //      do {
        //        doubleMultiply(a,x,d);
        //        doubleAdd(d,c);
        //        x = doubleModulus(d,m);
        //      } while( x >= p );
        //
        //      return(x);
        return wheel.nextInt();
    }

    //   public void  seed(int seed) {x = seed;}

    /**
     *  Description of the Method
     */
    public void timeSeed()
    {
        //      srand( System.currentTimeMillis() );
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
