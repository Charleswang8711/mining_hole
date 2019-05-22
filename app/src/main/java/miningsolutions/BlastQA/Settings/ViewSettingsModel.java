package miningsolutions.BlastQA.Settings;

public class ViewSettingsModel {

    String Item;
    String SheetName;
    String StartCell;
    String EndCell;
    Integer Type;

    ViewSettingsModel(String Item,String SheetName,String StartCell,String EndCell,Integer Type)
    {
        this.Item = Item;
        this.SheetName = SheetName;
        this.StartCell = StartCell;
        this.EndCell = EndCell;
        this.Type = Type;
    }

    public String getItem()
    {
        return Item;
    }

    public void setItem(String Item)
    {
        this.Item = Item;
    }

    public String getSheetName()
    {
        return SheetName;
    }

    public void setSheetName(String SheetName)
    {
        this.SheetName = SheetName;
    }

    public String getStartCell()
    {
        return StartCell;
    }

    public void setStartCell(String StartCell)
    {
        this.StartCell = StartCell;
    }

    public String getEndCell()
    {
        return EndCell;
    }

    public void setEndCell(String EndCell)
    {
        this.EndCell = EndCell;
    }

    public Integer getType() {
        return Type;
    }

    public void setType(Integer Type) {
        this.Type = Type;
    }



}
