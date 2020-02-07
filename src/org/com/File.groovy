package org.com

//every file in a pipeline is an object
//a file always has a name and it lies in a node's directory
fileEnv

def print_object(){
	println fileEnv
}

def get_object(){
	return fileEnv
}

def construct(String name, Node node, String directory){
	fileEnv = [
			name : name,
			node: node,
			directory: directory,
			file_full_path: '',
			cksum: ''
	]
	fileEnv.put('file_full_path', directory + '/' + name)
	cksum_command = "cd ${fileEnv.directory}; cksum ${fileEnv.name}"
	cksum = node.execute_command_and_get_output(cksum_command)
	fileEnv.put('cksum', cksum)
	this.print_object()
}


