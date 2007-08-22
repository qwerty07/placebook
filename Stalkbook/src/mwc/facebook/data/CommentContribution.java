/**
 * 
 */
package mwc.facebook.data;

import java.sql.Time;

/**
 * @author ramsayneil
 *
 */
public class CommentContribution
{
	public String comment;
	public Time contributedWhen;
	public String contributedBy;
	
	public CommentContribution(String comment, Time contributedWhen, String contributedBy)
	{
		this.comment = comment;
		this.contributedWhen = contributedWhen;
		this.contributedBy = contributedBy;
	}
}
