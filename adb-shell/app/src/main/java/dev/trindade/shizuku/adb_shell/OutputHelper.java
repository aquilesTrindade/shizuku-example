package dev.trindade.shizuku.adb_shell;

public class OutputHelper {

    public static final String ERROR_COLOR = "#FF0000";
    public static final String WARNING_COLOR = "#FFC400";
    public static final String SUCCESS_COLOR = "#198754";
        
    public static String formatOutput(String error) {
        String formatedOutput = error;
        formatedOutput = formatedOutput.replaceAll("tdevE: ", "<font color=\"" + ERROR_COLOR + "\">Error: </font>");
        formatedOutput = formatedOutput.replaceAll("tdevW: ", "<font color=\"" + WARNING_COLOR + "\">Warning: </font>");
        formatedOutput = formatedOutput.replaceAll("tdevS: ", "<font color=\"" + SUCCESS_COLOR + "\">Success: </font>");
        return formatedOutput;
    }
}