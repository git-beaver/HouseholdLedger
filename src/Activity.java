import java.util.GregorianCalendar;
import java.util.Calendar;
import java.io.*;

class Activity implements Serializable {
	GregorianCalendar date = new GregorianCalendar(); //날짜
	private String content; //세부내역
	private int money; //금액

	//생성자
	Activity(GregorianCalendar date, String content, int money) {
		this.date = date;
		this.content = content;
		this.money = money;
	}
	
	//get, set
	void setDate(GregorianCalendar date) {
		this.date = date;
	}
	
	GregorianCalendar getDate() {
		return date;
	}
	
	
	void setContent(String content) {
		this.content = content;
	}
	
	String getContent() {
		return content;
	}
	
	void setMoney(int money) {
		this.money = money;
	}
	
	int getMoney() {
		return money;
	}
}

