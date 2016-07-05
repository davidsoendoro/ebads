package com.davstele.abstractions;

import com.davstele.models.ProbabilityMassFunction;
import com.davstele.models.Sequence;

import java.util.List;

/**
 * Created by dx on 5/18/16.
 */
public interface IAbstraction {

    /**
     * Process sequence into probability mass function
     * @param sequence
     */
    void processSequence(Sequence sequence);

    /**
     * Get alphabets of the probability mass function
     * @return
     */
    List<String> getAlphabets();

    /**
     * Get p(x)
     * @param x a character in the alphabet
     * @return
     */
    ProbabilityMassFunction getPmf(String x);

    /**
     * Get Distance from this distribution to the other distribution D(p||q) this = p
     * @return
     */
    double getRelativeEntropy(IAbstraction abstraction);
}
