heimdallr
---
> the one who illuminates the world

### Description

project with ability to ~~illuminate the world~~ create nodes and build cluster upon them

different distribution algorithms and inter-cluster communication coming in,
 such as (probably coming in the same order as below):

* [ ] init cluster creation
* [ ] epidemic(rumor mongering) - periodically chooses another node to share an update,
     stop sharing updates when specified count of nodes replied that they already have an update or 
     with some probability after every decline(or whatever is the feedback) anti-entropy as backup strategy for this
     adding + removing nodes
* [ ] heartbeat
* [ ] quorum calculation
* [ ] raft
* [ ] jepsen tests for raft
* [ ] byzantine fault injection
* [ ] byzantine fault tolerance
* [ ] jepsen tests for byzantine fault tolerance
* [ ] multiple dc 

### How to launch

TBD