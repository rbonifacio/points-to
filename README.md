# Points-to and Call Graph

This project explores **the impact of the call graph on points-to relationships** using [Soot](https://soot-oss.github.io/soot/) 4 (org.soot-oss:soot) and [Qilin](https://github.com/rbonifacio/QilinPTA) (our fork of [QilinPTA/Qilin](https://github.com/QilinPTA/Qilin), built for Java 8). It uses two scenarios:

1. **Point scenario** – `PointTest.testPoints()`: compares how different call-graph/PTA configurations (CHA, Spark, RTA, VTA, Qilin INSENS, Qilin 1C) affect alias results for locals `point1`, `point2`, `point3`.
2. **Context scenario** – `samples.context.Main.main()`: tests whether context-sensitive vs context-insensitive analyses correctly distinguish `v1`/`v2` (returned from two calls to `B.foo(o, a1)`) and their relation to `o1`/`o2`.

A notable finding: for some imprecise algorithms (e.g. **CHA**), Soot still populates points-to information **even when Spark is disabled** in the configuration.

---

## Scenario: `PointTest.testPoints()`

The scenario under analysis is the following method in `br.ufpe.cin.pt.samples.PointTest`:

```java
public void testPoints() {
  Point point1 = new Point(1, 2);
  Point point2 = new Point(3, 4);
  Point point3 = point2;
  // ...
  assertEquals(5.0, point1.distance(point2), 0.000001);
  assertEquals(5.0, point3.distance(point2), 0.000001);
}
```

- **point1** and **point2** point to different allocations → no alias.
- **point2** and **point3** refer to the same object (`point3 = point2`) → they alias.

The analysis entry point is `PointsToAnalysisEntry.main()`, which invokes `new PointTest().testPoints()` so that `testPoints()` is reachable in the call graph.

---

## Scenario: `samples.context.Main.main()`

Second scenario used to study **context sensitivity**:

```java
public static void main(String[] args) {
    Object o1 = new Object();
    B b1 = new B();
    A a1 = new A();
    Object v1 = b1.foo(o1, a1);
    Object o2 = new Object();
    B b2 = new B();
    Object v2 = b2.foo(o2, a1);
    System.out.println(v1.equals(v2));
}
```

- Semantically, `v1` should point only to `o1`, and `v2` only to `o2` (each call to `B.foo` returns its first argument).
- A **context-insensitive** PTA may merge the two calls and report that `v1` and `v2` both point to `o1` and `o2` (e.g. through the shared `A a1` and its field `f`).
- A **1-callsite-sensitive** PTA (Qilin 1C) should distinguish the two calls and keep `v1`/`v2` separate.

Tests in `br.ufpe.cin.pt.testsuite.context` encode these expectations (Spark, Qilin INSENS, Qilin 1C). **Some Qilin context tests are commented out** because running multiple Qilin analyses in the same JVM can cause static-state issues; one test per Qilin context class is left active for now.

The context example (e.g. `Main.main()` with `v1`/`v2` and two calls to `B.foo`) is from the Qilin paper: Dongjie He, Jingbo Lu, and Jingling Xue, *Qilin: A New Framework For Supporting Fine-Grained Context-Sensitivity in Java Pointer Analysis*, ECOOP 2022, [LIPIcs 222, 30:1–30:29](https://doi.org/10.4230/LIPIcs.ECOOP.2022.30).

---

## Call Graph and PTA Configurations (Driver)

Call graph and points-to analysis are configured in the **`Driver`** class (`br.ufpe.cin.pt.soot.Driver`) via `setCallGraph(algorithm)`:

| Algorithm | Configuration |
|-----------|----------------|
| **CHA**  | `cg.cha` enabled, `cg.spark` **disabled**. Call graph is built only with Class Hierarchy Analysis; Spark is excluded from execution. |
| **SPARK** | `cg.spark` enabled, `on-fly-cg:true`. Spark builds the call graph on the fly with its points-to analysis. |
| **RTA**   | `cg.spark` enabled, `rta:true`, `on-fly-cg:false`. Rapid Type Analysis style. |
| **VTA**   | `cg.spark` enabled, `vta:true`, `on-fly-cg:false`. Variable Type Analysis style. |
| **QILIN_INSENS** | Soot’s CHA and Spark disabled; Qilin runs its context-insensitive PTA and builds the call graph. |
| **QILIN_1C**     | Same as above; Qilin runs its 1-callsite-sensitive PTA (PTAPattern `"1c"`). |

Relevant snippet from `Driver.setCallGraph()`:

```java
case "CHA":
    Options.v().setPhaseOption("cg.cha", "enabled:true");
    Options.v().setPhaseOption("cg.spark", "enabled:false");
    break;
case "SPARK":
    Options.v().setPhaseOption("cg.spark", "enabled:true");
    Options.v().setPhaseOption("cg.spark", "on-fly-cg:true");
    break;
// RTA and VTA use cg.spark with rta:true / vta:true and on-fly-cg:false
```

So for **CHA**, Spark is explicitly turned off; for **SPARK**, **RTA**, and **VTA**, Spark is used with different options.

---

## Test Suite: Purpose and Structure

Test suites are split into two packages:

- **`br.ufpe.cin.pt.testsuite.point`** – Point scenario (`PointTest.testPoints()`). **`PointsToTestSuite`** documents behaviour for CHA, SPARK, RTA, VTA; separate classes run Qilin INSENS and 1C (one JVM per class to avoid static state issues).
- **`br.ufpe.cin.pt.testsuite.context`** – Context scenario (`Main.main()`). **`SPARKContextPointsToTestSuite`**, **`QILINInsensContextPointsToTestSuite`**, and **`QILIN1CContextPointsToTestSuite`** each run in their own JVM. Some methods in the Qilin context suites are **commented out** because running several Qilin tests in the same VM has caused problems; the intent is to keep one active test per Qilin context class for now.

Each test runs the **Driver** with a **`TestConfiguration`** that specifies:

- Entry class and method (e.g. `PointsToAnalysisEntry.main` or `Main.main`)
- Target class and method (e.g. `PointTest.testPoints` or `Main.main`)
- The two locals to check for may-alias
- The call graph / PTA algorithm (CHA, SPARK, RTA, VTA, QILIN_INSENS, QILIN_1C)

There are two configurations in the **point** scenario:

| Configuration | Locals | Relationship in code |
|---------------|--------|------------------------|
| **configTestAliasForPoint1Point2** | point1, point2 | Different allocations → no alias |
| **configTestAliasForPoint2Point3** | point2, point3 | `point3 = point2` → they alias |

Results are compared against **`AliasTransformer.Result`**:

- **`PTA_NO_EVIDENCE_OF_ALIAS`** – points-to sets do not overlap.
- **`PTA_SUGGESTS_ALIAS`** – non-empty intersection (analysis suggests may-alias).
- **`PTA_UNAVAILABLE`** – points-to information could not be used (e.g. not `PointsToSetInternal`).

---

### Why some tests are `@Ignore`d

Several tests are marked **`@Ignore`** so that the suite still documents expectations and current behaviour without failing the build.

| Test | Reason ignored |
|------|----------------|
| **testPointsToWithCHAP1P2** | **Unexpected result.** The test expects `PTA_NO_EVIDENCE_OF_ALIAS` for point1 vs point2 when using CHA (with Spark disabled). In practice, CHA still populates points-to and reports that they may alias. The test is ignored to record this surprising behaviour (points-to populated even without Spark) until the cause is understood or accepted. |
| **testPointsToWithCHAP2P3** | **Unexpected result.** Same situation with point2 vs point3 under CHA: the test expected `PTA_NO_EVIDENCE_OF_ALIAS`, but CHA reports may-alias. (Note: in the code, point2 and point3 do alias; the ignore documents that CHA’s result differs from the test’s original expectation.) |
| **testPointsToWithRTAP1P2**, **testPointsToWithRTAP2P3** | **Memory issues.** RTA in this setup triggers high memory use or timeouts. Ignored with the intent to fix the configuration or environment later. |
| **testPointsToWithVTAP1P3**, **testPointsToWithVTAP2P3** | **Unexpected result.** The tests expect `PTA_NO_EVIDENCE_OF_ALIAS` for the chosen pairs, but VTA (with Spark) reports may-alias. Ignored to document the mismatch while the behaviour is investigated. |

**Tests that run (point scenario, no `@Ignore`):**

- **testPointsToWithSparkP1P2** – Spark reports no evidence of alias for point1 vs point2 (as expected).
- **testPointsToWithSparkP2P3** – Spark reports may-alias for point2 vs point3 (as expected).
- **testPointsToQilinINSENSP1P2** – Qilin context-insensitive PTA, point1/point2 (`PointsToTestSuiteQilinP1P2Test`, own JVM).
- **testPointsToQilinINSENSP2P3** – Qilin context-insensitive PTA, point2/point3 (`PointsToTestSuiteQilinP2P3Test`, own JVM).
- **testPointsToQilin1CP1P2** – Qilin 1-callsite-sensitive PTA, point1/point2 (`PointsToTestSuiteQilin1CP1P2Test`, own JVM).
- **testPointsToQilin1CP2P3** – Qilin 1-callsite-sensitive PTA, point2/point3 (`PointsToTestSuiteQilin1CP2P3Test`, own JVM).

**Context scenario** (`Main.main()`, package `testsuite.context`):

- **SPARKContextPointsToTestSuite** – e.g. `testSPARK_v1_v2_mayAlias` (Spark may report v1/v2 as may-alias).
- **QILINInsensContextPointsToTestSuite** – one active test: `testQilinInsens_v1_v2_mayAlias`; other methods (v1/o2, v2/o1) are commented out to avoid running multiple Qilin tests in the same JVM.
- **QILIN1CContextPointsToTestSuite** – one active test: `testQilin1C_v1_v2_noAlias`; v1/o2 and v2/o1 tests are commented out for the same reason.

Qilin tests use separate test classes so each runs in a fresh JVM (Surefire `reuseForks=false`), avoiding static state issues. The suite validates Spark and Qilin on both scenarios and keeps a written record of CHA, RTA, and VTA behaviour (and known issues) via ignored tests.

---

### Running the tests

**Use Java 8 to run tests.** Soot/ASM do not support Java 21 bytecode; on Java 21 you will see `Unsupported class file major version 65`. Switch to JDK 8 and run:

```bash
export JAVA_HOME=/path/to/jdk8   # or use jenv/sdkman to select Java 8
mvn clean test
```

`mvn clean test-compile` first ensures test classes (including `CallGraphAlgorithm` and `PointTest`) are compiled by Maven; without a clean build, stale IDE class files can cause “CallGraphAlgorithm cannot be resolved” at test time.

---

## Project Layout

All packages live under `br.ufpe.cin.pt` (test source root: `src/test/java/`).

- **`samples`** – Scenario code: `PointTest`, `Point`, `PointsToAnalysisEntry` (point scenario); `samples.context`: `Main`, `A`, `B` (context scenario).
- **`soot`** – Soot/Qilin wiring: `Driver`, `AliasTransformer`, `TestConfiguration`, `CallGraphAlgorithm`, and `pta` (SootPTA, QilinPTA, PTASingleton).
- **`testsuite.point`** – Point scenario: `PointsToTestSuite` (Spark, CHA, RTA, VTA), `PointsToTestSuiteQilinP1P2Test`, `PointsToTestSuiteQilinP2P3Test`, `PointsToTestSuiteQilin1CP1P2Test`, `PointsToTestSuiteQilin1CP2P3Test`.
- **`testsuite.context`** – Context scenario: `SPARKContextPointsToTestSuite`, `QILINInsensContextPointsToTestSuite`, `QILIN1CContextPointsToTestSuite`.

---

## Requirements

- **Java 8** to run tests. Soot/ASM in this stack do not support Java 21 bytecode (you get "Unsupported class file major version 65"). Use JDK 8 for `mvn test` (e.g. set `JAVA_HOME` to JDK 8).
- **Soot 4** (org.soot-oss:soot) is required; the [Qilin](https://github.com/rbonifacio/QilinPTA) dependency (our fork, built for Java 8) is built against Soot 4’s API.
- **Maven 3.x.**
