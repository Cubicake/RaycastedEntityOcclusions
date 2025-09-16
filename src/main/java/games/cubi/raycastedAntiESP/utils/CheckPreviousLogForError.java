package games.cubi.raycastedAntiESP.utils;

//Paper always prints the name of the exception being thrown, even if the message is empty. So, the name of this class will point the user to the actual error message
public class CheckPreviousLogForError extends RuntimeException {
    public CheckPreviousLogForError() {
        super("",null, false, false);
    }
}