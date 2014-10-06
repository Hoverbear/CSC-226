all: AVLTree test-small test-medium test-large

AVLTree:
	javac AVLTree.java

test-small:
	cat ./tests/place_names_small.txt | java AVLTree


test-medium:
	cat ./tests/place_names_med.txt | java AVLTree


test-large:
	cat ./tests/place_names_large.txt | java AVLTree
