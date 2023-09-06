// Names: Selepe Sello
// Student Number: uXXXXXXXX

public class Update extends Thread {
    public Crud api_request;

    public Update(Crud api_request) {
        this.api_request = api_request;
    }

    @Override
    public void run() {
        while(!api_request.getUpdateQueue().isEmpty()) {
            api_request.UpdateOperation();
        }
    }
}