package net.martinsnyder.scalamud

class BaseObject(val sdesc: String, val ldesc: String)

class AddressableObject(sdesc: String, ldesc: String, val keywords: Set[String]) extends BaseObject(sdesc, ldesc)

class Mobile(sdesc: String, ldesc: String, keywords: Set[String]) extends AddressableObject(sdesc, ldesc, keywords)
{
	def sendMessage(msg: String) = {}
}

class User(username: String) extends Mobile(username, "A user named " + username, Set(username.toLowerCase))
{
	var unseenMsgs: List[String] = Nil

	override def sendMessage(msg: String) = synchronized
	{
		unseenMsgs = msg::unseenMsgs
	}

	def popUnseenMessages() = synchronized
	{
		val curMsgs = unseenMsgs
		unseenMsgs = Nil
		curMsgs
	}
}

class Room(sdesc: String, ldesc: String) extends BaseObject(sdesc, ldesc)
{
	var mobs = Set[Mobile] ()

	def add(m: Mobile)
	{
		Util.check(!(mobs contains m)) { "Room " + sdesc + " already contains mob " + m.sdesc}

		mobs = mobs + m 

		// Send a msg
		sendMessage(new MessageBag("You have entered the room '%3$s'.", null, "%1$s has entered the room."), m, null, sdesc)
	}
	
	def remove(m: Mobile)
	{
		Util.check(mobs contains m) { "Room " + sdesc + " does not contain mob " + m.sdesc}

		mobs = mobs - m
	}

	def getObject(keyword: String):AddressableObject =
	{
		mobs.find(m => m.keywords contains keyword.toLowerCase) match
		{
		  case None => null
		  case Some(x) => x
		}
	}

	def sendMessage(msgBag: MessageBag, actor: Mobile, target: AddressableObject, extra: String)
	{
		mobs.foreach(m => m.sendMessage(msgBag.formatFor(m, actor, target, extra)))
	}
}

class World(entry: Room)
{
	var allUsers = Map[String,User] ()

	def getUser(username: String) =
	{
		allUsers.getOrElse(username, 
		    {
				// User doesn't exist yet -- create on demand!
				val user = new User(username)
			    allUsers += ((username, user))
			    entry.add(user)
			    user.sendMessage("You have entered ScalaMud!")
				user
		    })
	}

	def getRoomFor(mob: Mobile) =
    {
		// TODO: Need to implement a mechanism to do this for worlds with more than 1 room
		entry
    }
}
