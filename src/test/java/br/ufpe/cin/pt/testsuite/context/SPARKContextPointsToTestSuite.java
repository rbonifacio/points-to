package br.ufpe.cin.pt.testsuite.context;

import static org.junit.Assert.assertEquals;

import br.ufpe.cin.pt.soot.AliasTransformer;
import br.ufpe.cin.pt.soot.CallGraphAlgorithm;
import br.ufpe.cin.pt.soot.Driver;
import br.ufpe.cin.pt.soot.TestConfiguration;
import org.junit.Test;

/**
 * Tests for the context-sensitivity scenario in {@code br.ufpe.cin.pt.samples.context.Main#main(String[])}.
 *
 * In {@code Main.main()}:
 *
 * <pre>
 *   Object o1 = new Object();
 *   B b1 = new B();
 *   A a1 = new A();
 *   Object v1 = b1.foo(o1, a1);
 *   Object o2 = new Object();
 *   B b2 = new B();
 *   Object v2 = b2.foo(o2, a1);
 * </pre>
 *
 * These tests encode expectations about how Spark (Soot's points-to analysis) handles v1/v2.
 */
public class SPARKContextPointsToTestSuite {

    private static final String ENTRY_CLASS = "br.ufpe.cin.pt.samples.context.Main";
    private static final String ENTRY_METHOD = "main";
    private static final String TARGET_CLASS = "br.ufpe.cin.pt.samples.context.Main";
    private static final String TARGET_METHOD = "main";
    private static final String TARGET_TYPE = "java.lang.Object";

    private TestConfiguration config(String local1, String local2) {
        return new TestConfiguration(
                ENTRY_CLASS, ENTRY_METHOD,
                TARGET_CLASS, TARGET_METHOD,
                local1, local2, TARGET_TYPE);
    }

    @Test
    public void testSPARK_v1_v2_mayAlias() {
        assertEquals(
                "SPARK should report MAY_ALIAS for v1/v2 (merged call contexts).",
                AliasTransformer.Result.PTA_SUGGESTS_ALIAS,
                new Driver().runAnalysis(config("v1", "v2").setCallGraph(CallGraphAlgorithm.SOOT_SPARK)));
    }
}

