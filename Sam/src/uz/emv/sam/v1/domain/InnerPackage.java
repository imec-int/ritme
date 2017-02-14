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
@Table(name = "b#samInnerPackage",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"innerPackageCv", "innerPackageSource"}))
public class InnerPackage implements Comparable<InnerPackage>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 2, nullable = false)
    private String innerPackageCv;

    @Enumerated(EnumType.STRING)
    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    private Source innerPackageSource;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    @OneToMany(mappedBy = "innerPackage", targetEntity = AMPIntermediatePackage.class)
    private Set<AMPIntermediatePackage> ampIntermediatePackages = Collections.emptySet();

    public InnerPackage(){
    }

    public long getNameId() {
        return nameId;
    }

    public void setNameId(long nameId) {
        this.nameId = nameId;
    }

    public String getInnerPackageCv() {
        return innerPackageCv;
    }

    public void setInnerPackageCv(String innerPackageCv) {
        this.innerPackageCv = innerPackageCv;
    }

    public Set<AMPIntermediatePackage> getAMPIntermediatePackages(){
        return ampIntermediatePackages;
    }

    public void setAmpIntermediatePackages(@NotNull Set<AMPIntermediatePackage> ampIntermediatePackages) {
        this.ampIntermediatePackages = ampIntermediatePackages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Source getInnerPackageSource() {
        return innerPackageSource;
    }

    public void setInnerPackageSource(Source innerPackageSource) {
        if (innerPackageSource != null) {
            this.innerPackageSource = innerPackageSource;
        }
        this.innerPackageSource = Source.SAM;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InnerPackage a = (InnerPackage) o;
        return innerPackageCv.equals(a.innerPackageCv)
                && ObjectTools.equals(innerPackageSource, a.innerPackageSource);
    }

    @Override
    public int hashCode() {
        return innerPackageCv.hashCode();
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final InnerPackage a) {
        int c = innerPackageCv.compareTo(a.innerPackageCv);
        return c != 0 ? c : ObjectTools.compare(innerPackageSource, a.innerPackageSource);
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
