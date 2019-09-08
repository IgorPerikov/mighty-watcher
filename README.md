![Codacy grade](https://img.shields.io/codacy/grade/fc40498ca7ee4d1695550e202944f2ab)
[![Build Status](https://travis-ci.org/IgorPerikov/mighty-watcher.svg?branch=master)](https://travis-ci.org/IgorPerikov/mighty-watcher)
![GitHub](https://img.shields.io/github/license/igorperikov/mighty-watcher)
![Docker Pulls](https://img.shields.io/docker/pulls/igorperikov/mighty-watcher)

# Mighty Watcher
This utility helps to find issues available for contributing, based on repositories you starred

## It will search for issues that are
-  non-assigned 
-  open
-  created less than 1 year ago
-  labeled as `help wanted` or similar, all labels can be found [here](/src/main/kotlin/com/github/igorperikov/mightywatcher/service/LabelsService.kt)
-  starred by account, which issued API access token 

## Prerequisites
-  Docker [installed][1]
-  Github API access token obtained. You could generate it by going to your GitHub [personal access tokens page][2]
  Mighty Watcher requires no scopes. 
  I'd recommend you to put API token in env variable (e.g. `MIGHTY_WATCHER_GITHUB_TOKEN`) rather than passing it around everytime.  

## How to use
- Set environment variables:
  - Required:
    - `TOKEN` - GitHub API access token
  - Optional:
    - `INCLUDE` - comma-separated language names to be included (only main language of repository counts), if none passed - include all 
    - `EXCLUDE` - comma-separated repository names to be fully excluded from analysis in form `$owner/$name`, e.g. `IgorPerikov/mighty-watcher`
- Launch Docker container from terminal: 
 ```sh
   docker pull igorperikov/mighty-watcher:latest
   docker run -e "TOKEN=$MIGHTY_WATCHER_GITHUB_TOKEN" \
           -e "INCLUDE=java,kotlin,go" \
           -e "EXCLUDE=IgorPerikov/mighty-watcher" \
           --rm igorperikov/mighty-watcher:latest
 ```

## Example
<p align="center"><img src="/docs/example.gif?raw=true"/></p>

**NB**: In this example I intentionally made it to look only 20 days in the past, so output can fit 1 screen :smile:

## Rate limiting
Github lets you make up to 5000 API calls per hour, so you're fine as long as you have less than ~1500 starred repositories
that match your INCLUDE/EXCLUDE parameters. Let me know if that's a problem for you.

## Contribution
-  please mark issues in your repositories if you are willing to get some help
-  contribute your own labels, if the list misses any

[1]: https://docs.docker.com/install/
[2]: https://github.com/settings/tokens
