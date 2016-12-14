package com.mantono.syno.learn;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mantono.ghapic.Repository;
import com.mantono.syno.Document;
import com.mantono.syno.Word;
import com.mantono.syno.retrieve.github.MapSerializer;
import com.mantono.syno.retrieve.github.Retriever;
import com.mantono.syno.retrieve.github.SetSerializer;

public class Train
{

	public static void main(String[] args)
	{
		Set<Repository> repos = new HashSet<Repository>();
		repos.add(new Repository("mantono", "BachelorThesis"));
		repos.add(new Repository("mantono", "DuplicateSearcher"));
		
		MapSerializer<String, Word> ms = new MapSerializer<String, Word>(new File("knowledge"));
		SetSerializer<Document> ss = new SetSerializer<Document>(new File("addedDocumentIds"));
		final Map<String, Word> knowledge = ms.load();
		final Set<Document> addedDocuments = ss.load();
		SynonymLearner learner = new SynonymLearner(knowledge, addedDocuments);
		
		Retriever retriever = new Retriever(learner, repos);
		Thread rt = new Thread(retriever);
		Thread lt = new Thread(learner);
		rt.start();
		lt.start();
		
		final Runtime runtime = Runtime.getRuntime();
		SerializerHook serHook = new SerializerHook(ms, ss);
		Thread hook = new Thread(serHook);
		runtime.addShutdownHook(hook);
	}

}
