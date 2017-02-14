package uz.emv.sam.v1.domain;

import org.jetbrains.annotations.NotNull;
import uz.emv.sam.v1.ObjectTools;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

/**
 * User: simbre1
 * Date: 17/09/13
 */
@Entity
@Table(name = "b#samTreatmentDurationCategor",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"treatmentDurationCatCv", "treatmentDurationCatSource"}))
public class TreatmentDurationCategory implements Comparable<TreatmentDurationCategory> {

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 2, nullable = false)
    private String treatmentDurationCatCv;

    @Enumerated(EnumType.STRING)
    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    private Source treatmentDurationCatSource;

    @Column(length = 4)
    private Integer treatmentDurationValue;

    @Column(length = 5)
    private String treatmentDurationUnit;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    @OneToMany(mappedBy = "treatmentDurationCategory", targetEntity = VMPP.class)
    private Set<VMPP> vmpps = Collections.emptySet();

    public TreatmentDurationCategory(){
    }

    public void setTreatmentDurationCatCv(String treatmentDurationCatCv){
        this.treatmentDurationCatCv = treatmentDurationCatCv;
    }

    public String getTreatmentDurationCatCv(){
        return treatmentDurationCatCv;
    }

    public void setNameId(long nameId){
        this.nameId = nameId;
    }

    public long getNameId(){
        return nameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTreatmentDurationValue() {
        return treatmentDurationValue;
    }

    public void setTreatmentDurationValue(Integer treatmentDurationValue) {
        this.treatmentDurationValue = treatmentDurationValue;
    }

    public String getTreatmentDurationUnit() {
        return treatmentDurationUnit;
    }

    public void setTreatmentDurationUnit(String treatmentDurationUnit) {
        this.treatmentDurationUnit = treatmentDurationUnit;
    }

    public Set<VMPP> getVMPPs(){
        return vmpps;
    }

    public void setVmpps(@NotNull Set<VMPP> vmpps) {
        this.vmpps = vmpps;
    }

    public Source getTreatmentDurationCatSource() {
        return treatmentDurationCatSource;
    }

    public void setTreatmentDurationCatSource(Source treatmentDurationCatSource) {
        if (treatmentDurationCatSource != null) {
            this.treatmentDurationCatSource = treatmentDurationCatSource;
        }
        this.treatmentDurationCatSource = Source.SAM;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        TreatmentDurationCategory a = (TreatmentDurationCategory)o;
        return treatmentDurationCatCv.equals(a.treatmentDurationCatCv)
                && ObjectTools.equals(treatmentDurationCatSource, a.treatmentDurationCatSource);
    }

    @Override
    public int hashCode(){
        return treatmentDurationCatCv.hashCode();
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final TreatmentDurationCategory a) {
        int c = treatmentDurationCatCv.compareTo(a.treatmentDurationCatCv);
        return c != 0 ? c : ObjectTools.compare(treatmentDurationCatSource, a.treatmentDurationCatSource);
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
