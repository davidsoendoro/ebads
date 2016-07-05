package com.davstele;

import com.davstele.abstractions.BinaryOvertimeAbstraction;
import com.davstele.abstractions.IAbstraction;
import com.davstele.models.Sequence;
import com.davstele.sequencers.BinarySequencer;
import com.davstele.sequencers.ISequencer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Check if input files specified
        if (args.length < 3) {
            System.err.println("Please specify positive knowledge, anomalies, and input folder!");
            return;
        }

        List<IAbstraction> abstractions = new ArrayList<IAbstraction>();
        List<IAbstraction> anomalyAbstractions = new ArrayList<IAbstraction>();

        // Learn what is right and what is wrong with 4 right trace and 1 wrong trace
        File knowledgeFolder = new File(args[0]);
        if (knowledgeFolder.exists() && knowledgeFolder.isDirectory()) {
            File[] files = knowledgeFolder.listFiles();
            for (File file : files) {
                ISequencer sequencer = initializeSequencer();
                try {
                    sequencer.processFile(file);
                    List<Sequence> sequences = sequencer.getSequences();

                    for (Sequence sequence : sequences) {
                        IAbstraction abstraction = initializeAbstraction();
                        abstraction.processSequence(sequence);
                        abstractions.add(abstraction);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // File is not a pcap - write an exception
                }
            }
        } else {
            System.err.println("Specified knowledge is not a folder!");
            return;
        }

        File anomalyFolder = new File(args[1]);
        if (anomalyFolder.exists() && anomalyFolder.isDirectory()) {
            File[] files = anomalyFolder.listFiles();
            for (File file : files) {
                ISequencer sequencer = initializeSequencer();
                try {
                    sequencer.processFile(file);
                    List<Sequence> sequences = sequencer.getSequences();

                    for (Sequence sequence : sequences) {
                        IAbstraction abstraction = initializeAbstraction();
                        abstraction.processSequence(sequence);
                        anomalyAbstractions.add(abstraction);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // File is not a pcap - write an exception
                }
            }
        } else {
            System.err.println("Specified anomaly is not a folder!");
            return;
        }

        // Read input file
        File inputFolder = new File(args[2]);
        if (inputFolder.exists() && inputFolder.isDirectory()) {
            File[] files = inputFolder.listFiles();
            for (File file : files) {
                ISequencer sequencer = initializeSequencer();
                try {
                    sequencer.processFile(file);
                    List<Sequence> sequences = sequencer.getSequences();

                    int n = 0;
                    for (Sequence sequence : sequences) {
                        ++n;

                        double avgDistN = 0;
                        double avgDistAn = 0;
                        IAbstraction inAbstraction = initializeAbstraction();
                        inAbstraction.processSequence(sequence);

                        // Count average distance to normal
                        for (IAbstraction abstraction : abstractions) {
                            avgDistN += abstraction.getRelativeEntropy(inAbstraction);
                        }
                        avgDistN /= abstractions.size();

                        // Count average distance to anomaly
                        for (IAbstraction abstraction : anomalyAbstractions) {
                            avgDistAn += abstraction.getRelativeEntropy(inAbstraction);
                        }
                        avgDistAn /= abstractions.size();

                        // If distance is closer to normal then it is normal, if it is closer to anomaly then it is anomaly
                        System.out.println("Distance to Normal: " + avgDistN + " vs Anomaly: " + avgDistAn);
                        avgDistN *= avgDistN;
                        avgDistAn *= avgDistAn;
                        if(avgDistN > avgDistAn) {
                            System.out.print(file.getName() + " Sequence Nr:" + n + " ");
                            System.out.println("Anomaly");
                        }
                        else {
                            System.out.print(file.getName() + " Sequence Nr:" + n + " ");
                            System.out.println("Normal");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // File is not a pcap - write an exception
                }
            }
        } else {
            System.err.println("Specified input is not a folder!");
            return;
        }

        // Test 1 correct input and 1 wrong input

        // Formulate input into readable format

        // Test input classification in binary level


        return;
    }

    private static IAbstraction initializeAbstraction() {
        return new BinaryOvertimeAbstraction();
    }

    private static ISequencer initializeSequencer() {
        return new BinarySequencer();
    }

}
