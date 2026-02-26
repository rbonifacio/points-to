package br.ufpe.cin.pt.testsuite.point;

import static org.junit.Assert.assertEquals;

import br.ufpe.cin.pt.soot.AliasTransformer;
import br.ufpe.cin.pt.soot.CallGraphAlgorithm;
import br.ufpe.cin.pt.soot.Driver;
import br.ufpe.cin.pt.soot.TestConfiguration;
import org.junit.Test;

/**
 * Qilin MAHJONG-guided 2-object-sensitive PTA for point1 vs point2 (no alias).
 * Runs in a separate JVM so Qilin/Soot static state does not affect other Qilin tests.
 */
public class PointsToTestSuiteQilinM2OP1P2Test {

    private final TestConfiguration config = new TestConfiguration(
            "br.ufpe.cin.pt.samples.PointsToAnalysisEntry", "main",
            "br.ufpe.cin.pt.samples.PointTest", "testPoints",
            "point1", "point2", "br.ufpe.cin.pt.samples.Point");

    @Test
    public void testPointsToQilinM2OP1P2() {
        assertEquals(
                "Qilin MAHJONG-guided 2-object-sensitive PTA reports MAY_ALIAS for point1/point2 " +
                "because MAHJONG merges those two allocation sites into a single abstract object " +
                "(precision trade-off by design).",
                AliasTransformer.Result.PTA_SUGGESTS_ALIAS,
                new Driver().runAnalysis(config.setCallGraph(CallGraphAlgorithm.QILIN_M2O)));
    }
}
