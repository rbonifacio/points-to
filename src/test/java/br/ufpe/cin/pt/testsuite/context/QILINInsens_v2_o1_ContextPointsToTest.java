package br.ufpe.cin.pt.testsuite.context;

import static org.junit.Assert.assertEquals;

import br.ufpe.cin.pt.soot.AliasTransformer;
import br.ufpe.cin.pt.soot.CallGraphAlgorithm;
import br.ufpe.cin.pt.soot.Driver;
import br.ufpe.cin.pt.soot.TestConfiguration;
import org.junit.Test;

/**
 * Qilin context-insensitive PTA on {@code br.ufpe.cin.pt.samples.context.Main#main(String[])}.
 *
 * Verifies that {@code v2} and {@code o1} may alias under a context-insensitive analysis:
 * because the two {@code B.foo} calls are merged, {@code v2} can point to both {@code o1}
 * and {@code o2}, producing a may-alias result.
 */
public class QILINInsens_v2_o1_ContextPointsToTest {

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
    public void testQilinInsens_v2_o1_mayAlias() {
        assertEquals(
                "Qilin context-insensitive PTA should report MAY_ALIAS for v2/o1.",
                AliasTransformer.Result.PTA_SUGGESTS_ALIAS,
                new Driver().runAnalysis(config("v2", "o1").setCallGraph(CallGraphAlgorithm.QILIN_INSENS)));
    }
}
