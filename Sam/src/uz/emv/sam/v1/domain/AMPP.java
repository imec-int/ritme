package uz.emv.sam.v1.domain;

import org.jetbrains.annotations.NotNull;
import uz.emv.sam.v1.ObjectTools;

import javax.persistence.*;
import java.util.Date;

/**
 * User: simbre1
 * Date: 16/09/13
 */
@Entity
@Table(name = "b#samAmpp",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"amppId", "amppSource"}))
public class AMPP implements Comparable<AMPP>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 7, nullable = false)
    private int amppId;

    @Enumerated(EnumType.STRING)
    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    private Source amppSource;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ampId", referencedColumnName = "ampId", nullable = false),
            @JoinColumn(name = "ampSource", referencedColumnName = "ampSource", nullable = false),
            @JoinColumn(name = "contentQuantity", referencedColumnName = "contentQuantity", nullable = false),
            @JoinColumn(name = "contentUnit", referencedColumnName = "contentUnit", nullable = false)
    })
    private AMPIntermediatePackage ampIntermediatePackage;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "vmpId", referencedColumnName = "vmpId", nullable = false),
            @JoinColumn(name = "vmpSource", referencedColumnName = "vmpSource", nullable = false),
            @JoinColumn(name = "treatmentDurationCatCv", referencedColumnName = "treatmentDurationCatCv", nullable = false),
            @JoinColumn(name = "treatmentDurationCatSource", referencedColumnName = "treatmentDurationCatSource", nullable = false)
    })
    private VMPP vmpp;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "distributorId", referencedColumnName = "companyId"),
            @JoinColumn(name = "distributorSource", referencedColumnName = "companySource")
    })
    private Company company;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "amppIdMaxPackSize", referencedColumnName = "amppId"),
            @JoinColumn(name = "amppSourceMaxPackSize", referencedColumnName = "amppSource")
    })
    private AMPP amppMaxPackSize;

    @Column(length = 4, nullable = false)
    private int contentMultiplier = 1;

    @Column(precision = 12, scale = 3, nullable = false)
    private double totalPackSizeValue = 0;

    @Column(nullable = false)
    private boolean prescriptionInd = false;

    @Column(length = 9)
    private String socsecReimbCv;

    @Column
    private boolean cheapest = false;

    @Column
    private Date inSupply;

    @Column
    private Date availability;

    @Column
    private Date initDate;

    @Column
    private Date closeDate;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    public AMPP(){
    }

    public void setAmppId(int ampppId){
        this.amppId = ampppId;
    }

    public int getAmppId(){
        return amppId;
    }

    public void setNameId(long nameId){
        this.nameId = nameId;
    }

    public long getNameId(){
        return nameId;
    }

    public void setAMPIntermediatePackage(AMPIntermediatePackage ampIntermediatePackage){
        this.ampIntermediatePackage = ampIntermediatePackage;
    }

    public AMPIntermediatePackage getAMPIntermediatePackage(){
        return ampIntermediatePackage;
    }

    public void setVMPP(VMPP vmpp){
        this.vmpp = vmpp;
    }

    public VMPP getVMPP(){
        return vmpp;
    }

    public void setCompany(Company company){
        this.company = company;
    }

    public Company getCompany(){
        return company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getContentMultiplier() {
        return contentMultiplier;
    }

    public void setContentMultiplier(int contentMultiplier) {
        this.contentMultiplier = contentMultiplier;
    }

    public double getTotalPackSizeValue() {
        return totalPackSizeValue;
    }

    public void setTotalPackSizeValue(double totalPackSizeValue) {
        this.totalPackSizeValue = totalPackSizeValue;
    }

    public AMPP getAmppMaxPackSize() {
        return amppMaxPackSize;
    }

    public void setAmppMaxPackSize(AMPP amppMaxPackSize) {
        this.amppMaxPackSize = amppMaxPackSize;
    }

    public boolean getPrescriptionInd() {
        return prescriptionInd;
    }

    public void setPrescriptionInd(boolean prescriptionInd) {
        this.prescriptionInd = prescriptionInd;
    }

    public String getSocsecReimbCv() {
        return socsecReimbCv;
    }

    public void setSocsecReimbCv(String socsecReimbCv) {
        this.socsecReimbCv = SAMTools.intern(socsecReimbCv);
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

    public boolean getCheapest() {
        return cheapest;
    }

    public void setCheapest(boolean cheapest) {
        this.cheapest = cheapest;
    }

    public Date getInSupply() {
        return inSupply;
    }

    public void setInSupply(Date inSupply) {
        this.inSupply = inSupply;
    }

    public Date getAvailability() {
        return availability;
    }

    public void setAvailability(Date availability) {
        this.availability = availability;
    }

    public Source getAmppSource() {
        return amppSource;
    }

    public void setAmppSource(Source amppSource) {
        if (amppSource != null) {
            this.amppSource = amppSource;
        }
        this.amppSource = Source.SAM;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }

        AMPP a = (AMPP)o;
        return amppId == a.amppId
                && ObjectTools.equals(amppSource, a.amppSource);
    }

    @Override
    public int hashCode(){
        return amppId;
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final AMPP a) {
        int c = amppId - a.amppId;
        return c != 0 ? c : ObjectTools.compare(amppSource, a.amppSource);
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
