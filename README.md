# Github4j
* Github4j is a api wrapper for [github's api](https://developer.github.com/v3/)
* The main purpose is to be able to do things with the information provided from the repositories and also to clone and save a local copy of each repository object
## Getting started
* First you will need to create an OAuth2.0 token with github to do with navigate to [link](https://github.com/settings/tokens/new)
* You should create a new token tilted Github4j 

![alt text](https://i.imgur.com/Nt0P6n8.png)

* Based on which features you would like to use depends on which scopes to grant the token. I would not recommend giving all permission in any situation. Navigate below to which feature(s) you'd like to use to see what permission they require
* All methods are attached to the GithubUser object it requires you to base it a valid OAuth2.0 token
```java
new GithubUser(args[0])
```
* Args[0] being the first program parameter
## Repositories
* You will need to grant access to repo to use this feature 

![alt text](https://i.imgur.com/xwEyzCs.png)

* If you would like to use the backup feature you need to grant access to admin:public_key

![alt text](https://i.imgur.com/NN4QjF5.png)

* and user:email

![alt text](https://i.imgur.com/zxrupm2.png)

* This features allows you to call two methods
```java
GithubUser user = new GithubUser(args[0]);
user.getAllRepositories();
user.getPublicRepositories();
```
* getAllRepositories is a list of IRepository Objects which contain every repository on your github
* getPublicRepositories is a list of IRepository Objects which contain only public repositories on your github

## Repository backup
* This feature requires public key access and email access. See above photos
* In order to backup a repository you must either call backup on a IRepository object or call the GithubUser backUpAllRepos.
* If you would like to backup repositories separately I would suggest passing the same folder directory to each backup method because if the program can't find a key pair in that directory it will delete the Github4j key pair on your account and generate a new one.
* To back up all directories you just have to pass a folder in which to store them
```java
new GithubUser(args[0]).backUpAllRepos(System.getProperty("user.dir") + "/gitHubBackups");
``` 
* An example output for a first time run would be
```text
Generating new ssh key
Downloading Github4j
    -> Github4j finished downloading
```
* An example output for a run if the ssh key pair can be found and the files are up to date is
```text
SSH Key found
Downloading Github4j
    -> Github4j is already up to date
```
* An example output for a first time run but the ssh key exists on github would be
```text
Deleting missing sshkey pair with id: 31895369
Generating new ssh key
Downloading Github4j
    -> Github4j finished downloading
```
* Every time a new ssh key is added to your account you will receive an email.