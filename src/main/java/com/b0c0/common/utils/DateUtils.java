package com.b0c0.common.utils;




import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author zfc
 */
public final class DateUtils {

    private static final Logger log = LoggerFactory.getLogger(DateUtils.class);

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";// Timestamp

    public static final String NORMAL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    // UTC时间格式
    public static final String UTC_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

    /**
     * 精确到天的日期格式  yyyy-MM-dd
     */
    public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 精确到天的日期格式(八位) yyyyMMdd
     */
    public static final String SHORT_DATE_FORMAT_EIGHT = "yyyyMMdd";
    /**
     * 精确到月的日期格式(六位) yyyyMM
     */
    public static final String SHORT_DATE_FORMAT_SIX = "yyyyMM";

    /**
     * 精确到月的日期格式(七位) yyyy-MM
     */
    public static final String SHORT_DATE_FORMAT_SERVEN = "yyyy-MM";

    /**
     * 精确到月的日期格式(六位) yyMMdd
     */
    public static final String SHORT_DATE_FORMAT_SIX_D = "yyMMdd";
    /**
     * 精确到秒的日期格式 yyyyMMddHHmmss
     */
    public static final String DATE_FORMAT_FULL = "yyyyMMddHHmmss";

    /**
     * 精确到分的日期格式
     */
    public static final String MEDIUM_DATE_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * 时分加空格的日期格式
     */
    public static final String HOUR_MEDIUM_DATE_FORMAT = "HH:mm";

    /**
     * 带斜线格式,精确到天
     */
    public static final String SHORT_DATE_FORMAT_SLASH_SHORT = "yyyy/MM/dd";

    /**
     * 带斜线格式,精确到秒
     */
    public static final String SHORT_DATE_FORMAT_SLASH = "yyyy/MM/dd hh:mm:ss";

    /**
     * 只取时分秒
     */
    public static final String TIME_FORMAT = " hh:mm:ss";

    /**
     * 精确到毫秒
     */
    public static final String DATE_FORMAT_FULL_MIL = "yyyyMMddHHmmssSSS";

    /**
     * 只取24小时制时分秒
     */
    public static final String TIME_FORMAT_24H = "HHmmss";

    public static final String TIME_FORMAT_24H_MILLION_SECOND = "HHmmssSSS";

    /**
     * 默认构造函数
     */
    private DateUtils() {
    }

    /**
     * 字符串转换成日期 如果转换格式为空，则利用默认格式进行转换操作
     *
     * @param str    字符串
     * @param format 日期格式
     * @return 日期
     * @throws ParseException
     */
    public static Date str2Date(String str, String format) {
        if (null == str || "".equals(str)) {
            return null;
        }
        // 如果没有指定字符串转换的格式，则用默认格式进行转换
        if (null == format || "".equals(format)) {
            format = DEFAULT_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(str);
            return date;
        } catch (ParseException e) {
            log.error("日期字符串解析异常", e);
        }
        return null;
    }

    public static Date str2Date(String str) {
        return str2Date(str, NORMAL_DATE_FORMAT);
    }

    public static LocalDateTime str2LocalDateTime(String str) {
        if (!StringUtils.hasText(str)) {
            return null;
        }
        Date date = str2Date(str, NORMAL_DATE_FORMAT);
        ZoneId zoneId = ZoneId.systemDefault();
        return date.toInstant().atZone(zoneId).toLocalDateTime();
    }

    public static LocalDateTime str2LocalDateTime(String str, String format) {
        if (!StringUtils.hasText(str)) {
            return null;
        }
        Date date = str2Date(str, format);
        ZoneId zoneId = ZoneId.systemDefault();
        return date.toInstant().atZone(zoneId).toLocalDateTime();
    }

    /**
     * 日期转换为字符串
     *
     * @param date   日期
     * @param format 日期格式
     * @return 字符串
     */
    public static String date2Str(Date date, String format) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 时间戳转换为字符串
     *
     * @param time
     * @return
     */
    public static String timestamp2Str(Timestamp time) {
        Date date = null;
        if (null != time) {
            date = new Date(time.getTime());
        }
        return date2Str(date, DEFAULT_FORMAT);
    }

