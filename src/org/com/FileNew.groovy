package org.com

class FileNew {
	String name
	String directory
	Node node
	String fullPath

	FileNew(name, directory, node) {
		this.name = name
		this.node = node
		this.directory = directory 
		this.fullPath = "${this.directory}/${this.name}"
	}

}

