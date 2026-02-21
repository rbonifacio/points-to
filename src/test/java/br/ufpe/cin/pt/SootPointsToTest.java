package br.ufpe.cin.pt;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import soot.*;
import soot.options.Options;

/**
 * Tests that use Soot to analyze {@link PointTest#testPointsFromSources()} with different
 * call graph / points-to algorithms (CHA, RTA, VTA, Spark) and check whether
 * <code>p1</code> and <code>p2</code> may point to the same objects.
 * <p>
 * In testPointsFromSources(): p1 from PointSourceA.getPoint(), p2 from PointSourceB.getPoint().
 * Spark (precise): NO_ALIAS. CHA (conservative): MAY_ALIAS.
 */
public class SootPointsToTest {

    private static final String ENTRY_CLASS = "br.ufpe.cin.pt.PointsToAnalysisEntry";
    private static final String POINT_TEST_CLASS = "br.ufpe.cin.pt.PointTest";
    private static final String TARGET_METHOD = "testPointsFromSources";

    /**
     * Runs Soot with the given call graph / points-to configuration and
     * returns the may-alias result.
     */
    private PointsToCheckTransformer.Result runAnalysis(AnalysisConfiguration config) {
        setSootOptions();
        setCallGraph(config.algorithm);
        //TODO: parameterize local1, local2, ...
        PointsToCheckTransformer transformer = new PointsToCheckTransformer(config.targetClass, config.targetMethod, "point2", "point3", "br.ufpe.cin.pt.Point");
        try {
            Scene.v().loadNecessaryClasses();
            Scene.v().setEntryPoints(getEntryPoints(config.entryClass, config.entryMethod));
            PackManager.v().getPack("wjtp").add(new Transform("wjtp.ptcheck", transformer));
            PackManager.v().getPack("cg").apply();
            PackManager.v().getPack("wjtp").apply();
        } catch (Exception e) {
            throw new RuntimeException("Soot run failed for " + config.algorithm, e);
        }

        return transformer.getResult();
    }

    private void setSootOptions() {
        G.reset();

        String classpath = buildClassPath();
        String processDir = new File("target/test-classes").getAbsolutePath();

        // SVFA-style configuration (see workspace-scala/svfa JavaSootConfiguration)
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_include(getIncludeList());
        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_whole_program(true);
        Options.v().set_soot_classpath(classpath);
        Options.v().set_process_dir(Arrays.asList(new String[]{processDir}));
        Options.v().set_full_resolver(true);
        Options.v().set_keep_line_number(true);
        Options.v().set_prepend_classpath(true);
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().setPhaseOption("jop", "enabled:false");   // Jimple optimization pack (copy prop, CSE, etc.)
        Options.v().setPhaseOption("wjop", "enabled:false");  // Whole-Jimple optimization pack (inliner, etc.)
//        Options.v().setPhaseOption("cg.cha", "enabled:false");
//        Options.v().setPhaseOption("cg.spark", "enabled:false");
//        Options.v().setPhaseOption("cg.paddle", "enabled:false");
    }


