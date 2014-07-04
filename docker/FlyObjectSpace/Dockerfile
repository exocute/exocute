FROM ubuntu:14.04
MAINTAINER simon.kent@simonkent.com
# Update apt
RUN apt-get update
# Install 32 bit libraries so fly will work
RUN apt-get -qqy install lib32z1
RUN apt-get -qqy install lib32ncurses5
RUN apt-get -qqy install lib32bz2-1.0
RUN apt-get -qqy install gcc-4.7
# Copy the fly
RUN mkdir /usr/local/share/fly
ADD fly /usr/local/share/fly/fly
# Install consul
RUN mkdir /usr/local/share/consul
ADD consul /usr/local/share/fly/consul
# Expose the fly port
EXPOSE 4396
RUN chmod 755 /usr/local/share/fly/fly
# Use the entry point to pass parameters Tags to the space
# docker run -p 4396  JarSpace SignalSpace DataSpace
ENTRYPOINT /usr/local/share/fly/fly
