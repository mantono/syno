package com.mantono.syno;

import java.io.Serializable;

public interface Document extends Serializable
{
	public final String SENTENCE_SEPRATOR = "([!?.]+\\s+)|(\\n+)";
	
	String getContent();
	default String[] getSentences()
	{
		return getContent().split(SENTENCE_SEPRATOR);
	}
}
