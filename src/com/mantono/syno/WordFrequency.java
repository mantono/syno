package com.mantono.syno;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordFrequency implements Serializable
{
	private final static long serialVersionUID = 0L;
	private int entries = 0;
	private final Map<String, Integer> frequency = new HashMap<String, Integer>();

	public int getFrequency(final String w)
	{
		if(!frequency.containsKey(w))
			return 0;

		return frequency.get(w);
	}

	public double getWeight(final String w)
	{
		final double inverseFrequency = getInverseFrequency(w);
		return 1 + Math.log(inverseFrequency);
	}

	public double getInverseFrequency(final String w)
	{
		return entries / (double) getFrequency(w);
	}

	public Set<String> getElements()
	{
		return frequency.keySet();
	}

	public boolean add(String word)
	{
		if(word == null)
			return false;
		
		if(!frequency.containsKey(word))
		{
			frequency.put(word, 1);
		}
		else
		{
			final int itemFrequency = frequency.get(word) + 1;
			frequency.put(word, itemFrequency);
		}
		entries++;
		return true;
	}
}
