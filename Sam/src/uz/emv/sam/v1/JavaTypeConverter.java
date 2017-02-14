/**
 * Title:        KWS<p>
 * Description:  Your description<p>
 * Copyright:    Copyright (c) 1999<p>
 * Company:      UZLeuven<p>
 * adapted for non UZL-use by
 *
 * @author Bart Decuypere
 * @author Romain Reniers
 * @version
 */
package uz.emv.sam.v1;


import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converter voor JAVA types. Bevat enkel statische utility methodes.
 */
public class JavaTypeConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavaTypeConverter.class);

    private static final Pattern READABLE_DATE_FORMAT_PATTERN = Pattern.compile(
            "^(\\d{1,2})[ -](\\d{1,2})[ -](\\d{4})(?: {1,2}(\\d{1,2}):(\\d{1,2})(?::(\\d{1,2})(?:([\\.:])(\\d{1,3}))?)?)?$");
    private static final Pattern READABLE_TIME_FORMAT_PATTERN = Pattern.compile(
            "^(?:(\\d{1,2})[ -](\\d{1,2})[ -](\\d{4}) {1,2})?(?:(\\d{1,2}):(\\d{1,2})(?::(\\d{1,2})(?:([\\.:])(\\d{1,3}))?)?)?$");

    private static final Map<Class, Class> PRIMITIVE_TO_WRAPPER_MAPPING;

    static {
        PRIMITIVE_TO_WRAPPER_MAPPING = new HashMap<Class, Class>();
        PRIMITIVE_TO_WRAPPER_MAPPING.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_TO_WRAPPER_MAPPING.put(Character.TYPE, Character.class);
        PRIMITIVE_TO_WRAPPER_MAPPING.put(Byte.TYPE, Byte.class);
        PRIMITIVE_TO_WRAPPER_MAPPING.put(Short.TYPE, Short.class);
        PRIMITIVE_TO_WRAPPER_MAPPING.put(Integer.TYPE, Integer.class);
        PRIMITIVE_TO_WRAPPER_MAPPING.put(Long.TYPE, Long.class);
        PRIMITIVE_TO_WRAPPER_MAPPING.put(Float.TYPE, Float.class);
        PRIMITIVE_TO_WRAPPER_MAPPING.put(Double.TYPE, Double.class);
    }

    /**
     * Omwille van performantie zullen we bijhoden welke 'convertTo'-methode we moeten gebruiken
     * voor een combinatie van result/value class.
     */
    private static Map<Pair<Class, Class>, Method> methodCache = new HashMap<Pair<Class, Class>, Method>();

    /**
     * Geen constructor nodig, hier zijn enkel statische methodes.
     */
    private JavaTypeConverter() {
    }

    /**
     * Converts a value to the given class. If conversion does not succeed, this is only
     * written to System.out and null is returned.
     *
     * @param c     the resulting class
     * @param value the value to be converted
     * @return the converted value or null if the conversion fails.
     */
    public static <T> T convertValueSilent(Class<T> c, Object value) {
        try {
            return convertValue(c, value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts a value to the given class.
     *
     * @param c     the resulting class
     * @param value the value to be converted
     * @return the converted value
     * @throws Exception if the value could not be converted
     */
    public static <T> T convertValue(Class<T> c, Object value) throws Exception {
        if (value == null) {
            return null;
        }

        // In all cases we convert an empty string to the 'null' value on the database.
        if (value instanceof String && ((String) value).isEmpty()) {
            return null;
        }

        //noinspection unchecked
        c = convertToWrapperClass(c);

        Class clazz = value.getClass();
        if (c.equals(clazz) || c.isInstance(value)) {
            //noinspection unchecked
            return (T) value;
        }

        // kijk in de cache of we reeds deze combinatie van result/value class geconverteerd hebben.
        Pair<Class, Class> cacheKey = Pair.of((Class) c, clazz);
        Method m = methodCache.get(cacheKey);

        if (m == null) {
            // find the correct converter method
            String suffix = c.getName().substring(c.getName().lastIndexOf('.') + 1);
            if ("[B".equals(suffix)) {
                suffix = "ByteArray";
            }

            // deze loop eindigt ofwel met een gevonden methode, ofwel door een NoSuchMethodException te throwen
            while (m == null) {
                try {
                    m = JavaTypeConverter.class.getMethod("convertTo" + suffix, clazz);
                    methodCache.put(cacheKey, m);
                } catch (NoSuchMethodException nsme) {
                    // opnieuw proberen met superclass
                    clazz = clazz.getSuperclass();
                    if (clazz == null) {
                        LOGGER.error("Return Class: {}", c);
                        LOGGER.error("No such conversion exists: JavaTypeConverter.convertTo{}({} {})", suffix, value.getClass(), value);
                        throw nsme;
                    }
                }
            }
        }

        try {
            //noinspection unchecked
            return (T) m.invoke(null, value);
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if (cause instanceof Exception) {
                //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
                throw (Exception) cause;
            }
            throw ite;
        }
    }

    /**
     * Converteer een primitieve klasse naar zijn overeenkomstige wrapper klasse.
     */
    public static Class convertToWrapperClass(Class primitiveClass) {
        Class result = PRIMITIVE_TO_WRAPPER_MAPPING.get(primitiveClass);
        return result == null ? primitiveClass : result;
    }

    /**
     * Converts a Date value to the String type
     *
     * @param value the value
     * @return the converted value
     */
    public static String convertToString(Date value) {
        if (value == null) {
            return null;
        } else {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            return dateFormat.format(value);
        }
    }

    /**
     * Converts a Calendar value to the String type
     *
     * @param value the value
     * @return the converted value
     */
    public static String convertToString(Calendar value) {
        if (value == null) {
            return null;
        } else {

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            return dateFormat.format(value.getTime());
        }
    }

    /**
     * Converts a Boolean to the String type
     *
     * @param value the value
     * @return the converted value
     */
    public static String convertToString(Boolean value) {
        if (value == null) {
            return null;
        } else {
            if (value.booleanValue()) {
                return "1";
            } else {
                return "0";
            }
        }
    }

    /**
     * Converts an object to a String
     *
     * @param value the object
     * @return the converted value
     */
    public static String convertToString(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return (String) value;
        } else {
            return value.toString();
        }
    }

    /**
     * Converts a value to the Integer type
     *
     * @param value the value
     * @return the converted value
     */
    public static Integer convertToInteger(Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return value.intValue();
        }
    }

    /**
     * Converts a value to the Integer type
     *
     * @param value the value
     * @return the converted value
     */
    public static Integer convertToInteger(String value) {
        while (true) {
            if (value == null || value.isEmpty()) {
                return null;
            } else {
                // bugfix:
                // * <jdk1.7: Integer.parseInt("+5") throws Exception
                // * >=jdk1.7: Integer.parseInt("+5") == 5
                if (value.startsWith("+") && value.length() > 1 && Character.isDigit(value.charAt(1))) {
                    value = value.substring(1);
                    continue;
                }
                return Integer.valueOf(value);
            }
        }
    }

    /**
     * Converts a value to the Integer type
     *
     * @param value the value
     * @return the converted value
     */
    public static Integer convertToInteger(Boolean value) {
        if (value == null) {
            return null;
        } else {
            return value.booleanValue() ? 1 : 0;
        }
    }

    /**
     * Converts a value to the Long type
     *
     * @param value the value
     * @return the converted value
     */
    public static Long convertToLong(Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof Long) {
            return (Long) value;
        } else {
            return value.longValue();
        }
    }

    /**
     * Converts a value to the Long type
     *
     * @param value the value
     * @return the converted value
     */
    public static Long convertToLong(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        } else {
            // bugfix:
            // * <jdk1.7: Integer.parseInt("+5") throws Exception
            // * >=jdk1.7: Integer.parseInt("+5") == 5
            if (value.startsWith("+") && value.length() > 1 && Character.isDigit(value.charAt(1))) {
                return Long.valueOf(value.substring(1));
            }
            return Long.valueOf(value);
        }
    }

    /**
     * Converts a value to the Long type
     *
     * @param value the value
     * @return the converted value
     */
    public static Long convertToLong(Boolean value) {
        if (value == null) {
            return null;
        } else {
            return value.booleanValue() ? 1L : 0L;
        }
    }

    /**
     * Converts a value to the BigDecimal type
     *
     * @param value the value
     * @return the converted value
     */
    public static BigDecimal convertToBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        } else {
            return new BigDecimal(value.replace(',', '.'));
        }
    }

    /**
     * Converts a value to the BigDecimal type
     *
     * @param value the value
     * @return the converted value
     */
    public static BigDecimal convertToBigDecimal(Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof Long || value instanceof Integer) {
            return BigDecimal.valueOf(value.longValue());
        } else {
            return BigDecimal.valueOf(value.doubleValue());
        }
    }

    /**
     * Converts a value to the Short type
     *
     * @param value the value
     * @return the converted value
     */
    public static Short convertToShort(Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof Short) {
            return (Short) value;
        } else {
            return value.shortValue();
        }
    }

    /**
     * Converts a value to the Short type
     *
     * @param value the value
     * @return the converted value
     */
    public static Short convertToShort(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        } else {
            return Short.valueOf(value);
        }
    }

    /**
     * Converts a value to the Byte type
     *
     * @param value the value
     * @return the converted value
     */
    public static Byte convertToByte(Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof Byte) {
            return (Byte) value;
        } else {
            return value.byteValue();
        }
    }

    /**
     * Converts a value to the Byte type
     *
     * @param value the value
     * @return the converted value
     */
    public static Byte convertToByte(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        } else {
            return Byte.valueOf(value);
        }
    }

    /**
     * Converts a value to the Float type
     *
     * @param value the value
     * @return the converted value
     */
    public static Float convertToFloat(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        } else {
            return Float.valueOf(value.replace(',', '.'));
        }
    }

    /**
     * Converts a value to the Float type
     *
     * @param value the value
     * @return the converted value
     */
    public static Float convertToFloat(Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof Float) {
            return (Float) value;
        } else {
            return value.floatValue();
        }
    }

    /**
     * Converts a value to the Double type
     *
     * @param value the value
     * @return the converted value
     */
    public static Double convertToDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        } else {
            return Double.valueOf(value.replace(',', '.'));
        }
    }

    /**
     * Converts a value to the Double type
     *
     * @param value the value
     * @return the converted value
     */
    public static Double convertToDouble(Number value) {
        if (value == null) {
            return null;
        } else if (value instanceof Double) {
            return (Double) value;
        } else {
            return value.doubleValue();
        }
    }

    /**
     * Converts a value to the Boolean type
     *
     * @param value the value
     * @return the converted value
     */
    public static Boolean convertToBoolean(String value) {
        if (value == null) {
            return null;
        } else {
            if ("1".equals(value)) {
                return Boolean.TRUE;
            } else if ("0".equals(value)) {
                return Boolean.FALSE;
            } else {
                return Boolean.valueOf(value);
            }
        }
    }

    /**
     * Converts a value to the Boolean type
     *
     * @param value the value
     * @return the converted value
     */
    public static Boolean convertToBoolean(Number value) {
        if (value == null) {
            return null;
        } else {
            return Boolean.valueOf(value.intValue() == 1);
        }
    }


    /**
     * Converts a value to the Boolean type
     *
     * @param value         the value to convert
     * @param booleanIfNull the return value if null
     * @return the converted value
     */
    public static Boolean convertToBoolean(Object value, Boolean booleanIfNull) {
        if (value == null) {
            return booleanIfNull;
        }
        if (value instanceof String) {
            return convertToBoolean((String) value);
        } else if (value instanceof Float) {
            return convertToBoolean((Float) value);
        } else if (value instanceof Double) {
            return convertToBoolean((Double) value);
        } else if (value instanceof Number) {
            return convertToBoolean((Number) value);
        }
        throw new IllegalArgumentException("JavaTypeConverter: convert to " + value + " is not supported!");
    }

    /**
     * Converts a value to the Boolean type
     *
     * @param value the value
     * @return the converted value
     */
    public static Boolean convertToBoolean(Float value) {
        if (value == null) {
            return null;
        } else {
            //noinspection FloatingPointEquality
            return Boolean.valueOf(value.floatValue() == 1.0f);
        }
    }

    /**
     * Converts a value to the Boolean type
     *
     * @param value the value
     * @return the converted value
     */
    public static Boolean convertToBoolean(Double value) {
        if (value == null) {
            return null;
        } else {
            //noinspection FloatingPointEquality
            return Boolean.valueOf(value.doubleValue() == 1.0d);
        }
    }

    /**
     * Converts a value to the Date type
     *
     * @param value the value
     * @return the converted value
     */
    public static Date convertToDate(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        } else {


            final String trimmedValue = value.trim();
            final Matcher matcher = READABLE_DATE_FORMAT_PATTERN.matcher(trimmedValue);
            if (!matcher.matches() || matcher.groupCount() < 8) {
                throw new IllegalArgumentException("Illegal date format (dd-mm-yyyy hh:mm:ss): " + value);
            }

            final int day;
            final int month;
            final int year;
            final int hour;
            final int min;
            final int sec;
            int ms;
            day = Integer.parseInt(matcher.group(1));
            month = Integer.parseInt(matcher.group(2));
            year = Integer.parseInt(matcher.group(3));

            if (matcher.group(4) != null) {
                hour = Integer.parseInt(matcher.group(4));
            } else {
                hour = 0;
            }
            if (matcher.group(5) != null) {
                min = Integer.parseInt(matcher.group(5));
            } else {
                min = 0;
            }
            if (matcher.group(6) != null) {
                sec = Integer.parseInt(matcher.group(6));
            } else {
                sec = 0;
            }
            if (matcher.group(8) != null) {
                ms = Integer.parseInt(matcher.group(8));
                // Indien milliseconden voorafgegaan door een punt, dan nullen achteraan toevoegen i.p.v. vooraan !
                if (".".equals(matcher.group(7))) {
                    ms = Integer.parseInt((matcher.group(8) + "000").substring(0, 3));
                }
            } else {
                ms = 0;
            }

            final GregorianCalendar cal = new GregorianCalendar();

            boolean valid;
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    valid = (day <= 31);
                    break;

                case 4:
                case 6:
                case 9:
                case 11:
                    valid = (day <= 30);
                    break;

                case 2:
                    final boolean leap = cal.isLeapYear(year);
                    valid = (leap && day <= 29 || !leap && day <= 28);
                    break;

                default:
                    valid = false;
                    break;
            }

            valid = valid
                    && (day >= 1)
                    && (hour >= 0 && hour <= 23)
                    && (min >= 0 && min <= 59)
                    && (sec >= 0 && sec <= 59)
                    && (ms >= 0 && ms <= 999);

            if (!valid) {
                throw new IllegalArgumentException("Illegal date format (dd-mm-yyyy hh:mm:ss): " + value);
            }

            // The month value must be 0-based. e.g., 0 for January.
            // noinspection MagicConstant
            cal.set(year, month - 1, day, hour, min, sec);
            cal.set(Calendar.MILLISECOND, ms);

            // The method getTime() will validate all the fields !
            return cal.getTime();
        }
    }

    /**
     * Converts a time to the Date type
     *
     * @param value the time
     * @return the converted value
     */
    public static Date convertToDate(Long value) {
        if (value == null) {
            return null;
        } else {
            return new Date(value.longValue());
        }
    }

    /**
     * Converts a value to the Date type
     *
     * @param value the value
     * @return the converted value
     */
    public static Date convertToDate(Calendar value) {
        if (value == null) {
            return null;
        } else {
            return value.getTime();
        }
    }

    /**
     * Converts a value to the Calendar type
     *
     * @param value the value
     * @return the converted value
     */
    public static Calendar convertToCalendar(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        } else {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(convertToDate(value));
            return cal;
        }
    }

    /**
     * Converts a value to the Calendar type
     *
     * @param value the value
     * @return the converted value
     */
    public static Calendar convertToCalendar(Date value) {
        if (value == null) {
            return null;
        } else {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(value);
            return cal;
        }
    }

    /**
     * Converts a time to the Calendar type
     *
     * @param value the time
     * @return the converted value
     */
    public static Calendar convertToCalendar(Long value) {
        if (value == null) {
            return null;
        } else {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(new Date(value.longValue()));
            return cal;
        }
    }

    /**
     * Converts a value to the Timestamp type
     *
     * @param value the value
     * @return the converted value
     */
    public static Timestamp convertToTimestamp(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        } else {


            final String trimmedValue = value.trim();
            final Matcher matcher = READABLE_DATE_FORMAT_PATTERN.matcher(trimmedValue);
            if (!matcher.matches() || matcher.groupCount() < 8) {
                throw new IllegalArgumentException("Illegal date format (dd-mm-yyyy hh:mm:ss): " + value);
            }

            final int day;
            final int month;
            final int year;
            final int hour;
            final int min;
            final int sec;
            int ms;
            day = Integer.parseInt(matcher.group(1));
            month = Integer.parseInt(matcher.group(2));
            year = Integer.parseInt(matcher.group(3));

            if (matcher.group(4) != null) {
                hour = Integer.parseInt(matcher.group(4));
            } else {
                hour = 0;
            }
            if (matcher.group(5) != null) {
                min = Integer.parseInt(matcher.group(5));
            } else {
                min = 0;
            }
            if (matcher.group(6) != null) {
                sec = Integer.parseInt(matcher.group(6));
            } else {
                sec = 0;
            }
            if (matcher.group(8) != null) {
                ms = Integer.parseInt(matcher.group(8));
                // Indien milliseconden voorafgegaan door een punt, dan nullen achteraan toevoegen i.p.v. vooraan !
                if (".".equals(matcher.group(7))) {
                    ms = Integer.parseInt((matcher.group(8) + "000").substring(0, 3));
                }
            } else {
                ms = 0;
            }

            final GregorianCalendar cal = new GregorianCalendar();

            boolean valid;
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    valid = (day <= 31);
                    break;

                case 4:
                case 6:
                case 9:
                case 11:
                    valid = (day <= 30);
                    break;

                case 2:
                    final boolean leap = cal.isLeapYear(year);
                    valid = (leap && day <= 29 || !leap && day <= 28);
                    break;

                default:
                    valid = false;
                    break;
            }

            valid = valid
                    && (day >= 1)
                    && (hour >= 0 && hour <= 23)
                    && (min >= 0 && min <= 59)
                    && (sec >= 0 && sec <= 59)
                    && (ms >= 0 && ms <= 999);

            if (!valid) {
                throw new IllegalArgumentException("Illegal date format (dd-mm-yyyy hh:mm:ss): " + value);
            }

            // The month value must be 0-based. e.g., 0 for January.
            // noinspection MagicConstant
            cal.set(year, month - 1, day, hour, min, sec);
            cal.set(Calendar.MILLISECOND, ms);

            // The method getTime() will validate all the fields !
            Date date = cal.getTime();
            return new Timestamp(date.getTime());
        }
    }

    /**
     * Converts a Date to the Timestamp type
     *
     * @param value the Date
     * @return the converted value
     */
    public static Timestamp convertToTimestamp(Date value) {
        if (value == null) {
            return null;
        } else if (value instanceof Timestamp) {
            return (Timestamp) value;
        } else {
            return new Timestamp(value.getTime());
        }
    }

    /**
     * Converts a Calendar to the Timestamp type
     *
     * @param value the Date
     * @return the converted value
     */
    public static Timestamp convertToTimestamp(Calendar value) {
        if (value == null) {
            return null;
        } else {
            return new Timestamp(value.getTime().getTime());
        }
    }

    public static Time convertToTime(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        } else {


            final String trimmedValue = value.trim();
            final Matcher matcher = READABLE_TIME_FORMAT_PATTERN.matcher(trimmedValue);
            if (!matcher.matches() || matcher.groupCount() < 8) {
                throw new IllegalArgumentException("Illegal date format (dd-mm-yyyy hh:mm:ss): " + value);
            }

            final int hour;
            final int min;
            final int sec;
            int ms;

            hour = Integer.parseInt(matcher.group(4));
            min = Integer.parseInt(matcher.group(5));

            if (matcher.group(6) != null) {
                sec = Integer.parseInt(matcher.group(6));
            } else {
                sec = 0;
            }

            if (matcher.group(8) != null) {
                ms = Integer.parseInt(matcher.group(8));
                // Indien milliseconden voorafgegaan door een punt, dan nullen achteraan toevoegen i.p.v. vooraan !
                if (".".equals(matcher.group(7))) {
                    ms = Integer.parseInt((matcher.group(8) + "000").substring(0, 3));
                }
            } else {
                ms = 0;
            }

            final GregorianCalendar cal = new GregorianCalendar();
            cal.clear();

            boolean valid = (hour >= 0 && hour <= 23)
                    && (min >= 0 && min <= 59)
                    && (sec >= 0 && sec <= 59)
                    && (ms >= 0 && ms <= 999);

            if (!valid) {
                throw new IllegalArgumentException("Illegal date format (dd-mm-yyyy hh:mm:ss): " + value);
            }

            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, min);
            cal.set(Calendar.SECOND, sec);
            cal.set(Calendar.MILLISECOND, ms);

            // The method getTime() will validate all the fields !
            return new Time(cal.getTimeInMillis());
        }
    }

    public static Time convertToTime(Date value) {
        if (value == null) {
            return null;
        } else if (value instanceof Time) {
            return (Time) value;
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(value);
            calendar.clear(Calendar.YEAR);
            calendar.clear(Calendar.MONTH);
            calendar.clear(Calendar.DATE);
            return new Time(calendar.getTimeInMillis());
        }
    }

    /**
     * Converts a time to the Timestamp type
     *
     * @param value the time
     * @return the converted value
     */
    public static Timestamp convertToTimestamp(Long value) {
        if (value == null) {
            return null;
        } else {
            return new Timestamp(value.longValue());
        }
    }

    /**
     * Converts a value to a URL.
     *
     * @param value the value
     * @return the converted value
     * @throws MalformedURLException if the value couldn't be converted
     */
    public static URL convertToURL(String value) throws MalformedURLException {
        if (value == null) {
            return null;
        } else {
            return new URL(value);
        }
    }

    /**
     * Converts a hexadecimal string (12a2bc0d) to the byte[] type
     *
     * @param value the hexadecimal string
     * @return the converted value
     */
    @Nullable
    public static byte[] convertToByteArray(String value) {
        if (value == null) {
            return null;
        } else {
            final int len = value.length() / 2;
            final byte[] res = new byte[len];

            for (int i = 0; i < len; i++) {
                //	    System.out.println("Hex: "+hex.substring(i*2, (i+1)*2) + " Byte: "+
                //		(byte) Integer.parseInt(hex.substring(i*2, (i+1)*2), 16));
                res[i] = (byte) Integer.parseInt(value.substring(i * 2, (i + 1) * 2), 16);
            }
            return res;
        }
    }


    public static UUID convertToUUID(Object value) {
        if (value == null) {
            return null;
        } else {
            return UUID.fromString((String) value);
        }
    }

}
