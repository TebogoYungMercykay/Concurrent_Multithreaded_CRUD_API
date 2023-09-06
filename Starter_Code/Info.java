public class Info {
    public String id;
    public String name;

    public volatile int practicals;
    public volatile int assignments;

    public volatile int attempt;

    public Info(String id, String n, char operation ){
        this.id = id;
        this.name = n;
        this.attempt = 0;

        switch (operation) {
            case 'c': practicals=0 ; assignments=0 ; 
                break;
            case 'u': practicals=10 ; assignments=10 ;
                break;
        }
    }
}