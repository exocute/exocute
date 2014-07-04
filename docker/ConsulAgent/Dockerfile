FROM ubuntu:14.04
MAINTAINER simon.kent@simonkent.com
# Update apt
RUN apt-get update
# Install consul
RUN mkdir /usr/local/share/consul
ADD consul /usr/local/share/consul/consul
ADD start_consul.sh /usr/local/share/consul/start_consul.sh
ADD dist /usr/local/share/consul/dist
RUN chmod -R 755 /usr/local/share/consul
# Expose the service ports:
#  - Server RPC (8300)
#  - Serf LAN (8301)  
#  - Serf WAN (8302)
#  - Client RPC (8400)
#  - HTTP API (8500)
#  - DNS (8600)
EXPOSE 8300 8301 8302 8400 8500 8600/udp
# Use the entry point to start consul
# docker run exocute/consul 
ENTRYPOINT ["/bin/bash", "-c", "source /usr/local/share/consul/start_consul.sh"]
