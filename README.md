# mighty-watcher
Utility serves purpose to find issues available for contributing

#### It will search for issues that are:
 - non-assigned 
 - open
 - created less than 2 years ago
 - labeled as `easy` or similar, default list can be found [here](/src/main/kotlin/com/github/igorperikov/mightywatcher/service/ImportService.kt)
 - starred by account, which issued api access token 

#### How to use:
 - generate an API access token: go to Settings -> Developer Settings -> Personal access tokens -> Generate new token. Not a single scope is needed.
 - add it to env variables as whatever you prefer or pass directly to docker run command  
 - ` docker run -e "TOKEN=$MIGHTY_WATCHER_GITHUB_TOKEN" -e "INCLUDE=java,kotlin,go,rust" -e "EXCLUDE=AdoptOpenJDK/jsplitpkgscan" --network host --rm igorperikov/mighty-watcher:latest`

#### Contribution:
 - please mark issues in your repositories if you are willing to get some help
 - contribute your own labels, if this utility misses any
