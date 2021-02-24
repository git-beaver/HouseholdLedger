import java.util.GregorianCalendar;
import java.util.Calendar;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Management {
	//����Ʈ ����
	ArrayList<IncomeActivity> income = new ArrayList<IncomeActivity>(10000);
	ArrayList<OutgoActivity> outgo = new ArrayList<OutgoActivity>(10000);

	private int balance = 0; //�Ѿ�
	private int indexIn = 0; //���� ��ü ����
	private int indexOut = 0; // ���� ��ü ����

	public Management() throws Exception {
		super();
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream("Household Ledger.dat"));
			this.balance = in.readInt();
			this.indexIn = in.readInt();
			this.indexOut = in.readInt();
			
			//���� �ҷ�����
			for(int i = 0; i <indexIn; i++) {
				IncomeActivity ia = (IncomeActivity) in.readObject();
				income.add(ia);
			}
			
			//���� �ҷ�����
			for(int i = 0; i<indexOut; i++) {
				OutgoActivity oa = (OutgoActivity) in.readObject();
				outgo.add(oa);
			}
		}
		catch(FileNotFoundException fnfe) {
			System.out.println("������ �������� �ʽ��ϴ�.");
		}
		catch(EOFException eofe) {
			System.out.println("��");
		}
		catch(IOException ioe) {
			System.out.println("������ ���� �� �����ϴ�.");
		}
		finally {
			try {
				in.close();
			}
			catch (Exception e) {
				
			}
		}
	}
	
	//�ܾ� get
	int getBalance() {
		return balance;
	}
	
	//���� ����
	void addIncome(IncomeActivity act) throws Exception {
		try {
			income.add(act);
		}
		catch(IndexOutOfBoundsException e) {
			throw new Exception("����Ʈ ũ�⸦ �ʰ��Ͽ����ϴ�.");
		}
		balance += act.getMoney();
	}
	
	//���� ����
	void addOutgo(OutgoActivity act) throws Exception{
		try {
			outgo.add(act);		
		}
		catch(IndexOutOfBoundsException e) {
			throw new Exception("����Ʈ ũ�⸦ �ʰ��Ͽ����ϴ�.");
		}
		balance -= act.getMoney();
	}
	
	//���� ���� ��ȯ
	IncomeActivity[] getIncome() {
		int count = 0;
		IncomeActivity incomeResult[] = new IncomeActivity[10000];
		for(int i = 0; i < income.size(); i++) {
			incomeResult[count++] = income.get(i);
		}
		return incomeResult;
	}
	
	//���� ���� ��ȯ
	OutgoActivity[] getOutgo() {
		int count = 0;
		OutgoActivity outgoResult[] = new OutgoActivity[10000];
		for(int i = 0; i < outgo.size(); i++) {
			outgoResult[count++] = outgo.get(i);
		}
		return outgoResult;
	}
	
	//Ư�� ��¥ ���� ��ȸ
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
			throw new Exception("�ش� ������ �����ϴ�.");
		}
		return incomeResult;
	}
		
	//Ư�� �Ⱓ ���� ��ȸ	
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
				throw new Exception("�ش� ������ �����ϴ�.");
			}
		}
		return incomeResult;
	}

	//Ư�� ��¥ ���� ��ȸ
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
				throw new Exception("�ش� ������ �����ϴ�.");
			}
		}
		return outgoResult;
	}

	//Ư�� �Ⱓ ���� ��ȸ
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
				throw new Exception("�ش� ������ �����ϴ�.");
			}
		}
		return outgoResult;
	}
	
	//���� ���� ����
	void deleteIncome(int a) throws Exception {
		if(income.get(0) == null)
			throw new Exception("��ϵ� ������ �����ϴ�.");
		else {
			balance -= income.get(a).getMoney();
			income.remove(a);
		}
	}
	
	//���� ���� ����
	void deleteOutgo(int a) throws Exception {
		if(outgo.get(0) == null)
			throw new Exception("��ϵ� ������ �����ϴ�.");
		else {
			balance -= outgo.get(a).getMoney();
			income.remove(a);
		}
	}

	//���� ���� ����	
	void updateIncome(int d, GregorianCalendar date, String content, int cost, int group) throws Exception {
		if(income.get(d) == null) 
			throw new Exception("��ϵ� ������ �����ϴ�.");
		else {
			balance -= income.get(d).getMoney();
			balance += cost;
			income.get(d).setDate(date);
			income.get(d).setContent(content);
			income.get(d).setMoney(cost);
			income.get(d).setGroup(group);
		}
	}
	
	//���� ���� ����
	void updateOutgo(int d, GregorianCalendar date, String content, int cost, int group) throws Exception {
		if(outgo.get(d) == null)
			throw new Exception("��ϵ� ������ �����ϴ�.");
		else {
			balance += outgo.get(d).getMoney();
			balance -= cost;
			outgo.get(d).setDate(date);
			outgo.get(d).setContent(content);
			outgo.get(d).setMoney(cost);
			outgo.get(d).setGroup(group);
		}
	}
	
	//��ü �����ϱ�
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
			System.out.println("���Ϸ� ����� �� �����ϴ�.");
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
