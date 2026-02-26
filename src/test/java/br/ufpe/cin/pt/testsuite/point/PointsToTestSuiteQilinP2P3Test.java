package br.ufpe.cin.pt.testsuite.point;

import static org.junit.Assert.assertEquals;

import br.ufpe.cin.pt.soot.AliasTransformer;
import br.ufpe.cin.pt.soot.CallGraphAlgorithm;
import br.ufpe.cin.pt.soot.Driver;
import br.ufpe.cin.pt.soot.TestConfiguration;
import org.junit.Test;

/**
 * Runs in a separate JVM (own test class) so Qilin/Soot static state does not affect other Qilin tests.
 */
public class PointsToTestSuiteQilinP2P3Test {

    private final TestConfiguration config = new TestConfiguration(
            "br.ufpe.cin.pt.samples.PointsToAnalysisEntry", "main",
            "br.ufpe.cin.pt.samples.PointTest", "testPoints",
            "point2", "point3", "br.ufpe.cin.pt.samples.Point");

    @Test
    public void testPointsToQilinINSENSP2P3() {
        assertEquals(
                "Conceptually, I expected Qilin context-insensitive PTA to report MAY_ALIAS for point2/point3, because point3 is assigned from point2 (point3 = point2), so they may point to the same object. "
                + "However, Qilin currently reports NO_ALIAS (PTA_NO_EVIDENCE_OF_ALIAS) for this pair; this is a surprising FALSE negative.",
                AliasTransformer.Result.PTA_NO_EVIDENCE_OF_ALIAS,
                new Driver().runAnalysis(config.setCallGraph(CallGraphAlgorithm.QILIN_INSENS)));
    }
}
