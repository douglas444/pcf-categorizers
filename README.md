# pcf-categorizers

This project is a set of pcf's HighLevelCategorizer and LowLevelCategorizer classes implementations (https://github.com/douglas444/pcf).

## Requirements

* Apache Maven 3.6.3 or higher

## Maven Dependencies

* streams 1.0-SNAPSHOT (https://github.com/douglas444/streams)
* minas 1.0-SNAPSHOT (https://github.com/douglas444/minas)
* pcf-categorizers 1.0-SNAPSHOT (https://github.com/douglas444/pcf-categorizers)
* pcf-core 1.0-SNAPSHOT (https://github.com/douglas444/pcf)

## Requirements

* Apache Maven 3.6.3 or higher

## Maven Dependencies

* streams 1.0-SNAPSHOT (https://github.com/douglas444/streams)
* pcf-core 1.0-SNAPSHOT (https://github.com/douglas444/pcf)
* junit-jupiter 5.6.2 (available at Maven Central Repository)
* commons-math3 3.6.1 (available at Maven Central Repository)

## How do I build the JAR from the source code?

To build the JAR without the dependencies, execute the following command line from the root folder:

```mvn clean package```

To build the JAR with the dependencies included, execute the following command line from the root folder:

```mvn clean package assembly:single```

Once the process is finished, the JAR will be available at the ```target``` folder as 
```pcf-categorizers.jar``` or ```pcf-categorizers-jar-with-dependencies.jar```.

### Observations:

* We configured the build process in a way that, even if you choose to build with the dependencies included, the pcf-core dependency will not be included. 
The reason is that the pcf-core dependency is already provided by the pcf-gui when the JAR is loaded through the interface.

* If you choose to build the project without the dependencies included, make sure to load all the JAR dependencies individually at the pcf-gui interface. 
There is no need to load the pcf-core dependency though, since it is already provided by the pcf-gui.

## How do I use pcf-categorizers at pcf-gui?

Once you have the JAR, load it in classpath section of the pcf-gui, after that, the class ECHOInterceptable should be listed at the interface.
