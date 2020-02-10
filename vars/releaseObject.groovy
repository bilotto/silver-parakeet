import org.com.Release
import org.com.Node

def call(String name, String git_branch, String git_tag, String images, Node git_node, LinkedHashMap git_properties) {
	Release release = new Release()
	release.construct(name, git_branch, git_tag, images, git_node, git_properties)
	return release
}

def call(String name, String gitBranch, String gitTag, Node gitNode) {
	return new Release(name, gitBranch, gitTag, gitNode)
}
