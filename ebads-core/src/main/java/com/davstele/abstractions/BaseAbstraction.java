package com.davstele.abstractions;

import com.davstele.models.ProbabilityMassFunction;

import java.util.HashMap;

/**
 * Created by dx on 5/16/16.
 */
public abstract class BaseAbstraction {

    protected HashMap<String, ProbabilityMassFunction> pmf; // Probabiliy Mass Function

    public BaseAbstraction() {
        this.pmf = new HashMap<String, ProbabilityMassFunction>();
    }

}
