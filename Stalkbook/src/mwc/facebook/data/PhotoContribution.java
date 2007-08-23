/**
 * 
 */
package mwc.facebook.data;

import java.awt.Image;
import java.sql.Time;
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
	public String contributedBy;
	//public Location contributedWhere;
	
	public PhotoContribution(byte[] image, String description, Date contributedWhen, String contributedBy)
	{
		this.image = image;
		this.description = description;
		this.contributedWhen = contributedWhen;
		this.contributedBy = contributedBy;
		//this.contributedWhere = contributedWhere;
	}
}
