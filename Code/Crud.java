import java.util.LinkedList;
import java.util.Queue;
// Names: Selepe Sello
// Student Number: uXXXXXXXX

public class Crud {
    private volatile Queue<Info> create = new LinkedList<>();
    private volatile Queue<Boolean> read = new LinkedList<>();
    private volatile Queue<Info> update = new LinkedList<>();
    private volatile Queue<Info> delete = new LinkedList<>();

    private volatile Queue<Info> crud_database = new LinkedList<>();
    public int CRUD_NumThreads;

    private Bakery databaseLock = null;
    private Bakery createLock = null;
    private Bakery readLock = null;
    private Bakery updateLock = null;
    private Bakery deleteLock = null;

    public void run_test(Crud crud_ptr) {
        // Initializing the Requests Arrays
        Read [] Readers = new Read[this.CRUD_NumThreads];
        Delete [] Deletes = new Delete[this.CRUD_NumThreads];
        Create [] Creators = new Create [this.CRUD_NumThreads];
        Update [] Updaters = new Update [this.CRUD_NumThreads];

        // Work on the Request(s) Operations
        for (int i = 0; i < this.CRUD_NumThreads; i++) {
            Readers[i] = new Read(crud_ptr);
            Deletes[i] = new Delete(crud_ptr);
            Updaters[i] = new Update(crud_ptr);
            Creators[i] = new Create(crud_ptr);
            // Renaming the Threads: to Avoid ArrayIndexOutOfBoundsException
            Readers[i].setName("READ-Thread-" + i);
            Deletes[i].setName("DELETE-Thread-" + i);
            Updaters[i].setName("UPDATE-Thread-" + i);
            Creators[i].setName("CREATE-Thread-" + i);
        }

        for (int i = 0; i < this.CRUD_NumThreads; i++) {
            Creators[i].start();
            Updaters[i].start();
            Readers[i].start();
            Deletes[i].start();
        }
    }

    public Crud(int numThreads) {
        String ids[] = {"u123", "u456", "u789", "u321", "u654", "u987", "u147", "u258", "u369", "u741", "u852", "u963"};
        String names[] = {"Thabo", "Luke", "James", "Lunga", "Ntando", "Scott", "Michael", "Ntati", "Lerato", "Niel", "Saeed", "Rebecca"};
        for (int i = 0; i < 20; i++) {
            this.read.add(true);
            if (i < 12) {
                this.create.add(new Info(ids[i], names[i], 'c'));
            } if (i < 4) {
                this.update.add(new Info(ids[i + 1], names[i + 1], 'u'));
            } if (i < 4) {
                this.delete.add(new Info(ids[i + 2], names[i + 2], 'd'));
            } if (i >= 9 && i < 12) {
                this.update.add(new Info(ids[i], names[i], 'u'));
                this.delete.add(new Info(ids[i], names[i], 'd'));
            }
        }
        this.CRUD_NumThreads = numThreads;
        // Initiate the locks with the Number of Threads
        this.createLock = new Bakery(this.CRUD_NumThreads);
        this.readLock = new Bakery(this.CRUD_NumThreads);
        this.updateLock = new Bakery(this.CRUD_NumThreads);
        this.deleteLock = new Bakery(this.CRUD_NumThreads);
        this.databaseLock = new Bakery(this.CRUD_NumThreads);
    }

    public void CreateOperation() {
        System.out.println(Thread.currentThread().getName() + ": CREATE is waiting for request.");
		this.createLock.lock();
		try {
            Info createRequest = this.create.poll();
            if (createRequest != null) {
                this.databaseLock.lock();
                try{
                    this.crud_database.add(createRequest);
                    System.out.println(Thread.currentThread().getName() + ": CREATE success [id = " + createRequest.id + ", name = " + createRequest.name + "]");
                } finally {
                    this.databaseLock.unlock();
                }
            }
            System.out.println(Thread.currentThread().getName() + ": CREATE is sleeping");
            Thread.currentThread();
            Thread.sleep(this.getSleepingTime());
		} catch (InterruptedException exception) {
			exception.printStackTrace();
		} finally {
			this.createLock.unlock();
		}
	}

