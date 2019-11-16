# Consulo JDK (forked from JetbrainsRuntime)

## Getting sources
__OSX, Linux:__
```
git config --global core.autocrlf input
git clone git@github.com:consulo/ConsuloRuntime.git
```

__Windows:__
```
git config --global core.autocrlf false
git clone git@github.com:consulo/ConsuloRuntime.git
```

# Configure Local Build Environment
## Linux (docker)
```
$ cd jb/project/docker
$ docker build .
...
Successfully built 942ea9900054

$ docker run -v `pwd`../../../../:/ConsuloRuntime -it 942ea9900054

# cd /JetBrainsRuntime
# sh ./configure
# make images CONF=linux-x86_64-normal-server-release

```

## Linux (Ubuntu 18.10 desktop)
```
$ sudo apt-get install autoconf make build-essential libx11-dev libxext-dev libxrender-dev libxtst-dev libxt-dev libxrandr-dev libcups2-dev libfontconfig1-dev libasound2-dev 

$ cd ConsuloRuntime
$ sh ./configure --disable-warnings-as-errors
$ make images
```

## Windows
#### TBD

## OSX

install Xcode console tools, autoconf (via homebrew)

run

```
sh ./configure --prefix=$(pwd)/build  --disable-warnings-as-errors
make images
```