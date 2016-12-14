package com.mantono.syno.retrieve.github;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.JsonNode;
import com.mantono.ghapic.Client;
import com.mantono.ghapic.Repository;
import com.mantono.ghapic.Resource;
import com.mantono.ghapic.Response;
import com.mantono.ghapic.Verb;
import com.mantono.syno.learn.SynonymLearner;

public class Retriever implements Runnable
{
	private final SynonymLearner learner;
	private final Client client = new Client();
	private final Set<Repository> reposToLoad;
	private final BlockingQueue<Repository> repoQueue;
	private final Map<Repository, SetSerializer<Issue>> serializers;

	public Retriever(SynonymLearner learner, Set<Repository> repos)
	{
		this.learner = learner;
		this.reposToLoad = repos;
		this.repoQueue = new ArrayBlockingQueue<Repository>(100);
		this.serializers = new HashMap<Repository, SetSerializer<Issue>>(repos.size());
		reload();
	}

	public boolean add(Repository repo)
	{
		return repoQueue.offer(repo);
	}

	public void reload()
	{
		repoQueue.addAll(reposToLoad);
	}

	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				Repository repo = repoQueue.take();
				Set<Issue> issues = load(repo);
				download(repo, issues);
				forwardToLearner(issues);
				save(repo, issues);
			}
			catch(InterruptedException | IOException | ExecutionException e)
			{
				e.printStackTrace();
			}
		}
	}

	private Set<Issue> load(Repository repo)
	{
		SetSerializer<Issue> serializer;
		if(!serializers.containsKey(repo))
		{
			final File file = new File("repos/" + repo.getOwner() + "/" + repo.getName());
			serializer = new SetSerializer<Issue>(file);
			serializers.put(repo, serializer);
		}
		else
		{
			serializer = serializers.get(repo);
		}
		
		final Set<Issue> issuesForRepo = serializer.load();
		return issuesForRepo;
	}

	private Set<Issue> download(Repository repo, Set<Issue> issues) throws MalformedURLException, IOException, InterruptedException, ExecutionException
	{
		final int repoId = getRepoId(repo);
		
		Future<Response> response = client.submitRequest("repos/" + repo.getOwner() + "/" + repo.getName()
				+ "/issues?q=sort=created&direction=desc&state=all");
		final int issuesOnGithub = getIssueCount(response);

		int page = 1 + (issues.size() / 100);
		int previousSize = -1;
		
		while(previousSize < issues.size() )
		{
			previousSize = issues.size();
			Resource issueRequest = new Resource(Verb.GET, "repos/" + repo.getOwner() + "/" + repo.getName()
					+ "/issues?q=sort=created&direction=asc&state=all&page=" + page + "&per_page=100");
			Future<Response> issueFuture = client.submitRequest(issueRequest);
			Set<Issue> pageIssues = parseIssues(issueFuture, repoId);
			issues.addAll(pageIssues);
			page++;

			System.out.println(issues.size() + " of approximately " + issuesOnGithub);
		}
		
		return issues;
	}
	
	private int getRepoId(Repository repo) throws IOException, InterruptedException, ExecutionException
	{
		Resource idRequest = new Resource(Verb.GET, "repos/" + repo.getOwner() + "/" + repo.getName());
		Future<Response> idFuture = client.submitRequest(idRequest);
		while(!idFuture.isDone())
			Thread.yield();
		
		final Response response = idFuture.get();
		final int id = response.getBody().get("id").asInt();
		return id;
	}

	private static int getIssueCount(Future<Response> future) throws InterruptedException, ExecutionException
	{
		while(!future.isDone())
			Thread.yield();
		Response response = future.get();
		final JsonNode node = response.getBody();
		final JsonNode lastIssue = node.get(0);
		return lastIssue.get("number").asInt();
	}
	
	private static Set<Issue> parseIssues(Future<Response> future, final int repoId) throws InterruptedException, ExecutionException
	{
		while(!future.isDone())
			Thread.yield();
		Response response = future.get();
		final JsonNode node = response.getBody();
		Set<Issue> issues = new HashSet<Issue>(100);
		for(int i = 0; i < 100; i++)
		{
			if(!node.has(i))
				break;

			final JsonNode jsonIssue = node.get(i);
			final int number = jsonIssue.get("number").asInt();
			final String body = jsonIssue.get("body_text").asText();

			final Issue issue = new Issue(repoId, number, body);
			issues.add(issue);
		}

		return issues;
	}

	private void forwardToLearner(Set<Issue> issues) throws InterruptedException
	{
		for(Issue issue : issues)
			learner.add(issue);
	}

	private void save(Repository repo, Set<Issue> issues)
	{
		Serializer s = serializers.get(repo);
		s.save();
	}
}
