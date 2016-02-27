Kurbine: Kubernetes integration with Turbine
--------------------------------------------

This project provides simple integration with Netlix Turbine.

Specifically it provides:

* Kubernetes Instance Discovery
* A Kubernetes ready Docker image for Turbine
* Kubernetes configuration for the Hystrix dashboard
* A simple hystrix example to use as a demo


Instructions
------------

To build to the project:

    mvn clean install   
    
### Starting the discovery server
   
    cd server
    mvn clean package docker:build fabric8:apply


### Starting the dashboars
     
     cd dashboard
     kubectl create -f controller.yml
     kubectl create -f service.yml
     
    
### Running the example    
To start the a hystrix example

     cd examples/hello-hystrix
     mvn clean package docker:build fabric8:apply
     
     