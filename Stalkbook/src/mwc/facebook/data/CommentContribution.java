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
	public String contributedBy;
	//public Location contributedWhere;
	
	public CommentContribution(String comment, Date contributedWhen, String contributedBy)
	{
		this.comment = comment;
		this.contributedWhen = contributedWhen;
		this.contributedBy = contributedBy;
		//this.contributedWhere = contributedWhere;
	}
}
