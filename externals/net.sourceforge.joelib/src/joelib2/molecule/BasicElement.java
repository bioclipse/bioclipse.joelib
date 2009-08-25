///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: BasicElement.java,v $
//  Purpose:  Atom representation.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:36 $
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
package joelib2.molecule;

import java.awt.Color;

import java.util.StringTokenizer;


/**
 * Element entry in periodic table.
 *
 * @.author    wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:36 $
 */
public class BasicElement implements java.io.Serializable
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private int atomicNumber;

    private Color color;
    private double electronAffinity;
    private double enAllredRochow;
    private double enPauling;
    private double enSanderson;
    private String exteriorElectrons;
    private byte group;
    private double mass;
    private int maxBonds;
    private byte period;
    private double radiusBondOrder;
    private double radiusCovalent;
    private double radiusVanDerWaals;
    private String symbol;

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     *  Constructor for the JOEElement object
     */
    public BasicElement()
    {
    }

    /**
     *  Constructor for the JOEElement object
     *
     * @param num                Description of the Parameter
     * @param sym                Description of the Parameter
     * @param rcov               Description of the Parameter
     * @param rbo                Description of the Parameter
     * @param rvdw               Description of the Parameter
     * @param maxbo              Description of the Parameter
     * @param mass               Description of the Parameter
     * @param rgb                Description of the Parameter
     * @param exteriorElectrons  Description of the Parameter
     * @param period             Description of the Parameter
     * @param group              Description of the Parameter
     * @param enAllredRochow     Description of the Parameter
     * @param enPauling          Description of the Parameter
     * @param eAffinity          Description of the Parameter
     */
    public BasicElement(int num, String sym, double rcov, double rbo,
        double rvdw, int maxbo, Color rgb, double mass,
        String exteriorElectrons, byte period, byte group,
        double enAllredRochow, double enPauling, double enSanderson,
        double eAffinity)
    {
        this.atomicNumber = num;
        this.symbol = sym;
        this.radiusCovalent = rcov;
        this.radiusBondOrder = rbo;
        this.radiusVanDerWaals = rvdw;
        this.maxBonds = maxbo;
        this.color = rgb;
        this.mass = mass;
        this.exteriorElectrons = exteriorElectrons;
        this.period = period;
        this.group = group;
        this.enAllredRochow = enAllredRochow;
        this.enPauling = enPauling;
        this.electronAffinity = eAffinity;
        this.enSanderson = enSanderson;
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     *  Gets the atomicNum attribute of the JOEElement object
     *
     * @return   The atomicNum value
     */
    public final int getAtomicNumber()
    {
        return (atomicNumber);
    }

    /**
     * Gets the color attribute of the JOEElement object
     *
     * @return   The color value
     */
    public final Color getColor()
    {
        return (color);
    }

    /**
     * Gets the electronAffinity attribute of the JOEElement object
     *
     * @return   The electronAffinity value
     */
    public final double getElectronAffinity()
    {
        return electronAffinity;
    }

    /**
     * Gets the atom electronegativity after Allred and Rochow.
     *
     * @return   The allredRochowEN value
     */
    public final double getEnAllredRochow()
    {
        return enAllredRochow;
    }

    /**
     * Gets the atom electronegativity after Pauling.
     *
     * @return   The paulingEN value
     */
    public final double getEnPauling()
    {
        return enPauling;
    }

    /**
     * Gets the atom electronegativity after Pauling.
     *
     * @return   The paulingEN value
     */
    public final double getEnSanderson()
    {
        return enSanderson;
    }

    /**
     * Gets the exteriorElectrons attribute of the JOEElement object
     *
     * @return   The exteriorElectrons value
     */
    public final int getExteriorElectrons()
    {
        StringTokenizer tokenized = new StringTokenizer(exteriorElectrons,
                "spdf");

        int extEl = 0;
        String nextEl;

        while (tokenized.hasMoreTokens())
        {
            nextEl = tokenized.nextToken();
            extEl += Integer.parseInt(nextEl);
        }

        return extEl;
    }

    /**
     * Gets the group attribute of the JOEElement object
     *
     * @return   The group value
     */
    public final int getGroup()
    {
        return (group);
    }

    /**
     *  Gets the mass attribute of the JOEElement object
     *
     * @return   The mass value
     */
    public final double getMass()
    {
        return (mass);
    }

    /**
     *  Gets the maxBonds attribute of the JOEElement object
     *
     * @return   The maxBonds value
     */
    public final int getMaxBonds()
    {
        return (maxBonds);
    }

    /**
     * Gets the period attribute of the JOEElement object
     *
     * @return   The period value
     */
    public final int getPeriod()
    {
        return (period);
    }

    /**
     *  Gets the boRad attribute of the JOEElement object
     *
     * @return   The boRad value
     */
    public final double getRadiusBondOrder()
    {
        return (radiusBondOrder);
    }

    /**
     *  Gets the covalentRad attribute of the JOEElement object
     *
     * @return   The covalentRad value
     */
    public final double getRadiusCovalent()
    {
        return (radiusCovalent);
    }

    /**
     *  Gets the vdwRad attribute of the JOEElement object
     *
     * @return   The vdwRad value
     */
    public final double getRadiusVanDerWaals()
    {
        return (radiusVanDerWaals);
    }

    /**
     *  Gets the symbol attribute of the JOEElement object
     *
     * @return   The symbol value
     */
    public final String getSymbol()
    {
        return (symbol);
    }

    /**
     * @param atomicNumber The atomicNumber to set.
     */
    public void setAtomicNumber(int atomicNumber)
    {
        this.atomicNumber = atomicNumber;
    }

    /**
     * @param color The color to set.
     */
    public void setColor(Color color)
    {
        this.color = color;
    }

    /**
     * @param electronAffinity The electronAffinity to set.
     */
    public void setElectronAffinity(double elAffinity)
    {
        this.electronAffinity = elAffinity;
    }

    /**
     * @param enAllredRochow The enAllredRochow to set.
     */
    public void setEnAllredRochow(double enAR)
    {
        this.enAllredRochow = enAR;
    }

    /**
     * @param enPauling The enPauling to set.
     */
    public void setEnPauling(double enPauling)
    {
        this.enPauling = enPauling;
    }

    /**
     * @param enSanderson The enSanderson to set.
     */
    public void setEnSanderson(double enSanderson)
    {
        this.enSanderson = enSanderson;
    }

    /**
     * @param exteriorElectrons The exteriorElectrons to set.
     */
    public void setExteriorElectrons(String extEl)
    {
        this.exteriorElectrons = extEl;
    }

    /**
     * @param group The group to set.
     */
    public void setGroup(byte group)
    {
        this.group = group;
    }

    /**
     * @param mass The mass to set.
     */
    public void setMass(double mass)
    {
        this.mass = mass;
    }

    /**
     * @param maxBonds The maxBonds to set.
     */
    public void setMaxBonds(int maxBonds)
    {
        this.maxBonds = maxBonds;
    }

    /**
     * @param period The period to set.
     */
    public void setPeriod(byte period)
    {
        this.period = period;
    }

    /**
     * @param radiusBondOrder The radiusBondOrder to set.
     */
    public void setRadiusBondOrder(double rBO)
    {
        this.radiusBondOrder = rBO;
    }

    /**
     * @param radiusCovalence The radiusCovalence to set.
     */
    public void setRadiusCovalent(double rCov)
    {
        this.radiusCovalent = rCov;
    }

    /**
     * @param radiusVanDerWaals The radiusVanDerWaals to set.
     */
    public void setRadiusVanDerWaals(double rVdW)
    {
        this.radiusVanDerWaals = rVdW;
    }

    /**
     * @param symbol The symbol to set.
     */
    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
