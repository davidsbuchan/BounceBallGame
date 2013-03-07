BuildGame:
	javac -d ../bin ../src/uk/co/ashndave/GamePanel.java ../src/uk/co/ashndave/Game.java

Jarrify: BuildGame 
	(cd ../bin; jar -cvfe Game.jar uk.co.ashndave.Game uk/co/ashndave/Game*)

clean:
	rm -rf ../bin/* 
