heimdallr
---
> the one who illuminates the world

### Description

project with ability to ~~illuminate the world~~ create nodes and build cluster upon them, kv database in future

different distribution algorithms and inter-cluster communication coming in,
 such as (probably coming in the same order as below):

* [x] init cluster creation
* [x] anti-entropy mechanism
* [ ] direct mail mechanism
* [ ] rumor mongering epidemic (push/pull, counters/feedback, etc)
* [ ] heartbeat
* [ ] quorum calculation
* [ ] raft
* [ ] jepsen tests for raft
* [ ] byzantine fault injection
* [ ] byzantine fault tolerance
* [ ] jepsen tests for byzantine fault tolerance
* [ ] multiple dc 
* [ ] database recovery from backup

### How to launch

simply an executable .jar file, launch with `java -jar`

specify first or all three params, which are: 
* port which will new node will listen
* peer node address
* peer node port

peer node specified if at least 1 node is already exists
