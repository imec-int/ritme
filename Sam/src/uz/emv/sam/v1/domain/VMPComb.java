package uz.emv.sam.v1.domain;


import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * User: simbre1
 * Date: 23/09/13
 */
@Entity
@Table(name = "b#samVmpComb",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"vmpId", "vmpSource", "vmpIdCmb", "vmpSourceCmb"}))
public class VMPComb implements Comparable<VMPComb> {

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "vmpIdCmb", referencedColumnName = "vmpId", nullable = false),
            @JoinColumn(name = "vmpSourceCmb", referencedColumnName = "vmpSource", nullable = false)
    })
    private VMP parent;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "vmpId", referencedColumnName = "vmpId", nullable = false),
            @JoinColumn(name = "vmpSource", referencedColumnName = "vmpSource", nullable = false)
    })
    private VMP child;

    @Column(length = 4, nullable = false)
    private String vmpCmbSeq;

    public VMPComb(){

    }

    public void setParent(VMP parent){
        this.parent = parent;
    }

    public VMP getParent(){
        return parent;
    }

    public void setChild(VMP child){
        this.child = child;
    }

    public VMP getChild(){
        return child;
    }

    public void setVMPCmbSeq(String vmpCmbSeq){
        this.vmpCmbSeq = SAMTools.intern(vmpCmbSeq);
    }

    public String getVMPCmbSeq(){
        return vmpCmbSeq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VMPComb a = (VMPComb) o;
        return vmpCmbSeq.equals(a.vmpCmbSeq)
                && child.equals(a.child)
                && parent.equals(a.parent);
    }

    @Override
    public int hashCode() {
        return 31 * child.hashCode() + parent.hashCode();
    }

    @Override
    public int compareTo(@NotNull final VMPComb a) {
        int c = vmpCmbSeq.compareTo(a.vmpCmbSeq);
        if(c != 0){
            return c;
        }
        c = this.child.compareTo(a.child);
        return c != 0 ? c : this.parent.compareTo(a.parent);
    }

    @Override
    public String toString(){
        return String.valueOf(vmpCmbSeq) +": "+ String.valueOf(child);
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
