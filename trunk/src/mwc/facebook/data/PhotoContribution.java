/**
 * 
 */
package mwc.facebook.data;

import java.util.Date;

import mwc.facebook.JSONable;

/**
 * @author ramsayneil
 *
 */
public class PhotoContribution implements JSONable
{
	public byte[] image;
	public int photoId;
	public String description;
	public Date contributedWhen;
	public User contributedBy;
	public Location contributedWhere;
	
	public PhotoContribution(byte[] image, int photoId, String description, Date contributedWhen, User contributedBy, Location contributedWhere)
	{
		this.image = image;
		this.description = description;
		this.contributedWhen = contributedWhen;
		this.contributedBy = contributedBy;
		this.contributedWhere = contributedWhere;
	}

	public String toJSON() {
		StringBuffer sb = new StringBuffer();
		sb.append("{ ");
		sb.append("id: " + photoId +",");
		sb.append("user: " + contributedBy.toJSON() + ", ");
		sb.append("location: \"" + contributedWhere.getLocationName() + "\", ");
		sb.append("date: \"" + contributedWhen.toString() + "\", ");
		sb.append("userid: \"" + contributedBy.getUser() + "\", ");
		sb.append("description: \"" + Location.escapeString(description)+"\", ");		
		sb.append("}");
		return sb.toString();
	}
}
