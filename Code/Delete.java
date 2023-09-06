// Names: Selepe Sello
// Student Number: uXXXXXXXX

public class Delete extends Thread {
    public Crud api_request;

    public Delete(Crud api_request) {
        this.api_request = api_request;
    }

    @Override
    public void run() {
        while(!api_request.getDeleteQueue().isEmpty()) {
            api_request.DeleteOperation();
        }
    }
}