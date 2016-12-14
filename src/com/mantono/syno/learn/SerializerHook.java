package com.mantono.syno.learn;

import java.util.HashSet;
import java.util.Set;

import com.mantono.syno.retrieve.github.Serializer;

public class SerializerHook implements Runnable
{
	private final Set<Serializer> serializers = new HashSet<Serializer>();
	
	public SerializerHook(Serializer... s)
	{
		add(s);
	}

	@Override
	public void run()
	{
		for(Serializer s : serializers)
			s.save();
	}
	
	public boolean add(Serializer s)
	{
		return serializers.add(s);
	}
	
	public boolean add(Serializer... s)
	{
		final int sizeBefore = serializers.size(); 
		for(Serializer ser : s)
			add(ser);
		
		return sizeBefore != serializers.size();
	}
}
