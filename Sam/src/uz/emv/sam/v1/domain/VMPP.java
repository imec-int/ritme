package uz.emv.sam.v1.domain;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

/**
 * User: simbre1
 * Date: 16/09/13
 */
@Entity
@Table(name = "b#samVmpp",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"vmpId", "vmpSource", "treatmentDurationCatCv", "treatmentDurationCatSource"}))
public class VMPP implements Comparable<VMPP>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "treatmentDurationCatCv", referencedColumnName = "treatmentDurationCatCv", nullable = false),
            @JoinColumn(name = "treatmentDurationCatSource", referencedColumnName = "treatmentDurationCatSource", nullable = false)
    })
    private TreatmentDurationCategory treatmentDurationCategory;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "vmpId", referencedColumnName = "vmpId", nullable = false),
            @JoinColumn(name = "vmpSource", referencedColumnName = "vmpSource", nullable = false)
    })
    private VMP vmp;

    @OneToMany(mappedBy = "vmpp", targetEntity = AMPP.class)
    private Set<AMPP> ampps = Collections.emptySet();

    public VMPP(){
    }

    public void setTreatmentDurationCategory(TreatmentDurationCategory treatmentDurationCategory){
        this.treatmentDurationCategory = treatmentDurationCategory;
    }

    public TreatmentDurationCategory getTreatmentDurationCategory(){
        return treatmentDurationCategory;
    }

    public void setVMP(VMP vmp){
        this.vmp = vmp;
    }

    public VMP getVMP(){
        return vmp;
    }

    public Set<AMPP> getAMPPs(){
        return ampps;
    }

    public void setAmpps(@NotNull Set<AMPP> ampps) {
        this.ampps = ampps;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        VMPP a = (VMPP)o;
        return treatmentDurationCategory.equals(a.treatmentDurationCategory)
                && vmp.equals(a.vmp);
    }

    @Override
    public int hashCode(){
        return 31 * treatmentDurationCategory.hashCode()
                + vmp.hashCode();
    }

    @Override
    public String toString(){
        if(vmp == null || treatmentDurationCategory == null){
            return super.toString();
        }
        return vmp.toString() +" "+ treatmentDurationCategory.toString();
    }

    @Override
    public int compareTo(@NotNull final VMPP a) {
        int c = this.vmp.compareTo(a.vmp);
        return c != 0 ? c : this.treatmentDurationCategory.compareTo(a.treatmentDurationCategory);
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
