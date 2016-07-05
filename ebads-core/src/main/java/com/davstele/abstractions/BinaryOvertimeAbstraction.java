package com.davstele.abstractions;

import com.davstele.helpers.MathHelper;
import com.davstele.models.ProbabilityMassFunction;
import com.davstele.models.Sequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dx on 5/18/16.
 */
public class BinaryOvertimeAbstraction extends BaseAbstraction implements IAbstraction {

    private List<String> alphabets;
    private HashMap<String, ProbabilityMassFunction>[] overtimePmf;

    public BinaryOvertimeAbstraction() {
        super();
        alphabets = new ArrayList<String>();
        alphabets.add("00");
        alphabets.add("01");
        alphabets.add("10");
        alphabets.add("11");

        for(String key : alphabets) {
            pmf.put(key, new ProbabilityMassFunction(key));
        }
    }

    public void processSequence(Sequence sequence) {
        byte[] bytes = sequence.getAllBytes();
        overtimePmf = new HashMap[bytes.length * 8];

        int n = 0;
        Integer prev = null, curr = null;
        for (int i = 0; i < bytes.length; i++) {
            for ( int mask = 0x01; mask != 0x100; mask <<= 1 ) {
                boolean value = ( bytes[i] & mask ) != 0;

                if(prev == null) {
                    // The first one, construct first pmf and assign prev
                    HashMap<String, ProbabilityMassFunction> ontimePmf = initializePmf();
                    for(String k : getAlphabets()) {
                        ProbabilityMassFunction pk = ontimePmf.get(k);
                        pk.setVarFrequency(1);
                        pk.setDomainFrequency(getAlphabets().size());
                        ontimePmf.put(k, pk);
                    }
                    overtimePmf[n] = ontimePmf;
                    // assign prev
                    if(value) {
                        prev = 1;
                    }
                    else {
                        prev = 0;
                    }
                }
                else {
                    // The value after, assign curr - construct pmf then update prev
                    if(value) {
                        curr = 1;
                    }
                    else {
                        curr = 0;
                    }

                    // construct ontimePmf
                    HashMap<String, ProbabilityMassFunction> prevPmf = overtimePmf[n - 1];
                    HashMap<String, ProbabilityMassFunction> ontimePmf = initializePmf();

                    String key = "" + prev + curr;
                    ProbabilityMassFunction px = prevPmf.get(key);
                    px.incVarFrequency();
                    ontimePmf.put(key, px);
                    // add all domains
                    for(String k : getAlphabets()) {
                        if(k.equals(key)) continue;
                        ProbabilityMassFunction pk = prevPmf.get(k);
                        pk.incDomainFrequency();
                        ontimePmf.put(k, pk);
                    }
                    overtimePmf[n] = ontimePmf;

                    // update prev
                    prev = curr;
                }

                ++n;
            }
        }
    }

    private HashMap<String, ProbabilityMassFunction> initializePmf() {
        HashMap<String, ProbabilityMassFunction> pmf = new HashMap<String, ProbabilityMassFunction>();
        for(String k : getAlphabets()) {
            pmf.put(k, new ProbabilityMassFunction(k));
        }
        return pmf;
    }

    public List<String> getAlphabets() {
        return alphabets;
    }

    public ProbabilityMassFunction getPmf(String x) {
        return pmf.get(x);
    }

    public double getRelativeEntropy(IAbstraction abstraction) {
        if(!abstraction.getClass().equals(this.getClass())) {
            throw new ClassCastException("To count distance between distributions, two distributions must" +
                    " be of a same class");
        }

        BinaryOvertimeAbstraction binaryOvertimeAbstraction = (BinaryOvertimeAbstraction) abstraction;

        // get minimum length between this overtimePmf and the input overtimePmf
        int minSize = overtimePmf.length;
        if(minSize > binaryOvertimeAbstraction.getOvertimePmf().length) {
            minSize = binaryOvertimeAbstraction.getOvertimePmf().length;
        }

        // Average of RE overtime
        double avgD = 0;
        for(int i = 0; i < minSize; i++) {
            // Sigma of p(x) * log(p(x)/q(x))
            for(String k : getAlphabets()) {
                HashMap<String, ProbabilityMassFunction> ontimePmf = overtimePmf[i];
                HashMap<String, ProbabilityMassFunction> ontimePmfInput = binaryOvertimeAbstraction.getOvertimePmf()[i];
                ProbabilityMassFunction px = ontimePmf.get(k);
                ProbabilityMassFunction qx = ontimePmfInput.get(k);

                avgD += px.getProbability() * MathHelper.log2(px.getProbability() / qx.getProbability());
            }
        }
        avgD /= overtimePmf.length;

        return avgD;
    }

    public HashMap<String, ProbabilityMassFunction>[] getOvertimePmf() {
        return overtimePmf;
    }
}
