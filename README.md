# PlantUMLStubber for Generating PlantUML stubs

This project is a springboot application that generates plantuml stubs for component diagrams and sequence diagrams. The generated files can be used to build a repository of re-usable files (services/application) that come in handy if I need to discuss about software architecture or you want to document an application or a service.

## What?
The stubber will use yaml definitions to generate files in the target/ directory. These resulting files are either puml or iuml files. The iuml files contain "todo" marker that are supposed to be filled in with information. 

### YAML configuration
To generate stubs plantumlstubber needs yaml files that contain a number of information, such as name of the application, the domain, what kind of interfaces this application provides.

There is an example file that shows how a yaml file needs to be configured. Use _template_newApplication.yaml as starting point.
The plantumlstubber will consider all yaml files in the target/ folder.

#### Auto-Linking two applications
Sometimes it can be usefull to draw a link between two applications or an application and its database. This can be done on the configuration yaml by fill in these two configuration keys:
```
linkToComponent: ApplicationName
linkToCustomAlias: applicationalias
```

#### Call Stacks
The configuration yaml enables call stacks, meaning the generated stubs contain already the !includes and $function calls to the other application given in the call stack. 
Example:
```
...
   REST::JSON:
        /api/: 
            - convert->ApplicationName_ServiceName_InterfaceName
```

The Rest::JSON Interface /api/convert will call subsequently application with the Name ApplicationName. This application itself needs to provide this interface. There should be a configuration yaml for the application ApplicationName as well.

## Swagger UI and Output
The generator provides a swagger ui on http://localhost:19191/swagger-ui.html#/

## Basic notation
That is how I do it:

*.iuml are includible files that contain a re-usable service definition and may be used with !include
*.puml are plantuml files that are self-sufficient and should not be !included in other files. These files, however, may include .iuml files

This maybe your understanding as well or not, but the stubber will mostly generate .iuml files. The example.puml files give a hint how the generated stubs can be used.

### Output

You can generate two types of diagrams:
* **Component**: Services as component diagrams, that may be used for system context diagrams (Kontextdiagramm). This diagram consist of building blocks that describe how systems interact with each other and what is in scope of an implementation and what not.

* **Sequence**: A number of sequence diagrams that are useful as addition to system context diagrams. In later stages of architectural specifications these are helpful to show how systems interact.


## Requirements for using the PlantUML stubs
* The generated files from the repository need a plantuml version >= 1.2019.6 because it uses the new V2 preprocessor or >= 1.2020.7 if you choose the newer version in the swagger ui
* Plantuml itself needs a graphviz 2.38 installation
* The common files may need some manual clean up if you go with the old version of plantuml due to the fact that they introduced procedures in 1.2020.7 that helps with the backwards compatibility but made my life a bit more difficult 

## Intellij Configuration

You need to configure the working directory  (Java `user.dir`) in Run/Debug of the `PlantUMLStubber` 
(formerly known as`ServiceGenerator`) to the root directory of the module so that the service 
configuration yaml can be found, 
e.g. `$MODULE_WORKING_DIR$` in Intellij: ![](Intellij_Config.png)

_last update 17.05.2020_