    public void ReadOperation() {
        System.out.println(Thread.currentThread().getName() + ": READ is waiting for request.");
		this.readLock.lock();
		try {
            Boolean tempOperation = this.read.poll();
            if (tempOperation != null) {
                this.databaseLock.lock();
                try{
                    System.out.println(Thread.currentThread().getName() + ": READ");
                    System.out.println("___________________________________________");
                    for (Info record : this.crud_database) {
                        System.out.println("[id = " + record.id + ", name = " + record.name + ", practicals = " + record.practicals + ", assignments = " + record.assignments + "]");
                    }
                    System.out.println("___________________________________________");
                    System.out.println(Thread.currentThread().getName() + ": READ success [Reading all Records]");
                } finally {
                    this.databaseLock.unlock();
                }
                System.out.println(Thread.currentThread().getName() + ": READ is sleeping");
                Thread.currentThread();
                Thread.sleep(this.getSleepingTime());
            } else {
                this.read.add(true);
            }
		} catch (InterruptedException exception) {
			exception.printStackTrace();
		} finally {
            this.databaseLock.unlock();
			this.readLock.unlock();
		}
	}

    public void UpdateOperation() {
        System.out.println(Thread.currentThread().getName() + ": UPDATE is waiting for request.");
		this.updateLock.lock();
		try {
            Info updateRequest = this.update.poll();
            if (updateRequest != null) {
                this.databaseLock.lock();
                try{
                    boolean updatedRecord = false;
                    for (Info record : this.crud_database) {
                        if (record.id.equals(updateRequest.id) && record.name.equals(updateRequest.name)) {
                            record.practicals = updateRequest.practicals;
                            record.assignments = updateRequest.assignments;
                            updatedRecord = true;
                            break;
                        }
                    }
                    if (updatedRecord == true) {
                        System.out.println(Thread.currentThread().getName() + " success [id = " + updateRequest.id + ", name = " + updateRequest.name + "]");
                    } else {
                        updateRequest.attempt++;
                        if (updateRequest.attempt <= 2) {
                            this.update.add(updateRequest);
                        }
                        System.out.println(Thread.currentThread().getName() + " failed [id = " + updateRequest.id + ", name = " + updateRequest.name + "]");
                    }
                } finally {
                    this.databaseLock.unlock();
                }
            }
            System.out.println(Thread.currentThread().getName() + ": UPDATE is sleeping");
            Thread.currentThread();
            Thread.sleep(this.getSleepingTime());
		} catch (InterruptedException exception) {
			exception.printStackTrace();
		} finally {
            this.databaseLock.unlock();
			this.updateLock.unlock();
		}
	}

    public void DeleteOperation() {
        System.out.println(Thread.currentThread().getName() + ": DELETE is waiting for request.");
		this.deleteLock.lock();
		try {
            Info deleteRequest = this.delete.poll();
            if (deleteRequest != null) {
                this.databaseLock.lock();
                try{
                    boolean deletedRecord = false;
                    for (Info record : this.crud_database) {
                        if (record.id.equals(deleteRequest.id) && record.name.equals(deleteRequest.name)) {
                            this.crud_database.remove(record);
                            deletedRecord = true;
                            break;
                        }
                    }
                    if (deletedRecord) {
                        System.out.println(Thread.currentThread().getName() + " success [id = " + deleteRequest.id + ", name = " + deleteRequest.name + "]");
                    } else {
                        deleteRequest.attempt++;
                        if (deleteRequest.attempt <= 2) {
                            this.delete.add(deleteRequest);
                        }
                        System.out.println(Thread.currentThread().getName() + " failed [id = " + deleteRequest.id + ", name = " + deleteRequest.name + "]");
                    }
                } finally {
                    this.databaseLock.unlock();
                }
            }
            System.out.println(Thread.currentThread().getName() + ": DELETE is sleeping");
            Thread.currentThread();
            Thread.sleep(this.getSleepingTime());
		} catch (InterruptedException exception) {
			exception.printStackTrace();
		} finally {
            this.databaseLock.unlock();
			this.deleteLock.unlock();
		}
	}

    // The Getters and Setters

    public int getSleepingTime() {
		return (int)(Math.random() * (51) + 50);
	}

    public Queue<Info> getCreateQueue() {
		return this.create;
	}

    public Queue<Boolean> getReadQueue() {
		return this.read;
	}

    public Queue<Info> getUpdateQueue() {
		return this.update;
	}

    public Queue<Info> getDeleteQueue() {
		return this.delete;
	}

    public int getNumberOfThreads() {
        return this.CRUD_NumThreads;
    }
}