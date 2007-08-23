/**
 * 
 */
package mwc.facebook.data;

import java.util.Date;

/**
 * @author ramsayneil
 *
 */
public class CommentContribution
{
	public String comment;
	public Date contributedWhen;
	public User contributedBy;
	public Location contributedWhere;
	
	public CommentContribution(String comment, Date contributedWhen, User contributedBy, Location contributedWhere)
	{
		this.comment = comment;
		this.contributedWhen = contributedWhen;
		this.contributedBy = contributedBy;
		this.contributedWhere = contributedWhere;
	}
}
