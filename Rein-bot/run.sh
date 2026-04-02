#!/bin/bash
javac Main.java
jar cfe Rein-bot.jar Main Main.class
cd ..
java -jar harness.jar