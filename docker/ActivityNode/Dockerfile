FROM ubuntu:14.04
MAINTAINER simon.kent@simonkent.com
% Update apt
RUN apt-get update
% Install wget
RUN apt-get -qqy install wget
% Download, install jdk 1.8 and configure it
RUN wget --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u5-b13/jdk-8u5-linux-x64.tar.gz
RUN mkdir /opt/jdk
RUN tar -zxf jdk-8u5-linux-x64.tar.gz -C /opt/jdk
RUN update-alternatives --install /usr/bin/java java /opt/jdk/jdk1.8.0_05/bin/java 100
RUN update-alternatives --install /usr/bin/javac javac /opt/jdk/jdk1.8.0_05/bin/javac 100
% Copy the Clifton Exocute Platform jar
RUN mkdir /usr/local/share/exocute
ADD CliftonExocutePlatform-1.0.jar /usr/local/share/exocute/CliftonExocutePlatform-1.0.jar
% Configure the image to run the Activity Node
CMD java -jar /usr/local/share/exocute/CliftonExocutePlatform-1.0.jar
