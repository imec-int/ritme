package uz.emv.sam.v1.domain;


import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * User: simbre1
 * Date: 16/09/13
 */
@Entity
@Table(name = "b#samVirtualIngredientStrengt",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"casId", "casSource", "vtmId", "vtmSource", "vmpId", "vmpSource"}))
public class VirtualIngredientStrength implements Comparable<VirtualIngredientStrength>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "casId", referencedColumnName = "casId", nullable = false),
            @JoinColumn(name = "casSource", referencedColumnName = "casSource", nullable = false),
            @JoinColumn(name = "vtmId", referencedColumnName = "vtmId", nullable = false),
            @JoinColumn(name = "vtmSource", referencedColumnName = "vtmSource", nullable = false)
    })
    private VirtualIngredient virtualIngredient;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "vmpId", referencedColumnName = "vmpId", nullable = false),
            @JoinColumn(name = "vmpSource", referencedColumnName = "vmpSource", nullable = false)
    })
    private VMP vmp;

    @Column(precision = 12, scale = 3, nullable = false)
    private Double strengthQuantity;

    @Column(precision = 12, scale = 3)
    private Double strengthQuantity2;

    @Column(length = 15)
    private String strengthUnit;

    @Column(precision = 12, scale = 3)
    private Double strengthDenomQuantity;

    @Column(length = 15)
    private String strengthDenomUnit;

    public VirtualIngredientStrength(){
    }

    public void setVirtualIngredient(VirtualIngredient virtualIngredient){
        this.virtualIngredient = virtualIngredient;
    }

    public VirtualIngredient getVirtualIngredient(){
        return virtualIngredient;
    }

    public void setVMP(VMP vmp){
        this.vmp = vmp;
    }

    public VMP getVMP(){
        return vmp;
    }

    public Double getStrengthQuantity() {
        return strengthQuantity;
    }

    public void setStrengthQuantity(Double strengthQuantity) {
        this.strengthQuantity = strengthQuantity;
    }

    public Double getStrengthQuantity2() {
        return strengthQuantity2;
    }

    public void setStrengthQuantity2(Double strengthQuantity2) {
        this.strengthQuantity2 = strengthQuantity2;
    }

    public String getStrengthUnit() {
        return strengthUnit;
    }

    public void setStrengthUnit(String strengthUnit) {
        this.strengthUnit = SAMTools.intern(strengthUnit);
    }

    public Double getStrengthDenomQuantity() {
        return strengthDenomQuantity;
    }

    public void setStrengthDenomQuantity(Double strengthDenomQuantity) {
        this.strengthDenomQuantity = strengthDenomQuantity;
    }

    public String getStrengthDenomUnit() {
        return strengthDenomUnit;
    }

    public void setStrengthDenomUnit(String strengthDenomUnit) {
        this.strengthDenomUnit = SAMTools.intern(strengthDenomUnit);
    }

    @Override
    public boolean equals(Object o){
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        VirtualIngredientStrength a = (VirtualIngredientStrength)o;
        return virtualIngredient.equals(a.virtualIngredient)
                && vmp.equals(a.vmp);
    }

    @Override
    public int hashCode(){
        return 31 * virtualIngredient.hashCode()
                + vmp.hashCode();
    }

    @Override
    public int compareTo(@NotNull final VirtualIngredientStrength a) {
        int c = virtualIngredient.compareTo(a.virtualIngredient);
        return c != 0 ? c : vmp.compareTo(a.vmp);
    }

    @Override
    public String toString(){
        if(strengthQuantity == null
                || strengthUnit == null){
            return super.toString();
        }
        StringBuilder sb = new StringBuilder(virtualIngredient.toString());

        sb.append(" ").append(strengthQuantity);
        if(strengthQuantity2 != null && strengthQuantity2 > 0){
            sb.append(" a ").append(strengthQuantity2);
        }
        sb.append(strengthUnit);

        if(strengthDenomQuantity != null && strengthDenomQuantity != 0
                && !StringUtils.isEmpty(strengthDenomUnit)
                && !(strengthUnit.equals(strengthDenomUnit) && strengthDenomQuantity == 1)){
            sb.append(" / ").append(strengthDenomQuantity).append(strengthDenomUnit);
        }

        return sb.toString();
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
