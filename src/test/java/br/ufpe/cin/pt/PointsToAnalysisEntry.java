package br.ufpe.cin.pt;

/**
 * Entry point for Soot whole-program analysis. Invokes the method under analysis
 * so it is reachable in the call graph.
 */
public class PointsToAnalysisEntry {
    public static void main(String[] args) {
        new PointTest().testPoints();
    }
}
