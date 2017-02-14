package uz.emv.sam.v1.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: simbre1
 * Date: 16/10/13
 */
public class SAMTools {
    public static final String DATABASE = "apotheek";
    public static final String S9TABLE_PREFIX = "b#sam";
    public static final int ID_LENGTH = 10;
    public static final int NAME_LENGTH = 255;
    public static final int SOURCE_LENGTH = 4;

    public static final Class<?>[] SAM_CLASSES = {
            ActualIngredientStrength.class,
            AdministrationForm.class,
            AMP.class,
            AMPComb.class,
            AMPIntermediatePackage.class,
            AMPIntPckComb.class,
            AMPP.class,
            ATC.class,
            ATM.class,
            Application.class,
            Company.class,
            InnerPackage.class,
            PharmaceuticalForm.class,
            RouteOfAdministration.class,
            Substance.class,
            TreatmentDurationCategory.class,
            VirtualIngredient.class,
            VirtualIngredientStrength.class,
            VMP.class,
            VMPComb.class,
            VMPP.class,
            VTM.class
    };

    @NotNull
    public static String toSamColumnName(@NotNull final String s9ColumnName){
        return toSamName(s9ColumnName);
    }

    @NotNull
    public static String toSamTableName(@NotNull final String s9TableName){
        if(s9TableName.startsWith(S9TABLE_PREFIX)){
            return SAMTools.toSamName(s9TableName.substring(S9TABLE_PREFIX.length()));
        }else{
            return SAMTools.toSamName(s9TableName);
        }
    }

    @NotNull
    public static String toBaseS9TableName(@NotNull final String s9TableName){
        int i = s9TableName.indexOf('#');
        return i < 0 ? s9TableName : s9TableName.substring(i+1);
    }

    @NotNull
    public static String removeBaseTablePrefix(@NotNull final String baseTableName) {
        int i = baseTableName.indexOf("sam");
        return i < 0 ? baseTableName : baseTableName.substring(i + 3);
    }

    @NotNull
    public static String toSamName(@NotNull final String s9Name){
        StringBuilder sb = new StringBuilder();

        for(char c : s9Name.toCharArray()){
            if(Character.isUpperCase(c)){
                if(sb.length() > 0){
                    sb.append('_');
                }
                sb.append(c);
            }else{
                sb.append(Character.toUpperCase(c));
            }
        }

        return sb.toString();
    }

    @NotNull
    public static String toS9ColumnName(@NotNull final String samName){
        return toS9Name(samName);
    }

    @NotNull
    public static String toS9TableName(@NotNull final String samTableName){
        final String longS9TableName = S9TABLE_PREFIX + toS9Name(samTableName);
        return longS9TableName.substring(0, Math.min(longS9TableName.length(), 30));
    }

    @NotNull
    public static String toS9Name(@NotNull final String samColumnName){
        StringBuilder sb = new StringBuilder();

        boolean up = false;
        for(char c : samColumnName.toCharArray()){
            if(c == '_'){
                up = true;
            }else{
                if(up){
                    sb.append(Character.toUpperCase(c));
                }else{
                    sb.append(Character.toLowerCase(c));
                }
                up = false;
            }
        }

        return sb.toString();
    }

    public static void main(String[] args){
        testColumnNameConversion("VIRTUAL_INGREDIENT_STRENGTH", "virtualIngredientStrength");
    }

    private static void testColumnNameConversion(@NotNull final String sam, @NotNull final String s9){
        final String samb = toSamColumnName(s9);
        final String s9b = toS9ColumnName(sam);

        assert(sam.equals(samb));
        assert(s9.equals(s9b));
    }

    @Nullable
    static String intern(@Nullable final String s) {
        return s == null ? null : s.intern();
    }
}
