package com.mantono.syno.find;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mantono.syno.Word;

import graphProject.Graph;
import graphProject.GraphExplorer;
import graphProject.concurrent.ConcurrentGraph;
import graphProject.concurrent.ConcurrentPathFinder;

public class SynonymFinder
{
	private ConcurrentHashMap<String, Word> knowledge;
	private final Graph<String> graph = new ConcurrentGraph<String>();
	private final GraphExplorer<String> finder = new ConcurrentPathFinder<String>(graph);
	
	public SynonymFinder(final Map<String, Word> knowledge)
	{
		this.knowledge = new ConcurrentHashMap<String, Word>(knowledge);
	}
}
