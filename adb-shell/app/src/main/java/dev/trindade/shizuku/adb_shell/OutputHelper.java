package dev.trindade.shizuku.adb_shell;

public class OutputHelper {

    public static final String ERROR_COLOR = "#FF0000";
    public static final String WARNING_COLOR = "#FFC400";
    public static final String SUCCESS_COLOR = "#198754";
        
    public static void formatError (String error) {
         var error_atf1 = error.replaceAll("tdevE: ", "<font color=" + ERROR_COLOR + ">Error: </font>");
         error_atf1 = error.replaceAll("tdevW: ", "<font color=" + WARNING_COLOR + ">Warning: </font>");
         error_atf1 = error.replaceAll("tdevS: ", "<font color=" + WARNING_COLOR + ">Success: </font>");
         return error_atf1;
    }
}