package uz.emv.sam.v1.domain;


/**
 * User: simbre1
 * Date: 7/10/13
 */
public enum Source {
    UZL("UZ Leuven"),
    SAM("eHealth SAM");

    private final String description;

    Source(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    /*
    public static final Projection<Source, String> TO_STRING_PROJECTION = new AbstractProjection<Source, String>() {
        @Override
        public String project(Source source) {
            return source.name();
        }
    };

    public static final Projection<String, Source> FROM_STRING_PROJECTION = new AbstractProjection<String, Source>() {
        @Override
        public Source project(String source) {
            return Source.valueOf(source);
        }
    };
    */
}
