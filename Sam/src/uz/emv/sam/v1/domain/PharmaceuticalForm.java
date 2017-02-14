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
@Table(name = "b#samPharmaceuticalForm",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"pharmFormId", "pharmFormSource"}))
public class PharmaceuticalForm implements Comparable<PharmaceuticalForm> {

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 5, nullable = false)
    private int pharmFormId = -1;

    @Enumerated(EnumType.STRING)
    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    private Source pharmFormSource;

    @Column(nullable = false)
    private boolean noInnInd = false;

    @Column(nullable = false)
    private boolean dividableInd = false;

    @Column(nullable = false)
    private boolean entericCoatedInd = false;

    @Column(nullable = false)
    private boolean retardedInd = false;

    @Column(length = 2)
    private String solidToLiquid;

    @Column(length = 2)
    private String aerosolType;

    @Column(length = 2)
    private String tool;

    @Column(nullable = false)
    private boolean vehicInd = false;

    @Column(nullable = false)
    private boolean crushableInd = false;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    @OneToMany(mappedBy = "pharmaceuticalForm", targetEntity = AMP.class)
    private Set<AMP> amps = Collections.emptySet();

    public PharmaceuticalForm(){
    }

    public void setPharmFormId(int pharmFormId){
        this.pharmFormId = pharmFormId;
    }

    public int getPharmFormId(){
        return pharmFormId;
    }

    public void setNameId(long nameId){
        this.nameId = nameId;
    }

    public long getNameId(){
        return nameId;
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

    public boolean getNoInnInd() {
        return noInnInd;
    }

    public void setNoInnInd(boolean noInnInd) {
        this.noInnInd = noInnInd;
    }

    public boolean getDividableInd() {
        return dividableInd;
    }

    public void setDividableInd(boolean dividableInd) {
        this.dividableInd = dividableInd;
    }

    public boolean getEntericCoatedInd() {
        return entericCoatedInd;
    }

    public void setEntericCoatedInd(boolean entericCoatedInd) {
        this.entericCoatedInd = entericCoatedInd;
    }

    public boolean getRetardedInd() {
        return retardedInd;
    }

    public void setRetardedInd(boolean retardedInd) {
        this.retardedInd = retardedInd;
    }

    public String getSolidToLiquid() {
        return solidToLiquid;
    }

    public void setSolidToLiquid(String solidToLiquid) {
        this.solidToLiquid = SAMTools.intern(solidToLiquid);
    }

    public String getAerosolType() {
        return aerosolType;
    }

    public void setAerosolType(String aerosolType) {
        this.aerosolType = SAMTools.intern(aerosolType);
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = SAMTools.intern(tool);
    }

    public boolean getVehicInd() {
        return vehicInd;
    }

    public void setVehicInd(boolean vehicInd) {
        this.vehicInd = vehicInd;
    }

    public boolean getCrushableInd() {
        return crushableInd;
    }

    public void setCrushableInd(boolean crushableInd) {
        this.crushableInd = crushableInd;
    }

    public Source getPharmFormSource() {
        return pharmFormSource;
    }

    public void setPharmFormSource(Source pharmFormSource) {
        if (pharmFormSource != null) {
            this.pharmFormSource = pharmFormSource;
        }
        this.pharmFormSource = Source.SAM;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        PharmaceuticalForm a = (PharmaceuticalForm)o;
        return pharmFormId == a.pharmFormId
                && ObjectTools.equals(pharmFormSource, a.pharmFormSource);
    }

    @Override
    public int hashCode(){
        return pharmFormId;
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final PharmaceuticalForm a) {
        int c = pharmFormId - a.pharmFormId;
        return c != 0 ? c : ObjectTools.compare(pharmFormSource, a.pharmFormSource);
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
