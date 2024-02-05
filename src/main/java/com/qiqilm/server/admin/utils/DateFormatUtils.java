package com.qiqilm.server.admin.utils;


import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>Title: DateFormatUtils</p>
 * <p>Description: 时间格式工具类</p>
 *
 * @author Xuerong Xue
 * @author admin
 * @date 2015/12/12.
 * @date 2015/12/22.
 */
@Slf4j
public abstract class DateFormatUtils {
	public static final String SPLIT_PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
	public static final String SPLIT_PATTERN_TIME     = "HH:mm:ss";
	public static final String SPLIT_PATTERN_DATE     = "yyyy-MM-dd";
	public static final String SPLIT_PATTERN_MONTH    = "yyyy-MM";
	public static final String TIGHT_PATTERN_DATETIME = "yyyyMMddHHmmss";
	public static final String TIGHT_PATTERN_TIME     = "HHmmss";
	public static final String TIGHT_PATTERN_DATE     = "yyyyMMdd";
	// Web接口统一时间格式
	public static final SimpleDateFormat SPLIT_FORMAT_DATETIME = new SimpleDateFormat( SPLIT_PATTERN_DATETIME );
	public static final SimpleDateFormat SPLIT_FORMAT_TIME     = new SimpleDateFormat( SPLIT_PATTERN_TIME );
	public static final SimpleDateFormat SPLIT_FORMAT_DATE     = new SimpleDateFormat( SPLIT_PATTERN_DATE );
	public static final SimpleDateFormat TIGHT_FORMAT_DATETIME = new SimpleDateFormat( TIGHT_PATTERN_DATETIME );
	public static final SimpleDateFormat TIGHT_FORMAT_TIME     = new SimpleDateFormat( TIGHT_PATTERN_TIME );
	public static final SimpleDateFormat TIGHT_FORMAT_DATE     = new SimpleDateFormat( TIGHT_PATTERN_DATE );
	private DateFormatUtils() {
		throw new RuntimeException( "DateFormatUtils.class can't be instantiated" );
	}

	/**
	 * <p>使用默认时间格式（yyyy-MM-dd HH:mm:ss）进行时间格式化</p>
	 * <p>add by xuexuerong 20151212</p>
	 *
	 * @param date 待格式化时间
	 * @return 格式化后的时间字符串
	 */
	public static String formate( Date date ) {
		return formate( date, SPLIT_PATTERN_DATETIME );
	}

	/**
	 * <p>使用指定的时间格式进行时间格式化</p>
	 * <p>add by xuexuerong 20151212</p>
	 *
	 * @param date    待格式化时间
	 * @param pattern 时间格式
	 * @return 格式化后的时间字符串
	 */
	public static String formate( Date date, String pattern ) {
		return new SimpleDateFormat( pattern ).format( date );
	}

	/**
	 * <p>使用默认时间格式（yyyy-MM-dd HH:mm:ss）进行时间解析</p>
	 * <p>add by xuexuerong 20151212</p>
	 *
	 * @param date 待解析的时间字符串
	 * @return 解析后的时间对象
	 */
	public static Date parse( String date ) {
		return parse( date, SPLIT_PATTERN_DATETIME );
	}

	/**
	 * <p>使用指定的时间格式进行时间解析</p>
	 * <p>add by xuexuerong 20151212</p>
	 *
	 * @param date    待解析的时间字符串
	 * @param pattern 时间格式
	 * @return 解析后的时间对象
	 */
	public static Date parse( String date, String pattern ) {
		try {
			return new SimpleDateFormat( pattern ).parse( date );
		} catch ( ParseException e ) {
			log.error( e.getMessage(), e );
			return null;
		}
	}

	/**
	 * 获取间隔为sec的时间
	 *
	 * @return
	 * @throws Exception
	 */
	public static Date addSec( Date date, int sec ) throws Exception {
		Calendar cl = Calendar.getInstance();
		try {
			cl.setTime( date );
			cl.add( Calendar.SECOND, sec );
			return parse( SPLIT_FORMAT_DATETIME.format( cl.getTime() ) );
		} catch ( Exception e ) {
			throw new Exception( "时间格式化报错" + e );
		}
	}

	/**
	 * 获取间隔为day的时间
	 *
	 * @return
	 * @throws Exception
	 */
	public static Date addDay( Date date, int day ) throws Exception {
		Calendar cl = Calendar.getInstance();
		try {
			cl.setTime( date );
			cl.add( Calendar.DATE, day );
			return parse( SPLIT_FORMAT_DATETIME.format( cl.getTime() ) );
		} catch ( Exception e ) {
			throw new Exception( "时间格式化报错" + e );
		}
	}

	/**
	 * 获取间隔为hour的时间
	 *
	 * @return
	 * @throws Exception
	 */
	public static Date addHour( Date date, int hour ) throws Exception {
		Calendar cl = Calendar.getInstance();
		try {
			cl.setTime( date );
			cl.add( Calendar.HOUR, hour );
			return parse( SPLIT_FORMAT_DATETIME.format( cl.getTime() ) );
		} catch ( Exception e ) {
			throw new Exception( "时间格式化报错" + e );
		}
	}


