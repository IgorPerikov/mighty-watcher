<img src="/docs/logo_deepskyblue.png?raw=true" width=60% height=60%/>

![Codacy grade](https://img.shields.io/codacy/grade/fc40498ca7ee4d1695550e202944f2ab)
![Codecov](https://img.shields.io/codecov/c/github/igorperikov/mighty-watcher)
![Travis (.org)](https://img.shields.io/travis/igorperikov/mighty-watcher)
![GitHub](https://img.shields.io/github/license/igorperikov/mighty-watcher)
![Docker Pulls](https://img.shields.io/docker/pulls/igorperikov/mighty-watcher)

# Mighty Watcher
Finds issues available for contributing, based on repositories you starred

<img src="/docs/example.gif?raw=true" width="95%"/>

## Table of contents
- [Search criteria](#search-criteria)
- [How to use](#how-to-use)
- [Privacy policy](#privacy-policy)
- [Lacking starred repositories?](#lacking-starred-repositories)
- [Limitations](#limitations)
  - [Github rate limiting](#github-rate-limiting)
  - [Api abuse detection](#api-abuse-detection)
- [Contribution](#contribution)
  - [How to build](#how-to-build)
    - [Unit testing](#unit-testing)
    - [E2E testing](#e2e-testing)
  - [Issues labeling](#issues-labeling)
- [Roadmap](#roadmap)
- [Contact me](#contact-me)

## Search criteria
- Non-assigned 
- Open
- Was updated less than N days ago(configurable, see [how-to](#how-to-use))
- Labeled as `help wanted` or similar, all labels can be found [here][3]
- Starred by account, which issued API access token. If you are lacking starred repositories, 
there are some advices [here](#lacking-starred-repositories)

## How to use
- [Install Docker][1]
- Obtain github API access token. You could generate it by going to your GitHub [personal access tokens page][2]. 
Mighty Watcher requires no scopes. I'd recommend you to put API token in env variable (e.g. `MIGHTY_WATCHER_GITHUB_TOKEN`) rather than passing it around everytime.
- Set environment variables in [docker run command][13]:

|name         |description                                                                                                                 |required                |default behaviour    |
|-------------|----------------------------------------------------------------------------------------------------------------------------|------------------------|---------------------|
|`TOKEN`      |GitHub API access token                                                                                                     |:heavy_check_mark:      |                     |
|`INCLUDE`    |Comma-separated language names(in lower case) to be included, **nb** only main language counts                              |:heavy_multiplication_x:|include all languages|
|`DAYS`       |Days since last issue update to be included                                                                                 |:heavy_multiplication_x:|90                   |
|`PARALLELISM`|Parallelism level for fetching data from github, [more details below](#api-abuse-detection)                                 |:heavy_multiplication_x:|10                   |
|`EXCLUDE`    |Comma-separated repositories to be excluded from search, following `$repo/$name` template, e.g. `IgorPerikov/mighty-watcher`|:heavy_multiplication_x:|none will be excluded|
|`OUTPUT`     |Type of response output format (CONSOLE for console response, HTML for html-generated page)                                 |:heavy_multiplication_x:|console              |
- Launch Docker container from terminal: 
 ```sh
   docker pull igorperikov/mighty-watcher:latest
   docker run -e TOKEN=$MIGHTY_WATCHER_GITHUB_TOKEN --rm igorperikov/mighty-watcher:latest
 ```

## Privacy policy
**I do not track a thing about you**. 
Your api token in absolute safety - you can check this by codebase or by tcpdumping the outcome traffic -
application will communicate only with github api server via https. 
The only thing I can see is amount of docker image pulls (not runs!), it is provided by [docker hub][12]

## Lacking starred repositories? 
- Star languages/libraries you are using right now, if you enjoy them ;)
- [Trending section][4]. Don't forget to change the language and play with date ranges. 
- Check [organizations with big open-source effort][5] 
- Check [awesome lists][6]

## Limitations
### Github rate limiting
Github lets you make up to 5000 API calls per hour and I am limiting amount of processed repositories to 1000. 
So if you have more than 1000 starred repositories you should split requests by 1 language via `INCLUDE` variable to fit into limits. 
If you have more than that or still hitting some limits - please [contact me](#contact-me), I haven't thought about it thoroughly.

### Api abuse detection
`PARALLELISM` variable defines amount of threads to use to fetch data from Github. 
If you're hitting some limits, set lower amount and try again in a few minutes.
You can increase this value too if no error occurs to get results faster, 
but chances are high that you will trigger api abuse mechanisms. I warned you :warning:

## Contribution
### How to build
Kotlin style guide is predefined Kotlin style guide from Intellij Idea settings. 

#### Unit testing
- `./gradlew clean test`

#### E2E testing
- Build new docker image locally `docker build -t igorperikov/mighty-watcher:local .`
- Launch it via docker [command](#how-to-use)

### Issues labeling
 - Contribute your own labels, if [list][3] misses any
 - Put labels on issues in your repositories if you want to get some help
 - Spread the word!
 
## Roadmap
- [ ] [Advanced mode][9]
- [ ] [Pdf/html reports][10]
- [ ] [Track github api limits during import process][11]
- [ ] Web/Mobile application

## Contact me 
For general feedback please proceed to the [feedback issue][7]. 
If something doesn't work as expected or you have a feature request - don't hesitate to create new [issue][8] 

[1]: https://docs.docker.com/install/
[2]: https://github.com/settings/tokens
[3]: https://github.com/IgorPerikov/mighty-watcher/blob/master/src/main/kotlin/com/github/igorperikov/mightywatcher/service/EasyLabelsStorage.kt
[4]: https://github.com/trending/kotlin?since=monthly
[5]: https://gitstar-ranking.com/organizations
[6]: https://github.com/sindresorhus/awesome
[7]: https://github.com/IgorPerikov/mighty-watcher/issues/67
[8]: https://github.com/IgorPerikov/mighty-watcher/issues/new
[9]: https://github.com/IgorPerikov/mighty-watcher/issues/60
[10]: https://github.com/IgorPerikov/mighty-watcher/issues/41
[11]: https://github.com/IgorPerikov/mighty-watcher/issues/64
[12]: https://hub.docker.com/
[13]: https://docs.docker.com/engine/reference/commandline/run/#set-environment-variables--e---env---env-file
