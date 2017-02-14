package uz.emv.sam.v1.domain;

import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

/**
 * User: simbre1
 * Date: 16/09/13
 */
@Entity
@Table(name="b#samActualIngredientStrength",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames={"ampId", "ampSource", "casId", "casSource"}))
public class ActualIngredientStrength implements Comparable<ActualIngredientStrength>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="ampId", referencedColumnName="ampId", nullable = false),
            @JoinColumn(name="ampSource", referencedColumnName="ampSource", nullable = false)
    })
    private AMP amp;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="casId", referencedColumnName = "casId", nullable = false),
            @JoinColumn(name="casSource", referencedColumnName = "casSource", nullable = false)
    })
    private Substance substance;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "virtualIngredientCasId", referencedColumnName = "casId"),
            @JoinColumn(name = "virtualIngredientCasSource", referencedColumnName = "casSource")
    })
    private Substance replacementSubstance;

    @Column(nullable = false)
    private boolean excInd = false;

    @Column(precision = 12, scale = 3)
    private Double strengthQuantity;

    @Column(precision = 12, scale = 3)
    private Double strengthQuantity2;

    @Column(length = 15)
    private String strengthUnit;

    @Column(precision = 12, scale = 3)
    private Double strengthDenomQuantity;

    @Column(length = 15)
    private String strengthDenomUnit;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    public ActualIngredientStrength(){
    }

    public void setAMP(AMP amp){
        this.amp = amp;
    }

    public AMP getAMP(){
        return amp;
    }

    public void setSubstance(Substance substance){
        this.substance = substance;
    }

    public Substance getSubstance(){
        return substance;
    }

    public Substance getReplacementSubstance() {
        return replacementSubstance;
    }

    public void setReplacementSubstance(Substance replacementSubstance) {
        this.replacementSubstance = replacementSubstance;
    }

    public void setNameId(long nameId){
        this.nameId = nameId;
    }

    public long getNameId(){
        return nameId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getExcInd() {
        return excInd;
    }

    public void setExcInd(boolean excInd) {
        this.excInd = excInd;
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
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }

        ActualIngredientStrength a = (ActualIngredientStrength)o;
        return amp.equals(a.amp)
                && substance.equals(a.substance);
    }

    @Override
    public int hashCode(){
        return 31 * amp.hashCode() + substance.hashCode();
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final ActualIngredientStrength a) {
        int c = amp.compareTo(a.amp);
        return c != 0 ? c : substance.compareTo(a.substance);
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
