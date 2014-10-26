all: build test25 test50 test100 test1000

build:
	javac ShortestPath.java

test25:
	java ShortestPath test-in/sparse_ewg_25vertices.txt > tmp
	vimdiff tmp test-out/sparse_ewg_25vertices_output.txt

test50:
	java ShortestPath test-in/sparse_ewg_50vertices.txt > tmp
	vimdiff tmp test-out/sparse_ewg_50vertices_output.txt

test100:
	java ShortestPath test-in/sparse_ewg_100vertices.txt > tmp
	vimdiff tmp test-out/sparse_ewg_100vertices_output.txt

test1000:
	java ShortestPath test-in/sparse_ewg_1000vertices.txt > tmp
	vimdiff tmp test-out/sparse_ewg_1000vertices_output.txt
