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
@Table(name = "b#samApplication",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"applicationCv", "applicationSource"}))
public class Application implements Comparable<Application>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 15, nullable = false)
    private String applicationCv;

    @Enumerated(EnumType.STRING)
    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    private Source applicationSource;

    @Column(length = 10, nullable = false)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    @OneToMany(mappedBy = "application", targetEntity = VMP.class)
    private Set<VMP> vmps = Collections.emptySet();

    @OneToMany(mappedBy = "application", targetEntity = RouteOfAdministration.class)
    private Set<RouteOfAdministration> routeOfAdministrations = Collections.emptySet();

    public Application(){
    }

    public void setApplicationCv(String applicationCv){
        this.applicationCv = applicationCv;
    }

    public String getApplicationCv(){
        return applicationCv;
    }

    public void setNameId(long nameId){
        this.nameId = nameId;
    }

    public long getNameId(){
        return nameId;
    }

    public Set<VMP> getVMPs(){
        return vmps;
    }

    public void setVmps(@NotNull Set<VMP> vmps) {
        this.vmps = vmps;
    }

    public Set<RouteOfAdministration> getRouteOfAdministrations(){
        return routeOfAdministrations;
    }

    public void setRouteOfAdministrations(@NotNull Set<RouteOfAdministration> routeOfAdministrations) {
        this.routeOfAdministrations = routeOfAdministrations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Source getApplicationSource() {
        return applicationSource;
    }

    public void setApplicationSource(Source applicationSource) {
        if (applicationSource != null) {
            this.applicationSource = applicationSource;
        }
        this.applicationSource = Source.SAM;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        Application a = (Application)o;
        return applicationCv.equals(a.applicationCv)
                && ObjectTools.equals(applicationSource, a.applicationSource);
    }

    @Override
    public int hashCode(){
        return applicationCv.hashCode();
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final Application a) {
        int c = applicationCv.compareTo(a.applicationCv);
        return c != 0 ? c : ObjectTools.compare(applicationSource, a.applicationSource);
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
