package de.getrost.drools.test.junit.example;

import static de.getrost.drools.test.junit.RuleAssert.assertRuleFired;

import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import de.getrost.drools.test.junit.DroolsKnowledgeBaseRule;
import de.getrost.drools.test.junit.DroolsKnowledgeSessionRule;

public class SimpleRuleTest {
	/**
	 * creates and injects the {@link KnowledgeBase} into this class. Replaces
	 * {@link BeforeClass} for the initialization.
	 */
	@ClassRule
	public static DroolsKnowledgeBaseRule kbaseRule = new DroolsKnowledgeBaseRule("redenglishbus.drl");

	/**
	 * rule that creates a new {@link StatefulKnowledgeSession} for each test
	 * and disposes it afterwards
	 */
	@Rule
	public DroolsKnowledgeSessionRule ksessionRule = new DroolsKnowledgeSessionRule(
			kbaseRule.getKnowledgeBase(), this);

	/**
	 * state full knowledge session to be created and injected before each test
	 * by {@link DroolsKnowledgeSessionRule}
	 */
	private StatefulKnowledgeSession ksession;

	@Test
	public void testRule() {
		// insert facts
		ksession.insert(new Bus("50", "GB", "red"));
		ksession.insert(new Bus("51", "GB", "red"));

		// fire rules
		ksession.fireAllRules();

		// check results
		assertRuleFired("English Busses are red");
	}
}
