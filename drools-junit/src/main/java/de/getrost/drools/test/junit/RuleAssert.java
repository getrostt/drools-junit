package de.getrost.drools.test.junit;

import java.util.HashMap;
import java.util.Map;

import junit.framework.AssertionFailedError;

public class RuleAssert {
	static Map<String, Integer> rulesFired = new HashMap<String, Integer>();

	public static void assertRuleFired(String ruleName) {
		if (!rulesFired.containsKey(ruleName)) {
			throw new AssertionFailedError(String.format(
					"The rule %s did never fire.", ruleName));
		}
	}
}
