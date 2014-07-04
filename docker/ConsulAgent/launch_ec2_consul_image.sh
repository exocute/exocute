#!/bin/bash
source ../ec2_settings.sh
IMAGE_ID=`ec2-run-instances $BASE_AMI -k $EC2_PRIVATE_KEY -t $INSTANCE_TYPE | grep INSTANCE | cut -f2`

echo -n Started Image $IMAGE_ID

# Check that the image is up and running
while [[ $STATUS != "passed" ]]; do {
  STATUS=`ec2-describe-instance-status $IMAGE_ID | grep INSTANCESTATUS | cut -f3`
  echo -n .
  sleep 5
} done

echo Starting Consul

# Get the IP Address of the Image
IMAGE_ADDR=`ec2-describe-instances $IMAGE_ID | grep INSTANCE | cut -f4`

echo Starting Consul on Image Running on $IMAGE_ADDR

ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -i $EC2_PRIVATE_KEY.pem $USER@$IMAGE_ADDR sudo docker run exocute/consul master