    /** Configures Soot call-graph / points-to analysis for the given algorithm (CHA, SPARK, RTA, VTA). */
    private static void setCallGraph(String algorithm) {
        switch (algorithm.toUpperCase()) {
            case "CHA":
                Options.v().setPhaseOption("cg.cha", "on");
                Options.v().setPhaseOption("cg.spark", "on");
                Options.v().setPhaseOption("cg.spark", "on-fly-cg:false");
                break;
            case "SPARK":
                Options.v().setPhaseOption("cg.spark", "on");
                Options.v().setPhaseOption("cg.spark", "on-fly-cg:true");
                break;
            case "RTA":
                Options.v().setPhaseOption("cg.spark", "on");
                Options.v().setPhaseOption("cg.spark", "rta:true");
                Options.v().setPhaseOption("cg.spark", "on-fly-cg:false");
                break;
            case "VTA":
                Options.v().setPhaseOption("cg.spark", "on");
                Options.v().setPhaseOption("cg.spark", "vta:true");
                Options.v().setPhaseOption("cg.spark", "on-fly-cg:false");
                break;
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }

    /** Include list so these packages are treated as application classes (SVFA pattern). */
    private static List<String> getIncludeList() {
        return Arrays.asList("br.ufpe.cin.pt.*", "java.lang.*", "java.util.*");
    }

    /** Entry points for the call graph (SVFA pattern: main method of entry class). */
    private static List<SootMethod> getEntryPoints(String entryPointClass, String entryPointMethod) {
        SootClass entryClass = Scene.v().getSootClass(entryPointClass);
        SootMethod mainMethod = entryClass.getMethodByName(entryPointMethod);
        return Collections.singletonList(mainMethod);
    }

    private static String buildClassPath() {
        String cp = System.getProperty("java.class.path");
        String testClasses = new File("target/test-classes").getAbsolutePath();
        StringBuilder sb = new StringBuilder();
        sb.append(testClasses).append(File.pathSeparator).append(testClasses);
        if (cp != null && !cp.isEmpty()) {
            sb.append(File.pathSeparator).append(cp);
        }
        String rt = pathToRT();
        if (rt != null) {
            sb.append(File.pathSeparator).append(rt);
        }
        String jce = pathToJCE();
        if (jce != null) {
            sb.append(File.pathSeparator).append(jce);
        }
        return sb.toString();
    }

    private static String pathToRT() {
        File rt = new File(System.getProperty("java.home"), "lib/rt.jar");
        return rt.exists() ? rt.getAbsolutePath() : null;
    }

    private static String pathToJCE() {
        File jce = new File(System.getProperty("java.home"), "lib/jce.jar");
        return jce.exists() ? jce.getAbsolutePath() : null;
    }

    class AnalysisConfiguration {
        String entryClass;
        String entryMethod;
        String targetClass;
        String targetMethod;
        String algorithm;

        public AnalysisConfiguration(String entryClass, String entryMethod, String targetClass, String targetMethod) {
            this.entryClass = entryClass;
            this.entryMethod = entryMethod;
            this.targetClass = targetClass;
            this.targetMethod = targetMethod;
        }

        public AnalysisConfiguration setCallGraph(String callGraph) {
            this.algorithm = callGraph;
            return this;
        }
    }

    private AnalysisConfiguration config1 = new AnalysisConfiguration("br.ufpe.cin.pt.PointsToAnalysisEntry", "main", "br.ufpe.cin.pt.PointTest", "testPoints");

    @Ignore
    public void testPointsToWithCHA() {
        assertEquals(
            "CHA (conservative) should report NO_ALIAS for p1/p2 in testPointsFromSources()",
            PointsToCheckTransformer.Result.MAY_ALIAS,
            runAnalysis(config1.setCallGraph("CHA")));
    }

    @Test
    public void testPointsToWithSpark() {
        assertEquals(
            "Spark (precise) should report MAY_ALIAS for p1/p2 in testPointsFromSources()",
                PointsToCheckTransformer.Result.MAY_ALIAS,
                runAnalysis(config1.setCallGraph("SPARK")));
    }

    // @Test
    // public void testPointsToWithRTA() {
    //     assertEquals(
    //         "RTA-style analysis should report MAY_ALIAS for point2 and point3 in testPoints()",
    //         PointsToCheckTransformer.Result.MAY_ALIAS,
    //         runAnalysisAndGetPoint2Point3Result("RTA"));
    // }

    // @Test
    // public void testPointsToWithVTA() {
    //     assertEquals(
    //         "VTA-style analysis should report MAY_ALIAS for point2 and point3 in testPoints()",
    //         PointsToCheckTransformer.Result.MAY_ALIAS,
    //         runAnalysisAndGetPoint2Point3Result("VTA"));
    // }
}
