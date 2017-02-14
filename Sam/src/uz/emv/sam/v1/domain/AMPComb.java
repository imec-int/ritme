package uz.emv.sam.v1.domain;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * User: simbre1
 * Date: 23/09/13
 */
@Entity
@Table(name = "b#samAmpComb",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"ampId", "ampSource", "ampIdCmb", "ampSourceCmb"}))
public class AMPComb implements Comparable<AMPComb>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ampIdCmb", referencedColumnName = "ampId", nullable = false),
            @JoinColumn(name = "ampSourceCmb", referencedColumnName = "ampSource", nullable = false)
    })
    private AMP parent;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ampId", referencedColumnName = "ampId", nullable = false),
            @JoinColumn(name = "ampSource", referencedColumnName = "ampSource", nullable = false)
    })
    private AMP child;

    @Column(nullable = false)
    private String ampCmbSeq;

    public AMPComb(){
    }

    public void setParent(AMP parent){
        this.parent = parent;
    }

    public AMP getParent(){
        return parent;
    }

    public void setChild(AMP child){
        this.child = child;
    }

    public AMP getChild(){
        return child;
    }

    public void setAMPCmbSeq(String ampCmbSeq){
        this.ampCmbSeq = SAMTools.intern(ampCmbSeq);
    }

    public String getAMPCmbSeq(){
        return ampCmbSeq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }

        AMPComb a = (AMPComb) o;
        return ampCmbSeq.equals(a.ampCmbSeq)
                && child.equals(a.child)
                && parent.equals(a.parent);
    }

    @Override
    public int hashCode() {
        return child.hashCode() + 31 * parent.hashCode();
    }

    @Override
    public int compareTo(@NotNull final AMPComb a) {
        int c = ampCmbSeq.compareTo(a.ampCmbSeq);
        if(c != 0){
            return c;
        }
        c = child.compareTo(a.child);
        return c != 0 ? c : parent.compareTo(a.parent);
    }

    @Override
    public String toString(){
        return ampCmbSeq +": "+ String.valueOf(child);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(long rowVersion) {
        this.rowVersion = rowVersion;
    }
}
