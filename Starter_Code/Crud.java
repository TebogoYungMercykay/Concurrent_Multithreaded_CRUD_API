import java.util.LinkedList;
import java.util.Queue;

public class Crud {
    private volatile Queue<Info> database = new LinkedList<>(); 

    private volatile Queue<Info> create = new LinkedList<>();
    private volatile Queue<Boolean> read = new LinkedList<>();
    private volatile Queue<Info> update = new LinkedList<>();
    private volatile Queue<Info> delete = new LinkedList<>();

    public Crud(){
        String ids[] = {"u123","u456","u789","u321","u654","u987","u147","u258","u369","u741","u852","u963"};
        String names[] = {"Thabo","Luke","James","Lunga","Ntando","Scott","Michael","Ntati","Lerato","Niel","Saeed","Rebecca"};

        for( int i= 0; i<20; i++ ){
            read.add(true);

            if(i<12) create.add( new Info( ids[i], names[i], 'c' ));

            if(i<4) update.add( new Info( ids[i+1], names[i+1], 'u'));
            if(i<4) delete.add( new Info( ids[i+2], names[i+2], 'd'));

            if(i>=9 && i<12){
                update.add( new Info( ids[i], names[i], 'u'));
                delete.add( new Info( ids[i], names[i], 'd'));
            }
        }
    }


}