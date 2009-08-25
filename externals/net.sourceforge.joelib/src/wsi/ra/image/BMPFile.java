///////////////////////////////////////////////////////////////////////////////
//Filename: $RCSfile: BMPFile.java,v $
//Purpose:  BMP file encoder.
//Language: Java
//Compiler: Java (TM) 2 Platform Standard Edition 5.0
//Authors:  Fred Rapp, Joerg Kurt Wegner
//Version:  $Revision: 1.6 $
//                      $Date: 2005/02/17 16:48:43 $
//                      $Author: wegner $
//
//Copyright (C) 2002-2003  The Jmol Development Team
// Copyright OELIB:          OpenEye Scientific Software, Santa Fe,
//                           U.S.A., 1999,2000,2001
// Copyright JOELIB/JOELib2: Dept. Computer Architecture, University of
//                           Tuebingen, Germany, 2001,2002,2003,2004,2005
// Copyright JOELIB/JOELib2: ALTANA PHARMA AG, Konstanz, Germany,
//                           2003,2004,2005
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
// 02111-1307  USA.
///////////////////////////////////////////////////////////////////////////////
package wsi.ra.image;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.PixelGrabber;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * A BMP file encoder.
 *
 * @.author Jean-Pierre Dube
 * @.author http://www.javaworld.com/javaworld/javatips/jw-javatip60.html
 * @.author Christian Ribeaud (christian.ribeaud@genedata.com)
 * @.author egonw
 * @.license    LGPL
 * @.cvsversion    $Revision: 1.6 $, $Date: 2005/02/17 16:48:43 $
 */
public class BMPFile extends Component
{
    //~ Static fields/initializers /////////////////////////////////////////////

    //--- Private constants
    private final static int BITMAPFILEHEADER_SIZE = 14;
    private final static int BITMAPINFOHEADER_SIZE = 40;

    //~ Instance fields ////////////////////////////////////////////////////////

    //--- Private variable declaration
    //--- Bitmap file header
    //    private byte[] bitmapFileHeader = new byte[14];
    //--- Bitmap info header
    //    private byte[] bitmapInfoHeader = new byte[40];
    private int bfOffBits = BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE;
    private int bfReserved1 = 0;
    private int bfReserved2 = 0;
    private int bfSize = 0;
    private byte[] bfType = {(byte) 'B', (byte) 'M'};
    private int biBitCount = 24;
    private int biClrImportant = 0;
    private int biClrUsed = 0;
    private int biCompression = 0;
    private int biHeight = 0;
    private int biPlanes = 1;
    private int biSize = BITMAPINFOHEADER_SIZE;
    private int biSizeImage = 0x030000;

    //--- Bitmap raw data
    private int[] bitmap;
    private int biWidth = 0;
    private int biXPelsPerMeter = 0x0;
    private int biYPelsPerMeter = 0x0;

    //--- File section
    private OutputStream os;

    //~ Constructors ///////////////////////////////////////////////////////////

