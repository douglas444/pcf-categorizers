# pcf-categorizers

This project is a set of *pcf*'s *HighLevelCategorizer* and *LowLevelCategorizer* 
interfaces implementations, intended to be used at *pcf-gui* (https://github.com/douglas444/pcf). 

## Requirements

* Apache Maven 3.6.3 or higher
* Java 8

## Maven Dependencies

* streams 1.0-SNAPSHOT (https://github.com/douglas444/streams)
* pcf-core 1.0-SNAPSHOT (https://github.com/douglas444/pcf)

## How to use *pcf-categorizers* with *pcf-gui*

First of all you need to build the project's JAR.
This can be done by executing the following command line from the root folder:

```
mvn clean package
```

If you want to build the JAR with the dependencies included, 
execute the following command line instead:

```
mvn clean package assembly:single
```

Once the process is successfully finished, the JAR will be available at the ```target``` folder as 
```pcf-categorizers.jar``` or ```pcf-categorizers-jar-with-dependencies.jar```.

Once you have the JAR, load it in the classpath section of the pcf-gui. After that, 
the interfaces *HighLevelCategorizer* and *LowLevelCategorizer* implementations 
should be listed in the graphical interface.

### Observations:

* We configured the JAR's build process in a way that, 
even if you choose to build with the dependencies included, 
the *pcf-core* dependency will not be included. 
The reason is that the *pcf-core* dependency is already provided 
by the *pcf-gui* when the JAR is loaded through the graphical interface.

* If you choose to build the project without the dependencies 
included, make sure to load all the dependencies' JAR
individually at the *pcf-gui* graphical interface. There is no need to load the *pcf-core*
dependency though, since it is already provided by the *pcf-gui*.

