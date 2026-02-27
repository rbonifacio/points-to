package br.ufpe.cin.pt.testsuite.context;

import static org.junit.Assert.assertEquals;

import br.ufpe.cin.pt.soot.AliasTransformer;
import br.ufpe.cin.pt.soot.CallGraphAlgorithm;
import br.ufpe.cin.pt.soot.Driver;
import br.ufpe.cin.pt.soot.TestConfiguration;
import org.junit.Test;

/**
 * Qilin 3-type-sensitive PTA on {@code br.ufpe.cin.pt.samples.context.Main#main(String[])}.
 *
 * In the context scenario both receivers ({@code b1} and {@code b2}) are declared as type
 * {@code B}, so 3-type sensitivity assigns them the same type context and merges the two calls
 * to {@code B.foo}. As a result {@code v1} and {@code v2} may alias.
 */
public class QILIN3TContextPointsToTestSuite {

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
    public void testQilin3T_v1_v2_mayAlias() {
        assertEquals(
                "Qilin 3-type-sensitive PTA should report MAY_ALIAS for v1/v2 (same receiver type B merges both calls).",
                AliasTransformer.Result.PTA_SUGGESTS_ALIAS,
                new Driver().runAnalysis(config("v1", "v2").setCallGraph(CallGraphAlgorithm.QILIN_3T)));
    }
}
