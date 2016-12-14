package com.mantono.syno.retrieve.github;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapSerializer<K,V> implements Serializer
{
	private final File file;
	private final FileSaver saver;
	private Map<K,V> ramData, diskData;

	public MapSerializer(final File file)
	{
		this.file = file;
		this.saver = new FileSaver(file);		
	}

	@Override
	public void save()
	{
			if(diskData != null && ramData.size() == diskData.size())
				return;
			saver.save(ramData);
	}

	public Map<K,V> load()
	{
		ramData = new HashMap<K,V>();

		if(file.exists())
		{
			try(FileInputStream fis = new FileInputStream(file);
					ObjectInputStream ois = new ObjectInputStream(fis);)
			{
				ramData = (Map<K,V>) ois.readObject();
				diskData = new HashMap<K,V>(ramData);
			}
			catch(IOException | ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}

		return ramData;
	}
}
