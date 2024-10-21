package fr.LaurentFE.todolistserver;

public class ListItem {
    private final Integer item_id;
    private String label;
    private Boolean checked;

    public ListItem(Integer item_id, String label, Boolean checked) {
        this.item_id = item_id;
        this.label = label;
        this.checked = checked;
    }

    public Integer getItem_id() {
        return item_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean isChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
