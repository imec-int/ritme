package uz.ehealth.ritme.sam.model.v1;


import java.util.Date;

/**
 * Created by bdcuyp0 on 19-1-2017.
 */
public class JsonAMP implements AMP {
    private long ampId;
    private long nameId;
    private String name;
    private String galenicFormTxt;
    private String dimensions;
    private String dopCv;
    private String registSpec;
    private String descriptSpec;
    private Date closeDate;
    private Date initDate;
    private boolean flatRateInd;
    private String duration;

    @Override
    public long getAmpId() {
        return this.ampId;
    }

    @Override
    public long getNameId() {
        return this.nameId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getGalenicFormTxt() {
        return this.galenicFormTxt;
    }

    @Override
    public String getDimensions() {
        return this.dimensions;
    }

    @Override
    public String getDopCv() {
        return this.dopCv;
    }

    @Override
    public String getRegistSpec() {
        return this.registSpec;
    }

    @Override
    public String getDescriptSpec() {
        return this.descriptSpec;
    }

    @Override
    public String getDuration() {
        return this.duration;
    }

    @Override
    public boolean getFlatRateInd() {
        return this.flatRateInd;
    }

    @Override
    public Date getInitDate() {
        return this.initDate;
    }

    @Override
    public Date getCloseDate() {
        return this.closeDate;
    }


}
