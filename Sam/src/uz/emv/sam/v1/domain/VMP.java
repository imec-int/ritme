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
@Table(name = "b#samVmp",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"vmpId", "vmpSource"}))
public class VMP implements Comparable<VMP>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 7, nullable = false)
    private int vmpId;

    @Enumerated(EnumType.STRING)
    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    private Source vmpSource;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "vtmId", referencedColumnName = "vtmId", nullable = false),
            @JoinColumn(name = "vtmSource", referencedColumnName = "vtmSource", nullable = false)
    })
    private VTM vtm;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "applicationCv", referencedColumnName = "applicationCv", nullable = false),
            @JoinColumn(name = "applicationSource", referencedColumnName = "applicationSource", nullable = false)
    })
    private Application application;

    @Column(nullable = false)
    private boolean sequentialInd = false;

    @Column(length = 1, nullable = false)
    private String doseFormType;

    @Column(precision = 12, scale = 3)
    private Double administrationQuantity;

    @Column(length = 5)
    private String administrationUnit;

    @Column(length = 4)
    private Integer administrationMultiplier;

    @Column(length = 10)
    private Long hyrId;

    @Column(length = 1)
    private Integer noInn;

    @Column(length = 2)
    private Integer noSwitch;

    @Column(precision = 12, scale = 3)
    private Double definedDailyDoseValue;

    @Column(length = 5)
    private String definedDailyDoseUnit;

    @Column(nullable = false)
    private boolean blackTriangleInd = false;

    @Column
    private Date initDate;

    @Column
    private Date closeDate;

    @Column(length = 6)
    private String wadaCv;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    @OneToMany(mappedBy = "vmp", targetEntity = AMP.class)
    private Set<AMP> amps = Collections.emptySet();

    @OneToMany(mappedBy = "vmp", targetEntity = VMPP.class)
    private Set<VMPP> vmpps = Collections.emptySet();

    @OneToMany(mappedBy = "vmp", targetEntity = VirtualIngredientStrength.class)
    private Set<VirtualIngredientStrength> virtualIngredientStrengths = Collections.emptySet();

    @OneToMany(mappedBy = "parent", targetEntity = VMPComb.class)
    private Set<VMPComb> childCombinations = Collections.emptySet();

    @OneToMany(mappedBy = "child", targetEntity = VMPComb.class)
    private Set<VMPComb> parentCombinations = Collections.emptySet();

    public VMP(){
    }

    public void setVmpId(int vmpId){
        this.vmpId = vmpId;
    }

    public int getVmpId(){
        return vmpId;
    }

    public void setNameId(long nameId){
        this.nameId = nameId;
    }

    public long getNameId(){
        return nameId;
    }

    public void setVTM(VTM vtm){
        this.vtm = vtm;
    }

    public VTM getVTM(){
        return vtm;
    }

    public void setApplication(Application application){
        this.application = application;
    }

    public Application getApplication(){
        return application;
    }

    public Set<AMP> getAMPs(){
        return amps;
    }

    public void setAmps(@NotNull Set<AMP> amps) {
        this.amps = amps;
    }

    public Set<VMPComb> getParentCombinations(){
        return parentCombinations;
    }

    public void setParentCombinations(@NotNull Set<VMPComb> parentCombinations) {
        this.parentCombinations = parentCombinations;
    }

    public Set<VMPComb> getChildCombinations(){
        return childCombinations;
    }

    public void setChildCombinations(@NotNull Set<VMPComb> childCombinations) {
        this.childCombinations = childCombinations;
    }

    public Set<VMPP> getVMPPs(){
        return vmpps;
    }

    public void setVmpps(@NotNull Set<VMPP> vmpps) {
        this.vmpps = vmpps;
    }

    public Set<VirtualIngredientStrength> getVirtualIngredientStrengths(){
        return virtualIngredientStrengths;
    }

    public void setVirtualIngredientStrengths(@NotNull Set<VirtualIngredientStrength> virtualIngredientStrengths) {
        this.virtualIngredientStrengths = virtualIngredientStrengths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getSequentialInd() {
        return sequentialInd;
    }

    public void setSequentialInd(boolean sequentialInd) {
        this.sequentialInd = sequentialInd;
    }

    public String getDoseFormType() {
        return doseFormType;
    }

    public void setDoseFormType(String doseFormType) {
        this.doseFormType = SAMTools.intern(doseFormType);
    }

    public Double getAdministrationQuantity() {
        return administrationQuantity;
    }

    public void setAdministrationQuantity(Double administrationQuantity) {
        this.administrationQuantity = administrationQuantity;
    }

    public String getAdministrationUnit() {
        return administrationUnit;
    }

    public void setAdministrationUnit(String administrationUnit) {
        this.administrationUnit = SAMTools.intern(administrationUnit);
    }

    public Integer getAdministrationMultiplier() {
        return administrationMultiplier;
    }

    public void setAdministrationMultiplier(Integer administrationMultiplier) {
        this.administrationMultiplier = administrationMultiplier;
    }

    public Long getHyrId() {
        return hyrId;
    }

    public void setHyrId(Long hyrId) {
        this.hyrId = hyrId;
    }

    public Integer getNoInn() {
        return noInn;
    }

    public void setNoInn(Integer noInn) {
        this.noInn = noInn;
    }

    public Integer getNoSwitch() {
        return noSwitch;
    }

    public void setNoSwitch(Integer noSwitch) {
        this.noSwitch = noSwitch;
    }

    public Double getDefinedDailyDoseValue() {
        return definedDailyDoseValue;
    }

    public void setDefinedDailyDoseValue(Double definedDailyDoseValue) {
        this.definedDailyDoseValue = definedDailyDoseValue;
    }

    public String getDefinedDailyDoseUnit() {
        return definedDailyDoseUnit;
    }

    public void setDefinedDailyDoseUnit(String definedDailyDoseUnit) {
        this.definedDailyDoseUnit = SAMTools.intern(definedDailyDoseUnit);
    }

    public boolean getBlackTriangleInd() {
        return blackTriangleInd;
    }

    public void setBlackTriangleInd(boolean blackTriangleInd) {
        this.blackTriangleInd = blackTriangleInd;
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

    public String getWadaCv() {
        return wadaCv;
    }

    public void setWadaCv(String wadaCv) {
        this.wadaCv = SAMTools.intern(wadaCv);
    }

    public Source getVmpSource() {
        return vmpSource;
    }

    public void setVmpSource(Source vmpSource) {
        if (vmpSource != null) {
            this.vmpSource = vmpSource;
        }
        this.vmpSource = Source.SAM;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        VMP a = (VMP)o;
        return vmpId == a.vmpId
                && ObjectTools.equals(vmpSource, a.vmpSource);
    }

    @Override
    public int hashCode(){
        return vmpId;
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final VMP a) {
        int c = vmpId - a.vmpId;
        return c != 0 ? c : ObjectTools.compare(vmpSource, a.vmpSource);
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
