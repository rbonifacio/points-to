package br.ufpe.cin.pt.soot.pta;

import soot.Local;

public interface PTA {
    boolean hasIntersectingObjects(Local l1, Local l2);
}
