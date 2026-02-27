package br.ufpe.cin.pt.testsuite.context;

import static org.junit.Assert.assertEquals;

import br.ufpe.cin.pt.soot.AliasTransformer;
import br.ufpe.cin.pt.soot.CallGraphAlgorithm;
import br.ufpe.cin.pt.soot.Driver;
import br.ufpe.cin.pt.soot.TestConfiguration;
import org.junit.Test;

/**
 * Qilin 1-callsite-sensitive PTA on {@code br.ufpe.cin.pt.samples.context.Main#main(String[])}.
 *
 * Verifies that {@code v2} (return value of the second {@code B.foo} call, which receives {@code o2})
 * does not alias {@code o1} (the allocation site passed to the first call). Under 1-callsite
 * sensitivity the two calls are kept separate, so {@code v2} points only to {@code o2}.
 */
public class QILIN1C_v2_o1_ContextPointsToTest {

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
    public void testQilin1C_v2_o1_noAlias() {
        assertEquals(
                "Qilin 1-callsite-sensitive PTA should report NO_ALIAS for v2/o1 (v2 should only point to o2).",
                AliasTransformer.Result.PTA_NO_EVIDENCE_OF_ALIAS,
                new Driver().runAnalysis(config("v2", "o1").setCallGraph(CallGraphAlgorithm.QILIN_1C)));
    }
}
