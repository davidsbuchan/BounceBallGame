# Copyright David Strachan Buchan 2013
# This file is part of BounceBallGame.

# BounceBallGame is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# BounceBallGame is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with BounceBallGame.  If not, see <http://www.gnu.org/licenses/>.

all: clean Jarrify

BuildGame:
	javac -d ../bin ../src/uk/co/ashndave/game/Updateable.java ../src/uk/co/ashndave/game/Renderable.java ../src/uk/co/ashndave/game/GameLoop.java
	javac -d ../bin ../src/uk/co/ashndave/Bomb.java ../src/uk/co/ashndave/GamePanel.java ../src/uk/co/ashndave/KeepyUppy.java

Jarrify: BuildGame 
	(cd ../bin; jar -cvfe Game.jar uk.co.ashndave.Game uk/co/ashndave/Game* uk/co/ashndave/Bomb.class)

clean:
	rm -rf ../bin/* 
