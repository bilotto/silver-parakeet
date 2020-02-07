# silver-parakeet
An epic Jenkins shared library

It's quite often in the industry where we do need to automate processes, but we can't install Jenkins or any other automating tool in the nodes. Sometimes this is a client's restraint or just a matter of user permission. In this case, the only tool we have is often Bash. Even though Bash is essential, and it is the core of this library, it might not be easy to manage many nodes using merely Bash scripts.

Silver Parakeet offers a solution for those cases. All it requires is a Jenkins node that can connect through SSH to the nodes where you need the automation. Or even better, a Jenkins node that can connect through SSH to a node that can connect to these nodes. The node in the middle is often called Jump Server (JP), and that's how we will refer to it throughout the documentation.

What does it do?
In simple words, the library offers an easy way to execute commands in Linux environments. The things start to get interesting when you create new classes that inherit the original ones. You can get automation for practically everything you do at work. It's limitless.

Everything is an object
In Silver Parakeet, general things used on a daily bases at work have become Groovy objects. For instance, the node. We can represent a node by its username and hostname, and maybe also the home directory. Therefore, those are attributes used during the Node object instantiation. Methods of the Node class include actions that we regularly perform to nodes, such as execute a Bash command or transfer a file to it.