    /**
     * 字符串转换时间戳
     *
     * @param str
     * @return
     */
    public static Timestamp str2Timestamp(String str) {
        Date date = str2Date(str, DEFAULT_FORMAT);
        if (null == date) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    /**
     * 获取两个时间相差的天数，不足一天补一天。
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getCeilDays(Date startDate, Date endDate) {
        if (null == startDate || null == endDate) {
            return 0;
        }
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();

        return (int) Math.ceil((endTime - startTime) / (1000f * 60 * 60 * 24));
    }

    /**
     * 获取当前时间秒级时间戳
     *
     * @return
     */
    public static int getCurrTimeStamp() {
        return (int) Calendar.getInstance().getTimeInMillis() / 1000;
    }

    /**
     * 获取两个日期相差的天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getBetweenDay(String startDate, String endDate) {
        Date sDate = str2Date(startDate, DateUtils.SHORT_DATE_FORMAT);
        Date eDate = str2Date(endDate, DateUtils.SHORT_DATE_FORMAT);
        return getBetweenDay(sDate, eDate);
    }

    /**
     * 获取两个日期相差的天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getBetweenDay(Date startDate, Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);
        int year1 = calendar.get(Calendar.YEAR);
        calendar.setTime(endDate);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);
        int year2 = calendar.get(Calendar.YEAR);
        //如果两个日期的是在同一年，则只需要计算两个日期在一年的天数差；
        //不在同一年，还要加上相差年数对应的天数，闰年有366天
        if (year1 != year2) {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                    //闰年
                    timeDistance += 366;
                } else {
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else {
            return day2 - day1;
        }
    }

    /**
     * 得到几天前的时间
     *
     * @param currentDate
     * @param days
     * @return
     */
    public static Date getDateBefore(Date currentDate, int days) {
        Calendar now = Calendar.getInstance();
        now.setTime(currentDate);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - days);
        return now.getTime();
    }

    /**
     * 获取两个时间相差的分钟数，不足一分钟补一分钟。
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Integer getCeilMinutes(Date startDate, Date endDate) {
        if (null == startDate || null == endDate) {
            return null;
        }
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();

        return (int) Math.ceil((endTime - startTime) / (1000f * 60));
    }

    /**
     * 根据日期和偏移数计算新的日期。<br/>
     * days > 0,日期加；days < 0,日期减。
     *
     * @param date
     * @param days
     * @return
     */
    public static Date getDateOfDay(Date date, int days) {
        if (null == date) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);

