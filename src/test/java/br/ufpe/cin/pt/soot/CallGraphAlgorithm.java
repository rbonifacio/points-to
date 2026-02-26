package br.ufpe.cin.pt.soot;

public enum CallGraphAlgorithm {
    SOOT_CHA("soot::CHA"),
    SOOT_RTA("soot::RTA"),
    SOOT_VTA("soot::VTA"),
    SOOT_SPARK("soot::SPARK"),
    QILIN_INSENS("qilin::INSENS"),
    QILIN_1C("qilin::1C"),
    QILIN_1O("qilin::1O"),
    QILIN_1T("qilin::1T"),
    QILIN_2C("qilin::2C"),
    QILIN_2O("qilin::2O"),
    QILIN_2T("qilin::2T"),
    QILIN_2H("qilin::2H"),
    QILIN_2HT("qilin::2HT"),
    QILIN_3C("qilin::3C"),
    QILIN_3O("qilin::3O"),
    QILIN_3T("qilin::3T"),
    QILIN_3H("qilin::3H"),
    QILIN_3HT("qilin::3HT"),
    QILIN_B2O("qilin::bean-2o"),
    QILIN_D2O("qilin::datadriven-2o"),
    QILIN_D2C("qilin::datadriven-2c"),
    QILIN_E2O("qilin::eagle-2o"),
    QILIN_T2O("qilin::turner-2o"),
    QILIN_M2O("qilin::mahjong-2o"),
    QILIN_M2C("qilin::mahjong-2c"),
    QILIN_Z2O("qilin::zipper-2o"),
    QILIN_Z2C("qilin::zipper-2c");

    final String name;

    CallGraphAlgorithm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
