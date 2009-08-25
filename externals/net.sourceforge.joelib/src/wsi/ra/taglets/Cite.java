///////////////////////////////////////////////////////////////////////////////
//  Filename: $RCSfile: Cite.java,v $
//  Purpose:  Atom representation.
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
package wsi.ra.taglets;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.sun.javadoc.Tag;


/**
 * A Taglet that defines the <code>@cite</code> tag for Javadoc
 * comments.
 *
 * -J-Dwsi.ra.taglets.Cite.path=D:/workingAt/joelib/src/<br>
 * -J-Dwsi.ra.taglets.Cite.file=literature.html<br>
 * or under Ant:<br>
 * additionalparam=" -J-Dwsi.ra.taglets.Cite.path=D:/workingAt/joelib/src/ -J-Dwsi.ra.taglets.Cite.file=literature.html "<br>
 *
 * @author Joerg Kurt Wegner
 */
public class Cite extends ListTag
{
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final Map<String, String> bibtex = new HashMap<String, String>(
            20);

    static
    {
        bibtex.put("bk02",
            "<b><i>Article</i><a name=\"bk02\"> (bk02)</a></b>, M. B&ouml;hm & G. Klebe, Development of New Hydrogen--Bond Descriptors and Their Application to Comparative Molecular Field Analyses , <em>J. Med. Chem., </em> <b>2002</b> <i>, 45</i> , 1585-1597.");
        bibtex.put("bmv84",
            "<b><i>Article</i><a name=\"bmv84\"> (bmv84)</a></b>, P. Broto; G. Moreau & C. Vandycke, Molecular Structures: Perception, Autocorrelation Descriptor and SAR Studies , <em>Eur. J. Med. Chem., </em> <b>1984</b> <i>, 19</i> , 66-70.");
        bibtex.put("br90",
            "<b><i>Book</i><a name=\"br90\"> (br90)</a></b>, D. Bonchev & D. H. Rouvray, D. Bonchev & D. H. Rouvray <i>(ed.)</i>, Chemical Graph Theory: Introduction and Fundamentals , <em>Gordon and Breach Science Publishers, </em> <b>1990</b> <i>, 1</i>.");
        bibtex.put("bs93",
            "<b><i>Article</i><a name=\"bs93\"> (bs93)</a></b>, B.L.Bush & R.P.Sheridan, PATTY: A Programmable Atom Typer and Language for Automatic Classification of Atoms in Molecular Databases. , <em>J. Chem. Inf. Comput. Sci., </em> <b>1993</b> <i>, 33</i> , 756-762.");
        bibtex.put("clr98bfs",
            "<b><i>Inbook</i><a name=\"clr98bfs\"> (clr98bfs)</a></b>, T. H. Cormen; C. E. Leiserson & R. L. Rivest, Introduction to Algorithms , 23.2, <em>MIT--Press, </em> <b>1998</b> , 469-477.");
        bibtex.put("clr98complexity",
            "<b><i>Inbook</i><a name=\"clr98complexity\"> (clr98complexity)</a></b>, T. H. Cormen; C. E. Leiserson & R. L. Rivest, Introduction to Algorithms , 2, <em>MIT--Press, </em> <b>1998</b> , 23-41.");
        bibtex.put("clr98dfs",
            "<b><i>Inbook</i><a name=\"clr98dfs\"> (clr98dfs)</a></b>, T. H. Cormen; C. E. Leiserson & R. L. Rivest, Introduction to Algorithms , 23.3, <em>MIT--Press, </em> <b>1998</b> , 477-483.");
        bibtex.put("dhl90",
            "<b><i>Incollection</i><a name=\"dhl90\"> (dhl90)</a></b>, A.N. Davies; H. Hilling & M. Linscheid, J. Gasteiger <i>(ed.)</i>, JCAMP--DX, a standard ? , <em>Springer--Verlag, </em> <b>1990</b> , 147-156.");
        bibtex.put("dl93",
            "<b><i>Article</i><a name=\"dl93\"> (dl93)</a></b>, A. N. Davies & P. Lampen, JCAMP--DX for NMR , <em>Appl. Spec., </em> <b>1993</b> <i>, 47</i> , 1093-1099.");
        bibtex.put("dw88",
            "<b><i>Article</i><a name=\"dw88\"> (dw88)</a></b>, R. S. Mc Donald & Paul A. Wilks, JCAMP--DX: A Standard Form for Exchange of Infrared Spectra in Computer Readable Form , <em>Appl. Spec., </em> <b>1988</b> <i>, 42</i> , 151-162.");
        bibtex.put("ers00",
            "<b><i>Article</i><a name=\"ers00\"> (ers00)</a></b>, P. Ertl; B. Rohde & P. Selzer, Fast Calculation of Molecular Polar Surface Area as a Sum of Fragment-Based Contributions and Its Application to the Prediction of Drug Transport Properties , <em>J. Med. Chem. 2000, 43, 3714-3717, </em> <b>2000</b> <i>, 43</i> , 3714-3717.");
        bibtex.put("fig96",
            "<b><i>Article</i><a name=\"fig96\"> (fig96)</a></b>, J. Figueras, Ring Perception Using Breadth--First Search , <em>J. Chem. Inf. Comput. Sci., </em> <b>1996</b> <i>, 36</i> , 986-991.");
        bibtex.put("ghhjs91",
            "<b><i>Article</i><a name=\"ghhjs91\"> (ghhjs91)</a></b>, J. Gasteiger; B. M. P. Hendriks; P. Hoever; C. Jochum & H. Somberg, JCAMP--CS: A Standard Format for Chemical Structure Information in Computer Readable Form , <em>Appl. Spec., </em> <b>1991</b> <i>, 45</i> , 4-11.");
        bibtex.put("gm78",
            "<b><i>Article</i><a name=\"gm78\"> (gm78)</a></b>, J. Gasteiger & M. Marsili, A New Model for Calculating Atomic Charges in Molecules , <em>Tetrahedron Lett., </em> <b>1978</b> , 3181-3184.");
        bibtex.put("gmrw01",
            "<b><i>Article</i><a name=\"gmrw01\"> (gmrw01)</a></b>, G. V. Gkoutos; P. Murray--Rust; S. Rzepa & M. Wright, Chemical Markup, XML, and the World--Wide Web. 3. Toward a Signed Semantic Chemical Web of Trust , <em>J. Chem. Inf. Comput. Sci., </em> <b>2001</b> <i>, 41</i> , 1295-1300 . <a href=\"http://dx.doi.org/10.1021/ci000406v\">DOI: 10.1021/ci000406v</a>.");
        bibtex.put("gwb98",
            "<b><i>Article</i><a name=\"gwb98\"> (gwb98)</a></b>, J. Gillet; P. Willett & J. Bradshaw, Identification of Biological Activity Profiles Using Substructural Analysis and Genetic Algorithms , <em>J. Chem. Inf. Comput. Sci., </em> <b>1998</b> <i>, 38</i> , 165-179.");
        bibtex.put("gxsb00",
            "<b><i>Article</i><a name=\"gxsb00\"> (gxsb00)</a></b>, L. Xue; F. L. Stahura; J. W. Godden & J. Bajorath, Searching for molecules with similar biological activity: analysis by fingerprint profiling , <em>Pac. Symp. Biocomput., </em> <b>2000</b> <i>, 8</i> , 566-575.");
        bibtex.put("jwd00",
            "<b><i>Misc</i><a name=\"jwd00\"> (jwd00)</a></b>, C. A. James; D. Weininger & J. Delany, Daylight Theory Manual.");
        bibtex.put("lhdl94",
            "<b><i>Article</i><a name=\"lhdl94\"> (lhdl94)</a></b>, P. Lampen; H. Hillig; A. N. Davies & Michael Linscheid, JCAMP--DX for Mass Spectrometry , <em>Appl. Spec., </em> <b>1994</b> <i>, 48</i> , 1545-1552.");
        bibtex.put("lldf01",
            "<b><i>Article</i><a name=\"lldf01\"> (lldf01)</a></b>, C. A. Lipinski; F. Lombardo; B. W. Dominy & P. J. Feeney, Experimental and computational approaches to estimate solubility and permeability in drug discovery and development settings , <em>Adv. Drug Delivery Reviews, </em> <b>2001</b> <i>, 46</i> , 3-26.");
        bibtex.put("mdlMolFormat",
            "<b><i>Misc</i><a name=\"mdlMolFormat\"> (mdlMolFormat)</a></b>, MDL Information Systems, Inc., MDL CTfile Formats.");
        bibtex.put("ml91",
            "<b><i>Article</i><a name=\"ml91\"> (ml91)</a></b>, E. C. Meng & R. A. Lewis, Determination of Molecular Topology and Atomic Hybridisation States from Heavy Atom Coordinates , <em>J. Comp. Chem., </em> <b>1991</b> <i>, 12</i> , 891-898.");
        bibtex.put("mor65",
            "<b><i>Article</i><a name=\"mor65\"> (mor65)</a></b>, H. L. Morgan, The Generation of a Unique Machine Description for Chemical Structures -- A Technique Developed at Chemical Abstracts Service. , <em>J. Chem. Doc., </em> <b>1965</b> <i>, 5</i> , 107-113.");
        bibtex.put("mr01",
            "<b><i>Article</i><a name=\"mr01\"> (mr01)</a></b>, P. Murray--Rust & H. S. Rzepa, Chemical Markup, XML and the World--Wide Web. 2. Information Objects and the CMLDOM , <em>J. Chem. Inf. Comput. Sci., </em> <b>2001</b> <i>, 41</i> , 1113-1123 . <a href=\"http://dx.doi.org/10.1021/ci000404a\">DOI: 10.1021/ci000404a</a>.");
        bibtex.put("mr03",
            "<b><i>Article</i><a name=\"mr03\"> (mr03)</a></b>, P. Murray--Rust & H. S. Rzepa, Chemical Markup, XML and the World--Wide Web. 4. CML Schema , <em>J. Chem. Inf. Comput. Sci., </em> <b>2003</b> <i>, 43</i> , 757-772 . <a href=\"http://dx.doi.org/10.1021/ci0256541\">DOI: 10.1021/ci0256541</a>.");
        bibtex.put("mrww04",
            "<b><i>Article</i><a name=\"mrww04\"> (mrww04)</a></b>, P. Murray--Rust; H. S. Rzepa; J. Williamson & E. L. Willighagen, Chemical Markup, XML and the World--Wide Web. 5. Applications of Chemical Metadata in RSS Aggregators , <em>J. Chem. Inf. Comput. Sci., </em> <b>2004</b> <i>, 44</i> , 462-469 . <a href=\"http://dx.doi.org/10.1021/ci034244p\">DOI: 10.1021/ci034244p</a>.");
        bibtex.put("odtl01",
            "<b><i>Article</i><a name=\"odtl01\"> (odtl01)</a></b>, T. I. Oprea; A. M. Davis; S. J. Teague & P. D. Leeson, Is There a Difference between Leads and Drugs? A Historical Perspective , <em>J. Chem. Inf. Comput. Sci., </em> <b>2001</b> <i>, 41</i> , 1308-1315 . <a href=\"http://dx.doi.org/10.1021/ci010366a\">DOI: 10.1021/ci010366a</a>.");
        bibtex.put("pdbFormat",
            "<b><i>Misc</i><a name=\"pdbFormat\"> (pdbFormat)</a></b>, Protein Data Bank (PDB) File Format , <b>2002</b>.");
        bibtex.put("povray",
            "<b><i>Misc</i><a name=\"povray\"> (povray)</a></b>, POV--Team, Persistence of Vision Raytracer (POVRay).");
        bibtex.put("rr99b",
            "<b><i>Article</i><a name=\"rr99b\"> (rr99b)</a></b>, P. Murray--Rust & H. S. Rzepa, Chemical Markup, XML, and the Worldwide Web. 1. Basic Principles , <em>J. Chem. Inf. Comput. Sci., </em> <b>1999</b> <i>, 39</i> , 928-942 . <a href=\"http://dx.doi.org/10.1021/ci990052b\">DOI: 10.1021/ci990052b</a>.");
        bibtex.put("smarts",
            "<b><i>Manual</i><a name=\"smarts\"> (smarts)</a></b>, Daylight Chemical Information Systems, Inc., Smiles ARbitrary Target Specification (SMARTS).");
        bibtex.put("smilesFormat",
            "<b><i>Manual</i><a name=\"smilesFormat\"> (smilesFormat)</a></b>, Daylight Chemical Information Systems, Inc., Simplified Molecular Input Line Entry System (SMILES).");
        bibtex.put("sybylmol2",
            "<b><i>Manual</i><a name=\"sybylmol2\"> (sybylmol2)</a></b>, Tripos, Tripos Mol2 File Format.");
        bibtex.put("tc00kiershape",
            "<b><i>Inbook</i><a name=\"tc00kiershape\"> (tc00kiershape)</a></b>, R. Todeschini & V. Consonni, Handbook of Molecular Descriptors , K, <em>Wiley--VCH, </em> <b>2000</b> , 248-250.");
        bibtex.put("tc00zagrebgroup",
            "<b><i>Inbook</i><a name=\"tc00zagrebgroup\"> (tc00zagrebgroup)</a></b>, R. Todeschini & V. Consonni, Handbook of Molecular Descriptors , Z, <em>Wiley--VCH, </em> <b>2000</b> , 509.");
        bibtex.put("tri92",
            "<b><i>Book</i><a name=\"tri92\"> (tri92)</a></b>, N. Trinajsti, Chemical Graph Theory , <em>CRC Press, Florida, U.S.A., </em> <b>1992</b>.");
        bibtex.put("wc99",
            "<b><i>Article</i><a name=\"wc99\"> (wc99)</a></b>, S. A. Wildman & G. M. Crippen, Prediction of Physicochemical Parameters by Atomic Contributions , <em>J. Chem. Inf. Comput. Sci., </em> <b>1999</b> <i>, 39</i> , 868-873.");
        bibtex.put("wei88",
            "<b><i>Article</i><a name=\"wei88\"> (wei88)</a></b>, D. Weininger, SMILES, a Chemical Language for Information Systems. 1. Introduction to Methodology and Encoding Rules. , <em>J. Chem. Inf. Comput. Sci., </em> <b>1988</b> <i>, 28</i> , 31-36.");
        bibtex.put("wfz04a",
            "<b><i>Article</i><a name=\"wfz04a\"> (wfz04a)</a></b>, Wegner, J. K.; Fr&ouml;hlich, H. & Zell, A., Feature selection for Descriptor based Classification Models. 1. Theory and GA--SEC Algorithm , <em>J. Chem. Inf. Comput. Sci., </em> <b>2004</b> <i>, 44</i> . <a href=\"http://dx.doi.org/10.1021/ci0342324\">DOI: 10.1021/ci0342324</a>.");
        bibtex.put("wfz04b",
            "<b><i>Article</i><a name=\"wfz04b\"> (wfz04b)</a></b>, J. K. Wegner; H. Fr&ouml;hlich & A. Zell, Feature selection for Descriptor based Classification Models. 2. Human Intestinal Absorption (HIA) , <em>J. Chem. Inf. Comput. Sci., </em> <b>2004</b> <i>, 44</i> . <a href=\"http://dx.doi.org/10.1021/ci034233w\">DOI: 10.1021/ci034233w</a>.");
        bibtex.put("wil01",
            "<b><i>Article</i><a name=\"wil01\"> (wil01)</a></b>, E. L. Willighagen, Processing CML Conventions in Java , <em>Internet Journal of Chemistry, </em> <b>2001</b> <i>, 4</i> , 4.");
        bibtex.put("www89",
            "<b><i>Article</i><a name=\"www89\"> (www89)</a></b>, D. Weininger; A. Weininger & J. L. Weininger, Algorithm for generation of unique SMILES notation , <em>J. Chem. Inf. Comput. Sci., </em> <b>1989</b> <i>, 29</i> , 97-101.");
        bibtex.put("wy96",
            "<b><i>Article</i><a name=\"wy96\"> (wy96)</a></b>, W. P. Walters & S. H. Yalkowsky, ESCHER--A Computer Program for the Determination of External Rotational Symmetry Numbers from Molecular Topology , <em>J. Chem. Inf. Comput. Sci., </em> <b>1996</b> <i>, 36</i> , 1015-1017.");
        bibtex.put("wz03",
            "<b><i>Article</i><a name=\"wz03\"> (wz03)</a></b>, Wegner, J. K. & Zell, A., Prediction of Aqueous Solubility and Partition Coefficient Optimized by a Genetic Algorithm Based Descriptor Selection Method , <em>J. Chem. Inf. Comput. Sci., </em> <b>2003</b> <i>, 43</i> , 1077-1084 . <a href=\"http://dx.doi.org/10.1021/ci034006u\">DOI: 10.1021/ci034006u</a>.");
        bibtex.put("zup89c",
            "<b><i>Inbook</i><a name=\"zup89c\"> (zup89c)</a></b>, J. Zupan, Algorithms for Chemists , <em>Wiley, </em> <b>1989</b> , 102-142 .");
    }

    //~ Constructors ///////////////////////////////////////////////////////////

    /**
     * Create a new ListTag, with tag name 'todo'.  Default
     * the tag header to 'To Do:' and default to an
     * unordered list.
     *
     * @todo a single todo entry
     */
    public Cite()
    {
        super(".cite", "References:", ListTag.ORDERED_LIST);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public static void register(Map tagletMap)
    {
        ListTag.register(tagletMap, new Cite());
    }

    public String toString(Tag tag)
    {
        String html = (String) bibtex.get(tag.text());

        if (html == null)
        {
            return tag.text();
        }
        else
        {
            return html;
        }
    }

    public String toString(Tag[] tags)
    {
        if (tags.length == 0)
        {
            return "";
        }

        StringBuffer sbuf = new StringBuffer(200 + (800 * tags.length));

        startingTags();

        emitHeader(sbuf, true);

        for (int i = 0; i < tags.length; i++)
        {
            emitTag(toString(tags[i]), sbuf, true);
        }

        emitFooter(sbuf, true);

        endingTags(sbuf);

        return sbuf.toString();
    }
}

///////////////////////////////////////////////////////////////////////////////
//  END OF FILE.
///////////////////////////////////////////////////////////////////////////////
