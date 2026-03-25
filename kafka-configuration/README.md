
## Kafka

Btw the config is hardcoded, can use .env ofc


---

gRPC - REST synchronous (?) (afaik gRPC can do bidirectional streaming and async with pub-sub)

But for 1 - many ?

Kafka, async communication 

Publish the event, e.g. CreatePatient on Patient Service => Put on Announcement (Kafka Topic) => Another service e.g. Analytics can consume it separatedly, no need to blocking wait

Kafka Broker
- Server, store and deliver message to Consumer
    - Topic, Categorized channel of messages
        - Hold different events that match certain category
    - Event, data/message that stored on a topic

Below is lived on each services :
- Producer, send events into spesific kafka Topic 
- Consumer, read events from (at least 1) kafka Topic 

### Env setup 

```python
#  internal = localhost and external addresses
KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094;
#
KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER;
#
KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@kafka:9093;
#
KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,PLAINTEXT:PLAINTEXT;
#
KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094;
#
KAFKA_CFG_NODE_ID=0;
# role of kafka service
# controller = main container, orchestrating others
KAFKA_CFG_PROCESS_ROLES=controller,broker

```

Setup Kafka GUI with `Tools for Apache Kafka` Extension

Setup topic Kafka :
- 