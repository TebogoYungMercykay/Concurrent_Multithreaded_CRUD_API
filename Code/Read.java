// Names: Selepe Sello
// Student Number: uXXXXXXXX

public class Read extends Thread {
    public Crud api_request;

    public Read(Crud api_request) {
        this.api_request = api_request;
    }

    @Override
    public void run() {
        while(!api_request.getReadQueue().isEmpty()) {
            api_request.ReadOperation();
        }
    }
}