package uz.emv.sam.v1.db.tables.xml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uz.emv.sam.v1.db.tables.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * User: simbre1
 * Date: 23/09/13
 */
public class NameTranslation {

    public static enum Column{
        NAME_ID("NAME_ID"),
        NAME_TYPE_CV("NAME_TYPE_CV"),
        LANGUAGE_CV("LANGUAGE_CV"),
        SHORT_TEXT("SHORT_TEXT"),
        LONG_TEXT("LONG_TEXT"),
        LONG_BINARY_TEXT("LONG_BINARY_TEXT");

        private final String value;

        Column(@NotNull final String value){
            this.value = value;
        }

        @NotNull
        public String getValue(){
            return value;
        }
    }

    public static enum Type{
        QUALIF("QUALIF"),
        NAME("NAME");

        private final String value;

        Type(@NotNull final String value){
            this.value = value;
        }

        @NotNull
        public String getValue(){
            return value;
        }
    }

    public static enum Language{
        FR("FR"),
        NL("NL");

        private final String value;

        Language(@NotNull final String value){
            this.value = value;
        }

        @NotNull
        public String getValue(){
            return value;
        }
    }

    public static final String TABLE_NAME = "NAME_TRANSLATION";

    private final Map<Integer, String> names = new HashMap<Integer, String>();
    private final Type type;
    private final Language language;
    private final Column columnAsName;

    public NameTranslation(@NotNull final Table table) {
        this(table, Type.NAME, Language.NL, Column.SHORT_TEXT);
    }

    public NameTranslation(@NotNull final Table table,
                           @NotNull final Type type,
                           @NotNull final Language language,
                           @NotNull final Column columnAsName) {
        this.type = type;
        this.language = language;
        this.columnAsName = columnAsName;

        parseNames(table);
    }

    @NotNull
    public Type getType(){
        return type;
    }

    @NotNull
    public Language getLanguage(){
        return language;
    }

    @NotNull
    public Column getColumnAsName(){
        return columnAsName;
    }

    private void parseNames(@NotNull final Table table) {
        for(Map<String, Object> row : table){
            if(matches(row)){
                final Integer nameId
                        = Integer.parseInt((String) row.get(Column.NAME_ID.getValue()));
                names.put(nameId, (String)row.get(columnAsName.getValue()));
            }
        }
    }

    private boolean matches(@NotNull final Map<String, Object> row){
        return type.getValue().equals(row.get(Column.NAME_TYPE_CV.getValue()))
                && language.getValue().equals(row.get(Column.LANGUAGE_CV.getValue()));
    }

    @Nullable
    public String getName(@NotNull final Integer nameId){
        return names.get(nameId);
    }

    public void cleanUp(){
        names.clear();
    }
}
