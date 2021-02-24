import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.sql.Date;

public class UserInterface {
	static Connection conn = null;
	public static void main(String args[]) throws Exception {
		//Management management = new Management();
		//DB ����
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountBook?allowPublicKeyRetrieval=true&useSSL=false", "root", "apmsetup");
			System.out.println("�����ͺ��̽��� �����߽��ϴ�.");
		}
		catch (ClassNotFoundException cnfe) {
			System.out.println("�ش� Ŭ������ ã�� �� �����ϴ�" + cnfe.getMessage()); 
		}
		catch (SQLException se) {
			System.out.println(se.getMessage());
		}
				
		JFrame frame = new JFrame("HouseholdLedger");
		frame.setPreferredSize(new Dimension(700,450));
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//����� ��� ���α׷��� �� Ŭ�� ��� �ۼ�
		JLabel programName = new JLabel("����� ���α׷�");
		programName.setBounds(253, 22, 152, 37);
		programName.setFont(new Font("�������", Font.PLAIN, 20));
		frame.getContentPane().add(programName);
		
		JLabel menuOrder = new JLabel("�����Ͻ� �޴� ��ư�� Ŭ�����ּ���");
		menuOrder.setBounds(209, 71, 245, 37);
		menuOrder.setFont(new Font("�������", Font.PLAIN, 15));
		frame.getContentPane().add(menuOrder);
		
		//���� ���� �Է� �� ����
		JButton inButton = new JButton("���� ���� �Է� �� ����");
		inButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				JFrame addInFrame = new JFrame("���� ���� �Է� �� ����");
				addInFrame.setPreferredSize(new Dimension(750,450));
				Container addInPane = addInFrame.getContentPane();
				String inColNames[] = {"��¥", "����", "�ݾ�", "�з�"};
				DefaultTableModel inModel = new DefaultTableModel (inColNames, 0);
				JTable incomeTable = new JTable(inModel);
				addInPane.add(new JScrollPane(incomeTable), BorderLayout.CENTER);
				JPanel inPanel = new JPanel();
				JTextField textYear = new JTextField(4); JTextField textMonth = new JTextField(2); JTextField textDay = new JTextField(2);
				JTextField textContent = new JTextField(10);
				JTextField textCost = new JTextField(7);
				JComboBox<String> groupBox = new JComboBox<String>();
				JButton in_add_button = new JButton("�߰�");
				JButton in_del_button = new JButton("����");
				groupBox.setModel(new DefaultComboBoxModel<String>(new String[] {"���� �з�", "�޿�", "�뵷", "��������", "��Ÿ"}));
				inPanel.add(new JLabel("��¥(��,��,��)"));
				inPanel.add(textYear); inPanel.add(textMonth); inPanel.add(textDay);
				inPanel.add(new JLabel("����"));
				inPanel.add(textContent);
				inPanel.add(new JLabel("�ݾ�"));
				inPanel.add(textCost);
				inPanel.add(new JLabel("�з�"));
				inPanel.add(groupBox);
				inPanel.add(in_add_button);
				inPanel.add(in_del_button);
				addInPane.add(inPanel, BorderLayout.SOUTH);
				
