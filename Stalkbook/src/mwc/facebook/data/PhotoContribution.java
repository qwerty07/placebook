/**
 * 
 */
package mwc.facebook.data;

import java.util.Date;

/**
 * @author ramsayneil
 *
 */
public class PhotoContribution
{
	public byte[] image;
	public String description;
	public Date contributedWhen;
	public User contributedBy;
	public Location contributedWhere;
	
	public PhotoContribution(byte[] image, String description, Date contributedWhen, User contributedBy, Location contributedWhere)
	{
		this.image = image;
		this.description = description;
		this.contributedWhen = contributedWhen;
		this.contributedBy = contributedBy;
		this.contributedWhere = contributedWhere;
	}
}
