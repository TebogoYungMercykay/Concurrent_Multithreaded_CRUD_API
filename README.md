# CRUD API - Implementaion in JAVA-8

---

# `Introduction`

- This Implementation mimics a `database development` team using `multi-threading` to handle database `requests`.
- ##### An `API request` performs any of the following `operations` on the database:


  - `CREATE`: Add a new record to the database.
	```java
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
	```

  - `READ`: Returns a copy of the all records in the database.
  - `UPDATE`: Update a specific record in the database given partial information, `id` and `name`, about the record and the new information, `practicals` and `assignments`,to update. Updates first match.
  - `DELETE`: Delete a specific record from the database matching the given information, `partial/full`. Delete first match.
	```java
	public void DeleteOperation() {
		System.out.println(Thread.currentThread().getName() + ": DELETE is waiting for request.");
		this.deleteLock.lock();
		try {
			Info deleteRequest = this.delete.poll();
			if (deleteRequest != null) {
				boolean deletedRecord = false;
				for (Info record : this.crud_database) {
					if (record.id.equals(deleteRequest.id) && record.name.equals(deleteRequest.name)) {
						this.crud_database.remove(record);
						deletedRecord = true;
						break;
					}
				}
			}
			System.out.println(Thread.currentThread().getName() + ": DELETE is sleeping");
			Thread.currentThread();
			Thread.sleep(this.getSleepingTime());
		} catch (InterruptedException exception) {
			exception.printStackTrace();
		} finally {
			this.deleteLock.unlock();
		}
	}
	```

---

# `API Requests`
- The various requests to be made are `stored` in different `queues` based on the operation `type`.
	```java
	import java.util.LinkedList;
	import java.util.Queue;

	public class Crud {
		private volatile Queue<Info> create = new LinkedList<>();
		private volatile Queue<Boolean> read = new LinkedList<>();
		private volatile Queue<Info> update = new LinkedList<>();
		private volatile Queue<Info> delete = new LinkedList<>();
	}
	```
- ### For each operation:
	- There are `n` number of `threads` that handles the requests for that specific operation.
	- Requests perform operations on the `shared database`.
	- Only `one` thread may operate on the database at any given `time`.
	- The different operations are handled `concurrently`.
	- A request fails if the is no match, for `UPDATE` and `DELETE`, and the `attempt` attribute gets `incremented`.
	- If a request exceeds `2 attempts` the request is `deleted` otherwise its `added back` to the operations queue.
	- Threads `sleeps` for `random` amount of time between `50` and `100ms` after handling a request.
	- Threads will operate until all request are handled i.e. the respective operation queue is `empty`.

- ### The following output is produced:
	- When a thread is ready to handle a request (they are not sleeping):
	`[Thread-Name] [Operator-Name] is waiting for request.`
	- If a thread completes a request successfully:
	`[Thread-Name] [Operator-Name] success [request-info]`
	- If the request is a READ output the database records:
		```text
		[Thread-Name] [Operator-Name]
		_______________
		[record-1-info]
		[record-2-info]
		...
		[record-N-info]
		_______________
		```
	- If a thread does not complete a request successfully:
	`[Thread-Name] [Operator-Name] failed [request-info]`
	- When a thread sleeps:
	`[Thread-Name] [Operator-Name] is sleeping.`
	- Due to the concurrent nature of the program, many outputs may be interleaved, however there are a couple of basic rules to follow:
		- If there has been a "waiting" output, there may not be another one until a "success" or "failed" and a "sleeping" output by the same thread.
		- A request must receive a "success" output before the effect reflects in the read operator output.
- ## `Note Well`:
	- A general rule of thumb is that each queue should have their `OWN` lock when accessed.
	- Any locks used in the program are written from `scratch`. i.e NO using javas `pre-built locks`.
	- I made use of the below mentioned Javas `pre-built` data structures.
		- `LinkedList`
		- `Queue`
	- The `Bakery-Lock` used is `FAIR`.

---
---

# `Requirements before running codes`:

- Install an `IDE` that `compiles` and `runs` Java codes. Recommendation `VS Code`
- How to setup `WSL` Ubuntu terminal shell and run it from `Visual Studio Code`:
  visit: https://www.youtube.com/watch?v=fp45HpZuhS8&t=112s
- How to Install `Java JDK 17` on `Windows 11`: https://www.youtube.com/watch?v=ykAhL1IoQUM&t=136s
- #### `Installing Oracle JDK on Windows subsystem for Linux`.

  - Run WSL as Administrator
  - set -ex
  - NB: Update links for other JDK Versions
  - export JDK_URL=http://download.oracle.com/otn-pub/java/jdk/8u131-b11/d54c1d3a095b4ff2b6607d096fa80163/jdk-8u131-linux-x64.tar.gz
  - export UNLIMITED_STRENGTH_URL=http://download.oracle.com/otn-pub/java/jce/8/jce_policy-8.zip
  - wget --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" ${JDK_URL}
  - Extract the archive: tar -xzvf jdk-*.tar.gz
  - Clean up the tar: rm -fr jdk-*.tar.gz
  - Make the jvm dir: sudo mkdir -p /usr/lib/jvm
  - Move the server jre: sudo mv jdk1.8* /usr/lib/jvm/oracle_jdk8
  - Install unlimited strength policy: wget --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" ${UNLIMITED_STRENGTH_URL}
  - unzip jce_policy-8.zip
  - mv UnlimitedJCEPolicyJDK8/local_policy.jar /usr/lib/jvm/oracle_jdk8/jre/lib/security/
  - mv UnlimitedJCEPolicyJDK8/US_export_policy.jar /usr/lib/jvm/oracle_jdk8/jre/lib/security/
  - sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/oracle_jdk8/jre/bin/java 2000
  - sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/oracle_jdk8/bin/javac 2000
  - sudo echo "export J2SDKDIR=/usr/lib/jvm/oracle_jdk8 export J2REDIR=/usr/lib/jvm/oracle_jdk8/jre export PATH=$PATH:/usr/lib/jvm/oracle_jdk8/bin:/usr/lib/jvm/oracle_jdk8/db/bin:/usr/lib/jvm/oracle_jdk8/jre/bin export JAVA_HOME=/usr/lib/jvm/oracle_jdk8 export DERBY_HOME=/usr/lib/jvm/oracle_jdk8/db" | sudo tee -a /etc/profile.d/oraclejdk.sh

---

# `Makefile`

##### NB: A makefile Is Included to compile and run the codes on the terminal with the following commands:=

- make clean
- make
- make run

```Java
default:
	javac *.java
run:
	java Main
clean:
	rm -f *.class
	reset
	clear
tar:
	tar -cvz *.java -f Code.tar.gz
untar:
	tar -zxvf *.tar.gz
```

---

---

<p align="center">The End, Thank You</p>

---
