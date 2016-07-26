/***********************************/
COMP9415 Assginment 2 - Extensions

	
Group: H14A_1320

Students: 
Xiangyu Li 	z3449803
Chenyi Liu	z5034407

/***********************************/



1. Build a model with walking animation for avatar
	- the avatar can move forward, backward, turn left and right
	- avatar's legs will move when it walks
	- avatar will pace when it turns
	- animation stops when avatar stands and doesn't move
        - In file: Avatar.java, Game.java

2. Sun move and change colour according to the time of day
	- one day is one minute
	- first few seconds are sunrise, and last seconds are sunset
	- the colour of light and background changes during different period of time in one day
	- the light source (sun) moves in sine curve 10 units above the ground
	- In file: Terrain.java, MathUtil.java

3. Fix road extrusion so roads can go up and down hills
	- In file: Drawing.java, Road.java
	- there are 2 detail level provided


Keys description:

	UP/DOWN/LEFT/RIGHT	Avatar moving around the world
	W/S/A/D 		moving around the camera
	C	 		change to first person or third person camera
	B			change to high quantity graphic mode or change back
	SPACE 			pick up the item to win the game
	 
	