				//���� ���� ���� �ҷ��� ����ϱ�
				ResultSet iresult = null;
				Statement stmt = null;
				try {
					stmt = conn.createStatement();
					iresult = stmt.executeQuery("select * from income");
					while(iresult.next()) {
						Object data[] = {iresult.getDate(1),  iresult.getString(3), iresult.getInt(2), iresult.getString(4)};
						DefaultTableModel model = (DefaultTableModel) incomeTable.getModel();
						model.addRow(data);
					}
				}
				catch(SQLException se) {
					se.getMessage();
				}
				catch(NullPointerException npe) {
					npe.getMessage();
				}
				
				
				//���� ���� ����
				in_add_button.addActionListener (new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						Statement stmt = null;
						try {
							stmt = conn.createStatement();
							int year = Integer.parseInt(textYear.getText());
							int month = Integer.parseInt(textMonth.getText());
							int day = Integer.parseInt(textDay.getText());
							String content = textContent.getText();
							int money = Integer.parseInt(textCost.getText());
							String group = groupBox.getSelectedItem().toString();
							//DB �߰�
							int rowNum = stmt.executeUpdate("insert into income (date, money, memo, category) values('" + year + "-" + month + "-" + day +
									"', '" + money + "', '" + content + "', '" + group + "');");
							//���̺� �߰�
							Object data[] = {year+"-"+month+"-"+day, content, money, group};
							DefaultTableModel model = (DefaultTableModel) incomeTable.getModel();
							model.addRow(data);
							
						}
						catch (SQLException se) {
							se.getMessage();
						}
						catch (Exception e) {	
							e.getMessage();
						}
					}
				});
				
				//���� ��ư Ŭ�� ��
				in_del_button.addActionListener(new RemoveActionListener (incomeTable));
				//���� ���� ����
				in_del_button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						Statement stmt = null;
						try {
							stmt = conn.createStatement();
							int row = incomeTable.getSelectedRow();
							Object date = incomeTable.getValueAt(row,0);
							String content = incomeTable.getValueAt(row, 1).toString();
							int money = Integer.parseInt(incomeTable.getValueAt(row, 2).toString());
							String group = incomeTable.getValueAt(row, 3).toString();
							int rowNum = stmt.executeUpdate("delete from income where date = '" +date
									+"' and memo ='" + content + "' and money = '" + money + "' and category = '"
									+ group + "';");
						} 
						catch (Exception e){
							System.out.println(e.getMessage());
						}
					}
				});
					
				addInFrame.pack();
				addInFrame.setVisible(true);
			}
		});
		inButton.setBounds(36, 146, 289, 47);
		inButton.setFont(new Font("�������", Font.PLAIN, 15));
		frame.getContentPane().add(inButton);
		
		//���� ���� �Է� ��ư �� â
		JButton outButton = new JButton("���� ���� �Է� �� ����");
		outButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				JFrame addOutFrame = new JFrame("���� ���� �Է� �� ����");
				addOutFrame.setPreferredSize(new Dimension(750,450));
				Container addOutPane = addOutFrame.getContentPane();
				String outColNames[] = {"��¥", "�ݾ�", "����", "�з�"};
				DefaultTableModel outModel = new DefaultTableModel (outColNames, 0);
				JTable outgoTable = new JTable(outModel);
				addOutPane.add(new JScrollPane(outgoTable), BorderLayout.CENTER);
				JPanel outPanel = new JPanel();
				JTextField textYear = new JTextField(4); JTextField textMonth = new JTextField(2); JTextField textDay = new JTextField(2);
				JTextField textContent = new JTextField(10);
				JTextField textCost = new JTextField(7);
				JComboBox<String> groupBox = new JComboBox<String>();
				JButton out_add_button = new JButton("�߰�");
				JButton out_del_button = new JButton("����");
				groupBox.setModel(new DefaultComboBoxModel<String>(new String[] {"���� �з�","�ĺ�", "ī��", "��Ȱ��ǰ", "�м�/�̿�", "�ְ�/���", "�Ƿ�/�ǰ�", "��ȭ/����", "��/����","����/����", "������", "��Ÿ"}));
				outPanel.add(new JLabel("��¥(��,��,��)"));
				outPanel.add(textYear); outPanel.add(textMonth); outPanel.add(textDay);
				outPanel.add(new JLabel("�ݾ�"));
				outPanel.add(textCost);
				outPanel.add(new JLabel("����"));
				outPanel.add(textContent);
				outPanel.add(new JLabel("�з�"));
				outPanel.add(groupBox);
				outPanel.add(out_add_button);
				outPanel.add(out_del_button);
				addOutPane.add(outPanel, BorderLayout.SOUTH);
				
				//���� ���� ���� �ҷ��� ����ϱ�
				ResultSet oresult = null;
				Statement stmt = null;
				try {
					stmt = conn.createStatement();
					oresult = stmt.executeQuery("select * from outcome");
					while(oresult.next()) {
						Object data[] = {oresult.getDate(1), oresult.getInt(2), oresult.getString(3), oresult.getString(4)};
						DefaultTableModel model = (DefaultTableModel) outgoTable.getModel();
						model.addRow(data);
					}
				}
				catch(SQLException se) {
					se.getMessage();
				}
				catch(NullPointerException npe) {
					npe.getMessage();
				}
				
				
				//���� ���� ����
				out_add_button.addActionListener (new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						Statement stmt = null;
						try {
							int year = Integer.parseInt(textYear.getText());
							int month = Integer.parseInt(textMonth.getText());
							int day = Integer.parseInt(textDay.getText());
							String content = textContent.getText();
							int money = Integer.parseInt(textCost.getText());
							String group = groupBox.getSelectedItem().toString();
							//DB �߰�
							int rowNum = stmt.executeUpdate("insert into outcome (date, money, memo, category) values('" + year + "-" + month + "-" + day +
									"', '" + money + "', '" + content + "', '" + group + "');");
							//���̺� �߰�
							Object data[] = {year+"-"+month+"-"+day, money,content, group};
							DefaultTableModel model = (DefaultTableModel) outgoTable.getModel();
							model.addRow(data);
							
						}
						catch (SQLException se) {
							se.getMessage();
						}
						catch (Exception e) {	
							e.getMessage();
						}
					}
				});
				
				//���� ��ư Ŭ�� ��
				out_del_button.addActionListener(new RemoveActionListener (outgoTable));
				//���� ���� ����
				out_del_button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						Statement stmt = null;
						try {
							stmt = conn.createStatement();
							int row = outgoTable.getSelectedRow();
							Object date = outgoTable.getValueAt(row,0);
							String content = outgoTable.getValueAt(row, 1).toString();
							int money = Integer.parseInt(outgoTable.getValueAt(row, 2).toString());
							String group = outgoTable.getValueAt(row, 3).toString();
							int rowNum = stmt.executeUpdate("delete from income where date = '" +date
									+"' and memo ='" + content + "' and money = '" + money + "' and category = '"
									+ group + "';");
						}
						catch (Exception e){
							e.getMessage();
						}
					}
				});
				
				addOutFrame.pack();
				addOutFrame.setVisible(true);
			}
		});
		outButton.setBounds(361, 148, 286, 47);
		outButton.setFont(new Font("�������", Font.PLAIN, 15));
		frame.getContentPane().add(outButton);
		
		//���� ���� ���� �� ����
		JButton seeInButton = new JButton("���� ���� ���� �� ����");
		seeInButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				JFrame seeInFrame = new JFrame("���� ���� ���� �� ����");
				seeInFrame.setPreferredSize(new Dimension(700,450));
				Container seeInPane = seeInFrame.getContentPane();
				JLabel editOrder = new JLabel("������ ���� Ŭ���� �� ���� ������ ���� �� ���� ��ư�� �����ּ���");
				seeInFrame.getContentPane().add(editOrder);
				String inColNames[] = {"��¥", "����", "�ݾ�", "�з�"};
				DefaultTableModel inModel = new DefaultTableModel (inColNames, 0);
				JTable incomeTable = new JTable(inModel);
				seeInPane.add(new JScrollPane(incomeTable), BorderLayout.CENTER);
				JPanel inPanel = new JPanel();
				JPanel baPanel = new JPanel();
				JTextField textYear = new JTextField(4); JTextField textMonth = new JTextField(2); JTextField textDay = new JTextField(2);
				JTextField textContent = new JTextField(10);
				JTextField textCost = new JTextField(7);
				JComboBox<String> groupBox = new JComboBox<String>();
				JButton in_edit_button = new JButton("����");
				groupBox.setModel(new DefaultComboBoxModel<String>(new String[] {"���� �з�", "�޿�", "�뵷", "��������", "��Ÿ"}));
				
				GridLayout layout = new GridLayout(2,1);
				JPanel panel = new JPanel();
				panel.setLayout(layout);
				
				inPanel.add(new JLabel("��¥(��,��,��)"));
				inPanel.add(textYear); inPanel.add(textMonth); inPanel.add(textDay);
				inPanel.add(new JLabel("����"));
				inPanel.add(textContent);
				inPanel.add(new JLabel("�ݾ�"));
				inPanel.add(textCost);
				inPanel.add(new JLabel("�з�"));
				inPanel.add(groupBox);
				inPanel.add(in_edit_button);
				//baPanel.add(new JLabel("���� �����ϰ� �ִ� �ݾ��� " + balance + "���Դϴ�."));
				panel.add(inPanel);
				panel.add(baPanel);
				seeInPane.add(panel, BorderLayout.SOUTH);
				
				//���� ���� ����ϱ�
				ResultSet iresult = null;
				Statement stmt = null;
				try {
					stmt = conn.createStatement();
					iresult = stmt.executeQuery("select * from income");
					while(iresult.next()) {
						Object data[] = {iresult.getDate(1), iresult.getString(3), iresult.getInt(2), iresult.getString(4)};
						DefaultTableModel model = (DefaultTableModel) incomeTable.getModel();
						model.addRow(data);
					}
				}
				catch(SQLException se) {
					se.getMessage();
				}
				catch(NullPointerException npe) {
					npe.getMessage();
				}
				
				in_edit_button.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent arg0) {
						//������ ���� ����
						int row = incomeTable.getSelectedRow();
						//������ ����
						int year = Integer.parseInt(textYear.getText());
						int month = Integer.parseInt(textMonth.getText());
						int day = Integer.parseInt(textDay.getText());
						String content = textContent.getText();
						int cost = Integer.parseInt(textCost.getText());
						String group = groupBox.getSelectedItem().toString();
						String inputDate = year + "-"+month+"-"+day;
						//���� �� ����
						Date binputDate = (Date) inModel.getValueAt(row, 0);
						String bcontent = inModel.getValueAt(row, 1).toString();
						int bcost = Integer.parseInt(inModel.getValueAt(row, 2).toString());
						String bgroup = inModel.getValueAt(row, 3).toString();
						
						//�Ŵ�����Ʈ�� ��ü ����
						in_edit_button.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								Statement stmt = null;
								try {
									stmt = conn.createStatement();
									String sql = "update income set date = '"+ year +"-"+month+"-"+day +"', memo = '" 
									+ content +"', money = " +cost+ ", category = '" +group+ "' where date = '"+binputDate
									+"' and memo = '" + bcontent + "' and money = '" + bcost + "' and category = '" + bgroup + "' limit 1";
									stmt.executeUpdate(sql);
								}
								catch (Exception e){
									System.out.println(e.getMessage());
								}
							}
						});
						
						//���̺� ����
						try {
							inModel.setValueAt(inputDate, row, 0);
							inModel.setValueAt(content, row, 1);
							inModel.setValueAt(textCost.getText(), row, 2);
							inModel.setValueAt(group, row, 3);
							
						} catch (Exception e) {
							e.getMessage();
						}
						

						JOptionPane.showMessageDialog(null,"������ �Ϸ�Ǿ����ϴ�.");	
					}
				});
				
				seeInFrame.pack();
				seeInFrame.setVisible(true);
			}
		});		
		seeInButton.setBounds(36, 225, 289, 47);
		seeInButton.setFont(new Font("�������", Font.PLAIN, 15));
		frame.getContentPane().add(seeInButton);
		
		//���� ���� ���� �� ����
		JButton seeOutButton = new JButton("���� ���� ���� �� ����");
		seeOutButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				JFrame seeOutFrame = new JFrame("���� ���� ���� �� ����");
				seeOutFrame.setPreferredSize(new Dimension(700,450));
				Container seeOutPane = seeOutFrame.getContentPane();
				JLabel editOrder = new JLabel("������ ���� Ŭ���� �� ���� ������ ���� �� ���� ��ư�� �����ּ���");
				seeOutFrame.getContentPane().add(editOrder);
				String outColNames[] = {"��¥", "����", "�ݾ�", "�з�"};
				DefaultTableModel outModel = new DefaultTableModel (outColNames, 0);
				JTable outgoTable = new JTable(outModel);
				seeOutPane.add(new JScrollPane(outgoTable), BorderLayout.CENTER);
				JPanel outPanel = new JPanel();
				JPanel baPanel = new JPanel();
				JTextField textYear = new JTextField(4); JTextField textMonth = new JTextField(2); JTextField textDay = new JTextField(2);
				JTextField textContent = new JTextField(10);
				JTextField textCost = new JTextField(7);
				JComboBox<String> groupBox = new JComboBox<String>();
				JButton out_edit_button = new JButton("����");
				groupBox.setModel(new DefaultComboBoxModel<String>(new String[] {"����з�","�ĺ�", "ī��", "��Ȱ��ǰ", "�м�/�̿�", "�ְ�/���", "�Ƿ�/�ǰ�",
						"��ȭ/����", "��/����", "����/����", "������", "��Ÿ"}));
			
				GridLayout layout = new GridLayout(2,1);
				JPanel panel = new JPanel();
				panel.setLayout(layout);
				
				outPanel.add(new JLabel("��¥(��,��,��)"));
				outPanel.add(textYear); outPanel.add(textMonth); outPanel.add(textDay);
				outPanel.add(new JLabel("����"));
				outPanel.add(textContent);
				outPanel.add(new JLabel("�ݾ�"));
				outPanel.add(textCost);
				outPanel.add(new JLabel("�з�"));
				outPanel.add(groupBox);
				outPanel.add(out_edit_button);
				//baPanel.add(new JLabel("���� �����ϰ� �ִ� �ݾ��� " + balance + "���Դϴ�."));
				panel.add(outPanel);
				panel.add(baPanel);
				seeOutPane.add(panel, BorderLayout.SOUTH);
				
				//���� ���� ����ϱ�
				ResultSet oresult = null;
				Statement stmt = null;
				try {
					stmt = conn.createStatement();
					oresult = stmt.executeQuery("select * from outcome");
					while(oresult.next()) {
						Object data[] = {oresult.getDate(1),  oresult.getString(3), oresult.getInt(2), oresult.getString(4)};
						DefaultTableModel model = (DefaultTableModel) outgoTable.getModel();
						model.addRow(data);
					}
				}
				catch(SQLException se) {
					se.getMessage();
				}
				catch(NullPointerException npe) {
					npe.getMessage();
				}
				
				
				out_edit_button.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent arg0) {
						//������ ���� ����
						int row = outgoTable.getSelectedRow();
						//������ ����
						int year = Integer.parseInt(textYear.getText());
						int month = Integer.parseInt(textMonth.getText());
						int day = Integer.parseInt(textDay.getText());
						String content = textContent.getText();
						int cost = Integer.parseInt(textCost.getText());
						String group = groupBox.getSelectedItem().toString();
						String inputDate = textYear.getText() + "-" + textMonth.getText() + "-" + textDay.getText();
						//���� �� ����
						String binputDate = outModel.getValueAt(row, 0).toString();
						String bcontent = outModel.getValueAt(row, 2).toString();
						int bcost = Integer.parseInt(outModel.getValueAt(row, 1).toString());
						String bgroup = outModel.getValueAt(row, 3).toString();
						
						try {
							outModel.setValueAt(inputDate, row, 0);
							outModel.setValueAt(content, row, 1);
							outModel.setValueAt(textCost.getText(), row, 2);
							outModel.setValueAt(group, row, 3);
							
						} catch (Exception e) {
							e.getMessage();
						}
						out_edit_button.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								Statement stmt = null;
								try {
									stmt = conn.createStatement();
									String sql = "update outcome set date = '"+ inputDate +"', memo = '" 
									+ content +"', money = " +cost+ ", category = '" +group+ "' where date = '"+binputDate
									+"' and memo = '" + bcontent + "' and money = '" + bcost + "' and category = '" + bgroup + "' limit 1";
									stmt.executeUpdate(sql);
								}
								catch (Exception e){
									System.out.println(e.getMessage());
								}
							}
						});
						JOptionPane.showMessageDialog(null,"������ �Ϸ�Ǿ����ϴ�.");	
					}
				});
				
				seeOutFrame.pack();
				seeOutFrame.setVisible(true);
			}
		});	
		seeOutButton.setBounds(361, 225, 286, 47);
		seeOutButton.setFont(new Font("�������", Font.PLAIN, 15));
		frame.getContentPane().add(seeOutButton);
		
		//���� ���� ã��
		JButton searchInButton = new JButton("���� ���� ã��");
		searchInButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				JFrame searInFrame = new JFrame("���� ���� ã��");
				searInFrame.setPreferredSize(new Dimension(700,450));
				Container searInPane = searInFrame.getContentPane();
				String inColNames[] = {"��¥", "����", "�ݾ�", "�з�"};
				DefaultTableModel inModel = new DefaultTableModel (inColNames, 0);
				JTable incomeTable = new JTable(inModel);
				searInPane.add(new JScrollPane(incomeTable), BorderLayout.CENTER);
				JPanel inPanel = new JPanel();
				JTextField textYear1 = new JTextField(4); JTextField textMonth1 = new JTextField(2); JTextField textDay1 = new JTextField(2);
				JTextField textYear2 = new JTextField(4); JTextField textMonth2 = new JTextField(2); JTextField textDay2 = new JTextField(2);
				JButton in_sear_button = new JButton("�˻�");
				inPanel.add(new JLabel("�˻� �Ⱓ : "));
				inPanel.add(textYear1);
				inPanel.add(new JLabel("��"));
				inPanel.add(textMonth1);
				inPanel.add(new JLabel("��"));
				inPanel.add(textDay1);
				inPanel.add(new JLabel("�� ~ "));
				inPanel.add(textYear2);
				inPanel.add(new JLabel("��"));
				inPanel.add(textMonth2);
				inPanel.add(new JLabel("��"));
				inPanel.add(textDay2);
				inPanel.add(new JLabel("�� "));
				inPanel.add(in_sear_button);
				searInPane.add(inPanel, BorderLayout.NORTH);
				
				//�˻��� ��¥ �Է¹ޱ�
				in_sear_button.addActionListener (new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						try {
							int fromYear = Integer.parseInt(textYear1.getText());
							int fromMonth = Integer.parseInt(textMonth1.getText());
							int fromDay = Integer.parseInt(textDay1.getText());
							String fromDate = fromYear + "-" +fromMonth + "-" + fromDay;
							int toYear = Integer.parseInt(textYear2.getText());
							int toMonth = Integer.parseInt(textMonth2.getText());
							int toDay = Integer.parseInt(textDay2.getText());
							String toDate = toYear + "-" + toMonth + "-" + toDay;
							
							//Panel�� �߰��ϱ�
							ResultSet iresult = null;
							Statement stmt = null;
							try {
								stmt = conn.createStatement();
								if(fromDate == toDate) {
									iresult = stmt.executeQuery("select * from income where date = '" + fromDate + "';");
								}
								else {
									iresult = stmt.executeQuery("select * from income where date between '" + fromDate + "'and'" + toDate +"';");
								}
								while(iresult.next()) {
									Object data[] = {iresult.getDate(1),  iresult.getString(3), iresult.getInt(2), iresult.getString(4)};
									DefaultTableModel model = (DefaultTableModel) incomeTable.getModel();
									model.addRow(data);
								}
							}
							catch(SQLException se) {
								se.getMessage();
							}
							catch(NullPointerException npe) {
								npe.getMessage();
							}
						}
						catch (Exception e) {	
							e.getMessage();
						}
					}
				});
						
				searInFrame.pack();
				searInFrame.setVisible(true);
			}
		});
		searchInButton.setBounds(36, 310, 126, 47);
		searchInButton.setFont(new Font("�������", Font.PLAIN, 15));
		frame.getContentPane().add(searchInButton);
		
		//���� ���� ã��
		JButton searchOutButton = new JButton("���� ���� ã��");
		searchOutButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				JFrame searOutFrame = new JFrame("���� ���� ã��");
				searOutFrame.setPreferredSize(new Dimension(700,450));
				Container searOutPane = searOutFrame.getContentPane();
				String outColNames[] = {"��¥", "����", "�ݾ�", "�з�"};
				DefaultTableModel outModel = new DefaultTableModel (outColNames, 0);
				JTable outgoTable = new JTable(outModel);
				searOutPane.add(new JScrollPane(outgoTable), BorderLayout.CENTER);
				JPanel outPanel = new JPanel();
				JTextField textYear1 = new JTextField(4); JTextField textMonth1 = new JTextField(2); JTextField textDay1 = new JTextField(2);
				JTextField textYear2 = new JTextField(4); JTextField textMonth2 = new JTextField(2); JTextField textDay2 = new JTextField(2);
				JButton out_sear_button = new JButton("�˻�");
				outPanel.add(new JLabel("�˻� �Ⱓ : "));
				outPanel.add(textYear1);
				outPanel.add(new JLabel("��"));
				outPanel.add(textMonth1);
				outPanel.add(new JLabel("��"));
				outPanel.add(textDay1);
				outPanel.add(new JLabel("�� ~ "));
				outPanel.add(textYear2);
				outPanel.add(new JLabel("��"));
				outPanel.add(textMonth2);
				outPanel.add(new JLabel("��"));
				outPanel.add(textDay2);
				outPanel.add(new JLabel("�� "));
				outPanel.add(out_sear_button);
				searOutPane.add(outPanel, BorderLayout.NORTH);
				

				//�˻��� ��¥ �Է¹ޱ�
				out_sear_button.addActionListener (new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						try {
							int fromYear = Integer.parseInt(textYear1.getText());
							int fromMonth = Integer.parseInt(textMonth1.getText());
							int fromDay = Integer.parseInt(textDay1.getText());
							String fromDate = fromYear + "-" + fromMonth + "-" + fromDay;
							int toYear = Integer.parseInt(textYear2.getText());
							int toMonth = Integer.parseInt(textMonth2.getText());
							int toDay = Integer.parseInt(textDay2.getText());
							String toDate = toYear + "-" + toMonth + "-" + toDay;
							
							//Panel�� �߰��ϱ�
							ResultSet oresult = null;
							Statement stmt = null;
							try {							
								stmt = conn.createStatement();
								if(fromDate == toDate) {
									oresult = stmt.executeQuery("select * from outcome where date = '" + fromDate + "';");
								}
								else {
									oresult = stmt.executeQuery("select * from outcome where date between '" + fromDate + "'and'" + toDate +"';");
								}
								while(oresult.next()) {
									Object data[] = {oresult.getDate(1), oresult.getString(3), oresult.getInt(2),  oresult.getString(4)};
									DefaultTableModel model = (DefaultTableModel) outgoTable.getModel();
									model.addRow(data);
								}
							}
							catch(SQLException se) {
								se.getMessage();
							}
							catch(NullPointerException npe) {
								npe.getMessage();
							}
						}
						catch (Exception e) {	
						}
					}
				});
						
				searOutFrame.pack();
				searOutFrame.setVisible(true);
			}
		});
		searchOutButton.setBounds(198, 310, 126, 47);
		searchOutButton.setFont(new Font("�������", Font.PLAIN, 15));
		frame.getContentPane().add(searchOutButton);
		
		//���α׷� ����
		JButton exitButton = new JButton("���α׷� ����");
		exitButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				try {
					conn.close();
				}
				catch (SQLException se) {
					se.getMessage();
				}
				System.exit(0);
			}
		});
		exitButton.setBounds(522, 310, 126, 47);
		exitButton.setFont(new Font("�������", Font.PLAIN, 15));
		frame.getContentPane().add(exitButton);
				

		
		
		frame.pack();
		frame.setVisible(true);
	}
}
