JMap
====

Just a silly util for parsing a list of JMap dumps to (possibly)
gain insight into memory leaks.

To run/compile use `SBT <https://github.com/harrah/xsbt/wiki>`_::

    sbt
    console

And then you can query the JMap files with something like::

    scala> import org.charleso.util.JMap._
    import org.charleso.util.JMap._

    scala> parseAll("/opt/home/co4222/Desktop/jmap")
    res1: scala.collection.immutable.Map[java.lang.String,List[(Int, Int)]] = ... 

    scala> res1.filter(_._1 startsWith "sun.reflect.GeneratedMethodAccessor")
    res2: scala.collection.immutable.Map[java.lang.String,List[(Int, Int)]] = Map(sun.reflect.GeneratedMethodAccessor -> List((8293,132688), ...
