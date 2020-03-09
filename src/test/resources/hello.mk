all: hello
hello: main.o
	g++ main.o -o hello
main.o: main.cpp
	g++ -c main.cpp
clean:
	rm *o hello
