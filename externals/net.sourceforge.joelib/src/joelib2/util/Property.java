/*
 * Created on Jan 14, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package joelib2.util;

/**
 * @.author wegnerj
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface Property
{
    //~ Methods ////////////////////////////////////////////////////////////////

    public boolean equals(Object obj);

    public Object getDefaultProperty();

    /**
     * @return Returns the description.
     */
    public String getDescription();

    /**
     * @return Returns the propName.
     */
    public String getPropName();

    /**
     * @return Returns the representation.
     */
    public String getRepresentation();

    public int hashCode();

    public boolean isOptional();

    public Object setDefaultProperty(Object defaultObj);

    /**
     * @param description The description to set.
     */
    public void setDescription(String description);

    /**
     * @param optional The optional to set.
     */
    public void setOptional(boolean optional);

    /**
     * @param propName The propName to set.
     */
    public void setPropName(String propName);

    /**
     * @param representation The representation to set.
     */
    public void setRepresentation(String representation);
}