        return calendar.getTime();
    }

    /**
     * 根据日期、偏移字段、偏移数计算新的日期。<br/>
     * days > 0,日期加；days < 0,日期减。
     *
     * @param date
     * @param field  如：Calendar.DAY_OF_YEAR
     * @param amount
     * @return
     */
    public static Date getDateOfTime(Date date, int field, int amount) {
        if (null == date) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);

        return calendar.getTime();
    }

    /**
     * 返回一个日期当天最晚的时间，精确至23:59:59。<br/>
     * 如2012-11-07 10:23 返回 2012-11-07 23:59:59。
     *
     * @param date
     * @return
     */
    public static Date getLastTimeOfDate(Date date) {
        if (null == date) {
            return null;
        }
        return str2Date(date2Str(date, SHORT_DATE_FORMAT) + " 23:59:59", NORMAL_DATE_FORMAT);
    }

    /**
     * 返回一个日期 00:00:00 <br/>
     * 如2012-11-07 10:23 返回 2012-11-07 00:00:00。
     *
     * @param date
     * @return
     */
    public static Date get0TimeOfDate(Date date) {
        if (null == date) {
            return null;
        }
        return str2Date(date2Str(date, SHORT_DATE_FORMAT) + " 00:00:00", NORMAL_DATE_FORMAT);
    }

    /**
     * 返回一个日期 24:00:00 <br/>
     * 如2012-11-07 10:23 返回 2012-11-07 24:00:00。
     *
     * @param date
     * @return
     */
    public static Date get24TimeOfDate(Date date) {
        if (null == date) {
            return null;
        }
        return str2Date(date2Str(date, SHORT_DATE_FORMAT) + " 24:00:00", NORMAL_DATE_FORMAT);
    }

    /**
     * 获取精确到天的日期<br/>
     * 如2012-11-07 10:23 返回2012-11-07
     *
     * @param date
     * @return
     */
    public static Date truncDate(Date date) {
        if (null == date) {
            return null;
        }
        return str2Date(date2Str(date, SHORT_DATE_FORMAT), SHORT_DATE_FORMAT);
    }

    /**
     * 获取当前日期下一天精确到天的日期<br/>
     * 如2012-11-07 10:23 返回2012-11-08
     *
     * @param date
     * @return
     */
    public static Date ceilDate(Date date) {
        if (null == date) {
            return null;
        }
        return getDateOfDay(truncDate(date), 1);
    }

    public static int converSecondsToDays(int seconds) {
        if (seconds <= 0) {
            return 0;
        }

        return seconds / 60 / 60 / 24;
    }

    /**
     * 比较两个时间是否是同年同月同一天
     */
    public static boolean isSameDay(Date date, Date otherDate) {
        if (null == date || null == otherDate) {
            return false;
        }
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date);
        calendar2.setTime(otherDate);
        //年月日相同的一天
        if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)) {
            return true;
        } else {
            return false;
        }


    }

    /**
     * 预先格式化成UTC的时间字符格式
     *
     * @param timeStr
     * @return
     */
    public static String filterUtcTime(String timeStr) {
        if (timeStr == null) {
            return null;
        }
        Pattern p = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z)");
        Matcher m = p.matcher(timeStr);
        StringBuffer sb = new StringBuffer();

        boolean result = m.find();
        while (result) {
            String key = m.group().replace("T", " ").replace("Z", ".000 utc");
            m.appendReplacement(sb, key);
            result = m.find();
        }
        m.appendTail(sb);
        return filterUtcLongTime(sb.toString());
    }


    public static String filterUtcLongTime(String timeStr) {
        if (timeStr == null) {
            return null;
        }
        Pattern p = Pattern.compile("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{1,3}Z)");
        Matcher m = p.matcher(timeStr);
        StringBuffer sb = new StringBuffer();

        boolean result = m.find();
        while (result) {
            String key = m.group().replace("T", " ").replace("Z", " utc");
            m.appendReplacement(sb, key);
            result = m.find();
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 获取日期中的某数值。如获取月份
     *
     * @param date     日期
     * @param dateType 日期格式
     * @return 数值
     */
    private static int getInteger(Date date, int dateType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(dateType);
    }

    /**
     * 获取日期的年份。失败返回0。
     *
     * @param date 日期
     * @return 年份
     */
    public static int getYear(Date date) {
        if (date == null) {
            return 0;
        }
        return getInteger(date, Calendar.YEAR);
    }

    /**
     * 获取日期的月份。失败返回0。
     *
     * @param date 日期
     * @return 月份
     */
    public static int getMonth(Date date) {
        if (date == null) {
            return 0;
        }
        return getInteger(date, Calendar.MONTH);
    }

    /**
     * 获取日期的天数。失败返回0。
     *
     * @param date 日期
     * @return 天
     */
    public static int getDay(Date date) {
        if (date == null) {
            return 0;
        }
        return getInteger(date, Calendar.DATE);
    }

    /**
     * 获取日期的小时。失败返回0。
     *
     * @param date 日期
     * @return 小时
     */
    public static int getHour(Date date) {
        if (date == null) {
            return 0;
        }
        return getInteger(date, Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取日期的分钟。失败返回0。
     *
     * @param date 日期
     * @return 分钟
     */
    public static int getMinute(Date date) {
        if (date == null) {
            return 0;
        }
        return getInteger(date, Calendar.MINUTE);
    }

    /**
     * 获取日期的秒钟。失败返回0。
     *
     * @param date 日期
     * @return 秒钟
     */
    public static int getSecond(Date date) {
        if (date == null) {
            return 0;
        }
        return getInteger(date, Calendar.SECOND);
    }

    /**
     * 获取当前日期上一天精确到天的日期<br/>
     * 如2012-11-07 10:23 返回2012-11-06
     *
     * @param date
     * @return
     */
    public static Date floorDate(Date date) {
        if (null == date) {
            return null;
        }
        return getDateOfDay(truncDate(date), -1);
    }

    /**
     * 输入参数月份的上一个月第一天和最后一天 <br/>
     * 例如2014/2/15，可得出1月1号至1月31号
     *
     * @param date
     * @return
     */
    public static Map<String, Object> getPreMonthFirstdayLastday(Date date) {
        if (null == date) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        Date theDate = calendar.getTime();

        // 上个月第一天
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        gcLast.set(Calendar.HOUR_OF_DAY, 0);
        gcLast.set(Calendar.MINUTE, 0);
        gcLast.set(Calendar.SECOND, 0);

        // 上个月最后一天
        calendar.add(Calendar.MONTH, 1); // 加一个月
        calendar.set(Calendar.DATE, 1); // 设置为该月第一天
        calendar.add(Calendar.DATE, -1); // 再减一天即为上个月最后一天
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("firstDate", gcLast.getTime());
        map.put("lastDate", calendar.getTime());
        return map;
    }


    /**
     * 输入参数月份的上一个月第一天和最后一天 <br/>
     * 例如2014/2/15，可得出1月1号至1月31号 无时分秒
     *
     * @param date
     * @return
     */
    public static Map<String, Date> getPreMonthFirstAndLastday(Date date) {
        if (null == date) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        Date theDate = calendar.getTime();

        // 上个月第一天
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);

        // 上个月最后一天
        calendar.add(Calendar.MONTH, 1); // 加一个月
        calendar.set(Calendar.DATE, 1); // 设置为该月第一天
        calendar.add(Calendar.DATE, -1); // 再减一天即为上个月最后一天

        Map<String, Date> map = new HashMap<String, Date>();

        map.put("firstDate", truncDate(gcLast.getTime()));
        map.put("lastDate", truncDate(calendar.getTime()));
        return map;
    }

    /**
     * 判断传入的时间是否是月初
     *
     * @param date
     * @return
     */
    public static boolean isFirstDayOfMonth(Date date) {
        if (null == date) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (calendar.get(Calendar.DAY_OF_MONTH) == 1) {
            return true;
        }
        return false;
    }

    /**
     * 获取月份最后日期
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getMaxMonthDate(String date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar.setTime(dateFormat.parse(date));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return dateFormat.format(calendar.getTime());
    }


    /**
     * 获取月份起始日期
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static String getMinMonthDate(String date) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar.setTime(dateFormat.parse(date));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return dateFormat.format(calendar.getTime());
    }


    /**
     * @param firstDate
     * @param secondDate
     * @Description:比较两个时间点 如果secondDate表示的时间等于此 firstDate 表示的时间，则返回 0 值；
     * 如果此 firstDate 的时间在参数<secondDate>表示的时间之前，则返回小于 0 的值；
     * 如果此 firstDate 的时间在参数<secondDate>表示的时间之后，则返回大于 0 的值
     * @ReturnType int
     * @author:
     * @Created 2014-11-25
     */
    public static int compare(Date firstDate, Date secondDate) {
        if (null == firstDate || null == secondDate) {
            throw new IllegalArgumentException("NullPointerException of firstDate or secondDate");
        }
        /** 使用给定的 Date 设置此 Calendar 的时间。 **/
        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.setTime(firstDate);

        /** 使用给定的 Date 设置此 Calendar 的时间。 **/
        Calendar secondCalendar = Calendar.getInstance();
        secondCalendar.setTime(secondDate);

        try {
            /**
             * 比较两个 Calendar 对象表示的时间值（从历元至现在的毫秒偏移量）。 如果参数表示的时间等于此 Calendar
             * 表示的时间，则返回 0 值； 如果此 Calendar 的时间在参数表示的时间之前，则返回小于 0 的值； 如果此
             * Calendar 的时间在参数表示的时间之后，则返回大于 0 的值
             * **/
            return firstCalendar.compareTo(secondCalendar);
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 获取上上个月的最后一天
     *
     * @param date
     * @return
     */
    public static Date getBeforeMonthLastDate(Date date) {
        //获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.truncDate(date));
        //调到上个月
        calendar.add(Calendar.MONTH, -2);
        //得到一个月最最后一天日期(31/30/29/28)
        int MaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        //按你的要求设置时间
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), MaxDay);
        return calendar.getTime();
    }

    /**
     * 获取给定时间的上个月份
     *
     * @param date
     * @author 800024
     */
    public static int getBeforeMonth(Date date) {
        //获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //调到上个月
        calendar.add(Calendar.MONTH, -1);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 拆分日期
     * eg：20150115-20150215 --> date[0]= 20150115,date[1]= 20150215
     *
     * @param dateStr
     * @return
     */
    public static Date[] splitDateStr(String dateStr) {
        if (null == dateStr) {
            return null;
        }
        if (!dateStr.contains("-")) {
            return null;
        }

        String[] dateStrs = dateStr.split("-");
        if (dateStrs.length != 2) {
            return null;
        }
        Date[] dates = new Date[2];
        dates[0] = str2Date(dateStrs[0].trim(), SHORT_DATE_FORMAT_EIGHT);
        dates[1] = str2Date(dateStrs[1].trim(), SHORT_DATE_FORMAT_EIGHT);
        return dates;
    }

    /**
     * 返回某个时间加上一定分钟后的日期
     *
     * @param date
     * @param addMinute
     * @return
     */
    public static Date addDateMinute(Date date, int addMinute) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, addMinute);// 24小时制
        date = cal.getTime();
        cal = null;
        return date;
    }


    public static Date getMonthDate(Date date, int count) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, count);
        return calendar.getTime();
    }

    /**
     * 获取月份第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayByCurMonth(Date date) {
        if (null == date) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
        return c.getTime();
    }

    /**
     * 获取月份最后一刻：年月日时分秒
     *
     * @param date
     * @return
     */
    public static Date getDateMonthEnd(Date date) {
        if (null == date) {
            return date;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //获取某月最大天数
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));

        return cal.getTime();
    }

    /**
     * @author zfc
     */
    public static Date getDateStart(Date date) {
        if (null == date) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    /**
     * @author zfc
     */
    public static Date getDateEnd(Date date) {
        if (null == date) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * @author zfc
     */
    public static Date getAddMinuteTime(Date date, int minute) {
        if (null == date) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, minute);
        return c.getTime();
    }

    /**
     * @author zfc
     */
    public static long getAbsoluteDaysBetweenDate(Date startDate, Date endDate) {
        startDate = truncDate(startDate);
        endDate = truncDate(endDate);
        if (null == startDate || endDate == null) {
            throw new NullPointerException();
        }
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        long diffSecondTime = Math.abs(startTime - endTime) / 1000;
        return diffSecondTime / 60 / 60 / 24;
    }


    /**
     * 指定时间1是否晚于指定时间2指定的间隔
     *
     * @param date1    指定时间1
     * @param date2    指定时间2
     * @param interval 指定间隔，以毫秒为单位
     * @return <code>TRUE</code>表示晚于
     */
    public static boolean after(Date date1, Date date2, long interval) {
        return date1.getTime() - date2.getTime() > interval;
    }

    /**
     * 指定时间1是否早于指定时间2指定的间隔
     *
     * @param date1    指定时间1
     * @param date2    指定时间2
     * @param interval 指定间隔，以毫秒为单位
     * @return <code>TRUE</code>表示早于
     */
    public static boolean before(Date date1, Date date2, long interval) {
        return date2.getTime() - date1.getTime() > interval;
    }

    public static String LocalDateStr(LocalDate date, String format) {
        if (null == date) {
            return null;
        }
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(format);
        return date.format(pattern);
    }

    public static String LocalDateTimeStr(LocalDateTime date, String format) {
        if (null == date) {
            return null;
        }
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(format);
        return date.format(pattern);
    }

    public static LocalDate toLocalDate(Date date) {
        if (null == date) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        if (null == date) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    /**
     * 功能描述: 获取两个时间的间隔(不包含当天)   end - st
     * st 2022-01-10  end 2022-01-16
     * return 6
     *
     * @param st  起始时间
     * @param end 截止时间
     * @return: java.lang.Long
     * @Author: 89011916
     * @Date: 2022/2/16 19:17
     */
    public static Long interval(LocalDate st, LocalDate end) {
        if (null == st || null == end) {
            return null;
        }
        return end.toEpochDay() - st.toEpochDay();
    }

    public static LocalDateTime strToLocalDateTime(String str, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(str, dateTimeFormatter);
    }

    public static LocalDate strToLocalDate(String str, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(str, dateTimeFormatter);
    }

    public static LocalDateTime localDateToLocalDateTime(LocalDate localDate) {
        return localDate.atStartOfDay();
    }


    /**
     * 给指定时间增加或减少指定时间 的开始时间
     */
    public static Date getNewDayStart(Date date, int calendar, int count) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendar, count);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 给指定时间增加或减少指定时间 的结束时间
     */
    public static Date getNewDayEnd(Date date, int calendar, int count) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendar, count);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 给指定时间增加或减少指定时间
     *
     * @param date 设置当前时间加
     */
    public static Date getNewDate(Date date, int calendar, int count) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);   //设置时间
        c.add(calendar, count); //日期分钟加1,Calendar.DATE(天),Calendar.HOUR(小时)
        return c.getTime();
    }
}
