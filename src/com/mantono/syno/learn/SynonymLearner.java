package com.mantono.syno.learn;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.mantono.syno.Document;
import com.mantono.syno.Word;

public class SynonymLearner implements Serializable, Runnable
{
	private final Map<String, Word> knowledge;
	private final Set<Document> addedDocuments;
	private final BlockingQueue<Document> inbound = new ArrayBlockingQueue<Document>(1000);
	
	public SynonymLearner()
	{
		this(new HashMap<String, Word>(1_000_000), new HashSet<Document>(100_000));
	}
	
	public SynonymLearner(Map<String, Word> knowledge, Set<Document> addedDocuments)
	{
		this.knowledge = knowledge;
		this.addedDocuments = addedDocuments;
	}

	@Override
	public void run()
	{
		while(true)
		{
				try
				{
					final Document container = inbound.take();
					if(!addedDocuments.contains(container))
					{
						process(container);
						addedDocuments.add(container);
					}
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
		}
	}
	
	public boolean add(Document doc) throws InterruptedException
	{
		return inbound.offer(doc, 10, TimeUnit.SECONDS);
	}

	private void process(Document doc)
	{
		for(String sentence : doc.getSentences())
		{
			sentence = sentence.replaceAll("[!?,]", " ");
			sentence = sentence.toLowerCase();
			final String[] words = sentence.split("\\s+");
			for(String word : words)
			{
				if(!knowledge.containsKey(word))
					knowledge.put(word, new Word(word));
				
				final Word wordKnowledge = knowledge.get(word);
				wordKnowledge.add(sentence);
			}
		}
	}
	
	public Map<String, Word> getKnowledge()
	{
		return knowledge;
	}
	
	public Set<Document> getAddedDocuments()
	{
		return addedDocuments;
	}
	
	public double getWeight(final String word)
	{
		final double occurences = knowledge.get(word).getCount();
		final double inverseFrequency = addedDocuments.size() / occurences;
		return Math.log1p(inverseFrequency);
	}
}
