package br.ufpe.cin.pt.testsuite.point;

import static org.junit.Assert.assertEquals;

import br.ufpe.cin.pt.soot.AliasTransformer;
import br.ufpe.cin.pt.soot.CallGraphAlgorithm;
import br.ufpe.cin.pt.soot.Driver;
import br.ufpe.cin.pt.soot.TestConfiguration;
import org.junit.Test;

/**
 * Qilin 3-hybrid-type-sensitive PTA for point1 vs point2 (no alias).
 * Runs in a separate JVM so Qilin/Soot static state does not affect other Qilin tests.
 */
public class PointsToTestSuiteQilin3HTP1P2Test {

    private final TestConfiguration config = new TestConfiguration(
            "br.ufpe.cin.pt.samples.PointsToAnalysisEntry", "main",
            "br.ufpe.cin.pt.samples.PointTest", "testPoints",
            "point1", "point2", "br.ufpe.cin.pt.samples.Point");

    @Test
    public void testPointsToQilin3HTP1P2() {
        assertEquals(
                "Qilin 3-hybrid-type-sensitive PTA should report NO_ALIAS for point1/point2, " +
                "because point1 and point2 are created from different allocations.",
                AliasTransformer.Result.PTA_NO_EVIDENCE_OF_ALIAS,
                new Driver().runAnalysis(config.setCallGraph(CallGraphAlgorithm.QILIN_3HT)));
    }
}
