import java.util.Scanner;
// Names: Selepe Sello
// Student Number: uXXXXXXXX

public class Main {
    public static void main(String[] args) {
        int numThreads = 0;
        Scanner userInputObj = new Scanner(System.in);
        System.out.print("Enter the Number of Threads for the API: ");
        numThreads = userInputObj.nextInt();
        Crud crud = new Crud(numThreads);
        userInputObj.close();
        System.out.println("___________________________________________");
        System.out.println("____________ CRUD API STARTING ____________");
        System.out.println("___________________________________________");
        crud.run_test(crud);
    }
}