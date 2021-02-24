import java.util.GregorianCalendar;
import java.util.Calendar;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Management {
	//리스트 생성
	ArrayList<IncomeActivity> income = new ArrayList<IncomeActivity>(10000);
	ArrayList<OutgoActivity> outgo = new ArrayList<OutgoActivity>(10000);

	private int balance = 0; //총액
	private int indexIn = 0; //수입 객체 갯수
	private int indexOut = 0; // 지출 객체 갯수

	public Management() throws Exception {
		super();
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream("Household Ledger.dat"));
			this.balance = in.readInt();
			this.indexIn = in.readInt();
			this.indexOut = in.readInt();
			
			//수입 불러오기
			for(int i = 0; i <indexIn; i++) {
				IncomeActivity ia = (IncomeActivity) in.readObject();
				income.add(ia);
			}
			
			//지출 불러오기
			for(int i = 0; i<indexOut; i++) {
				OutgoActivity oa = (OutgoActivity) in.readObject();
				outgo.add(oa);
			}
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("파일이 존재하지 않습니다.");
		}
		catch(EOFException eofe) {
			System.out.println("끝");
		}
		catch(IOException ioe) {
			System.out.println("파일을 읽을 수 없습니다.");
		}
		finally {
			try {
				in.close();
			}
			catch (Exception e) {
				
			}
		}
	}
	
	//잔액 get
	int getBalance() {
		return balance;
	}
	
	//수입 삽입
	void addIncome(IncomeActivity act) throws Exception {
		try {
			income.add(act);
		}
		catch(IndexOutOfBoundsException e) {
			throw new Exception("리스트 크기를 초과하였습니다.");
		}
		balance += act.getMoney();
	}
	
	//지출 삽입
	void addOutgo(OutgoActivity act) throws Exception{
		try {
			outgo.add(act);		
		}
		catch(IndexOutOfBoundsException e) {
			throw new Exception("리스트 크기를 초과하였습니다.");
		}
		balance -= act.getMoney();
	}
	
	//수입 내역 반환
	IncomeActivity[] getIncome() {
		int count = 0;
		IncomeActivity incomeResult[] = new IncomeActivity[10000];
		for(int i = 0; i < income.size(); i++) {
			incomeResult[count++] = income.get(i);
		}
		return incomeResult;
	}
	
	//지출 내역 반환
	OutgoActivity[] getOutgo() {
		int count = 0;
		OutgoActivity outgoResult[] = new OutgoActivity[10000];
		for(int i = 0; i < outgo.size(); i++) {
			outgoResult[count++] = outgo.get(i);
		}
		return outgoResult;
	}
	
	//특정 날짜 수입 조회
	IncomeActivity[] searchIncome(GregorianCalendar someday) throws Exception {
		int count = 0;
		IncomeActivity incomeResult[] = new IncomeActivity[10000];
		for(int i = 0; i<income.size(); i++) {
			if(income.get(i) != null) {
				GregorianCalendar date = income.get(i).getDate();
				if(date.equals(someday)) {
					incomeResult[count++] = income.get(i);
				}
				else {
					continue;
				}
			} 
		}
		if (count == 0) {
			throw new Exception("해당 내역이 없습니다.");
		}
		return incomeResult;
	}
		
	//특정 기간 수입 조회	
	IncomeActivity[] searchIncome(GregorianCalendar from, GregorianCalendar to) throws Exception {
		int count = 0;
		IncomeActivity incomeResult[] = new IncomeActivity[10000];
		for(int i = 0; i < income.size(); i++) {
			if(income.get(i) != null) {
				GregorianCalendar date = income.get(i).getDate();
				if(date.equals(from) || date.after(from) && date.before(to) || date.equals(to)) {
					incomeResult[count++] = income.get(i);
				}
				else {
					continue;
				}
			}
			if (count == 0) {
				throw new Exception("해당 내역이 없습니다.");
			}
		}
		return incomeResult;
	}

	//특정 날짜 지출 조회
	OutgoActivity[] searchOutgo(GregorianCalendar someday) throws Exception {
		int count = 0;
		OutgoActivity outgoResult[] = new OutgoActivity[10000];
		for(int i = 0; i < outgo.size(); i++) {
			if(outgo.get(i) != null) {
				GregorianCalendar date = outgo.get(i).getDate();
				if(date.equals(someday)) {
					outgoResult[count++] = outgo.get(i);
				}
				else {
					continue;
				}
			}
			if (count == 0) {
				throw new Exception("해당 내역이 없습니다.");
			}
		}
		return outgoResult;
	}

	//특정 기간 지출 조회
	OutgoActivity[] searchOutgo(GregorianCalendar from, GregorianCalendar to) throws Exception {
		int count = 0;
		OutgoActivity outgoResult[] = new OutgoActivity[10000];
		for(int i = 0; i<outgo.size(); i++) {
			if(outgo.get(i) != null) {
				GregorianCalendar date = outgo.get(i).getDate();
				if(date.equals(from) || date.after(from) && date.before(to) || date.equals(to)) {
					outgoResult[count++] = outgo.get(i);
				}
				else {
					continue;
				}
			}
			if (count == 0) {
				throw new Exception("해당 내역이 없습니다.");
			}
		}
		return outgoResult;
	}
	
	//수입 내역 삭제
	void deleteIncome(int a) throws Exception {
		if(income.get(0) == null)
			throw new Exception("등록된 내역이 없습니다.");
		else {
			balance -= income.get(a).getMoney();
			income.remove(a);
		}
	}
	
	//지출 내역 삭제
	void deleteOutgo(int a) throws Exception {
		if(outgo.get(0) == null)
			throw new Exception("등록된 내역이 없습니다.");
		else {
			balance -= outgo.get(a).getMoney();
			income.remove(a);
		}
	}

	//수입 내역 수정	
	void updateIncome(int d, GregorianCalendar date, String content, int cost, int group) throws Exception {
		if(income.get(d) == null) 
			throw new Exception("등록된 내용이 없습니다.");
		else {
			balance -= income.get(d).getMoney();
			balance += cost;
			income.get(d).setDate(date);
			income.get(d).setContent(content);
			income.get(d).setMoney(cost);
			income.get(d).setGroup(group);
		}
	}
	
	//지출 내역 수정
	void updateOutgo(int d, GregorianCalendar date, String content, int cost, int group) throws Exception {
		if(outgo.get(d) == null)
			throw new Exception("등록된 내용이 없습니다.");
		else {
			balance += outgo.get(d).getMoney();
			balance -= cost;
			outgo.get(d).setDate(date);
			outgo.get(d).setContent(content);
			outgo.get(d).setMoney(cost);
			outgo.get(d).setGroup(group);
		}
	}
	
	//전체 저장하기
	void saveAll() {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream("Household Ledger.dat"));
			out.writeInt(balance);
			out.writeInt(income.size());
			out.writeInt(outgo.size());
			for(int i = 0; i<income.size(); i++) {
				out.writeObject(income.get(i));
			}
			for(int i = 0; i < outgo.size(); i++) {
				out.writeObject(outgo.get(i));
			}
		}
		catch(IOException ioe) {
			System.out.println("파일로 출력할 수 없습니다.");
		}
		finally {
			try {
				out.close();
			}
			catch(Exception e) {
				System.out.println("close error!");
			}
		}

	}
	
}
