import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AddActionListener implements ActionListener {
	JTable table;
	JTextField year, month, day, content, cost;
	JComboBox groupBox;

	AddActionListener (JTable table, JTextField year, JTextField month, JTextField day,
			JTextField content,JTextField cost, JComboBox groupBox) {
		this.table = table;
		this.year = year;
		this.month = month;
		this.day = day;
		this.content = content;
		this.cost = cost;
		this.groupBox = groupBox;
	}
	
	public void actionPerformed(ActionEvent e) {
		String arr[] = new String[4];
		arr[0] = year.getText() +". "+ month.getText() + ". " + day.getText();
		arr[1] = content.getText();
		arr[2] = cost.getText();
		arr[3] =  (String) groupBox.getSelectedItem();
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.addRow(arr);
	}

}
