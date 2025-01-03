# Lamp Demo (Java Spring-Boot)
##### The project provides Arrowhead Application demo implementation developed from [application-skeleton project](https://github.com/arrowhead-f/client-skeleton-java-spring)

## Overview
The goal of the project is to simply demonstrate how a consumer could orchestrate for a service and consume it afterward.
##### The Local Cloud Architecture 
🟦 `AH Service Registry`
🟥 `AH Authorization` 
🟩 `AH Orchestrator`
![Alt text](https://github.com/arrowhead-f/sos-examples-spring/blob/master/smart-city/doc/overview.png)

## Service Descriptions
**create-lamp:**

Creates a new lamp instance.
* ***input:*** LampRequestDTO.json
```
{
   "group":"string",
   "status":"string"
}
```
* ***output:*** LampResponseDTO.json
```
{
   "id":"integer",
   "status":"string"
}
```

**get-lamp:**

Returns a lamp list based on the given parameters.
* ***input:*** Query parameters: 

  `group`={group} [*not mandatory*]
  
  `status`={status} [*not mandatory*]

* ***output:*** List of LampResponseDTO.json
```
[{
   "id":"integer",
   "status":"string"
}]
```

## How to run?
1. Clone this repo to your local machine.
2. Go to the root directory and execute `mvn install` command, then wait until the build succeeds.
3. Start the [Arrowhead Framework](https://github.com/eclipse-arrowhead/core-java-spring), before you would start the demo.
   Required core systems:
   * Service Registry
   * Authorization
   * Orchestration
4. Start the provider (it will registrate automatically to the Service Registry Core System).
5. At the very first time, register the consumer manually and create the intra cloud authorization rules.
6. Start the Consumer.

## Configuration
  - Find the `application.properties` confirguration file under the `<project>/src/main/resources` folder before the build or under the `<project>/target` after the build.
  - Default configuration is provided out of the box which works when the Arrowhead Local Cloud is running on your localhost and has the common [testclou2 certificates](https://github.com/eclipse-arrowhead/core-java-spring/tree/master/certificates/testcloud2). 

## Video tutorial
[link](https://www.youtube.com/watch?v=9BHemnv3mQA&t=5s)
