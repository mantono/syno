package com.mantono.syno;

public interface Document
{
	public final String SENTENCE_SEPRATOR = "([!?.]+\\s+)|(\\n+)";
	
	String getContent();
	default String[] getSentences()
	{
		return getContent().split(SENTENCE_SEPRATOR);
	}
}