	/**
	 * 获取间隔为min的时间
	 *
	 * @return
	 * @throws Exception
	 */
	public static Date addMin( Date date, int min ) throws Exception {
		Calendar cl = Calendar.getInstance();
		try {
			cl.setTime( date );
			cl.add( Calendar.MINUTE, min );
			return parse( SPLIT_FORMAT_DATETIME.format( cl.getTime() ) );
		} catch ( Exception e ) {
			throw new Exception( "时间格式化报错" + e );
		}
	}

	/**
	 * 获取间隔为month的时间
	 *
	 * @return
	 * @throws Exception
	 */
	public static Date addMonth( Date date, int month ) throws Exception {
		Calendar cl = Calendar.getInstance();
		try {
			cl.setTime( date );
			cl.add( Calendar.MONTH, month );
			return parse( SPLIT_FORMAT_DATETIME.format( cl.getTime() ) );
		} catch ( Exception e ) {
			throw new Exception( "时间格式化报错" + e );
		}
	}

	/**
	 * 获取两个时间间隔(毫秒)
	 *
	 * @return
	 * @throws Exception
	 */
	public static long getIntervalTime( Date date1, Date date2 ) throws Exception {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		long     l  = 0;
		try {
			c1.setTime( date1 );
			c2.setTime( date2 );
			l = c2.getTimeInMillis() - c1.getTimeInMillis();
		} catch ( Exception e ) {
			throw new Exception( "时间格式化报错" + e );
		}
		return l;
	}

	/**
	 * 获取倒计时(分钟)
	 *
	 * @return
	 * @throws Exception
	 */
	public static String getCountDownTime( Date date, long min ) throws Exception {
		Calendar c1           = Calendar.getInstance();
		Calendar c2           = Calendar.getInstance();
		long     l            = 0;
		long     countDown    = 0;
		String   countDownStr = "00";
		try {
			c1.setTime( date );
			c2.setTime( new Date() );
			l = ( c2.getTimeInMillis() - c1.getTimeInMillis() ) / 1000;
			if ( l < min * 60 ) {
				countDown = ( min * 60 - l ) / 60 + 1;
			}
		} catch ( Exception e ) {
			throw new Exception( "时间格式化报错" + e );
		}
		if ( countDown < 10 ) {
			countDownStr = "0" + countDown;
		} else {
			countDownStr = "" + countDown;
		}
		return countDownStr;
	}

	public static String getToday( String pattern ) {
		Date date = new Date();
		return new SimpleDateFormat( pattern ).format( date );
	}

	/**
	 * <p>把日期时间格式转换为日期格式</p>
	 * <p>add by Chen Nan 20160504</p>
	 *
	 * @param datetime 日期时间
	 * @return 日期 (时间为0点0分0秒)
	 */
	public static Date datetime2Date( Date datetime ) {
		String dateStr = DateFormatUtils.formate( datetime, DateFormatUtils.SPLIT_PATTERN_DATE );
		return DateFormatUtils.parse( dateStr, DateFormatUtils.SPLIT_PATTERN_DATE );
	}


	public static boolean isSameDay( Date date1, Date date2 ) {
		try {
			String d1 = formate( date1, "yyyyMMdd" );
			String d2 = formate( date2, "yyyyMMdd" );
			return d1.equals( d2 );
		} catch ( Exception e ) {

		}
		return false;
	}

	public static boolean isSameWeek( Date date1, Date date2 ) {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime( date1 );
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime( date2 );
		return calendar1.get( Calendar.WEEK_OF_YEAR ) == calendar2.get( Calendar.WEEK_OF_YEAR );
	}

	public static boolean isSameMonth( Date date1, Date date2 ) {
		try {
			String d1 = formate( date1, SPLIT_PATTERN_MONTH );
			String d2 = formate( date2, SPLIT_PATTERN_MONTH );
			return d1.equals( d2 );
		} catch ( Exception e ) {

		}
		return false;
	}

	/**
	 * <p>毫秒数转天数</p>
	 * <p>add by Chen Nan 20160504</p>
	 *
	 * @param msec 毫秒数
	 * @return 日期 (时间为0点0分0秒)
	 */
	public static int msec2day( long msec ) {
		return ( int ) Math.ceil( ( double ) msec / ( 1000.0 * 60.0 * 60.0 * 24.0 ) );
	}

