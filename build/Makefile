all: clean Jarrify

BuildGame:
	javac -d ../bin ../src/uk/co/ashndave/Bomb.java ../src/uk/co/ashndave/GamePanel.java ../src/uk/co/ashndave/Game.java

Jarrify: BuildGame 
	(cd ../bin; jar -cvfe Game.jar uk.co.ashndave.Game uk/co/ashndave/Game* uk/co/ashndave/Bomb.class)

clean:
	rm -rf ../bin/* 
