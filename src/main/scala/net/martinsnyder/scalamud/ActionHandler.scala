package net.martinsnyder.scalamud

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ActionHandler extends HttpServlet
{
	val world = new World(new Room("Entry room", "From just one look, you can tell this room is the entry."))
	var commands = Map[String, ICommand] ("look" -> new LookCommand(), "say" -> new SayCommand())

	override def doPost(request: HttpServletRequest, response: HttpServletResponse)
	{
		// Collect params from the query string
		val username = request.getParameter("u")
		val entry = request.getParameter("e")

		// Get (or create) the user
		val user = world.getUser(username)

		// Process the entered command
		val (cmdName, cmdArgs) = splitEntry(entry)
    if (Util.notBlank(cmdName))
		  commands.get(cmdName) match {
		  	case None => if (Util.notBlank(entry)) user.sendMessage("Unrecognized command")
			  case Some(cmd) => cmd.process(world, user, cmdArgs)
		  }

		// Send the available messages to the response stream
		user.popUnseenMessages().reverse.foreach(s => response.getOutputStream.println(s))
	}

	// Helper to parse input
	def splitEntry(entry: String) =
	{
		entry.indexOf(' ') match {
		  case i if i < 0 => (entry, "")
		  case i => (entry.substring(0, i), entry.substring(i + 1).trim)
		}
	}
}
