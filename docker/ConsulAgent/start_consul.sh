#!/bin/bash
COMMAND=$0
IPADDR=$1
CONSUL_CMD=/usr/local/share/consul/consul

function runAsLeader {
  echo Starting Consul as Leader
  $CONSUL_CMD agent -server -bootstrap -data-dir /tmp -ui-dir /usr/local/share/consul/dist 
}

function runAsSlave {
  echo Starting Consul as Slave ready to join $IPADDR
  $CONSUL_CMD agent -server -ui-dir /usr/local/share/consul/dist -data-dir /tmp &
  echo Joining $IPADDR
  $CONSUL_CMD join $IPADDR
}

if [[ $COMMAND == "master" ]];
  then runAsLeader;
fi

if [[ $COMMAND == "slave" ]];
  then runAsSlave;
fi

