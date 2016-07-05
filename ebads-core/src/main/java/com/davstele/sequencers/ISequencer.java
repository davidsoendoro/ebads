package com.davstele.sequencers;

import com.davstele.models.Sequence;

import java.io.File;
import java.util.List;

/**
 * Created by dx on 5/19/16.
 */
public interface ISequencer {

    /**
     * Process file into probability mass function
     * @param file
     */
    void processFile(File file) throws Exception;

    /**
     * Get Sequenced Sequences
     * @return
     */
    List<Sequence> getSequences();
}
