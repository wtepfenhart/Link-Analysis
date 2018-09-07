/*Mary Fatima Menges s1012284*/

class CheckableItem {
	private String str;
	private boolean isSelected;

	public CheckableItem(String str) {
		this.str = str;
		isSelected = false;
	}

	public void setSelected(boolean b) {
		isSelected = b;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public String toString() {
		return str;
	}
}