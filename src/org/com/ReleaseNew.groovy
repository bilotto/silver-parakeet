package org.com

class ReleaseNew {
	String name
	String gitBranch
	String gitTag
	GitNode gitNode
	String filename
	ReleaseNew(name, gitBranch, gitTag, gitNode) {
		this.name = name
		this.gitBranch = gitBranch
		this.gitTag = gitTag
		this.gitNode = gitNode
		this.filename = name + ".tar.gz"
	}
	
	void createTag(){
		def cmd = "cd ${gitNode.gitPath}; git fetch --tags"
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
		def command = """
			cd ${gitNode.gitPath}
			git pull --all
			git checkout ${this.gitBranch}
			git pull
			"""
		this.gitNode.execute(command)
	}
	
	void cleanOldBuilds(){
		def command = """
			cd ${gitNode.releaseBaseDir}
			if [ -e ${this.filename} ]; then rm ${this.filename}; fi
			if [ -e ${this.name} ]; then rm -r ${this.name}; fi
			"""
		this.gitNode.execute(command)
	}
	
	void makeReleaseFolder(){
		def command = """
			cd ${gitNode.releaseBaseDir}
			mkdir ${this.name}
			"""
		this.gitNode.execute(command)
	}
	
	
	void setPermissions() {
		def command = """
			cd ${gitNode.releaseBaseDir}/${this.name}
			find . -name \"*.sh\" | xargs chmod -R 740
			find . -name \"*.py\" | xargs chmod -R 740
			find . -type f | xargs dos2unix
			"""
		this.gitNode.execute(command)	
	}
	
	void compressBuild(){
		def command = """
			cd ${gitNode.releaseBaseDir}
			tar -cf ${this.name}.tar ${this.name}
			gzip -f ${this.name}.tar
			rm -r ${this.name}
			"""
		this.gitNode.execute(command)
	}
	
	void copyFolder(){
		def command = """
			cd ${gitNode.gitPath}/${this.name}
			cp -r ${gitNode.gitPath}/* .
			"""
		this.gitNode.execute(command)
	}
	
	void createRelease() {
	//replace this method in a superclass if you need
		this.pullBranch()
		this.cleanOldBuilds()
		this.makeReleaseFolder()
		this.copyFolder()
		this.setPermissions()
		this.compressBuild()
		if (this.gitTag != '') {
			this.createTag()
		}
	}

	
}
