# pcf-categorizers

This project is a set of pcf's HighLevelCategorizer and LowLevelCategorizer classes implementations (https://github.com/douglas444/pcf).

## Requirements

* Apache Maven 3.6.3 or higher

## Maven Dependencies

* streams 1.0-SNAPSHOT (https://github.com/douglas444/streams)
* minas 1.0-SNAPSHOT (https://github.com/douglas444/minas)
* echo 1.0-SNAPSHOT (https://github.com/douglas444/echo)
* pcf-core 1.0-SNAPSHOT (https://github.com/douglas444/pcf)

## Build the JAR

To build without the dependencies: 

```mvn clean install```

To build with the dependencies included (except pcf-core dependency): 

```mvn clean install assembly:single```

### Observations about the commands to build the JAR

1. We configured the build process in a way that, even if you choose to build with the dependencies included, the pcf-core dependency will not be included. 
The reason is that the pcf-core dependency is already provided by the pcf-gui when the JAR is loaded through the interface.

2. If you choose to build the project without the dependencies included, make sure to load all the JAR dependencies individually at the pcf-gui interface. 
There is no need to load the pcf-core dependency though, since it is already provided by the pcf-gui.

## Using it

Once you have the JAR, load it in classpath section of the pcf-gui, after that, the implementations should be listed at the interface.
