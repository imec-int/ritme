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
@Table(name="b#samAmp",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames={"ampId", "ampSource"}))
public class AMP implements Comparable<AMP>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 10, nullable = false)
    private long ampId = -1;

    @Enumerated(EnumType.STRING)
    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    private Source ampSource;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "vmpId", referencedColumnName = "vmpId", nullable = false),
            @JoinColumn(name = "vmpSource", referencedColumnName = "vmpSource", nullable = false)
    })
    private VMP vmp;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "atmId", referencedColumnName = "atmId", nullable = false),
            @JoinColumn(name = "atmSource", referencedColumnName = "atmSource", nullable = false)
    })
    private ATM atm;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "admFormId", referencedColumnName = "admFormId"),
            @JoinColumn(name = "admFormSource", referencedColumnName = "admFormSource")
    })
    private AdministrationForm administrationForm;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "pharmFormId", referencedColumnName = "pharmFormId", nullable = false),
            @JoinColumn(name = "pharmFormSource", referencedColumnName = "pharmFormSource", nullable = false)
    })
    private PharmaceuticalForm pharmaceuticalForm;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "routeAdmCv", referencedColumnName = "routeAdmCv", nullable = false),
            @JoinColumn(name = "routeAdmSource", referencedColumnName = "routeAdmSource", nullable = false)
    })
    private RouteOfAdministration routeOfAdministration;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "atcCv", referencedColumnName = "atcCv", nullable = true),
            @JoinColumn(name = "atcSource", referencedColumnName = "atcSource", nullable = true)
    })
    private ATC atc;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "distributorId", referencedColumnName = "companyId"),
            @JoinColumn(name = "distributorSource", referencedColumnName = "companySource")
    })
    private Company company;

    @Column(length = 90)
    private String galenicFormTxt;

    @Column(length = 30)
    private String dimensions;

    @Column(length = 1)
    private String dopCv;

    @Column(length = 30)
    private String registSpec;

    @Column(length = 50)
    private String descriptSpec;

    @Column(length = 30)
    private String duration;

    @Column(nullable = false)
    private boolean flatRateInd = false;

    @Column
    private Date initDate;

    @Column
    private Date closeDate;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    @OneToMany(mappedBy = "parent", targetEntity = AMPComb.class)
    private Set<AMPComb> childCombinations = Collections.emptySet();

    @OneToMany(mappedBy = "child", targetEntity = AMPComb.class)
    private Set<AMPComb> parentCombinations = Collections.emptySet();

    @OneToMany(mappedBy = "amp", targetEntity = AMPIntermediatePackage.class)
    private Set<AMPIntermediatePackage> ampIntermediatePackages = Collections.emptySet();

    @OneToMany(mappedBy = "amp", targetEntity = ActualIngredientStrength.class)
    private Set<ActualIngredientStrength> actualIngredientStrengths = Collections.emptySet();

    public AMP(){
    }

    public void setAmpId(long ampId){
        this.ampId = ampId;
    }

    public long getAmpId(){
        return ampId;
    }

    public void setNameId(long nameId){
        this.nameId = nameId;
    }

    public long getNameId(){
        return nameId;
    }

    public void setATM(ATM atm){
        this.atm = atm;
    }

    public ATM getATM(){
        return atm;
    }

    public void setPharmaceuticalForm(PharmaceuticalForm pharmaceuticalForm){
        this.pharmaceuticalForm = pharmaceuticalForm;
    }

    public PharmaceuticalForm getPharmaceuticalForm(){
        return pharmaceuticalForm;
    }

    public void setATC(ATC atc){
        this.atc = atc;
    }

    public ATC getATC(){
        return atc;
    }

    public void setVMP(VMP vmp){
        this.vmp = vmp;
    }

    public VMP getVMP(){
        return vmp;
    }

    public void setCompany(Company company){
        this.company = company;
    }

    public Company getCompany(){
        return company;
    }

    public void setAdministrationForm(AdministrationForm administrationForm){
        this.administrationForm = administrationForm;
    }

    public AdministrationForm getAdministrationForm(){
        return administrationForm;
    }

    public void setRouteOfAdministration(RouteOfAdministration routeOfAdministration){
        this.routeOfAdministration = routeOfAdministration;
    }

    public RouteOfAdministration getRouteOfAdministration(){
        return routeOfAdministration;
    }

    public Set<AMPComb> getChildCombinations(){
        return childCombinations;
    }

    public void setChildCombinations(@NotNull Set<AMPComb> childCombinations) {
        this.childCombinations = childCombinations;
    }

    public Set<AMPComb> getParentCombinations(){
        return parentCombinations;
    }

    public void setParentCombinations(@NotNull Set<AMPComb> parentCombinations) {
        this.parentCombinations = parentCombinations;
    }

    public Set<AMPIntermediatePackage> getAMPIntermediatePackages(){
        return ampIntermediatePackages;
    }

    public void setAmpIntermediatePackages(@NotNull Set<AMPIntermediatePackage> ampIntermediatePackages) {
        this.ampIntermediatePackages = ampIntermediatePackages;
    }

    public Set<ActualIngredientStrength> getActualIngredientStrengths(){
        return actualIngredientStrengths;
    }

    public void setActualIngredientStrengths(@NotNull Set<ActualIngredientStrength> actualIngredientStrengths){
        this.actualIngredientStrengths = actualIngredientStrengths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGalenicFormTxt() {
        return galenicFormTxt;
    }

    public void setGalenicFormTxt(String galenicFormTxt) {
        this.galenicFormTxt = SAMTools.intern(galenicFormTxt);
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getDopCv() {
        return dopCv;
    }

    public void setDopCv(String dopCv) {
        this.dopCv = dopCv;
    }

    public String getRegistSpec() {
        return registSpec;
    }

    public void setRegistSpec(String registSpec) {
        this.registSpec = registSpec;
    }

    public String getDescriptSpec() {
        return descriptSpec;
    }

    public void setDescriptSpec(String descriptSpec) {
        this.descriptSpec = descriptSpec;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean getFlatRateInd() {
        return flatRateInd;
    }

    public void setFlatRateInd(boolean flatRateInd) {
        this.flatRateInd = flatRateInd;
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

    public Source getAmpSource() {
        return ampSource;
    }

    public void setAmpSource(Source ampSource) {
        if (ampSource != null) {
            this.ampSource = ampSource;
        }
        this.ampSource = Source.SAM;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        AMP a = (AMP)o;
        return ampId == a.ampId
                && ObjectTools.equals(ampSource, a.ampSource);
    }

    @Override
    public int hashCode(){
        return (int) ampId;
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final AMP a) {
        int c = (int) (ampId - a.ampId);
        return c != 0 ? c : ObjectTools.compare(ampSource, a.ampSource);
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

