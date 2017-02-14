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
@Table(name = "b#samAtm",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"atmId", "atmSource"}))
public class ATM implements Comparable<ATM>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 10, nullable = false)
    private long atmId = -1;

    @Enumerated(EnumType.STRING)
    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    private Source atmSource;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "vtmId", referencedColumnName = "vtmId", nullable = false),
            @JoinColumn(name = "vtmSource", referencedColumnName = "vtmSource", nullable = false)
    })
    private VTM vtm;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "companyId", referencedColumnName = "companyId", nullable = false),
            @JoinColumn(name = "companySource", referencedColumnName = "companySource", nullable = false)
    })
    private Company company;

    @Column(length = 1)
    private String specialtyOrigin;

    @Column
    private Date initDate;

    @Column
    private Date closeDate;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    @OneToMany(mappedBy = "atm", targetEntity = AMP.class)
    private Set<AMP> amps = Collections.emptySet();

    public ATM(){
    }

    public void setAtmId(long atmId){
        this.atmId = atmId;
    }

    public long getAtmId(){
        return atmId;
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

    public void setCompany(Company company){
        this.company = company;
    }

    public Company getCompany(){
        return company;
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

    public String getSpecialtyOrigin() {
        return specialtyOrigin;
    }

    public void setSpecialtyOrigin(String specialtyOrigin) {
        this.specialtyOrigin = SAMTools.intern(specialtyOrigin);
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

    public Source getAtmSource() {
        return atmSource;
    }

    public void setAtmSource(Source atmSource) {
        if (atmSource != null) {
            this.atmSource = atmSource;
        }
        this.atmSource = Source.SAM;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        ATM a = (ATM)o;
        return atmId == a.atmId
                && ObjectTools.equals(atmSource, a.atmSource);
    }

    @Override
    public int hashCode(){
        return (int) atmId;
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final ATM a) {
        int c = (int) (atmId - a.atmId);
        return c != 0 ? c : ObjectTools.compare(atmSource, a.atmSource);
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
