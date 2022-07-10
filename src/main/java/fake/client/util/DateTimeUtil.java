package fake.client.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import fake.client.pojo.data.Chronos;

public class DateTimeUtil {
	private DateTimeUtil() {}
	
	public static long localDateTimeToTimeMillis(String dateTimeStr, String pattern, String zoneOffsetId) {
		LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
		long result = dateTime.toInstant(ZoneOffset.of(zoneOffsetId)).toEpochMilli();
		return result;
	}
	
	public static String timeSecondsToDateTimeStr(long timeSeconds, String pattern, String zoneOffsetId) {
		LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timeSeconds, 0, ZoneOffset.of(zoneOffsetId));
		return dateTime.format(DateTimeFormatter.ofPattern(pattern));
	}
	
	public static Chronos calculateDate(String dateStr, String pattern, long dayOffset) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		LocalDate date = LocalDate.now();
		if(dateStr != null && !dateStr.isEmpty())
			date = LocalDate.parse(dateStr, formatter);
		date = date.plusDays(dayOffset);
		Chronos chronos = new Chronos()
				.setDate(date.format(formatter))
				.setDayOfMonth(date.getDayOfMonth())
				.setDayOfWeek(date.getDayOfWeek().getValue())
				.setDayOfYear(date.getDayOfYear())
				.setLeapYear(date.isLeapYear())
				.setLengthOfMonth(date.lengthOfMonth())
				.setMonth(date.getMonth().getValue())
				.setYear(date.getYear());
		return chronos;
	}
}
