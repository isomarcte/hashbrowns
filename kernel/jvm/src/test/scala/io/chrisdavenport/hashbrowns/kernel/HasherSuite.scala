package io.chrisdavenport.hashbrowns.kernel

import fs2._
import scodec.bits._
import munit._
import java.security.MessageDigest

final class HasherSuite extends FunSuite {
  import HasherSuite._
  test("Hasher MD5 Sanity Check") {
    // On a linux shell
    //
    // > echo -n '' | md5sum
    val emptyInputMd5Hash: Option[ByteVector] = ByteVector.fromHex("d41d8cd98f00b204e9800998ecf8427e")

    assertEquals(Option(md5Hasher.digestByteVector(ByteVector.empty).value), emptyInputMd5Hash)
    assertEquals(Option(md5Hasher.digestByteStream[Pure](Stream.empty).value.compile.to(ByteVector)), emptyInputMd5Hash)
  }
}

object HasherSuite {
  private final abstract class MD5

  private val md5Hasher: Hasher[MD5] =
    new Hasher[MD5] {
      private def md: MessageDigest =
        MessageDigest.getInstance("MD5")

      override def hashByteVector(value: ByteVector): ByteVector =
        value.digest(md)

      override def hashByteStream[F[_]](value: Stream[F, Byte]): Stream[F, Byte] =
        fs2.hash.digest(md)(value)
    }
}
