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
		def cksumCommand = "cd ${this.directory}; cksum ${this.name}"
		this.cksum = node.executeAndGetOutput(cksumCommand)
	}
}

