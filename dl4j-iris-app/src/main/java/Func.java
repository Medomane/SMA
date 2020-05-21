import javafx.scene.control.TextField;

public class Func {
    public static boolean isNull(String str){
        if(str == null) return true ;
        if(str.trim().equals("")) return true ;
        return str.trim().length() == 0;
    }
    public static boolean isDouble(String value) {
        try {
            if(isNull(value)) return false ;
            Double.parseDouble(value);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean isLong(String value) {
        try {
            if(isNull(value)) return false ;
            Long.parseLong(value);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
}
