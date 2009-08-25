///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: TransformationAtom.java,v $
//  Purpose:  Brute force symmetry analyzer.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Original author: (C) 1996, 2003 S. Patchkovskii, Serguei.Patchkovskii@sympatico.ca
//  Version:  $Revision: 1.7 $
//            $Date: 2005/02/17 16:48:36 $
//            $Author: wegner $
//
// Copyright Symmetry:       S. Patchkovskii, 1996,2000,2003
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program; if not, write to the Free Software
//  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
///////////////////////////////////////////////////////////////////////////////
package joelib2.math.symmetry;

/**
 * TransformationAtom.
 *
 * @.author     Serguei Patchkovskii
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.7 $, $Date: 2005/02/17 16:48:36 $
 */
public class TransformationAtom
{
    //~ Static fields/initializers /////////////////////////////////////////////

    private static final int UNDEFINED = 0;
    private static final int ROTATE_REFLECT_ATOM = 1;
    private static final int MIRROR_ATOM = 2;
    private static final int INVERT_ATOM = 3;
    private static final int ROTATE_ATOM = 4;

    //~ Instance fields ////////////////////////////////////////////////////////

    // SLOW
    //private Method method;
    //FAST
    public int method = UNDEFINED;

    //  private static Method methods[] = Symmetry.class.getMethods();
    private String methodName;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the TransformationAtom object
     *
     */
    public TransformationAtom(String _methodName) throws SymmetryException
    {
        methodName = _methodName;

        if (!existsMethod())
        {
            throw new SymmetryException("Method " + methodName +
                " does not exist in class " + Symmetry.class.getName());
        }
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void callTransformation(Symmetry invoker, SymmetryElement _el,
        SymAtom _from, SymAtom _to) throws SymmetryException
    {
        //FAST
        switch (method)
        {
        case ROTATE_REFLECT_ATOM:
            invoker.rotateReflectAtom(_el, _from, _to);

            break;

        case MIRROR_ATOM:
            invoker.mirrorAtom(_el, _from, _to);

            break;

        case INVERT_ATOM:
            invoker.invertAtom(_el, _from, _to);

            break;

        case ROTATE_ATOM:
            invoker.rotateAtom(_el, _from, _to);

            break;
        }

        // SLOW
        //              Object args[] = new Object[] { _el, _from, _to };
        //              try
        //              {
        //                      method.invoke(invoker, args);
        //              }
        //              catch (IllegalAccessException e)
        //              {
        //                      throw new SymmetryException(e.getMessage());
        //              }
        //              catch (InvocationTargetException e)
        //              {
        //                      throw new SymmetryException(e.getMessage());
        //              }
    }

    public boolean equals(Object obj)
    {
        if ((obj instanceof TransformationAtom) && (obj != null))
        {
            // SLOW
            //          return methodName.equals(other.methodName);
            // FAST
            return ((TransformationAtom) obj).method == method;
        }

        return false;
    }

    public int hashCode()
    {
        return this.method;
    }

    private boolean existsMethod()
    {
        //SLOW
        //
        // A additional disadvantage is, that the
        // mentioned methods must be PUBLIC !!!
        // or the reflection mechanism can not find them.
        //
        //              for (int i = 0; i < methods.length; i++)
        //              {
        //                      if (methods[i].getName().equals(methodName))
        //                      {
        //                                                              method = methods[i];
        //                              return true;
        //                      }
        //              }
        //FAST
        if (methodName.equals("rotateReflectAtom"))
        {
            method = ROTATE_REFLECT_ATOM;

            return true;
        }
        else if (methodName.equals("mirrorAtom"))
        {
            method = MIRROR_ATOM;

            return true;
        }
        else if (methodName.equals("invertAtom"))
        {
            method = INVERT_ATOM;

            return true;
        }
        else if (methodName.equals("rotateAtom"))
        {
            method = ROTATE_ATOM;

            return true;
        }

        return false;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
