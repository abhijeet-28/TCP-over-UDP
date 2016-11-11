# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is the fromat of data being sent between the sender and the receiver? ###

* SENDER TO RECEIVER - "<sequence num>,<packet size>,<dummy data>" eg : "21,1000,%%%%%%%%"
* RECEIVER TO SENDER - "<sequence num>,<ack number>" eg: "22,45"

### What exactly do the ack_number and seq_number represent? ###
* all the 1,00,000 bytes to be sent are indexed from 0 to 99,999
* seq_number in a packet represents the index of the first byte in the packet
* ack_number maintained by the receiver represents the index of the first byte as expected by the receiver of the next packet---- i.e it has received the bytes 
of index 0 to ack_num -1
 