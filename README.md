
# Exmaple IoT Configurator <img align="right" src="./readme_images/picmg_logo.png" width=8% height=8%><img align="right" src="./readme_images/SmartSensorLogo.png" width=20% height=20%>

## Overview

This project implements an example firmware configuration tool that meets the requirements found in the PICMG(R) IoT.1 (IoT Firmware) specification. More information about PICMG IoT.1 can be found on the PICMG website (www.picmg.org).  The configurator is one of several references implementations provided by PICMG to demonstrate implementation of the IoT specifications.

A typical use case of the configurator involves a user who wishes to create a smart sensor but does not nessarily have the proficiency to create frimware code. The configurator allows the user to configure the fimware, guided by constraints of the hardware used, such that a working configuration is generated without requiring coding knowledge. It is assumed that the user has one or more sensors and effecters that they wish to connect to an I/O module, an I/O module (such as a PICMG MicroSAM module), and knowlege of the mode of operation that they wish to use the sensors/effecters.  In addition, the vendor of the I/O module has provided a Controller Capabilities file that documents the capabilities of the I/O module and vendor-specific build tools for the firmware.  This use-case is shown in the following image.

<img align="center" src="./readme_images/UseCase.png" width=60% height=60%>

**This configurator example is not dependent upon a specific target hardware device for the firmware, although other PICMG example code assumes a PICMG MicroSAM module based on the Atmega 328PB microcontroller.**

The primary features of this confugrator are:
- Create configuration files (config.json) based on constraints found in a Controller Capabilities file.
- Define / modify sensor definitions for use in device configurations
- Define / modify effecter definitions for use in device configurations
- Define / modify new state sets for use with state sensors and state effecters in device configurations

Other example code from PICMG can be found here:
- https://github.com/PICMG/iot_builder - example code that converts configurator output (config.json) to C code that completes the firmware configuration for a Atmega 328PB-based firmware build.  This code can be adapted to other microcontrollers with minor modification.
- https://github.com/PICMG/iot_firmware.git - example firmware implementation for the Atmega328PB.  This code instantiates many of the features described in the PICMG IoT.1 Firmware Specification.  Run-time features of the code are tuned through use of the configurator and builder tools.

## Example Controller Capabilities Json

An example controller capabilities file can be found in the repository at ./configurator/microsam_new3.json

## Build Tools

The configurator was developed using Maven, the IntelliJ Idea IDE, and Java 16 on Microsoft Windows
