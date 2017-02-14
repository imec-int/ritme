package uz.emv.sam.v1.domain;

import org.jetbrains.annotations.NotNull;
import uz.emv.sam.v1.ObjectTools;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

/**
 * User: simbre1
 * Date: 23/09/13
 */
@Entity
@Table(name = "b#samAmpIntermediatePackage",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"ampId", "ampSource", "contentQuantity", "contentUnit"}))
public class AMPIntermediatePackage implements Comparable<AMPIntermediatePackage>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ampId", referencedColumnName = "ampId", nullable = false),
            @JoinColumn(name = "ampSource", referencedColumnName = "ampSource", nullable = false)
    })
    private AMP amp;

    @Column(precision = 12, scale = 3, nullable = false)
    private double contentQuantity = 0;

    @Column(length = 5, nullable = false)
    private String contentUnit;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "innerPackageCv", referencedColumnName = "innerPackageCv"),
            @JoinColumn(name = "innerPackageSource", referencedColumnName = "innerPackageSource")
    })
    private InnerPackage innerPackage;

    @Column
    private Integer addedMultiplier;

    @Column
    private Double addedQuantity;

    @Column
    private String addedUnit;

    @Column
    private String addedType;

    @Column
    private String packageTxt;

    @OneToMany(mappedBy = "parent", targetEntity = AMPIntPckComb.class)
    private Set<AMPIntPckComb> childCombinations = Collections.emptySet();

    @OneToMany(mappedBy = "child", targetEntity = AMPIntPckComb.class)
    private Set<AMPIntPckComb> parentCombinations = Collections.emptySet();

    @OneToMany(mappedBy = "ampIntermediatePackage", targetEntity = AMPP.class)
    private Set<AMPP> ampps = Collections.emptySet();

    public AMPIntermediatePackage(){
    }

    public AMP getAMP() {
        return amp;
    }

    public void setAMP(AMP amp) {
        this.amp = amp;
    }

    public double getContentQuantity() {
        return contentQuantity;
    }

    public void setContentQuantity(double contentQuantity) {
        this.contentQuantity = contentQuantity;
    }

    public String getContentUnit() {
        return contentUnit;
    }

    public void setContentUnit(String contentUnit) {
        this.contentUnit = SAMTools.intern(contentUnit);
    }

    public InnerPackage getInnerPackage() {
        return innerPackage;
    }

    public void setInnerPackage(InnerPackage innerPackage) {
        this.innerPackage = innerPackage;
    }

    public Integer getAddedMultiplier() {
        return addedMultiplier;
    }

    public void setAddedMultiplier(Integer addedMultiplier) {
        this.addedMultiplier = addedMultiplier;
    }

    public Double getAddedQuantity() {
        return addedQuantity;
    }

    public void setAddedQuantity(Double addedQuantity) {
        this.addedQuantity = addedQuantity;
    }

    public String getAddedUnit() {
        return addedUnit;
    }

    public void setAddedUnit(String addedUnit) {
        this.addedUnit = SAMTools.intern(addedUnit);
    }

    public String getAddedType() {
        return addedType;
    }

    public void setAddedType(String addedType) {
        this.addedType = SAMTools.intern(addedType);
    }

    public Set<AMPIntPckComb> getChildCombinations() {
        return childCombinations;
    }

    public void setChildCombinations(@NotNull Set<AMPIntPckComb> childCombinations) {
        this.childCombinations = childCombinations;
    }

    public Set<AMPIntPckComb> getParentCombinations() {
        return parentCombinations;
    }

    public void setParentCombinations(@NotNull Set<AMPIntPckComb> parentCombinations) {
        this.parentCombinations = parentCombinations;
    }

    public Set<AMPP> getAMPPs(){
        return ampps;
    }

    public void setAmpps(@NotNull Set<AMPP> ampps) {
        this.ampps = ampps;
    }

    public String getPackageTxt() {
        return packageTxt;
    }

    public void setPackageTxt(String packageTxt) {
        this.packageTxt = packageTxt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }

        AMPIntermediatePackage a = (AMPIntermediatePackage) o;
        //noinspection FloatingPointEquality
        return amp.equals(a.amp)
                && contentQuantity == a.contentQuantity
                && contentUnit.equals(a.contentUnit);
    }

    @Override
    public int hashCode() {
        int result = amp.hashCode();
        result = 31 * result + (int) contentQuantity;
        result = 31 * result + contentUnit.hashCode();
        return result;
    }

    @Override
    public int compareTo(@NotNull final AMPIntermediatePackage a) {
        int c = amp.compareTo(a.amp);
        if(c != 0){
            return c;
        }
        c = ObjectTools.compare(this.contentUnit, a.contentUnit);
        return c != 0 ? c : ObjectTools.compare(this.contentQuantity, a.contentQuantity);
    }

    @Override
    public String toString(){
        return amp.toString() +" ("+ contentQuantity +" "+ contentUnit +")";
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
