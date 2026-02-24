package br.ufpe.cin.pt.soot.pta;

public class PTASingleton {
    private static PTA singleton;

    public static PTA getInstance() {
        if(singleton == null) {
            throw new RuntimeException("You should call either 'configureSootPTA(pta)' or 'configureQilinPTA(pta)', " +
                    "before calling 'getInstance()'");
        }
        return singleton;
    }

    public static void configureSootPTA(soot.PointsToAnalysis pta) {
        singleton = new SootPTA(pta);
    }

    public static void configureQilinPTA(qilin.core.PointsToAnalysis pta) {
        singleton = new QilinPTA(pta);
    }
}
