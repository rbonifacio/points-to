package br.ufpe.cin.pt.soot.pta;

import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;

public class SootPTA implements PTA {
    private PointsToAnalysis pta;

    public SootPTA(PointsToAnalysis pta) {
        if(pta == null){
            throw new NullPointerException("pta should not be null");
        }
        this.pta = pta;
    }

    @Override
    public boolean hasIntersectingObjects(Local l1, Local l2) {
        PointsToSet s1 = pta.reachingObjects(l1);
        PointsToSet s2 = pta.reachingObjects(l2);
        return s1.hasNonEmptyIntersection(s2);
    }
}
