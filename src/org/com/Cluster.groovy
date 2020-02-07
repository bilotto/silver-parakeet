package org.com


def print_object(){
	println clusterEnv
}

def construct(List node_list, String role){
	clusterEnv = [
			'node_list': node_list,
			'role': role
	]
	this.print_object()
}

def execute(String command){
	if (clusterEnv.role == "PASSIVE") {
		branches = [ : ]
		clusterEnv.node_list.each { node ->
			branches[ node ] = { node.execute_command(command) }
		}
		parallel branches
	} else {
		clusterEnv.node_list.each { node ->
			node.execute_command(command)
		}		
	}
}



