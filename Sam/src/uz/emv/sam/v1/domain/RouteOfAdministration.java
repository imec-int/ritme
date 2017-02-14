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
@Table(name = "b#samRouteOfAdministration",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"routeAdmCv", "routeAdmSource"}))
public class RouteOfAdministration implements Comparable<RouteOfAdministration>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 15, nullable = false)
    private String routeAdmCv;

    @Enumerated(EnumType.STRING)
    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    private Source routeAdmSource;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "applicationCv", referencedColumnName = "applicationCv", nullable = false),
            @JoinColumn(name = "applicationSource", referencedColumnName = "applicationSource", nullable = false)
    })
    private Application application;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    @OneToMany(mappedBy = "routeOfAdministration", targetEntity = AMP.class)
    private Set<AMP> amps = Collections.emptySet();

    public RouteOfAdministration(){
    }

    public void setRouteAdmCv(String routeAdmCv){
        this.routeAdmCv = routeAdmCv;
    }

    public String getRouteAdmCv(){
        return routeAdmCv;
    }

    public void setNameId(long nameId){
        this.nameId = nameId;
    }

    public long getNameId(){
        return nameId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Source getRouteAdmSource() {
        return routeAdmSource;
    }

    public void setRouteAdmSource(Source routeAdmSource) {
        if (routeAdmSource != null) {
            this.routeAdmSource = routeAdmSource;
        }
        this.routeAdmSource = Source.SAM;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        RouteOfAdministration a = (RouteOfAdministration)o;
        return routeAdmCv.equals(a.routeAdmCv)
                && ObjectTools.equals(routeAdmSource, a.routeAdmSource);
    }

    @Override
    public int hashCode(){
        return routeAdmCv.hashCode();
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final RouteOfAdministration a) {
        int c = routeAdmCv.compareTo(a.routeAdmCv);
        return c != 0 ? c : ObjectTools.compare(routeAdmSource, a.routeAdmSource);
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
