package br.ufpe.cin.pt.testsuite.point;

import static org.junit.Assert.assertEquals;

import br.ufpe.cin.pt.soot.AliasTransformer;
import br.ufpe.cin.pt.soot.CallGraphAlgorithm;
import br.ufpe.cin.pt.soot.Driver;
import br.ufpe.cin.pt.soot.TestConfiguration;
import org.junit.Test;

/**
 * Qilin 3-hybrid-object-sensitive PTA for point2 vs point3 (alias: point3 = point2).
 * Runs in a separate JVM so Qilin/Soot static state does not affect other Qilin tests.
 */
public class PointsToTestSuiteQilin3HP2P3Test {

    private final TestConfiguration config = new TestConfiguration(
            "br.ufpe.cin.pt.samples.PointsToAnalysisEntry", "main",
            "br.ufpe.cin.pt.samples.PointTest", "testPoints",
            "point2", "point3", "br.ufpe.cin.pt.samples.Point");

    @Test
    public void testPointsToQilin3HP2P3() {
        assertEquals(
                "Qilin 3-hybrid-object-sensitive PTA should report MAY_ALIAS for point2/point3, " +
                "because point3 is assigned from point2 (point3 = point2), so they may point to the same object.",
                AliasTransformer.Result.PTA_SUGGESTS_ALIAS,
                new Driver().runAnalysis(config.setCallGraph(CallGraphAlgorithm.QILIN_3H)));
    }
}
