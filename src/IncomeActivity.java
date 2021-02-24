
import java.util.GregorianCalendar;

public class IncomeActivity extends Activity{
		private int group; //분류
		
		//생성자
		IncomeActivity(GregorianCalendar date, String content, int money, int group) {
			super(date, content, money);
			this.group = group;
		}
		
		//get, set
		void setGroup(int group) {
			this.group = group;
		}
		
		int getGroupNum() {
			return group;
		}

		public String toString() {
			return "날짜 : " + getDate().get(GregorianCalendar.YEAR) + "년 " +
					(getDate().get(GregorianCalendar.MONTH)) + "월 " +
					getDate().get(GregorianCalendar.DAY_OF_MONTH) + "일, 내역 : "
					+ getContent() + ", 금액 : " + getMoney() + ", 분류 : " + getGroupNum();
		}
	
		
}
