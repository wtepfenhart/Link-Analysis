/*Mary Fatima Menges s1012284*/

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class MyList extends JList{
	
	public MyList(){
		super();
	}
	
	public MyList(String[] options){
		super(createData(options));
		this.setCellRenderer(new CheckListRenderer());
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setBorder(new EmptyBorder(0, 4, 0, 0));
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				CheckableItem item = (CheckableItem) getModel()
						.getElementAt(index);
				item.setSelected(!item.isSelected());
				Rectangle rect = getCellBounds(index, index);
				repaint(rect);
			}
		});
	}
	public MyList(String[] options, String[] links, String outputFile) {
		super(createData(options));
		this.setCellRenderer(new CheckListRenderer());
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setBorder(new EmptyBorder(0, 4, 0, 0));
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int index = locationToIndex(e.getPoint());
				CheckableItem item = (CheckableItem) getModel()
						.getElementAt(index);
				item.setSelected(!item.isSelected());
				Rectangle rect = getCellBounds(index, index);
				repaint(rect);
			}
		});
		
	}
	
	private static CheckableItem[] createData(String[] strs) {
		int n = strs.length;
		CheckableItem[] items = new CheckableItem[n];
		for (int i = 0; i < n; i++) {
			items[i] = new CheckableItem(strs[i]);
		}
		return items;
	}

}
