all: build test10 test20 test100

build:
	javac MWST.java

test10:
	java MWST ./test-in/ewg_10vertices.txt > out-10
	vimdiff out-10 ./test-out/ewg_10vertices_output.txt
	rm out-10

test20:
	java MWST ./test-in/ewg_20vertices.txt > out-20
	vimdiff out-20 ./test-out/ewg_20vertices_output.txt
	rm out-20

test100:
	java MWST ./test-in/ewg_100vertices.txt > out-100
	vimdiff out-100 ./test-out/ewg_100vertices_output.txt
	rm out-100
