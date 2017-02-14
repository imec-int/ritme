package uz.emv.sam.v1.domain;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.Collections;
import java.util.Set;

/**
 * User: simbre1
 * Date: 16/09/13
 */
@Entity
@Table(name = "b#samVtmIngredient",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"casId", "casSource", "vtmId", "vtmSource"}))
public class VirtualIngredient implements Comparable<VirtualIngredient>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "casId", referencedColumnName = "casId", nullable = false),
            @JoinColumn(name = "casSource", referencedColumnName = "casSource", nullable = false)
    })
    private Substance substance;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "vtmId", referencedColumnName = "vtmId", nullable = false),
            @JoinColumn(name = "vtmSource", referencedColumnName = "vtmSource", nullable = false)
    })
    private VTM vtm;

    @Column(nullable = false)
    private int rank = 1;

    @OneToMany(mappedBy = "virtualIngredient", targetEntity = VirtualIngredientStrength.class)
    private Set<VirtualIngredientStrength> virtualIngredientStrengths = Collections.emptySet();

    public VirtualIngredient(){
    }

    public void setSubstance(Substance substance){
        this.substance = substance;
    }

    public Substance getSubstance(){
        return substance;
    }

    public void setVTM(VTM vtm){
        this.vtm = vtm;
    }

    public VTM getVTM(){
        return vtm;
    }

    public void setRank(int rank){
        this.rank = rank;
    }

    public int getRank(){
        return this.rank;
    }

    public Set<VirtualIngredientStrength> getVirtualIngredientStrengths(){
        return virtualIngredientStrengths;
    }

    public void setVirtualIngredientStrengths(@NotNull Set<VirtualIngredientStrength> virtualIngredientStrengths) {
        this.virtualIngredientStrengths = virtualIngredientStrengths;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        VirtualIngredient a = (VirtualIngredient)o;
        return substance.equals(a.substance)
                && vtm.equals(a.vtm)
                && rank == a.rank;
    }

    @Override
    public int hashCode(){
        return substance.hashCode()
                + vtm.hashCode();
    }

    @Override
    public int compareTo(@NotNull final VirtualIngredient a) {
        int c = rank - a.rank;
        if(c != 0){
            return c;
        }
        c = vtm.compareTo(a.vtm);
        return c != 0 ? c : substance.compareTo(a.substance);
    }

    @Override
    public String toString(){
        if(substance == null){
            return super.toString();
        }
        return rank +": "+ substance.toString();
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