	/**
	 * 获取月份天数
	 *
	 * @return
	 */
	public static int getDaysOfMonth( Date date ) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( date );
		return calendar.getActualMaximum( Calendar.DAY_OF_MONTH );
	}

	/**
	 * 获取小时
	 *
	 * @return
	 */
	public static int getDaysOfHour( Date date ) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( date );
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取当前是一年中的第几天
	 *
	 * @return
	 */
	public static int getDaysOfYear( Date date ) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( date );
		return calendar.get( Calendar.DAY_OF_YEAR );
	}

	/**
	 * 秒数转xx:xx:xx
	 */
	public static String secToTime( Integer time ) {
		if ( time == null )
			return "";
		if ( time == null )
			time = 0;
		String timeStr = null;
		int    hour    = 0;
		int    minute  = 0;
		int    second  = 0;
		if ( time <= 0 )
			return "00:00";
		else {
			minute = time / 60;
			if ( minute < 60 ) {
				second = time % 60;
				timeStr = unitFormat( minute ) + ":" + unitFormat( second );
			} else {
				hour = minute / 60;
				if ( hour > 99 )
					return "99:59:59";
				minute = minute % 60;
				second = time - hour * 3600 - minute * 60;
				timeStr = unitFormat( hour ) + ":" + unitFormat( minute ) + ":" + unitFormat( second );
			}
		}
		return timeStr;
	}

	/**
	 * 获取周
	 * @param l
	 * @return
	 */
	public static int getWeekOfYear(LocalDate l ){
		WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY,1);
		return l.get(weekFields.weekOfYear());
	}

	/**
	 * 获取年
	 * @param l
	 * @return
	 */
	public static int getYear(LocalDate l){
		return l.getYear();
	}

	public static String unitFormat( int i ) {
		String retStr = null;
		if ( i >= 0 && i < 10 ) {
			retStr = "0" + i;
		} else {
			retStr = "" + i;
		}
		return retStr;
	}

	public static Long dateToMillionSeconds( Date date ) {
		if ( date == null ) {
			return 0L;
		}
		return date.getTime();
	}

	public static Date millionSecondsToDate( Long millonSeconds ) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis( millonSeconds );
		Date date = c.getTime();
		return date;
	}

	public static Date addYears( Date date, Integer year ) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime( date );
		calendar.add( Calendar.YEAR, year );
		return calendar.getTime();
	}

	public static Date getNowMonth() {
		String month = formate( new Date(), "yyyy-MM" );
		return parse( month, "yyyy-MM" );

	}

	/**
	 * 获取今天凌晨的Date
	 *
	 * @return Date
	 */
	public static Date getTodayMorning() {
		Calendar cal = Calendar.getInstance();
		cal.set( Calendar.HOUR_OF_DAY, 0 );
		cal.set( Calendar.MINUTE, 0 );
		cal.set( Calendar.SECOND, 0 );
		return cal.getTime();
	}

	/**
	 * 获取明天凌晨的Date
	 *
	 * @return Date
	 */
	public static Date getTomorrowMorning(Date dateCur) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateCur);
		cal.add( Calendar.DATE, 1 );
		cal.set( Calendar.HOUR_OF_DAY, 0 );
		cal.set( Calendar.MINUTE, 0 );
		cal.set( Calendar.SECOND, 0 );
		return cal.getTime();
	}

	public static String beiJinToMeiDong(Date date,String style){
		SimpleDateFormat sdf8=new SimpleDateFormat(style);
		sdf8.setTimeZone(TimeZone.getTimeZone("America/Caracas"));
		String dataTime=sdf8.format(date);
		return dataTime;
	}

	public static String convertToBeijingTime(Date date){
		SimpleDateFormat sdf8=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf8.setTimeZone(TimeZone.getTimeZone("GMT+8"));//委内瑞拉时间 (加拉加斯)
		//System.out.println("东八区的时间:"+sdf8.format(date));//输出格式化日期
		String dataTime=sdf8.format(date);
		return dataTime;
	}


	public static String convertMeidongTOBeijing(String date1) throws ParseException {
		SimpleDateFormat sm=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sm.setTimeZone(TimeZone.getTimeZone("America/Caracas"));
		Date date3=sm.parse(date1);
		String resultDate=DateFormatUtils.convertToBeijingTime(date3);
		return resultDate;
	}

	/**
	 * 拼接每日任务有效时间范围
	 * @return 例：2021-08-12 00:00:00至2021-08-12 23:59:59
	 */
	public static String getZeroToDayOver() {
		Calendar calendar2 = Calendar.getInstance();
		calendar2.set(calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH),
				23, 59, 59);
		Date endOfDate = calendar2.getTime();
		return SPLIT_FORMAT_DATETIME.format( getTodayMorning()).concat("至").concat(SPLIT_FORMAT_DATETIME.format(endOfDate));
	}

	public static long getTodayEndTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime().getTime();
	}



	public static void main(String[] args) {
		for (int i = 1; i < 10 ;i ++){
			String stringDate = "2021-0"+i+"-05 11:00:00";
			Date testDate = parse(stringDate);
			System.err.println(testDate);
			try {
				System.out.println(convertMeidongTOBeijing(stringDate));
			} catch (ParseException e) {
				log.error( e.getMessage(), e );
			}
		}
		for (int i = 10; i < 13 ;i ++){
			String stringDate = "2021-0"+i+"-05 11:00:00";
			Date testDate = parse(stringDate);
			System.err.println(testDate);
			try {
				System.out.println(convertMeidongTOBeijing(stringDate));
			} catch (ParseException e) {
				log.error( e.getMessage(), e );
			}
		}
	}
}
