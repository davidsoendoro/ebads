package com.davstele.abstractions;

import com.davstele.helpers.MathHelper;
import com.davstele.models.ProbabilityMassFunction;
import com.davstele.models.Sequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dx on 5/18/16.
 */
public class BinaryAbstraction extends BaseAbstraction implements IAbstraction {

    private List<String> alphabets;

    public BinaryAbstraction() {
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

        Integer prev = null, curr = null;
        for (int i = 0; i < bytes.length; i++) {
            for ( int mask = 0x01; mask != 0x100; mask <<= 1 ) {
                boolean value = ( bytes[i] & mask ) != 0;

                if(prev == null) {
                    // The first one, assign prev
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

                    // construct pmf
                    String key = "" + prev + curr;
                    ProbabilityMassFunction px = pmf.get(key);
                    px.incVarFrequency();
                    pmf.put(key, px);
                    // add all domains
                    for(String k : getAlphabets()) {
                        if(k.equals(key)) continue;
                        ProbabilityMassFunction pk = pmf.get(k);
                        pk.incDomainFrequency();
                        pmf.put(k, pk);
                    }

                    // update prev
                    prev = curr;
                }
            }
        }
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

        // Sigma of p(x) * log(p(x)/q(x))
        double d = 0;
        for(String k : getAlphabets()) {
            ProbabilityMassFunction px = getPmf(k);
            ProbabilityMassFunction qx = abstraction.getPmf(k);

            d = px.getProbability() * MathHelper.log2(px.getProbability() / qx.getProbability());
        }

        return d;
    }
}
