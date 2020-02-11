package org.com

class ReleaseNew {
	String name
	String gitBranch
	String gitTag
	Node gitNode
	String filename

	ReleaseNew(name, gitBranch, gitTag, gitNode) {
		this.name = name
		this.gitBranch = gitBranch
		this.gitTag = gitTag
		this.gitNode = gitNode
	}
}

void createTag(){
	cmd = "cd ${gitNode.gitPath}; git fetch --tags"
	gitNode.execute(cmd)
	cmd = "cd ${gitNode.gitPath}; git tag -f ${this.gitTag}"
	gitNode.execute(cmd)
	//todo: the push command might fail if the tag already exists
	try {
		cmd = "cd ${gitNode.gitPath}; git push origin ${this.gitTag}"
		gitNode.execute(cmd)
	} catch(Exception ex) {
		println("Catching the exception");
	}
}


void pullBranch(){
	command = """
		cd ${gitNode.gitPath}
		git checkout ${this.gitBranch}
		git pull
		"""
	this.gitNode.execute(command)
}

void cleanOldBuilds(){
	command = """
		cd ${gitNode.releaseBaseDir}
		if [ -e ${this.filename} ]; then rm ${this.filename}; fi
		if [ -e ${this.name} ]; then rm -r ${this.name}; fi
		"""
	this.gitNode.execute(command)
}

void makeReleaseFolder(){
	command = """
		cd ${gitNode.releaseBaseDir}
		mkdir ${this.name}
		"""
	this.gitNode.execute(command)
}


void setPermissions() {
	command = """
		cd ${gitNode.releaseBaseDir}/${this.name}
		find . -name \"*.sh\" | xargs chmod -R 740
		find . -name \"*.py\" | xargs chmod -R 740
		find . -type f | xargs dos2unix
		"""
	this.gitNode.execute(command)	
}

void compressBuild(){
	command = """
		cd ${gitNode.releaseBaseDir}
		tar -cf ${this.name}.tar ${this.name}
		gzip -f ${this.name}.tar
		rm -r ${this.name}
		"""
	this.gitNode.execute(command)
	this.filename = name + ".tar.gz"
}

return this