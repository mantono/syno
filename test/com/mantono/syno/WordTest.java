package com.mantono.syno;

import static org.junit.Assert.*;

import org.junit.Test;

public class WordTest
{

	@Test
	public void test()
	{
		final Word w = new Word("sun");
		w.add("the sun is shining");
		w.add("I miss the sun");
		System.out.println(w);
	}

}
