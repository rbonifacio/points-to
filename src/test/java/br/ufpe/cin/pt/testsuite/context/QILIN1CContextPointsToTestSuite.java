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
 * Same scenario as {@link QILINInsensContextPointsToTestSuite}, but with 1-callsite sensitivity:
 * the two calls to {@code B.foo} should be distinguished and v1/v2 kept separate.
 */
public class QILIN1CContextPointsToTestSuite {

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
    public void testQilin1C_v1_v2_noAlias() {
        assertEquals(
                "Qilin 1-callsite-sensitive PTA should report NO_ALIAS for v1/v2 (separate call contexts).",
                AliasTransformer.Result.PTA_NO_EVIDENCE_OF_ALIAS,
                new Driver().runAnalysis(config("v1", "v2").setCallGraph(CallGraphAlgorithm.QILIN_1C)));
    }

    // @Test
    // public void testQilin1C_v1_o2_noAlias() {
    //     assertEquals(
    //             "Qilin 1-callsite-sensitive PTA should report NO_ALIAS for v1/o2 (v1 should only point to o1).",
    //             AliasTransformer.Result.PTA_NO_EVIDENCE_OF_ALIAS,
    //             new Driver().runAnalysis(config("v1", "o2").setCallGraph(CallGraphAlgorithm.QILIN_1C)));
    // }

    // @Test
    // public void testQilin1C_v2_o1_noAlias() {
    //     assertEquals(
    //             "Qilin 1-callsite-sensitive PTA should report NO_ALIAS for v2/o1 (v2 should only point to o2).",
    //             AliasTransformer.Result.PTA_NO_EVIDENCE_OF_ALIAS,
    //             new Driver().runAnalysis(config("v2", "o1").setCallGraph(CallGraphAlgorithm.QILIN_1C)));
    // }
}

