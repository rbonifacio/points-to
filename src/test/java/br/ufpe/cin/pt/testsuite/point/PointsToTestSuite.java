package br.ufpe.cin.pt.testsuite.point;

import static org.junit.Assert.assertEquals;

import br.ufpe.cin.pt.samples.PointTest;
import br.ufpe.cin.pt.soot.AliasTransformer;
import br.ufpe.cin.pt.soot.CallGraphAlgorithm;
import br.ufpe.cin.pt.soot.Driver;
import br.ufpe.cin.pt.soot.TestConfiguration;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Tests that use Soot to analyze {@link PointTest#testPointsFromSources()} with different
 * call graph / points-to algorithms (CHA, RTA, VTA, Spark) and check whether
 * <code>p1</code> and <code>p2</code> may point to the same objects.
 * <p>
 * In testPointsFromSources(): p1 from PointSourceA.getPoint(), p2 from PointSourceB.getPoint().
 * Spark (precise): NO_ALIAS. CHA (conservative): MAY_ALIAS.
 */
public class PointsToTestSuite {

    /* A test configuration for testing alias between point1 and point2.
    *  This configuration must fail for SPARK.
    */
    private final TestConfiguration configTestAliasForPoint1Point2 = new TestConfiguration("br.ufpe.cin.pt.samples.PointsToAnalysisEntry", "main", "br.ufpe.cin.pt.samples.PointTest", "testPoints", "point1", "point2", "br.ufpe.cin.pt.samples.Point");

    private final TestConfiguration configTestAliasForPoint2Point3 = new TestConfiguration("br.ufpe.cin.pt.samples.PointsToAnalysisEntry", "main", "br.ufpe.cin.pt.samples.PointTest", "testPoints", "point2", "point3", "br.ufpe.cin.pt.samples.Point");

    @Test
    public void testPointsToWithCHAP1P2() {
        assertEquals(
                "Conceptually, I expected CHA (without SPARK) to report NO_ALIAS for p1/p2, but Soot reports that they may alias. " +
                "If it reports MAY_ALIAS, it is because the call graph is not precise enough; this is a false positive.",
                AliasTransformer.Result.PTA_SUGGESTS_ALIAS,
                new Driver().runAnalysis(configTestAliasForPoint1Point2.setCallGraph(CallGraphAlgorithm.SOOT_CHA)));
    }

    @Test
    public void testPointsToWithCHAP2P3() {
        assertEquals(
                "Conceptually, I expected CHA (without SPARK) to report NO_ALIAS for p2/p3, but Soot reports that they may alias. " +
                "This is a surprising TRUE positive: even without Spark, CHA still leads Soot to populate points-to information and detect that p2 and p3 may point to the same object.",
                AliasTransformer.Result.PTA_SUGGESTS_ALIAS,
                new Driver().runAnalysis(configTestAliasForPoint2Point3.setCallGraph(CallGraphAlgorithm.SOOT_CHA)));
    }

    @Test
    public void testPointsToWithSparkP1P2() {
        assertEquals(
                "Spark (precise) should report NO_ALIAS for p1/p2, because p1 and p2 are created from different allocations (distinct Point objects).",
                AliasTransformer.Result.PTA_NO_EVIDENCE_OF_ALIAS,
                new Driver().runAnalysis(configTestAliasForPoint1Point2.setCallGraph(CallGraphAlgorithm.SOOT_SPARK)));
    }

    @Test
    public void testPointsToWithSparkP2P3() {
        assertEquals(
                "Spark (precise) should report MAY_ALIAS for p2/p3, because p3 is assigned from p2 (p3 = p2), so they may point to the same object.",
                AliasTransformer.Result.PTA_SUGGESTS_ALIAS,
                new Driver().runAnalysis(configTestAliasForPoint2Point3.setCallGraph(CallGraphAlgorithm.SOOT_SPARK)));
    }

   
    @Test
    public void testPointsToWithRTAP1P2() {
        assertEquals(
                "Conceptually, I expected RTA (with Spark) to report NO_ALIAS for p1/p2, because p1 and p2 are created from different allocations. " +
                "If it reports MAY_ALIAS, it is because the call graph / analysis is not precise enough; this is a false positive.",
                AliasTransformer.Result.PTA_SUGGESTS_ALIAS,
                new Driver().runAnalysis(configTestAliasForPoint1Point2.setCallGraph(CallGraphAlgorithm.SOOT_RTA)));
    }

    @Test
    public void testPointsToWithRTAP2P3() {
        assertEquals(
                "Conceptually, I expected RTA (with Spark) to report NO_ALIAS for p2/p3, but Soot reports that they may alias. " +
                "This is a surprising TRUE positive: even with RTA, Soot's points-to information detects that p2 and p3 may point to the same object (p3 = p2).",
                AliasTransformer.Result.PTA_SUGGESTS_ALIAS,
                new Driver().runAnalysis(configTestAliasForPoint2Point3.setCallGraph(CallGraphAlgorithm.SOOT_RTA)));
    }

    @Test
    public void testPointsToWithVTAP1P2() {
        assertEquals(
                "Conceptually, I expected VTA (with Spark) to report NO_ALIAS for p1/p2, because p1 and p2 are created from different allocations. " +
                "If it reports MAY_ALIAS, it is because the call graph / analysis is not precise enough; this is a false positive.",
                AliasTransformer.Result.PTA_SUGGESTS_ALIAS,
                new Driver().runAnalysis(configTestAliasForPoint1Point2.setCallGraph(CallGraphAlgorithm.SOOT_VTA)));
    }

    @Test
    public void testPointsToWithVTAP2P3() {
        assertEquals(
                "Conceptually, I expected VTA (with Spark) to report NO_ALIAS for p2/p3, but Soot reports that they may alias. " +
                "This is a surprising TRUE positive: even with VTA, Soot's points-to information detects that p2 and p3 may point to the same object (p3 = p2).",
                AliasTransformer.Result.PTA_SUGGESTS_ALIAS,
                new Driver().runAnalysis(configTestAliasForPoint2Point3.setCallGraph(CallGraphAlgorithm.SOOT_VTA)));
    }
}
