package com.mantono.syno.retrieve.github;

import com.mantono.syno.Document;

public class Issue implements Document
{
	private final int repoId, issueId;
	private final String content;
	
	public Issue(final int repo, final int issue, final String content)
	{
		this.repoId = repo;
		this.issueId = issue;
		this.content = content;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == null)
			return false;
		if(this.getClass() != obj.getClass())
			return false;
		
		final Issue other = (Issue) obj; 
		return this.repoId == other.repoId && this.issueId == other.issueId;
	}
	
	@Override
	public int hashCode()
	{
		return repoId + (issueId*17);
	}
	
	@Override
	public String getContent()
	{
		return content;
	}
}
