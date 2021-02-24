
import java.util.GregorianCalendar;

public class IncomeActivity extends Activity{
		private int group; //�з�
		
		//������
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
			return "��¥ : " + getDate().get(GregorianCalendar.YEAR) + "�� " +
					(getDate().get(GregorianCalendar.MONTH)) + "�� " +
					getDate().get(GregorianCalendar.DAY_OF_MONTH) + "��, ���� : "
					+ getContent() + ", �ݾ� : " + getMoney() + ", �з� : " + getGroupNum();
		}
	
		
}
