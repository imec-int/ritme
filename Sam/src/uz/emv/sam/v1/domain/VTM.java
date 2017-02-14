package uz.emv.sam.v1.domain;

import org.jetbrains.annotations.NotNull;
import uz.emv.sam.v1.ObjectTools;

import javax.persistence.*;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * User: simbre1
 * Date: 16/09/13
 */
@Entity
@Table(name = "b#samVtm",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"vtmId", "vtmSource"}))
public class VTM implements Comparable<VTM> {

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 10, nullable = false)
    private long vtmId;

    @Enumerated(EnumType.STRING)
    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    private Source vtmSource;

    @Column(nullable = false)
    private boolean plus3Ind = false;

    @Column(nullable = false)
    private boolean cheapInd = false;

    @Column(nullable = false)
    private boolean whoListInd = false;

    @Column(length = 12)
    private String whoListRef;

    @Column(nullable = false)
    private boolean educListInd = false;

    @Column(length = 12)
    private String educListGroup;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    @Column
    private Date initDate;

    @Column
    private Date closeDate;

    @OneToMany(mappedBy = "vtm", targetEntity = VirtualIngredient.class)
    private Set<VirtualIngredient> virtualIngredients = Collections.emptySet();

    @OneToMany(mappedBy = "vtm", targetEntity = ATM.class)
    private Set<ATM> atms = Collections.emptySet();

    @OneToMany(mappedBy = "vtm", targetEntity = VMP.class)
    private Set<VMP> vmps = Collections.emptySet();

    public VTM(){
    }

    public void setVtmId(long vtmId){
        this.vtmId = vtmId;
    }

    public long getVtmId(){
        return vtmId;
    }

    public void setNameId(long nameId){
        this.nameId = nameId;
    }

    public long getNameId(){
        return nameId;
    }

    public Set<VirtualIngredient> getVirtualIngredients(){
        return virtualIngredients;
    }

    public void setVirtualIngredients(@NotNull Set<VirtualIngredient> virtualIngredients) {
        this.virtualIngredients = virtualIngredients;
    }

    public Set<ATM> getATMs(){
        return atms;
    }

    public void setAtms(@NotNull Set<ATM> atms) {
        this.atms = atms;
    }

    public Set<VMP> getVMPs(){
        return vmps;
    }

    public void setVmps(@NotNull Set<VMP> vmps) {
        this.vmps = vmps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public boolean getPlus3Ind() {
        return plus3Ind;
    }

    public void setPlus3Ind(boolean plus3Ind) {
        this.plus3Ind = plus3Ind;
    }

    public boolean getCheapInd() {
        return cheapInd;
    }

    public void setCheapInd(boolean cheapInd) {
        this.cheapInd = cheapInd;
    }

    public boolean getWhoListInd() {
        return whoListInd;
    }

    public void setWhoListInd(boolean whoListInd) {
        this.whoListInd = whoListInd;
    }

    public String getWhoListRef() {
        return whoListRef;
    }

    public void setWhoListRef(String whoListRef) {
        this.whoListRef = whoListRef;
    }

    public boolean getEducListInd() {
        return educListInd;
    }

    public void setEducListInd(boolean educListInd) {
        this.educListInd = educListInd;
    }

    public String getEducListGroup() {
        return educListGroup;
    }

    public void setEducListGroup(String educListGroup) {
        this.educListGroup = educListGroup;
    }

    public Source getVtmSource() {
        return vtmSource;
    }

    public void setVtmSource(Source vtmSource) {
        if (vtmSource != null) {
            this.vtmSource = vtmSource;
        }
        this.vtmSource = Source.SAM;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        VTM a = (VTM)o;
        return vtmId == a.vtmId
                && ObjectTools.equals(vtmSource, a.vtmSource);
    }

    @Override
    public int hashCode(){
        return (int) vtmId;
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final VTM a) {
        int c = (int) (vtmId - a.vtmId);
        return c != 0 ? c : ObjectTools.compare(vtmSource, a.vtmSource);
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
