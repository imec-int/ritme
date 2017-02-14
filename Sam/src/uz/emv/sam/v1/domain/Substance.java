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
@Table(name = "b#samSubstance",
        catalog = SAMTools.DATABASE,
        uniqueConstraints = @UniqueConstraint(columnNames = {"casId", "casSource"}))
public class Substance implements Comparable<Substance>{

    @Id
    @Column(length = SAMTools.ID_LENGTH, nullable = false)
    private Long id;

    @Version
    @Column(name = "rowVersion")
    private long rowVersion = 0;

    @Column(length = 10, nullable = false)
    private long casId = -1;


    @Column(length = SAMTools.SOURCE_LENGTH, nullable = false)
    @Enumerated(EnumType.STRING)
    private Source casSource;

    @Column(length = 10)
    private Long casNr;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "baseFormCasId", referencedColumnName = "casId"),
            @JoinColumn(name = "baseFormCasSource", referencedColumnName = "casSource")
    })
    private Substance baseForm;

    @Column(nullable = false)
    private boolean excInd = false;

    @Column(nullable = false)
    private boolean activeBaseInd = false;

    @Column(length = 2)
    private String derivFormType;

    @Column(length = 10)
    private long nameId = -1;

    @Column(length = SAMTools.NAME_LENGTH)
    private String name;

    @OneToMany(mappedBy = "baseForm", targetEntity = Substance.class)
    private Set<Substance> derivates = Collections.emptySet();

    @OneToMany(mappedBy = "substance", targetEntity = VirtualIngredient.class)
    private Set<VirtualIngredient> virtualIngredients = Collections.emptySet();

    @OneToMany(mappedBy = "substance", targetEntity = ActualIngredientStrength.class)
    private Set<ActualIngredientStrength> actualIngredientStrengths = Collections.emptySet();

    @OneToMany(mappedBy = "replacementSubstance", targetEntity = ActualIngredientStrength.class)
    private Set<ActualIngredientStrength> replacementActualIngredientStrengths = Collections.emptySet();

    public Substance(){
    }

    public void setCasId(long casId){
        this.casId = casId;
    }

    public long getCasId(){
        return casId;
    }

    public void setNameId(long nameId){
        this.nameId = nameId;
    }

    public long getNameId(){
        return nameId;
    }

    public void setBaseForm(Substance baseForm){
        this.baseForm = baseForm;
    }

    public Substance getBaseForm(){
        return baseForm;
    }

    public Set<Substance> getDerivates(){
        return derivates;
    }

    public void setDerivates(@NotNull Set<Substance> derivates){
        this.derivates = derivates;
    }

    public Set<VirtualIngredient> getVirtualIngredients(){
        return virtualIngredients;
    }

    public void setVirtualIngredients(@NotNull Set<VirtualIngredient> virtualIngredients) {
        this.virtualIngredients = virtualIngredients;
    }

    public Set<ActualIngredientStrength> getActualIngredientStrengths(){
        return actualIngredientStrengths;
    }

    public void setActualIngredientStrengths(@NotNull Set<ActualIngredientStrength> actualIngredientStrengths) {
        this.actualIngredientStrengths = actualIngredientStrengths;
    }

    public Set<ActualIngredientStrength> getReplacementActualIngredientStrengths() {
        return replacementActualIngredientStrengths;
    }

    public void setReplacementActualIngredientStrengths(@NotNull Set<ActualIngredientStrength> replacementActualIngredientStrengths) {
        this.replacementActualIngredientStrengths = replacementActualIngredientStrengths;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCasNr() {
        return casNr;
    }

    public void setCasNr(Long casNr) {
        this.casNr = casNr;
    }

    public boolean getExcInd() {
        return excInd;
    }

    public void setExcInd(boolean excInd) {
        this.excInd = excInd;
    }

    public boolean getActiveBaseInd() {
        return activeBaseInd;
    }

    public void setActiveBaseInd(boolean activeBaseInd) {
        this.activeBaseInd = activeBaseInd;
    }

    public String getDerivFormType() {
        return derivFormType;
    }

    public void setDerivFormType(String derivFormType) {
        this.derivFormType = SAMTools.intern(derivFormType);
    }

    public Source getCasSource() {
        return casSource;
    }

    public void setCasSource(Source casSource) {
        if (casSource != null) {
            this.casSource = casSource;
        }
        this.casSource = Source.SAM;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(o == null || getClass() != o.getClass()){
            return false;
        }
        Substance a = (Substance)o;
        return casId == a.casId
                && ObjectTools.equals(casSource, a.casSource);
    }

    @Override
    public int hashCode(){
        return (int) casId;
    }

    @Override
    public String toString(){
        return name == null ? super.toString() : name;
    }

    @Override
    public int compareTo(@NotNull final Substance a) {
        int c = (int) (casId - a.casId);
        return c != 0 ? c : ObjectTools.compare(casSource, a.casSource);
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
