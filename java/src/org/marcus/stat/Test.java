package org.marcus.stat;

import java.util.Calendar;
import java.util.Random;

public class Test {
	
	public static void main(String[] args){
		long seed = Calendar.getInstance().getTimeInMillis() % 234185981;
		Random rand = new Random(seed);
		long pos = 0;
		for (int i = 0; i < 100; i++) {
			double nextDouble = rand.nextDouble();
			double err = .2 * (nextDouble - 0.5);
			double r = rand.nextDouble()
//					+ err
					;
			System.out.println(nextDouble-0.5);
			// System.out.println(r);
			if (r <= .5)
				pos++;
		}
		System.out.println((double)pos/100.);
	}

}
