package br.ufpe.cin.pt.testsuite.context;

import static org.junit.Assert.assertEquals;

import br.ufpe.cin.pt.soot.AliasTransformer;
import br.ufpe.cin.pt.soot.CallGraphAlgorithm;
import br.ufpe.cin.pt.soot.Driver;
import br.ufpe.cin.pt.soot.TestConfiguration;
import org.junit.Test;

/**
 * Qilin 1-type-sensitive PTA on {@code br.ufpe.cin.pt.samples.context.Main#main(String[])}.
 *
 * Verifies that {@code v1} (return value of the first {@code B.foo} call, which receives {@code o1})
 * may alias {@code o2} when the two calls to {@code B.foo} are merged by 1-type sensitivity (both
 * receivers have the same static type {@code B}).
 */
public class QILIN1T_v1_o2_ContextPointsToTest {

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
    public void testQilin1T_v1_o2_mayAlias() {
        assertEquals(
                "Qilin 1-type-sensitive PTA should report MAY_ALIAS for v1/o2 when calls to B.foo are merged by type context.",
                AliasTransformer.Result.PTA_SUGGESTS_ALIAS,
                new Driver().runAnalysis(config("v1", "o2").setCallGraph(CallGraphAlgorithm.QILIN_1T)));
    }
}

