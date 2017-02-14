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
@Table(name = "b#samCompany",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"companyId", "companySource"}))
public class Company implements Comparable<Company>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 5, nullable = false)
    private Integer companyId;

    @Enumerated(EnumType.STRING)
    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    private Source companySource;

    @Column(nullable = false)
    private boolean pipInd = false;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    @OneToMany(mappedBy = "company", targetEntity = ATM.class)
    private Set<ATM> atms = Collections.emptySet();

    @OneToMany(mappedBy = "company", targetEntity = AMP.class)
    private Set<AMP> amps = Collections.emptySet();

    @OneToMany(mappedBy = "company", targetEntity = AMPP.class)
    private Set<AMPP> ampps = Collections.emptySet();

    public Company(){
    }

    public void setCompanyId(Integer companyId){
        this.companyId = companyId;
    }

    public Integer getCompanyId(){
        return companyId;
    }

    public void setNameId(long nameId){
        this.nameId = nameId;
    }

    public long getNameId(){
        return nameId;
    }

    public Set<ATM> getATMs(){
        return atms;
    }

    public void setAtms(@NotNull Set<ATM> atms) {
        this.atms = atms;
    }

    public Set<AMP> getAMPs(){
        return amps;
    }

    public void setAmps(@NotNull Set<AMP> amps) {
        this.amps = amps;
    }

    public Set<AMPP> getAMPPs(){
        return ampps;
    }

    public void setAmpps(@NotNull Set<AMPP> ampps) {
        this.ampps = ampps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getPipInd() {
        return pipInd;
    }

    public void setPipInd(boolean pipInd) {
        this.pipInd = pipInd;
    }

    public Source getCompanySource() {
        return companySource;
    }

    public void setCompanySource(Source companySource) {
        if (companySource != null) {
            this.companySource = companySource;
        }
        this.companySource = Source.SAM;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        Company a = (Company)o;
        return companyId.equals(a.companyId)
                && ObjectTools.equals(companySource, a.companySource);
    }

    @Override
    public int hashCode(){
        return companyId.hashCode();
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final Company a) {
        int c = companyId.compareTo(a.companyId);
        return c != 0 ? c : ObjectTools.compare(companySource, a.companySource);
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
