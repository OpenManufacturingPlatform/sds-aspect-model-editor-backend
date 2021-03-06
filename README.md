# Aspect Model Editor backend

## Table of Contents

- [Introduction](#introduction)
- [Getting help](#getting-help)
- [Setup](#setup)
- [Build and run](#build-and-run)
- [License](#license)

## Introduction

This project is used as the backend for the Aspect Model Editor and interacts with the [SDS-SDK](https://github.com/OpenManufacturingPlatform/sds-sdk) to create [BAMM](https://github.com/OpenManufacturingPlatform/sds-bamm-aspect-meta-model) specific Aspect Models.

## Getting help

Are you having trouble with Aspect Model Editor backend? We want to help!

* Check the [developer documentation](https://openmanufacturingplatform.github.io)
* Check the BAMM [specification](https://openmanufacturingplatform.github.io/sds-documentation/bamm-specification/v1.0.0/index.html)
* Having issues with the Aspect Model Editor backend? Open a [GitHub issue](https://github.com/OpenManufacturingPlatform/sds-aspect-model-editor-backend/issues).

## Setup

* Download and install [JDK 11](https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_windows-x64_bin.zip)
* Download and Install [Maven](https://maven.apache.org/download.cgi)
* Environment Settings
  * Add "{JAVA_HOME}/bin" to PATH
  * Add "{MAVEN_HOME}/bin" to PATH
* Configure Maven Settings

> Note : Configure your IDE to use the new JDK and Maven installation.

## Build and run

```
mvn clean package
mvn exec:java -Dexec.mainClass="io.openmanufacturing.ame.Application"
```

We are always looking forward to your contributions. For more details on how to contribute just take a look at the
[contribution guidelines](CONTRIBUTING.md). Please create an issue first before opening a pull request.

## License

SPDX-License-Identifier: MPL-2.0

This program and the accompanying materials are made available under the terms of the
[Mozilla Public License, v. 2.0](LICENSE).

The [Notice file](NOTICE.md) details contained third party materials.

## GraalVm native-image

To build a native image we use GraalVm: [GraalVm](https://github.com/oracle/graal/tree/vm-ce-22.1.0)
