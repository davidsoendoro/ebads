package teadui.models;

import com.davstele.models.Sequence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by dx on 5/19/16.
 */
@Entity
public class Knowledge {

    @Id
    @GeneratedValue
    private Long id;
    @Column(columnDefinition = "LONGBLOB")
    private byte[] sequence;

    private String label;

    protected Knowledge() {

    }

    public Knowledge(String sequence, String label) {
        this.sequence = sequence.getBytes();
        this.label = label;
    }

    public Knowledge(Sequence sequence, String label) {
        this.sequence = sequence.getAllBytes();
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public byte[] getSequence() {
        return sequence;
    }

    public void setSequence(byte[] sequence) {
        this.sequence = sequence;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return String.format("Knowledge[id=%d, label='%s']", id, label);
    }
}
