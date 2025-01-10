# D7042E
## Overview
The goal of this project is to simulate a smart city. This project developed from [application-skeleton project](https://github.com/arrowhead-f/client-skeleton-java-spring) is part of the coursework for the D7042E class.

## How to run?
1. Clone this repo to your local machine.
2. Go to the code directory and execute `mvn install` command, then wait until the build succeeds.
3. Start the [Arrowhead Framework](https://github.com/eclipse-arrowhead/core-java-spring), before you would start the demo.
   Required core systems:
   * Service Registry
   * Authorization
   * Orchestration
   * Event Handler
4. Run lightSensorValues.py and weatherSensorValues.py.
5. Start the weather and light providers, then the controller.
6. At the very first time, register the street light consumer manually and create the intra cloud authorization rules.
7. Start the Consumer.

## Configuration
  Arrowhead Local Cloud is running on your localhost and has the common [testclou2 certificates](https://github.com/eclipse-arrowhead/core-java-spring/tree/master/certificates/testcloud2). 
