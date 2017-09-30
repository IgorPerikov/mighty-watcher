heimdallr
---
> the one who illuminates the world

### Description

cluster work possible only in deployment, where every node see every other node as `localhost`

project with ability to ~~illuminate the world~~ create nodes and build cluster upon them, kv database in future

different distribution algorithms and inter-cluster communication coming in,
 such as (probably coming in the same order as below):

* [x] init cluster creation
* [x] anti-entropy mechanism
* [x] sending diffs where possible(initiator send whole state, other node sends back diff)
* [x] heartbeat
* [ ] push rumor mongering epidemic with configurable feedback + counter system

### How to launch

simply an executable .jar file, launch with `java -jar`

specify first or all three params, which are:
* port which node will listen for inter-node communication
* peer node address
* peer node port

peer node specified if at least 1 node is already exists

### Whitepapers used:
* [Epidemic algorithms](http://www.bitsavers.org/pdf/xerox/parc/techReports/CSL-89-1_Epidemic_Algorithms_for_Replicated_Database_Maintenance.pdf)
