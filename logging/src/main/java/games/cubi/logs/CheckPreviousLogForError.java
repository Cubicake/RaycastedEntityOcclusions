package games.cubi.logs;

//Paper always prints the name of the exception being thrown, even if the message is empty. So, the name of this class will point the user to the actual error message. For other platforms, this should throw silently, leaving just the error message above it
public class CheckPreviousLogForError extends RuntimeException {
    public CheckPreviousLogForError() {
        super(null,null, false, false);
    }
}