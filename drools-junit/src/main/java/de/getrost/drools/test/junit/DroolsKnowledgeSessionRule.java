package de.getrost.drools.test.junit;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.drools.KnowledgeBase;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class DroolsKnowledgeSessionRule implements TestRule {
	private final Object test;
	private final KnowledgeBase kbase;

	public DroolsKnowledgeSessionRule(KnowledgeBase kbase, Object test) {
		this.kbase = kbase;
		this.test = test;
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				// build knowledge session
				StatefulKnowledgeSession ksession = kbase
						.newStatefulKnowledgeSession();

				// register listener for rule assertions
				ksession.addEventListener(new DefaultAgendaEventListener() {
					public void afterActivationFired(
							AfterActivationFiredEvent event) {
						super.afterActivationFired(event);
						String ruleName = event.getActivation().getRule().getName();
						if (RuleAssert.rulesFired.containsKey(ruleName)) {
							Integer count = RuleAssert.rulesFired.get(ruleName);
							RuleAssert.rulesFired.put(ruleName, count + 1);
						} else {
							RuleAssert.rulesFired.put(ruleName, new Integer(1));
						}
					}
				});

				// inject knowledge base
				injectKnowledgeSession(ksession);

				// go on
				base.evaluate();
				
				//cleanup
				RuleAssert.rulesFired.clear();

				// close knowledge session
				ksession.dispose();
			}

			private void injectKnowledgeSession(
					StatefulKnowledgeSession ksession)
					throws IllegalAccessException {
				for (Field f : test.getClass().getDeclaredFields()) {
					if (StatefulKnowledgeSession.class.equals(f.getType())) {
						FieldUtils.writeField(f, test, ksession, true);
					}
				}
			}
		};
	}

}
