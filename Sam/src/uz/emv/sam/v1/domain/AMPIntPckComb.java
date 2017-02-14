package uz.emv.sam.v1.domain;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * User: simbre1
 * Date: 23/09/13
 */
@Entity
@Table(name = "b#samAmpIntPckComb",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "ampIdCmb", "ampSourceCmb", "ampIntPckCqCmb", "ampIntPckCuCmb",
                "ampId", "ampSource", "ampIntPckCq", "ampIntPckCu"}))
public class AMPIntPckComb implements Comparable<AMPIntPckComb>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ampIdCmb", referencedColumnName = "ampId", nullable = false),
            @JoinColumn(name = "ampSourceCmb", referencedColumnName = "ampSource", nullable = false),
            @JoinColumn(name = "ampIntPckCqCmb", referencedColumnName = "contentQuantity", nullable = false),
            @JoinColumn(name = "ampIntPckCuCmb", referencedColumnName = "contentUnit", nullable = false)
    })
    private AMPIntermediatePackage parent;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ampId", referencedColumnName = "ampId", nullable = false),
            @JoinColumn(name = "ampSource", referencedColumnName = "ampSource", nullable = false),
            @JoinColumn(name = "ampIntPckCq", referencedColumnName = "contentQuantity", nullable = false),
            @JoinColumn(name = "ampIntPckCu", referencedColumnName = "contentUnit", nullable = false)
    })
    private AMPIntermediatePackage child;

    @Column(nullable = false)
    private String ampIntPckCmbSeq;

    public AMPIntPckComb(){
    }

    public AMPIntermediatePackage getParent() {
        return parent;
    }

    public void setParent(AMPIntermediatePackage parent) {
        this.parent = parent;
    }

    public AMPIntermediatePackage getChild() {
        return child;
    }

    public void setChild(AMPIntermediatePackage child) {
        this.child = child;
    }

    public String getAMPIntPckCmbSeq() {
        return ampIntPckCmbSeq;
    }

    public void setAMPIntPckCmbSeq(String ampIntPckCmbSeq) {
        this.ampIntPckCmbSeq = ampIntPckCmbSeq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AMPIntPckComb a = (AMPIntPckComb) o;
        return ampIntPckCmbSeq.equals(a.ampIntPckCmbSeq)
                && child.equals(a.child)
                && parent.equals(a.parent);
    }

    @Override
    public int hashCode() {
        return 31 * child.hashCode() + parent.hashCode();
    }

    @Override
    public int compareTo(@NotNull final AMPIntPckComb a) {
        int c = ampIntPckCmbSeq.compareTo(a.ampIntPckCmbSeq);
        if(c != 0){
            return c;
        }
        c = child.compareTo(a.child);
        return c != 0 ? c : parent.compareTo(a.parent);
    }

    @Override
    public String toString(){
        return ampIntPckCmbSeq +": "+ String.valueOf(child);
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
