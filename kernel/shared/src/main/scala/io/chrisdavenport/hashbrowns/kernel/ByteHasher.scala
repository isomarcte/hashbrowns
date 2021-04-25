package io.chrisdavenport.hashbrowns.kernel

import fs2._
import scodec.bits._

/** An interface for something which can create a hash/digest of bytes.
  *
  * This is the lowest level interface for hashing. It does not include any
  * type level information about the type of hash or the provider of the hash.
  *
  * Some definition of terms may be useful here. A Hash or Digest is a fixed
  * sized byte value computed from an arbitrary size input value. For a given
  * input, the output is always the same, but the same output may be shared
  * between more than one input. This is called a "hash collision".
  *
  * @note This is ''not'' a typeclass. Clearly it can't be because it doesn't
  *       have any type parameters.
  *
  * @note There is no `F` parameter here, as you commonly see in Scala,
  *       because creating a hash/digest is a 'pure' operation. It's just a
  *       math function.
  */
trait ByteHasher {

  /** Create the hash/digest a [[scodec.bits.ByteVector]] input.
    */
  def hashByteVector(value: ByteVector): ByteVector

  /** Create the hash/digest of a [[fs2.Stream]] of bytes. */
  def hashByteStream[F[_]](value: Stream[F, Byte]): Stream[F, Byte]

  // final

  /** Create the hash/digest a byte array input. This is just a convenience
    * method since many APIs deal with `Array[Byte]` directly.
    */
  final def hashByteArray(value: Array[Byte]): ByteVector =
    hashByteVector(ByteVector(value))
}
