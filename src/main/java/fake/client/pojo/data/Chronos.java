package fake.client.pojo.data;

public class Chronos {
	private String date;
	private int dayOfWeek;
	private int dayOfMonth;
	private int dayOfYear;
	private boolean isLeapYear;
	private int year;
	private int month;
	private int lengthOfMonth;
	
	public String getDate() {
		return date;
	}
	public Chronos setDate(String date) {
		this.date = date;
		return this;
	}
	public int getDayOfWeek() {
		return dayOfWeek;
	}
	public Chronos setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
		return this;
	}
	public int getDayOfMonth() {
		return dayOfMonth;
	}
	public Chronos setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
		return this;
	}
	public int getDayOfYear() {
		return dayOfYear;
	}
	public Chronos setDayOfYear(int dayOfYear) {
		this.dayOfYear = dayOfYear;
		return this;
	}
	public boolean isLeapYear() {
		return isLeapYear;
	}
	public Chronos setLeapYear(boolean isLeapYear) {
		this.isLeapYear = isLeapYear;
		return this;
	}
	public int getYear() {
		return year;
	}
	public Chronos setYear(int year) {
		this.year = year;
		return this;
	}
	public int getMonth() {
		return month;
	}
	public Chronos setMonth(int month) {
		this.month = month;
		return this;
	}
	public int getLengthOfMonth() {
		return lengthOfMonth;
	}
	public Chronos setLengthOfMonth(int lengthOfMonth) {
		this.lengthOfMonth = lengthOfMonth;
		return this;
	}
}