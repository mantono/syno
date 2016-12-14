package com.mantono.syno.retrieve.github;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;

import com.mantono.ghapic.Repository;

public class SetSerializer<T> implements Serializer
{
	private final File file;
	private final FileSaver saver;
	private Set<T> ramData, diskData;

	public SetSerializer(final File file)
	{
		this.file = file;
		this.saver = new FileSaver(file);		
	}

	@Override
	public void save()
	{
			if(diskData != null && ramData.size() == diskData.size())
				return;
			if(ramData == null)
				throw new IllegalStateException("Trying to save an object before it is loaded.");
			saver.save(ramData);
	}

	public Set<T> load()
	{
		ramData = new HashSet<T>();

		if(file.exists())
		{
			try(FileInputStream fis = new FileInputStream(file);
					ObjectInputStream ois = new ObjectInputStream(fis);)
			{
				ramData = (Set<T>) ois.readObject();
				diskData = new HashSet<T>(ramData);
			}
			catch(IOException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}

		return ramData;
	}
}
