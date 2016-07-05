package com.davstele.models;

/**
 * Created by dx on 5/16/16.
 */
public class ProbabilityMassFunction {

    private String variableName;
    private Integer varFrequency;
    private Integer domainFrequency;

    public ProbabilityMassFunction(String variableName) {
        this.variableName = variableName;
        this.varFrequency = 0;
        this.domainFrequency = 0;
    }

    public String getVariableName() {
        return variableName;
    }

    public Integer getVarFrequency() {
        return varFrequency;
    }

    public void setVarFrequency(Integer varFrequency) {
        this.varFrequency = varFrequency;
    }

    public void incVarFrequency() {
        this.varFrequency += 1;
        this.domainFrequency += 1;
    }

    public Integer getDomainFrequency() {
        return domainFrequency;
    }

    public void setDomainFrequency(Integer domainFrequency) {
        this.domainFrequency = domainFrequency;
    }

    public void incDomainFrequency() {
        this.domainFrequency += 1;
    }

    public double getProbability() {
        double probability = 0;

        if(getDomainFrequency() > 0) {
            probability = (double)getVarFrequency() / (double)getDomainFrequency();
        }

        return probability;
    }
}
