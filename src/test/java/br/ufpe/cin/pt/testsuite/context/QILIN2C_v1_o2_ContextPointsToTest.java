package br.ufpe.cin.pt.testsuite.context;

import static org.junit.Assert.assertEquals;

import br.ufpe.cin.pt.soot.AliasTransformer;
import br.ufpe.cin.pt.soot.CallGraphAlgorithm;
import br.ufpe.cin.pt.soot.Driver;
import br.ufpe.cin.pt.soot.TestConfiguration;
import org.junit.Test;

/**
 * Qilin 2-callsite-sensitive PTA on {@code br.ufpe.cin.pt.samples.context.Main#main(String[])}.
 *
 * Verifies that {@code v1} (return value of the first {@code B.foo} call, which receives {@code o1})
 * does not alias {@code o2} when the two call sites to {@code B.foo} are separated by 2-callsite
 * sensitivity.
 */
public class QILIN2C_v1_o2_ContextPointsToTest {

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
    public void testQilin2C_v1_o2_noAlias() {
        assertEquals(
                "Qilin 2-callsite-sensitive PTA should report NO_ALIAS for v1/o2 (v1 should only point to o1).",
                AliasTransformer.Result.PTA_NO_EVIDENCE_OF_ALIAS,
                new Driver().runAnalysis(config("v1", "o2").setCallGraph(CallGraphAlgorithm.QILIN_2C)));
    }
}

