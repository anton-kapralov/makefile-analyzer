# First line comment
# Second line comment
 all:  hello # Comment for hello
# After "all" comment
hello: main.o   general.o \
# Internal prerequisite comment
       a\
       b # Comment for b
# Internal rule comment
	g++  main.o -o hello

main.o: main.cpp
	g++ -c main.cpp



clean:
	rm *o hello


# End comment
