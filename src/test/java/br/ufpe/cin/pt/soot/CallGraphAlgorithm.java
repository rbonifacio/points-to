package br.ufpe.cin.pt.soot;

public enum CallGraphAlgorithm {
    SOOT_CHA("soot::CHA"),
    SOOT_RTA("soot::RTA"),
    SOOT_VTA("soot::VTA"),
    SOOT_SPARK("soot::SPARK"),
    QILIN_INSENS("qilin::INSENS");

    final String name;

    CallGraphAlgorithm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
