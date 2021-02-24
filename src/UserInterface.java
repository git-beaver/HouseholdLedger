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
		//DB 연결
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/accountBook?allowPublicKeyRetrieval=true&useSSL=false", "root", "apmsetup");
			System.out.println("데이터베이스에 접속했습니다.");
		}
		catch (ClassNotFoundException cnfe) {
			System.out.println("해당 클래스를 찾을 수 없습니다" + cnfe.getMessage()); 
		}
		catch (SQLException se) {
			System.out.println(se.getMessage());
		}
				
		JFrame frame = new JFrame("HouseholdLedger");
		frame.setPreferredSize(new Dimension(700,450));
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//가계부 상단 프로그램명 및 클릭 명령 작성
		JLabel programName = new JLabel("가계부 프로그램");
		programName.setBounds(253, 22, 152, 37);
		programName.setFont(new Font("맑은고딕", Font.PLAIN, 20));
		frame.getContentPane().add(programName);
		
		JLabel menuOrder = new JLabel("실행하실 메뉴 버튼을 클릭해주세요");
		menuOrder.setBounds(209, 71, 245, 37);
		menuOrder.setFont(new Font("맑은고딕", Font.PLAIN, 15));
		frame.getContentPane().add(menuOrder);
		
		//수입 내역 입력 및 삭제
		JButton inButton = new JButton("수입 내역 입력 및 삭제");
		inButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				JFrame addInFrame = new JFrame("수입 내역 입력 및 삭제");
				addInFrame.setPreferredSize(new Dimension(750,450));
				Container addInPane = addInFrame.getContentPane();
				String inColNames[] = {"날짜", "내역", "금액", "분류"};
				DefaultTableModel inModel = new DefaultTableModel (inColNames, 0);
				JTable incomeTable = new JTable(inModel);
				addInPane.add(new JScrollPane(incomeTable), BorderLayout.CENTER);
				JPanel inPanel = new JPanel();
				JTextField textYear = new JTextField(4); JTextField textMonth = new JTextField(2); JTextField textDay = new JTextField(2);
				JTextField textContent = new JTextField(10);
				JTextField textCost = new JTextField(7);
				JComboBox<String> groupBox = new JComboBox<String>();
				JButton in_add_button = new JButton("추가");
				JButton in_del_button = new JButton("삭제");
				groupBox.setModel(new DefaultComboBoxModel<String>(new String[] {"수입 분류", "급여", "용돈", "금융수입", "기타"}));
				inPanel.add(new JLabel("날짜(년,월,일)"));
				inPanel.add(textYear); inPanel.add(textMonth); inPanel.add(textDay);
				inPanel.add(new JLabel("내역"));
				inPanel.add(textContent);
				inPanel.add(new JLabel("금액"));
				inPanel.add(textCost);
				inPanel.add(new JLabel("분류"));
				inPanel.add(groupBox);
				inPanel.add(in_add_button);
				inPanel.add(in_del_button);
				addInPane.add(inPanel, BorderLayout.SOUTH);
				
				//기존 수입 내역 불러와 출력하기
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
				
				
				//수입 내역 삽입
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
							//DB 추가
							int rowNum = stmt.executeUpdate("insert into income (date, money, memo, category) values('" + year + "-" + month + "-" + day +
									"', '" + money + "', '" + content + "', '" + group + "');");
							//테이블 추가
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
				
				//삭제 버튼 클릭 시
				in_del_button.addActionListener(new RemoveActionListener (incomeTable));
				//수입 내역 삭제
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
		inButton.setFont(new Font("맑은고딕", Font.PLAIN, 15));
		frame.getContentPane().add(inButton);
		
		//지출 내역 입력 버튼 및 창
		JButton outButton = new JButton("지출 내역 입력 및 삭제");
		outButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				JFrame addOutFrame = new JFrame("지출 내역 입력 및 삭제");
				addOutFrame.setPreferredSize(new Dimension(750,450));
				Container addOutPane = addOutFrame.getContentPane();
				String outColNames[] = {"날짜", "금액", "내역", "분류"};
				DefaultTableModel outModel = new DefaultTableModel (outColNames, 0);
				JTable outgoTable = new JTable(outModel);
				addOutPane.add(new JScrollPane(outgoTable), BorderLayout.CENTER);
				JPanel outPanel = new JPanel();
				JTextField textYear = new JTextField(4); JTextField textMonth = new JTextField(2); JTextField textDay = new JTextField(2);
				JTextField textContent = new JTextField(10);
				JTextField textCost = new JTextField(7);
				JComboBox<String> groupBox = new JComboBox<String>();
				JButton out_add_button = new JButton("추가");
				JButton out_del_button = new JButton("삭제");
				groupBox.setModel(new DefaultComboBoxModel<String>(new String[] {"지출 분류","식비", "카페", "생활용품", "패션/미용", "주거/통신", "의료/건강", "문화/여가", "술/유흥","여행/숙박", "경조사", "기타"}));
				outPanel.add(new JLabel("날짜(년,월,일)"));
				outPanel.add(textYear); outPanel.add(textMonth); outPanel.add(textDay);
				outPanel.add(new JLabel("금액"));
				outPanel.add(textCost);
				outPanel.add(new JLabel("내역"));
				outPanel.add(textContent);
				outPanel.add(new JLabel("분류"));
				outPanel.add(groupBox);
				outPanel.add(out_add_button);
				outPanel.add(out_del_button);
				addOutPane.add(outPanel, BorderLayout.SOUTH);
				
				//기존 지출 내역 불러와 출력하기
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
				
				
				//지출 내역 삽입
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
							//DB 추가
							int rowNum = stmt.executeUpdate("insert into outcome (date, money, memo, category) values('" + year + "-" + month + "-" + day +
									"', '" + money + "', '" + content + "', '" + group + "');");
							//테이블 추가
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
				
				//삭제 버튼 클릭 시
				out_del_button.addActionListener(new RemoveActionListener (outgoTable));
				//지출 내역 삭제
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
		outButton.setFont(new Font("맑은고딕", Font.PLAIN, 15));
		frame.getContentPane().add(outButton);
		
		//수입 내역 보기 및 수정
		JButton seeInButton = new JButton("수입 내역 보기 및 수정");
		seeInButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				JFrame seeInFrame = new JFrame("수입 내역 보기 및 수정");
				seeInFrame.setPreferredSize(new Dimension(700,450));
				Container seeInPane = seeInFrame.getContentPane();
				JLabel editOrder = new JLabel("수정할 행을 클릭한 후 수정 내용을 적은 뒤 수정 버튼을 눌러주세요");
				seeInFrame.getContentPane().add(editOrder);
				String inColNames[] = {"날짜", "내역", "금액", "분류"};
				DefaultTableModel inModel = new DefaultTableModel (inColNames, 0);
				JTable incomeTable = new JTable(inModel);
				seeInPane.add(new JScrollPane(incomeTable), BorderLayout.CENTER);
				JPanel inPanel = new JPanel();
				JPanel baPanel = new JPanel();
				JTextField textYear = new JTextField(4); JTextField textMonth = new JTextField(2); JTextField textDay = new JTextField(2);
				JTextField textContent = new JTextField(10);
				JTextField textCost = new JTextField(7);
				JComboBox<String> groupBox = new JComboBox<String>();
				JButton in_edit_button = new JButton("수정");
				groupBox.setModel(new DefaultComboBoxModel<String>(new String[] {"수입 분류", "급여", "용돈", "금융수입", "기타"}));
				
				GridLayout layout = new GridLayout(2,1);
				JPanel panel = new JPanel();
				panel.setLayout(layout);
				
				inPanel.add(new JLabel("날짜(년,월,일)"));
				inPanel.add(textYear); inPanel.add(textMonth); inPanel.add(textDay);
				inPanel.add(new JLabel("내역"));
				inPanel.add(textContent);
				inPanel.add(new JLabel("금액"));
				inPanel.add(textCost);
				inPanel.add(new JLabel("분류"));
				inPanel.add(groupBox);
				inPanel.add(in_edit_button);
				//baPanel.add(new JLabel("현재 보유하고 있는 금액은 " + balance + "원입니다."));
				panel.add(inPanel);
				panel.add(baPanel);
				seeInPane.add(panel, BorderLayout.SOUTH);
				
				//수입 내역 출력하기
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
						//수정을 위한 변수
						int row = incomeTable.getSelectedRow();
						//수정될 내용
						int year = Integer.parseInt(textYear.getText());
						int month = Integer.parseInt(textMonth.getText());
						int day = Integer.parseInt(textDay.getText());
						String content = textContent.getText();
						int cost = Integer.parseInt(textCost.getText());
						String group = groupBox.getSelectedItem().toString();
						String inputDate = year + "-"+month+"-"+day;
						//수정 전 내용
						Date binputDate = (Date) inModel.getValueAt(row, 0);
						String bcontent = inModel.getValueAt(row, 1).toString();
						int bcost = Integer.parseInt(inModel.getValueAt(row, 2).toString());
						String bgroup = inModel.getValueAt(row, 3).toString();
						
						//매니지먼트에 객체 삽입
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
						
						//테이블 수정
						try {
							inModel.setValueAt(inputDate, row, 0);
							inModel.setValueAt(content, row, 1);
							inModel.setValueAt(textCost.getText(), row, 2);
							inModel.setValueAt(group, row, 3);
							
						} catch (Exception e) {
							e.getMessage();
						}
						

						JOptionPane.showMessageDialog(null,"수정이 완료되었습니다.");	
					}
				});
				
				seeInFrame.pack();
				seeInFrame.setVisible(true);
			}
		});		
		seeInButton.setBounds(36, 225, 289, 47);
		seeInButton.setFont(new Font("맑은고딕", Font.PLAIN, 15));
		frame.getContentPane().add(seeInButton);
		
		//지출 내역 보기 및 수정
		JButton seeOutButton = new JButton("지출 내역 보기 및 수정");
		seeOutButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				JFrame seeOutFrame = new JFrame("지출 내역 보기 및 수정");
				seeOutFrame.setPreferredSize(new Dimension(700,450));
				Container seeOutPane = seeOutFrame.getContentPane();
				JLabel editOrder = new JLabel("수정할 행을 클릭한 후 수정 내용을 적은 뒤 수정 버튼을 눌러주세요");
				seeOutFrame.getContentPane().add(editOrder);
				String outColNames[] = {"날짜", "내역", "금액", "분류"};
				DefaultTableModel outModel = new DefaultTableModel (outColNames, 0);
				JTable outgoTable = new JTable(outModel);
				seeOutPane.add(new JScrollPane(outgoTable), BorderLayout.CENTER);
				JPanel outPanel = new JPanel();
				JPanel baPanel = new JPanel();
				JTextField textYear = new JTextField(4); JTextField textMonth = new JTextField(2); JTextField textDay = new JTextField(2);
				JTextField textContent = new JTextField(10);
				JTextField textCost = new JTextField(7);
				JComboBox<String> groupBox = new JComboBox<String>();
				JButton out_edit_button = new JButton("수정");
				groupBox.setModel(new DefaultComboBoxModel<String>(new String[] {"지출분류","식비", "카페", "생활용품", "패션/미용", "주거/통신", "의료/건강",
						"문화/여가", "술/유흥", "여행/숙박", "경조사", "기타"}));
			
				GridLayout layout = new GridLayout(2,1);
				JPanel panel = new JPanel();
				panel.setLayout(layout);
				
				outPanel.add(new JLabel("날짜(년,월,일)"));
				outPanel.add(textYear); outPanel.add(textMonth); outPanel.add(textDay);
				outPanel.add(new JLabel("내역"));
				outPanel.add(textContent);
				outPanel.add(new JLabel("금액"));
				outPanel.add(textCost);
				outPanel.add(new JLabel("분류"));
				outPanel.add(groupBox);
				outPanel.add(out_edit_button);
				//baPanel.add(new JLabel("현재 보유하고 있는 금액은 " + balance + "원입니다."));
				panel.add(outPanel);
				panel.add(baPanel);
				seeOutPane.add(panel, BorderLayout.SOUTH);
				
				//지출 내역 출력하기
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
						//수정을 위한 변수
						int row = outgoTable.getSelectedRow();
						//수정될 내용
						int year = Integer.parseInt(textYear.getText());
						int month = Integer.parseInt(textMonth.getText());
						int day = Integer.parseInt(textDay.getText());
						String content = textContent.getText();
						int cost = Integer.parseInt(textCost.getText());
						String group = groupBox.getSelectedItem().toString();
						String inputDate = textYear.getText() + "-" + textMonth.getText() + "-" + textDay.getText();
						//수정 전 내용
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
						JOptionPane.showMessageDialog(null,"수정이 완료되었습니다.");	
					}
				});
				
				seeOutFrame.pack();
				seeOutFrame.setVisible(true);
			}
		});	
		seeOutButton.setBounds(361, 225, 286, 47);
		seeOutButton.setFont(new Font("맑은고딕", Font.PLAIN, 15));
		frame.getContentPane().add(seeOutButton);
		
		//수입 내역 찾기
		JButton searchInButton = new JButton("수입 내역 찾기");
		searchInButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				JFrame searInFrame = new JFrame("수입 내역 찾기");
				searInFrame.setPreferredSize(new Dimension(700,450));
				Container searInPane = searInFrame.getContentPane();
				String inColNames[] = {"날짜", "내역", "금액", "분류"};
				DefaultTableModel inModel = new DefaultTableModel (inColNames, 0);
				JTable incomeTable = new JTable(inModel);
				searInPane.add(new JScrollPane(incomeTable), BorderLayout.CENTER);
				JPanel inPanel = new JPanel();
				JTextField textYear1 = new JTextField(4); JTextField textMonth1 = new JTextField(2); JTextField textDay1 = new JTextField(2);
				JTextField textYear2 = new JTextField(4); JTextField textMonth2 = new JTextField(2); JTextField textDay2 = new JTextField(2);
				JButton in_sear_button = new JButton("검색");
				inPanel.add(new JLabel("검색 기간 : "));
				inPanel.add(textYear1);
				inPanel.add(new JLabel("년"));
				inPanel.add(textMonth1);
				inPanel.add(new JLabel("월"));
				inPanel.add(textDay1);
				inPanel.add(new JLabel("일 ~ "));
				inPanel.add(textYear2);
				inPanel.add(new JLabel("년"));
				inPanel.add(textMonth2);
				inPanel.add(new JLabel("월"));
				inPanel.add(textDay2);
				inPanel.add(new JLabel("일 "));
				inPanel.add(in_sear_button);
				searInPane.add(inPanel, BorderLayout.NORTH);
				
				//검색할 날짜 입력받기
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
							
							//Panel에 추가하기
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
		searchInButton.setFont(new Font("맑은고딕", Font.PLAIN, 15));
		frame.getContentPane().add(searchInButton);
		
		//지출 내역 찾기
		JButton searchOutButton = new JButton("지출 내역 찾기");
		searchOutButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				JFrame searOutFrame = new JFrame("지출 내역 찾기");
				searOutFrame.setPreferredSize(new Dimension(700,450));
				Container searOutPane = searOutFrame.getContentPane();
				String outColNames[] = {"날짜", "내역", "금액", "분류"};
				DefaultTableModel outModel = new DefaultTableModel (outColNames, 0);
				JTable outgoTable = new JTable(outModel);
				searOutPane.add(new JScrollPane(outgoTable), BorderLayout.CENTER);
				JPanel outPanel = new JPanel();
				JTextField textYear1 = new JTextField(4); JTextField textMonth1 = new JTextField(2); JTextField textDay1 = new JTextField(2);
				JTextField textYear2 = new JTextField(4); JTextField textMonth2 = new JTextField(2); JTextField textDay2 = new JTextField(2);
				JButton out_sear_button = new JButton("검색");
				outPanel.add(new JLabel("검색 기간 : "));
				outPanel.add(textYear1);
				outPanel.add(new JLabel("년"));
				outPanel.add(textMonth1);
				outPanel.add(new JLabel("월"));
				outPanel.add(textDay1);
				outPanel.add(new JLabel("일 ~ "));
				outPanel.add(textYear2);
				outPanel.add(new JLabel("년"));
				outPanel.add(textMonth2);
				outPanel.add(new JLabel("월"));
				outPanel.add(textDay2);
				outPanel.add(new JLabel("일 "));
				outPanel.add(out_sear_button);
				searOutPane.add(outPanel, BorderLayout.NORTH);
				

				//검색할 날짜 입력받기
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
							
							//Panel에 추가하기
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
		searchOutButton.setFont(new Font("맑은고딕", Font.PLAIN, 15));
		frame.getContentPane().add(searchOutButton);
		
		//프로그램 종료
		JButton exitButton = new JButton("프로그램 종료");
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
		exitButton.setFont(new Font("맑은고딕", Font.PLAIN, 15));
		frame.getContentPane().add(exitButton);
				

		
		
		frame.pack();
		frame.setVisible(true);
	}
}
