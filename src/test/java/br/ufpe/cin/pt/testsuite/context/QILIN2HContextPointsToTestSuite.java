package br.ufpe.cin.pt.testsuite.context;

import static org.junit.Assert.assertEquals;

import br.ufpe.cin.pt.soot.AliasTransformer;
import br.ufpe.cin.pt.soot.CallGraphAlgorithm;
import br.ufpe.cin.pt.soot.Driver;
import br.ufpe.cin.pt.soot.TestConfiguration;
import org.junit.Test;

/**
 * Qilin 2-hybrid-sensitive PTA on {@code br.ufpe.cin.pt.samples.context.Main#main(String[])}.
 *
 * With 2-hybrid sensitivity the two calls to {@code B.foo} are kept separate, so
 * {@code v1} and {@code v2} should not alias.
 */
public class QILIN2HContextPointsToTestSuite {

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
    public void testQilin2H_v1_v2_noAlias() {
        assertEquals(
                "Qilin 2-hybrid-sensitive PTA should report NO_ALIAS for v1/v2 (separate call contexts).",
                AliasTransformer.Result.PTA_NO_EVIDENCE_OF_ALIAS,
                new Driver().runAnalysis(config("v1", "v2").setCallGraph(CallGraphAlgorithm.QILIN_2H)));
    }
}
