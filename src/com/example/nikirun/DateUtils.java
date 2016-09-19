package com.example.nikirun;

import android.annotation.SuppressLint;
import android.util.Log;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressLint("SimpleDateFormat")
public class DateUtils {

    // 获取当前日期
    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(c.getTime());
    }

    public static int[] getYMDArray(String datetime, String splite) {
        int[] date = { 0, 0, 0, 0, 0 };
        if (datetime != null && datetime.length() > 0) {
            String[] dates = datetime.split(splite);
            int position = 0;
            for (String temp : dates) {
                date[position] = Integer.valueOf(temp);
                position++;
            }
        }
        return date;
    }

    /**
     * 将当前时间戳转化为标准时间函数
     * 
     * @param timestamp
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTime(String time1) {

        int timestamp = Integer.parseInt(time1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = null;
        try {
            String str = sdf.format(new Timestamp(intToLong(timestamp)));
            time = str.substring(11, 16);
            String month = str.substring(5, 7);
            String day = str.substring(8, 10);
            time = getDate(month, day) + time;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;
    }

    public static String getTime(int timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = null;
        try {
            String str = sdf.format(new Timestamp(intToLong(timestamp)));
            time = str.substring(11, 16);

            String month = str.substring(5, 7);
            String day = str.substring(8, 10);
            time = getDate(month, day) + time;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;
    }

    public static String getHMS(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = null;
        try {
            return sdf.format(new Date(timestamp));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;
    }

    /**
     * 将当前时间戳转化为标准时间函数
     * 
     * @param timestamp
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getHMS(String time) {

        long timestamp = Long.parseLong(time);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            String str = sdf.format(new Timestamp(timestamp));
            return str;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;
    }

    // java Timestamp构造函数需传入Long型
    public static long intToLong(int i) {
        long result = (long) i;
        result *= 1000;
        return result;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDate(String month, String day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 24小时制
        java.util.Date d = new java.util.Date();
        ;
        String str = sdf.format(d);
        @SuppressWarnings("unused")
        String nowmonth = str.substring(5, 7);
        String nowday = str.substring(8, 10);
        String result = null;

        int temp = Integer.parseInt(nowday) - Integer.parseInt(day);
        switch (temp) {
            case 0:
                result = "今天";
                break;
            case 1:
                result = "昨天";
                break;
            case 2:
                result = "前天";
                break;
            default:
                StringBuilder sb = new StringBuilder();
                sb.append(Integer.parseInt(month) + "月");
                sb.append(Integer.parseInt(day) + "日");
                result = sb.toString();
                break;
        }
        return result;
    }

    /* 将字符串转为时间戳 */
    public static String getTimeToStamp(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);
        Date date = new Date();
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String tmptime = String.valueOf(date.getTime()).substring(0, 10);

        return tmptime;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getYMD(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(timestamp));
    }

    public static String getDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sdf.format(new Date(timestamp * 1000));
    }

    public static String getTimestamp() {
        long time = System.currentTimeMillis() / 1000;
        return String.valueOf(time);
    }
    
    /**
     * 计算时间间隔 传入时间格式"yyyy-MM-dd HH:mm:ss"
     */
    public static String getInterval(String startTime, String finishedTime) {
		String interval = null;
		
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ParsePosition position = new ParsePosition(0);
		Date date1 = sDateFormat.parse(finishedTime,position);//解析后pos会指向结尾；
		position.setIndex(0);
		Date date2 = sDateFormat.parse(startTime,position);
		//时间戳相减
		long time=0;
		
		if(date1 != null && date2 != null)
			time = date1.getTime() - date2.getTime();
		
		//如果间隔时间小于60秒
		if(time == 0){
			return "0";
		}
		if(time/1000 < 60 && time/1000 > 0){
			interval = (time%60000)/1000 + "s";
		}else if (time/60000 <60 && time/60000 > 0 ) {
		//如果间隔时间小于60分钟
			int s = (int) ((time%60000)/1000);
			int m = (int) ((time%3600000)/60000);
			interval = m+"min"+s+"s";
		}else if (time/3600000 >= 0 && time/3600000 < 24) {
			int h = (int) (time/3600000);
			int m = (int) ((time%3600000)/60000);
			int s = (int) ((time%60000)/1000);
			interval = h +"h"+m+"min"+s+"s";
		}
		 
    	return interval;
	}
    
    
    /**
	 * 得到当前日期是星期几。
	 * @return 当为周日时，返回0，当为周一至周六时，则返回对应的1-6。
	 */
    public static final int getCurrentDayOfWeek() {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
	}
    
    
    /**
    * 根据日期取得星期几
    * @param date
    * @return  "日","一","二","三","四","五","六"
    */
    public static String getWeek(Date date){
    	String[] weeks = {"日","一","二","三","四","五","六"};
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
    	if(week_index<0){
    		week_index = 0;
    	}
    	return weeks[week_index];
    }
    

}