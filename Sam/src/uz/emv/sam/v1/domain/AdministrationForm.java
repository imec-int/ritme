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
@Table(name="b#samAdministrationForm",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames={"admFormId", "admFormSource"}))
public class AdministrationForm implements Comparable<AdministrationForm> {

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 5, nullable = false)
    private int admFormId = -1;


    @Enumerated(EnumType.STRING)
    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    private Source admFormSource;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    @OneToMany(mappedBy = "administrationForm", targetEntity = AMP.class)
    private Set<AMP> amps = Collections.emptySet();

    public AdministrationForm(){
    }

    public void setAdmFormId(int admFormId){
        this.admFormId = admFormId;
    }

    public int getAdmFormId(){
        return admFormId;
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

    public Source getAdmFormSource() {
        return admFormSource;
    }

    public void setAdmFormSource(Source admFormSource) {
        if (admFormSource != null) {
            this.admFormSource = admFormSource;
        }
        this.admFormSource = Source.SAM;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        AdministrationForm a = (AdministrationForm)o;
        return admFormId == a.admFormId
                && ObjectTools.equals(admFormSource, a.admFormSource);
    }

    @Override
    public int hashCode(){
        return admFormId;
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final AdministrationForm a) {
        int c = admFormId - a.admFormId;
        return c != 0 ? c : ObjectTools.compare(admFormSource, a.admFormSource);
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
