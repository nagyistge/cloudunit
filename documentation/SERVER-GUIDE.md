# CloudUnit server installation

You are reading the right guide if you want to set up a CloudUnit server, in order to use it frequently. 
We hope to provide a simple installation following KISS principles.

## Requirements

A virtual or baremetal server with
* at least 8 GB RAM (32 GB or even 64 GB will be better!)
* Ubuntu 14.04 LTS with a 4.x kernel with AUFS support (see FAQ if needed)

> No mysql server must be installed because one will be provided by CloudUnit. 
> If one is already installed on the server, it will create a conflict on port 3306.
> You must remove it before installing CloudUnit. 
> Backup your data if necessary.

## Installation

### As `root` 

Run the command below as `root` to create `admincu` user and install Docker.
The installation script requires a branch to be selected.
Branch `master` contains the latest stable version, whereas `dev` has the latest features.

```
export BRANCH=master
curl https://raw.githubusercontent.com/Treeptik/cloudunit/$BRANCH/cu-production/bootstrap.sh > bootstrap.sh
sh bootstrap.sh $BRANCH
```

After installation, you need to set a password for `admincu`.
Otherwise set up ssh keys for authentication.

Configure your access URLs by appending following environment variables to `/etc/environment`.

```
MYSQL_ROOT_PASSWORD=changeit
CU_PORTAL_DOMAIN=portal.domain.com
CU_MANAGER_DOMAIN=manager.domain.com
CU_GITLAB_DOMAIN=gitlab.domain.com
CU_JENKINS_DOMAIN=jenkins.domain.com
CU_KIBANA_DOMAIN=kibana.domain.com
```

### As `admincu`

Open a new session as `admincu` on the server.

#### Configuration

Create a configuration file at `/home/admincu/.cloudunit/configuration.properties`.
You may use the following template:

```
# ################################################################################ #
#                                                                                  #
#      >>  FILE TO PUT INTO ${USER.HOME}/.cloudunit/configuration.properties       #
#                                                                                  #
# ################################################################################ #

# label for UI
cloudunit.instance.name=PROD

#mail server configuration :
#admin.email=support.cloudunit@treeptik.fr
#email.active=true
#email.host=smtp.gmail.com
#email.port=587
#email.protocol=smtp
#email.username=support.cloudunit@treeptik.fr
#email.password=xxx
```

Uncomment the properties to enable the email notifications.

#### Finish the installation

Run the command below as `admincu` to build Docker images.

```
cd ~/cloudunit/cu-services && ./build-services.sh all
```

You can check the previous step if you want.

```
cd ~/cloudunit/cu-services && ./check_build_images.sh
```

Build the manager for `master` branch.

```
cd ~/cloudunit/cu-manager/dockerhub && docker build --no-cache --build-arg GIT_BRANCH=master -t cloudunit/manager .
```

To finish you have to run the platform's (re)init script.

```
cd ~/cloudunit/cu-compose && ./re-init.sh
```

# FAQ

## How to install a 4.x kernel with AUFS support

```
apt-get install linux-image-4.4.0-42-generic
apt-get install -y linux-image-extra-$(uname -r)
```

## How to restart the production environment without reseting data

```
~/cloudunit/cu-compose/restart.sh
```

## How to reset the production environment 

```
/home/admincu/cloudunit/cu-compose/re-init.sh
```

## How to change the MySQL password

You have to change the MySQL root password (`changeit` by default)
To do so, you have to change the value in the following files
* /home/admincu/.cloudunit/configuration.properties
* /etc/profile

## How to change the SSL Certificates

In order to customize your Cloudunit installation with your own domain name and SSL certificates,
please follow these instructions.

### NGINX config files

NGINX is the entrypoint of the Cloudunit PaaS frontend and is provided as a Docker container.

SSL certificates directory location:

```
~/cloudunit/cu-compose/nginx/DOMAIN_NAME/
```

NGINX global configuration for domain wildcard:

```
/home/admincu/cloudunit/cu-compose/nginx/nginx.conf
```

NGINX domain configuration for apps (gitlab, jenkins, admin...):
Please rename the following file with your domain name and customize it.

```
~/cloudunit/cu-compose/nginx/sites-enabled/cloudunit.conf
```

### SSL Certificates

The number of certificates to list per Nginx server name depends on your SSL Provider.
Extensions also could be differ from a provider to another.

As an example Globalsign gives, in addition, an intermediate certificate. Some others aggregate and encrypt certificates in PKCS / P7B format. In this case, you have to split the file in multiple standard certificates.

