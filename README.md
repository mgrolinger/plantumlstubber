# This repository moved to: https://codeberg.org/mgrolinger/plantumlstubber
The repository will not be updated here as it moved to a new code hosting place.



# PlantUMLStubber for generating plantUML application/service stubs

This project is a spring-boot application that generates plantUML stubs for component diagrams and sequence diagrams. Those stubs contain information about applications and services. The generated files can be used to build a repository of re-usable files that come in handy to discuss software architecture or to document an application, a service or how application are working together.

[![Java CI with Maven](https://github.com/mgrolinger/plantumlstubber/actions/workflows/maven.yml/badge.svg)](https://github.com/mgrolinger/plantumlstubber/actions/workflows/maven.yml)
[![CodeQL](https://github.com/mgrolinger/plantumlstubber/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/mgrolinger/plantumlstubber/actions/workflows/codeql-analysis.yml)




## What?
This application will use _yaml_ files, which contain certain information, to generate files in the _target/_ directory. These resulting files are either _.puml_ or _.iuml_ files. The _.iuml_ files contain "todo" marker that are need to be filled in with your information. 

This works like this. First you write the yaml files and let them process by this application. The result are plantuml files in the folder "Component" or "Sequence". These files need to be finished by filling out the marked TODOs with according details. Over the time you will build a repository of plantuml files of application/services. 
![](documentation/process.png)


As a next step you may want to define your plantUML files, e.g. to show how an application embeds into the environment. You can reuse the generated services from your repository and use the files by including them into your current file. Then you let plantUML process your file and plantUML will do its magic. The result are the plantUML diagrams.

## Configuration
That is how I personally differentiate:

* _.iuml_ are files that can be included and contain a re-usable service definition. Use them with the directive _!include_
* _.puml_ are plantUML files that are self-sufficient and should not be !included in other files. These files, however, may include .iuml files

The stubber will mostly generate _.iuml_ files. The example.puml files give a hint how you can use the generated stubs.

### YAML configuration
To generate stubs PlantUMLStubber needs yaml files that contains some information, such as name of the application, the domain, what kind of interfaces this application provides.

There is an example file that shows how a yaml file needs to be configured. Use _template_newApplication.yaml as starting point. There is also a rest route available to copy the template to the target folder.
The PlantUMLStubber will consider all yaml files in the target/ folder.

#### Separating names by . or /
PlantUMLStubber treats the characters slash ("/") and dot (".") as separator in the applicationName, service or interface. It will create subdirectories subsequently. In general, PlantUMLStubber removes a lot of special characters due to the fact how plantuml will treat special characters. For example, if you take the variables _$part1_part2_ vs. _$part3+part4_, the first variable will work within plantUML and the latter not, as plantUML will recognize _$part3_ as variable and _+part4_ as a second word. PlantUML will show a syntax error.   

Taking the following example: 
```
applicationName: part1/part2
...
   REST::JSON:
        /api/v2/: 
            - resource
```
Will lead to the following directory and file structure:
```
$ tree
.
└── part1
   └── part2
       └── api
           └── v2
               └── resource.iuml
```
Within the generated _.iuml_ file the !procedure has the name: $Part1_part2_api_v2_resource(). As you may notice, the application replaces the character "/" or "." by "_". This is due to the fact how plantUML will treat different special characters as mentioned above.  


#### Automatically link two applications
Sometimes it can be useful to draw a link between two applications or an application and its database. This can be done on the configuration yaml by fill in these two configuration keys:
```
linkToComponent: ApplicationName
linkToCustomAlias: applicationalias
```

#### Call stacks
The configuration yaml enables call stacks, meaning the generated stubs contain already the !includes and $function calls to the other application given in the call stack. 
Example:
```
...
   REST::JSON:
        /api/: 
            - convert->ApplicationName_ServiceName_InterfaceName
```

The Rest::JSON Interface /api/convert will call subsequently application with the Name ApplicationName. This application itself needs to provide this interface. There should be a configuration yaml for the application ApplicationName as well.

As the call stack definition suits to two cases, first to generate include-path for the files, and second to generate the !procedure call for the just included file, this application cannot differentiate between e.g. rest interfaces /api/rest-interface and /api/rest/interface. PlantUMLStubber uses the latter, so the application handles all special characters that may produce problems as "/". Please be aware of that.   

#### Domain of an interface
Although an application should reside within a single domain, it might be necessary to assign a different domain (color) to an interface. You may do so with specifying a domain within the interface definition. Just ad a domain surrounded by <<>>, e.g. <<customer>> that will override the domain of the application.
```
   REST::JSON:
        /api/: 
            - /interface<<authentifizierung>>
```

#### HTTP methods
For Rest interfaces it might be interesting to specify the supported HTTP methods. The definition must be added in the interface by starting "::" and separating the single methods by ":", as you can see in the example below for ::POST:GET
```
   REST::JSON:
        /api/: 
            - /interface::POST:GET
```

#### Combining interface specifications
Domain of Interfaces, HTTP Methods and Call Stacks can be combined into one specification like below.
```
   REST::JSON:
        /api/: 
            - /interface<<authentifizierung>>::POST:GET->App_Service_interface
            - /interface->App_Service_secondInt::POST:GET<<authentifizierung>>
```
The order does not matter.

#### Important note for interfaces with the same name
If an application has e.g. two service implementations (especially "EMPTY") and both have the same interfaceName, the last one wins. E.g. you have a soap service _getVersion()_ and a rest service _/getVersion_ without any additional (service) path, the last definition will override the preceding.
```
...
SOAP::XML:
    EMPTY: [getVersion]
REST::JSON:
    EMPTY: [/getVersion]
...
```


## Swagger UI and Output
The generator provides a swagger ui on http://localhost:19191/swagger-ui.html#/

### Output

You can generate two types of diagrams:
* **Component**: Services as component diagrams, that may be used for system context diagrams (Kontextdiagramm). This diagram consists of building blocks that describe how systems interact with each other and what is in scope of an implementation and what not.

* **Sequence**: A number of sequence diagrams that are useful as addition to system context diagrams. In later stages of architectural specifications these are helpful to show how systems interact.

#### Example
Using the _template_newApplication.yaml without modifying it would generate in the root folder "Component", for instance the following folders.  
![](documentation/component_folder_result.png)

Please note that there are a number of other sub-folders as well, such as email server and file system. For those are yaml files packaged in the project as well.

Stepping down into the folder NewApplication (generated from the template), you will find a newApplication_example.puml. This file will use plantUML to generate the following image.
![](documentation/component_generated_result.png)

## Building your own repository
Over the time more and more application fill up a repository and are available for re-use. I usually have one subfolder where I keep those re-usable files (e.g. _includes_) and a second subfolder (e.g. _documentation_) where I keep my files for the specific use case.

Files from the _documentation_ folder may !include files from the _include_ folder, but you may want to prevent to !include within the folder. 
Files in the _include_ folder may !include each other, but should never !include files from the _documentation_ folder.

```
$ tree
.
├── documentation
│   └──  use case 1
│       ├── use case 1.1.puml
│       └── use case 1.2.puml
└── includes
    ├── Application 1
        └── Service 1
            └── interface1.iuml
    ├── Application 2
    └──  etc...
```

## Requirements for using the PlantUML stubs
* The generated files from the repository need a plantUML version >= 1.2020.7
* PlantUML itself requires a graphviz 2.38 installation or newer

## Intellij Configuration

You need to configure the working directory  (Java `user.dir`) in Run/Debug of the `PlantUMLStubber` 
(formerly known as`ServiceGenerator`) to the root directory of the module so that the service 
configuration yaml can be found, 
e.g. `$MODULE_WORKING_DIR$` in Intellij: ![](documentation/Intellij_Config.png)

## Known issues
* A Rest-API or method containing a hyphen does not work very well. The challenge is that plantuml will identify a string "foo-bar" not as one but two separate stings. This, however, does not work well when the PlantUMLStubber generates a method name from this. That's why I decided to transform a hyphen to an underscore. Underscores on the other hand will result in a subdirectory. Taking the example from the beginning: "foo-bar" will be "foo/bar.iuml" rather than "foo-bar.iuml", as you would expect. This was also a consequence of introducing the _callstack_ feature, which is described above.  

## Future Plans

* Automatic color schemes for domain colors if the domain is not defined
* Get rid of FA_SERVER and other macros and use their sprites directly, incl. more supported types

_last update 13.07.2021_
