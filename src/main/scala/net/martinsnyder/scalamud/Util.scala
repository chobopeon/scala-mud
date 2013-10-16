package net.martinsnyder.scalamud

object Util
{
	def check(cond: Boolean) (m: => String) = if (!cond) sys error m

	def blank(s: String) = { null == s || "" == s }
	def notBlank(s: String) = { !blank(s) }
}
