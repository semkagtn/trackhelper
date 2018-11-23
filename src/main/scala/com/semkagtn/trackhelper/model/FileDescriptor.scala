package com.semkagtn.trackhelper.model

import java.nio.file.Paths

/**
  * Describes an arbitrary file in the file system
  *
  * @param enclosingDir absolute path to the enclosing dir of this file
  * @param name name of the file without extension
  * @param extension file extension.
  *
  * @author semkagtn
  */
case class FileDescriptor(enclosingDir: String,
                          name: String,
                          extension: Option[String]) {

  require(enclosingDir.nonEmpty, "Empty enclosing dir")
  require(name.nonEmpty, "Empty name")
  require(extension.forall(_.nonEmpty), "Empty extension")

  /**
    * Returns file name with exstension
    */
  final def nameWithExtension: String = extension match {
    case Some(ex) => s"$name.$ex"
    case None => name
  }

  /**
    * Returns absolute path of the file
    */
  final def absolutePath: String = s"$enclosingDir/$nameWithExtension"
}

object FileDescriptor {

  def fromPath(path: String): FileDescriptor = {
    val absolutePath = Paths.get(path).toAbsolutePath
    val enclosingDir = absolutePath.getParent.toString
    val fullFileName = absolutePath.getFileName.toString
    val splittedName = fullFileName.split('.')
    val (name, extension) =
      if (splittedName.length == 1) {
        (splittedName.head, None)
      } else {
        val name = splittedName.init.mkString(".")
        if (name.nonEmpty)
          (name, splittedName.lastOption)
        else
          (fullFileName, None)
      }
    FileDescriptor(
      enclosingDir = enclosingDir,
      name = name,
      extension = extension
    )
  }
}
