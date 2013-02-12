package de.getrost.drools.test.junit;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class DroolsKnowledgeBaseRule implements TestRule {

	private final String[] ruleFiles;
	
	private KnowledgeBase knowledgeBase;

	public DroolsKnowledgeBaseRule(String... ruleFiles) {
		this.ruleFiles = ruleFiles;
	}

	@Override
	public Statement apply(final Statement base, Description desc) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				// build knowledge base
				knowledgeBase = buildKnowledgeBase();

				// go on
				base.evaluate();
			}

			private KnowledgeBase buildKnowledgeBase() throws Exception {
				KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
						.newKnowledgeBuilder();
				for (String ruleFile : ruleFiles) {
					kbuilder.add(
							ResourceFactory.newClassPathResource(ruleFile),
							ResourceType.DRL);
					hasErrors(kbuilder);
				}
				KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
				kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

				return kbase;
			}

			private void hasErrors(KnowledgeBuilder kbuilder) throws Exception {
				KnowledgeBuilderErrors errors = kbuilder.getErrors();
				if (errors.size() > 0) {
					for (KnowledgeBuilderError error : errors) {
						System.err.println(error);
					}
					throw new IllegalArgumentException(
							"Could not parse knowledge.");
				}
			}
		};
	}

	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}
}
