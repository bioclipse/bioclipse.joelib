///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: JCAMPData.java,v $
//  Purpose:  Reader/Writer for SDF files.
//  Language: Java
//  Compiler: JDK 1.4
//  Authors:  Joerg Kurt Wegner
//  Version:  $Revision: 1.6 $
//            $Date: 2005/02/17 16:48:35 $
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
package joelib2.jcamp;

import java.util.List;
import java.util.Vector;


/**
 * Stores single JCAMP data.
 *
 * @.author     wegnerj
 * @.license GPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:35 $
 * @.cite dl93
 * @.cite dw88
 * @.cite ghhjs91
 * @.cite lhdl94
 * @.cite dhl90
 */
public class JCAMPData
{
    //~ Instance fields ////////////////////////////////////////////////////////

    private List csEntries;
    private List dxEntries;
    private List linkEntries;

    //~ Constructors ///////////////////////////////////////////////////////////

    public JCAMPData()
    {
        dxEntries = new Vector();
        csEntries = new Vector();
        linkEntries = new Vector();
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void addCSEntry(JCAMPDataBlock cs)
    {
        csEntries.add(cs);
    }

    public void addDXEntry(JCAMPDataBlock dx)
    {
        dxEntries.add(dx);
    }

    public void addLinkEntry(JCAMPDataBlock link)
    {
        linkEntries.add(link);
    }

    public JCAMPDataBlock getDXEntry(int index)
    {
        if (dxEntries.size() == 0)
        {
            return null;
        }

        return (JCAMPDataBlock) dxEntries.get(index);
    }

    //public String toString(){
    //  String dummy=new String("$$ written by JCampInterpreter\r\n");
    //  LabelData[] labelData=dataBlock.getParameter();
    //  LabelData singleLabelData=null;
    //
    //  try{
    //    singleLabelData=dataBlock.getParameter("TITLE");
    //    dummy=new String(dummy+singleLabelData.label+
    //                     "="+singleLabelData.data+"\r\n");
    //  }
    //  catch(MissingJCampLabel e){
    //    //should never happen
    //    e.printStackTrace();
    //  }
    //  for(int i=0;i<labelData.length;i++){
    //    if(!labelData[i].label.equals("TITLE") &&
    //       !labelData[i].label.equals("END"))
    //    {
    //      dummy=new String(dummy+labelData[i].label+"="+labelData[i].data+"\r\n");
    //    }
    //  }
    //  for(int i=0;i<internalDataBlock.size();i++){
    //    ((JCampInterpreter)internalDataBlock.get(i)).toString();
    //  }
    //  try{
    //    singleLabelData=dataBlock.getParameter("END");
    //    dummy=new String(dummy+singleLabelData.label+
    //                     "="+singleLabelData.data+"\r\n");
    //  }
    //  catch(MissingJCampLabel e){
    //    //should never happen
    //    e.printStackTrace();
    //  }
    //  return dummy;
    //}
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
