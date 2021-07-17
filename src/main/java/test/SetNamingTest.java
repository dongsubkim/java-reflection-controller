package test;

import org.junit.Test;

public class SetNamingTest {

	@Test
	public void testKeyToMethodKey() {
		String key = "username";
		
		String firstKey = "set";
		String upperKey = key.substring(0, 1).toUpperCase();
		String remainKey = key.substring(1);
		
		String res = firstKey + upperKey + remainKey;
		System.out.println(res);
	}
}