    //--- Default constructor
    public BMPFile()
    {
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void saveBitmap(OutputStream os, Image parImage)
    {
        this.os = os;

        try
        {
            saveBitmap(parImage, parImage.getWidth(null),
                parImage.getHeight(null));
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public void saveBitmap(String parFilename, Image parImage, int parWidth,
        int parHeight)
    {
        try
        {
            os = new FileOutputStream(parFilename);
            saveBitmap(parImage, parWidth, parHeight);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    /*
     * convertImage converts the memory image to the bitmap format (BRG).
     * It also computes some information for the bitmap info header.
     *
     */
    private boolean convertImage(Image parImage, int parWidth, int parHeight)
    {
        int pad;
        bitmap = new int[parWidth * parHeight];

        PixelGrabber pg = new PixelGrabber(parImage, 0, 0, parWidth, parHeight,
                bitmap, 0, parWidth);

        try
        {
            pg.grabPixels();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();

            return (false);
        }

        pad = (4 - ((parWidth * 3) % 4)) * parHeight;
        biSizeImage = ((parWidth * parHeight) * 3) + pad;
        bfSize = biSizeImage + BITMAPFILEHEADER_SIZE + BITMAPINFOHEADER_SIZE;
        biWidth = parWidth;
        biHeight = parHeight;

        return (true);
    }

    /*
     *
     * intToDWord converts an int to a double word, where the return
     * value is stored in a 4-byte array.
     *
     */
    private byte[] intToDWord(int parValue)
    {
        byte[] retValue = new byte[4];
        retValue[0] = (byte) (parValue & 0x00FF);
        retValue[1] = (byte) ((parValue >> 8) & 0x000000FF);
        retValue[2] = (byte) ((parValue >> 16) & 0x000000FF);
        retValue[3] = (byte) ((parValue >> 24) & 0x000000FF);

        return (retValue);
    }

    /*
     *
     * intToWord converts an int to a word, where the return
     * value is stored in a 2-byte array.
     *
     */
    private byte[] intToWord(int parValue)
    {
        byte[] retValue = new byte[2];
        retValue[0] = (byte) (parValue & 0x00FF);
        retValue[1] = (byte) ((parValue >> 8) & 0x00FF);

        return (retValue);
    }

    /*
     *  The saveMethod is the main method of the process. This method
     *  will call the convertImage method to convert the memory image to
     *  a byte array; method writeBitmapFileHeader creates and writes
     *  the bitmap file header; writeBitmapInfoHeader creates the
     *  information header; and writeBitmap writes the image.
     *
     */
    private void save(Image parImage, int parWidth, int parHeight)
    {
        try
        {
            convertImage(parImage, parWidth, parHeight);
            writeBitmapFileHeader();
            writeBitmapInfoHeader();
            writeBitmap();
        }
        catch (Exception saveEx)
        {
            saveEx.printStackTrace();
        }
    }

    private void saveBitmap(Image parImage, int parWidth, int parHeight)
        throws IOException
    {
        save(parImage, parWidth, parHeight);

        //os.close();
    }

    /*
     * writeBitmap converts the image returned from the pixel grabber to
     * the format required. Remember: scan lines are inverted in
     * a bitmap file!
     *
     * Each scan line must be padded to an even 4-byte boundary.
     */
    private void writeBitmap()
    {
        int size;
        int value;
        int j;
        int i;
        int rowCount;
        int rowIndex;
        int lastRowIndex;
        int pad;
        int padCount;
        byte[] rgb = new byte[3];
        size = (biWidth * biHeight) - 1;
        pad = 4 - ((biWidth * 3) % 4);

        if (pad == 4)
        { // <==== Bug correction
            pad = 0; // <==== Bug correction
        }

        rowCount = 1;
        padCount = 0;
        rowIndex = size - biWidth;
        lastRowIndex = rowIndex;

        try
        {
            for (j = 0; j < size; j++)
            {
                value = bitmap[rowIndex];
                rgb[0] = (byte) (value & 0xFF);
                rgb[1] = (byte) ((value >> 8) & 0xFF);
                rgb[2] = (byte) ((value >> 16) & 0xFF);
                os.write(rgb);

                if (rowCount == biWidth)
                {
                    padCount += pad;

                    for (i = 1; i <= pad; i++)
                    {
                        os.write(0x00);
                    }

                    rowCount = 1;
                    rowIndex = lastRowIndex - biWidth;
                    lastRowIndex = rowIndex;
                }
                else
                {
                    rowCount++;
                }

                rowIndex++;
            }

            //--- Update the size of the file
            bfSize += (padCount - pad);
            biSizeImage += (padCount - pad);
        }
        catch (Exception wb)
        {
            wb.printStackTrace();
        }
    }

    /*
     * writeBitmapFileHeader writes the bitmap file header to the file.
     *
     */
    private void writeBitmapFileHeader()
    {
        try
        {
            os.write(bfType);
            os.write(intToDWord(bfSize));
            os.write(intToWord(bfReserved1));
            os.write(intToWord(bfReserved2));
            os.write(intToDWord(bfOffBits));
        }
        catch (Exception wbfh)
        {
            wbfh.printStackTrace();
        }
    }

    /*
     *
     * writeBitmapInfoHeader writes the bitmap information header
     * to the file.
     *
     */
    private void writeBitmapInfoHeader()
    {
        try
        {
            os.write(intToDWord(biSize));
            os.write(intToDWord(biWidth));
            os.write(intToDWord(biHeight));
            os.write(intToWord(biPlanes));
            os.write(intToWord(biBitCount));
            os.write(intToDWord(biCompression));
            os.write(intToDWord(biSizeImage));
            os.write(intToDWord(biXPelsPerMeter));
            os.write(intToDWord(biYPelsPerMeter));
            os.write(intToDWord(biClrUsed));
            os.write(intToDWord(biClrImportant));
        }
        catch (Exception wbih)
        {
            wbih.printStackTrace();
        }
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
