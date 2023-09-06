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