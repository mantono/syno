package com.mantono.syno;

import java.util.HashMap;
import java.util.Map;

public class Word
{
	private final static int MAX_DISTANCE = 3;
	
	private final String word;
	private final Map<String, Double> weights;

	public Word(final String word)
	{
		this.word = word.toLowerCase();
		this.weights = new HashMap<String, Double>();
	}

	public void add(String context)
	{
		context = context.toLowerCase();
		if(!context.contains(word))
			throw new IllegalArgumentException("Missing the word " + word + " in context.");
		String[] words = context.split("\\s+");
		final int wordIndex = findIndex(words);

		for(int distance = 1; distance < MAX_DISTANCE; distance++)
		{
			final double weight = Math.pow(2, 1 - distance);
			final int left = wordIndex - distance;
			final int right = wordIndex + distance;
			
			if(left >= 0)
				addWeight(words[left], weight);
			
			if(right < words.length)
				addWeight(words[right], weight);
		}
	}

	private void addWeight(String string, double additionalWeight)
	{
		double currentWeight = 0;
		if(weights.containsKey(string))
			currentWeight = weights.get(string);
		weights.put(string, currentWeight+additionalWeight);	
	}

	private int findIndex(String[] words)
	{
		for(int i = 0; i < words.length; i++)
			if(words[i].equals(word))
				return i;
		return -1;
	}
	
	@Override
	public String toString()
	{
		return word.toString() + " " + weights.toString();
	}

	@Override
	public boolean equals(Object obj)
	{
		return word.equals(obj);
	}

	@Override
	public int hashCode()
	{
		return word.hashCode();
	}
}
