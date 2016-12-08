package com.mantono.syno;

import java.util.HashMap;
import java.util.Map;

public class Word
{
	private final String word;
	private final Map<String, Double> weights;
	
	public Word(final String word)
	{
		this.word = word.toLowerCase();
		this.weights = new HashMap<String, Double>();
	}
	
	public void add(final String context)
	{
		if(!context.contains(word))
			throw new IllegalArgumentException("Missing the word " + word + " in context.");
		String[] words = context.split("\\s+");
		final int wordIndex = findIndex(words);
		
		int index = wordIndex-2;
		if(index >= 0)
			weights.put(words[index], 0.5);
		if(++index >= 0)
			weights.put(words[index], 1.0);
		index++;
		if(++index < words.length)
			weights.put(words[index], 1.0);
		if(++index < words.length)
			weights.put(words[index], 0.5);
	}

	private int findIndex(String[] words)
	{
		for(int i = 0; i < words.length; i++)
			if(words[i].equals(word))
				return i;
		return -1;
	}
}
