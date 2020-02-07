import org.foo.File
import org.foo.Node

def call(String name, Node node, String directory) {
	file = new File()
	file.construct(name, node, directory)
	return file
}


def dump_map_to_file(map, filename){
	content = ''
	for (element in map) {
		content = content + "${element.key} -> ${element.value}\n"
	}
	writeFile(file: filename, text: content, encoding: "UTF-8")
	current_dir = sh (returnStdout: true, script: 'pwd').trim()
}