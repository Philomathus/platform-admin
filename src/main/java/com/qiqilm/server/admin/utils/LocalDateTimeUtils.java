package com.qiqilm.server.admin.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public class LocalDateTimeUtils {

	public static final DateTimeFormatter YYYY_MM_DD_FORMATTER = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );
	public static final DateTimeFormatter YYYYMMDD_FORMATTER   = DateTimeFormatter.ofPattern( "yyyyMMdd" );

	public static final DateTimeFormatter YYYY_MM_DD_HH_MM_SS_FORMATTER = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );
	public static final DateTimeFormatter YYYYMMDDHHMMSS_FORMATTER      = DateTimeFormatter.ofPattern( "yyyyMMddHHmmss" );
	public static final DateTimeFormatter YYYYMMDDHHMMSSSSS_FORMATTER   = DateTimeFormatter.ofPattern( "yyyyMMddHHmmssSSS" );

	public static final String SPLIT_PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
	public static final String SPLIT_PATTERN_TIME     = "HH:mm:ss";
	public static final String SPLIT_PATTERN_DATE     = "yyyy-MM-dd";
	public static final String SPLIT_DATE             = "yyyyMMdd";
	public static final String SPLIT_PATTERN_MONTH    = "yyyy-MM";
	public static final String TIGHT_PATTERN_DATETIME = "yyyyMMddHHmmss";
	public static final String TIGHT_PATTERN_TIME     = "HHmmss";
	public static final String TIGHT_PATTERN_DATE     = "yyyyMMdd";

	/**
	 * 格式化LocalDate
	 *
	 * @param date
	 */
	public static String format( LocalDate date, DateTimeFormatter formatter ) {
		return date.format( formatter );
	}

	/**
	 * 默认格式化LocalDate
	 *
	 * @param date
	 */
	public static String format( LocalDate date ) {
		return date.format( LocalDateTimeUtils.YYYY_MM_DD_FORMATTER );
	}

	/**
	 * 格式化LocalDateTime
	 *
	 * @param time
	 */
	public static String format( LocalDateTime time, DateTimeFormatter formatter ) {
		return time.format( formatter );
	}

	/**
	 * 默认格式化LocalDateTime
	 *
	 * @param time
	 */
	public static String format( LocalDateTime time ) {
		return time.format( LocalDateTimeUtils.YYYY_MM_DD_HH_MM_SS_FORMATTER );
	}

	/**
	 * 将字符串转化为LocalDate
	 *
	 * @param dateStr
	 */
	public static LocalDate parseLocalDate( String dateStr, DateTimeFormatter formatter ) {
		return LocalDate.parse( dateStr, formatter );
	}

	/**
	 * 将字符串转化为LocalDate 默认
	 *
	 * @param dateStr
	 */
	public static LocalDate parseLocalDate( String dateStr ) {
		return LocalDate.parse( dateStr, YYYY_MM_DD_FORMATTER );
	}

	/**
	 * 将字符串转化为LocalDateTime
	 *
	 * @param dateStr
	 */
	public static LocalDateTime parseLocalDateTime( String dateStr, DateTimeFormatter formatter ) {
		return LocalDateTime.parse( dateStr, formatter );
	}

	/**
	 * 将字符串转化为LocalDateTime
	 *
	 * @param dateStr
	 */
	public static LocalDateTime parseLocalDateTime( String dateStr ) {
		return LocalDateTime.parse( dateStr, YYYY_MM_DD_HH_MM_SS_FORMATTER );
	}

	public static LocalDateTime getDateTimeOfTimestamp( long timestamp ) {
		Instant instant = Instant.ofEpochMilli( timestamp );
		return LocalDateTime.ofInstant( instant, ZoneId.systemDefault() );
	}

	//获取指定日期的毫秒
	public static Long getMilliByTime( LocalDateTime time ) {
		return time.atZone( ZoneId.systemDefault() ).toInstant().toEpochMilli();
	}

	//获取指定日期的秒
	public static Long getSecondsByTime( LocalDateTime time ) {
		return time.atZone( ZoneId.systemDefault() ).toInstant().getEpochSecond();
	}

	//获取指定时间的指定格式
	public static String formatTime( LocalDateTime time, String pattern ) {
		return time.format( DateTimeFormatter.ofPattern( pattern ) );
	}

	//获取当前时间的指定格式
	public static String formatNow( String pattern ) {
		return formatTime( LocalDateTime.now(), pattern );
	}

	//日期加上一个数,根据field不同加不同值,field为ChronoUnit.*
	public static LocalDateTime plus( LocalDateTime time, long number, TemporalUnit field ) {
		return time.plus( number, field );
	}

	//日期减去一个数,根据field不同减不同值,field参数为ChronoUnit.*
	public static LocalDateTime minu( LocalDateTime time, long number, TemporalUnit field ) {
		return time.minus( number, field );
	}

	/**
	 * 获取两个日期的差  field参数为ChronoUnit.*
	 *
	 * @param field 单位(年月日时分秒)
	 * @return
	 */
	public static long betweenTwoTime( LocalDateTime startTime, LocalDateTime endTime, ChronoUnit field ) {
		Period period = Period.between( LocalDate.from( startTime ), LocalDate.from( endTime ) );
		if ( field == ChronoUnit.YEARS ) {
			return period.getYears();
		}
		if ( field == ChronoUnit.MONTHS ) {
			return period.getYears() * 12 + period.getMonths();
		}
		return field.between( startTime, endTime );
	}

	//获取一天的开始时间，2017,7,22 00:00
	public static LocalDateTime getDayStart( LocalDateTime time ) {
		return time.withHour( 0 ).withMinute( 0 ).withSecond( 0 ).withNano( 0 );
	}

	//获取一天的结束时间，2017,7,22 23:59:59.999999999
	public static LocalDateTime getDayEnd( LocalDateTime time ) {
		return time.withHour( 23 ).withMinute( 59 ).withSecond( 59 ).withNano( 999999999 );
	}

	public  static String getFormatTimeSecond(long          sec){
		LocalDateTime dateTime = LocalDateTimeUtils.getDateTimeOfTimestamp( sec * 1000L );
		return  LocalDateTimeUtils.formatTime( dateTime, LocalDateTimeUtils.SPLIT_PATTERN_DATETIME );
	}

	/**
	 * 判断是否同一天
	 *
	 * @param one
	 * @param two
	 */
	public static boolean isSameDay( LocalDateTime one, LocalDateTime two ) {
		return one.toLocalDate().compareTo( two.toLocalDate() ) == 0;
	}

	/**
	 * 获得今天的 23:59:59
	 */
	public static LocalDateTime getEndOfToday() {
		LocalDate localDate = LocalDate.now();
		return localDate.atTime( 23, 59, 59 );
	}
}
