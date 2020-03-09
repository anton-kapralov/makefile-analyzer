all: hello
hello: main.o
	g++ main.o -o hello
main.o: main.cpp
	g++ -c main.cpp
hello: main.o
	g++ main.o -o hello
clean:
	rm *o hello
