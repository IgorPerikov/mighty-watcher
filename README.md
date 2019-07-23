# mighty-watcher
This utility helps to find issues available for contributing, based on repositories you starred

#### It will search for issues that are:
 - non-assigned 
 - open
 - created less than 1 year ago
 - labeled as `easy` or similar, all labels can be found [here](/src/main/kotlin/com/github/igorperikov/mightywatcher/service/ImportService.kt)
 - starred by account, which issued api access token 

#### Prerequisites:
 - docker installed
 - github api access token, generate it by going to Settings -> Developer Settings -> Personal access tokens -> Generate new token.
  Mighty-watcher requires no scopes. 
  I'd recommend you to save api token in env variable(e.g. `MIGHTY_WATCHER_GITHUB_TOKEN`) rather than passing it around everytime.  

#### How to use:
 - suggest to prepare envvars **TODO**
 - explain INCLUDE/EXCLUDE variables
 - Launch docker container from your favourite terminal: `docker run -e "TOKEN=$MIGHTY_WATCHER_GITHUB_TOKEN" -e "INCLUDE=java,kotlin,go,rust" -e "EXCLUDE=AdoptOpenJDK/jsplitpkgscan" --network host --rm igorperikov/mighty-watcher:latest`
 
#### Example:
![](example.gif)
**NB**: In this example I intentionally made it to look only 20 days in the past, so output can fit 1 screen :smile:

#### Contribution:
 - please mark issues in your repositories if you are willing to get some help
 - contribute your own labels, if this utility misses any
