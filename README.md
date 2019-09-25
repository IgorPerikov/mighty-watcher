<img src="/docs/logo_deepskyblue.png?raw=true" width=60% height=60%/>

![Codacy grade](https://img.shields.io/codacy/grade/fc40498ca7ee4d1695550e202944f2ab)
![Codecov](https://img.shields.io/codecov/c/github/igorperikov/mighty-watcher)
![Travis (.org)](https://img.shields.io/travis/igorperikov/mighty-watcher)
![GitHub](https://img.shields.io/github/license/igorperikov/mighty-watcher)
![Docker Pulls](https://img.shields.io/docker/pulls/igorperikov/mighty-watcher)

# Mighty Watcher
Finds issues available for contributing, based on repositories you starred

## It will search for issues that are
- non-assigned 
- open
- was updated less than N days ago(configurable, see [how-to](#how-to-use))
- labeled as `help wanted` or similar, all labels can be found [here](/src/main/kotlin/com/github/igorperikov/mightywatcher/service/EasyLabelsStorage.kt)
- starred by account, which issued API access token, if you lack starred repositories, there are some advices [here](#lacking-starred-repositories)

## Prerequisites
- Docker [installed][1]
- Github API access token obtained. You could generate it by going to your GitHub [personal access tokens page][2]. 
Mighty Watcher requires no scopes. I'd recommend you to put API token in env variable (e.g. `MIGHTY_WATCHER_GITHUB_TOKEN`) rather than passing it around everytime.  

## How to use
- Set environment variables in docker run command:

|name         |description                                                                                                                 |required                |default behaviour    |
|-------------|----------------------------------------------------------------------------------------------------------------------------|------------------------|---------------------|
|`TOKEN`      |GitHub API access token                                                                                                     |:heavy_check_mark:      |                     |
|`INCLUDE`    |Comma-separated language names(in lower case) to be included, **nb** only main language counts                              |:heavy_multiplication_x:|include all languages|
|`DAYS`       |Days since last issue update to be included                                                                                 |:heavy_multiplication_x:|365                  |
|`PARALLELISM`|Parallelism level for fetching data from github, [more details below](#parallelism-level)                                   |:heavy_multiplication_x:|10                   |
|`EXCLUDE`    |Comma-separated repositories to be excluded from search, following `$repo/$name` template, e.g. `IgorPerikov/mighty-watcher`|:heavy_multiplication_x:|none will be excluded|
- Launch Docker container from terminal: 
 ```sh
   docker pull igorperikov/mighty-watcher:latest
   docker run -e "TOKEN=$MIGHTY_WATCHER_GITHUB_TOKEN" \
              -e "DAYS=15" \ 
              --rm igorperikov/mighty-watcher:latest
 ```

## Example
<p align="center"><img src="/docs/example.gif?raw=true"/></p>

## Lacking starred repositories?
- go and star languages/libraries you are using right now, if you like them ;)
- check the trending section https://github.com/trending/kotlin?since=monthly and don't forget to change the language and play with date ranges!
- check organizations, known for their open-source effort https://gitstar-ranking.com/organizations 
- you might find more here https://github.com/MunGell/awesome-for-beginners

## Rate limiting
Github lets you make up to 5000 API calls per hour and I am limiting amount of processed repositories to 1000. 
So if you have more than 1000 starred repositories you better split requests by 1 language to fit into limits. 
If you have more than that or still hitting some limits - please let me know, I haven't thought about it thoroughly. 
Also see section on [parallelism](#parallelism-level)

## Parallelism level
The amount of threads to fetch data from Github. If you're hitting some limits, set lower amount and try again in a few minutes.
You can increase this value too if no error occurs, but chances are high that you will trigger api abuse mechanisms. I warned you :warning:

## How to build
Unit testing:
- `./gradlew clean test`

e2e testing:
- build new docker image locally `docker build -t igorperikov/mighty-watcher:local .`
- launch it via docker, same as [here](#how-to-use)

## Contribution
 - please mark issues in your repositories if you are willing to get some help
 - contribute your own labels, if the list misses any

[1]: https://docs.docker.com/install/
[2]: https://github.com/settings/tokens
