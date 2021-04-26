package io.chrisdavenport.hashbrowns.kernel

import cats._
import cats.syntax.all._
import fs2._
import scodec.bits._

/** An interface for an Algorithm which can create a hash/digest of bytes.
  *
  * This is very similar to [[ByteHasher]], but also provides methods with tag
  * the resulting hash with the Algorithm so you can verify at the type level
  * that something has been hashed. That is the only utility of this
  * interface.
  *
  * @note This is ''not'' a typeclass. In someways it ''could'' be made to be
  *       a typeclass, but in the JVM ecosystem there are ''many'' different
  *       implementations of hashing algorithms. Because of this, instances of
  *       this class ''will'' not have coherence, e.g. there may be more than
  *       one instance of this class for any given Algorithm.
  */
trait HasherF[F[_], A] extends ByteHasherF[F] {
  import HasherF._

  // final

  /** Create the hash/digest of a [[scodec.bits.ByteVector]] and tag the result
    * with the Algorithm.
    */
  final def digestByteVector(value: ByteVector)(implicit F: Functor[F]): F[Digest[A]] =
    hashByteVector(value).map(Digest.DigestImpl.apply _)

  /** Create the hash/digest of a [[fs2.Stream]] and tag the result
    * with the Algorithm.
    */
  final def digestByteStream[G[_]](value: Stream[G, Byte])(implicit F: Functor[F]): F[StreamDigest[G, A]] =
    hashByteStream[G](value).map(StreamDigest.StreamDigestImpl[G, A])

  /** As [[#digestByteVector]] but takes an `Array[Byte]` for ease of working
    * with other APIs.
    */
  final def digestByteArray(value: Array[Byte])(implicit F: Functor[F]): F[Digest[A]] =
    digestByteVector(ByteVector(value))
}

object HasherF {

  /** The most canonical type. Since hashing is a pure operation, it is unlikely
    * that most implementations will need the `F` type to be anything other
    * than Id.
    */
  type Hasher[A] = HasherF[Id, A]

  /** A wrapper type for a [[scodec.bits.ByteVector]] which denotes the
    * Algorithm used to create the hash/digest.
    *
    * @note The ''only'' way to create an instance of this type is by using
    *       one of the methods on a [[Hasher]] instance.
    */
  sealed trait Digest[A] {
    def value: ByteVector
  }

  object Digest {
    private[HasherF] final case class DigestImpl[A](override val value: ByteVector) extends Digest[A]
  }

  /** A wrapper type for a [[fs2.Stream]] which denotes the
    * Algorithm used to create the hash/digest.
    *
    * @note The ''only'' way to create an instance of this type is by using
    *       one of the methods on a [[Hasher]] instance.
    */
  sealed trait StreamDigest[F[_], A] {
    def value: Stream[F, Byte]
  }

  object StreamDigest {
    private[HasherF] final case class StreamDigestImpl[F[_], A](override val value: Stream[F, Byte]) extends StreamDigest[F, A]
  }
}
