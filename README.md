##OpenStreetMap Routing
### Build

    $ mkdir build
    $ cd build
    $ cmake ..
    $ make
###Run
You'll need an osm:

    $ wget http://reccos.inf.unideb.hu/~norbi/res/debrecen.osm
	
In dist directory:

	$ ./routing path/to/osm start_lat start_lon end_lat end_lon
or

	$ ./routing path/to/osm

### Build Viewer

	$ mvn clean compile package assembly:single

### Run

	$ java -jar target/popcornmaci-0.0.1-jar-with-dependencies.jar
