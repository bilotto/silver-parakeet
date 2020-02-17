package org.com

class FileNew {
	String name
	String directory
	NodeNew node
	String fullPath
	String cksum
	FileNew(name, directory, node) {
		this.name = name
		this.node = node
		this.directory = directory 
		this.fullPath = "${this.directory}/${this.name}"
	}
	
	FileNew(name, directory) {
		this.name = name
		this.directory = directory 
		this.fullPath = "${this.directory}/${this.name}"
	}
	
	Boolean existsInNode(NodeNew node, String directory){
		if (this.node){
			def command = "cd ${directory}; if [ -e ${this.name} ]; then echo true; else echo false; fi"
		    def stdout = node.executeAndGetOutput(command)
			if (stdout != 'true'){
				println "It does not exist"
				return false
			}
			println "File exists. Is the same file?"
			def cksumCommand = "cd ${directory}; cksum ${this.name}"
			def cksum = node.executeAndGetOutput(cksumCommand)
			if (cksum == this.cksum) {
				println "They are the same file"
				return true
			}
			println "They are not the same file"
			return false
		}
	}
	
	void replaceNode(node, directory) {
		if (this.node){
			this.node = node
			this.directory = directory
			this.fullPath = "${this.directory}/${this.name}"
		}
	}
	
	void deleteItself(){
		if (this.node){
			def command = "rm -f ${this.fullPath}"
			this.node.execute(command)
		}
	}
	
}

