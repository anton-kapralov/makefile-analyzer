all: hello
hello: main.o
	g++ main.o -o hello
main.o: main.cpp
	g++ -c main.cpp
foo: hello
clean:
	rm *o hello
