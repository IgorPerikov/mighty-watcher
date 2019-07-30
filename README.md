# mighty-watcher
This utility helps to find issues available for contributing, based on repositories you starred

### It will search for issues that are:
 - non-assigned 
 - open
 - created less than 1 year ago
 - labeled as `help wanted` or similar, all labels can be found [here](/src/main/resources/labels)
 - starred by account, which issued api access token 

### Prerequisites:
 - docker installed
 - github api access token, generate it by going to Settings -> Developer Settings -> Personal access tokens -> Generate new token.
  Mighty-watcher requires no scopes. 
  I'd recommend you to put api token in env variable (e.g. `MIGHTY_WATCHER_GITHUB_TOKEN`) rather than passing it around everytime.  

### How to use:
 - There are 3 environment variables you should know about:
   - `TOKEN` - plain api access token, **the only required parameter** to pass
   - `INCLUDE` - comma-separated language names to be included(only main language of repository counts), if none passed - include all 
   - `EXCLUDE` - comma-separated repository names to be fully excluded from analysis in form `$owner/$name`, e.g. `IgorPerikov/mighty-watcher`
 - Launch docker container from terminal: 
 `docker run -e "TOKEN=$MIGHTY_WATCHER_GITHUB_TOKEN" -e "INCLUDE=java,kotlin,go,rust" -e "EXCLUDE=IgorPerikov/mighty-watcher" --network host --rm igorperikov/mighty-watcher:latest`

### Example:
<p align="center"><img src="/docs/example.gif?raw=true"/></p>

**NB**: In this example I intentionally made it to look only 20 days in the past, so output can fit 1 screen :smile:

### Rate limiting:
Github lets you make up to 5000 api calls per hour, so you're fine as long as you have less than ~1500 starred repositories
that match your INCLUDE/EXCLUDE parameters. Let me know if that's a problem for you.

### Contribution:
 - please mark issues in your repositories if you are willing to get some help
 - contribute your own labels, if the list misses any
