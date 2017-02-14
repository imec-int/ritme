package uz.ehealth.ritme.sam.model.v1;


import java.util.Date;

/**
 * Created by bdcuyp0 on 19-1-2017.
 */
public interface AMP {

    public long getAmpId();


    public long getNameId();

        /*
        public ATM getATM();

        public PharmaceuticalForm getPharmaceuticalForm();

        public ATC getATC();

        public VMP getVMP();

        public Company getCompany();

        public AdministrationForm getAdministrationForm();

        public RouteOfAdministration getRouteOfAdministration();
        public Set<AMPComb> getChildCombinations();

        public Set<AMPComb> getParentCombinations();

        public Set<AMPIntermediatePackage> getAMPIntermediatePackages();

        public Set<ActualIngredientStrength> getActualIngredientStrengths();
        */

    public String getName();

    public String getGalenicFormTxt();

    public String getDimensions();

    public String getDopCv();

    public String getRegistSpec();

    public String getDescriptSpec();

    public String getDuration();

    public boolean getFlatRateInd();

    public Date getInitDate();

    public Date getCloseDate();
        /*

        public Source getAmpSource();
        */

}



