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
	
	Boolean existsinNode(NodeNew node, String directory){
		command = "cd ${directory}; if [ -e ${this.name} ]; then echo true; else echo false; fi"
	    stdout = node.executeAndGetOutput(command)
		if (stdout != 'true'){
			return false
		}
		println "File exists. Is the same file?"
		cksumCommand = "cd ${directory}; cksum ${file.name}"
		cksum = node.executeAndGetOutput(cksumCommand)
		if (cksum == this.cksum) {
			println "They are the same file"
			return true
		}
		println "They are not the same file"
		return false
	}
	

}

