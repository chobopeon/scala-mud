package net.martinsnyder.scalamud

import java.lang.String

class MessageBag(actorMsg: String, targetMsg: String, witnessMsg: String)
{
	def formatFor(recipient: Mobile, actor: Mobile, target: AddressableObject, extra: String) =
	{
		val fmt = recipient match {
		  case a if a == actor => actorMsg
		  case t if t == target => targetMsg
		  case w => witnessMsg
		}

		Util.check(null != fmt) { "Needed message is not available for this MessageBag" }

		val actorSDesc = if (null == actor) "" else actor.sdesc
		val targetSDesc = if (null == target) "" else target.sdesc

		String.format(fmt, actorSDesc, targetSDesc, extra)
	}
}

abstract class ICommand
{
	def process(world: World, actor: Mobile, args: String)
}

class SayCommand extends ICommand
{
	val msg = new MessageBag("You say '%3$s'", null, "%1$s says '%3$s'")

	override def process(world: World, actor: Mobile, args: String) {
		world.getRoomFor(actor).sendMessage(msg, actor, null, args)
	}
}

class LookCommand extends ICommand
{
	override def process(world: World, actor: Mobile, args: String)
	{
		args match {
		  case "" => actor.sendMessage("You look at the room: " + world.getRoomFor(actor).ldesc)
		  case _ =>
		  	world.getRoomFor(actor).getObject(args) match {
		  	  case null => actor.sendMessage("You can't find anything like that here.")
		  	  case target if target == actor => {
            actor.sendMessage("Vanity is not a virtue.")
            actor.sendMessage(String.format("You look at yourself: %s", target.ldesc))
		  	  }
		  	  case target => actor.sendMessage(String.format("You look at the %s: %s", target.sdesc, target.ldesc))
		  	}
		}
	}
}
