package uz.emv.sam.v1.domain;

import org.jetbrains.annotations.NotNull;
import uz.emv.sam.v1.ObjectTools;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

/**
 * User: simbre1
 * Date: 16/09/13
 */
@Entity
@Table(name = "b#samAtc",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"atcCv", "atcSource"}))
public class ATC implements Comparable<ATC>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 7, nullable = false)
    private String atcCv;

    @Enumerated(EnumType.STRING)
    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    private Source atcSource;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "atcCvParent", referencedColumnName = "atcCv"),
            @JoinColumn(name = "atcSourceParent", referencedColumnName = "atcSource")
    })
    private ATC parent;

    @Column(nullable = false)
    private boolean finalLevelInd = false;

    @Column(nullable = false)
    private boolean flatRateInd = false;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    @OneToMany(mappedBy = "parent", targetEntity = ATC.class)
    private Set<ATC> children = Collections.emptySet();

    @OneToMany(mappedBy = "atc", targetEntity = AMP.class)
    private Set<AMP> amps = Collections.emptySet();

    public ATC(){
    }

    public void setAtcCv(String atcCv){
        this.atcCv = atcCv;
    }

    public String getAtcCv(){
        return atcCv;
    }

    public void setNameId(long nameId){
        this.nameId = nameId;
    }

    public long getNameId(){
        return nameId;
    }

    public void setParent(ATC parent){
        this.parent = parent;
    }

    public ATC getParent(){
        return parent;
    }

    public Set<ATC> getChildren(){
        return children;
    }

    public void setChildren(@NotNull Set<ATC> children) {
        this.children = children;
    }

    public Set<AMP> getAMPs(){
        return amps;
    }

    public void setAmps(@NotNull Set<AMP> amps) {
        this.amps = amps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getFinalLevelInd() {
        return finalLevelInd;
    }

    public void setFinalLevelInd(boolean finalLevelInd) {
        this.finalLevelInd = finalLevelInd;
    }

    public boolean getFlatRateInd() {
        return flatRateInd;
    }

    public void setFlatRateInd(boolean flatRateInd) {
        this.flatRateInd = flatRateInd;
    }

    public Source getAtcSource() {
        return atcSource;
    }

    public void setAtcSource(Source atcSource) {
        if (atcSource != null) {
            this.atcSource = atcSource;
        }
        this.atcSource = Source.SAM;

    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        ATC a = (ATC)o;
        return atcCv.equals(a.atcCv)
                && ObjectTools.equals(atcSource, a.atcSource);
    }

    @Override
    public int hashCode(){
        return atcCv.hashCode();
    }

    @Override
    public String toString(){
        return atcCv == null ? super.toString() : atcCv;
    }

    @Override
    public int compareTo(@NotNull final ATC a) {
        int c = atcCv.compareTo(a.atcCv);
        return c != 0 ? c : ObjectTools.compare(atcSource, a.atcSource);
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
