kbd.o command.o files.o: command.h
bigoutput littleoutput : text.g
	generate text.g -$(subst output,,$@) > $@
