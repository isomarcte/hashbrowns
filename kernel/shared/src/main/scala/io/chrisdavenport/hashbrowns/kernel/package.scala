package io.chrisdavenport.hashbrowns

package object kernel {

  /** See [[ByteHasherF#ByteHasher]]. This is a re-export. */
  type ByteHasher = ByteHasherF.ByteHasher

  /** See [[HasherF#Hasher]]. This is a re-export. */
  type Hasher[A] = HasherF.Hasher[A]
}
